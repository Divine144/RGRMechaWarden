package com.divinity.hmedia.rgrmechawarden.entity;

import com.divinity.hmedia.rgrmechawarden.utils.MechaWardenUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class NukeEntity extends PrimedTnt implements GeoEntity {

    public static final int RADIUS = 60;
    private final AnimatableInstanceCache instanceCache = GeckoLibUtil.createInstanceCache(this);
    private LivingEntity goodOwner;
    private Vec3 targetPos = Vec3.ZERO;
    private boolean shouldGoToTargetPos;

    public NukeEntity(EntityType<? extends PrimedTnt> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

   public void setTargetPos(Vec3 targetPos) {
        this.targetPos = targetPos;
   }

    public void setGoodOwner(LivingEntity goodOwner) {
        this.goodOwner = goodOwner;
    }

    @Override
    public void tick() {
        if (this.onGround()) {
            if (getFuse() > 20 * 5) {
                setFuse(20 * 5);
            }
        }
        else {
            setFuse(20 * 6);
        }
        if (!this.level().isClientSide) {
            if (this.tickCount % 60 == 0) {
                shouldGoToTargetPos = true;
            }
            if (shouldGoToTargetPos) {
                if (!onGround()) {
                    MechaWardenUtils.pullEntityToPoint(this, targetPos, 3);
                }
            }
        }
        super.tick();
    }

    @Override
    protected void explode() {
        Explosion explosion = level().explode(goodOwner, getX(), getY(), getZ(), 20, Level.ExplosionInteraction.TNT);
        explosion.explode();
        explosion.finalizeExplosion(true);
        MechaWardenUtils.fancyExplosion(position(), level(), RADIUS, Blocks.AIR.defaultBlockState());
        List<Entity> entities = level().getEntities((Entity) null, getBoundingBox().inflate(50), e -> e instanceof Player);
        for (Entity entity : entities) {
            level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 1, 1);
        }
        for (double i = 0; i <= Math.PI / 2; i += Math.PI / 40) {
            double radius = Math.sin(i) * 10;
            double y = 16 + (Math.cos(i) * 8);
            for (double a = 0; a < Math.PI * 2; a += Math.PI / 80) {
                double x = Math.cos(a) * radius;
                double z = Math.sin(a) * radius;
                ((ServerLevel) level()).sendParticles(ParticleTypes.FLAME, getX() + x, getY() + y, getZ() + z, 1, 0, 0, 0, 0);
                ((ServerLevel) level()).sendParticles(ParticleTypes.LARGE_SMOKE, getX() + x, getY() + y, getZ() + z, 1, 0, 0, 0, 0);
            }
        }
        for (double y = 0; y <= 16; y+=0.5) {
            for (double a = 0; a < Math.PI * 2; a += Math.PI / 80) {
                double x = Math.cos(a) * 6;
                double z = Math.sin(a) * 6;
                ((ServerLevel) level()).sendParticles(ParticleTypes.FLAME, getX() + x, getY() + y, getZ() + z, 1, 0, 0, 0, 0);
                ((ServerLevel) level()).sendParticles(ParticleTypes.LARGE_SMOKE, getX() + x, getY() + y, getZ() + z, 1, 0, 0, 0, 0);
            }
        }
        if (goodOwner instanceof ServerPlayer player) {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20 * 40, 0));
            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20 * 40, 0));
            player.connection.send(new ClientboundSetTitleTextPacket(Component.literal("Reactor Refilling").withStyle(ChatFormatting.RED)));
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return instanceCache;
    }
}
