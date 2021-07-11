package com.gladurbad.medusa.check.impl.player.badpackets;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;

/**
 * Created on 11/14/2020 Package com.gladurbad.medusa.check.impl.player.Protocol by GladUrBad
 */

@CheckInfo(name = "BadPackets (A)", description = "Checks for bad rotation packets.")
public final class BadPacketsA extends Check {

    public BadPacketsA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isRotation()) {
            final float pitch = data.getRotationProcessor().getPitch();

            if (Math.abs(pitch) > 90) fail(String.format("Invalid Rotation", "pitch=%.2f", pitch));
        }
    }
}
