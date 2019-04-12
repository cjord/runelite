package net.runelite.client.plugins.combatalerter;

import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.InteractingChanged;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.idlenotifier.IdleNotifierConfig;

import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;


@PluginDescriptor(
        name = "Combat AFK Notifications",
        description = "Improved notifications and alerts for AFKing combat",
        tags = {"combat", "minigame", "nmz", "prayer", "pve", "pvm", "afk", "alert", "notification", "notify"}
)
public class CombatAlerterPlugin extends Plugin
{
    @Inject
    private Client client;

    @Inject
    private Notifier notifier;

    @Inject
    private CombatAlerterConfig config;

    private Instant lastTime;

    private long lastHitpointNotification = 0;
    private long lastAbsorptionNotification = 0;
    private long lastPrayerNotification = 0;
    private long lastCombatNotification = 0;

    private Instant lastCombatTime = Instant.now();

    @Provides
    CombatAlerterConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(CombatAlerterConfig.class);
    }

    @Override
    protected void startUp() throws Exception
    {
    }

    @Override
    protected void shutDown() throws Exception
    {
    }

    //todo notify every X seconds while out of combat, low HP, low prayer, low absorption, potions/boost ran out

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged)
    {
        if (gameStateChanged.getGameState() != GameState.LOGGED_IN)
        {
            return;
        }
    }

    @Subscribe
    public void onGameTick(GameTick event)
    {
        final Player localPlayer = client.getLocalPlayer();
        String notifyResults = "";

        notifyResults += checkOutOfCombat(localPlayer);

        if (config.NMZHitpointsToggle() == true) // for when you want to stay under a certain hitpoint value in NMZ
        {
            notifyResults += checkHighHitpoints(localPlayer);
        }
        else
        {
            notifyResults += checkLowHitpoints(localPlayer);
        }

        notifyResults += checkLowAbsorption(localPlayer);

        notifyResults += checkLowPrayer(localPlayer);

        //notifyResults += checkOverloadStatus(localPlayer);
        //notifyResults += checkCombatStatBoostDrain(localPlayer); //check regular potions

        if (notifyResults.length() > 0)
        {
            notifier.notify(notifyResults);
        }
    }

    private String checkOutOfCombat(Player localPlayer)
    {
        if (config.notifyOutOfCombat() == false)
        {
            return "";
        }
        final Actor interactingWith = localPlayer.getInteracting();

        if (interactingWith == null)
        {
            final Duration delayTime = Duration.ofMillis(5000); //todo config

            if (Instant.now().compareTo(lastCombatTime.plus(delayTime)) >= 0)
            {
                if (System.currentTimeMillis() - lastCombatNotification > config.alertFrequency() * 1000)
                {
                    lastCombatNotification = System.currentTimeMillis();
                    //todo counter for notifications, stop after a few
                    return "[" + localPlayer.getName() + "] is out of combat!\n";
                }
            }
        }
        else
        {
            lastCombatTime = Instant.now(); // reset time
        }
        return "";
    }

    private String checkHighHitpoints(Player localPlayer)
    {
        if (config.notifyHitpointsValue() == 0)
        {
            return "";
        }
        if (client.getBoostedSkillLevel(Skill.HITPOINTS) >= config.notifyHitpointsValue())
        {
            if (System.currentTimeMillis() - lastHitpointNotification > config.alertFrequency() * 1000)
            {
                lastHitpointNotification = System.currentTimeMillis();
                return "[" + localPlayer.getName() + "] has high [" + client.getBoostedSkillLevel(Skill.HITPOINTS) + "] hitpoints!\n";
            }
        }
        return "";
    }

    private String checkLowHitpoints(Player localPlayer)
    {
        if (config.notifyHitpointsValue() == 0 || client.getVar(Varbits.NMZ_ABSORPTION) > 0)
        {
            return "";
        }
        if (client.getBoostedSkillLevel(Skill.HITPOINTS) <= config.notifyHitpointsValue())
        {
            if (System.currentTimeMillis() - lastHitpointNotification > config.alertFrequency() * 1000)
            {
                lastHitpointNotification = System.currentTimeMillis();
                return "[" + localPlayer.getName() + "] has low [" + client.getBoostedSkillLevel(Skill.HITPOINTS) + "] hitpoints!\n";
            }
        }
        return "";
    }

    private String checkLowAbsorption(Player localPlayer) //todo check if in nmz
    {
        if (config.notifyAbsorptionValue() == 0)
        {
            return "";
        }
        if (client.getVar(Varbits.NMZ_ABSORPTION) <= config.notifyAbsorptionValue())
        {
            if (System.currentTimeMillis() - lastAbsorptionNotification > config.alertFrequency() * 1000)
            {
                lastAbsorptionNotification = System.currentTimeMillis();
                return "[" + localPlayer.getName() + "] has low [" + client.getVar(Varbits.NMZ_ABSORPTION) + "] absorption points!\n";
            }
        }
        return "";
    }

    private String checkLowPrayer(Player localPlayer)
    {
        if (config.notifyPrayerValue() == 0)
        {
            return "";
        }
        if (client.getBoostedSkillLevel(Skill.PRAYER) <= config.notifyPrayerValue())
        {
            if (System.currentTimeMillis() - lastPrayerNotification > config.alertFrequency() * 1000)
            {
                lastPrayerNotification = System.currentTimeMillis();
                return "[" + localPlayer.getName() + "] has low [" + client.getBoostedSkillLevel(Skill.PRAYER) + "] prayer points!\n";
            }
        }
        return "";
    }

    @Subscribe
    public void onInteractingChanged(InteractingChanged event)
    {
        final Actor source = event.getSource();

        if (source != client.getLocalPlayer())
        {
            return;
        }

        final Actor target = event.getTarget();

        final boolean isNpc = target instanceof NPC;

        // If this is not NPC, do not process as we are not interested in other entities
        if (!isNpc)
        {
            return;
        }

        final NPC npc = (NPC) target;
        final NPCComposition npcComposition = npc.getComposition();
        final List<String> npcMenuActions = Arrays.asList(npcComposition.getActions());

        if (npcMenuActions.contains("Attack"))
        {
            lastCombatTime = Instant.now();
        }
    }
}
