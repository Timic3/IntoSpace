package com.intospace.world.quests;

public abstract class Quest {
    public int progress;
    public int goal;

    public String description;

    public Quest(int goal) {
        this.goal = goal;
    }

    public void progress(int progress) {
        this.progress += progress;
    }

    public void onCompletion() {

    }
}
