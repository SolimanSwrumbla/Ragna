package com.github.ageofwar.ragna;

public interface Scene extends AutoCloseable {
    void init(RenderContext context);
    void render(RenderContext context);
    @Override
    void close();
}
