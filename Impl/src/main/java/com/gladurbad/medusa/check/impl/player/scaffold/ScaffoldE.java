package com.gladurbad.medusa.check.impl.player.scaffold;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockPlace;


//skidded from FairFight anticheat

@CheckInfo(name = "Scaffold (E)", description = "Checks for high yaw/pitch change.", experimental = true)
public final class ScaffoldE extends Check {

    private BlockPosition lastBlock;
    private float lastYaw;
    private float lastPitch;
    private float lastX;
    private float lastY;
    private float lastZ;

    public ScaffoldE(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isBlockPlace()) {
            final PacketPlayInBlockPlace blockPlace = new PacketPlayInBlockPlace();

            final BlockPosition blockPosition = blockPlace.a();
            final float x = blockPlace.d();
            final float y = blockPlace.e();
            final float z = blockPlace.f();

            if (this.lastBlock != null
                    && x != this.lastBlock.getX()
                    && y != this.lastBlock.getY()
                    && z != this.lastBlock.getZ()
            ) {

                if (this.lastX == x
                        && this.lastY == y
                        && this.lastZ == z
                ) {
                    final float deltaAngle = Math.abs(this.lastYaw - data.getRotationProcessor().getYaw())
                            + Math.abs(this.lastPitch - data.getRotationProcessor().getPitch());

                    if (deltaAngle > 4.0f && ++buffer > 4) {
                        fail();
                    }
                } else {
                    buffer -= 0.5;
                }

                this.lastX = x;
                this.lastY = y;
                this.lastZ = z;
                this.lastYaw = data.getRotationProcessor().getYaw();
                this.lastPitch = data.getRotationProcessor().getLastPitch();
            }

            this.lastBlock = blockPosition;
        }
    }
}