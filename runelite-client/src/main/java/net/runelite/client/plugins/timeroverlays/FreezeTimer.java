package net.runelite.client.plugins.timeroverlays;

import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.Plugin;
import lombok.Getter;
import net.runelite.client.ui.overlay.infobox.Timer;

public class FreezeTimer extends Timer
{
    @Getter
    private GameTimer timer;
    @Getter
    private boolean freeze;
    @Getter
    private Instant unfrozenTime;
    @Getter
    private WorldPoint freezeLoc;

    public FreezeTimer(GameTimer timer, Plugin plugin, BufferedImage image, boolean freeze, WorldPoint freezeLoc)
    {
        super(timer.getDuration().toMillis(), ChronoUnit.MILLIS, image, plugin);
        this.timer = timer;
        this.freeze = freeze;
        this.freezeLoc = freezeLoc;
        if (freeze)
        {
            unfrozenTime = super.getEndTime().plusSeconds(3);
        }
    }

    public boolean cull(WorldPoint currentLoc)
    {
        Instant unfrozenTime = super.getEndTime();
        Duration timeLeft = Duration.between(Instant.now(), unfrozenTime);

        if (freeze)
        {
            if (!currentLoc.equals(freezeLoc) && timeLeft.compareTo(Duration.ZERO) > 0) //negative time = immune to freezes
                return true;
            unfrozenTime = unfrozenTime.plusSeconds(3); // 3 seconds (5 ticks) of immunity from freezes after they expire
        }

        timeLeft = Duration.between(Instant.now(), unfrozenTime);

        return timeLeft.isZero() || timeLeft.isNegative();
    }
}
