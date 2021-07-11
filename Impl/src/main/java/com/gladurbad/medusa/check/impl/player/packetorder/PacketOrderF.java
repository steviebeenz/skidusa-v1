package com.gladurbad.medusa.check.impl.player.packetorder;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.config.ConfigValue;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;

//skidded from anticheat-open-source

@CheckInfo(name = "PacketOrder (F)", description = "Checks for late block place packets.", experimental = true)
public class PacketOrderF extends Check {

    //8
    private static final ConfigValue bufferamount = new ConfigValue(ConfigValue.ValueType.DOUBLE, "buffer");
    //0.75
    private static final ConfigValue bufferDecay = new ConfigValue(ConfigValue.ValueType.DOUBLE, "buffer-decay");

    private long lastFlying;

    public PacketOrderF(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isBlockPlace()) {
            if ((System.currentTimeMillis() - lastFlying) < 5L) {
                if (++buffer > bufferamount.getDouble()) {
                    fail();
                }
            } else {
                buffer = Math.max(buffer - bufferDecay.getDouble(), 0);
            }
        }

        if (packet.isFlying() || packet.isLook() || packet.isPosLook() || packet.isPosition()) {

            lastFlying = System.currentTimeMillis();
        }
    }
}
