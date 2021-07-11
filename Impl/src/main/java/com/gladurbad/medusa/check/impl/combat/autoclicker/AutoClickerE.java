package com.gladurbad.medusa.check.impl.combat.autoclicker;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;
import com.gladurbad.medusa.util.MathUtil;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

//skidded from SpookyAC

@CheckInfo(name = "AutoClicker (E)", experimental = true, description = "Checks for consistency.")
public final class AutoClickerE extends Check {

    private Queue<Integer> intervals = new ConcurrentLinkedQueue();
    private int ticks = 0;
    private boolean swung = false;
    private boolean place = false;

    public AutoClickerE(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            if (swung && !place && !data.getActionProcessor().isDigging()) {

                if (ticks < 8) {

                    intervals.add(ticks);

                    if (intervals.size() >= 40) {

                        double deviation = MathUtil.deviation(intervals);

                        if (deviation < 0.325D) {
                            fail(" d=" + deviation);
                        }

                        intervals.clear();
                    }
                }

                ticks = 0;
            }

            place = false;
            swung = false;
            ++ticks;
        } else if (packet.isBlockPlace()) {
            place = true;
        } else if (packet.isUseEntity()) {
            final WrappedPacketInUseEntity wrapper = new WrappedPacketInUseEntity(packet.getRawPacket());
            if (wrapper.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK) {
                swung = true;
            }
        }
    }
}
