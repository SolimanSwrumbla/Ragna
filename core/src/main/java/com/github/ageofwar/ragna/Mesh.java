package com.github.ageofwar.ragna;

public record Mesh(float[] vertices, int[] indices) {
    public int numVertices() {
        return indices.length;
    }

    public float[] firstVertex() {
        return new float[] {vertices[0], vertices[1], vertices[2]};
    }

    public float[] firstVertexUniform() {
        return new float[] {vertices[0], vertices[1], vertices[2], 1};
    }
}
