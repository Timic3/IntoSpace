package com.intospace.enums;

import com.badlogic.gdx.graphics.Color;

public enum DayNightCycle {
    MIDDAY(
            new Color(.1f, .1f, .1f, 1f),
            new Color(1f, 1f, 1f, 1f)
    ),
    SUNSET(
            new Color(250f / 255f - 0.9f, 214f / 255f - 0.9f, 165f / 255f - 0.9f, 0.1f),
            new Color(250f / 255f, 214f / 255f, 165f / 255f, 0f)
    ),
    MIDNIGHT(
            new Color(.02f, .02f, .02f, 1f),
            new Color(0f, 0f, 0f, 0f)
    ),
    SUNRISE(
            new Color(71f / 255f - 0.9f, 249f / 255f - 0.9f, 254f / 255f - 0.9f, .1f),
            new Color(71f / 255f, 249f / 255f, 254f / 255f, 0f)
    );

    Color ambientColor;
    Color lightColor;

    DayNightCycle(Color ambientColor, Color lightColor) {
        this.ambientColor = ambientColor;
        this.lightColor = lightColor;
    }

    public Color getAmbientColor() {
        return ambientColor;
    }

    public Color getLightColor() {
        return lightColor;
    }
}
