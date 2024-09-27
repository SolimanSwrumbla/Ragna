package com.github.ageofwar.ragna;

import java.util.concurrent.ExecutorService;

public interface Engine extends Runnable, AutoCloseable {
    void init();

    @Override
    void run();

    void close();

    Window createWindow(WindowConfiguration configuration, Scene scene);

    ExecutorService executor();
}
