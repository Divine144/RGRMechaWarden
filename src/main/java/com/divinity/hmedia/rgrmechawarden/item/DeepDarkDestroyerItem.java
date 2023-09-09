package com.divinity.hmedia.rgrmechawarden.item;

import com.divinity.hmedia.rgrmechawarden.entity.DeepDarkDestroyerEntity;
import com.divinity.hmedia.rgrmechawarden.init.EntityInit;
import com.divinity.hmedia.rgrmechawarden.init.SoundInit;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class DeepDarkDestroyerItem extends Item {

    public DeepDarkDestroyerItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);
        if (pLevel.isClientSide) {
            return InteractionResultHolder.consume(itemStack);
        }
        DeepDarkDestroyerEntity entity = new DeepDarkDestroyerEntity(EntityInit.DEEP_DARK_DESTROYER.get(), pLevel);
        entity.setPos(pPlayer.getX(), pPlayer.getEyeY() - 0.55, pPlayer.getZ());
        entity.setOwner(pPlayer);
        entity.setNoGravity(true);
        entity.shootFromRotation(pPlayer, pPlayer.getXRot(), pPlayer.getYRot(), 0, 1.5F, 0);
        pPlayer.level().addFreshEntity(entity);
        pPlayer.getCooldowns().addCooldown(this, 20 * 15);
        pLevel.playSound(null, pPlayer.blockPosition(), SoundInit.DEEP_DARK_DESTROYER.get(), SoundSource.PLAYERS, 0.5f, 1.0f);
        return InteractionResultHolder.consume(itemStack);
    }
}
