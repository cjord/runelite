package net.runelite.client.plugins.pvpattackableindicators;

import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.WorldType;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.playerindicators.PlayerIndicatorsConfig;

import javax.inject.Inject;
import java.awt.*;
import java.util.EnumSet;
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
        int wildernessLv = Math.min(getWildernessLevel(localPlayer), getWildernessLevel(actor)); //minimum wildy level for both players
        int combatDifference;
        EnumSet<WorldType> worldTypes = client.getWorldType();

        if (worldTypes.contains(WorldType.SEASONAL_DEADMAN) || worldTypes.contains(WorldType.DEADMAN_TOURNAMENT) || worldTypes.contains(WorldType.DEADMAN))
            //set worldtypes on login instead of checking every time
        {
            if (localCombatLv - otherCombatLv >= 30)
                return Color.RED; // no 30 minute skull
            return Color.GREEN;
        }

        if (WorldType.isPvpWorld(client.getWorldType()))
            wildernessLv += 15;

        if (localCombatLv > otherCombatLv)
        {
            combatDifference = localCombatLv - otherCombatLv;
        }
        else
        {
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


    public int getWildernessLevel(Actor actor) // coordinates below are the coords of the wilderness in the OVERWORLD
    {
        WorldPoint position = actor.getWorldLocation();
        if (position.getY() < 3525 || position.getY() > 4100 || position.getX() < 2942 || position.getX() > 3400)
            return 0;
        return (actor.getWorldLocation().getY() - 3520) / 8 + 1;
    }

    public int forEachPlayer(Consumer<Player> consumer)
    {
        final Player localPlayer = client.getLocalPlayer();
        int clanCount = 0;

        for (Player player : client.getPlayers())
        {
            if (player == null)
                continue;

            if (player.isClanMember())
            {
                //if (config.hideClan())
                    //continue;
                clanCount++;
            }

            if (config.displayOnlyNearWild() && getWildernessLevel(player) == 0)
            {
                if (WorldType.isPvpWorld(client.getWorldType()))
                {
                    if (player == localPlayer)
                    {
                        if (config.displayOwnCombat())
                            consumer.accept(player);
                    }
                    else
                    {
                        consumer.accept(player);
                    }
                }
            }
            else
            {
                if (player == localPlayer)
                {
                    if (config.displayOwnCombat())
                        consumer.accept(player);
                }
                else
                {
                    consumer.accept(player);
                }
            }
        }
        return clanCount;
    }
}
