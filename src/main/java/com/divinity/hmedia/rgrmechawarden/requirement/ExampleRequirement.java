package com.divinity.hmedia.rgrmechawarden.requirement;

import dev._100media.hundredmediaquests.skill.requirements.SkillRequirement;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class ExampleRequirement implements SkillRequirement {
    @Override
    public boolean hasRequirement(Player player) {
        return false;
    }

    @Override
    public void consumeRequirement(ServerPlayer player) {

    }

    @Override
    public MutableComponent getFancyDescription(Player player) {
        return Component.empty();
    }
}
