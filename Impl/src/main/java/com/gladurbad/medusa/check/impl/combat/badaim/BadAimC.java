package com.gladurbad.medusa.check.impl.combat.badaim;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.config.ConfigValue;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;

import java.util.function.Predicate;

/**
 * Created on 10/24/2020 Package com.gladurbad.medusa.check.impl.combat.aim by GladUrBad
 */

@CheckInfo(name = "BadAim (C)", description = "Checks for irregular movements in the rotation.")
public final class BadAimC extends Check {

    private static final ConfigValue bufferamount = new ConfigValue(ConfigValue.ValueType.DOUBLE, "buffer");

    private final Predicate<Float> validRotation = rotation -> rotation > 3F && rotation < 35F;

    public BadAimC(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isRotation()) {
            double buffers = bufferamount.getDouble();

            final float deltaPitch = Math.abs(data.getRotationProcessor().getDeltaPitch());
            final float deltaYaw =  Math.abs(data.getRotationProcessor().getDeltaYaw() % 360F);

            final float pitch = Math.abs(data.getRotationProcessor().getPitch());

            final boolean invalidPitch = deltaPitch < 0.009 && validRotation.test(deltaYaw);
            final boolean invalidYaw = deltaYaw < 0.009 && validRotation.test(deltaPitch);

            final boolean exempt = isExempt(ExemptType.INSIDE_VEHICLE);
            final boolean invalid = !exempt && (invalidPitch || invalidYaw) && pitch < 89F;

            debug(String.format("deltaYaw=%.2f, deltaPitch=%.2f", deltaYaw, deltaPitch));

            if (invalid) {
                if (++buffer > buffers) {
                    fail(String.format("deltaYaw=%.2f, deltaPitch=%.2f", deltaYaw, deltaPitch));
                }
            } else {
                buffer -= buffer > 0 ? 1 : 0;
            }
        }
    }
}
