package com.divinity.hmedia.rgrmechawarden.quest.goal;

import dev._100media.hundredmediaquests.goal.BasicQuestGoal;

public class BreakBlocksMechaMinesGoal extends BasicQuestGoal {
    public BreakBlocksMechaMinesGoal(double target) {
        super(target);
    }

    @Override
    public String getDescriptionId() {
        return "quest.goal.rgrmechawarden.break_block_mecha_mines_goal";
    }
}
