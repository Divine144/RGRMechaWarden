package com.divinity.hmedia.rgrmechawarden.ability;

import com.divinity.hmedia.rgrmechawarden.cap.SkulkHolderAttacher;
import com.divinity.hmedia.rgrmechawarden.utils.MechaWardenUtils;
import dev._100media.hundredmediaabilities.ability.Ability;
import dev._100media.hundredmediaabilities.capability.MarkerHolderAttacher;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

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
                        entity.hurt(level.damageSources().playerAttack(player), 2);
                        // TODO: Code for lightning effect going from tip of the coil to targeted entity
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
