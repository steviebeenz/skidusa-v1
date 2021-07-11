package com.gladurbad.medusa.check.impl.player.inventory;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;

@CheckInfo(name = "Inventory (B)", description = "Checks for moving while in inventory.", experimental = true)
public final class InventoryB extends Check {

    public InventoryB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isPosition()) {

            final boolean exempt = isExempt(
                    ExemptType.ICE, ExemptType.VELOCITY
            );

            if (data.getActionProcessor().isInventory()) {
                if (data.getPositionProcessor().isTeleporting()) {
                    data.getPlayer().closeInventory();
                    buffer = 0;
                    data.getActionProcessor().setInventory(false);
                }
                if ((data.getPositionProcessor().getDeltaXZ() > 0.1 || packet.isArmAnimation() || packet.isBlockPlace()
                        || packet.isBlockDig()) && data.getPositionProcessor().isOnGround() && !exempt) {
                    if (++buffer > 5) {
                        fail();
                        if (buffer >= 20) {
                            data.getPlayer().closeInventory();
                            buffer = 0;
                            data.getActionProcessor().setInventory(false);
                        }
                    }
                } else {
                    buffer -= buffer > 0 ? 1 : 0;
                }
            }
        }
    }
}
