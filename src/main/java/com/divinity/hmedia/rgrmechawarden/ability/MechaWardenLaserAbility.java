package com.divinity.hmedia.rgrmechawarden.ability;

import com.divinity.hmedia.rgrmechawarden.cap.SkulkHolderAttacher;
import com.divinity.hmedia.rgrmechawarden.init.SoundInit;
import com.divinity.hmedia.rgrmechawarden.utils.MechaWardenUtils;
import dev._100media.hundredmediaabilities.ability.Ability;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class MechaWardenLaserAbility extends Ability {

    @Override
    public void executeHeld(ServerLevel level, ServerPlayer player, int tick) {
        var holder = SkulkHolderAttacher.getSkulkHolderUnwrap(player);
        if (holder != null) {
            int cost = 12;
            if (holder.getSkulk() >= cost || holder.isInfinite()) {
                if (tick % 3 == 0) {
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
                if (tick % 20 == 0) {
                    level.playSound(null, player.blockPosition(), SoundInit.WARDEN_LASER.get(), SoundSource.PLAYERS, 0.5f, 1.0f);
                    holder.removeSkulk(cost);
                    MechaWardenUtils.rayTraceEntitiesWithAction(level, player, 50, entity -> {
                        if (entity instanceof LivingEntity target) {
                            player.serverLevel().playSound(null, player.blockPosition(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 1.0f, 1.0f);
                            target.level().playSound(null, target.blockPosition(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 1.0f, 1.0f);
                            target.hurt(level.damageSources().indirectMagic(player, player), 12.0F);
                        }
                    }, false);
                }
            }
            else {
                this.addCooldown(level, player, CooldownType.PRESSED);
            }
        }
        super.executeHeld(level, player, tick);
    }

    @Override
    public int getCooldownDuration(Level level, Player player) {
        return 20 * 10;
    }

    @Override
    public boolean isHeldAbility() {
        return true;
    }
}
