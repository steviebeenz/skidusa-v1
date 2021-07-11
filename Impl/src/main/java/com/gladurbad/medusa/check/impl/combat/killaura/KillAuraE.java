package com.gladurbad.medusa.check.impl.combat.killaura;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;
import com.gladurbad.medusa.util.MathUtil;

import java.util.LinkedList;
import java.util.List;

//skidded from hades

@CheckInfo(name = "KillAura (E)", description = "Checks for invalid rotations.", experimental = true)
public class KillAuraE extends Check {

    private boolean attacked;
    private double lastDeviation;

    private List<Double> deltas = new LinkedList<>();

    public KillAuraE(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isRotation() || packet.isPosLook() || packet.isLook()) {
            if (attacked) {
                deltas.add((double) (data.getRotationProcessor().getDeltaYaw() % data.getRotationProcessor().getDeltaPitch()));
                attacked = false;
            }

            if (deltas.size() == 36) {
                double deviation = MathUtil.getStandardDeviation(deltas);

                if (Double.isNaN(deviation) & !Double.isNaN(lastDeviation) && !isExempt(ExemptType.INSIDE_VEHICLE)) {
                    fail("d=" + deviation + " ld=" + lastDeviation);
                }

                lastDeviation = deviation;
                deltas.clear();

            }
        }

        if (packet.isUseEntity()) {
            attacked = true;
        }
    }
}
