package com.gladurbad.medusa.check.impl.combat.velocity;

import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.check.Check;
import com.gladurbad.medusa.config.ConfigValue;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;
import com.gladurbad.medusa.util.MathUtil;
import com.gladurbad.medusa.util.PlayerUtil;
import org.bukkit.potion.PotionEffectType;

//created by me, actually somewhat fucking works
@CheckInfo(name = "Velocity (C)", experimental = true, description = "Checks for horizontal velocity modification.")
public final class VelocityC extends Check {

    //default: 0.635 | fp's fix: 0.594
    private static final ConfigValue sprintingVel = new ConfigValue(ConfigValue.ValueType.DOUBLE, "sprinting-velocity");
    //0.715
    private static final ConfigValue walkingVel = new ConfigValue(ConfigValue.ValueType.DOUBLE, "walking-velocity");
    //0.975
    private static final ConfigValue standingVel = new ConfigValue(ConfigValue.ValueType.DOUBLE, "standing-velocity");

    private double lastGroundTicks;

    public VelocityC(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        //im not dealing with these bs things. in a liquid, in a vehicle (boat or minecart), on soul sand, on slime,
        //on a climable (vine or ladder), sneaking, or blocking their sword
        if (data.getPositionProcessor().isInLiquid()
                || isExempt(ExemptType.INSIDE_VEHICLE) || data.getPositionProcessor().isOnSoulSand()
                || data.getPositionProcessor().isOnSlime() || data.getPositionProcessor().isOnClimbable()
                || data.getPlayer().isSneaking() || data.getPlayer().isBlocking()) return;

        //check if theyve recently taken kb, are lagging, have a block near their head, were last on ground,
        //not currently on ground, and were previously on the ground for atleast 3 ticks
        if (data.getVelocityProcessor().getTicksSinceVelocity() == 2
                && !data.getActionProcessor().isLagging()
                && !data.getPositionProcessor().isBlockNearHead()
                && data.getPositionProcessor().isLastOnGround()
                && !data.getPositionProcessor().isOnGround()
                && lastGroundTicks >= 3) {

            //calculate the horizontal movement offset
            double offset = MathUtil.hypot(
                    data.getPositionProcessor().getX() - data.getPositionProcessor().getLastX(),
                    data.getPositionProcessor().getZ() - data.getPositionProcessor().getLastZ());

            //calculate the horizontal velocity taken
            double velocityH = MathUtil.hypot(data.getVelocityProcessor().getVelocityX(),
                    data.getVelocityProcessor().getVelocityZ());

            //declare our sprinting velocity from config so we can modify it
            double sprintingVelocity = sprintingVel.getDouble();

            //handle potions. max support for speed 2 i think
            if (PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.SPEED) > 0) {
                sprintingVelocity = sprintingVelocity - (
                        PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.SPEED) * 0.073);
            }

            //handle hit reduction. took this line from Nemesis due to not knowing this
            if (data.getCombatProcessor().getHitTicks() <= 1) velocityH *= 0.6;

            //calculate the knockback ratio
            double ratio = (offset / velocityH);

            //just quick access
            double deltaXZ = data.getPositionProcessor().getLastDeltaXZ();

            //check if theyre sprinting with the proper velocity
            if ((deltaXZ > 0.25 || data.getPlayer().isSprinting()) && ratio < sprintingVelocity) {
                fail("S | " + "r=" + ratio + " dx=" + deltaXZ);

                //check if theyre slowly moving with the proper velocity
            } else if (deltaXZ > 0 && deltaXZ < 0.25 && ratio < walkingVel.getDouble()) {
                fail("W | " + "r=" + ratio + " dx=" + deltaXZ);

                //check if they havent moved with the proper velocity
            } else if (ratio < standingVel.getDouble() && deltaXZ < 0.1) {
                fail("N | " + "r=" + ratio + " dx=" + deltaXZ);
            }
        }

        //this is just setting previous on ground ticks. we need to be off ground to trigger this check, but we
        //still want to make sure they were on ground for atleast 3 ticks beforehand (to prevent fp's from speed being
        //boosted from falling)
        if (packet.isPosition()) {
            lastGroundTicks = data.getPositionProcessor().getGroundTicks();
        }
    }
}