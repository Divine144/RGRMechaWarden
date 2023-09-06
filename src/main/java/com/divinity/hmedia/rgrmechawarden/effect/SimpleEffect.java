package com.divinity.hmedia.rgrmechawarden.effect;


import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class SimpleEffect extends MobEffect {

    public SimpleEffect() {
        super(MobEffectCategory.NEUTRAL, 0);
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return false;
    }

}
