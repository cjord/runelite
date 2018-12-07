package net.runelite.client.plugins.timers;

import net.runelite.api.Actor;
import net.runelite.api.Player;
import net.runelite.client.ui.overlay.infobox.Timer;

import javax.inject.Singleton;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.List;

@Singleton
public class TimerList
{
    private Map<Actor, List<FreezeTimer>> playerEffects;

    public TimerList()
    {
        this.playerEffects = new HashMap<Actor, List<FreezeTimer>>();
    }

    public Map<Actor, List<FreezeTimer>> getEffectsMap()
    {
        return playerEffects;
    }

    @Override
    public String toString()
    {
        return "";
    }

    //todo can save player position when frozen, if they move then refresh the timer
    //todo don't add timer for a player if they are already frozen (barrage on entangled player)

    public boolean isUnfrozen(Actor actor)
    {
        if (playerEffects.containsKey(actor))
        {
            List<FreezeTimer> playerTimers = playerEffects.get(actor);

            for (FreezeTimer timer : playerTimers)
            {
                if (timer.isFreeze())
                {
                    Duration timeLeft = Duration.between(Instant.now(), timer.getUnfrozenTime());
                    if (!timeLeft.isZero() && !timeLeft.isNegative())
                        return false;
                }
            }
        }
        return true;
    }

    public Duration getMaxRemainingFreezeTime(Actor actor)
    {
        Duration maxFreeze = Duration.ZERO;

        if (playerEffects.containsKey(actor))
        {
            List<FreezeTimer> playerTimers = playerEffects.get(actor);

            for (FreezeTimer timer : playerTimers)
            {
                if (timer.getTimeLeft().compareTo(maxFreeze) > 0)
                {
                    maxFreeze = timer.getTimeLeft();
                }
            }
        }
        return maxFreeze;
    }

    public void addPlayerEffect(Actor actor, FreezeTimer t)
    {
        if (playerEffects.containsKey(actor))
        {
            playerEffects.get(actor).add(t);
        }
        else
        {
            List<FreezeTimer> tl = new ArrayList<FreezeTimer>();
            tl.add(t);
            playerEffects.putIfAbsent(actor, tl);
        }
        System.out.println("Added: "+actor.getName() + " -> " + playerEffects.get(actor).toString());
    }

    public void removeAllFreezeEffects(Actor actor)
    {
        if (playerEffects.containsKey(actor))
        {
            List<FreezeTimer> playerTimers = playerEffects.get(actor);
            Iterator<FreezeTimer> ptIter = playerTimers.iterator();
            while (ptIter.hasNext())
            {
                FreezeTimer t = ptIter.next();
                if (t.isFreeze())
                {
                    ptIter.remove();
                }
            }
        }
    }

    public boolean removePlayerEffect(Actor actor, int spriteId)
    {
        if (playerEffects.containsKey(actor))
        {
            List<FreezeTimer> playerTimers = playerEffects.get(actor);
            Iterator<FreezeTimer> ptIter = playerTimers.iterator();
            while (ptIter.hasNext())
            {
                FreezeTimer t = ptIter.next();
                if (t.getTimer().getGraphicId() == spriteId)
                {
                    ptIter.remove();
                    return true;
                }
            }
        }
        return false;
    }

    public boolean removeAllPlayerEffects(Actor actor)
    {
        if (playerEffects.containsKey(actor))
        {
            playerEffects.remove(actor);
            System.out.println("Removed all effects for: " + actor.getName());
            return true;
        }
        return false;
    }

}
