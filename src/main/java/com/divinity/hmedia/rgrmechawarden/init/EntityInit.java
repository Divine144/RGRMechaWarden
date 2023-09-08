package com.divinity.hmedia.rgrmechawarden.init;

import com.divinity.hmedia.rgrmechawarden.RGRMechaWarden;
import com.divinity.hmedia.rgrmechawarden.entity.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = RGRMechaWarden.MODID)
public class EntityInit {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, RGRMechaWarden.MODID);
    private static final List<AttributesRegister<?>> attributeSuppliers = new ArrayList<>();

    public static final RegistryObject<EntityType<MissileEntity>> MISSILE = registerEntity("missile", () ->
            EntityType.Builder.of(MissileEntity::new, MobCategory.MISC).sized(0.5F, 0.5F));

    public static final RegistryObject<EntityType<LaserEntity>> LASER = registerEntity("laser", () ->
            EntityType.Builder.<LaserEntity>of(LaserEntity::new, MobCategory.MISC).sized(0.5F, 0.5F));

    public static final RegistryObject<EntityType<EmpOrbEntity>> EMP_ORB = registerEntity("emp_orb", () ->
            EntityType.Builder.<EmpOrbEntity>of(EmpOrbEntity::new, MobCategory.MISC).sized(0.5F, 0.5F));

    public static final RegistryObject<EntityType<NukeEntity>> NUKE = registerEntity("nuke", () ->
            EntityType.Builder.of(NukeEntity::new, MobCategory.MISC).fireImmune().sized(0.98F, 0.98F)
                    .clientTrackingRange(10).updateInterval(10));

    public static final RegistryObject<EntityType<DeepDarkDestroyerEntity>> DEEP_DARK_DESTROYER = registerEntity("deep_dark_destroyer", () ->
            EntityType.Builder.of(DeepDarkDestroyerEntity::new, MobCategory.MISC).fireImmune().sized(1F, 1F));

    private static <T extends Entity> RegistryObject<EntityType<T>> registerEntity(String name, Supplier<EntityType.Builder<T>> supplier) {
        return ENTITIES.register(name, () -> supplier.get().build(RGRMechaWarden.MODID + ":" + name));
    }

    private static <T extends LivingEntity> RegistryObject<EntityType<T>> registerEntity(String name, Supplier<EntityType.Builder<T>> supplier,
            Supplier<AttributeSupplier.Builder> attributeSupplier) {
        RegistryObject<EntityType<T>> entityTypeSupplier = registerEntity(name, supplier);
        attributeSuppliers.add(new AttributesRegister<>(entityTypeSupplier, attributeSupplier));
        return entityTypeSupplier;
    }

    @SubscribeEvent
    public static void attribs(EntityAttributeCreationEvent e) {
        attributeSuppliers.forEach(p -> e.put(p.entityTypeSupplier.get(), p.factory.get().build()));
    }

    private record AttributesRegister<E extends LivingEntity>(Supplier<EntityType<E>> entityTypeSupplier, Supplier<AttributeSupplier.Builder> factory) {}
}
