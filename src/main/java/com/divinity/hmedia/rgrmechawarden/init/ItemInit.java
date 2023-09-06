package com.divinity.hmedia.rgrmechawarden.init;

import com.divinity.hmedia.rgrmechawarden.RGRMechaWarden;
import com.divinity.hmedia.rgrmechawarden.item.BlockMorphItem;
import com.divinity.hmedia.rgrmechawarden.item.MechaMorphItem;
import com.divinity.hmedia.rgrmechawarden.item.MechaWardenLaserItem;
import com.divinity.hmedia.rgrmechawarden.item.MountedWristRocketsItem;
import dev._100media.hundredmediageckolib.item.animated.AnimatedItemProperties;
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


    public static Item.Properties getItemProperties() {
        return new Item.Properties();
    }
}
