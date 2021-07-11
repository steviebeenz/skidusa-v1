package com.gladurbad.medusa.check.impl.combat.autoclicker;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.config.ConfigValue;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;
import com.gladurbad.medusa.util.MathUtil;

import java.util.ArrayDeque;

/**
 * Created on 12/30/2020 Package com.gladurbad.medusa.check.impl.combat.autoclicker by GladUrBad
 */
@CheckInfo(name = "AutoClicker (B)", description = "Simple change in statistics check.")
public final class AutoClickerB extends Check {

    private static final ConfigValue bufferamount = new ConfigValue(ConfigValue.ValueType.DOUBLE, "buffer");
    private static final ConfigValue bufferDecay = new ConfigValue(ConfigValue.ValueType.DOUBLE, "buffer-decay");
    private static final ConfigValue sampleSize = new ConfigValue(ConfigValue.ValueType.DOUBLE, "sample-size");

    double buffers = bufferamount.getDouble();
    double bufferDecays = bufferDecay.getDouble();
    double sampleSizes = sampleSize.getDouble();

    private int ticks;
    private double lastDev, lastSkew, lastKurt;
    private ArrayDeque<Integer> samples = new ArrayDeque<>();

    public AutoClickerB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isArmAnimation() && !isExempt(ExemptType.AUTO_CLICKER)) {
            if (ticks < 4) {
                samples.add(ticks);
            }

            if (samples.size() == sampleSizes) {
                final double deviation = MathUtil.getStandardDeviation(samples);
                final double skewness = MathUtil.getSkewness(samples);
                final double kurtosis = MathUtil.getKurtosis(samples);

                final double deltaDeviation = Math.abs(deviation - lastDev);
                final double deltaSkewness = Math.abs(skewness - lastSkew);
                final double deltaKurtosis = Math.abs(kurtosis - lastKurt);

                debug("dd=" + deltaDeviation + " ds=" + deltaSkewness + " dk=" + deltaKurtosis);

                if (deltaDeviation < 0.01 || deltaSkewness < 0.01 || deltaKurtosis < 0.01) {
                    if ((buffer += 20) > buffers) {
                        fail("dd=" + deltaDeviation + " ds=" + deltaSkewness + " dk=" + deltaKurtosis);
                    }
                } else {
                    buffer = Math.max(buffer - bufferDecays, 0);
                }

                lastDev = deviation;
                lastSkew = skewness;
                lastKurt = kurtosis;

                samples.clear();
            }

            ticks = 0;
        } else if (packet.isFlying()) {
            ++ticks;
        }
    }
}
