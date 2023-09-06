package com.divinity.hmedia.rgrmechawarden.quest;

import com.divinity.hmedia.rgrmechawarden.quest.goal.AquireAdvancementGoal;
import com.divinity.hmedia.rgrmechawarden.quest.goal.BreakBlocksMechaMinesGoal;
import dev._100media.hundredmediaquests.goal.KillSpecificTypeGoal;
import dev._100media.hundredmediaquests.goal.QuestGoal;
import dev._100media.hundredmediaquests.quest.Quest;
import dev._100media.hundredmediaquests.quest.QuestType;
import dev._100media.hundredmediaquests.reward.QuestReward;
import net.minecraft.world.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class MechaMorphQuest extends Quest {
    public MechaMorphQuest(QuestType<?> type) {
        super(type);
    }

    @Override
    protected List<QuestGoal> initializeGoals() {
        List<QuestGoal> goals = new ArrayList<>();
        goals.add(new AquireAdvancementGoal("play_jukebox_in_meadows", "sound_of_music_advancement_goal"));
        goals.add(new KillSpecificTypeGoal(10, EntityType.PIGLIN_BRUTE));
        goals.add(new BreakBlocksMechaMinesGoal(500));
        return goals;
    }

    @Override
    protected List<QuestReward> initializeRewards() {
        List<QuestReward> rewards = new ArrayList<>();
        /*   rewards.add(new ItemQuestReward(new ItemStack(ItemInit.ACID_SPRAY.get())));*/
        return rewards;
    }
}
