package com.github.ageofwar.ragna.opengl;

import com.github.ageofwar.ragna.Scene;
import com.github.ageofwar.ragna.Window;

public class FrameLimiterScene implements Scene {
    private final Scene scene;

    private final long minFrameTime;
    private long nextRenderTime;

    private FrameLimiterScene(Scene scene, long minFrameTime) {
        this.scene = scene;
        this.minFrameTime = minFrameTime;
        nextRenderTime = 0;
    }

    public static FrameLimiterScene maxFrameRate(Scene scene, int maxFrameRate) {
        return new FrameLimiterScene(scene, 1000000000L / maxFrameRate);
    }

    public static FrameLimiterScene minFrameTime(Scene scene, long minFrameTime) {
        return new FrameLimiterScene(scene, minFrameTime);
    }

    public static FrameLimiterScene unlimited(Scene scene) {
        return new FrameLimiterScene(scene, 0);
    }

    @Override
    public void render(Window window) {
        if (minFrameTime == 0 || window.isResizing()) {
            scene.render(window);
            return;
        }

        long currentTime = System.nanoTime();
        if (currentTime < nextRenderTime) {
            return;
        }
        scene.render(window);
        nextRenderTime = currentTime + minFrameTime;
    }

    @Override
    public void close(Window window) {
        scene.close(window);
    }
}
