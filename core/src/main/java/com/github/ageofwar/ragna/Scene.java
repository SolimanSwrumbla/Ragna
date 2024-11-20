package com.github.ageofwar.ragna;

public interface Scene {
    void render(Window window);
    default void close(Window window) {
    }
}
