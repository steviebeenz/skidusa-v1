package com.gladurbad.medusa.check.impl.combat.killaura;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;

/**
 * Created on 10/24/2020 Package com.gladurbad.medusa.check.impl.combat.killaura by GladUrBad
 */

@CheckInfo(name = "KillAura (A)", description = "Checks for multi-aura.")
public final class KillAuraA extends Check {

    public KillAuraA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isUseEntity()) {
            final int targets = data.getCombatProcessor().getCurrentTargets();

            debug("tg="+ targets);
            if (targets > 1) fail("tg=" + targets);

            WrappedPacketInUseEntity wrapper = new WrappedPacketInUseEntity(packet.getRawPacket());
        }
    }
}
