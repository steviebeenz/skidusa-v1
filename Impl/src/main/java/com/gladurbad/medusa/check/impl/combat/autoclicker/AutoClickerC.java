package com.gladurbad.medusa.check.impl.combat.autoclicker;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.config.ConfigValue;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;
import com.gladurbad.medusa.util.MathUtil;

import java.util.ArrayDeque;

@CheckInfo(name = "AutoClicker (C)", experimental = true, description = "Checks for rounded(ish) CPS.")
public final class AutoClickerC extends Check {

    private static final ConfigValue bufferamount = new ConfigValue(ConfigValue.ValueType.DOUBLE, "buffer");
    private static final ConfigValue bufferDecay = new ConfigValue(ConfigValue.ValueType.DOUBLE, "buffer-decay");
    private static final ConfigValue sampleSize = new ConfigValue(ConfigValue.ValueType.DOUBLE, "sample-size");

    double buffers = bufferamount.getDouble();
    double bufferDecays = bufferDecay.getDouble();
    double sampleSizes = sampleSize.getDouble();

    private final ArrayDeque<Integer> samples = new ArrayDeque<>();
    private int ticks;

    public AutoClickerC(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isArmAnimation() && !isExempt(ExemptType.AUTO_CLICKER)) {

           if (ticks < 4) {
               samples.add(ticks);
           }

           if (samples.size() == sampleSizes) {

               final double cps = MathUtil.getCps(samples);
               final double difference = Math.abs(Math.round(cps) - cps);

               debug("diff=" + difference + " cps=" + cps + " buf=" + buffer);

               if (difference < 0.001) {
                   if ((buffer += 10) > buffers) {
                       fail("diff=" + difference);
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
