package com.intospace.world.inventory;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class InventoryManager {
    private static InventoryManager instance = null;

    // public ArrayList<InventoryItem> items = new ArrayList<>();
    public LinkedHashMap<TextureAtlas.AtlasRegion, Integer> inventory = new LinkedHashMap<>();
    public int selectedIndex = 0;

    public static InventoryManager getInstance() {
        if (instance == null) {
            instance = new InventoryManager();
        }
        return instance;
    }

    public void scrolled(float amountX, float amountY) {
        if (amountY < 0) {
            if (selectedIndex + 1 > inventory.size() - 1)
                selectedIndex = 0;
            else
                ++selectedIndex;
        } else {
            if (selectedIndex - 1 < 0)
                selectedIndex = inventory.size() - 1;
            else
                --selectedIndex;
        }
    }

    public void setSelectedIndex(int index) {
        if (index > inventory.size() - 1 || index < 0) {
            return;
        }
        selectedIndex = index;
    }

    public TextureAtlas.AtlasRegion getHeldAtlas() {
        return (TextureAtlas.AtlasRegion) inventory.keySet().toArray()[selectedIndex];
    }

    public void reset() {
        instance = null;
    }
}
