package com.github.ageofwar.ragna.opengl;

import com.github.ageofwar.ragna.RenderContext;
import com.github.ageofwar.ragna.Scene;

import static org.lwjgl.glfw.GLFW.*;

public class VSyncScene implements Scene {
    private final Scene scene;

    private VSyncScene(Scene scene) {
        this.scene = scene;
    }

    @Override
    public void init(RenderContext context) {
        glfwSwapInterval(1);
    }

    @Override
    public void render(RenderContext context) {
        scene.render(context);
    }

    @Override
    public void close() {
        glfwSwapInterval(0);
    }
}
