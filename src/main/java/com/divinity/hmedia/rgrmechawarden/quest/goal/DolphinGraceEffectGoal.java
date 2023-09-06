package com.divinity.hmedia.rgrmechawarden.quest.goal;

import dev._100media.hundredmediaquests.goal.BasicQuestGoal;

public class DolphinGraceEffectGoal extends BasicQuestGoal {
    public DolphinGraceEffectGoal(double target) {
        super(target);
    }

    @Override
    public String getDescriptionId() {
        return "quest.goal.rgrmechawarden.dolphin_grace_effect_goal";
    }
}
