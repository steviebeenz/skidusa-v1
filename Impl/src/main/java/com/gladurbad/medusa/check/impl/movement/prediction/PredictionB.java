package com.gladurbad.medusa.check.impl.movement.prediction;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.config.ConfigValue;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;

//skidded from Hades

@CheckInfo(name = "Prediction (B)", description = "Prediction friction check.")
public final class PredictionB extends Check {

    private static final ConfigValue bufferamount = new ConfigValue(ConfigValue.ValueType.DOUBLE, "buffer");

    public PredictionB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isFlying()) {

            double prediction = data.getPositionProcessor().getLastDeltaXZ() * 0.91F + 0.025999999F;
            double equalness = data.getPositionProcessor().getDeltaXZ() - prediction;

            if (equalness > 1E-12 && !data.getPositionProcessor().isOnGround() &&
                    !data.getPositionProcessor().isLastOnGround() && data.getPositionProcessor().getY() !=
                    data.getPositionProcessor().getLastY() && data.getPositionProcessor().getSinceLiquidTicks() > 20
                    && data.getPositionProcessor().getSinceClimableTicks() > 20 &&
                    data.getPositionProcessor().getSinceFlyingTicks() > 40 &&
                    data.getVelocityProcessor().getTicksSinceVelocity() > 20 &&
                    data.getPositionProcessor().getSinceWebTicks() > 5 &&
                    !isExempt(ExemptType.INSIDE_VEHICLE)) {
                if (++buffer > bufferamount.getDouble()) {
                    fail();
                }
            } else buffer *= 0.75;
        }
    }
}