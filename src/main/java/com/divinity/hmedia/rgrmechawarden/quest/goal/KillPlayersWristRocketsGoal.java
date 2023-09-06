package com.divinity.hmedia.rgrmechawarden.quest.goal;

import dev._100media.hundredmediaquests.goal.BasicQuestGoal;

public class KillPlayersWristRocketsGoal extends BasicQuestGoal {
    public KillPlayersWristRocketsGoal(double target) {
        super(target);
    }

    @Override
    public String getDescriptionId() {
        return "quest.goal.rgrmechawarden.kill_players_wrist_rockets_goal";
    }
}
