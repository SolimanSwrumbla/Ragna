package com.github.ageofwar.ragna;

public record Mesh(float[] vertices, int[] indices) {
    public int numVertices() {
        return indices.length;
    }
}
