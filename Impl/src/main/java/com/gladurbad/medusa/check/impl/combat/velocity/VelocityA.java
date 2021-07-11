package com.gladurbad.medusa.check.impl.combat.velocity;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.config.ConfigValue;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;

/**
 * Created on 11/23/2020 Package com.gladurbad.medusa.check.impl.combat.velocity by GladUrBad
 */
@CheckInfo(name = "Velocity (A)", experimental = true, description = "Checks for total vertical anti-kb")
public final class VelocityA extends Check {

    private static final ConfigValue bufferamount = new ConfigValue(ConfigValue.ValueType.DOUBLE, "buffer");

    double buffers = bufferamount.getDouble();

    public VelocityA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            if (data.getVelocityProcessor().getTicksSinceVelocity() < 5) {
                final double deltaY = data.getPositionProcessor().getDeltaY();
                final double velocityY = data.getVelocityProcessor().getVelocityY();

                final boolean lagging = data.getActionProcessor().isLagging();
                
                debug("dy=" + deltaY + " vy=" + velocityY);
                if (velocityY > 0) {
                    final int percentage = (int) Math.round((deltaY * 100.0) / velocityY);

                    final boolean exempt = isExempt(
                            ExemptType.LIQUID, ExemptType.PISTON, ExemptType.CLIMBABLE,
                            ExemptType.UNDER_BLOCK, ExemptType.TELEPORT, ExemptType.FLYING,
                            ExemptType.WEB, ExemptType.STEPPED
                    );

                    if (percentage < 0.4 && !exempt && !lagging) {
                        if (++buffer > buffers) {
                            buffer = 0;
                            fail("vy=" + velocityY + " pct=" + percentage);
                        }
                    } else {
                        buffer = 0;
                    }
                }
            }
        }
    }
}
