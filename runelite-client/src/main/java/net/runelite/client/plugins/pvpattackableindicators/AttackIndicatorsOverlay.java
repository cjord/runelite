package net.runelite.client.plugins.pvpattackableindicators;

import net.runelite.api.Player;
import net.runelite.api.Point;
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

    @Inject
    private AttackIndicatorsOverlay(AttackIndicatorsConfig config, AttackIndicatorsService attackIndicatorsService)
    {
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

    private void renderPlayerOverlay(Graphics2D graphics, Player actor) //todo MOVE TEXT IF PLAYER NAMES ARE TURNED ON
    {
        String combat = "Lv-" + actor.getCombatLevel();
        int offsetCombat = actor.getLogicalHeight() + config.levelHeight();
        Point textLocationCombat = actor.getCanvasTextLocation(graphics, combat, offsetCombat);

        OverlayUtil.renderTextLocation(graphics, textLocationCombat, combat, attackIndicatorsService.getWildernessAttackableColor(actor));
    }
}
