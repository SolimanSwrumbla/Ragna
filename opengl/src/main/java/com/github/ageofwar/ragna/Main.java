package com.github.ageofwar.ragna;

import com.github.ageofwar.ragna.opengl.GlEngine;
import com.github.ageofwar.ragna.opengl.scene.Scene3D;
import com.github.ageofwar.ragna.opengl.scene.SceneFrameLimit;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class Main {
    public static final int FPS = 156;
    public static final int IPS = 60;

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
        var cube = ModelLoader.load("assets/cube.obj")[0];
        var camera = new Camera(Position.ORIGIN, Rotation.ZERO, new PerspectiveProjection((float) Math.toRadians(90), 0.01f, 1000f));
        var scene = Scene3D.withEntities(camera, entities(cube));
        setRotationCallback(window, camera.rotation(), new Rotation(2, 2, 2), scene::setCameraRotation);
        setMovementCallback(window, camera.position(), new Position(2, 2, 2), Math.ceilDiv(1000000000L, IPS), scene::getCamera, scene::setCameraPosition);
        return SceneFrameLimit.maxFrameRate(scene, FPS);
    }

    public static Entity[] entities(Model model) {
        var entities = new Entity[100];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                entities[i * 10 + j] = new Entity(model, new Position(i - 5, -1, j - 5), Rotation.ZERO, new Scale(0.999f));
            }
        }
        // random
        // for (int i = 0; i < 100; i++) {
        //     entities[i] = new Entity(cube, Position.fromVector(new float[]{(float) Math.random() * 10 - 5, (float) Math.random() * 10 - 5, (float) Math.random() * 10 - 5}), Rotation.random(), (float) Math.random() + 0.5f);
        // }
        return entities;
    }

    public static void setRotationCallback(Window window, Rotation start, Rotation velocity, Consumer<Rotation> callback) {
        var rotationCallback = new RotationCallback(window, start, velocity, callback);
        window.setMouseButtonCallback(rotationCallback);
        window.setCursorPositionRelativeCallback(rotationCallback);
    }

    public static void setMovementCallback(Window window, Position start, Position velocity, long period, Supplier<Camera> camera, Consumer<Position> callback) {
        var movementCallback = new MovementCallback(window, start, velocity, callback, camera);
        window.engine().executor().scheduleAtFixedRate(movementCallback, period, period);
    }
}
