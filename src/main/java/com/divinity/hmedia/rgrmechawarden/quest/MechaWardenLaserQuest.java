package com.divinity.hmedia.rgrmechawarden.quest;

import com.divinity.hmedia.rgrmechawarden.init.ItemInit;
import com.divinity.hmedia.rgrmechawarden.quest.goal.AquireAdvancementGoal;
import com.divinity.hmedia.rgrmechawarden.quest.goal.KillBatBowGoal;
import com.divinity.hmedia.rgrmechawarden.quest.goal.KillPlayersWristRocketsGoal;
import dev._100media.hundredmediamorphs.capability.MorphHolderAttacher;
import dev._100media.hundredmediaquests.goal.KillSpecificTypeGoal;
import dev._100media.hundredmediaquests.goal.QuestGoal;
import dev._100media.hundredmediaquests.quest.Quest;
import dev._100media.hundredmediaquests.quest.QuestType;
import dev._100media.hundredmediaquests.reward.ItemQuestReward;
import dev._100media.hundredmediaquests.reward.QuestReward;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public class MechaWardenLaserQuest extends Quest {

    public MechaWardenLaserQuest(QuestType<?> type) {
        super(type);
    }

    @Override
    protected List<QuestGoal> initializeGoals() {
        List<QuestGoal> goals = new ArrayList<>();
        goals.add(new AquireAdvancementGoal("voluntary_exile", "voluntary_exile_advancement_goal"));
        goals.add(new KillBatBowGoal(5));
        goals.add(new KillPlayersWristRocketsGoal(3));
        return goals;
    }

    @Override
    protected List<QuestReward> initializeRewards() {
        List<QuestReward> rewards = new ArrayList<>();
        rewards.add(new ItemQuestReward(new ItemStack(ItemInit.MECHA_WARDEN_LASER.get())));
        return rewards;
    }
}