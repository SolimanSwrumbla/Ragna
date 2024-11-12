package com.github.ageofwar.ragna;

import com.github.ageofwar.ragna.opengl.*;

import java.time.Duration;

public class Main {
    public static void main(String[] args) {
        var mainThread = Thread.currentThread();
        var windowConfiguration = new WindowConfiguration("Hello World!", 800, 800);
        try (var engine = GlEngine.create()) {
            var cube = ObjLoader.loadResource("cube.obj", "cube.png");
            var camera = new Camera(Position.ORIGIN, Rotation.ZERO, new PerspectiveProjection((float) Math.toRadians(90), 0.01f, 1000f));
            var scene = new Gl3DScene(camera, cube);
            engine.createWindow(windowConfiguration, scene);
            new Thread(() -> {
                try {
                    Thread.sleep(Duration.ofSeconds(60));
                    mainThread.interrupt();
                } catch (InterruptedException ignored) {
                }
            }).start();
            engine.run();
        }
    }
}