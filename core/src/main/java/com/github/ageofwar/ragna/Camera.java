package com.github.ageofwar.ragna;

public record Camera(Position position, Projection projection) {
    public Camera move(Position offset) {
        return new Camera(position.add(offset), projection);
    }

    public Camera move(float x, float y, float z) {
        return move(new Position(x, y, z));
    }

    public float[] transformMatrix(float aspectRatio) {
        return Matrix.product(projection().projectionMatrix(aspectRatio), position.translationMatrix());
    }
}
