package com.gladurbad.medusa.check.impl.movement.fly;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.config.ConfigValue;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;
import org.bukkit.GameMode;

/**
 * Created on 11/17/2020 Package com.gladurbad.medusa.check.impl.movement.fly by GladUrBad
 */

@CheckInfo(name = "Fly (A)", description = "Checks for gravity.")
public final class FlyA extends Check {

    private static final ConfigValue bufferamount = new ConfigValue(ConfigValue.ValueType.DOUBLE, "buffer");
    private static final ConfigValue bufferDecay = new ConfigValue(ConfigValue.ValueType.DOUBLE, "buffer-decay");

    double buffers = bufferamount.getDouble();
    double bufferDecays = bufferDecay.getDouble();

    public FlyA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isPosition()) {

            final boolean exempt = isExempt(
                    ExemptType.NEAR_VEHICLE, ExemptType.FLYING,// ExemptType.TELEPORT,
                    ExemptType.INSIDE_VEHICLE, ExemptType.PLACING, ExemptType.SLIME
            ) || (data.getActionProcessor().getTicksSincePlacing() <= 5);

            if (!exempt) return;

            if (data.getPlayer().getGameMode() == GameMode.CREATIVE) return;
            if (data.getVelocityProcessor().getTicksSinceVelocity() < 5) return;

                final double deltaY = data.getPositionProcessor().getDeltaY();
                final double lastDeltaY = data.getPositionProcessor().getLastDeltaY();

                final boolean onGround = data.getPositionProcessor().getAirTicks() <= 5;

                final double prediction = Math.abs((lastDeltaY - 0.08) * 0.98F) < 0.005 ? -0.08 * 0.98F : (lastDeltaY - 0.08) * 0.98F;
                final double difference = Math.abs(deltaY - prediction);

                final boolean invalid = difference > 0.079D
                        && !onGround
                        && !(data.getPositionProcessor().getY() % 0.5 == 0 && data.getPositionProcessor().isOnGround() && lastDeltaY < 0);

                debug("posY=" + data.getPositionProcessor().getY() + " dY=" + deltaY + " at=" + data.getPositionProcessor().getAirTicks());

                if (invalid) {
                    buffer += buffer < 50 ? 10 : 0;
                    if (buffer > buffers) {
                        fail(String.format("diff=%.4f, buffer=%.2f, at=%o", difference, buffer, data.getPositionProcessor().getAirTicks()));
                    }
                } else {
                    buffer = Math.max(buffer - bufferDecays, 0);
                }
            }
        }
    }
