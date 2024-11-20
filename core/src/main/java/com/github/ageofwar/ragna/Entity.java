package com.github.ageofwar.ragna;

public record Entity(Model model, Position position, Rotation rotation, float scale) {
    public float[] transformMatrix() {
        return Matrix.product(position.matrix(), rotation.matrix(), new float[] {
                scale, 0, 0, 0,
                0, scale, 0, 0,
                0, 0, scale, 0,
                0, 0, 0, 1
        });
    }
}
