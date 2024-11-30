package com.github.ageofwar.ragna;

public sealed interface Light permits Light.Ambient, Light.Point, Light.Directional {
    record Ambient(Color color, float intensity) implements Light {
    }

    record Point(Position position, Color color, float intensity, Attenuation attenuation) implements Light {
    }

    record Directional(Direction direction, Color color, float intensity) implements Light {
    }

    record Attenuation(float constant, float linear, float quadratic) {
    }
}
