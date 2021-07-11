package com.gladurbad.medusa.check.impl.player.packetorder;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.config.ConfigValue;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;

/**
 * Created on 11/14/2020 Package com.gladurbad.medusa.check.impl.player.killaura by GladUrBad
 */

@CheckInfo(name = "PacketOrder (A)", description = "Checks for entity use packet order.")
public final class PacketOrderA extends Check {

    private static final ConfigValue bufferamount = new ConfigValue(ConfigValue.ValueType.DOUBLE, "buffer");
    private static final ConfigValue bufferDecay = new ConfigValue(ConfigValue.ValueType.DOUBLE, "buffer-decay");

    private boolean usedEntity;
    private long lastUseEntityTime;

    public PacketOrderA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isUseEntity()) {
            usedEntity = true;
            lastUseEntityTime = now();
        } else if (packet.isFlying()) {
            if (usedEntity) {
                final long delay = now() - lastUseEntityTime;
                final boolean invalid = !data.getActionProcessor().isLagging() && delay > 15;

                debug("delay=" + delay);
                if (invalid) {
                    if (++buffer > bufferamount.getDouble()) {
                        fail(String.format("delay=%d", delay));
                    }
                } else {
                    buffer = Math.max(buffer - bufferDecay.getDouble(), 0);
                }
            }
            usedEntity = false;
        }
    }
}
