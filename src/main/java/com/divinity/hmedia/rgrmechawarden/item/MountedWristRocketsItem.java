package com.divinity.hmedia.rgrmechawarden.item;

import com.divinity.hmedia.rgrmechawarden.cap.SkulkHolderAttacher;
import com.divinity.hmedia.rgrmechawarden.entity.MissileEntity;
import com.divinity.hmedia.rgrmechawarden.init.EffectInit;
import com.divinity.hmedia.rgrmechawarden.init.EntityInit;
import com.divinity.hmedia.rgrmechawarden.utils.MechaWardenUtils;
import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
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
        return ItemUtils.startUsingInstantly(pLevel, pPlayer, pUsedHand);
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
                            living.addEffect(new MobEffectInstance(EffectInit.LOCK_ON.get(), -1, 0, false, false, false));
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
                if (entities.isEmpty()) {
/*                if (cap.removeSkulk(10)) {
                // TODO: Play sound
                pLevel.playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundInit.SHOULDER_MISSILE.get(), SoundSource.PLAYERS, 1, 1);

                ShoulderMissileEntity missile = new ShoulderMissileEntity(EntityInit.SHOULDER_MISSILE.get(), pLevel);
                missile.setPos(pPlayer.getX(), pPlayer.getEyeY(), pPlayer.getZ());
                missile.setOwner(pPlayer);
                missile.shootFromRotation(pPlayer, pPlayer.getXRot(), pPlayer.getYRot(), 0, 1.5F, 0);
                pLevel.addFreshEntity(missile);
            }*/
                }
                else {
                    for (LivingEntity entity : entities) {
                        if (cap.removeSkulk(10)) {
                            // TODO: Play sound
//                        pLevel.playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundInit.SHOULDER_MISSILE.get(), SoundSource.PLAYERS, 1, 1);

                            for (int j = 0; j < 2; j++) {
                                MissileEntity missile = new MissileEntity(EntityInit.MISSILE.get(), pLevel);
                                missile.setPos(pPlayer.getX(), pPlayer.getEyeY(), pPlayer.getZ());
                                missile.setOwner(pPlayer);
                                missile.setTarget(entity);
                                pLevel.addFreshEntity(missile);
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
}
