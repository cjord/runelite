/*
 * Copyright (c) 2018, Connor Jordan
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.pvpattackableindicators;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.awt.Color;

@ConfigGroup("pvpattackableindicators")
public interface AttackIndicatorsConfig extends Config
{
    @ConfigItem(
            keyName = "displayOwnCombat",
            name = "Display own combat",
            description = "Display your own combat level above your head"
    )
    default boolean displayOwnCombat()
    {
        return false;
    }

    @ConfigItem(
            keyName = "displayOnlyNearWild",
            name = "Display only near wilderness",
            description = "Should levels be displayed only near the wilderness (EXCEPT IN in PVP worlds)"
    )
    default boolean displayOnlyNearWild() { return false; }

    @ConfigItem(
            keyName = "hideUnattackable",
            name = "Hide unattackable",
            description = "Hide all players that are not attackable"
    )
    default boolean hideUnattackable() { return false; }

    @ConfigItem(
            keyName = "showPlayerCount",
            name = "Show Player Counts",
            description = "Display count of clan members and non clan members on screen"
    )
    default boolean showPlayerCount() { return true; }

    @ConfigItem(
            keyName = "hideClan",
            name = "Hide clan",
            description = "Hide all players that are in your clan"
    )
    default boolean hideClan() { return true; }

    @ConfigItem(
            keyName = "swapClanAttackMenuEntry",
            name = "Swap Clan Attack Option",
            description = "Prevents attack option for clan members from being left click"
    )
    default boolean swapClanAttackMenuEntry() { return true; }

    @ConfigItem(
            keyName = "levelHeight",
            name = "Text height",
            description = "Where the level should be displayed on the player"
    )
    default int levelHeight() { return 40; }

    @ConfigItem(
            keyName = "displayMode",
            name = "Display Mode",
            description = "What should be displayed for each player"
    )
    default DisplayMode displayMode() { return DisplayMode.LEVEL_ONLY; }

    //todo customizable color and level ranges
    //todo change color or text of all players attacking or are in combat with local player
    //todo further integration with playerindicators plugin, friends and clan mates

    @Getter
    @RequiredArgsConstructor
    enum DisplayMode
    {
        LEVEL_ONLY("Levels only"),
        NAME_AND_LEVEL("Names and levels"),
        NAME_ONLY("Names only");

        private final String name;

        @Override
        public String toString()
        {
            return name;
        }
    }
}
