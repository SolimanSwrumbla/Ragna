package com.github.ageofwar.ragna;

public record Scale(float x, float y, float z) {
    public static final Scale ZERO = new Scale(0);
    public static final Scale ONE = new Scale(1);

    public Scale(float factor) {
        this(factor, factor, factor);
    }

    public float[] matrix() {
        return new float[] {
                x, 0, 0, 0,
                0, y, 0, 0,
                0, 0, z, 0,
                0, 0, 0, 1
        };
    }
}
