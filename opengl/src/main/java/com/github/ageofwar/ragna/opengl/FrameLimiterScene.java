package com.github.ageofwar.ragna.opengl;

import com.github.ageofwar.ragna.RenderContext;
import com.github.ageofwar.ragna.Scene;

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
    public void init(RenderContext context) {
        nextRenderTime = 0;
        scene.init(context);
    }

    @Override
    public void render(RenderContext context) {
        if (minFrameTime == 0 || context.isWindowResizing()) {
            scene.render(context);
            return;
        }

        long currentTime = System.nanoTime();
        if (currentTime < nextRenderTime) {
            return;
        }
        scene.render(context);
        nextRenderTime = currentTime + minFrameTime;
    }

    @Override
    public void close() {
        scene.close();
    }
}
