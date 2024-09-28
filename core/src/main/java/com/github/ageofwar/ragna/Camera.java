package com.github.ageofwar.ragna;

public record Camera(Position position, Rotation rotation, Projection projection) {
    public Camera move(Position offset) {
        return new Camera(position.add(offset), rotation, projection);
    }

    public Camera move(float x, float y, float z) {
        return move(new Position(x, y, z));
    }

    public Camera rotate(Rotation offset) {
        return new Camera(position, rotation.add(offset), projection);
    }

    public Camera rotate(float roll, float pitch, float yaw) {
        return rotate(new Rotation(roll, pitch, yaw));
    }

    public Camera withRotation(Rotation rotation) {
        return new Camera(position, rotation, projection);
    }

    public float[] transformMatrix(float aspectRatio) {
        return Matrix.product(projection().projectionMatrix(aspectRatio), rotation.rotationMatrix(), position.translationMatrix());
    }
}
