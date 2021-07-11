package com.gladurbad.medusa.check.impl.player.packetorder;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;

//skidded from SpookyAC L

@CheckInfo(name = "PacketOrder (E)", description = "Checks for invalid entity use packets.", experimental = true)
public class PacketOrderE extends Check {

    private int ticks = 0, invalidTicks = 0, totalTicks = 0;
    private Integer lastTicks = null;

    public PacketOrderE(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            ++ticks;
        } else {
            WrappedPacketInUseEntity wrapper = new WrappedPacketInUseEntity(packet.getRawPacket());
            if (packet.isUseEntity() && wrapper.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK) {
                if (ticks <= 8) {
                    if (lastTicks != null) {
                        if (ticks == lastTicks) {
                            ++invalidTicks;
                        }

                        ++totalTicks;
                        if (totalTicks >= 25) {
                            if (invalidTicks > 22) {
                                fail();
                            }

                            invalidTicks = 0;
                            totalTicks = 0;
                        }
                    }

                    lastTicks = ticks;
                }

                ticks = 0;
            }
        }
    }
}
