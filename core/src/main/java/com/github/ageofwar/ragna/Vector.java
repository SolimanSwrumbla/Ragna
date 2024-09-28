package com.github.ageofwar.ragna;

public class Vector {
    private Vector() {
    }

    public static float[] scale(float[] vector, float factor) {
        return new float[] { vector[0] * factor, vector[1] * factor, vector[2] * factor };
    }

    public static float[] product(float[] vector1, float[] vector2) {
        return new float[] { vector1[1] * vector2[2] - vector1[2] * vector2[1], vector1[2] * vector2[0] - vector1[0] * vector2[2], vector1[0] * vector2[1] - vector1[1] * vector2[0] };
    }
}
