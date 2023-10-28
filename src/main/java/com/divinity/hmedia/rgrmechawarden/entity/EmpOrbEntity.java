package com.divinity.hmedia.rgrmechawarden.entity;

import com.divinity.hmedia.rgrmechawarden.cap.SkulkHolderAttacher;
import com.divinity.hmedia.rgrmechawarden.init.AbilityInit;
import com.divinity.hmedia.rgrmechawarden.init.ItemInit;
import com.divinity.hmedia.rgrmechawarden.init.SoundInit;
import dev._100media.hundredmediaabilities.capability.AbilityHolderAttacher;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class EmpOrbEntity extends Projectile implements GeoEntity {
    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);

    public EmpOrbEntity(EntityType<? extends EmpOrbEntity> entityType, Level level) {
        super(entityType, level);
        this.setNoGravity(true);
    }

    public EmpOrbEntity(EntityType<? extends EmpOrbEntity> entityType, double x, double y, double z, Level level) {
        this(entityType, level);
        this.setPos(x, y, z);
    }

    public EmpOrbEntity(EntityType<? extends EmpOrbEntity> entityType, LivingEntity shooter, Level level) {
        this(entityType, shooter.getX(), shooter.getEyeY() - 0.1D, shooter.getZ(), level);
        this.setOwner(shooter);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide && this.getY() > this.level().getMaxBuildHeight() + 128) {
            this.discard();

            return;
        }

        HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        boolean flag = false;
        if (hitresult.getType() == HitResult.Type.BLOCK) {
            BlockPos blockpos = ((BlockHitResult) hitresult).getBlockPos();
            BlockState blockstate = this.level().getBlockState(blockpos);
            if (blockstate.is(Blocks.NETHER_PORTAL)) {
                this.handleInsidePortal(blockpos);
                flag = true;
            } else if (blockstate.is(Blocks.END_GATEWAY)) {
                BlockEntity blockentity = this.level().getBlockEntity(blockpos);
                if (blockentity instanceof TheEndGatewayBlockEntity && TheEndGatewayBlockEntity.canEntityTeleport(this)) {
                    TheEndGatewayBlockEntity.teleportEntity(this.level(), blockpos, blockstate, this, (TheEndGatewayBlockEntity) blockentity);
                }

                flag = true;
            }
        }

        if (hitresult.getType() != HitResult.Type.MISS && !flag && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, hitresult)) {
            this.onHit(hitresult);
        }

        this.checkInsideBlocks();
        Vec3 vec3 = this.getDeltaMovement();
        double d2 = this.getX() + vec3.x;
        double d0 = this.getY() + vec3.y;
        double d1 = this.getZ() + vec3.z;
        this.updateRotation();
        // float f;
        // if (this.isInWater()) {
        //     for (int i = 0; i < 4; ++i) {
        //         float f1 = 0.25F;
        //         this.level().addParticle(ParticleTypes.BUBBLE, d2 - vec3.x * 0.25D, d0 - vec3.y * 0.25D, d1 - vec3.z * 0.25D, vec3.x, vec3.y, vec3.z);
        //     }
        //
        //     f = 0.8F;
        // } else {
        //     f = 0.99F;
        // }
        //
        // this.setDeltaMovement(vec3.scale((double) f));
        // if (!this.isNoGravity()) {
        //     Vec3 vec31 = this.getDeltaMovement();
        //     this.setDeltaMovement(vec31.x, vec31.y - (double) this.getGravity(), vec31.z);
        // }

        this.setPos(d2, d0, d1);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (!this.level().isClientSide) {
            Entity target = result.getEntity();
            if (target instanceof ServerPlayer player) {
                player.sendSystemMessage(Component.literal("You got hit by an EMP Blast!").withStyle(ChatFormatting.RED), true);
                player.getCooldowns().addCooldown(ItemInit.DEEP_DARK_DESTROYER.get(), 20 * 5);
                player.getCooldowns().addCooldown(ItemInit.MECHA_WARDEN_LASER.get(), 20 * 5);
                player.getCooldowns().addCooldown(ItemInit.MECHO_LOCATION.get(), 20 * 5);
                player.getCooldowns().addCooldown(ItemInit.MOUNTED_WRIST_ROCKETS.get(), 20 * 5);
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20 * 5, 1));
                AbilityHolderAttacher.getAbilityHolder(player).ifPresent(p -> {
                    p.addCooldown(AbilityInit.SCULKY_MECHA_MINES.get(), true);
                    p.addCooldown(AbilityInit.LASER_TURRET_MORPH.get(), true);
                    p.addCooldown(AbilityInit.WARDEN_LASER.get(), true);
                });

                SkulkHolderAttacher.getSkulkHolder(player).ifPresent(p -> p.setCoolDownsReduced(true));
            }
            target.hurt(this.level().damageSources().indirectMagic(this, this.getOwner()), 1F);
            this.level().playSound(null, this.blockPosition(), SoundInit.SHOCK_TRAP.get(), SoundSource.PLAYERS, 0.5f, 1.0f);
            this.discard();
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!this.level().isClientSide)
            this.discard();
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public void setNoGravity(boolean noGravity) {
        super.setNoGravity(true);
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.animationCache;
    }
}
