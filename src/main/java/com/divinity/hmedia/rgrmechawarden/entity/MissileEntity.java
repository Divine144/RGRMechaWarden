package com.divinity.hmedia.rgrmechawarden.entity;

import net.minecraft.client.particle.SonicBoomParticle;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class MissileEntity extends Entity implements GeoEntity {

    private static final EntityDataAccessor<Integer> DATA_TARGET = SynchedEntityData.defineId(MissileEntity.class, EntityDataSerializers.INT);
    private final AnimatableInstanceCache instanceCache = GeckoLibUtil.createInstanceCache(this);
    private LivingEntity target;
    private LivingEntity owner;
    private boolean antiAir = false;

    public MissileEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.noPhysics = true;
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && target == null) {
            discard();
            return;
        }

        if (!level().isClientSide && (target.distanceToSqr(this) <= 2 * 2 || (antiAir && !level().getBlockState(blockPosition()).isAir()))) {
            level().explode(owner, getX(), getY(), getZ(), 2, Level.ExplosionInteraction.NONE);
            discard();
            return;
        }

        if (target != null) {
            Vec3 vec3 = target.position().subtract(position()).normalize().scale(0.5);
            setDeltaMovement(vec3);
            double d2 = this.getX() + vec3.x;
            double d0 = this.getY() + vec3.y;
            double d1 = this.getZ() + vec3.z;
            this.setPos(d2, d0, d1);
            updateRotation();
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return instanceCache;
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(DATA_TARGET, -1);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {

    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
        super.onSyncedDataUpdated(pKey);
        if (pKey == DATA_TARGET) {
            target = (LivingEntity) level().getEntity(entityData.get(DATA_TARGET));
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    public void setTarget(LivingEntity target) {
        this.target = target;
        entityData.set(DATA_TARGET, target.getId());
    }

    public LivingEntity getTarget() {
        return target;
    }

    public void setOwner(LivingEntity owner) {
        this.owner = owner;
    }

    public void setAntiAir(boolean antiAir) {
        this.antiAir = antiAir;
    }

    protected void updateRotation() {
        Vec3 vec3 = this.getDeltaMovement();
        double d0 = vec3.horizontalDistance();
        this.setXRot(lerpRotation(this.xRotO, (float)(Mth.atan2(vec3.y, d0) * (double)(180F / (float)Math.PI))));
        this.setYRot(lerpRotation(this.yRotO, (float)(Mth.atan2(vec3.x, vec3.z) * (double)(180F / (float)Math.PI))));
    }

    protected static float lerpRotation(float pCurrentRotation, float pTargetRotation) {
        while(pTargetRotation - pCurrentRotation < -180.0F) {
            pCurrentRotation -= 360.0F;
        }

        while(pTargetRotation - pCurrentRotation >= 180.0F) {
            pCurrentRotation += 360.0F;
        }

        return Mth.lerp(0.2F, pCurrentRotation, pTargetRotation);
    }
}
