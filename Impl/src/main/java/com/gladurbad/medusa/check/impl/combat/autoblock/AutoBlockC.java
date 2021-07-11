package com.gladurbad.medusa.check.impl.combat.autoblock;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.config.ConfigValue;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;

@CheckInfo(name = "AutoBlock (C)", description = "Checks for attacking and digging.")
public final class AutoBlockC extends Check {

    private static final ConfigValue bufferamount = new ConfigValue(ConfigValue.ValueType.DOUBLE, "buffer");
    private static final ConfigValue bufferDecay = new ConfigValue(ConfigValue.ValueType.DOUBLE, "buffer-decay");

    double buffers = bufferamount.getDouble();
    double bufferDecays = bufferDecay.getDouble();

    public AutoBlockC(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isUseEntity()) {
            final WrappedPacketInUseEntity wrapper = new WrappedPacketInUseEntity(packet.getRawPacket());

            if (wrapper.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK) {
                final boolean sword = data.getPlayer().getItemInHand().getType().toString().contains("SWORD");
                final boolean invalid = data.getActionProcessor().isSendingDig();

                if (invalid && sword) {
                    if (++buffer > buffers) {
                        fail();
                    }
                } else {
                    buffer = Math.max(buffer - bufferDecays, 0);
                }
            }
        }
    }
}
