package com.github.ageofwar.ragna;

public interface RenderContext {
    Engine engine();
    Window window();
    boolean isWindowResizing();
    void stateUpdated();
}
