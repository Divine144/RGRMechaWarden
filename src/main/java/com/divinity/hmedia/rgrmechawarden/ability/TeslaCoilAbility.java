package com.divinity.hmedia.rgrmechawarden.ability;

import com.divinity.hmedia.rgrmechawarden.cap.SkulkHolderAttacher;
import com.divinity.hmedia.rgrmechawarden.init.SoundInit;
import com.divinity.hmedia.rgrmechawarden.utils.MechaWardenUtils;
import dev._100media.hundredmediaabilities.ability.Ability;
import net.minecraft.core.particles.VibrationParticleOption;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class TeslaCoilAbility extends Ability {

    @Override
    public void executeHeld(ServerLevel level, ServerPlayer player, int tick) {
        super.executeHeld(level, player, tick);
        var holder = SkulkHolderAttacher.getSkulkHolderUnwrap(player);
        if (holder != null) {
            if (holder.getSkulk() >= 5) {
                if (tick % 20 == 0) {
                    Optional<LivingEntity> optional = MechaWardenUtils
                                                      .getEntitiesInRange(player, LivingEntity.class, 20, 20, 20, p -> p != player)
                                                      .stream().findFirst();
                    if (optional.isPresent()) {
                        LivingEntity entity = optional.get();
                        level.playSound(null, player.blockPosition(), SoundInit.TESLA_COIL.get(), SoundSource.PLAYERS, 0.5f, 1.0f);
                        entity.hurt(level.damageSources().playerAttack(player), 2);
                        Vec3 origin = player.getEyePosition(1.0f);
                        Vec3 traceVector = entity.position().subtract(origin);
                        Vec3 direction = traceVector.normalize();
                        BlockPositionSource source = new BlockPositionSource(entity.blockPosition());
                        for (float i = 0.1f; i < Mth.floor(traceVector.length()) + 15; ++i) {
                            Vec3 particlePosition = origin.add(direction.scale(i));
                            player.serverLevel().sendParticles(new VibrationParticleOption(source, 30), particlePosition.x, particlePosition.y, particlePosition.z, 10, 0, 0, 0, 0);
                        }
                    }
                    holder.removeSkulk(5);
                }
            }
            else super.executePressed(level, player);
        }
    }

    @Override
    public boolean isToggleAbility() {
        return true;
    }
}
