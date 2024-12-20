package com.divinity.hmedia.rgrmechawarden.init;

import com.divinity.hmedia.rgrmechawarden.RGRMechaWarden;
import com.divinity.hmedia.rgrmechawarden.ability.*;
import dev._100media.hundredmediaabilities.HundredMediaAbilitiesMod;
import dev._100media.hundredmediaabilities.ability.Ability;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class AbilityInit {

    public static final DeferredRegister<Ability> ABILITIES = DeferredRegister.create(new ResourceLocation(HundredMediaAbilitiesMod.MODID, "abilities"), RGRMechaWarden.MODID);

    public static final RegistryObject<Ability> LASER_TURRET_SHOOT = ABILITIES.register("laser_turret_shoot", ShootLaserTurretAbility::new);

    public static final RegistryObject<Ability> LASER_TURRET_MORPH = ABILITIES.register("laser_turret_morph", LaserTurretAbility::new);

    public static final RegistryObject<Ability> TESLA_COIL = ABILITIES.register("tesla_coil", TeslaCoilAbility::new);

    public static final RegistryObject<Ability> FUSION_CORE_REACTOR = ABILITIES.register("fusion_core_reactor", ReactorCoreEjectionAbility::new);

    public static final RegistryObject<Ability> SCULKY_MECHA_MINES = ABILITIES.register("sculky_mecha_mines", MechaMinesAbility::new);

    public static final RegistryObject<Ability> WARDEN_LASER = ABILITIES.register("warden_laser", MechaWardenLaserAbility::new);

}
