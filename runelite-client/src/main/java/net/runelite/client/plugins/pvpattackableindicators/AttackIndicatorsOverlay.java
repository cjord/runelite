package net.runelite.client.plugins.pvpattackableindicators;

import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.WorldType;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.awt.image.BufferedImage;

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
        attackIndicatorsService.forEachPlayer((player) -> renderPlayerOverlay(graphics, player));
        return null;
    }

    private void renderPlayerOverlay(Graphics2D graphics, Player actor) //todo integration with playerindicators plugin
    {
        AttackIndicatorsConfig.DisplayMode mode = config.displayMode();
        Color displayColor = attackIndicatorsService.getWildernessAttackableColor(actor);

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
