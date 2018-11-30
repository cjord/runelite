package net.runelite.client.plugins.timers;

import net.runelite.api.Actor;
import net.runelite.api.Player;
import net.runelite.client.ui.overlay.infobox.Timer;

import javax.inject.Singleton;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

@Singleton
public class TimerList
{
    private Map<Actor, List<Timer>> playerEffects;

    public TimerList()
    {
        this.playerEffects = new HashMap<Actor, List<Timer>>();
    }

    public Map<Actor, List<Timer>> getEffectsMap()
    {
        return playerEffects;
    }

    @Override
    public String toString()
    {
        return "";
    }

    public void addPlayerEffect(Actor actor, Timer t)
    {
        if (playerEffects.containsKey(actor))
        {
            playerEffects.get(actor).add(t);
        }
        else
        {
            List<Timer> tl = new ArrayList<Timer>();
            tl.add(t);
            playerEffects.putIfAbsent(actor, tl);
        }
        System.out.println("Added: "+actor.getName() + " -> " + playerEffects.get(actor).toString());
    }

    public boolean removePlayerEffect(Actor actor, Timer timer)
    {
        if (playerEffects.containsKey(actor))
        {
            List<Timer> playerTimers = playerEffects.get(actor);
            Iterator<Timer> ptIter = playerTimers.iterator();
            while (ptIter.hasNext())
            {
                Timer t = ptIter.next();
                if (t.getImage().equals(timer.getImage()))  //this needs to be done a better way than comparing images
                {
                    ptIter.remove();
                    return true;
                }
            }
        }
        return false;
    }

}
