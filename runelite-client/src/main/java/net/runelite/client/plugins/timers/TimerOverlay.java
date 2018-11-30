package net.runelite.client.plugins.timers;

import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.infobox.Timer;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.List;
import java.awt.*;
import java.util.Map;

@Singleton
public class TimerOverlay extends Overlay
{
    private final Client client;
    private final TimerList timerList;
    private final TimersConfig config;

    @Inject
    private TimerOverlay(Client client, TimerList timerList, TimersConfig timerConfig)
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
        Map<Actor, List<Timer>> effectsMap = timerList.getEffectsMap();
        Iterator<Map.Entry<Actor, List<Timer>>> entryIterator = effectsMap.entrySet().iterator();

        while (entryIterator.hasNext())
        {
            Map.Entry<Actor, List<Timer>> entry = entryIterator.next();

            Iterator<Timer> timerIterator = entry.getValue().iterator();
            int textLine = 0;
            while (timerIterator.hasNext())
            {
                Timer t = timerIterator.next();
                if (t.cull()) // timer has ran out, remove and ignore
                {
                    timerIterator.remove();
                    if (entry.getValue().isEmpty()) //no timers are left for the actor
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

    public void add(Actor actor, Timer t)
    {
        timerList.addPlayerEffect(actor, t);
    }

    public boolean remove(Actor actor, Timer t)
    {
        return timerList.removePlayerEffect(actor, t);
    }

    public void renderTimerOverlay(Graphics2D graphics, Actor actor, Timer timer, int textOffset) //graphics.getFontMetrics().stringWidth(timeText);
    {
        if (!config.showTimerOverlay())
            return;

        String timeText = timer.getText();
        BufferedImage image = (BufferedImage) timer.getImage();

        int offset = actor.getLogicalHeight() - 150;
        Point textLocation = actor.getCanvasTextLocation(graphics, timeText, offset);

        if (image != null)
        {
            int width = image.getWidth();
            int textHeight = graphics.getFontMetrics().getHeight() - graphics.getFontMetrics().getMaxDescent();
            Point imageLocation = new Point(textLocation.getX() - width / 2 - 1, textLocation.getY() - textHeight / 2 - image.getHeight() / 2);
            OverlayUtil.renderImageLocation(graphics, imageLocation, image);

            // move text
            textLocation = new Point(textLocation.getX() + width / 2, textLocation.getY() -  20 * textOffset);
        }
        OverlayUtil.renderTextLocation(graphics, textLocation, timeText, timer.getTextColor());
    }

}
