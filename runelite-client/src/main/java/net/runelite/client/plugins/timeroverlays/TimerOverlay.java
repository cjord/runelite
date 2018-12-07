package net.runelite.client.plugins.timeroverlays;

import com.google.common.eventbus.Subscribe;
import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.infobox.Timer;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.util.Iterator;
import java.util.List;
import java.awt.*;
import java.util.Map;

@Singleton
public class TimerOverlay extends Overlay
{
    private final Client client;
    private final TimerList timerList;
    private final TimerOverlayConfig config;

    @Inject
    private TimerOverlay(Client client, TimerList timerList, TimerOverlayConfig timerConfig)
    {
        this.client = client;
        this.timerList = timerList;
        this.config = timerConfig;
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.MED);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        Map<Actor, List<FreezeTimer>> effectsMap = timerList.getEffectsMap();
        Iterator<Map.Entry<Actor, List<FreezeTimer>>> entryIterator = effectsMap.entrySet().iterator();

        while (entryIterator.hasNext())
        {
            Map.Entry<Actor, List<FreezeTimer>> entry = entryIterator.next();

            Iterator<FreezeTimer> timerIterator = entry.getValue().iterator();
            int textLine = 0;
            while (timerIterator.hasNext())
            {
                FreezeTimer t = timerIterator.next();
                WorldPoint currentLoc = entry.getKey().getWorldLocation();

                if (t.cull(currentLoc)) // timer ran out, remove and ignore OR player has moved from last freeze
                {
                    timerIterator.remove();
                    if (entry.getValue().isEmpty()) //no timers are left
                        entryIterator.remove();
                }
                else
                {
                    renderTimerOverlay(graphics, entry.getKey(), t, textLine++);
                }
            }
        }
        return null;
    }

    @Subscribe
    public void processPlayerDespawn(Player player)
    {
        timerList.removeAllEffects(player);
    }

    @Subscribe
    public void processNPCDespawn(NPC npc)
    {
        timerList.removeAllEffects(npc);
    }

    public void add(Actor actor, FreezeTimer t)
    {
        timerList.addPlayerEffect(actor, t);
    }

    public boolean removePlayerEffect(Actor actor, int spriteId)
    {
        return timerList.removePlayerEffect(actor, spriteId);
    }

    public Duration getMaxRemainingFreezeTime(Actor actor) {
        return timerList.getMaxRemainingFreezeTime(actor);
    }

    public boolean isUnfrozen(Actor actor) { return timerList.isUnfrozen(actor); }

    public void renderTimerOverlay(Graphics2D graphics, Actor actor, Timer timer, int textOffset) //graphics.getFontMetrics().stringWidth(timeText);
    {
        String timeText = timer.getText();
        BufferedImage image = (BufferedImage) timer.getImage();

        int offset = actor.getLogicalHeight() - 200;
        Point textLocation = actor.getCanvasTextLocation(graphics, timeText, offset);

        if (image != null && textLocation != null)
        {
            int width = image.getWidth();
            int textHeight = graphics.getFontMetrics().getHeight() - graphics.getFontMetrics().getMaxDescent();
            Point imageLocation = new Point(textLocation.getX() - width / 2 - 1, textLocation.getY() - textHeight / 2 - image.getHeight() / 2 -  20 * textOffset); //fixed a nullptr here

            if (config.showSprites())
                OverlayUtil.renderImageLocation(graphics, imageLocation, image);

            // move text
            textLocation = new Point(textLocation.getX() + width / 2, textLocation.getY() -  20 * textOffset);
            OverlayUtil.renderTextLocation(graphics, textLocation, timeText, timer.getTextColor());
        }
    }

}
