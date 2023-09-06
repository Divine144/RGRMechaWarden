package com.divinity.hmedia.rgrmechawarden.quest.goal;

import dev._100media.hundredmediaquests.goal.BasicQuestGoal;

public class TradeWithVillagerGoal extends BasicQuestGoal {
    public TradeWithVillagerGoal(double target) {
        super(target);
    }

    @Override
    public String getDescriptionId() {
        return "quest.goal.rgrmechawarden.trade_with_villager_goal";
    }
}
