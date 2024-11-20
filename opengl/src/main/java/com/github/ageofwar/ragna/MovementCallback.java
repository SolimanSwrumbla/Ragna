package com.github.ageofwar.ragna;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.DoubleSupplier;
import java.util.function.LongConsumer;
import java.util.function.Supplier;

import static org.lwjgl.glfw.GLFW.*;

public class MovementCallback implements LongConsumer {
    private final Window window;
    private Position start;
    private final Position velocity;
    private final Consumer<Position> callback;
    private final Supplier<Camera> camera;

    private long lastTime = System.nanoTime();

    public MovementCallback(Window window, Position start, Position velocity, Consumer<Position> callback, Supplier<Camera> camera) {
        this.window = window;
        this.start = start;
        this.velocity = velocity;
        this.callback = callback;
        this.camera = camera;
    }

    @Override
    public void accept(long t) {
        var elapsedTime = (t - lastTime) / 1_000_000_000.0f;
        var upPressed = window.isKeyPressed(GLFW_KEY_W);
        var downPressed = window.isKeyPressed(GLFW_KEY_S);
        var leftPressed = window.isKeyPressed(GLFW_KEY_A);
        var rightPressed = window.isKeyPressed(GLFW_KEY_D);
        var spacePressed = window.isKeyPressed(GLFW_KEY_SPACE);
        var leftShiftPressed = window.isKeyPressed(GLFW_KEY_LEFT_SHIFT);
        var direction = new float[3];
        var pitch = this.camera.get().rotation().pitch();
        if (upPressed && !downPressed) {
            direction[0] += (float) Math.sin(pitch);
            direction[2] += (float) Math.cos(pitch);
        }
        if (downPressed && !upPressed) {
            direction[0] -= (float) Math.sin(pitch);
            direction[2] -= (float) Math.cos(pitch);
        }
        if (leftPressed && !rightPressed) {
            direction[0] += (float) Math.sin(pitch + Math.PI / 2);
            direction[2] += (float) Math.cos(pitch + Math.PI / 2);
        }
        if (rightPressed) {
            direction[0] -= (float) Math.sin(pitch + Math.PI / 2);
            direction[2] -= (float) Math.cos(pitch + Math.PI / 2);
        }
        if (spacePressed) {
            direction[1] -= 1;
        }
        if (leftShiftPressed) {
            direction[1] += 1;
        }
        if (!Vector.isZero(direction)) {
            direction = Vector.normalize(direction);
            start = start.add(Position.fromVector(new float[] {
                    direction[0] * velocity.x() * elapsedTime,
                    direction[1] * velocity.y() * elapsedTime,
                    direction[2] * velocity.z() * elapsedTime
            }));
            callback.accept(start);
        }
        lastTime = t;
    }
}
