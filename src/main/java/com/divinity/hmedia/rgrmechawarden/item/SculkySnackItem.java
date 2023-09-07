package com.divinity.hmedia.rgrmechawarden.item;

import com.divinity.hmedia.rgrmechawarden.init.MorphInit;
import dev._100media.hundredmediamorphs.capability.MorphHolderAttacher;
import dev._100media.hundredmediamorphs.morph.Morph;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class SculkySnackItem extends Item {

    public SculkySnackItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public SoundEvent getEatingSound() {
        return SoundEvents.GENERIC_EAT;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.EAT;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack pFood, Level pLevel, LivingEntity pLivingEntity) {
        if (pLivingEntity instanceof Player player) {
            player.eat(pLevel, pFood.copy());
            if (!player.level().isClientSide) {
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20 * 4, getLevelForMorph(MorphHolderAttacher.getCurrentMorphUnwrap(player))));
            }
        }
        return pFood;
    }

    private int getLevelForMorph(Morph morph) {
        if (morph == MorphInit.BABY_MECHA.get()) {
            return 1;
        }
        else if (morph == MorphInit.MECHA_TEEN.get()) {
            return 2;
        }
        else if (morph == MorphInit.MECHA_WARDEN.get()) {
            return 3;
        }
        else if (morph == MorphInit.MECHA_KING.get()) {
            return 4;
        }
        else if (morph == MorphInit.MECHA_SCULK.get()) {
            return 5;
        }
        return 1;
    }
}
