/*
 * Copyright (c) 2017, Seth <Sethtroll3@gmail.com>
 * Copyright (c) 2018, Jordan Atwood <jordan.atwood423@gmail.com>
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
package net.runelite.client.plugins.timeroverlays;

import com.google.inject.Provides;

import java.awt.image.BufferedImage;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.PlayerDespawned;
import net.runelite.client.ui.overlay.OverlayManager;
import static net.runelite.client.plugins.timeroverlays.GameTimer.*;

@PluginDescriptor(
        name = "Timer Overlays",
        description = "Shows various timers ontop of players and NPCs",
        tags = {"combat", "items", "magic", "overlay", "timer", "overlay", "freeze", "pvp"}
)
@Slf4j
public class TimerOverlayPlugin extends Plugin
{
    private int lastRaidVarb;
    private int lastWildernessVarb;
    private WorldPoint lastPoint;
    private int lastAnimation;
    private boolean loggedInRace;
    private boolean widgetHiddenChangedOnPvpWorld;

    @Inject
    private ItemManager itemManager;

    @Inject
    private SpriteManager spriteManager;

    @Inject
    private Client client;

    @Inject
    private TimerOverlayConfig config;

    @Provides
    TimerOverlayConfig getConfig(ConfigManager configManager)
    {
        return configManager.getConfig(TimerOverlayConfig.class);
    }

    @Inject
    private TimerOverlay timerOverlay;

    @Inject
    private OverlayManager overlayManager;

    @Override
    protected void startUp() throws Exception
    {
        overlayManager.add(timerOverlay);
    }

    @Override
    protected void shutDown() throws Exception
    {
        overlayManager.remove(timerOverlay);
        lastRaidVarb = -1;
        lastPoint = null;
        lastAnimation = -1;
        loggedInRace = false;
        widgetHiddenChangedOnPvpWorld = false;
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged event)
    {
        int inWilderness = client.getVar(Varbits.IN_WILDERNESS);

        if (lastWildernessVarb != inWilderness
                && client.getGameState() == GameState.LOGGED_IN
                && !loggedInRace)
        {
            if (!WorldType.isPvpWorld(client.getWorldType())
                    && inWilderness == 0)
            {
                log.debug("Left wilderness in non-PVP world, clearing Teleblock timer.");
            }

            lastWildernessVarb = inWilderness;
        }
    }

    @Subscribe
    public void onWidgetHiddenChanged(WidgetHiddenChanged event)
    {
        Widget widget = event.getWidget();
        if (WorldType.isPvpWorld(client.getWorldType())
                && WidgetInfo.TO_GROUP(widget.getId()) == WidgetInfo.PVP_CONTAINER.getGroupId())
        {
            widgetHiddenChangedOnPvpWorld = true;
        }
    }

    @Subscribe
    public void onGraphicChanged(GraphicChanged event)
    {
        if (event.getActor() instanceof Player)
        {
            Player player = (Player) event.getActor();
        
            if (config.showVengeance() && player.getGraphic() == VENGEANCE.getGraphicId())
            {
                createGameTimerOverlay(VENGEANCE, player);
            }

            if (player.getGraphic() == 345) //TELEBLOCK CONTACT GRAPHIC ID - TELEBLOCK.getGraphicId()
            {
                removeGameTimerOverlay(player, SpriteID.SPELL_TELE_BLOCK);
                if (player.getOverheadIcon() == HeadIcon.MAGIC)
                {
                    if (client.getWorldType().contains(WorldType.SEASONAL_DEADMAN))
                        createGameTimerOverlay(DMM_HALFTB, player);
                    else
                        createGameTimerOverlay(HALFTB, player);
                }
                else
                {
                    if (client.getWorldType().contains(WorldType.SEASONAL_DEADMAN))
                        createGameTimerOverlay(DMM_FULLTB, player);
                    else
                        createGameTimerOverlay(FULLTB, player);
                }
            }

            if (config.showFreezes())
            {
                if (player.getGraphic() == BIND.getGraphicId())
                {
                    if (player.getOverheadIcon() == HeadIcon.MAGIC
                            && !client.getWorldType().contains(WorldType.SEASONAL_DEADMAN))
                    {
                        createGameTimerOverlay(HALFBIND, player, true, player.getWorldLocation());
                    }
                    else
                        {
                        createGameTimerOverlay(BIND, player, true, player.getWorldLocation()); //updated
                    }
                }

                if (player.getGraphic() == SNARE.getGraphicId())
                {
                    if (player.getOverheadIcon() == HeadIcon.MAGIC
                            && !client.getWorldType().contains(WorldType.SEASONAL_DEADMAN))
                    {
                        createGameTimerOverlay(HALFSNARE, player, true, player.getWorldLocation());
                    }
                    else
                        {
                        createGameTimerOverlay(SNARE, player, true, player.getWorldLocation());
                    }
                }

                if (player.getGraphic() == ENTANGLE.getGraphicId())
                {
                    if (player.getOverheadIcon() == HeadIcon.MAGIC
                            && !client.getWorldType().contains(WorldType.SEASONAL_DEADMAN))
                    {
                        createGameTimerOverlay(HALFENTANGLE, player, true, player.getWorldLocation());
                    }
                    else
                        {
                        createGameTimerOverlay(ENTANGLE, player, true, player.getWorldLocation());
                    }
                }

                // downgrade freeze based on graphic, if at the same tick as the freeze message
                if (timerOverlay.isUnfrozen(player))
                {
                    if (player.getGraphic() == ICERUSH.getGraphicId())
                    {
                        createGameTimerOverlay(ICERUSH, player, true, player.getWorldLocation());
                    }
                    if (player.getGraphic() == ICEBURST.getGraphicId())
                    {
                        createGameTimerOverlay(ICEBURST, player, true, player.getWorldLocation());
                    }
                    if (player.getGraphic() == ICEBLITZ.getGraphicId())
                    {
                        createGameTimerOverlay(ICEBLITZ, player, true, player.getWorldLocation());
                    }
                    if (player.getGraphic() == ICEBARRAGE.getGraphicId()) //needs more testing
                    {
                        createGameTimerOverlay(ICEBARRAGE, player, true, player.getWorldLocation());
                    }
                }
            }
        }
        else if (event.getActor() instanceof NPC)
        {
            NPC npc = (NPC) event.getActor();
            
            if (config.showFreezes())
            {
                if (npc.getGraphic() == BIND.getGraphicId())
                {
                    createGameTimerOverlay(BIND, npc, true, npc.getWorldLocation()); //updated
                }

                if (npc.getGraphic() == SNARE.getGraphicId())
                {
                    createGameTimerOverlay(SNARE, npc, true, npc.getWorldLocation());
                }

                if (npc.getGraphic() == ENTANGLE.getGraphicId())
                {
                    createGameTimerOverlay(ENTANGLE, npc, true, npc.getWorldLocation());
                }

                // downgrade freeze based on graphic, if at the same tick as the freeze message
                if (timerOverlay.isUnfrozen(npc))
                {
                    if (npc.getGraphic() == ICERUSH.getGraphicId())
                    {
                        createGameTimerOverlay(ICERUSH, npc, true, npc.getWorldLocation());
                    }
                    if (npc.getGraphic() == ICEBURST.getGraphicId())
                    {
                        createGameTimerOverlay(ICEBURST, npc, true, npc.getWorldLocation());
                    }
                    if (npc.getGraphic() == ICEBLITZ.getGraphicId())
                    {
                        createGameTimerOverlay(ICEBLITZ, npc, true, npc.getWorldLocation());
                    }
                    if (npc.getGraphic() == ICEBARRAGE.getGraphicId()) //needs more testing
                    {
                        createGameTimerOverlay(ICEBARRAGE, npc, true, npc.getWorldLocation());
                    }
                }
            }
        }
    }

    @Subscribe
    public void onPlayerDespawned(PlayerDespawned event)
    {
        timerOverlay.processPlayerDespawn(event.getPlayer());
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned npcDespawned)
    {
        timerOverlay.processNPCDespawn(npcDespawned.getNpc());
    }

    @Subscribe
    public void onLocalPlayerDeath(LocalPlayerDeath event)
    {
        timerOverlay.processPlayerDespawn(client.getLocalPlayer());
    }

    private FreezeTimer createGameTimerOverlay(final GameTimer timer, Actor actor) // FOR OTHER PLAYERS
    {
        BufferedImage image = timer.getImage(itemManager, spriteManager);
        FreezeTimer t = new FreezeTimer(timer, this, image, false, null);

        timerOverlay.add(actor, t); //other players??
        return t;
    }

    private FreezeTimer createGameTimerOverlay(final GameTimer timer, Actor actor, boolean freeze, WorldPoint freezeLoc) // FOR OTHER PLAYERS
    {
        BufferedImage image = timer.getImage(itemManager, spriteManager);
        FreezeTimer t = new FreezeTimer(timer, this, image, freeze, freezeLoc);

        timerOverlay.add(actor, t); //other players??
        return t;
    }

    private boolean removeGameTimerOverlay(Actor actor, int spriteId)
    {
        return timerOverlay.removePlayerEffect(actor, spriteId);
    }
}
