package com.github.ageofwar.ragna.example;

import com.github.ageofwar.ragna.*;
import com.github.ageofwar.ragna.opengl.GlEngine;
import com.github.ageofwar.ragna.opengl.scene.Scene3D;

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
        var camera = new Camera(new Position(0, 0, 100f), Rotation.ZERO, new PerspectiveProjection((float) Math.toRadians(90), 0.001f, 174f));
        var content = new SceneContent(window);
        var scene = new Scene3D(camera, content);
        scene.addLights(
                new Light.Ambient(Color.WHITE, 0.02f),
                new Light.Point(Position.ORIGIN, Color.WHITE, 1f, new Light.Attenuation(0, 0.02f, 0))
        );
        setRotationCallback(window, camera.rotation(), new Rotation(4, 4, 4), scene::setCameraRotation);
        setMovementCallback(window, camera.position(), () -> getCameraVelocity(content.getNearestPlanetDistance()), Math.floorDiv(1000000000L, IPS), scene::getCamera, scene::setCameraPosition);
        content.setCallbacks();
        //return new DebugScene(scene);
        return scene;
    }

    public static void setRotationCallback(Window window, Rotation start, Rotation velocity, Consumer<Rotation> callback) {
        var rotationCallback = new RotationCallback(window, start, velocity, callback);
        window.setMouseButtonCallback(rotationCallback);
        window.setCursorPositionRelativeCallback(rotationCallback);
    }

    public static void setMovementCallback(Window window, Position start, Supplier<Position> velocity, long period, Supplier<Camera> camera, Consumer<Position> callback) {
        var movementCallback = new MovementCallback(window, start, velocity, callback, camera);
        window.engine().executor().scheduleAtFixedRate(movementCallback, period, period);
    }

    public static Position getCameraVelocity(float nearestPlanetDistance) {
        if (nearestPlanetDistance > 25) return new Position(100, 100, 100);
        if (nearestPlanetDistance > 2.5) return new Position(10, 10, 10);
        if (nearestPlanetDistance > 0.25) return new Position(1, 1, 1);
        return new Position(0.05f, 0.05f, 0.05f);
    }
}
