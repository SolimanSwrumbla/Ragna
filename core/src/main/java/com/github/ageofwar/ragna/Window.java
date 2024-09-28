package com.github.ageofwar.ragna;

public interface Window {
    void close();
    Size viewportSize();
    float aspectRatio();
    boolean isResizing();
    Engine engine();
    void requestRendering();

    record Size(int width, int height) {
    }
}
