package com.gladurbad.medusa.check.impl.movement.speed;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.config.ConfigValue;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;
import com.gladurbad.medusa.util.PlayerUtil;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(name = "Speed (B)", description = "Basic verbose speed check.", experimental = true)
public final class SpeedB extends Check {

    private static final ConfigValue bufferamount = new ConfigValue(ConfigValue.ValueType.DOUBLE, "buffer");

    double buffers = bufferamount.getDouble();

    public SpeedB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isPosition()) {
            final double deltaXZ = data.getPositionProcessor().getDeltaXZ();

            final double maxSpeed = getSpeed(0.45);

            final boolean exempt = isExempt(
                    ExemptType.JOINED, ExemptType.PISTON, ExemptType.VELOCITY,
                    ExemptType.INSIDE_VEHICLE, ExemptType.FLYING, ExemptType.SLIME, ExemptType.UNDER_BLOCK,
                    ExemptType.ICE
            );

            debug("dxz-ms" + (deltaXZ-maxSpeed) + " buffer=" + buffer);
            if (deltaXZ > maxSpeed && !exempt) {
                buffer += buffer < 15 ? 1 : 0;
                if (buffer > buffers) {
                    fail("deltaXZ=" + deltaXZ + " max=" + maxSpeed);
                    buffer /= 2;
                }
            } else {
                buffer -= buffer > 0 ? 0.25 : 0;
            }
        }
    }

    private double getSpeed(double movement) {
        if (PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.SPEED) > 0) {
            movement *= 1.0D + 0.2D * (double)(PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.SPEED));
        }
        return movement;
    }
}
