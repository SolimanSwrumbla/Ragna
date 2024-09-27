package com.github.ageofwar.ragna;

public sealed interface Material permits Material.Fill {
    record Fill(Color color) implements Material {
    }
}
