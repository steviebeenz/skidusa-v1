package com.gladurbad.medusa.check.impl.movement.keepsprint;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.config.ConfigValue;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

/**
 * Created on 10/24/2020 Package com.gladurbad.medusa.check.impl.combat.killaura by GladUrBad
 */

@CheckInfo(name = "KeepSprint (A)", description = "Checks for KeepSprint modules.")
public final class KeepSprintA extends Check {

    private static final ConfigValue bufferamount = new ConfigValue(ConfigValue.ValueType.DOUBLE, "buffer");
    private static final ConfigValue bufferDecay = new ConfigValue(ConfigValue.ValueType.DOUBLE, "buffer-decay");

    double buffers = bufferamount.getDouble();
    double bufferDecays = bufferDecay.getDouble();

    public KeepSprintA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isPosLook()) {
            if (data.getExemptProcessor().isExempt(ExemptType.COMBAT)) {
                final boolean sprinting = data.getActionProcessor().isSprinting();
                final double deltaXZ = data.getPositionProcessor().getDeltaXZ();
                final double lastDeltaXZ = data.getPositionProcessor().getLastDeltaXZ();

                final double acceleration = Math.abs(deltaXZ - lastDeltaXZ);
                final long clickDelay = data.getClickProcessor().getDelay();
                final Entity target = data.getCombatProcessor().getTarget();

                final boolean invalid = acceleration < 0.0025 &&
                        deltaXZ > 0.22 &&
                        sprinting &&
                        clickDelay < 250 &&
                        target.getType() == EntityType.PLAYER;

                debug("a=" + acceleration + " dxz=" + deltaXZ + " cd=" + clickDelay);
                if (invalid) {
                    if (++buffer > buffers) {
                        fail(String.format("acceleration=%.6f", acceleration));
                        buffer = Math.min(10, buffer);
                    }
                } else {
                    buffer = Math.max(buffer - bufferDecays, 0);
                }
            }
        }
    }
}