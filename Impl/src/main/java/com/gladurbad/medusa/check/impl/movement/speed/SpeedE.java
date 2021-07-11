package com.gladurbad.medusa.check.impl.movement.speed;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.config.ConfigValue;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.data.processor.PositionProcessor;
import com.gladurbad.medusa.data.processor.VelocityProcessor;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;
import com.gladurbad.medusa.util.PlayerUtil;
import io.github.retrooper.packetevents.packetwrappers.play.in.flying.WrappedPacketInFlying;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

//skidded from Hades

@CheckInfo(name = "Speed (E)", description = "Speed limit check.")
public final class SpeedE extends Check {

    public SpeedE(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isPosition()) {
            double limit = 0.34f + (PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.SPEED) * 0.062f) +
                    ((data.getPlayer().getWalkSpeed() - 0.2f) * 1.6f);

            if (data.getPositionProcessor().getSinceIceTicks() < 40 || data.getPositionProcessor().getSinceSlimeTicks() < 40)
                 limit += 0.34;

            if (data.getPositionProcessor().getSinceBlockNearHeadTicks() < 40) limit += 0.91;

            if (data.getVelocityProcessor().getTicksSinceVelocity() < 70) limit += Math.abs(
                    data.getVelocityProcessor().getVelocityX() + data.getVelocityProcessor().getVelocityZ());

            if (data.getPositionProcessor().getDeltaXZ() > limit &&
                    data.getPositionProcessor().getSinceTeleportTicks() > 40 &&
                    data.getPositionProcessor().getSinceFlyingTicks() > 40
            ) {
                if (++buffer > 7) {
                    fail();
                }
            } else buffer *= 0.75;
        }
    }
}