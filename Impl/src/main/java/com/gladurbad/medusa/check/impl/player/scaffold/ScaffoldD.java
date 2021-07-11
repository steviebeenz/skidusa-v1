package com.gladurbad.medusa.check.impl.player.scaffold;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.config.ConfigValue;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;

@CheckInfo(name = "Scaffold (D)", description = "Checks for SafeWalk scaffold.", experimental = true)
public final class ScaffoldD extends Check {

    private static final ConfigValue bufferamount = new ConfigValue(ConfigValue.ValueType.DOUBLE, "buffer");
    private static final ConfigValue bufferDecay = new ConfigValue(ConfigValue.ValueType.DOUBLE, "buffer-decay");

    double buffers = bufferamount.getDouble();
    double bufferDecays = bufferDecay.getDouble();

    private int placedTicks;

    private double lastLastAccel, lastAccel;

    public ScaffoldD(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isPosition()) {
            if (++placedTicks < 5 && isBridging() && !data.getActionProcessor().isSneaking()) {
                final double accel = data.getPositionProcessor().getDeltaXZ() - data.getPositionProcessor().getLastDeltaXZ();

                final boolean exempt = isExempt(ExemptType.FLYING);

                debug("accel=" + accel);
                if (accel < 0.0001 && lastAccel > 0.05 && lastLastAccel < 0.0001) {
                    if (++buffer > buffers && !exempt) {
                        fail();
                        buffer = 0;
                    }
                    else buffer = Math.max(buffer - bufferDecays, 0);
                }

                lastLastAccel = lastAccel;
                lastAccel = accel;
            }
        } else if (packet.isBlockPlace()) {
            if (data.getPlayer().getItemInHand().getType().isBlock()) placedTicks = 0;
            buffer = Math.max(buffer - 0.05, 0);
        }
    }
}
