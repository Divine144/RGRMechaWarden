package com.divinity.hmedia.rgrmechawarden.mixin;

import com.divinity.hmedia.rgrmechawarden.quest.goal.BreakBlocksMechaMinesGoal;
import com.divinity.hmedia.rgrmechawarden.utils.MechaWardenUtils;
import dev._100media.hundredmediamorphs.capability.MorphHolderAttacher;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Explosion.class)
public class ExplosionMixin {

    @Redirect(
            method = "finalizeExplosion",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;onBlockExploded(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/Explosion;)V",
                    remap = false
            )
    )
    public void rgrmechawarden$onInvokeOnBlockExploded(BlockState instance, Level level, BlockPos pos, Explosion explosion) {
        instance.onBlockExploded(level, pos, explosion);
        if (explosion.getDirectSourceEntity() instanceof ServerPlayer player && MorphHolderAttacher.getCurrentMorph(player).isPresent()) {
            MechaWardenUtils.addToGenericQuestGoal(player, BreakBlocksMechaMinesGoal.class);
        }
    }
}
