package com.github.ageofwar.ragna.opengl;

import com.github.ageofwar.ragna.Callback;
import com.github.ageofwar.ragna.Engine;
import com.github.ageofwar.ragna.Scene;
import com.github.ageofwar.ragna.Window;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL30.*;

public class GlWindow implements Window {
    private final long id;
    private final GlRenderContext context;
    private final List<ResizeCallback> resizeCallbacks;
    private Scene scene;

    public GlWindow(long id, Engine engine, Scene scene) {
        this.id = id;
        this.scene = Objects.requireNonNull(scene);
        context = new GlRenderContext(engine, this);
        resizeCallbacks = new ArrayList<>();
    }

    public Callback onResize(ResizeCallback callback) {
        resizeCallbacks.add(callback);
        if (resizeCallbacks.size() == 1) {
            glfwSetFramebufferSizeCallback(id, (window, width, height) -> {
                resizeCallbacks.forEach((cb) -> cb.onResize(width, height));
            });
        }
        return () -> {
            resizeCallbacks.remove(callback);
            if (resizeCallbacks.isEmpty()) {
                glfwSetFramebufferSizeCallback(id, null);
            }
        };
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
        var viewport = context.window().viewportSize();
        glViewport(0, 0, viewport.width(), viewport.height());
        scene.init(context);
        glfwSetWindowRefreshCallback(id, (window) -> {
            var viewportSize = context.window().viewportSize();
            glViewport(0, 0, viewportSize.width(), viewportSize.height());
            context.setWindowResizing(true);
            context.stateUpdated();
            render();
            context.setWindowResizing(false);
        });
    }

    public void render() {
        if (context.isStateUpdated()) {
            makeContextCurrent();
            scene.render(context);
            swapBuffers();
            context.resetStateUpdated();
        }
    }

    @Override
    public void close() {
        glfwFreeCallbacks(id);
        glfwDestroyWindow(id);
        scene.close();
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

    public long id() {
        return id;
    }
}
