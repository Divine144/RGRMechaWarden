package com.divinity.hmedia.rgrmechawarden.init;

import com.divinity.hmedia.rgrmechawarden.RGRMechaWarden;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemInit {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, RGRMechaWarden.MODID);

    public static Item.Properties getItemProperties() {
        return new Item.Properties();
    }
}
