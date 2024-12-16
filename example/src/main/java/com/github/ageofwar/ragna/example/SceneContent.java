package com.github.ageofwar.ragna.example;

import com.github.ageofwar.ragna.*;
import com.github.ageofwar.ragna.opengl.scene.Scene3D;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

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
    private long startSimulationTime = System.nanoTime();
    private float nearestPlanetDistance = Float.MAX_VALUE;
    private long lastTime = startSimulationTime;
    private boolean paused = false;


    public SceneContent(Window window) {
        this.window = window;
    }

    @Override
    public Iterable<Entity> entities(Camera camera, long time) {
        window.setTitle(windowTitle());
        if (window.isKeyPressed(GLFW_KEY_R)) startSimulationTime = System.nanoTime();
        if (!paused) {
            lastTime = time;
        }
        nearestPlanetDistance = Float.MAX_VALUE;
        var helpMode = window.isKeyPressed(GLFW_KEY_H);
        var entities = new ArrayList<Entity>();

        for (var planet : planets) {
            var position = planet.position(lastTime - startSimulationTime);

            var virtualPosition = position.scale(1e-6f);
            var virtualRadius = planet.radius() * 1e-6f;
            nearestPlanetDistance = Math.min(nearestPlanetDistance, camera.position().distance(virtualPosition) - planet.radius() * 1e-6f);
            if (virtualPosition.distance(camera.position()) > SKYBOX_SIZE) {
                if (!helpMode) virtualRadius = virtualRadius * SKYBOX_SIZE / camera.position().distance(virtualPosition);
                virtualPosition = camera.position().add(camera.position().directionTo(virtualPosition), SKYBOX_SIZE);
            }
            virtualRadius = Math.max(virtualRadius, FAR_PLANET_RADIUS);
            entities.add(new Entity(helpMode ? helpModel(planet) : planet.model(), virtualPosition, Rotation.ZERO, new Scale(virtualRadius)));
        }

        entities.add(new Entity(skybox, camera.position(), Rotation.ZERO, new Scale(SKYBOX_SIZE)));
        return entities;
    }

    public void setCallbacks() {
        window.setKeyCallback((key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ENTER && action == GLFW_RELEASE) {
                paused = !paused;
                if (!paused) {
                    startSimulationTime += System.nanoTime() - lastTime;
                }
            }
        });
    }

    public float getNearestPlanetDistance() {
        return nearestPlanetDistance;
    }

    public Model[] helpModel(Planet planet) {
        return new Model[] { new Model(point, new Material.Fill(new Material.Emissive(planet.color()))) };
    }

    private String windowTitle() {
        var time = Instant.ofEpochMilli((lastTime - startSimulationTime) / 1_000_000 * 60 * 60 * 24);
        var title = time.atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        if (paused) {
            title += " (paused)";
        }
        return title;
    }
}
