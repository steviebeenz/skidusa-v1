package com.gladurbad.medusa.check.impl.player.packetorder;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.config.ConfigValue;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;

/**
 * Created on 11/10/2020 Package com.gladurbad.medusa.check.impl.player.scaffold by GladUrBad
 */

@CheckInfo(name = "PacketOrder (D)", description = "Checks for packet order.")
public final class PacketOrderD extends Check {

    private static final ConfigValue bufferamount = new ConfigValue(ConfigValue.ValueType.DOUBLE, "buffer");
    private static final ConfigValue bufferDecay = new ConfigValue(ConfigValue.ValueType.DOUBLE, "buffer-decay");

    double buffers = bufferamount.getDouble();
    double bufferDecays = bufferDecay.getDouble();

    private long lastBlockPlace;
    private boolean placedBlock;

    public PacketOrderD(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isBlockPlace()) {
            lastBlockPlace = now();
            placedBlock = true;
        } else if (packet.isFlying()) {
            if (placedBlock) {
                final long delay = now() - lastBlockPlace;
                final boolean invalid = !data.getActionProcessor().isLagging() && delay > 25;

                if (invalid) {
                    if (++buffer > buffers) {
                        fail("delay=" + delay + " buffer=" + buffer);
                    }
                } else {
                    buffer = Math.max(buffer - bufferDecays, 0);
                }
            }
            placedBlock = false;
        }
    }
}
