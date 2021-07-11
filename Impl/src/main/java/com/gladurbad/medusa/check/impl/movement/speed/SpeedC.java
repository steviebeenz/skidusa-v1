package com.gladurbad.medusa.check.impl.movement.speed;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.config.ConfigValue;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;

/**
 * Thank you so much to Nik (developer of Alice) for the code to this check. Feeds my skidding addiction
 */

@CheckInfo(name = "Speed (C)", description = "Checks for impossible rotations")
public final class SpeedC extends Check {

    private static final ConfigValue bufferamount = new ConfigValue(ConfigValue.ValueType.DOUBLE, "buffer");
    private static final ConfigValue bufferDecay = new ConfigValue(ConfigValue.ValueType.DOUBLE, "buffer-decay");

    double buffers = bufferamount.getDouble();
    double bufferDecays = bufferDecay.getDouble();

    public SpeedC(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isRotation()) {

            final float deltaYaw = data.getRotationProcessor().getDeltaYaw();

            final double deltaXZ = data.getPositionProcessor().getDeltaXZ();

            final double squaredAccel = data.getPositionProcessor().getAccelXZ() * 100;

            final boolean invalid = deltaYaw > 1.5F && squaredAccel < 1.0E-5 &&
                    deltaXZ > .15D + (data.getVelocityProcessor().isTakingVelocity() ?
                            Math.abs(data.getVelocityProcessor().getVelocityXZ()) : 0D);

            final boolean isExempt = isExempt(ExemptType.CLIMBABLE);

            if (invalid && !isExempt) {
                if ((buffer += buffer < 175 ? 5 : 0) > buffers) {
                    fail("sa=" + squaredAccel + " dxz=" + deltaXZ + " dy=" + deltaYaw);
                }
            } else {
                buffer = Math.max(buffer - bufferDecays, 0);
            }
        }
    }
}