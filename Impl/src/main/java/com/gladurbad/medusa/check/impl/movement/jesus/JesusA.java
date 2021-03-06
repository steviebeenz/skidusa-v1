package com.gladurbad.medusa.check.impl.movement.jesus;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.config.ConfigValue;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.List;
@CheckInfo(name = "Jesus (A)", description = "Checks for water friction.", experimental = true)
public final class JesusA extends Check {

    private static final ConfigValue bufferamount = new ConfigValue(ConfigValue.ValueType.DOUBLE, "buffer");
    private static final ConfigValue bufferDecay = new ConfigValue(ConfigValue.ValueType.DOUBLE, "buffer-decay");

    double buffers = bufferamount.getDouble();
    double bufferDecays = bufferDecay.getDouble();

    private int ticks;

    public JesusA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isPosition()) {
            final List<Block> blocks = data.getPositionProcessor().getBoundingBox().expand(1, 1, 1).getBlocks(
                    data.getPlayer().getWorld()
            );

            final boolean check = blocks.stream().noneMatch(block -> block.getType() != Material.AIR && !block.getType().toString().contains("WATER"))
                    && blocks.stream().anyMatch(block -> block.getType().toString().contains("WATER"));

            if (check) {
                if ((ticks += 2) > 10) {
                    final double prediction = data.getPositionProcessor().getLastDeltaXZ() * 0.8F;
                    final double difference = Math.abs(data.getPositionProcessor().getLastDeltaXZ() - prediction);

                    final boolean exempt = isExempt(ExemptType.TELEPORT, ExemptType.FLYING,
                            ExemptType.INSIDE_VEHICLE,
                            ExemptType.NEAR_VEHICLE);

                    debug("diff=" + difference + " exempt=" + exempt + " buffer=" + buffer);
                    if (difference > 0.04 && !exempt
                            //&& data.getPositionProcessor().getNearLilyPadTicks() > 10
                            //&& data.getPositionProcessor().getNearCarpetTicks() > 10
                    ) {
                        if ((buffer += 5) > buffers) {
                            fail("diff=" + difference);
                        }
                    } else {
                        buffer = Math.max(buffer - bufferDecays, 0);
                    }
                }
            } else {
                ticks -= ticks > 0 ? 1 : 0;
            }
        }
    }
}
