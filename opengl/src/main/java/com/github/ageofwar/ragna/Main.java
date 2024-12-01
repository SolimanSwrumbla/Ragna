package com.github.ageofwar.ragna;

import com.github.ageofwar.ragna.opengl.GlEngine;
import com.github.ageofwar.ragna.opengl.scene.DebugScene;
import com.github.ageofwar.ragna.opengl.scene.Scene3D;
import com.github.ageofwar.ragna.opengl.scene.SceneFrameLimit;

import java.util.ArrayList;
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
        var cube = ModelLoader.load("assets/cube.obj");
        var auto = ModelLoader.load("assets/Car-Model/Car.obj");
        var airplane = ModelLoader.load("assets/airplane/11803_Airplane_v1_l1.obj");
        var camera = new Camera(Position.ORIGIN, Rotation.ZERO, new PerspectiveProjection((float) Math.toRadians(90), 0.1f, 100f));
        var scene = Scene3D.withEntities(camera, entities(cube, auto, airplane));
        scene.addLights(
                new Light.Ambient(Color.WHITE, 0.1f),
                new Light.Directional(new Direction(1, -1, 1), Color.WHITE, 0.5f)
        );
        setRotationCallback(window, camera.rotation(), new Rotation(2, 2, 2), scene::setCameraRotation);
        setMovementCallback(window, camera.position(), new Position(2, 2, 2), Math.ceilDiv(1000000000L, IPS), scene::getCamera, scene::setCameraPosition);
        return new DebugScene(SceneFrameLimit.maxFrameRate(scene, FPS));
    }

    public static Entity[] entities(Model[]... models) {
        var entities = new ArrayList<Entity>();
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 100; j++) {
                entities.add(new Entity(models[0], new Position(i - 49.5f, -1, j - 49.5f)));
            }
        }
        entities.add(new Entity(models[1], new Position(0, -0.5f, 0)));
        entities.add(new Entity(models[2], new Position(10, 10, 10), new Rotation((float) Math.PI / 2, 0, 0), new Scale(0.01f)));
        return entities.toArray(new Entity[0]);
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
