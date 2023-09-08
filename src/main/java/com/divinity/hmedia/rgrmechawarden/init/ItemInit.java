package com.divinity.hmedia.rgrmechawarden.init;

import com.divinity.hmedia.rgrmechawarden.RGRMechaWarden;
import com.divinity.hmedia.rgrmechawarden.item.*;
import dev._100media.hundredmediageckolib.item.animated.AnimatedItemProperties;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemInit {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, RGRMechaWarden.MODID);

    public static final RegistryObject<Item> MOUNTED_WRIST_ROCKETS = ITEMS.register("mounted_wrist_rockets", () -> new MountedWristRocketsItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> MECHA_WARDEN_LASER = ITEMS.register("mecha_warden_laser", () -> new MechaWardenLaserItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> MECHA_MORPH = ITEMS.register("mecha_morph", () -> new MechaMorphItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> BLOCK_MORPH = ITEMS.register("block_morph", () -> new BlockMorphItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> SCULKY_SNACK = ITEMS.register("sculky_snack", () -> new SculkySnackItem(new Item.Properties().stacksTo(1).food(
            new FoodProperties.Builder()
                    .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 20 * 5, 1), 1.0F)
                    .nutrition(10).saturationMod(0.8F).alwaysEat().build())));

    public static final RegistryObject<Item> MECHO_LOCATION = ITEMS.register("mecho_location", () -> new MechoLocationItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> DEEP_DARK_DESTROYER = ITEMS.register("deep_dark_destroyer", () -> new DeepDarkDestroyerItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> MEGA_MAGNET = ITEMS.register("mega_magnet", () -> new MegaMagnetItem(new Item.Properties().stacksTo(1).durability(10)));

    public static final RegistryObject<Item> DISRUPTOR_RAY = ITEMS.register("disruptor_ray", () -> new DisruptorRayItem(new Item.Properties().stacksTo(1).durability(10)));

    public static Item.Properties getItemProperties() {
        return new Item.Properties();
    }
}
