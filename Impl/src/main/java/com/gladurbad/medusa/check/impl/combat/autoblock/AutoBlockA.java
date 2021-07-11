package com.gladurbad.medusa.check.impl.combat.autoblock;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.packetwrappers.play.out.helditemslot.WrappedPacketOutHeldItemSlot;

//skidded from hades

@CheckInfo(name = "AutoBlock (A)", description = "Checks for autoblock cheats.")
public final class AutoBlockA extends Check {

    private int ticks;
    private boolean attacked;

    public AutoBlockA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isBlockDig()) {
            attacked = true;
        }
        if (packet.isBlockPlace()) {
            if (attacked) {
                if (ticks < 2) {
                    fail();
                    final int slot = data.getActionProcessor().getHeldItemSlot() == 8 ? 1 : 8;
                    final WrappedPacketOutHeldItemSlot wrapper = new WrappedPacketOutHeldItemSlot(slot);
                    PacketEvents.get().getPlayerUtils().sendPacket(data.getPlayer(), wrapper);
                }
                attacked = false;
            }
            ticks = 0;
        } else if (packet.isFlying()) {
            ticks++;
        }
    }
}
