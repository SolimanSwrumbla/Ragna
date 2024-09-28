package com.github.ageofwar.ragna;

import com.github.ageofwar.ragna.opengl.*;

import java.time.Duration;

public class Main {
    public static void main(String[] args) {
        var mainThread = Thread.currentThread();
        var windowConfiguration = new WindowConfiguration("Hello World!", 800, 800);
        try (var engine = GlEngine.create()) {
            var model1 = new Model(new Mesh(new float[]{
                    -0.6f, 0.4f, 0.5f,
                    -0.6f, -0.6f, 0.5f,
                    0.4f, -0.6f, 0.5f,
                    0.4f, 0.4f, 0.5f,
            }, new int[]{0, 1, 3, 3, 1, 2}), new Material.Fill(Color.rgba(1.0f, 0.0f, 0.0f, 0.5f)));
            var model2 = new Model(new Mesh(new float[]{
                    -0.4f, 0.6f, -0.5f,
                    -0.4f, -0.4f, -0.5f,
                    0.6f, -0.4f, -0.5f,
                    0.6f, 0.6f, -0.5f,
            }, new int[]{0, 1, 3, 3, 1, 2}), new Material.Fill(Color.rgba(0.0f, 1.0f, 0.0f, 0.5f)));
            var camera = new Camera(new Position(0, 0, 0), new Rotation(0, 0, 0), new PerspectiveProjection((float) Math.toRadians(90), 0.01f, 1000f));
            var scene = new Gl3DScene(camera, model2, model1);
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