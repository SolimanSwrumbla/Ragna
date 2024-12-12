package com.github.ageofwar.ragna.example;

import com.github.ageofwar.ragna.Camera;
import com.github.ageofwar.ragna.Position;
import com.github.ageofwar.ragna.Vector;
import com.github.ageofwar.ragna.Window;

import java.util.function.Consumer;
import java.util.function.LongConsumer;
import java.util.function.Supplier;

import static org.lwjgl.glfw.GLFW.*;

public class MovementCallback implements LongConsumer {
    private final Window window;
    private Position start;
    private final Position velocity;
    private final Consumer<Position> callback;
    private final Supplier<Camera> camera;

    private boolean slowMode = false;

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

        if (window.isKeyPressed(GLFW_KEY_CAPS_LOCK)) {
            slowMode = !slowMode;
        }

        if (upPressed && !downPressed) {
            direction[0] -= (float) Math.sin(pitch);
            direction[2] -= (float) Math.cos(pitch);
        }
        if (downPressed && !upPressed) {
            direction[0] += (float) Math.sin(pitch);
            direction[2] += (float) Math.cos(pitch);
        }
        if (leftPressed && !rightPressed) {
            direction[0] -= (float) Math.sin(pitch + Math.PI / 2);
            direction[2] -= (float) Math.cos(pitch + Math.PI / 2);
        }
        if (rightPressed && !leftPressed) {
            direction[0] += (float) Math.sin(pitch + Math.PI / 2);
            direction[2] += (float) Math.cos(pitch + Math.PI / 2);
        }
        if (spacePressed && !leftShiftPressed) {
            direction[1] += 1;
        }
        if (leftShiftPressed && !spacePressed) {
            direction[1] -= 1;
        }
        if (!Vector.isZero(direction)) {
            direction = Vector.normalize(direction);
            start = start.add(Position.fromVector(new float[] {
                    direction[0] * (slowMode ? velocity.x() * 0.01f : velocity.x()) * elapsedTime,
                    direction[1] * (slowMode ? velocity.y() * 0.01f : velocity.y()) * elapsedTime,
                    direction[2] * (slowMode ? velocity.z() * 0.01f : velocity.z()) * elapsedTime
            }));
            callback.accept(start);
        }
        lastTime = t;
    }
}
