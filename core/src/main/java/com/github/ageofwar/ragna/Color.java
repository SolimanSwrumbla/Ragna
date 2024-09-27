package com.github.ageofwar.ragna;

import java.util.Objects;

public final class Color {
    private float red;
    private float green;
    private float blue;
    private float alpha;

    public static final Color WHITE = rgb(1, 1, 1);
    public static final Color BLACK = rgb(0, 0, 0);
    public static final Color RED = rgb(1, 0, 0);
    public static final Color GREEN = rgb(0, 1, 0);
    public static final Color BLUE = rgb(0, 0, 1);
    public static final Color YELLOW = rgb(1, 1, 0);
    public static final Color CYAN = rgb(0, 1, 1);
    public static final Color MAGENTA = rgb(1, 0, 1);
    public static final Color TRANSPARENT = rgba(0, 0, 0, 0);

    public static Color rgba(float red, float green, float blue, float alpha) {
        return new Color(red, green, blue, alpha);
    }

    public static Color rgb(float red, float green, float blue) {
        return new Color(red, green, blue, 1);
    }

    private Color(float red, float green, float blue, float alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public float red() {
        return red;
    }

    public float green() {
        return green;
    }

    public float blue() {
        return blue;
    }

    public float alpha() {
        return alpha;
    }

    @Override
    public int hashCode() {
        return Objects.hash(red, green, blue, alpha);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Color color)) return false;
        return Float.compare(color.red, red) == 0 && Float.compare(color.green, green) == 0 && Float.compare(color.blue, blue) == 0 && Float.compare(color.alpha, alpha) == 0;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
