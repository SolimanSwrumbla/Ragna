package com.github.ageofwar.ragna.opengl.scene;

import com.github.ageofwar.ragna.Scene;
import com.github.ageofwar.ragna.Window;

public class DebugScene implements Scene {
    private final Scene scene;
    private long lastRenderTime;

    public DebugScene(Scene scene) {
        this.scene = scene;
    }

    @Override
    public void render(Window window, long time) {
        System.out.println("FPS: " + 1_000_000_000.0 / (time - lastRenderTime));
        scene.render(window, time);
        lastRenderTime = time;
    }
}
