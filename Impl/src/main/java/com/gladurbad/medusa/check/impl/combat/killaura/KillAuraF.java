package com.gladurbad.medusa.check.impl.combat.killaura;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;

//skidded from LighterAC

@CheckInfo(name = "KillAura (F)", description = "Checks for killaura tendencies.", experimental = true)
public class KillAuraF extends Check {

    private int ticks, invalidTicks, lastTicks, totalTicks;

    public KillAuraF(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            ++ticks;
        } else if (packet.isUseEntity()) {
            if (ticks <= 8) {
                if (lastTicks == ticks) {
                    ++invalidTicks;
                }

                if (++totalTicks >= 25) {
                    if (invalidTicks > 22) {
                        fail();
                    }

                    totalTicks = 0;
                    invalidTicks = 0;
                }

                lastTicks = ticks;
            }

            ticks = 0;
        }
    }
}
