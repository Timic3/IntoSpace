package com.intospace.enums;

import com.badlogic.gdx.graphics.Color;

public enum WorldType {
    EARTH(2, new Color(0, 1, 1, 1)),
    ROCKY(6, new Color(0, 0, 0, 1)),
    SANDY(4, new Color(251 / 255f, 236 / 255f, 167 / 255f, 1));

    double flatness;
    Color backgroundColor;

    WorldType(float flatness, Color backgroundColor) {
        this.flatness = flatness;
        this.backgroundColor = backgroundColor;
    }

    public double getFlatness() {
        return flatness;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }
}
