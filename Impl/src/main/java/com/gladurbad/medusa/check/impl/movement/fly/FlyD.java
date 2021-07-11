package com.gladurbad.medusa.check.impl.movement.fly;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.config.ConfigValue;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;

//skidded from Hades

@CheckInfo(name = "Fly (D)", description = "Checks for falling speed.")
public final class FlyD extends Check {

    private static final ConfigValue buffers = new ConfigValue(ConfigValue.ValueType.DOUBLE, "buffer");
    private static final ConfigValue bufferdecay = new ConfigValue(ConfigValue.ValueType.DOUBLE, "buffer-decay");

    public FlyD(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isPosition()) {

            final boolean exempt = isExempt(
                    ExemptType.LIQUID, ExemptType.FLYING, ExemptType.CLIMBABLE, ExemptType.SLIME,
                    ExemptType.UNDER_BLOCK, ExemptType.NEAR_VEHICLE, ExemptType.PLACING
            ) || data.getVelocityProcessor().getTicksSinceVelocity() < 5 || data.getPositionProcessor().isOnGround();

            if (exempt) return;

            double predict = (data.getPositionProcessor().getLastDeltaY() - 0.08D) * 0.9800000190734863D;

            if (Math.abs(predict) <= 0.005D) {
                predict = 0;
            }

            if (data.getPositionProcessor().getAirTicks() > 10) {
                if (Math.abs(data.getPositionProcessor().getDeltaY() - predict) > 1E-12) {
                    if (++buffer > buffers.getDouble()) {
                        fail();
                    }
                } else {
                    buffer = Math.max(buffer - bufferdecay.getDouble(), 0);
                }
            }
        }
    }
}