package com.github.ageofwar.ragna.example;

import com.github.ageofwar.ragna.Scene;
import com.github.ageofwar.ragna.Window;
import com.github.ageofwar.ragna.WindowConfiguration;
import com.github.ageofwar.ragna.opengl.GlEngine;
import com.github.ageofwar.ragna.opengl.scene.Scene3D;
import com.github.ageofwar.ragna.opengl.scene.SceneFrameLimit;
import com.github.ageofwar.ragna.opengl.scene.SceneTelemetry;

public class Main {
    public static final int FPS = 156;

    public static void main(String[] args) {
        var mainThread = Thread.currentThread();
        var windowConfiguration = new WindowConfiguration("Hello World!", 800, 800);
        try (var engine = GlEngine.create()) {
            var window = engine.createWindow(windowConfiguration);
            window.setScene(setupScene(window));
            window.setCloseCallback(mainThread::interrupt);
            engine.run();
        }
    }

    public static Scene setupScene(Window window) {
        var content = new SceneContent(window);
        var scene = new Scene3D(content);
        var frameLimit = SceneFrameLimit.maxFrameRate(scene, FPS);
        var telemetry = new SceneTelemetry(frameLimit);
        window.engine().asyncExecutor().scheduleAtFixedRate(() -> {
           window.setTitle(content.windowTitle(telemetry.getFps().getAverage()));
           telemetry.reset();
        }, 500000000, 500000000);
        return telemetry;
    }
}
