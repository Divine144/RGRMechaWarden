package com.divinity.hmedia.rgrmechawarden.ability;

import com.divinity.hmedia.rgrmechawarden.cap.SkulkHolderAttacher;
import com.divinity.hmedia.rgrmechawarden.entity.LaserEntity;
import com.divinity.hmedia.rgrmechawarden.init.EntityInit;
import com.divinity.hmedia.rgrmechawarden.init.MorphInit;
import com.divinity.hmedia.rgrmechawarden.init.SoundInit;
import dev._100media.hundredmediaabilities.ability.Ability;
import dev._100media.hundredmediamorphs.capability.MorphHolderAttacher;
import dev._100media.hundredmediamorphs.morph.Morph;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.tuple.Pair;

public class ShootLaserTurretAbility extends Ability {

    @Override
    public void executeHeld(ServerLevel level, ServerPlayer player, int tick) {
        var holder = SkulkHolderAttacher.getSkulkHolderUnwrap(player);
        if (holder != null) {
            if (holder.isMechaMorphed()) {
                var pair = this.getDamageOutputPair(MorphHolderAttacher.getCurrentMorphUnwrap(player));
                if (tick % (20 / pair.getRight()) == 0) {
                    LaserEntity entity = new LaserEntity(EntityInit.LASER.get(), level, pair.getLeft());
                    entity.setPos(player.getX(), player.getEyeY() - 0.15, player.getZ());
                    entity.setOwner(player);
                    entity.setNoGravity(true);
                    entity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, 1.5F, 0);
                    entity.setYRot(-Mth.wrapDegrees(player.getYRot()));
                    entity.setXRot(-Mth.wrapDegrees(player.getXRot()));
                    entity.xRotO = -Mth.wrapDegrees(player.xRotO);
                    entity.yRotO = -Mth.wrapDegrees(player.yRotO);
                    player.level().addFreshEntity(entity);
                    player.serverLevel().playSound(null, player.blockPosition(), SoundInit.LASER.get(), SoundSource.PLAYERS, 0.5f, 1.0f);
                }
            }
        }
    }

    @Override
    public boolean isHeldAbility() {
        return true;
    }

    private Pair<Integer, Integer> getDamageOutputPair(Morph morph) {
        if (morph != null) {
            if (morph == MorphInit.BABY_MECHA.get() || morph == MorphInit.MECHA_TEEN.get() || morph == MorphInit.MECHA_WARDEN.get()) {
                return Pair.of(6, 1);
            }
            else if(morph == MorphInit.MECHA_KING.get()) {
                return Pair.of(8, 1);
            }
            else return Pair.of(10, 2);
        }
        return Pair.of(0, 1);
    }
}
