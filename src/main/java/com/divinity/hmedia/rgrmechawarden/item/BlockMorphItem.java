package com.divinity.hmedia.rgrmechawarden.item;

import com.divinity.hmedia.rgrmechawarden.cap.SkulkHolderAttacher;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class BlockMorphItem extends Item {

    public BlockMorphItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);
        if (pLevel.isClientSide) {
            return InteractionResultHolder.consume(itemStack);
        }
        var holder = SkulkHolderAttacher.getSkulkHolderUnwrap(pPlayer);
        CompoundTag tag = itemStack.getOrCreateTag();
        if (holder != null) {
            if (!holder.isMechaMorphed()) {
                if (!pPlayer.isShiftKeyDown()) {
                    Block block = Block.stateById(tag.getInt("block")).getBlock();
                    if (block != Blocks.AIR) {
                        holder.setCamouflagedBlock(block);
                        ((ServerPlayer) pPlayer).sendSystemMessage(Component.literal("Selected Block: ").append(block.getName()).withStyle(ChatFormatting.GREEN), true);
                    }
                    else {
                        ((ServerPlayer) pPlayer).sendSystemMessage(Component.literal("Invalid Block Selected").withStyle(ChatFormatting.RED), true);
                    }
                }
                else {
                    holder.setCamouflagedBlock(Blocks.AIR);
                    ((ServerPlayer) pPlayer).sendSystemMessage(Component.literal("Reset Morph").withStyle(ChatFormatting.GREEN), true);
                }
            }
        }
        return InteractionResultHolder.consume(itemStack);
    }
}
