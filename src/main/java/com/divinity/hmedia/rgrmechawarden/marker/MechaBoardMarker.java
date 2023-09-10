package com.divinity.hmedia.rgrmechawarden.marker;

import com.divinity.hmedia.rgrmechawarden.cap.SkulkHolderAttacher;
import com.divinity.hmedia.rgrmechawarden.init.MorphInit;
import com.divinity.hmedia.rgrmechawarden.utils.MechaWardenUtils;
import dev._100media.hundredmediaabilities.marker.defaults.FlightMarker;
import dev._100media.hundredmediamorphs.capability.MorphHolderAttacher;
import dev._100media.hundredmediamorphs.morph.Morph;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

public class MechaBoardMarker extends FlightMarker {

    private static final float DEFAULT_FLYING_SPEED = 0.05F;
    private boolean hasTouchedGround = false;

    @Override
    public void onTick(Level level, Player player, int stackCount) {
        // We have to run this every tick to account for switching from creative/spectator to survival mode
        super.onTick(level, player, stackCount);
        Morph currentMorph = MorphHolderAttacher.getCurrentMorphUnwrap(player);
        float flyingSpeed = getFlyingSpeedForMorph(currentMorph);
        var holder = SkulkHolderAttacher.getSkulkHolderUnwrap(player);
        if (player instanceof ServerPlayer serverPlayer) {
            if (serverPlayer.getAbilities().flying && !serverPlayer.isCreative() && !serverPlayer.isSpectator()) {
                if (holder != null ) {
                    if (!holder.isMechaBoard()) {
                        holder.setMechaBoard(true);
                    }
                }
            }
        }
        if (flyingSpeed != 0 && holder != null && holder.getSkulk() >= getSkulkCostForEvo(currentMorph)) {
            if (!hasTouchedGround) {
                if (holder.isCoolDownsReduced()) {
                    hasTouchedGround = player.onGround() || player.isInWater();
                    if (hasTouchedGround) holder.setCoolDownsReduced(false);
                    else {
                        this.disableFlight(level, player);
                        return;
                    }
                }
                else hasTouchedGround = false;
            }
            if (player.getAbilities().flying) {
                BlockHitResult result = MechaWardenUtils.blockTrace(player, ClipContext.Fluid.NONE, 100, true);
                if (result != null) {
                    float distanceAboveGround = Mth.abs((float) player.getY() - (float) result.getLocation().y);
                    if (currentMorph == MorphInit.BABY_MECHA.get()) {
                        if (distanceAboveGround >= 3) {
                            this.disableFlight(level, player);
                            return;
                        }
                    }
                    else if (currentMorph == MorphInit.MECHA_TEEN.get()) {
                        if (distanceAboveGround >= 5) {
                            this.disableFlight(level, player);
                            return;
                        }
                    }
                    else if (currentMorph == MorphInit.MECHA_WARDEN.get()) {
                        if (distanceAboveGround >= 7) {
                            this.disableFlight(level, player);
                            return;
                        }
                    }
                }
                if (player.tickCount % 20 == 0) {
                    if (!player.level().isClientSide) {
                        holder.removeSkulk(getSkulkCostForEvo(currentMorph));
                    }
                }
                if (player.getAbilities().getFlyingSpeed() != flyingSpeed) {
                    player.getAbilities().setFlyingSpeed(flyingSpeed);
                }
            }
        }
        else this.disableFlight(level, player);
    }

    @Override
    public void onDisable(Level level, Player player, int stackCount) {
        super.onDisable(level, player, stackCount);
        this.disableFlight(level, player);
    }

    public void disableFlight(Level level, Player player) {
        var holder = SkulkHolderAttacher.getSkulkHolderUnwrap(player);
        if (player instanceof ServerPlayer serverPlayer) {
            if (serverPlayer.getAbilities().flying) {
                if (holder != null ) {
                    holder.setMechaBoard(false);
                }
            }
        }
        if (!player.isCreative() && !player.isSpectator()) {
            player.getAbilities().flying = false;
        }
        if (player.getAbilities().getFlyingSpeed() != DEFAULT_FLYING_SPEED) {
            player.getAbilities().setFlyingSpeed(DEFAULT_FLYING_SPEED);
        }
    }

    private float getFlyingSpeedForMorph(Morph morph) {
        if (morph == MorphInit.BABY_MECHA.get()) {
            return 0.0325f;
        }
        else if (morph == MorphInit.MECHA_TEEN.get()) {
            return 0.0433f;
        }
        else if (morph == MorphInit.MECHA_WARDEN.get()) {
            return DEFAULT_FLYING_SPEED;
        }
        else if (morph == MorphInit.MECHA_KING.get()) {
            return 0.065f;
        }
        else if (morph == MorphInit.MECHA_SCULK.get()) {
            return 0.081f;
        }
        return 0;
    }

    private int getSkulkCostForEvo(Morph morph) {
        if (morph == MorphInit.BABY_MECHA.get()) {
            return 3;
        }
        else if (morph == MorphInit.MECHA_TEEN.get()) {
            return 4;
        }
        else if (morph == MorphInit.MECHA_WARDEN.get()) {
            return 5;
        }
        else if (morph == MorphInit.MECHA_KING.get()) {
            return 6;
        }
        else if (morph == MorphInit.MECHA_SCULK.get()) {
            return 7;
        }
        return 0;
    }
}
