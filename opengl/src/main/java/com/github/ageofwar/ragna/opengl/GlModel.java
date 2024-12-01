package com.github.ageofwar.ragna.opengl;

import com.github.ageofwar.ragna.Model;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.stb.STBImage.*;

public class GlModel implements AutoCloseable {
    private final int objectId;
    private final int positionsBufferId;
    private final int indicesBufferId;
    private final int normalsBufferId;
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

        glVertexAttrib2fv(1, new float[] {-1, -1});
        glDisableVertexAttribArray(1);

        var normalsBufferId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, normalsBufferId);
        glBufferData(GL_ARRAY_BUFFER, model.mesh().normals(), GL_STATIC_DRAW);
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        var material = Material.from(model.material());
        material.create(model);

        var indicesBufferId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indicesBufferId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, model.mesh().indices(), GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindVertexArray(0);
        return new GlModel(objectId, positionsBufferId, indicesBufferId, normalsBufferId, model.mesh().numVertices(), material);
    }

    private GlModel(int objectId, int positionsBufferId, int indicesBufferId, int normalsBufferId, int vertices, Material material) {
        this.objectId = objectId;
        this.positionsBufferId = positionsBufferId;
        this.indicesBufferId = indicesBufferId;
        this.normalsBufferId = normalsBufferId;
        this.vertices = vertices;
        this.material = material;
    }

    public void render(GlShaderProgram shaderProgram) {
        glBindVertexArray(objectId);
        material.prerender(shaderProgram);
        glDrawElements(GL_TRIANGLES, vertices, GL_UNSIGNED_INT, 0);
    }

    @Override
    public void close() {
        material.close();
        glDeleteBuffers(indicesBufferId);
        glDeleteBuffers(positionsBufferId);
        glDeleteBuffers(normalsBufferId);
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
            throw new IllegalArgumentException();
        }

        void create(Model model);
        void prerender(GlShaderProgram shaderProgram);
        void close();
        boolean isTransparent();

        class Fill implements Material {
            private final com.github.ageofwar.ragna.Material.Fill fill;

            public Fill(com.github.ageofwar.ragna.Material.Fill fill) {
                this.fill = fill;
            }

            @Override
            public void create(Model model) {
            }

            @Override
            public void prerender(GlShaderProgram shaderProgram) {
                shaderProgram.setUniform("material.ambient", fill.ambientColor());
                shaderProgram.setUniform("material.diffuse", fill.diffuseColor());
                shaderProgram.setUniform("material.specular", fill.specularColor());
                shaderProgram.setUniform("material.reflectance", fill.reflectance());
            }

            @Override
            public void close() {
            }

            @Override
            public boolean isTransparent() {
                return fill.ambientColor().alpha() < 1;
            }
        }

        class Texture implements Material {
            private final com.github.ageofwar.ragna.Material.Texture texture;
            private int textureId;
            private int textureBufferId;

            public Texture(com.github.ageofwar.ragna.Material.Texture texture) {
                this.texture = texture;
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
                    glBufferData(GL_ARRAY_BUFFER, texture.coordinates(), GL_STATIC_DRAW);
                    glEnableVertexAttribArray(1);
                    glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
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
                return stbi_load(texture.path(), widthBuffer, heightBuffer, channels, 4);
            }

            @Override
            public void prerender(GlShaderProgram shaderProgram) {
                glBindTexture(GL_TEXTURE_2D, textureId);
                shaderProgram.setUniform("material.ambient", texture.ambientColor());
                shaderProgram.setUniform("material.diffuse", texture.diffuseColor());
                shaderProgram.setUniform("material.specular", texture.specularColor());
                shaderProgram.setUniform("material.reflectance", texture.reflectance());
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
