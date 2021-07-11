package com.gladurbad.medusa.check.impl.combat.badaim;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.config.ConfigValue;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;

@CheckInfo(name = "BadAim (H)", description = "Checks for snappy rotation.", experimental = true)
public class BadAimH extends Check {

    //1.5
    private static final ConfigValue bufferamount = new ConfigValue(ConfigValue.ValueType.DOUBLE, "buffer");
    //0.1
    private static final ConfigValue bufferDecay = new ConfigValue(ConfigValue.ValueType.DOUBLE, "buffer-decay");

    private float lastDeltaYaw;
    private float lastLastDeltaYaw;

    public BadAimH(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isRotation()) {
            if (isExempt(ExemptType.TELEPORT, ExemptType.JOINED, ExemptType.RESPAWN)) return;

            float deltaYaw = data.getRotationProcessor().getDeltaYaw();

            if (deltaYaw < 5.0F && lastDeltaYaw > 20.0F && lastLastDeltaYaw < 5.0F) {
                double low = (deltaYaw - lastLastDeltaYaw) / 2.0F;
                double high = lastDeltaYaw;

                if (++buffer > bufferamount.getDouble()) {
                    fail("low=" + low + " high=" + high);
                }
            } else {
                buffer = Math.max(buffer - bufferDecay.getDouble(), 0);
            }

            this.lastLastDeltaYaw = this.lastDeltaYaw;
            this.lastDeltaYaw = deltaYaw;
        }
    }
}