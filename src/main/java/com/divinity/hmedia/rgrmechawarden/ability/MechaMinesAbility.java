package com.divinity.hmedia.rgrmechawarden.ability;

import com.divinity.hmedia.rgrmechawarden.cap.SkulkHolderAttacher;
import com.divinity.hmedia.rgrmechawarden.init.BlockInit;
import com.divinity.hmedia.rgrmechawarden.init.SoundInit;
import dev._100media.hundredmediaabilities.ability.Ability;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;

public class MechaMinesAbility extends Ability {

    @Override
    public void executePressed(ServerLevel level, ServerPlayer player) {
        var holder = SkulkHolderAttacher.getSkulkHolderUnwrap(player);
        if (holder != null) {
            if (holder.removeSkulk(10)) {
                level.setBlockAndUpdate(player.getOnPos().above(), BlockInit.MECHA_MINE_BLOCK.get().defaultBlockState());
                level.playSound(null, player.blockPosition(), SoundInit.MECHA_MINES.get(), SoundSource.PLAYERS, 0.5f, 1.0f);
            }
        }
        super.executePressed(level, player);
    }

    @Override
    public int getCooldownDuration() {
        return 20 * 3;
    }
}
