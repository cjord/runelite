package net.runelite.client.plugins.pvpattackableindicators;

import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.client.plugins.playerindicators.PlayerIndicatorsConfig;

import javax.inject.Inject;
import java.awt.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class AttackIndicatorsService
{
    private final Client client;
    private final AttackIndicatorsConfig config;

    @Inject
    private AttackIndicatorsService(Client client, AttackIndicatorsConfig config)
    {
        this.config = config;
        this.client = client;
    }

    public Color getWildernessAttackableColor(Actor actor)
    {
        final Player localPlayer = client.getLocalPlayer();
        int localCombatLv = localPlayer.getCombatLevel();
        int otherCombatLv = actor.getCombatLevel();
        int wildernessLv = Math.min(getWildernessLevel(localPlayer), getWildernessLevel(actor));
        int combatDifference;
        if (localCombatLv > otherCombatLv)
        {
            combatDifference = localCombatLv - otherCombatLv;
        }
        else {
            combatDifference = otherCombatLv - localCombatLv;
        }
        if (combatDifference <= wildernessLv)
            return Color.GREEN; //attackable
        else if (combatDifference <= wildernessLv + 2)
            return Color.YELLOW;
        else if (combatDifference <= wildernessLv + 4)
            return Color.ORANGE;
        else
            return Color.RED;
    }


    public int getWildernessLevel(Actor actor)
    {
        if (actor.getWorldLocation().getY() < 3525)
            return 0;
        return (actor.getWorldLocation().getY() - 3520) / 8 + 1;
    }

    public void forEachPlayer(Consumer<Player> consumer)
    {
        final Player localPlayer = client.getLocalPlayer();

        for (Player player : client.getPlayers())
        {
            if (player == null)
                continue;


            if (player == localPlayer)
            {
                if (config.displayOwnCombat())
                {
                    consumer.accept(player);
                }
            }
            else
            {
                if (config.displayOnlyNearWild())
                {
                    if (player.getWorldLocation().getY() > 3520)
                        consumer.accept(player);
                }
                else
                {
                    consumer.accept(player);
                }
            }

        }
    }
}
