package com.divinity.hmedia.rgrmechawarden.quest.goal;

import dev._100media.hundredmediaquests.goal.BasicQuestGoal;

public class MineSpawnerNetherGoal extends BasicQuestGoal {
    public MineSpawnerNetherGoal(double target) {
        super(target);
    }

    @Override
    public String getDescriptionId() {
        return "quest.goal.rgrmechawarden.mine_spawner_nether_goal";
    }
}
