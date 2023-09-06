package com.divinity.hmedia.rgrmechawarden.quest.goal;

import dev._100media.hundredmediaquests.goal.BasicQuestGoal;

public class KillPlayersLaserGoal extends BasicQuestGoal {
    public KillPlayersLaserGoal(double target) {
        super(target);
    }

    @Override
    public String getDescriptionId() {
        return "quest.goal.rgrmechawarden.kill_players_laser_goal";
    }
}
