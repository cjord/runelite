package net.runelite.client.plugins.timeroverlays;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("timers")
public interface TimerOverlayConfig extends Config
{

    @ConfigItem(
            keyName = "showFreezes",
            name = "Freeze timer",
            description = "Configures whether freeze timer is displayed"
    )
    default boolean showFreezes()
    {
        return true;
    }

    @ConfigItem(
            keyName = "showVengeance",
            name = "Vengeance timer",
            description = "Configures whether vengeance and vengeance other timer is displayed"
    )
    default boolean showVengeance()
    {
        return true;
    }

    @ConfigItem(
            keyName = "showImbuedHeart",
            name = "Imbued heart timer",
            description = "Configures whether imbued heart timer is displayed"
    )
    default boolean showImbuedHeart()
    {
        return true;
    }

    @ConfigItem(
            keyName = "showTeleblock",
            name = "Teleblock timer",
            description = "Configures whether teleblock timer is displayed"
    )
    default boolean showTeleblock()
    {
        return true;
    }

    @ConfigItem(
            keyName = "showCharge",
            name = "Charge timer",
            description = "Configures whether to show a timer for the Charge spell"
    )
    default boolean showCharge()
    {
        return true;
    }

    @ConfigItem(
            keyName = "showSprites",
            name = "Show spell icons",
            description = "Configures whether to show the spell icons next to the timers"
    )
    default boolean showSprites()
    {
        return true;
    }
}
