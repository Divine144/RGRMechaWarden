package com.divinity.hmedia.rgrmechawarden.item;

import com.divinity.hmedia.rgrmechawarden.cap.SkulkHolderAttacher;
import com.divinity.hmedia.rgrmechawarden.utils.MechaWardenUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class BlockMorphItem extends Item {

    public BlockMorphItem(Properties pProperties) {
        super(pProperties);
    }

    // TODO: this

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);
        if (pLevel.isClientSide) {
            return InteractionResultHolder.consume(itemStack);
        }
        var holder = SkulkHolderAttacher.getSkulkHolderUnwrap(pPlayer);
        if (holder != null) {
            if (!holder.isMechaMorphed()) {
                if (!pPlayer.isShiftKeyDown()) {
                    BlockHitResult result = MechaWardenUtils.blockTrace(pPlayer, ClipContext.Fluid.NONE, 10, false);
                    if (result != null) {
                        BlockState blockState = pLevel.getBlockState(result.getBlockPos());
                        if (!blockState.isAir()) {
                            holder.setCamouflagedBlock(blockState.getBlock());
                        }
                    }
                }
                else {
                    holder.setCamouflagedBlock(Blocks.AIR);
                }
            }
        }
        return InteractionResultHolder.consume(itemStack);
    }
}
