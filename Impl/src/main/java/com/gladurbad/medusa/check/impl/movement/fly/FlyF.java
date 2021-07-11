package com.gladurbad.medusa.check.impl.movement.fly;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;

@CheckInfo(name = "Fly (F)", description = "Checks for stable fly.")
public final class FlyF extends Check {

    // Custom check. Made by Spiriten 6/23/21

    private double stableY;
  //  private boolean lastAir;

    public FlyF(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isPosition()) {
            if (isExempt(ExemptType.FLYING)) return;

            if (data.getPositionProcessor().getY() == data.getPositionProcessor().getLastY() &&
                    data.getPositionProcessor().isInAir() // && lastAir
            ) {
                stableY += 1;
            } else {
                stableY = 0;
            }

            if (stableY > 2) {
                fail(stableY);
            }

           // lastAir = data.getPositionProcessor().isInAir();
        }
    }
}