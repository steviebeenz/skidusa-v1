package com.gladurbad.medusa.check.impl.combat.autoclicker;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;
import com.gladurbad.medusa.util.MathUtil;
import io.github.retrooper.packetevents.packetwrappers.play.in.blockdig.WrappedPacketInBlockDig;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

//skidded from SpookyAC

@CheckInfo(name = "AutoClicker (F)", experimental = true, description = "Checks for consistency.")
public final class AutoClickerF extends Check {

    private Queue<Integer> averageTicks = new ConcurrentLinkedQueue();
    private int ticks = 0;
    private boolean swung = false;
    private boolean digging = false;
    private boolean abortedDigging = false;
    private int done = 0;
    private int lastTicks = 0;

    public AutoClickerF(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            if (this.swung && !this.digging) {
                if (this.ticks < 7) {
                    this.averageTicks.add(this.ticks);
                    if (this.averageTicks.size() > 50) {
                        this.averageTicks.poll();
                    }
                }

                if (this.averageTicks.size() > 40) {
                    double average = MathUtil.getAverage(this.averageTicks);
                    if (average < 2.5D) {
                        if (this.ticks > 3 && this.ticks < 20 && this.lastTicks < 20) {
                            this.done = 0;
                        } else if ((double)(this.done++) > 500.0D / (average * 1.5D)) {
                            fail();
                            this.done = 0;
                        }
                    } else {
                        this.done = 0;
                    }
                }

                this.lastTicks = this.ticks;
                this.ticks = 0;
            }

            if (this.abortedDigging) {
                this.digging = false;
                this.abortedDigging = false;
            }

            this.swung = false;
            ++this.ticks;
        } else if (packet.isArmAnimation()) {
            this.swung = true;
        } else if (packet.isBlockDig()) {
            WrappedPacketInBlockDig wrapper = new WrappedPacketInBlockDig(packet.getRawPacket());;
            if (wrapper.getDigType() == WrappedPacketInBlockDig.PlayerDigType.START_DESTROY_BLOCK) {
                this.digging = true;
                this.abortedDigging = false;
            } else if (wrapper.getDigType() == WrappedPacketInBlockDig.PlayerDigType.ABORT_DESTROY_BLOCK) {
                this.abortedDigging = true;
            }
        }

    }
}
