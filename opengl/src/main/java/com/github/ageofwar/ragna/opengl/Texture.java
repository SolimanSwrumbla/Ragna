package com.github.ageofwar.ragna.opengl;

import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.stb.STBImage.*;

public record Texture(int id) {
    public static Texture loadFromResource(String resource) {
        try (var stack = MemoryStack.stackPush()) {
            var w = stack.mallocInt(1);
            var h = stack.mallocInt(1);
            var channels = stack.mallocInt(1);

            try (var stream = Texture.class.getClassLoader().getResourceAsStream(resource)) {
                if (stream == null) {
                    throw new IllegalArgumentException("Resource not found: " + resource);
                }
                var bytes = ByteBuffer.wrap(stream.readAllBytes());
                var buf = stbi_load(bytes, w, h, channels, 4);
                if (buf == null) {
                    throw new RuntimeException("Image file [" + resource + "] not loaded: " + stbi_failure_reason());
                }

                int width = w.get();
                int height = h.get();

                var textureId = generateTexture(width, height, buf);

                stbi_image_free(buf);

                return new Texture(textureId);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, id);
    }

    public void cleanup() {
        glDeleteTextures(id);
    }

    private static int generateTexture(int width, int height, ByteBuffer buf) {
        glEnable(GL_TEXTURE_2D);
        var textureId = glGenTextures();

        glBindTexture(GL_TEXTURE_2D, textureId);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);
        glGenerateMipmap(GL_TEXTURE_2D);

        return textureId;
    }
}