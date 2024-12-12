package com.github.ageofwar.ragna.example;

import com.github.ageofwar.ragna.*;
import com.github.ageofwar.ragna.opengl.GlEngine;
import com.github.ageofwar.ragna.opengl.scene.DebugScene;
import com.github.ageofwar.ragna.opengl.scene.Scene3D;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Main {
    public static final int FPS = 156;
    public static final int IPS = 60;

    public static Model[] skybox = Model.skybox(ModelLoader.load("assets/skybox/model.obj"), 0.5f);
    public static Model[] point = ModelLoader.load("assets/point/sphere.obj");
    public static Model[] sun = ModelLoader.load("assets/sun/sphere.obj");
    public static Model[] earth = ModelLoader.load("assets/earth/sphere.obj");

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
        var camera = new Camera(new Position(0, 0, 100f), Rotation.ZERO, new PerspectiveProjection((float) Math.toRadians(90), 0.001f, 400f));
        var scene = new Scene3D(camera, Main::entities);
        scene.addLights(
                new Light.Ambient(Color.WHITE, 0.03f),
                new Light.Point(Position.ORIGIN, Color.WHITE, 1f, new Light.Attenuation(0, 0.02f, 0))
        );
        setRotationCallback(window, camera.rotation(), new Rotation(4, 4, 4), scene::setCameraRotation);
        setMovementCallback(window, camera.position(), new Position(10, 10, 10), Math.floorDiv(1000000000L, IPS), scene::getCamera, scene::setCameraPosition);
        return new DebugScene(scene);
    }

    public static Iterable<Entity> entities(Camera camera, long time) {
        var entities = new ArrayList<Entity>();
        entities.add(new Entity(sun, Position.ORIGIN, Rotation.ZERO, new Scale(0.69634f)));
        if (Position.ORIGIN.distance(camera.position()) > 150) {
            entities.add(new Entity(point, camera.position().add(camera.position().directionTo(Position.ORIGIN), 1), Rotation.ZERO, new Scale(0.005f)));
        }

        entities.add(new Entity(earth, new Position(147.1f, 0, 0), Rotation.ZERO, new Scale(0.006378f)));
        entities.add(new Entity(point, camera.position().add(camera.position().directionTo(new Position(147.1f, 0, 0)), 1), Rotation.ZERO, new Scale(0.005f)));

        entities.add(new Entity(skybox, camera.position(), Rotation.ZERO, new Scale(0.4f)));
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
