package com.gladurbad.medusa.check.impl.player.badpackets;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.config.ConfigValue;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.in.steervehicle.WrappedPacketInSteerVehicle;

/**
 * Created on 11/14/2020 Package com.gladurbad.medusa.check.impl.player.Protocol by GladUrBad
 */


@CheckInfo(name = "BadPackets (E)", description = "Checks for a common exploit in disabler modules.")
public final class BadPacketsE extends Check {

    private static final ConfigValue bufferamount = new ConfigValue(ConfigValue.ValueType.DOUBLE, "buffer");

    public BadPacketsE(final PlayerData data) {
        super(data);
    }


    @Override
    public void handle(final Packet packet) {
        if (packet.isSteerVehicle()) {
            final WrappedPacketInSteerVehicle wrapper = new WrappedPacketInSteerVehicle(packet.getRawPacket());

            final boolean unmount = wrapper.isDismount();

            final boolean invalid = data.getPlayer().getVehicle() == null && !unmount;

            if (invalid) {
                if (++buffer > bufferamount.getDouble()) {
                    fail("buffer=" + buffer);
                    buffer /= 2;
                }
            } else {
                buffer = 0;
            }
        }
    }
}
