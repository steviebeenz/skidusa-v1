package com.gladurbad.medusa.check.impl.movement.fly;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;
import com.gladurbad.medusa.util.PlayerUtil;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.atomic.AtomicReference;

//skidded from Nemesis

@CheckInfo(name = "Fly (B)", description = "An extremely lenient fly check.")
public final class FlyB extends Check {
    private static final double LIMIT = 1.83;

    private double lastGroundY;

    public FlyB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isPosition()) {

            final boolean preexempt = isExempt(ExemptType.FLYING, ExemptType.PLACING, ExemptType.SLIME)
                    || data.getVelocityProcessor().getTicksSinceVelocity() < 10;;

            if (preexempt) return;

            final boolean exempt = isExempt(
                    ExemptType.CLIMBABLE, ExemptType.LIQUID, ExemptType.GROUND//, ExemptType.TELEPORT
                    );

            if (exempt) {
                lastGroundY = data.getPositionProcessor().getY();
                return;
            }

            if (data.getPositionProcessor().getY() <= data.getPositionProcessor().getLastY()) {
                return;
            }

            if (data.getPositionProcessor().getAirTicks() > 10) {

                double difference = data.getPositionProcessor().getY() - lastGroundY;

                AtomicReference<Double> limit = new AtomicReference<>(LIMIT);

                int jumpAmplifier = PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.JUMP);

                if (jumpAmplifier > 0) limit.updateAndGet(v -> v + Math.pow(jumpAmplifier + 4.2, 2D) / 16D);

                if (difference > limit.get()) {
                    fail();
                }
            }
        }
    }
}