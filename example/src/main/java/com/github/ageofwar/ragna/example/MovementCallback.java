package com.github.ageofwar.ragna.example;

import com.github.ageofwar.ragna.Camera;
import com.github.ageofwar.ragna.Direction;
import com.github.ageofwar.ragna.Position;
import com.github.ageofwar.ragna.Window;

import java.util.function.Consumer;
import java.util.function.LongConsumer;
import java.util.function.Supplier;

import static org.lwjgl.glfw.GLFW.*;

public class MovementCallback implements LongConsumer {
    private final Window window;
    private Position start;
    private final Supplier<Position> velocity;
    private final Consumer<Position> callback;
    private final Supplier<Camera> camera;

    private long lastTime = System.nanoTime();

    public MovementCallback(Window window, Position start, Supplier<Position> velocity, Consumer<Position> callback, Supplier<Camera> camera) {
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
        var relativeDirection = Direction.NONE;

        if (upPressed && !downPressed) {
            relativeDirection = relativeDirection.add(Direction.FORWARD);
        }
        if (downPressed && !upPressed) {
            relativeDirection = relativeDirection.add(Direction.BACKWARD);
        }
        if (leftPressed && !rightPressed) {
            relativeDirection = relativeDirection.add(Direction.LEFT);
        }
        if (rightPressed && !leftPressed) {
            relativeDirection = relativeDirection.add(Direction.RIGHT);
        }
        if (spacePressed && !leftShiftPressed) {
            relativeDirection = relativeDirection.add(Direction.UP);
        }
        if (leftShiftPressed && !spacePressed) {
            relativeDirection = relativeDirection.add(Direction.DOWN);
        }
        if (relativeDirection != Direction.NONE) {
            var velocity = this.velocity.get();
            var camera = this.camera.get();
            var direction = camera.rotation().direction(relativeDirection);
            start = start.add(Position.fromVector(new float[] {
                    direction.x() * velocity.x() * elapsedTime,
                    direction.y() * velocity.y() * elapsedTime,
                    direction.z() * velocity.z() * elapsedTime
            }));
            callback.accept(start);
        }
        lastTime = t;
    }
}
