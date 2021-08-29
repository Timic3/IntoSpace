package com.intospace.world.quests;

public class DilithiumQuest extends Quest {
    public DilithiumQuest(int goal) {
        super(goal);
        this.description = "Find [#00FFFF]%s[]/%s of [#7D007B]Dilithium []to power your rocket engine.\n(Use [#FFFF00]left mouse button[] to collect)";
    }

    @Override
    public void progress(int goal) {
        super.progress(goal);
        this.onCompletion();
    }

    @Override
    public void onCompletion() {
        super.onCompletion();

        if (this.progress >= this.goal) {
            QuestManager.getInstance().setCurrentQuest(new RocketQuest(1));
        }
    }
}
