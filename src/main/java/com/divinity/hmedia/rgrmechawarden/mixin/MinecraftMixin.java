package com.divinity.hmedia.rgrmechawarden.mixin;

import com.divinity.hmedia.rgrmechawarden.init.EffectInit;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Inject(method = "shouldEntityAppearGlowing", at = @At("HEAD"), cancellable = true)
    public void rgrmechawarden$shouldEntityAppearGlowing_HEAD(Entity pEntity, CallbackInfoReturnable<Boolean> cir) {
        if (pEntity instanceof LivingEntity living && living.hasEffect(EffectInit.LOCK_ON.get())) {
            cir.setReturnValue(true);
        }
    }
}
