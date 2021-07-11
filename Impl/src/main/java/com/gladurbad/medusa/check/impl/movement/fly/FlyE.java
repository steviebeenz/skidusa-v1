package com.gladurbad.medusa.check.impl.movement.fly;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;

@CheckInfo(name = "Fly (E)", description = "Backup ground-spoof check.")
public final class FlyE extends Check {

    // custom check, but so bad that im not even gonna add my credit

    private boolean lastInAir;

    public FlyE(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (data.getActionProcessor().isPlacing()) return;

        if (packet.isPosition()) {
            if (data.getPositionProcessor().isOnGround()
            && data.getPositionProcessor().isLastOnGround()
            //&& lastLastGround
            && data.getPositionProcessor().isInAir()
            && lastInAir
            ) {
                fail();
            }

            lastInAir = data.getPositionProcessor().isInAir();
        }
    }
}