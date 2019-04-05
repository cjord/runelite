package net.runelite.client.plugins.pvpattackableindicators;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.Text;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.awt.image.BufferedImage;

import static net.runelite.api.ClanMemberRank.UNRANKED;
import static net.runelite.api.MenuAction.*;

@Singleton
public class AttackIndicatorsOverlay extends Overlay
{
    private final AttackIndicatorsConfig config;
    private final AttackIndicatorsService attackIndicatorsService;
    private final Client client;

    @Inject
    private AttackIndicatorsOverlay(Client client, AttackIndicatorsConfig config, AttackIndicatorsService attackIndicatorsService)
    {
        this.client = client;
        this.config = config;
        this.attackIndicatorsService = attackIndicatorsService;
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.MED);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        int totalClanMembers = attackIndicatorsService.forEachPlayer((player) -> renderPlayerOverlay(graphics, player));

        if (config.showPlayerCount())
        {
            int totalPlayers = client.getPlayers().size();

            Point clanCountTextLocation = new Point(10, 200);
            OverlayUtil.renderTextLocation(graphics, clanCountTextLocation, "Clan members: " + totalClanMembers, Color.GREEN);

            Point nonClanCountTextLocation = new Point(10, 220);
            OverlayUtil.renderTextLocation(graphics, nonClanCountTextLocation, "Not clan: " + (totalPlayers - totalClanMembers), Color.GREEN);
        }
        return null;
    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded menuEntryAdded)
    {
        MenuEntry[] menuEnt = client.getMenuEntries();
        System.out.println("MENU ENTRIES:");
        for (MenuEntry m : menuEnt)
        {
            System.out.println("option: " +m.getOption());
            System.out.println("target: " +m.getTarget());
        }

        int type = menuEntryAdded.getType();

        if (type >= 2000)
        {
            type -= 2000;
        }

        int identifier = menuEntryAdded.getIdentifier();

        if (type == FOLLOW.getId() || type == TRADE.getId()
                || type == SPELL_CAST_ON_PLAYER.getId() || type == ITEM_USE_ON_PLAYER.getId()
                || type == PLAYER_FIRST_OPTION.getId()
                || type == PLAYER_SECOND_OPTION.getId()
                || type == PLAYER_THIRD_OPTION.getId()
                || type == PLAYER_FOURTH_OPTION.getId()
                || type == PLAYER_FIFTH_OPTION.getId()
                || type == PLAYER_SIXTH_OPTION.getId()
                || type == PLAYER_SEVENTH_OPTION.getId()
                || type == PLAYER_EIGTH_OPTION.getId()
                || type == RUNELITE.getId())
        {
            final Player localPlayer = client.getLocalPlayer();
            Player[] players = client.getCachedPlayers();
            Player player = null;

            if (identifier >= 0 && identifier < players.length)
            {
                player = players[identifier];
            }

            if (player == null)
            {
                return;
            }

            if (config.swapClanAttackMenuEntry())
            {
                MenuEntry[] menuEntries = client.getMenuEntries();

                MenuEntry lastEntry = menuEntries[menuEntries.length - 1];

                if (true) {
                    // strip out existing <col...
                    String target = lastEntry.getTarget();
                    int idx = target.indexOf('>');
                    if (idx != -1) {
                        target = target.substring(idx + 1);
                    }

                    //lastEntry.setTarget(ColorUtil.prependColorTag(target, color));
                }

                for (MenuEntry m : menuEntries)
                {
                    System.out.println(m.toString());
                }
                client.setMenuEntries(menuEntries);
            }
        }
    }

    private void swap(String optionA, String optionB, String target, boolean strict)
    {
        MenuEntry[] entries = client.getMenuEntries();

        int idxA = searchIndex(entries, optionA, target, strict);
        int idxB = searchIndex(entries, optionB, target, strict);

        if (idxA >= 0 && idxB >= 0)
        {
            MenuEntry entry = entries[idxA];
            entries[idxA] = entries[idxB];
            entries[idxB] = entry;

            client.setMenuEntries(entries);
        }
    }

    private int searchIndex(MenuEntry[] entries, String option, String target, boolean strict)
    {
        for (int i = entries.length - 1; i >= 0; i--)
        {
            MenuEntry entry = entries[i];
            String entryOption = Text.removeTags(entry.getOption()).toLowerCase();
            String entryTarget = Text.removeTags(entry.getTarget()).toLowerCase();

            if (strict)
            {
                if (entryOption.equals(option) && entryTarget.equals(target))
                {
                    return i;
                }
            }
            else
            {
                if (entryOption.contains(option.toLowerCase()) && entryTarget.equals(target))
                {
                    return i;
                }
            }
        }

        return -1;
    }



    private void renderPlayerOverlay(Graphics2D graphics, Player actor) //todo integration with playerindicators plugin
    {
        AttackIndicatorsConfig.DisplayMode mode = config.displayMode();
        Color displayColor = attackIndicatorsService.getWildernessAttackableColor(actor);

        if (config.hideClan() && actor.isClanMember())
        {
            return;
        }

        if (config.hideUnattackable())
        {
            if (WorldType.isPvpWorld(client.getWorldType()) && attackIndicatorsService.getWildernessLevel(actor) == 0)
            {
                if (displayColor != Color.GREEN)
                    return;
            }
            else
            {
                if (displayColor == Color.RED)
                    return;
            }
        }

        String text = "Lv-" + actor.getCombatLevel();

        if (mode == AttackIndicatorsConfig.DisplayMode.NAME_AND_LEVEL)
            text = "Lv-" + actor.getCombatLevel() + " " + actor.getName();
        else if (mode == AttackIndicatorsConfig.DisplayMode.NAME_ONLY)
            text = actor.getName();

        int offsetCombat = actor.getLogicalHeight() + config.levelHeight();
        Point textLocationCombat = actor.getCanvasTextLocation(graphics, text, offsetCombat);

        OverlayUtil.renderTextLocation(graphics, textLocationCombat, text, displayColor);
    }
}
