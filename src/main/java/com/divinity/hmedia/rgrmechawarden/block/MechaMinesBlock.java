package com.divinity.hmedia.rgrmechawarden.block;

import com.divinity.hmedia.rgrmechawarden.block.be.MechaMineBE;
import com.divinity.hmedia.rgrmechawarden.entity.NukeEntity;
import com.divinity.hmedia.rgrmechawarden.init.BlockInit;
import com.divinity.hmedia.rgrmechawarden.utils.MechaWardenUtils;
import dev._100media.hundredmediamorphs.capability.MorphHolderAttacher;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class MechaMinesBlock extends BaseEntityBlock {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    protected static final VoxelShape BOTTOM_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);

    public MechaMinesBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
        if (pEntity instanceof LivingEntity player && player.level() instanceof ServerLevel serverLevel) {
            if (MorphHolderAttacher.getCurrentMorph(player).isEmpty()) {
                Optional<ServerPlayer> warden = serverLevel.players().stream().filter(p -> MorphHolderAttacher.getCurrentMorph(p).isPresent()).findAny();
                Explosion explosion = serverLevel.explode(warden.orElse(null), pPos.getX(), pPos.getY(), pPos.getZ(), 4, Level.ExplosionInteraction.TNT);
                explosion.explode();
                explosion.finalizeExplosion(true);
                MechaWardenUtils.fancyExplosion(new Vec3(pPos.getX(), pPos.getY(), pPos.getZ()), pLevel, 4, BlockInit.SPECIAL_SCULK_BLOCK.get().defaultBlockState());
                if (pLevel.getBlockState(pPos).getBlock() instanceof MechaMinesBlock) {
                    pLevel.setBlockAndUpdate(pPos, Blocks.AIR.defaultBlockState());
                }
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
        return new MechaMineBE(pPos, pState);
    }
}
