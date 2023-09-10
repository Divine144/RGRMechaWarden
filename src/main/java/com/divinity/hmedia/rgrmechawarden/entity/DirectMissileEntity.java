package com.divinity.hmedia.rgrmechawarden.entity;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class DirectMissileEntity extends ThrowableProjectile implements GeoEntity {

    private final AnimatableInstanceCache instanceCache = GeckoLibUtil.createInstanceCache(this);
    private static final RawAnimation FLY = RawAnimation.begin().thenLoop("fly");

    public DirectMissileEntity(EntityType<? extends ThrowableProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public void tick() {
        super.tick();
        updateRotation();
    }

    @Override
    protected void onHit(HitResult pResult) {
        super.onHit(pResult);
        if (!level().isClientSide) {
            level().explode(getOwner(), getX(), getY(), getZ(), 3, Level.ExplosionInteraction.TNT);
            discard();
        }
    }

    @Override
    protected void defineSynchedData() {

    }


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controllerDirect", 0, event -> event.setAndContinue(FLY)));
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return instanceCache;
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
