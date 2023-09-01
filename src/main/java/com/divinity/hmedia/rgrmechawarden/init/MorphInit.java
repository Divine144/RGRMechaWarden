package com.divinity.hmedia.rgrmechawarden.init;

import com.divinity.hmedia.rgrmechawarden.RGRMechaWarden;
import com.divinity.hmedia.rgrmechawarden.cap.SkulkHolderAttacher;
import dev._100media.hundredmediamorphs.HundredMediaMorphsMod;
import dev._100media.hundredmediamorphs.morph.Morph;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class MorphInit {
    public static final DeferredRegister<Morph> MORPHS = DeferredRegister.create(new ResourceLocation(HundredMediaMorphsMod.MODID, "morphs"), RGRMechaWarden.MODID);

    public static final RegistryObject<Morph> BABY_MECHA = MORPHS.register("baby_mecha", () -> new Morph(new Morph.Properties<>()
            .maxHealth(10)
            .dimensions(0.65f, 0.65f)
            .eyeHeight(0.5f)
            .morphedTo(entity -> {
                SkulkHolderAttacher.getSkulkHolder(entity).ifPresent(p -> {
                    p.setSkulkCap(40);
                    p.setSkulkRegen(p.getBaseSkulkRegen());
                });
            })
            .demorph(entity -> {

            })
    ));
    public static final RegistryObject<Morph> MECHA_TEEN = MORPHS.register("mecha_teen", () -> new Morph(new Morph.Properties<>()
            .maxHealth(20)
            .dimensions(1f, 1f)
            .morphedTo(entity -> {
                SkulkHolderAttacher.getSkulkHolder(entity).ifPresent(p -> {
                    p.setSkulkCap(70);
                    p.setSkulkRegen(p.getBaseSkulkRegen() + 1);
                });
                entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, -1, 0, false, false, false));
                entity.addEffect(new MobEffectInstance(MobEffects.JUMP, -1, 1, false, false, false));
            })
            .demorph(entity -> {
                entity.removeEffect(MobEffects.DAMAGE_BOOST);
                entity.removeEffect(MobEffects.JUMP);
            })
    ));
    public static final RegistryObject<Morph> MECHA_WARDEN = MORPHS.register("mecha_warden", () -> new Morph(new Morph.Properties<>()
            .maxHealth(40)
            .dimensions(1.5f, 3f)
            .morphedTo(entity -> {
                SkulkHolderAttacher.getSkulkHolder(entity).ifPresent(p -> {
                    p.setSkulkCap(100);
                    p.setSkulkRegen(p.getBaseSkulkRegen() + 2);
                });
                entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, -1, 1, false, false, false));
                entity.addEffect(new MobEffectInstance(MobEffects.JUMP, -1, 3, false, false, false));
                var reachDistance = entity.getAttribute(ForgeMod.BLOCK_REACH.get());
                var attackDistance = entity.getAttribute(ForgeMod.ENTITY_REACH.get());
                if (reachDistance != null && attackDistance != null) {
                    reachDistance.setBaseValue(reachDistance.getAttribute().getDefaultValue() + 2);
                    attackDistance.setBaseValue(attackDistance.getAttribute().getDefaultValue() + 2);
                }
            })
            .demorph(entity -> {
                entity.removeEffect(MobEffects.DAMAGE_BOOST);
                entity.removeEffect(MobEffects.JUMP);
                var reachDistance = entity.getAttribute(ForgeMod.BLOCK_REACH.get());
                var attackDistance = entity.getAttribute(ForgeMod.ENTITY_REACH.get());
                if (reachDistance != null && attackDistance != null) {
                    reachDistance.setBaseValue(reachDistance.getAttribute().getDefaultValue());
                    attackDistance.setBaseValue(attackDistance.getAttribute().getDefaultValue());
                }
            })
    ));
    public static final RegistryObject<Morph> MECHA_KING = MORPHS.register("mecha_king", () -> new Morph(new Morph.Properties<>()
            .maxHealth(60)
            .dimensions(1.5f, 3f)
            .morphedTo(entity -> {
                SkulkHolderAttacher.getSkulkHolder(entity).ifPresent(p -> {
                    p.setSkulkCap(150);
                    p.setSkulkRegen(p.getBaseSkulkRegen() + 3);
                });
                entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, -1, 2, false, false, false));
                entity.addEffect(new MobEffectInstance(MobEffects.JUMP, -1, 3, false, false, false));
                var reachDistance = entity.getAttribute(ForgeMod.BLOCK_REACH.get());
                var attackDistance = entity.getAttribute(ForgeMod.ENTITY_REACH.get());
                if (reachDistance != null && attackDistance != null) {
                    reachDistance.setBaseValue(reachDistance.getAttribute().getDefaultValue() + 3);
                    attackDistance.setBaseValue(attackDistance.getAttribute().getDefaultValue() + 3);
                }
            })
            .demorph(entity -> {
                entity.removeEffect(MobEffects.DAMAGE_BOOST);
                entity.removeEffect(MobEffects.JUMP);
                var reachDistance = entity.getAttribute(ForgeMod.BLOCK_REACH.get());
                var attackDistance = entity.getAttribute(ForgeMod.ENTITY_REACH.get());
                if (reachDistance != null && attackDistance != null) {
                    reachDistance.setBaseValue(reachDistance.getAttribute().getDefaultValue());
                    attackDistance.setBaseValue(attackDistance.getAttribute().getDefaultValue());
                }
            })
    ));
    public static final RegistryObject<Morph> MECHA_SCULK = MORPHS.register("mecha_sculk", () -> new Morph(new Morph.Properties<>()
            .maxHealth(100)
            .dimensions(2f, 4f)
            .morphedTo(entity -> {
                SkulkHolderAttacher.getSkulkHolder(entity).ifPresent(p -> {
                    p.setSkulkCap(200);
                    p.setSkulkRegen(p.getBaseSkulkRegen() + 4);
                });
                entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, -1, 4, false, false, false));
                var reachDistance = entity.getAttribute(ForgeMod.BLOCK_REACH.get());
                var attackDistance = entity.getAttribute(ForgeMod.ENTITY_REACH.get());
                if (reachDistance != null && attackDistance != null) {
                    reachDistance.setBaseValue(reachDistance.getAttribute().getDefaultValue() + 5);
                    attackDistance.setBaseValue(attackDistance.getAttribute().getDefaultValue() + 5);
                }
            })
            .demorph(entity -> {
                entity.removeEffect(MobEffects.DAMAGE_BOOST);
                var reachDistance = entity.getAttribute(ForgeMod.BLOCK_REACH.get());
                var attackDistance = entity.getAttribute(ForgeMod.ENTITY_REACH.get());
                if (reachDistance != null && attackDistance != null) {
                    reachDistance.setBaseValue(reachDistance.getAttribute().getDefaultValue());
                    attackDistance.setBaseValue(attackDistance.getAttribute().getDefaultValue());
                }
            })
    ));
}
