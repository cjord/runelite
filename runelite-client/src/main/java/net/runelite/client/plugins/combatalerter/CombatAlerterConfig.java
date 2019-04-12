package net.runelite.client.plugins.combatalerter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("combatalerter")
public interface CombatAlerterConfig extends Config
{
    /*
    @ConfigItem(
            keyName = "focusOnAlert",
            name = "Focus on Alert ",
            description = "Sets the client window to topmost and focused on alert"
    )
    default boolean focusOnAlert()
    {
        return false;
    }


    @ConfigItem(
            keyName = "playSoundOnCombatEnd",
            name = "Play Sound on Combat End",
            description = "Plays a jingle when out of combat for the set Alert Time"
    )
    default boolean playSoundOnCombatEnd()
    {
        return true;
    }

    @ConfigItem(
            keyName = "alertTime",
            name = "Alert Time",
            description = "How many milliseconds out of combat before alerts go off. 0 to disable"
    )
    default int alertTime()
    {
        return 10;
    }
    */

    @ConfigItem(
            keyName = "notifyOutOfCombat",
            name = "Notify out of Combat",
            description = "Toggles notifications for when combat ends and you are out of combat",
            position = 0
    )
    default boolean notifyOutOfCombat()
    {
        return true;
    }

    @ConfigItem(
            keyName = "notifyHitpointsValue",
            name = "HP Value",
            description = "Notify if HP is less than this value. 0 to disable",
            position = 1
    )
    default int notifyHitpointsValue()
    {
        return 0;
    }

    @ConfigItem(
            keyName = "NMZHitpointsToggle",
            name = "NMZ HP Toggle",
            description = "Changes HP notifications to be for above the HP Value instead of below.",
            position = 2
    )
    default boolean NMZHitpointsToggle()
    {
        return false;
    }

    @ConfigItem(
            keyName = "notifyAbsorptionValue",
            name = "Absorption Value",
            description = "Notify if absorption is less than this value. 0 to disable",
            position = 3
    )
    default int notifyAbsorptionValue()
    {
        return 0;
    }

    @ConfigItem(
            keyName = "notifyPrayerValue",
            name = "Prayer Value",
            description = "Notify if prayer is less than this value. 0 to disable",
            position = 4
    )
    default int notifyPrayerValue()
    {
        return 0;
    }

    @ConfigItem(
            keyName = "alertFrequency",
            name = "Alert Frequency",
            description = "How many seconds before alerts of the same type repeat. 0 to disable",
            position = 5
    )
    default int alertFrequency()
    {
        return 10;
    }

    //todo potion support
    //todo nightmare zone overload support
}
