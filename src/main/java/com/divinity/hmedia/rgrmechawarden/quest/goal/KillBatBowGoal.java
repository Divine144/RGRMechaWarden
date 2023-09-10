package com.divinity.hmedia.rgrmechawarden.quest.goal;

import dev._100media.hundredmediaquests.goal.BasicQuestGoal;

public class KillBatBowGoal extends BasicQuestGoal {

    public KillBatBowGoal(double target) {
        super(target);
    }

    @Override
    public String getDescriptionId() {
        return "quest.goal.rgrmechawarden.kill_bat_bow_goal";
    }
}
