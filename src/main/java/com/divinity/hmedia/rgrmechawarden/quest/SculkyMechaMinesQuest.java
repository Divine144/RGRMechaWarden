package com.divinity.hmedia.rgrmechawarden.quest;

import com.divinity.hmedia.rgrmechawarden.quest.goal.AquireAdvancementGoal;
import com.divinity.hmedia.rgrmechawarden.quest.goal.KillPlayersLaserGoal;
import com.divinity.hmedia.rgrmechawarden.quest.goal.MineSpawnerNetherGoal;
import dev._100media.hundredmediaquests.goal.HarvestBlocksGoal;
import dev._100media.hundredmediaquests.goal.QuestGoal;
import dev._100media.hundredmediaquests.quest.Quest;
import dev._100media.hundredmediaquests.quest.QuestType;
import dev._100media.hundredmediaquests.reward.QuestReward;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;

public class SculkyMechaMinesQuest extends Quest {
    public SculkyMechaMinesQuest(QuestType<?> type) {
        super(type);
    }

    @Override
    protected List<QuestGoal> initializeGoals() {
        List<QuestGoal> goals = new ArrayList<>();
        goals.add(new AquireAdvancementGoal("cure_zombie_villager", "zombie_doctor_advancement_goal"));
        goals.add(new MineSpawnerNetherGoal(2));
        goals.add(new KillPlayersLaserGoal(4));
        return goals;
    }

    @Override
    protected List<QuestReward> initializeRewards() {
        List<QuestReward> rewards = new ArrayList<>();
        /*   rewards.add(new ItemQuestReward(new ItemStack(ItemInit.ACID_SPRAY.get())));*/
        return rewards;
    }
}
