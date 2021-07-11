package com.gladurbad.medusa.check.impl.player.packetorder;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;


// skidded from FairFight anticheat

@CheckInfo(name = "PacketOrder (B)", description = "Checks for a common scaffold packet order.", experimental = true)
public final class PacketOrderB extends Check {

    private int looks;
    private int stage;

    public PacketOrderB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isLook()) {
            if (stage == 0) {
                ++stage;
            } else if (stage == 4) {
                if ((buffer += 1.75) > 3.5) {
                    fail();
                }

                stage = 0;
            } else {
                looks = 0;
                stage = 0;
                buffer -= 0.2;
            }
        } else if (packet.isBlockPlace()) {
            if (stage == 1) {
                ++stage;
            } else {
                looks = 0;
                stage = 0;
            }
        } else if (packet.isArmAnimation()) {
            if (stage == 2) {
                ++stage;
            } else {
                looks = 0;
                stage = 0;
                buffer -= 0.2;
            }
        } else if (packet.isPosLook() || packet.isPosition()) {
            if (stage == 3) {
                if (++looks == 3) {
                    stage = 4;
                    looks = 0;
                }
            } else {
                looks = 0;
                stage = 0;
            }
        }
    }
}