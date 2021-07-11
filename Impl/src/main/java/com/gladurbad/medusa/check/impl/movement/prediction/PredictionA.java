package com.gladurbad.medusa.check.impl.movement.prediction;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;
import com.gladurbad.medusa.util.MathUtil;

//comes from hades with some modification

@CheckInfo(name = "Prediction (A)", description = "Prediction fly check.")
public final class PredictionA extends Check {

    public PredictionA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isFlying()) {

            if (data.getPositionProcessor().getAirTicks() > 6
                   // && data.getVelocityProcessor().getVelocityY() < -0.075D
                    && !isExempt(ExemptType.SLIME, ExemptType.INSIDE_VEHICLE,
                    ExemptType.CLIMBABLE, ExemptType.LIQUID, ExemptType.FLYING)
                    && data.getVelocityProcessor().getTicksSinceVelocity() >= 20
                    && !data.getActionProcessor().isPlacing()
                    && !data.getPositionProcessor().isNearVehicle()
                   // && data.getPositionProcessor().getSinceFlyingTicks() > 40
                   // && data.getPositionProcessor().getSinceLiquidTicks() > 20
                   // && data.getPositionProcessor().getSinceClimableTicks() > 20
                   // && data.getVelocityProcessor().getTicksSinceVelocity() > 20
                   // && data.getPositionProcessor().getSinceSlimeTicks() > 10
                   // && !data.getPositionProcessor().isInVehicle()
                   // && !data.getPositionProcessor().isInLiquid()
              ) {

                double prediction = (data.getPositionProcessor().getLastDeltaY() - 0.08D) * 0.9800000190734863D;

                if (!MathUtil.isRoughlyEqual(data.getPositionProcessor().getDeltaY(), prediction, 0.001)) {
                    if (++buffer > 1.5) {
                        fail("1 | " + "p=" + prediction + " dy=" + data.getPositionProcessor().getDeltaY() + " ldy=" +
                                data.getPositionProcessor().getLastDeltaY());
                    }
                } else {
                    buffer = Math.max(buffer - 0.1, 0);
                }
            } else {
                buffer = Math.max(buffer - 0.1, 0);
            }
        }
    }
}