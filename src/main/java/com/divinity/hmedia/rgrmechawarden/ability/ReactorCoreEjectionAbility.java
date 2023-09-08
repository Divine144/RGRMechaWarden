package com.divinity.hmedia.rgrmechawarden.ability;

import com.divinity.hmedia.rgrmechawarden.cap.SkulkHolderAttacher;
import com.divinity.hmedia.rgrmechawarden.entity.NukeEntity;
import com.divinity.hmedia.rgrmechawarden.init.EntityInit;
import dev._100media.hundredmediaabilities.ability.Ability;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class ReactorCoreEjectionAbility extends Ability {

    @Override
    public void executePressed(ServerLevel level, ServerPlayer player) {
        super.executePressed(level, player);
        SkulkHolderAttacher.getSkulkHolder(player).ifPresent(cap -> {
            if (cap.removeSkulk(80)) {
                NukeEntity nuke = new NukeEntity(EntityInit.NUKE.get(), level);
                nuke.setPos(player.getX(), player.getY(), player.getZ());
                nuke.setGoodOwner(player);
                this.shootFromRotation(nuke, player, -65, player.getYRot(), 0.0F, 2.5F, 0F);
                level.addFreshEntity(nuke);
            }
        });
    }

/*    @Override
    public int getRemainingCooldownDuration(Player player) {
        int i = super.getRemainingCooldownDuration(player);
        if (i == 0) {
            var holder = SkulkHolderAttacher.getSkulkHolderUnwrap(player);
            if (holder != null) {
                if (holder.isCoolDownsReduced()) {
                    return 20 * 5;
                }
            }
        }
        return i;
    }*/

    @Override
    public int getCooldownDuration() {
        return 20 * 45;
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
