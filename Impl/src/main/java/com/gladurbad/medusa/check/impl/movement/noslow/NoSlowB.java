package com.gladurbad.medusa.check.impl.movement.noslow;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.packetwrappers.play.out.helditemslot.WrappedPacketOutHeldItemSlot;
import org.bukkit.Difficulty;

@CheckInfo(name = "NoSlow (B)", description = "Checks for NoSlow modules.", experimental = true)
public final class NoSlowB extends Check {

    public NoSlowB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isPosition()) {
            if (data.getPlayer().getLocation().getWorld().getDifficulty() == Difficulty.PEACEFUL) return;
            if (data.getPlayer().isSprinting() && data.getPlayer().getFoodLevel() < 6) {
                fail();
                final int slot = data.getActionProcessor().getHeldItemSlot() == 8 ? 1 : 8;
                final WrappedPacketOutHeldItemSlot wrapper = new WrappedPacketOutHeldItemSlot(slot);
                PacketEvents.get().getPlayerUtils().sendPacket(data.getPlayer(), wrapper);
            }
        }
    }
}