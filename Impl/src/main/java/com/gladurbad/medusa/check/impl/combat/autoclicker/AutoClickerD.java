package com.gladurbad.medusa.check.impl.combat.autoclicker;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.config.ConfigValue;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;
import com.gladurbad.medusa.util.MathUtil;
import com.gladurbad.medusa.util.type.Pair;

import java.util.ArrayDeque;
import java.util.List;

/**
 * Created by Elevated.
 * @link https://github.com/ElevatedDev/Frequency/blob/master/src/main/java/xyz/elevated/frequency/check/impl/autoclicker/AutoClickerE.java
 */

@CheckInfo(name = "AutoClicker (D)", experimental = true, description = "Checks for consistency.")
public final class AutoClickerD extends Check {

    private static final ConfigValue bufferamount = new ConfigValue(ConfigValue.ValueType.DOUBLE, "buffer");
    private static final ConfigValue bufferDecay = new ConfigValue(ConfigValue.ValueType.DOUBLE, "buffer-decay");
    private static final ConfigValue sampleSize = new ConfigValue(ConfigValue.ValueType.DOUBLE, "sample-size");
    private static final ConfigValue duplMin = new ConfigValue(ConfigValue.ValueType.DOUBLE, "duplicates-minimum");
    private static final ConfigValue outMax = new ConfigValue(ConfigValue.ValueType.DOUBLE, "outliers-maximum");

    double buffers = bufferamount.getDouble();
    double bufferDecays = bufferDecay.getDouble();

    private final ArrayDeque<Integer> samples = new ArrayDeque<>();
    private int ticks;

    public AutoClickerD(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isArmAnimation() && !isExempt(ExemptType.AUTO_CLICKER)) {
            if (ticks < 4) {
                samples.add(ticks);
            }

            if (samples.size() == sampleSize.getDouble()) {
                final Pair<List<Double>, List<Double>> outlierPair = MathUtil.getOutliers(samples);

                final int outliers = outlierPair.getX().size() + outlierPair.getY().size();
                final int duplicates = (int) (samples.size() - samples.stream().distinct().count());

                debug("outliers=" + outliers + " dupl=" + duplicates);

                if (outliers < outMax.getDouble() && duplicates > duplMin.getDouble()) {
                    if ((buffer += 10) > buffers) {
                        fail("outliers=" + outliers + " dupl=" + duplicates);
                    }
                } else {
                    buffer = Math.max(buffer - bufferDecays, 0);
                }
                samples.clear();
            }

            ticks = 0;
        } else if (packet.isFlying()) {
            ++ticks;
        }
    }
}
