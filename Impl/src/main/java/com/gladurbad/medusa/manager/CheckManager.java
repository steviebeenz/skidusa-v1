package com.gladurbad.medusa.manager;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.check.impl.combat.autoblock.AutoBlockA;
import com.gladurbad.medusa.check.impl.combat.autoblock.AutoBlockB;
import com.gladurbad.medusa.check.impl.combat.autoblock.AutoBlockC;
import com.gladurbad.medusa.check.impl.combat.autoblock.AutoBlockD;
import com.gladurbad.medusa.check.impl.combat.autoclicker.*;
import com.gladurbad.medusa.check.impl.combat.badaim.*;
import com.gladurbad.medusa.check.impl.combat.killaura.*;
import com.gladurbad.medusa.check.impl.combat.reach.ReachA;
import com.gladurbad.medusa.check.impl.combat.reach.ReachB;
import com.gladurbad.medusa.check.impl.combat.velocity.VelocityA;
import com.gladurbad.medusa.check.impl.combat.velocity.VelocityB;
import com.gladurbad.medusa.check.impl.combat.velocity.VelocityC;
import com.gladurbad.medusa.check.impl.movement.fastclimb.FastClimbA;
import com.gladurbad.medusa.check.impl.movement.fly.*;
import com.gladurbad.medusa.check.impl.movement.jesus.JesusA;
import com.gladurbad.medusa.check.impl.movement.jesus.JesusB;
import com.gladurbad.medusa.check.impl.movement.keepsprint.KeepSprintA;
import com.gladurbad.medusa.check.impl.movement.motion.*;
import com.gladurbad.medusa.check.impl.movement.noslow.NoSlowA;
import com.gladurbad.medusa.check.impl.movement.noslow.NoSlowB;
import com.gladurbad.medusa.check.impl.movement.prediction.PredictionA;
import com.gladurbad.medusa.check.impl.movement.prediction.PredictionB;
import com.gladurbad.medusa.check.impl.movement.prediction.PredictionC;
import com.gladurbad.medusa.check.impl.movement.speed.*;
import com.gladurbad.medusa.check.impl.player.badpackets.*;
import com.gladurbad.medusa.check.impl.player.hand.HandA;
import com.gladurbad.medusa.check.impl.player.hand.HandB;
import com.gladurbad.medusa.check.impl.player.inventory.InventoryA;
import com.gladurbad.medusa.check.impl.player.inventory.InventoryB;
import com.gladurbad.medusa.check.impl.player.packetorder.*;
import com.gladurbad.medusa.check.impl.player.scaffold.*;
import com.gladurbad.medusa.check.impl.player.timer.TimerA;
import com.gladurbad.medusa.check.impl.player.timer.TimerB;
import com.gladurbad.medusa.check.impl.player.timer.TimerC;
import com.gladurbad.medusa.check.impl.player.timer.TimerD;
import com.gladurbad.medusa.config.Config;
import com.gladurbad.medusa.data.PlayerData;
import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public final class CheckManager {

    public static final Class<?>[] CHECKS = new Class[] {
            AutoBlockA.class,
            AutoBlockB.class,
            AutoBlockC.class,
            AutoBlockD.class,
            AutoClickerA.class,
            AutoClickerB.class,
            AutoClickerC.class,
            AutoClickerD.class,
            AutoClickerE.class,
            AutoClickerF.class,
            BadAimA.class,
            BadAimB.class,
            BadAimC.class,
            BadAimD.class,
            BadAimE.class,
            BadAimF.class,
            BadAimG.class,
            BadAimH.class,
            KillAuraA.class,
            KillAuraB.class,
            KillAuraC.class,
            KillAuraD.class,
            KillAuraE.class,
            KillAuraF.class,
            KillAuraG.class,
            ReachA.class,
            ReachB.class,
            VelocityA.class,
            VelocityB.class,
            VelocityC.class,
            FastClimbA.class,
            FlyA.class,
            FlyB.class,
            FlyC.class,
            FlyD.class,
            FlyE.class,
            FlyF.class,
            JesusA.class,
            JesusB.class,
            KeepSprintA.class,
            MotionA.class,
            MotionB.class,
            MotionC.class,
            MotionD.class,
            MotionE.class,
            SpeedA.class,
            SpeedB.class,
            SpeedC.class,
            SpeedD.class,
            SpeedE.class,
            NoSlowA.class,
            NoSlowB.class,
            PredictionA.class,
            PredictionB.class,
            PredictionC.class,
            TimerA.class,
            TimerB.class,
            TimerC.class,
            TimerD.class,
            HandA.class,
            HandB.class,
            InventoryA.class,
            InventoryB.class,
            BadPacketsA.class,
            BadPacketsB.class,
            BadPacketsC.class,
            BadPacketsD.class,
            BadPacketsE.class,
            BadPacketsF.class,
            PacketOrderA.class,
            PacketOrderB.class,
            PacketOrderC.class,
            PacketOrderD.class,
            PacketOrderE.class,
            PacketOrderF.class,
            ScaffoldA.class,
            ScaffoldB.class,
            ScaffoldC.class,
            ScaffoldD.class,
            ScaffoldE.class,
    };

    private static final List<Constructor<?>> CONSTRUCTORS = new ArrayList<>();

    public static List<Check> loadChecks(final PlayerData data) {
        final List<Check> checkList = new ArrayList<>();
        for (Constructor<?> constructor : CONSTRUCTORS) {
            try {
                checkList.add((Check) constructor.newInstance(data));
            } catch (Exception exception) {
                System.err.println("Failed to load checks for " + data.getPlayer().getName());
                exception.printStackTrace();
            }
        }
        return checkList;
    }

    public static void setup() {
        for (Class<?> clazz : CHECKS) {
            if (Config.ENABLED_CHECKS.contains(clazz.getSimpleName())) {
                try {
                    CONSTRUCTORS.add(clazz.getConstructor(PlayerData.class));
                    //Bukkit.getLogger().info(clazz.getSimpleName() + " is enabled!");
                } catch (NoSuchMethodException exception) {
                    exception.printStackTrace();
                }
            } else {
                Bukkit.getLogger().info(clazz.getSimpleName() + " is disabled!");
            }
        }
    }
}

