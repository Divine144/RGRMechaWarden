package com.divinity.hmedia.rgrmechawarden.quest;

import dev._100media.hundredmediaquests.goal.QuestGoal;
import dev._100media.hundredmediaquests.quest.Quest;
import dev._100media.hundredmediaquests.quest.QuestType;
import dev._100media.hundredmediaquests.reward.QuestReward;

import java.util.ArrayList;
import java.util.List;

public class PortableTeslaCoilQuest extends Quest {
    public PortableTeslaCoilQuest(QuestType<?> type) {
        super(type);
    }

    @Override
    protected List<QuestGoal> initializeGoals() {
        List<QuestGoal> goals = new ArrayList<>();
/*        goals.add(new AquireAdvancementGoal("lightning_rod_with_villager_no_fire", "surge_protector_advancement_goal"));
        goals.add(new KillEntityCritGoal(15, EntityType.CAVE_SPIDER) {
            @Override
            public String getDescriptionId() {
                return "quest.goal.rgrant.kill_cave_spider_crit_goal";
            }
        });
        goals.add(new KillPlayersMandiblesGoal(3));*/
        return goals;
    }

    @Override
    protected List<QuestReward> initializeRewards() {
        List<QuestReward> rewards = new ArrayList<>();
        /*   rewards.add(new ItemQuestReward(new ItemStack(ItemInit.ACID_SPRAY.get())));*/
        return rewards;
    }
}
