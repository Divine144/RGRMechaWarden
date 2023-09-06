package com.divinity.hmedia.rgrmechawarden.mixin;

import com.divinity.hmedia.rgrmechawarden.init.EffectInit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {

    @Inject(method = "getTeamColor", at = @At("HEAD"), cancellable = true)
    public void rgrmechawarden$getTeamColor_HEAD(CallbackInfoReturnable<Integer> cir) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof LivingEntity living && living.hasEffect(EffectInit.LOCK_ON.get())) {
            cir.setReturnValue(0xFFff1100);
        }
    }
}
