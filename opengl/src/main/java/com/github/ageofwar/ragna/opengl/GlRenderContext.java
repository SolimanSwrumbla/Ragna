package com.github.ageofwar.ragna.opengl;

import com.github.ageofwar.ragna.Engine;
import com.github.ageofwar.ragna.RenderContext;
import com.github.ageofwar.ragna.Window;

public class GlRenderContext implements RenderContext {
    private final Engine engine;
    private final Window window;
    private boolean resizing;
    private boolean stateUpdated;

    public GlRenderContext(Engine engine, Window window) {
        this.engine = engine;
        this.window = window;
        resizing = false;
        stateUpdated = true;
    }

    @Override
    public Engine engine() {
        return engine;
    }

    @Override
    public Window window() {
        return window;
    }

    @Override
    public boolean isWindowResizing() {
        return resizing;
    }

    @Override
    public void stateUpdated() {
        stateUpdated = true;
    }

    public boolean isStateUpdated() {
        return stateUpdated;
    }

    void resetStateUpdated() {
        stateUpdated = false;
    }

    void setWindowResizing(boolean resizing) {
        this.resizing = resizing;
    }
}
