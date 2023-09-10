package com.divinity.hmedia.rgrmechawarden.ability;

import com.divinity.hmedia.rgrmechawarden.cap.SkulkHolderAttacher;
import dev._100media.hundredmediaabilities.ability.Ability;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;

public class LaserTurretAbility extends Ability {

    @Override
    public void executePressed(ServerLevel level, ServerPlayer pPlayer) {
        var holder = SkulkHolderAttacher.getSkulkHolderUnwrap(pPlayer);
        if (holder != null) {
            if (!holder.isMechaMorphed()) {
                if (holder.removeSkulk(20)) {
                    holder.setMechaMorphed(true);
                    holder.setCamouflagedBlock(Blocks.AIR);
                }
            }
            else {
                pPlayer.sendSystemMessage(Component.literal("Reset Morph").withStyle(ChatFormatting.GREEN), true);
                holder.setMechaMorphed(false);
            }
        }
        super.executePressed(level, pPlayer);
    }

    @Override
    public int getCooldownDuration() {
        return 20 * 5;
    }

}
