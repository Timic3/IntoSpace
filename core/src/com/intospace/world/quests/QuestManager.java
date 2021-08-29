package com.intospace.world.quests;

public class QuestManager {
    private static QuestManager instance = null;

    private Quest currentQuest;

    public void setCurrentQuest(Quest quest) {
        this.currentQuest = quest;
    }

    public Quest getCurrentQuest() {
        return this.currentQuest;
    }

    public static QuestManager getInstance() {
        if (instance == null) {
            instance = new QuestManager();
        }
        return instance;
    }
}
