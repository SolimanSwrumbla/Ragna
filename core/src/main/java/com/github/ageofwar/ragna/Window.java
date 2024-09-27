package com.github.ageofwar.ragna;

public interface Window {
    void close();
    Size viewportSize();
    float aspectRatio();
    Callback onResize(ResizeCallback callback);

    record Size(int width, int height) {
    }

    @FunctionalInterface
    interface ResizeCallback {
        void onResize(int width, int height);
    }
}
