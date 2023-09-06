package com.divinity.hmedia.rgrmechawarden.quest;

import com.divinity.hmedia.rgrmechawarden.init.ItemInit;
import com.divinity.hmedia.rgrmechawarden.quest.goal.AquireAdvancementGoal;
import com.divinity.hmedia.rgrmechawarden.quest.goal.DolphinGraceEffectGoal;
import com.divinity.hmedia.rgrmechawarden.quest.goal.TradeWithVillagerGoal;
import dev._100media.hundredmediaquests.goal.KillPlayersGoal;
import dev._100media.hundredmediaquests.goal.QuestGoal;
import dev._100media.hundredmediaquests.quest.Quest;
import dev._100media.hundredmediaquests.quest.QuestType;
import dev._100media.hundredmediaquests.reward.ItemQuestReward;
import dev._100media.hundredmediaquests.reward.QuestReward;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MountedWristRocketsQuest extends Quest {
    public MountedWristRocketsQuest(QuestType<?> type) {
        super(type);
    }

    @Override
    protected List<QuestGoal> initializeGoals() {
        List<QuestGoal> goals = new ArrayList<>();
        goals.add(new DolphinGraceEffectGoal(1));
        goals.add(new TradeWithVillagerGoal(1));
        goals.add(new KillPlayersGoal(2) {
            @Override
            public String getDescriptionId() {
                return "quest.goal.rgrmechawarden.kill_warden_hunters_goal";
            }
        });
        return goals;
    }

    @Override
    protected List<QuestReward> initializeRewards() {
        List<QuestReward> rewards = new ArrayList<>();
        rewards.add(new ItemQuestReward(new ItemStack(ItemInit.MOUNTED_WRIST_ROCKETS.get())));
        return rewards;
    }
}
