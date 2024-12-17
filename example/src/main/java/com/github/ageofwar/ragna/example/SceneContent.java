package com.github.ageofwar.ragna.example;

import com.github.ageofwar.ragna.*;
import com.github.ageofwar.ragna.opengl.scene.Scene3D;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class SceneContent implements Scene3D.Content {
    private static final Model[] skybox = ModelLoader.load("assets/skybox/model.obj");
    private static final Mesh point = ModelLoader.load("assets/point/sphere.obj")[0].mesh();

    private static final float SKYBOX_SIZE = 100;
    private static final float FAR_PLANET_RADIUS = SKYBOX_SIZE / 300;

    private final Window window;
    private final Planet[] planets = new Planet[] {
            Planet.SUN,
            Planet.MERCURY,
            Planet.VENUS,
            Planet.EARTH,
            Planet.MARS,
            Planet.JUPITER,
            Planet.SATURN,
            Planet.URANUS,
            Planet.NEPTUNE,
            Planet.PLUTO
    };
    private final MovementFunction movementFunction;
    private final RotationFunction rotationFunction;
    private final ZoomFunction zoomFunction;
    private long startSimulationTime = System.nanoTime();
    private float nearestPlanetDistance = Float.MAX_VALUE;
    private long lastTime = startSimulationTime;
    private Position lastPosition = new Position(0, 0, 100);
    private boolean paused = false;

    public SceneContent(Window window) {
        this.window = window;
        movementFunction = new MovementFunction(window, new Position(0, 0, 100));
        rotationFunction = new RotationFunction(window, Rotation.ZERO, new Rotation(4, 4, 4));
        zoomFunction = new ZoomFunction(window, (float) Math.toRadians(50), (float) Math.toRadians(5), 500000000);
        window.setKeyCallback((key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ENTER && action == GLFW_RELEASE) {
                paused = !paused;
                if (!paused) {
                    startSimulationTime += System.nanoTime() - lastTime;
                }
            }
        });
    }

    @Override
    public Camera camera(long time) {
        var rotation = rotationFunction.apply(time);
        var velocity = getCameraVelocity(nearestPlanetDistance);
        var position = movementFunction.apply(time, velocity, rotation);
        var fov = zoomFunction.apply(time);
        return new Camera(position, rotation, new PerspectiveProjection(fov, 0.001f, 174f));
    }

    @Override
    public Iterable<Entity> entities(Camera camera, long time) {
        if (window.isKeyPressed(GLFW_KEY_R)) startSimulationTime = System.nanoTime();
        if (!paused) {
            lastTime = time;
        }
        lastPosition = camera.position();
        nearestPlanetDistance = Float.MAX_VALUE;
        var helpMode = window.isKeyPressed(GLFW_KEY_H);
        var t = lastTime - startSimulationTime;
        var entities = new ArrayList<Entity>();

        for (var planet : planets) {
            var position = planet.position(t);

            var virtualPosition = position.scale(1e-6f);
            var virtualRadius = planet.radius() * 1e-6f;
            nearestPlanetDistance = Math.min(nearestPlanetDistance, camera.position().distance(virtualPosition) - planet.radius() * 1e-6f);
            if (virtualPosition.distance(camera.position()) > SKYBOX_SIZE) {
                if (!helpMode) virtualRadius = virtualRadius * SKYBOX_SIZE / camera.position().distance(virtualPosition);
                virtualPosition = camera.position().add(camera.position().directionTo(virtualPosition), SKYBOX_SIZE);
            }
            virtualRadius = Math.max(virtualRadius, FAR_PLANET_RADIUS);
            entities.add(new Entity(helpMode ? helpModel(planet) : planet.model(), virtualPosition, planet.rotation(t), new Scale(virtualRadius)));
        }

        entities.add(new Entity(skybox, camera.position(), Rotation.ZERO, new Scale(SKYBOX_SIZE)));
        return entities;
    }

    @Override
    public Iterable<Light> lights(Camera camera, long time) {
        return List.of(
            new Light.Ambient(Color.WHITE, 0.02f),
            new Light.Point(Position.ORIGIN, Color.WHITE, 1f, new Light.Attenuation(0.7f, 0.005f, 0))
        );
    }

    public float getNearestPlanetDistance() {
        return nearestPlanetDistance;
    }

    public Model[] helpModel(Planet planet) {
        return new Model[] { new Model(point, new Material.Fill(new Material.Emissive(planet.color()))) };
    }

    private Position getCameraVelocity(float nearestPlanetDistance) {
        if (nearestPlanetDistance > 25) return new Position(100, 100, 100);
        if (nearestPlanetDistance > 2.5) return new Position(10, 10, 10);
        return new Position(1, 1, 1);
    }

    public String windowTitle(double fps) {
        var time = Instant.ofEpochMilli((lastTime - startSimulationTime) / 1_000_000 * 60 * 60 * 24);
        var title = time.atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        if (paused) {
            title += " (paused)";
        }
        title += " - " + lastPosition;
        title += " - " + String.format("%.0f", fps) + " FPS";
        return title;
    }
}
