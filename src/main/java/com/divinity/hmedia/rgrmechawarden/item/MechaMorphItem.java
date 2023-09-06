package com.divinity.hmedia.rgrmechawarden.item;

import com.divinity.hmedia.rgrmechawarden.cap.SkulkHolderAttacher;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class MechaMorphItem extends Item {

    public MechaMorphItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);
        if (pLevel.isClientSide) {
            return InteractionResultHolder.consume(itemStack);
        }
        var holder = SkulkHolderAttacher.getSkulkHolderUnwrap(pPlayer);
        if (holder != null) {
            if (!holder.isMechaMorphed()) {
                if (holder.removeSkulk(20)) {
                    holder.setMechaMorphed(true);
                    holder.setCamouflagedBlock(Blocks.AIR);
                }
            }
            else holder.setMechaMorphed(false);
        }
        return InteractionResultHolder.consume(itemStack);
    }
}
