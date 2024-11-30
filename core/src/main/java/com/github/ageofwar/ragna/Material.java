package com.github.ageofwar.ragna;

public sealed interface Material permits Material.Fill, Material.Texture {
    record Fill(Color ambientColor, Color diffuseColor, Color specularColor, float reflectance) implements Material {
        public Fill(Color color, float reflectance) {
            this(color, color, color, reflectance);
        }

        public Fill(Color color) {
            this(color, 0);
        }
    }

    record Texture(String path, float[] coordinates, Color ambientColor, Color diffuseColor, Color specularColor, float reflectance) implements Material {
        public Texture(String path, float[] coordinates) {
            this(path, coordinates, Color.BLACK, Color.BLACK, Color.BLACK, 0);
        }
    }
}
