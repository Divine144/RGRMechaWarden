package com.divinity.hmedia.rgrmechawarden.quest.goal;

import dev._100media.hundredmediaquests.goal.BasicQuestGoal;

public class DamagePlayersMechaMorphGoal extends BasicQuestGoal {
    public DamagePlayersMechaMorphGoal(double target) {
        super(target);
    }

    @Override
    public String getDescriptionId() {
        return "quest.goal.rgrmechashark.damage_players_mecha_morph_goal";
    }
}
