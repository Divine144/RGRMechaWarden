package com.divinity.hmedia.rgrmechawarden.entity;

import com.google.common.collect.Sets;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class NukeEntity extends PrimedTnt implements GeoEntity {

    public static final int RADIUS = 60;
    private final AnimatableInstanceCache instanceCache = GeckoLibUtil.createInstanceCache(this);
    private LivingEntity goodOwner;

    public NukeEntity(EntityType<? extends PrimedTnt> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
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
        super.tick();
    }

    @Override
    protected void explode() {
        Explosion explosion = level().explode(goodOwner, getX(), getY(), getZ(), 10, Level.ExplosionInteraction.TNT);
        explosion.explode();
        explosion.finalizeExplosion(true);
        fancyExplosion(position(), level(), RADIUS);
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

    public static void fancyExplosion(Vec3 center, Level level, int radius) {
        Set<BlockPos> set = Sets.newHashSet();
        ExplosionDamageCalculator damageCalculator = new ExplosionDamageCalculator();
        for(int j = 0; j < 20; ++j) {
            for(int k = 0; k < 20; ++k) {
                for(int l = 0; l < 20; ++l) {
                    if (j == 0 || j == 15 || k == 0 || k == 15 || l == 0 || l == 15) {
                        double d0 = (float)j / 15.0F * 2.0F - 1.0F;
                        double d1 = (float)k / 15.0F * 2.0F - 1.0F;
                        double d2 = (float)l / 15.0F * 2.0F - 1.0F;
                        double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                        d0 /= d3;
                        d1 /= d3;
                        d2 /= d3;
                        float f = radius * (0.7F + level.random.nextFloat() * 0.6F);
                        double d4 = center.x;
                        double d6 = center.y;
                        double d8 = center.z;
                        for(; f > 0.0F; f -= 0.22500001F) {
                            BlockPos blockpos = new BlockPos((int) d4, (int) d6, (int) d8);
                            BlockState blockstate = level.getBlockState(blockpos);
                            FluidState fluidstate = level.getFluidState(blockpos);
                            if (!level.isInWorldBounds(blockpos)) {
                                break;
                            }
                            Optional<Float> optional = damageCalculator.getBlockExplosionResistance(null, level, blockpos, blockstate, fluidstate);
                            if (optional.isPresent()) {
                                f -= (optional.get() + 0.3F) * 0.3F;
                            }
                            if (f > 0.0F && damageCalculator.shouldBlockExplode(null, level, blockpos, blockstate, f)) {
                                set.add(blockpos);
                            }
                            d4 += d0 * (double)0.3F;
                            d6 += d1 * (double)0.3F;
                            d8 += d2 * (double)0.3F;
                        }
                    }
                }
            }
        }
        for (BlockPos pos : set) {
            BlockState state = level.getBlockState(pos);
            if (state.isAir())
                continue;
            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
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
