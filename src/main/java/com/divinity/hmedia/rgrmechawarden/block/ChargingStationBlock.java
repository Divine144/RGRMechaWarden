package com.divinity.hmedia.rgrmechawarden.block;

import com.divinity.hmedia.rgrmechawarden.block.be.ChargingStationBE;
import com.divinity.hmedia.rgrmechawarden.init.BlockInit;
import com.divinity.hmedia.rgrmechawarden.init.ItemInit;
import com.divinity.hmedia.rgrmechawarden.network.NetworkHandler;
import com.divinity.hmedia.rgrmechawarden.network.clientbound.UpdateChargingStationPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public class ChargingStationBlock extends BaseEntityBlock {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public ChargingStationBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pLevel.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        else {
            ItemStack stack = pPlayer.getItemInHand(pHand);
            if (pLevel.getBlockEntity(pPos) instanceof ChargingStationBE be) {
                if (pPlayer.isShiftKeyDown()) {
                    if (!be.getStoredStack().isEmpty()) {
                        pPlayer.getInventory().add(be.getStoredStack().copy());
                        be.setStoredStack(ItemStack.EMPTY);
                        be.setEmpty(true);
                        be.setCharged(false);
                        NetworkHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new UpdateChargingStationPacket(pPos, be.isEmpty(), be.isCharged()));
                    }
                }
                else if (stack.is(ItemInit.MEGA_MAGNET.get())) {
                    if (!be.getStoredStack().isEmpty()) {
                        if (be.hasFinishedCharging()) {
                            pPlayer.getInventory().add(be.getStoredStack().copy());
                            be.setStoredStack(stack.copy());
                            pPlayer.getInventory().removeItem(stack);
                            be.setEmpty(false);
                            be.setCharged(be.hasFinishedCharging());
                            NetworkHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new UpdateChargingStationPacket(pPos, be.isEmpty(), be.isCharged()));
                        }
                        else ((ServerPlayer) pPlayer).sendSystemMessage(Component.literal("Has not finished charging yet!").withStyle(ChatFormatting.RED), true);
                    }
                    else {
                        be.setStoredStack(stack.copy());
                        pPlayer.getInventory().removeItem(stack);
                        be.setEmpty(false);
                        be.setCharged(be.hasFinishedCharging());
                        NetworkHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new UpdateChargingStationPacket(pPos, be.isEmpty(), be.isCharged()));
                    }
                }
            }
            return InteractionResult.CONSUME;
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new ChargingStationBE(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return !pLevel.isClientSide ? createTickerHelper(pBlockEntityType, BlockInit.CHARGING_STATION_BLOCK_ENTITY.get(), this::tick) : null;
    }

    private void tick(Level level, BlockPos blockPos, BlockState state, BlockEntity blockEntity) {
        if (!level.isClientSide) {
            BlockEntity entity = level.getBlockEntity(blockPos);
            if (entity instanceof ChargingStationBE chargingStationBE) {
                if (level.getDayTime() % 20 == 0) {
                    if (!chargingStationBE.hasFinishedCharging()) {
                        chargingStationBE.doCharge();
                    }
                }
            }
        }
    }
}
