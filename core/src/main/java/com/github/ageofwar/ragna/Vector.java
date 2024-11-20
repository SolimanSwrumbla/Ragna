package com.github.ageofwar.ragna;

public class Vector {
    private Vector() {
    }

    public static float[] scale(float[] vector, float factor) {
        return new float[] { vector[0] * factor, vector[1] * factor, vector[2] * factor };
    }

    public static float[] crossProduct(float[] vector1, float[] vector2) {
        return new float[] { vector1[1] * vector2[2] - vector1[2] * vector2[1], vector1[2] * vector2[0] - vector1[0] * vector2[2], vector1[0] * vector2[1] - vector1[1] * vector2[0] };
    }

    public static float dotProduct(float[] vector1, float[] vector2) {
        return vector1[0] * vector2[0] + vector1[1] * vector2[1] + vector1[2] * vector2[2];
    }

    public static float[] normalize(float[] vector) {
        var length = (float) Math.sqrt(vector[0] * vector[0] + vector[1] * vector[1] + vector[2] * vector[2]);
        if (length == 0) return new float[] { 0, 0, 0 };
        return new float[] { vector[0] / length, vector[1] / length, vector[2] / length };
    }

    public static boolean isZero(float[] vector) {
        return vector[0] == 0 && vector[1] == 0 && vector[2] == 0;
    }
}
