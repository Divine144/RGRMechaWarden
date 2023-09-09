package com.divinity.hmedia.rgrmechawarden.item;

import com.divinity.hmedia.rgrmechawarden.cap.SkulkHolderAttacher;
import com.divinity.hmedia.rgrmechawarden.entity.MissileEntity;
import com.divinity.hmedia.rgrmechawarden.init.EffectInit;
import com.divinity.hmedia.rgrmechawarden.init.EntityInit;
import com.divinity.hmedia.rgrmechawarden.init.SoundInit;
import com.divinity.hmedia.rgrmechawarden.quest.goal.KillPlayersLaserGoal;
import com.divinity.hmedia.rgrmechawarden.utils.MechaWardenUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
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

public class MechaWardenLaserItem extends Item {

    public MechaWardenLaserItem(Properties pProperties) {
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
                var holder = SkulkHolderAttacher.getSkulkHolderUnwrap(player);
                if (holder != null) {
                    int cost = 12;
                    if (holder.getSkulk() >= cost) {
                        if (pRemainingUseDuration % 3 == 0) {
                            MechaWardenUtils.scanHitWithFollowup(player, 50, false, hitResult -> {
                                Vec3 origin = player.position().add(0.0D, 1.6F, 0.0D);
                                Vec3 traceVector = hitResult.getLocation().subtract(origin);
                                Vec3 direction = traceVector.normalize();

                                for (int i = 1; i < Mth.floor(traceVector.length()) + 7; ++i) {
                                    Vec3 particlePosition = origin.add(direction.scale(i));
                                    player.serverLevel().sendParticles(player, ParticleTypes.SONIC_BOOM, true, particlePosition.x, particlePosition.y, particlePosition.z, 1, 0, 0, 0, 0);
                                }
                            });
                        }
                        // TODO: Add sound
                        if (pRemainingUseDuration % 20 == 0) {
                            pLevel.playSound(null, player.blockPosition(), SoundInit.WARDEN_LASER.get(), SoundSource.PLAYERS, 0.5f, 1.0f);
                            holder.removeSkulk(cost);
                            MechaWardenUtils.rayTraceEntitiesWithAction(pLevel, player, 50, entity -> {
                                if (entity instanceof LivingEntity target) {
                                    player.serverLevel().playSound(null, player.blockPosition(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 1.0f, 1.0f);
                                    target.level().playSound(null, target.blockPosition(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 1.0f, 1.0f);
                                    target.hurt(pLevel.damageSources().sonicBoom(player), 6.0F);
                                    if (target.isDeadOrDying()) {
                                        MechaWardenUtils.addToGenericQuestGoal(player, KillPlayersLaserGoal.class);
                                    }
                                }
                            }, false);
                        }
                    }
                    else {
                        player.stopUsingItem();
                        player.getCooldowns().addCooldown(this, 20 * 10);
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
            pPlayer.awardStat(Stats.ITEM_USED.get(this));
            pPlayer.getCooldowns().addCooldown(this, 20 * 10);
        }
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.NONE;
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
