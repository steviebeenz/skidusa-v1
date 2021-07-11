package com.gladurbad.medusa.check.impl.player.badpackets;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;

/**
 * Created on 11/14/2020 Package com.gladurbad.medusa.check.impl.player.Protocol by GladUrBad
 */

@CheckInfo(name = "BadPackets (F)", description = "Checks for no swing.")
public final class BadPacketsF extends Check {

    private int hits;

    public BadPacketsF(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isUseEntity()) {
            final WrappedPacketInUseEntity wrapper = new WrappedPacketInUseEntity(packet.getRawPacket());
            if (wrapper.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK) {
                debug("hits=" + hits);
                if (++hits > 2) {
                    fail("ticks=" + hits);
                }
            }
        } else if (packet.isArmAnimation()) {
            hits = 0;
        }
    }
}