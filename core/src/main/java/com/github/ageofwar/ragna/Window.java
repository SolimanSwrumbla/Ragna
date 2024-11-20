package com.github.ageofwar.ragna;

public interface Window {
    void close();
    void setScene(Scene scene);
    Size viewportSize();
    float aspectRatio();
    boolean isResizing();
    Engine engine();
    boolean isKeyPressed(int key);
    CursorPosition cursorPosition();
    CursorPosition cursorPositionRelative();

    void setCloseCallback(Runnable callback);
    void setKeyCallback(KeyCallback callback);
    void setMouseButtonCallback(MouseButtonCallback callback);
    void setCursorPositionCallback(CursorPositionCallback callback);
    void setCursorPositionRelativeCallback(CursorPositionCallback callback);

    record Size(int width, int height) {
    }

    interface KeyCallback {
        void invoke(int key, int scancode, int action, int mods);
    }

    interface MouseButtonCallback {
        void invoke(int button, int action, int mods);
    }

    interface CursorPositionCallback {
        void invoke(double x, double y);
    }

    record CursorPosition(double x, double y) {
    }
}
