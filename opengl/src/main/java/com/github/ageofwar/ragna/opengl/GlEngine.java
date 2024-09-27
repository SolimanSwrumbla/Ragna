package com.github.ageofwar.ragna.opengl;

import com.github.ageofwar.ragna.Engine;
import com.github.ageofwar.ragna.Scene;
import com.github.ageofwar.ragna.Window;
import com.github.ageofwar.ragna.WindowConfiguration;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GLUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;

import static org.lwjgl.glfw.GLFW.*;

public class GlEngine implements Engine {
    private EngineExecutorService executor;
    private ArrayList<GlWindow> windows;

    public static GlEngine create() {
        var engine = new GlEngine();
        engine.init();
        return engine;
    }

    public GlEngine() {
        executor = new EngineExecutorService();
        windows = new ArrayList<>();
    }

    @Override
    public void init() {
        if (!glfwInit()) throw new RuntimeException("Unable to initialize GLFW");
        GLFWErrorCallback.createPrint(System.err).set();
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            executor.executeRemaining();
            glfwPollEvents();
            windows.removeIf(window -> {
                if (window.shouldClose()) {
                    window.close();
                    return true;
                }
                window.render();
                return false;
            });
        }
    }

    @Override
    public void close() {
        for (var window : windows) {
            window.close();
        }
        glfwTerminate();
    }

    @Override
    public Window createWindow(WindowConfiguration configuration, Scene scene) {
        var id = glfwCreateWindow(configuration.width(), configuration.height(), configuration.title(), 0, 0);
        if (id == 0) throw new RuntimeException("Unable to create window");
        var window = new GlWindow(id, this, scene);
        this.windows.add(window);
        window.init();
        return window;
    }

    @Override
    public ExecutorService executor() {
        return executor;
    }

    private static class EngineExecutorService extends AbstractExecutorService {
        Queue<Runnable> tasks = new ConcurrentLinkedQueue<>();

        public void executeRemaining() {
            while (!tasks.isEmpty()) {
                tasks.poll().run();
            }
        }

        @Override
        public void shutdown() {
            throw new UnsupportedOperationException();
        }

        @NotNull
        @Override
        public List<Runnable> shutdownNow() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isShutdown() {
            return false;
        }

        @Override
        public boolean isTerminated() {
            return false;
        }

        @Override
        public boolean awaitTermination(long timeout, @NotNull TimeUnit unit) throws InterruptedException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void execute(@NotNull Runnable command) {
            tasks.add(command);
        }

        @Override
        public void close() {
        }
    }
}
