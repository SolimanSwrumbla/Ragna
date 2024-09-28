package com.github.ageofwar.ragna;

public record Position(float x, float y, float z) {
    public static Position fromVector(float[] vector) {
        return new Position(vector[0], vector[1], vector[2]);
    }

    public Position add(Position position) {
        return new Position(x + position.x, y + position.y, z + position.z);
    }

    public float[] translationMatrix() {
        return new float[]{
                1, 0, 0, x,
                0, 1, 0, y,
                0, 0, 1, z,
                0, 0, 0, 1
        };
    }
}
