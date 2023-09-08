package com.divinity.hmedia.rgrmechawarden.init;

import com.divinity.hmedia.rgrmechawarden.RGRMechaWarden;
import com.divinity.hmedia.rgrmechawarden.effect.NettedEffect;
import com.divinity.hmedia.rgrmechawarden.effect.SimpleEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EffectInit {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, RGRMechaWarden.MODID);

    public static final RegistryObject<MobEffect> LOCK_ON = EFFECTS.register("lock_on", SimpleEffect::new);

    public static final RegistryObject<MobEffect> NETTED = EFFECTS.register("netted", () -> new NettedEffect(MobEffectCategory.HARMFUL, 0xFF964B00));
}
