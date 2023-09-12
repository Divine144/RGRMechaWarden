package com.divinity.hmedia.rgrmechawarden.item;

import com.divinity.hmedia.rgrmechawarden.cap.SkulkHolderAttacher;
import com.divinity.hmedia.rgrmechawarden.entity.DirectMissileEntity;
import com.divinity.hmedia.rgrmechawarden.entity.MissileEntity;
import com.divinity.hmedia.rgrmechawarden.init.EffectInit;
import com.divinity.hmedia.rgrmechawarden.init.EntityInit;
import com.divinity.hmedia.rgrmechawarden.init.SoundInit;
import com.divinity.hmedia.rgrmechawarden.utils.MechaWardenUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class MountedWristRocketsItem extends Item {

    public MountedWristRocketsItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);
        if (pLevel.isClientSide) {
            return InteractionResultHolder.pass(itemStack);
        }
        CompoundTag tag = itemStack.getOrCreateTag();
        boolean isNormal = tag.getBoolean("normal");
        if (pPlayer.isShiftKeyDown()) {
            tag.putBoolean("normal", !isNormal);
            ((ServerPlayer) pPlayer).sendSystemMessage(Component.literal("Mode Switched To: %s".formatted(tag.getBoolean("normal") ? "Normal" : "Lock On"))
                    .withStyle(ChatFormatting.GREEN), true);
        }
        else {
            if (isNormal) {
                SkulkHolderAttacher.getSkulkHolder(pPlayer).ifPresent(cap -> {
                    if (cap.removeSkulk(10)) {
                        pLevel.playSound(null, pPlayer.blockPosition(), SoundInit.WRIST_ROCKETS.get(), SoundSource.PLAYERS, 0.5f, 1.0f);
                        DirectMissileEntity missile = new DirectMissileEntity(EntityInit.DIRECT_MISSILE.get(), pLevel);
                        missile.setPos(pPlayer.getX(), pPlayer.getEyeY(), pPlayer.getZ());
                        missile.setOwner(pPlayer);
                        missile.shootFromRotation(pPlayer, pPlayer.getXRot(), pPlayer.getYRot(), 0, 1.5f, 0);
                        pLevel.addFreshEntity(missile);
                        pPlayer.getCooldowns().addCooldown(this, 20 * 2);
                    }
                });
            }
            else return ItemUtils.startUsingInstantly(pLevel, pPlayer, pUsedHand);
        }
        return InteractionResultHolder.pass(itemStack);
    }

    @Override
    public void onUseTick(Level pLevel, LivingEntity pLivingEntity, ItemStack pStack, int pRemainingUseDuration) {
        if (!pLevel.isClientSide) {
            if (pLivingEntity instanceof ServerPlayer player) {
                if (pRemainingUseDuration % 5 == 0) {
                    EntityHitResult result = MechaWardenUtils.rayTraceEntities(player.serverLevel(), player, 30, p -> p != player);
                    if (result != null) {
                        Entity entity = result.getEntity();
                        if (entity instanceof LivingEntity living && !living.hasEffect(EffectInit.LOCK_ON.get())) {
                            MobEffectInstance instance = new MobEffectInstance(EffectInit.LOCK_ON.get(), 20 * 30, 0, false, false, false);
                            living.addEffect(instance);
                            ((ServerLevel) pLevel).getChunkSource().broadcast(living, new ClientboundUpdateMobEffectPacket(living.getId(), instance));
                        }
                    }
                }
            }
        }
        super.onUseTick(pLevel, pLivingEntity, pStack, pRemainingUseDuration);
    }

    @Override
    public void releaseUsing(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity, int pTimeCharged) {
        if (pLivingEntity instanceof ServerPlayer pPlayer) {
            int i = this.getUseDuration(pStack) - pTimeCharged;
            if (i < 0) return;
            List<LivingEntity> entities = pLevel.getEntitiesOfClass(LivingEntity.class, pPlayer.getBoundingBox().inflate(30), e -> e.hasEffect(EffectInit.LOCK_ON.get()));
            SkulkHolderAttacher.getSkulkHolder(pPlayer).ifPresent(cap -> {
                if (!entities.isEmpty()) {
                    for (LivingEntity entity : entities) {
                        if (cap.removeSkulk(10)) {
                            for (int j = 0; j < 2; j++) {
                                MissileEntity missile = new MissileEntity(EntityInit.MISSILE.get(), pLevel);
                                missile.setPos(pPlayer.position().offsetRandom(pPlayer.getRandom(), 0.7F));
                                missile.setOwner(pPlayer);
                                missile.setTarget(entity);
                                pLevel.addFreshEntity(missile);
                                pLevel.playSound(null, pPlayer.blockPosition(), SoundInit.WRIST_ROCKETS.get(), SoundSource.PLAYERS, 0.5f, 1.0f);
                            }
                        }
                        entity.removeEffect(EffectInit.LOCK_ON.get());
                        ((ServerLevel) pLevel).getChunkSource().broadcast(entity, new ClientboundRemoveMobEffectPacket(entity.getId(), EffectInit.LOCK_ON.get()));
                    }
                }
            });
            pPlayer.awardStat(Stats.ITEM_USED.get(this));
            pPlayer.getCooldowns().addCooldown(this, 20 * 6);
        }
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 72000;
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return false;
    }

    public void shootFromRotation(Entity entity, Entity pShooter, float pX, float pY, float pZ, float pVelocity, float pInaccuracy) {
        float f = -Mth.sin(pY * ((float)Math.PI / 180F)) * Mth.cos(pX * ((float)Math.PI / 180F));
        float f1 = -Mth.sin((pX + pZ) * ((float)Math.PI / 180F));
        float f2 = Mth.cos(pY * ((float)Math.PI / 180F)) * Mth.cos(pX * ((float)Math.PI / 180F));
        this.shoot(entity, f, f1, f2, pVelocity, pInaccuracy);
        Vec3 vec3 = pShooter.getDeltaMovement();
        entity.setDeltaMovement(entity.getDeltaMovement().add(vec3.x, pShooter.onGround() ? 0.0D : vec3.y, vec3.z));
    }

    public void shoot(Entity entity, double pX, double pY, double pZ, float pVelocity, float pInaccuracy) {
        Vec3 vec3 = (new Vec3(pX, pY, pZ)).normalize().add(entity.level().random.triangle(0.0D, 0.0172275D * (double)pInaccuracy), entity.level().random.triangle(0.0D, 0.0172275D * (double)pInaccuracy), entity.level().random.triangle(0.0D, 0.0172275D * (double)pInaccuracy)).scale((double)pVelocity);
        entity.setDeltaMovement(vec3);
        double d0 = vec3.horizontalDistance();
        entity.setYRot((float)(Mth.atan2(vec3.x, vec3.z) * (double)(180F / (float)Math.PI)));
        entity.setXRot((float)(Mth.atan2(vec3.y, d0) * (double)(180F / (float)Math.PI)));
        entity.yRotO = entity.getYRot();
        entity.xRotO = entity.getXRot();
    }
}
