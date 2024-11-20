package com.github.ageofwar.ragna;

public interface Scene {
    void render(Window window, long time);
    default void close(Window window) {
    }
}
