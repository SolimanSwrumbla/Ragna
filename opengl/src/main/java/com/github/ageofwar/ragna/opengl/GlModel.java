package com.github.ageofwar.ragna.opengl;

import com.github.ageofwar.ragna.Model;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.stb.STBImage.*;

public class GlModel implements AutoCloseable {
    private final int objectId;
    private final int positionsBufferId;
    private final int indicesBufferId;
    private final int vertices;
    private final Material material;

    public static GlModel create(Model model) {
        var objectId = glGenVertexArrays();
        glBindVertexArray(objectId);

        var positionsBufferId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, positionsBufferId);
        glBufferData(GL_ARRAY_BUFFER, model.mesh().vertices(), GL_STATIC_DRAW);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glVertexAttrib2fv(2, new float[] {-1, -1});
        glDisableVertexAttribArray(2);

        var material = Material.from(model.material());
        material.create(model);

        var indicesBufferId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indicesBufferId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, model.mesh().indices(), GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindVertexArray(0);
        return new GlModel(objectId, positionsBufferId, indicesBufferId, model.mesh().numVertices(), material, Arrays.copyOfRange(model.mesh().vertices(), 0, 3));
    }

    private GlModel(int objectId, int positionsBufferId, int indicesBufferId, int vertices, Material material, float[] firstVertex) {
        this.objectId = objectId;
        this.positionsBufferId = positionsBufferId;
        this.indicesBufferId = indicesBufferId;
        this.vertices = vertices;
        this.material = material;
    }

    public void render() {
        glBindVertexArray(objectId);
        glDrawElements(GL_TRIANGLES, vertices, GL_UNSIGNED_INT, 0);
    }

    @Override
    public void close() {
        material.close();
        glDeleteBuffers(indicesBufferId);
        glDeleteBuffers(positionsBufferId);
        glDeleteVertexArrays(objectId);
    }

    public boolean isTransparent() {
        return material.isTransparent();
    }

    private interface Material {
        static Material from(com.github.ageofwar.ragna.Material material) {
            if (material instanceof com.github.ageofwar.ragna.Material.Fill fill) {
                return new Fill(fill);
            }
            if (material instanceof com.github.ageofwar.ragna.Material.Texture texture) {
                return new Texture(texture);
            }
            if (material instanceof com.github.ageofwar.ragna.Material.TextureFromResource texture) {
                return new Texture(texture);
            }
            throw new IllegalArgumentException();
        }

        void create(Model model);
        void close();
        boolean isTransparent();

        class Fill implements Material {
            private final com.github.ageofwar.ragna.Material.Fill fill;
            private int colorsBufferId = 0;

            public Fill(com.github.ageofwar.ragna.Material.Fill fill) {
                this.fill = fill;
            }

            @Override
            public void create(Model model) {
                var vertices = model.mesh().numVertices();
                var colors = new float[vertices * 4];
                for (int i = 0; i < vertices; i++) {
                    colors[i * 4] = fill.color().red();
                    colors[i * 4 + 1] = fill.color().green();
                    colors[i * 4 + 2] = fill.color().blue();
                    colors[i * 4 + 3] = fill.color().alpha();
                }
                colorsBufferId = glGenBuffers();
                glBindBuffer(GL_ARRAY_BUFFER, colorsBufferId);
                glBufferData(GL_ARRAY_BUFFER, colors, GL_STATIC_DRAW);
                glEnableVertexAttribArray(1);
                glVertexAttribPointer(1, 4, GL_FLOAT, false, 0, 0);
                glBindBuffer(GL_ARRAY_BUFFER, 0);
            }

            @Override
            public void close() {
                glDeleteBuffers(colorsBufferId);
            }

            @Override
            public boolean isTransparent() {
                return fill.color().alpha() < 1;
            }
        }

        class Texture implements Material {
            private final String path;
            private final boolean fromResource;
            private final float[] coordinates;
            private int textureId;
            private int textureBufferId;

            public Texture(com.github.ageofwar.ragna.Material.Texture texture) {
                this.path = texture.path();
                this.fromResource = false;
                this.coordinates = texture.coordinates();
            }

            public Texture(com.github.ageofwar.ragna.Material.TextureFromResource texture) {
                this.path = texture.path();
                this.fromResource = true;
                this.coordinates = texture.coordinates();
            }

            @Override
            public void create(Model model) {
                try (var stack = MemoryStack.stackPush()) {
                    var widthBuffer = stack.mallocInt(1);
                    var heightBuffer = stack.mallocInt(1);
                    var channels = stack.mallocInt(1);

                    var buffer = load(widthBuffer, heightBuffer, channels);
                    if (buffer == null) {
                        throw new RuntimeException("Failed to load texture file: " + stbi_failure_reason());
                    }
                    var width = widthBuffer.get();
                    var height = heightBuffer.get();

                    createTexture(width, height, buffer);

                    stbi_image_free(buffer);

                    textureBufferId = glGenBuffers();
                    glBindBuffer(GL_ARRAY_BUFFER, textureBufferId);
                    glBufferData(GL_ARRAY_BUFFER, coordinates, GL_STATIC_DRAW);
                    glEnableVertexAttribArray(2);
                    glVertexAttribPointer(2, 2, GL_FLOAT, false, 0, 0);
                    glBindBuffer(GL_ARRAY_BUFFER, 0);
                }
            }

            private void createTexture(int width, int height, ByteBuffer buffer) {
                textureId = glGenTextures();
                glBindTexture(GL_TEXTURE_2D, textureId);
                glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
                glGenerateMipmap(GL_TEXTURE_2D);
            }

            private ByteBuffer load(IntBuffer widthBuffer, IntBuffer heightBuffer, IntBuffer channels) {
                if (fromResource) {
                    try (var stream = getClass().getClassLoader().getResourceAsStream(path)) {
                        if (stream == null) throw new IOException("Resource not found: " + path);
                        var bytes = stream.readAllBytes();
                        var buffer = ByteBuffer.allocateDirect(bytes.length);
                        buffer.put(bytes).flip();
                        return stbi_load_from_memory(buffer, widthBuffer, heightBuffer, channels, 4);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                } else {
                    return stbi_load(path, widthBuffer, heightBuffer, channels, 4);
                }
            }

            @Override
            public void close() {
                glDeleteBuffers(textureBufferId);
                glDeleteTextures(textureId);
            }

            @Override
            public boolean isTransparent() {
                return false;
            }
        }
    }
}
