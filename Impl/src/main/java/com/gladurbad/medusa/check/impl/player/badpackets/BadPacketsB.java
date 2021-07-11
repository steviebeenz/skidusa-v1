package com.gladurbad.medusa.check.impl.player.badpackets;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.in.abilities.WrappedPacketInAbilities;

/**
 * Created on 11/14/2020 Package com.gladurbad.medusa.check.impl.player.Protocol by GladUrBad
 */


@CheckInfo(name = "BadPackets (B)", description = "Checks for spoofed ability packets.")
public final class BadPacketsB extends Check {

    public BadPacketsB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isAbilities()) {
            final WrappedPacketInAbilities wrapper = new WrappedPacketInAbilities(packet.getRawPacket());

            final boolean invalid = wrapper.isFlightAllowed().get() && !data.getPlayer().getAllowFlight();

            if (invalid) {
                fail();
            }
        }
    }
}
