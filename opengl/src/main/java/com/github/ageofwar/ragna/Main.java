package com.github.ageofwar.ragna;

import com.github.ageofwar.ragna.opengl.GlEngine;
import com.github.ageofwar.ragna.opengl.scene.DebugScene;
import com.github.ageofwar.ragna.opengl.scene.Scene3D;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Main {
    public static final int FPS = 156;
    public static final int IPS = 60;

    public static Model[] cube = ModelLoader.load("assets/cube.obj");
    public static Model[] auto = ModelLoader.load("assets/Car-Model/Car.obj");
    public static Model[] airplane = ModelLoader.load("assets/airplane/11803_Airplane_v1_l1.obj");
    public static Model[] skybox = Model.skybox(ModelLoader.load("assets/skybox/model.obj"), 0.5f);

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
        var camera = new Camera(Position.ORIGIN, Rotation.ZERO, new PerspectiveProjection((float) Math.toRadians(90), 0.1f, 1000f));
        var scene = new Scene3D(camera, Main::entities);
        scene.addLights(
                new Light.Ambient(Color.WHITE, 0.1f),
                new Light.Directional(new Direction(-1, -1, -1), Color.WHITE, 0.5f)
        );
        setRotationCallback(window, camera.rotation(), new Rotation(4, 4, 4), scene::setCameraRotation);
        setMovementCallback(window, camera.position(), new Position(4, 4, 4), Math.floorDiv(1000000000L, IPS), scene::getCamera, scene::setCameraPosition);
        return new DebugScene(scene);
    }

    public static Iterable<Entity> entities(Camera camera, long time) {
        var entities = new ArrayList<Entity>();
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 100; j++) {
                entities.add(new Entity(cube, new Position(i - 49.5f, -1, j - 49.5f)));
            }
        }
        entities.add(new Entity(auto, new Position(0, -0.5f, 0)));
        entities.add(new Entity(airplane, new Position(10, 10, 10), new Rotation((float) Math.PI / 2, 0, 0), new Scale(0.01f)));
        entities.add(new Entity(skybox, camera.position(), new Rotation((float) Math.PI / 2, 0, 0), new Scale(1)));
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
