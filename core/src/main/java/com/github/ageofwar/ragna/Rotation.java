package com.github.ageofwar.ragna;

public record Rotation(float roll, float pitch, float yaw) {
    public Rotation add(Rotation rotation) {
        return new Rotation(roll + rotation.roll, pitch + rotation.pitch, yaw + rotation.yaw);
    }

    public Rotation add(float roll, float pitch, float yaw) {
        return add(new Rotation(roll, pitch, yaw));
    }

    public Rotation normalized() {
        return new Rotation(roll % (2 * (float) Math.PI), pitch % (2 * (float) Math.PI), yaw % (2 * (float) Math.PI));
    }

    public float[] rotationMatrix() {
        float cosRoll = (float) Math.cos(roll);
        float sinRoll = (float) Math.sin(roll);
        float cosPitch = (float) Math.cos(pitch);
        float sinPitch = (float) Math.sin(pitch);
        float cosYaw = (float) Math.cos(yaw);
        float sinYaw = (float) Math.sin(yaw);
        return new float[]{
                cosPitch * cosYaw, cosPitch * sinYaw, -sinPitch, 0,
                sinRoll * sinPitch * cosYaw - cosRoll * sinYaw, sinRoll * sinPitch * sinYaw + cosRoll * cosYaw, sinRoll * cosPitch, 0,
                cosRoll * sinPitch * cosYaw + sinRoll * sinYaw, cosRoll * sinPitch * sinYaw - sinRoll * cosYaw, cosRoll * cosPitch, 0,
                0, 0, 0, 1
        };
    }

    public float[] toVector() {
        float cosPitch = (float) Math.cos(pitch);
        float sinPitch = (float) Math.sin(pitch);
        float cosYaw = (float) Math.cos(yaw);
        float sinYaw = (float) Math.sin(yaw);
        return new float[]{
                cosPitch * sinYaw,
                sinPitch,
                cosPitch * cosYaw,
        };
    }
}
