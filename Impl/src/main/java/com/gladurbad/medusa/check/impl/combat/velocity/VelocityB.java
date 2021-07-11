package com.gladurbad.medusa.check.impl.combat.velocity;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.config.ConfigValue;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;

/**
 * attempted skidding on 5/19/2021. if it works, its from fiona
 */
@CheckInfo(name = "Velocity (B)", experimental = true, description = "Checks for vertical velocity modification")
public final class VelocityB extends Check {

    private static final ConfigValue maxVelo = new ConfigValue(ConfigValue.ValueType.DOUBLE, "max-velocity");
    private static final ConfigValue minVelo = new ConfigValue(ConfigValue.ValueType.DOUBLE, "min-velocity");

    public VelocityB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            if (data.getVelocityProcessor().getTicksSinceVelocity() < 5
                    && !data.getActionProcessor().isLagging()
                    && !data.getPositionProcessor().isBlockNearHead()
                    && data.getPositionProcessor().isLastOnGround()) {

                final double deltaY = data.getPositionProcessor().getDeltaY();
                final double velocityY = data.getVelocityProcessor().getVelocityY();

                if (deltaY > 0 && velocityY > 0) {

                    final boolean exempt = isExempt(
                            ExemptType.LIQUID, ExemptType.PISTON, ExemptType.CLIMBABLE,
                            ExemptType.UNDER_BLOCK, ExemptType.TELEPORT, ExemptType.FLYING,
                            ExemptType.WEB, ExemptType.STEPPED, ExemptType.SLIME);

                    double velocityRatio = deltaY / velocityY;

                    if (velocityRatio < maxVelo.getDouble() && velocityRatio > minVelo.getDouble() && !exempt) {
                        fail("vr=" + velocityRatio + " dy" + deltaY + " vy" + velocityY);
                    }
                }
            }
        }
    }
}