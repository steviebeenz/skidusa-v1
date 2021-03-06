package com.gladurbad.medusa.check.impl.movement.noslow;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.config.ConfigValue;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.packetwrappers.play.out.helditemslot.WrappedPacketOutHeldItemSlot;

@CheckInfo(name = "NoSlow (A)", description = "Checks for NoSlow modules.", experimental = true)
public final class NoSlowA extends Check {

    private static final ConfigValue bufferamount = new ConfigValue(ConfigValue.ValueType.DOUBLE, "buffer");

    double buffers = bufferamount.getDouble();

    public NoSlowA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isPosition()) {
            if (data.getActionProcessor().isSprinting() &&
                    data.getPlayer().isBlocking()
            ) {
                debug("buffer=" + buffer);
                if (++buffer > buffers) {
                    double deltaXZ = data.getPositionProcessor().getDeltaXZ();
                    fail("buffer=" + buffer + " dXZ=" + deltaXZ);
                    final int slot = data.getActionProcessor().getHeldItemSlot() == 8 ? 1 : 8;
                    final WrappedPacketOutHeldItemSlot wrapper = new WrappedPacketOutHeldItemSlot(slot);
                    PacketEvents.get().getPlayerUtils().sendPacket(data.getPlayer(), wrapper);
                    buffer /= 2;
                }
            } else {
                buffer -= buffer > 0 ? 0.25 : 0;
            }
        }
    }
}
