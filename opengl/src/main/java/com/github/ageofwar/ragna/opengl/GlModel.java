package com.github.ageofwar.ragna.opengl;

import com.github.ageofwar.ragna.Model;

import static org.lwjgl.opengl.GL30.*;

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

        var material = Material.from(model.material());
        material.create(model);

        var indicesBufferId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indicesBufferId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, model.mesh().indices(), GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindVertexArray(0);
        return new GlModel(objectId, positionsBufferId, indicesBufferId, model.mesh().numVertices(), material);
    }

    private GlModel(int objectId, int positionsBufferId, int indicesBufferId, int vertices, Material material) {
        this.objectId = objectId;
        this.positionsBufferId = positionsBufferId;
        this.indicesBufferId = indicesBufferId;
        this.vertices = vertices;
        this.material = material;
    }

    public void render() {
        glBindVertexArray(objectId);
        glDrawElements(GL_TRIANGLES, vertices, GL_UNSIGNED_INT, 0);
        // glBindVertexArray(0);
    }

    @Override
    public void close() {
        material.close();
        glDeleteBuffers(indicesBufferId);
        glDeleteBuffers(positionsBufferId);
        glDeleteVertexArrays(objectId);
    }

    private interface Material {
        static Material from(com.github.ageofwar.ragna.Material material) {
            if (material instanceof com.github.ageofwar.ragna.Material.Fill fill) {
                return new Fill(fill);
            }
            throw new IllegalArgumentException();
        }

        void create(Model model);
        void close();

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
        }
    }
}
