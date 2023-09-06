package com.divinity.hmedia.rgrmechawarden.entity;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class LaserEntity extends ThrowableProjectile implements GeoEntity {

    private final AnimatableInstanceCache instanceCache = GeckoLibUtil.createInstanceCache(this);
    private final int damage;

    public LaserEntity(EntityType<? extends ThrowableProjectile> pEntityType, Level pLevel) {
        this(pEntityType, pLevel, 0);
    }

    public LaserEntity(EntityType<? extends ThrowableProjectile> pEntityType, Level pLevel, int damage) {
        super(pEntityType, pLevel);
        this.damage = damage;
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide) {
            if (this.getOwner() != null && this.distanceTo(getOwner()) >= 25) {
                discard();
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        super.onHitBlock(pResult);
        if (!level().isClientSide) {
            discard();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        if (pResult.getEntity() != this.getOwner() && pResult.getEntity() instanceof LivingEntity living) {
            if (!level().isClientSide) {
                living.hurt(this.damageSources().mobProjectile(this, getOwner() instanceof LivingEntity l ? l : null), damage);
            }
        }
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return instanceCache;
    }
}
