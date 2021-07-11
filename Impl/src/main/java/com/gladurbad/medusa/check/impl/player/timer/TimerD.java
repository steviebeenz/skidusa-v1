package com.gladurbad.medusa.check.impl.player.timer;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.config.ConfigValue;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;
import com.gladurbad.medusa.util.type.RollingAverageDouble;

@CheckInfo(name = "Timer (D)", description = "More advanced under 1.0 timer check.", experimental = true)
public final class TimerD extends Check {


    private static final ConfigValue bufferamount = new ConfigValue(ConfigValue.ValueType.DOUBLE, "buffer");
    private static final ConfigValue bufferdecay = new ConfigValue(ConfigValue.ValueType.DOUBLE, "buffer-decay");
    private static final ConfigValue timerspeeds = new ConfigValue(ConfigValue.ValueType.DOUBLE, "timer-speed");

    private long lastTime, lastMove;
    private final RollingAverageDouble timerRate = new RollingAverageDouble(20, 50.0);

    public TimerD(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isLook() || packet.isPosLook() || packet.isPosition() || packet.isFlying()) {

            long timeNow = System.currentTimeMillis();
            long diff = timeNow - lastMove;

            timerRate.add(diff);

            if (timeNow - lastTime >= 1000L) {

                lastTime = timeNow;

                double timerSpeed = 50.0 / timerRate.getAverage();

                if (timerSpeed < timerspeeds.getDouble()) {
                    if (++buffer > bufferamount.getDouble()) { //12
                        fail("speed:" + timerSpeed + " buffer:" + buffer);
                    }
                } else {
                    buffer = Math.max(buffer - bufferdecay.getDouble(), 0); //0.5
                }
            }
            lastMove = timeNow;
        }
    }
}