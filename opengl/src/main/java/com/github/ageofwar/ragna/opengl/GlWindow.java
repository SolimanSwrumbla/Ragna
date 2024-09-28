package com.github.ageofwar.ragna.opengl;

import com.github.ageofwar.ragna.Engine;
import com.github.ageofwar.ragna.Scene;
import com.github.ageofwar.ragna.Window;
import org.lwjgl.opengl.GL;

import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL30.*;

public class GlWindow implements Window {
    private final Engine engine;
    private final long id;
    private boolean resizing;
    private boolean renderingRequested;
    private Scene scene;

    public GlWindow(long id, Engine engine, Scene scene) {
        this.engine = Objects.requireNonNull(engine);
        this.id = id;
        this.scene = Objects.requireNonNull(scene);
        renderingRequested = true;
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(id);
    }

    private void swapBuffers() {
        glfwSwapBuffers(id);
    }

    private void makeContextCurrent() {
        glfwMakeContextCurrent(id);
    }

    public void init() {
        makeContextCurrent();
        GL.createCapabilities();
        // GLUtil.setupDebugMessageCallback();
        glClearColor(0, 0, 0, 0);
        var viewport = viewportSize();
        glViewport(0, 0, viewport.width(), viewport.height());
        scene.init(this);
        glfwSwapInterval(1); // TODO: Make this configurable
        glfwSetWindowRefreshCallback(id, (window) -> {
            var viewportSize = viewportSize();
            glViewport(0, 0, viewportSize.width(), viewportSize.height());
            resizing = true;
            requestRendering();
            render();
            resizing = false;
        });
    }

    public void render() {
        if (renderingRequested) {
            renderingRequested = false;
            makeContextCurrent();
            scene.render(this);
            swapBuffers();
        }
    }

    @Override
    public void close() {
        glfwFreeCallbacks(id);
        scene.close(this);
        glfwDestroyWindow(id);
    }

    @Override
    public Size viewportSize() {
        int[] width = new int[1];
        int[] height = new int[1];
        glfwGetFramebufferSize(id, width, height);
        return new Size(width[0], height[0]);
    }

    @Override
    public float aspectRatio() {
        int[] width = new int[1];
        int[] height = new int[1];
        glfwGetFramebufferSize(id, width, height);
        return (float) width[0] / height[0];
    }

    @Override
    public boolean isResizing() {
        return resizing;
    }

    @Override
    public Engine engine() {
        return engine;
    }

    @Override
    public void requestRendering() {
        renderingRequested = true;
    }

    public long id() {
        return id;
    }
}
