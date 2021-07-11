package com.gladurbad.medusa.check.impl.combat.killaura;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;

//skidded from Hades

@CheckInfo(name = "KillAura (G)", description = "Checks for switch aura.", experimental = true)
public class KillAuraG extends Check {

    private int ticks;

    public KillAuraG(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isUseEntity()) {
            if (data.getCombatProcessor().getTarget() != data.getCombatProcessor().getLastTarget()) {
                if (ticks < 2) {
                    if (++buffer > 2) {
                        fail();
                    }
                } else buffer *= 0.75;
            }
            ticks = 0;
        } else if (packet.isFlying()) {
            ++ticks;
        }
    }
}
