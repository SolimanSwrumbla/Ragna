package com.github.ageofwar.ragna;

public sealed interface Material permits Material.Fill, Material.Texture, Material.TextureFromResource {
    record Fill(Color color) implements Material {
    }

    record Texture(String path, float[] coordinates) implements Material {
    }

    record TextureFromResource(String path, float[] coordinates) implements Material {
    }
}
