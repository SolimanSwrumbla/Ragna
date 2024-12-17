package com.github.ageofwar.ragna.example;

import com.github.ageofwar.ragna.Direction;
import com.github.ageofwar.ragna.Position;
import com.github.ageofwar.ragna.Rotation;
import com.github.ageofwar.ragna.Window;

import static org.lwjgl.glfw.GLFW.*;

public class MovementFunction {
    private final Window window;
    private Position start;

    private long lastTime = System.nanoTime();

    public MovementFunction(Window window, Position start) {
        this.window = window;
        this.start = start;
    }

    public Position apply(long t, Position velocity, Rotation rotation) {
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
            var direction = rotation.direction(relativeDirection);
            start = start.add(Position.fromVector(new float[] {
                    direction.x() * velocity.x() * elapsedTime,
                    direction.y() * velocity.y() * elapsedTime,
                    direction.z() * velocity.z() * elapsedTime
            }));
        }
        lastTime = t;
        return start;
    }
}
