package com.gladurbad.medusa.check.impl.movement.prediction;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;

//donated by demon

@CheckInfo(name = "Prediction (C)", description = "Prediction fly check.")
public final class PredictionC extends Check {

    public PredictionC(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isFlying()) {

            if (!isExempt(ExemptType.SLIME, ExemptType.INSIDE_VEHICLE) &&
                    data.getVelocityProcessor().getTicksSinceVelocity() >= 20) {

                double prediction = (data.getPositionProcessor().getLastDeltaY() - 0.08D) * 0.9800000190734863D;

                double total = Math.abs(data.getPositionProcessor().getDeltaY() - prediction);

                if (data.getPositionProcessor().getDeltaY() < 0.0 && data.getPositionProcessor().isInAir()) {
                    if (total > 0.35) {
                        if (++buffer > 1.25) {
                            fail("2 | " + "p=" + prediction + " dy=" + data.getPositionProcessor().getDeltaY() + " ldy=" +
                                    data.getPositionProcessor().getLastDeltaY());
                        }
                    } else {
                        buffer -= Math.min(buffer, 0.025f);
                    }
                } else {
                    buffer -= Math.min(buffer, 0.025f);
                }
            }
        }
    }
}