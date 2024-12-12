package com.github.ageofwar.ragna.example;

import com.github.ageofwar.ragna.Rotation;
import com.github.ageofwar.ragna.Window;

import java.util.function.Consumer;

import static org.lwjgl.glfw.GLFW.*;

class RotationCallback implements Window.MouseButtonCallback, Window.CursorPositionCallback {
    private final Window window;
    private Rotation start;
    private final Rotation velocity;
    private Window.CursorPosition dragStart;
    private final Consumer<Rotation> callback;

    public RotationCallback(Window window, Rotation start, Rotation velocity, Consumer<Rotation> callback) {
        this.window = window;
        this.start = start;
        this.velocity = velocity;
        this.callback = callback;
    }

    private Rotation newRotation(double x, double y) {
        var pitch = (float) (x - dragStart.x()) * velocity.pitch();
        var roll = (float) (y - dragStart.y()) * velocity.roll();
        var newRotation = start.add(new Rotation(roll, pitch, 0));
        if (newRotation.roll() > Math.PI / 2) newRotation = newRotation.withRoll((float) (Math.PI / 2));
        if (newRotation.roll() < -Math.PI / 2) newRotation = newRotation.withRoll((float) (-Math.PI / 2));
        return newRotation;
    }

    @Override
    public void invoke(double x, double y) {
        if (dragStart == null) return;
        callback.accept(newRotation(x, y));
    }

    @Override
    public void invoke(int button, int action, int mods) {
        if (action == GLFW_PRESS && button == GLFW_MOUSE_BUTTON_1) {
            dragStart = window.cursorPositionRelative();
        }
        if (action == GLFW_RELEASE && button == GLFW_MOUSE_BUTTON_1) {
            var pos = window.cursorPositionRelative();
            start = newRotation(pos.x(), pos.y());
            dragStart = null;
        }
    }
}
