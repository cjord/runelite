package net.runelite.client.plugins.pvpattackableindicators;

import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.MenuEntry;
import net.runelite.api.Player;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.InteractingChanged;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.playerindicators.PlayerIndicatorsConfig;
import net.runelite.client.plugins.timers.TimersConfig;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;

import javax.inject.Inject;

import static net.runelite.api.MenuAction.*;

@PluginDescriptor(
        name = "PvP Combat Indicators",
        description = "Displays combat levels of players on the screen in different colors depending on whether or not you can attack them",
        tags = {"pvp", "attack", "attackable", "wilderness", "wild", "overlay", "players"}
)
public class AttackIndicatorsPlugin extends Plugin
{
    @Inject
    private OverlayManager overlayManager;

    @Inject
    private AttackIndicatorsOverlay overlay;

    @Inject
    private Client client;

    @Inject
    private AttackIndicatorsConfig config;

    @Provides
    AttackIndicatorsConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(AttackIndicatorsConfig.class);
    }


    @Override
    protected void startUp() throws Exception
    {
        overlayManager.add(overlay);
    }

    @Override
    protected void shutDown() throws Exception
    {
        overlayManager.remove(overlay);
    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded menuEntryAdded)
    {
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

            if (player.isClanMember() && config.swapClanAttackMenuEntry())
            {
                swap("Attack", "Walk here");
            }
        }
    }

    private void swap(String optionA, String optionB)
    {
        MenuEntry[] entries = client.getMenuEntries();

        int idxA = searchIndex(entries, optionA);
        int idxB = searchIndex(entries, optionB);

        if (idxA >= 0 && idxB >= 0)
        {
            MenuEntry entry = entries[idxA];
            entries[idxA] = entries[idxB];
            entries[idxB] = entry;

            client.setMenuEntries(entries);
        }
    }

    private int searchIndex(MenuEntry[] entries, String option)
    {
        for (int i = entries.length - 1; i >= 0; i--)
        {
            MenuEntry entry = entries[i];
            String entryOption = Text.removeTags(entry.getOption()).toLowerCase();

            if (entryOption.contains(option.toLowerCase()))
            {
                return i;
            }
        }
        return -1;
    }



}

