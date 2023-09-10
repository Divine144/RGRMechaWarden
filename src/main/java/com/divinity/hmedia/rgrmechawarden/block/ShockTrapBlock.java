package com.divinity.hmedia.rgrmechawarden.block;

import com.divinity.hmedia.rgrmechawarden.block.be.ShockTrapBE;
import com.divinity.hmedia.rgrmechawarden.cap.SkulkHolderAttacher;
import com.divinity.hmedia.rgrmechawarden.init.EffectInit;
import com.divinity.hmedia.rgrmechawarden.init.SoundInit;
import dev._100media.hundredmediamorphs.capability.MorphHolderAttacher;
import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class ShockTrapBlock extends BaseEntityBlock {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    protected static final VoxelShape BOTTOM_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);

    public ShockTrapBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
        if (pEntity instanceof ServerPlayer player) {
            if (MorphHolderAttacher.getCurrentMorph(player).isPresent()) {
                SkulkHolderAttacher.getSkulkHolder(player).ifPresent(cap -> {
                    if (cap.getNettedInvulnTicks() <= 0) {
                        if (!player.hasEffect(EffectInit.NETTED.get())) {
                            player.addEffect(new MobEffectInstance(EffectInit.NETTED.get(), -1, 0, false, false, false));
                            player.sendSystemMessage(Component.literal("You are stuck in a trap! Press the Jump/Shift Keys to break free!").withStyle(ChatFormatting.RED), true);
                        }
                        if (player.getHealth() - 1.0F <= 0) {
                            if (!cap.isHasLost()) {
                                cap.setHasLost(true);
                                ((ServerLevel) pLevel).players().forEach(serverPlayer -> {
                                    serverPlayer.connection.send(new ClientboundSetTitleTextPacket(Component.literal("THE MECHA WARDEN HAS LOST").withStyle(ChatFormatting.RED, ChatFormatting.BOLD)));
                                });
                            }
                        }
                        else {
                            player.hurt(pLevel.damageSources().generic(), 1.0F);
                            if (player.tickCount % 20 == 0) {
                                player.level().playSound(null, player.blockPosition(), SoundInit.SHOCK_TRAP.get(), SoundSource.PLAYERS, 0.5f, 1.0f);
                            }
                        }
                    }
                });
            }
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return BOTTOM_AABB;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new ShockTrapBE(pPos, pState);
    }
}
