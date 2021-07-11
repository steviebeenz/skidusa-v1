package com.gladurbad.medusa.data.processor;

import com.gladurbad.medusa.Medusa;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.util.MathUtil;
import lombok.Getter;
import org.bukkit.util.Vector;

import java.util.ArrayDeque;

@Getter
public final class RotationProcessor {

    private final PlayerData data;

    private float yaw, pitch, lastYaw, lastPitch,
        deltaYaw, deltaPitch, lastDeltaYaw, lastDeltaPitch,
        joltYaw, joltPitch, lastJoltYaw, lastJoltPitch, gcd;

    private int sensitivity, lastCinematic, cinematicTicks;

    private final ArrayDeque<Integer> sensitivitySamples = new ArrayDeque<>();

    private boolean cinematic;

    private double finalSensitivity;

    public RotationProcessor(final PlayerData data) {
        this.data = data;
    }

    public void handle(final float yaw, final float pitch) {
        lastYaw = this.yaw;
        lastPitch = this.pitch;

        this.yaw = yaw;
        this.pitch = pitch;

        lastDeltaYaw = deltaYaw;
        lastDeltaPitch = deltaPitch;

        deltaYaw = Math.abs(yaw - lastYaw) % 360F;
        deltaPitch = Math.abs(pitch - lastPitch);

        lastJoltPitch = joltPitch;
        lastJoltYaw = joltYaw;

        joltYaw = Math.abs(deltaYaw - lastDeltaYaw);
        joltPitch = Math.abs(deltaPitch - lastDeltaPitch);

        processCinematic();

        if (deltaPitch > 0 && deltaPitch < 30) {
            processSensitivity();
        }
    }

    private void processCinematic() {
        final float yawAccelAccel = Math.abs(joltYaw - lastJoltYaw);
        final float pitchAccelAccel = Math.abs(joltPitch - lastJoltPitch);

        final boolean invalidYaw = yawAccelAccel < .05 && yawAccelAccel > 0;
        final boolean invalidPitch = pitchAccelAccel < .05 && pitchAccelAccel > 0;

        final boolean exponentialYaw = String.valueOf(yawAccelAccel).contains("E");
        final boolean exponentialPitch = String.valueOf(pitchAccelAccel).contains("E");

        if (sensitivity < 100 && (exponentialYaw || exponentialPitch)) {
            cinematicTicks += 3;
        } else if (invalidYaw || invalidPitch) {
            cinematicTicks += 1;
        } else {
            if (cinematicTicks > 0) cinematicTicks--;
        }
        if (cinematicTicks > 20) {
            cinematicTicks--;
        }

        cinematic = cinematicTicks > 8 || (Medusa.INSTANCE.getTickManager().getTicks() - lastCinematic < 120);

        if (cinematic && cinematicTicks > 8) {
            lastCinematic = Medusa.INSTANCE.getTickManager().getTicks();
        }
    }

    private void processSensitivity() {
        final float gcd = (float) MathUtil.getGcd(deltaPitch, lastDeltaPitch);
        final double sensitivityModifier = Math.cbrt(0.8333 * gcd);
        final double sensitivityStepTwo = (sensitivityModifier / 0.6) - 0.3333;
        final double finalSensitivity = sensitivityStepTwo * 200;

        this.finalSensitivity = finalSensitivity;

        sensitivitySamples.add((int)finalSensitivity);

        if (sensitivitySamples.size() >= 40) {
            this.sensitivity = MathUtil.getMode(sensitivitySamples);

            final float gcdOne = (sensitivity / 200F) * 0.6F + 0.2F;
            this.gcd = gcdOne * gcdOne * gcdOne * 1.2F;

            sensitivitySamples.clear();
        }
    }

    //thank you to Nik, the developer of Alice, for the getAngle code below

    public static float getAngle(final Vector one, final Vector two) {

        final double dot = Math.min(Math.max(
                (one.getX() * two.getX() + one.getY() * two.getY() + one.getZ() * two.getZ())
                        / (one.length() * two.length()),
                -1.0),
                1.0);

        return (float) Math.acos(dot);
    }

    public Vector getDirection() {

        Vector vector = new Vector();

        final double rotX = getYaw();
        final double rotY = getPitch();

        final double radiansRotY = Math.toRadians(rotY);

        vector.setY(-Math.sin(radiansRotY));

        final double xz = Math.cos(radiansRotY);

        final double radiansRotX = Math.toRadians(rotX);

        vector.setX(-xz * Math.sin(radiansRotX));
        vector.setZ(xz * Math.cos(radiansRotX));

        return vector;
    }
}