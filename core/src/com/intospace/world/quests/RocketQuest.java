package com.intospace.world.quests;

import com.intospace.enums.GameState;
import com.intospace.world.WorldManager;

public class RocketQuest extends Quest {
    public RocketQuest(int goal) {
        super(goal);
        this.description = "Go to your [#FF5555]Rocket[] and travel to another world.\n(Use [#FFFF00]right mouse button[] to load the fuel)";
    }

    @Override
    public void progress(int goal) {
        super.progress(goal);
        this.onCompletion();
    }

    @Override
    public void onCompletion() {
        super.onCompletion();

        if (this.progress >= this.goal && WorldManager.getInstance().getState() == GameState.RUNNING) {
            WorldManager.getInstance().setState(GameState.COMMENCE_TRAVEL);
        }
    }
}
