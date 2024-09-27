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

    public Gl3DScene(Camera camera, Model... models) {
        this.camera = camera;
        this.models = models;
    }

    @Override
    public void init(RenderContext context) {
        var vertexShader = GlShader.loadVertexFromResources("shaders/3D.vert");
        var fragmentShader = GlShader.loadFragmentFromResources("shaders/3D.frag");
        shaderProgram = GlShaderProgram.create(vertexShader, fragmentShader);
        vertexShader.close();
        fragmentShader.close();

        glModels = new GlModel[models.length];
        for (int i = 0; i < models.length; i++) {
            glModels[i] = GlModel.create(models[i]);
        }

        glfwSetKeyCallback(((GlWindow) context.window()).id(), (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_UP) {
                camera = camera.move(0, 0, -0.1f);
            }
            if (key == GLFW_KEY_DOWN) {
                camera = camera.move(0, 0, 0.1f);
            }
            if (key == GLFW_KEY_LEFT) {
                camera = camera.move(-0.1f, 0, 0);
            }
            if (key == GLFW_KEY_RIGHT) {
                camera = camera.move(0.1f, 0, 0);
            }
            if (key == GLFW_KEY_SPACE) {
                camera = camera.move(0, 0.1f, 0);
            }
            if (key == GLFW_KEY_LEFT_SHIFT) {
                camera = camera.move(0, -0.1f, 0);
            }
            System.out.println("Camera: " + camera);
            context.stateUpdated();
        });
    }

    @Override
    public void render(RenderContext context) {
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        GlShaderProgram.bind(shaderProgram);
        var aspectRatio = context.window().aspectRatio();
        shaderProgram.setUniform("projectionMatrix", camera.transformMatrix(aspectRatio));
        for (GlModel glModel : glModels) {
            glModel.render();
        }
    }

    @Override
    public void close() {
        for (GlModel glModel : glModels) {
            glModel.close();
        }
        shaderProgram.close();
    }
}
