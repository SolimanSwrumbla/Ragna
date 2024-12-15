package com.github.ageofwar.ragna.example;

import com.github.ageofwar.ragna.*;
import com.github.ageofwar.ragna.opengl.scene.Scene3D;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_H;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_R;

public class SceneContent implements Scene3D.Content {
    private static final Model[] skybox = Model.skybox(ModelLoader.load("assets/skybox/model.obj"), 0.5f);
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

    public SceneContent(Window window) {
        this.window = window;
    }

    @Override
    public Iterable<Entity> entities(Camera camera, long time) {
        if (window.isKeyPressed(GLFW_KEY_R)) startSimulationTime = System.nanoTime();
        nearestPlanetDistance = Float.MAX_VALUE;
        var helpMode = helpMode();
        var entities = new ArrayList<Entity>();

        for (var planet : planets) {
            var position = planet.position(time - startSimulationTime);

            var virtualPosition = position.scale(1e-6f);
            var virtualRadius = planet.radius() * 1e-6f;
            nearestPlanetDistance = Math.min(nearestPlanetDistance, camera.position().distance(virtualPosition) - planet.radius() * 1e-6f);
            if (virtualPosition.distance(camera.position()) > SKYBOX_SIZE) {
                virtualRadius = virtualRadius * SKYBOX_SIZE / camera.position().distance(virtualPosition);
                virtualPosition = camera.position().add(camera.position().directionTo(virtualPosition), SKYBOX_SIZE);
            }
            virtualRadius = Math.max(virtualRadius,  helpMode ? FAR_PLANET_RADIUS * 5 : FAR_PLANET_RADIUS);
            entities.add(new Entity(helpMode ? helpModel(planet) : planet.model(), virtualPosition, Rotation.ZERO, new Scale(virtualRadius)));
        }

        entities.add(new Entity(skybox, camera.position(), Rotation.ZERO, new Scale(SKYBOX_SIZE)));
        return entities;
    }

    public float getNearestPlanetDistance() {
        return nearestPlanetDistance;
    }

    private boolean helpMode() {
        return window.isKeyPressed(GLFW_KEY_H);
    }

    public Model[] helpModel(Planet planet) {
        return new Model[] { new Model(point, new Material.Fill(planet.color())) };
    }
}
