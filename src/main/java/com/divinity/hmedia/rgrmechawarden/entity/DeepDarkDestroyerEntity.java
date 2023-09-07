package com.divinity.hmedia.rgrmechawarden.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.*;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class DeepDarkDestroyerEntity extends ThrowableProjectile implements GeoEntity {

    private static final EntityDataAccessor<Boolean> SHOULD_GROW = SynchedEntityData.defineId(DeepDarkDestroyerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> GROW_TICKS = SynchedEntityData.defineId(DeepDarkDestroyerEntity.class, EntityDataSerializers.INT);
    private static final int STOP_GROWING_TICK = 180;
    private static final float RADIUS_PER_TICK = 0.5f / 20f;
    private static final float MIN_REACH = 3.0f;
    private static final float REACH_MODIFIER = 1.33f;
    private static final float GRAVITY_STRENGTH = 0.075f;
    private final AnimatableInstanceCache instanceCache = GeckoLibUtil.createInstanceCache(this);
    private float radius = 0;

    public DeepDarkDestroyerEntity(EntityType<? extends ThrowableProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    // getDimensions

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            if (this.getOwner() != null && this.distanceTo(getOwner()) >= 50) {
                discard();
                return;
            }
            if (entityData.get(SHOULD_GROW)) {
                this.setDeltaMovement(Vec3.ZERO);
                if (getGrowTicks() < STOP_GROWING_TICK) {
                    setGrowTicks(getGrowTicks() + 1);

                    radius += RADIUS_PER_TICK;
                    double radiusSqr = radius * radius;
                    double pullRadius = (radius * REACH_MODIFIER) + MIN_REACH;
                    double pullDistance = radius - pullRadius;
                    double pullSize = pullRadius * 2;
                    double pullRadiusSqr = pullRadius * pullRadius;

                    // entity gravity
                    for (Entity entity : level().getEntities((Entity)null, AABB.ofSize(position(), pullSize, pullSize, pullSize), p -> p instanceof LivingEntity && p != getOwner())) {
                        Vec3 pullDirection = position().subtract(entity.position());
                        double lengthSqr = pullDirection.lengthSqr();
                        if (lengthSqr <= pullRadiusSqr) {
                            if (lengthSqr <= radiusSqr) {
                                if (entity instanceof LivingEntity living && !living.isDeadOrDying()) {
                                    if (!living.hasEffect(MobEffects.BLINDNESS)) {
                                        living.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20 * 3, 0));
                                    }
                                    living.hurt(damageSources().mobProjectile(this, getOwner() instanceof LivingEntity livingEntity ? livingEntity : null), 2);
                                }
                            }
                            else {
                                float magnitude = 1.5f - (float) ((pullDirection.length() - radius) / pullDistance);
                                double gravity = interpolate(0, GRAVITY_STRENGTH, magnitude);
                                Vec3 pull = pullDirection.normalize().scale(gravity);
                                entity.setDeltaMovement(entity.getDeltaMovement().add(pull));
                                entity.hurtMarked = true;
                            }
                        }
                    }

                    //blocks get sucked in (but let's not be too expensive)
                    if (getGrowTicks() % 20 == 0) {
                        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
                        for (int x = (int)-pullRadius; x < pullRadius; ++x) {
                            for (int y = (int)-pullRadius; y < pullRadius; ++y) {
                                for (int z = (int)-pullRadius; z < pullRadius; ++z) {
                                    mutableBlockPos.set(position().x + x, position().y + y, position().z + z);
                                    BlockState state = level().getBlockState(mutableBlockPos);
                                    if (!state.isAir() && level().getBlockEntity(mutableBlockPos) == null && mutableBlockPos.distToCenterSqr(position()) <= pullRadiusSqr) {
                                        if (!state.getFluidState().is(Fluids.EMPTY)) { //delete all fluids regardless of rng because water likes to fight it
                                            level().setBlockAndUpdate(mutableBlockPos, Blocks.AIR.defaultBlockState());
                                            continue;
                                        }
                                        if (level().random.nextFloat() > 0.5f) continue; //stagger the effect on normal blocks to make it look more organic
                                        if (level().random.nextFloat() > 0.5f) { //delete half of blocks to reduce lag from falling block entities
                                            level().setBlockAndUpdate(mutableBlockPos, Blocks.AIR.defaultBlockState());
                                            continue;
                                        }
                                        FallingBlockEntity block = FallingBlockEntity.fall(level(), mutableBlockPos, state);
                                        block.setNoGravity(true);
                                        block.noPhysics = true;
                                        block.dropItem = false;
                                        block.setDeltaMovement(position().subtract(block.position()).normalize().scale(0.5f));
                                    }
                                }
                            }
                        }
                    }

                    if (getGrowTicks() == STOP_GROWING_TICK) {
                        //clean up any noGravity on falling blocks that may have survived
                        double broomSize = (radius + radius + MIN_REACH + MIN_REACH) * REACH_MODIFIER;
                        for (FallingBlockEntity entity : level().getEntitiesOfClass(FallingBlockEntity.class, AABB.ofSize(position(), broomSize, broomSize, broomSize))) {
                            entity.setNoGravity(false);
                        }
                        radius = 0;
                        discard();
                    }
                }
            }
        }
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(SHOULD_GROW, false);
        this.entityData.define(GROW_TICKS, 0);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("shouldGrow", entityData.get(SHOULD_GROW));
        pCompound.putInt("growTicks", entityData.get(GROW_TICKS));
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        entityData.set(SHOULD_GROW, pCompound.getBoolean("shouldGrow"));
        entityData.set(GROW_TICKS, pCompound.getInt("growTicks"));
    }

    @Override
    protected void onHit(HitResult pResult) {
        super.onHit(pResult);
        if (!this.level().isClientSide) {
            if (!entityData.get(SHOULD_GROW)) {
                this.entityData.set(SHOULD_GROW, true);
            }
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return instanceCache;
    }

    public int getGrowTicks() {
        return entityData.get(GROW_TICKS);
    }

    public void setGrowTicks(int growTicks) {
        this.entityData.set(GROW_TICKS, growTicks);
    }

    private float interpolate(float start, float end, float scale) {
        return ((end - start) * scale) + start;
    }
}
