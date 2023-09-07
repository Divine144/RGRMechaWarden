package com.divinity.hmedia.rgrmechawarden.init;

import com.divinity.hmedia.rgrmechawarden.RGRMechaWarden;
import com.divinity.hmedia.rgrmechawarden.ability.ReactorCoreEjectionAbility;
import com.divinity.hmedia.rgrmechawarden.ability.ShootLaserTurretAbility;
import com.divinity.hmedia.rgrmechawarden.ability.TeslaCoilAbility;
import dev._100media.hundredmediaabilities.HundredMediaAbilitiesMod;
import dev._100media.hundredmediaabilities.ability.Ability;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class AbilityInit {

    public static final DeferredRegister<Ability> ABILITIES = DeferredRegister.create(new ResourceLocation(HundredMediaAbilitiesMod.MODID, "abilities"), RGRMechaWarden.MODID);

    public static final RegistryObject<Ability> LASER_TURRET_SHOOT = ABILITIES.register("laser_turret_shoot", ShootLaserTurretAbility::new);

    public static final RegistryObject<Ability> TESLA_COIL = ABILITIES.register("tesla_coil", TeslaCoilAbility::new);

    public static final RegistryObject<Ability> FUSION_CORE_REACTOR = ABILITIES.register("fusion_core_reactor", ReactorCoreEjectionAbility::new);

    public static final RegistryObject<Ability> ANT_ARMY = ABILITIES.register("ant_army", Ability::new);

    public static final RegistryObject<Ability> SIZE_UP = ABILITIES.register("size_up", Ability::new);

    public static final RegistryObject<Ability> SIZE_DOWN = ABILITIES.register("size_down", Ability::new);

    public static final RegistryObject<Ability> CAMOUFLAGE = ABILITIES.register("camouflage", Ability::new);

    public static final RegistryObject<Ability> SWARM_SHIELD = ABILITIES.register("swarm_shield", Ability::new);

    public static final RegistryObject<Ability> GIGA_ANT = ABILITIES.register("giga_ant", Ability::new);

}
