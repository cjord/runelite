package net.runelite.client.plugins.pvpattackableindicators;

import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.timers.TimersConfig;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;

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

}

