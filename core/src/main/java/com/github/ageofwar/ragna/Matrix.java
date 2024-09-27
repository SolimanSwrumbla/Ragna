package com.github.ageofwar.ragna;

public final class Matrix {
    private Matrix() {
    }

    public static float[] product(float[] a, float[] b) {
        float[] result = new float[16];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result[i * 4 + j] = 0;
                for (int k = 0; k < 4; k++) {
                    result[i * 4 + j] += a[i * 4 + k] * b[k * 4 + j];
                }
            }
        }
        return result;
    }

    public static String toString(float[] matrix) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                builder.append(matrix[i * 4 + j]).append(' ');
            }
            builder.append('\n');
        }
        return builder.toString();
    }
}
