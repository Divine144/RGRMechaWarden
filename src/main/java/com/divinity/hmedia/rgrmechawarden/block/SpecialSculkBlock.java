package com.divinity.hmedia.rgrmechawarden.block;

import dev._100media.hundredmediamorphs.capability.MorphHolderAttacher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SculkBlock;
import net.minecraft.world.level.block.state.BlockState;

public class SpecialSculkBlock extends SculkBlock {

    public SpecialSculkBlock(Properties pProperties) {
        super(pProperties);
    }


    // TODO: Change to event
    @Override
    public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
        if (pEntity instanceof LivingEntity living && !living.level().isClientSide && living.getFeetBlockState().is(this)) {
            if (MorphHolderAttacher.getCurrentMorph(living).isEmpty()) {
                if (!living.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) {
                    living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 1));
                }
            }
        }
    }
}
