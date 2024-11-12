package com.github.ageofwar.ragna.opengl;

import com.github.ageofwar.ragna.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;

public class Gl3DScene implements Scene {
    private Camera camera;
    private Model[] models;

    private GlShaderProgram shaderProgram;
    private GlModel[] glModels;

    private Gl3DRenderer renderer;

    private Rotation dragStartRotation;
    private double dragStartX;
    private double dragStartY;

    public Gl3DScene(Camera camera, Model... models) {
        this.camera = camera;
        this.models = models;
        this.renderer = new Gl3DRenderer();
    }

    @Override
    public void init(Window window) {
        var vertexShader = GlShader.loadVertexFromResources("shaders/3D.vert");
        var fragmentShader = GlShader.loadFragmentFromResources("shaders/3D.frag");
        shaderProgram = GlShaderProgram.create(vertexShader, fragmentShader);
        GlShaderProgram.bind(shaderProgram);
        shaderProgram.setUniform("textureSampler", 0);
        vertexShader.close();
        fragmentShader.close();

        glModels = new GlModel[models.length];
        for (int i = 0; i < models.length; i++) {
            glModels[i] = GlModel.create(models[i]);
            renderer.addModel(glModels[i]);
        }

        glfwSetMouseButtonCallback(((GlWindow) window).id(), (id, button, action, mods) -> {
            if (action == GLFW_PRESS && button == GLFW_MOUSE_BUTTON_1) {
                var x = new double[1];
                var y = new double[1];
                glfwGetCursorPos(id, x, y);
                dragStartRotation = camera.rotation();
                dragStartX = x[0];
                dragStartY = y[0];
            }
            if (action == GLFW_RELEASE && button == GLFW_MOUSE_BUTTON_1) {
                dragStartRotation = null;
            }
        });

        glfwSetCursorPosCallback(((GlWindow) window).id(), (id, x, y) -> {
            if (dragStartRotation != null) {
                var dx = (float) (x - dragStartX) / 300;
                var dy = (float) (y - dragStartY) / 300;
                camera = camera.withRotation(dragStartRotation.add(new Rotation(dy, dx, 0)));
            }
        });
    }

    @Override
    public void render(Window window) {
        if (!(window instanceof GlWindow glWindow)) return;
        window.requestRendering();
        var upPressed = glfwGetKey(glWindow.id(), GLFW_KEY_W) == GLFW_PRESS;
        var downPressed = glfwGetKey(glWindow.id(), GLFW_KEY_S) == GLFW_PRESS;
        var leftPressed = glfwGetKey(glWindow.id(), GLFW_KEY_A) == GLFW_PRESS;
        var rightPressed = glfwGetKey(glWindow.id(), GLFW_KEY_D) == GLFW_PRESS;
        var spacePressed = glfwGetKey(glWindow.id(), GLFW_KEY_SPACE) == GLFW_PRESS;
        var leftShiftPressed = glfwGetKey(glWindow.id(), GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS;
        if (upPressed) {
            camera = camera.move(Position.fromVector(Vector.scale(new float[] { (float) Math.sin(camera.rotation().pitch()), 0, (float) Math.cos(camera.rotation().pitch()) }, 0.01f)));
        }
        if (downPressed) {
            camera = camera.move(Position.fromVector(Vector.scale(new float[] { (float) Math.sin(camera.rotation().pitch()), 0, (float) Math.cos(camera.rotation().pitch()) }, -0.01f)));
        }
        if (leftPressed) {
            camera = camera.move(Position.fromVector(Vector.scale(new float[] { (float) Math.sin(camera.rotation().pitch() + Math.PI / 2), 0, (float) Math.cos(camera.rotation().pitch() + Math.PI / 2) }, 0.01f)));
        }
        if (rightPressed) {
            camera = camera.move(Position.fromVector(Vector.scale(new float[] { (float) Math.sin(camera.rotation().pitch() + Math.PI / 2), 0, (float) Math.cos(camera.rotation().pitch() + Math.PI / 2) }, -0.01f)));
        }
        if (spacePressed) {
            camera = camera.move(0, -0.01f, 0);
        }
        if (leftShiftPressed) {
            camera = camera.move(0, 0.01f, 0);
        }
        renderer.updateView(camera);

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        GlShaderProgram.bind(shaderProgram);
        var aspectRatio = window.aspectRatio();
        shaderProgram.setUniform("projectionMatrix", camera.matrix(aspectRatio));
        renderer.render();
    }

    @Override
    public void close(Window window) {
        for (GlModel glModel : glModels) {
            glModel.close();
        }
        shaderProgram.close();
    }
}
