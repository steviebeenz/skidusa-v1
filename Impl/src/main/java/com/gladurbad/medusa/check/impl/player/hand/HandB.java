package com.gladurbad.medusa.check.impl.player.hand;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.in.blockplace.WrappedPacketInBlockPlace;
import io.github.retrooper.packetevents.utils.player.Direction;

//this check is completely copy n pasted from AntiHaxerMan (by Tecnio) here:
// https://github.com/Tecnio/AntiHaxerman/blob/master/src/main/java/me/tecnio/antihaxerman/check/impl/player/interact/InteractE.java

@CheckInfo(name = "Hand (B)", experimental = true, description = "Checks for valid block placement.")
public final class HandB extends Check {

    public HandB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isBlockPlace()) {
            final WrappedPacketInBlockPlace wrapper = new WrappedPacketInBlockPlace(packet.getRawPacket());

            if (wrapper.getDirection() == Direction.INVALID) fail();

            final float x = wrapper.getCursorPosition().get().getX();
            final float y = wrapper.getCursorPosition().get().getY();
            final float z = wrapper.getCursorPosition().get().getZ();

            for (final float value : new float[]{x, y, z}) {

                if (value > 1.0 || value < 0.0) fail();
            }
        }
    }
}