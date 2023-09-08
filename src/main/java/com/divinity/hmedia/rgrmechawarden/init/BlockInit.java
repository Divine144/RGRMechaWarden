package com.divinity.hmedia.rgrmechawarden.init;

import com.divinity.hmedia.rgrmechawarden.RGRMechaWarden;
import com.divinity.hmedia.rgrmechawarden.block.ChargingStationBlock;
import com.divinity.hmedia.rgrmechawarden.block.HundredMediaBlock;
import com.divinity.hmedia.rgrmechawarden.block.ShockTrapBlock;
import com.divinity.hmedia.rgrmechawarden.block.be.ChargingStationBE;
import com.divinity.hmedia.rgrmechawarden.block.be.ShockTrapBE;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;
import java.util.function.Supplier;

public class BlockInit {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, RGRMechaWarden.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, RGRMechaWarden.MODID);

    public static final RegistryObject<HundredMediaBlock> HUNDRED_MEDIA = registerBlock("hundred_media", () -> new HundredMediaBlock(BlockBehaviour.Properties.of().mapColor(DyeColor.RED).noOcclusion().strength(1)));

    public static final RegistryObject<Block> SHOCK_TRAP_BLOCK = registerBlock("shock_trap", () -> new ShockTrapBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F).noOcclusion().noCollission().isValidSpawn((a, b, c, d) -> false)));

    public static final RegistryObject<Block> CHARGING_STATION_BLOCK = registerBlock("charging_station", () -> new ChargingStationBlock(BlockBehaviour.Properties.of().mapColor(MapColor.RAW_IRON).strength(3F, 10.0F).noOcclusion().isValidSpawn((a, b, c, d) -> false)));

    public static final RegistryObject<BlockEntityType<ShockTrapBE>> SHOCK_TRAP_BLOCK_ENTITY = BLOCK_ENTITIES.register("shock_trap_be", () -> BlockEntityType.Builder.of(ShockTrapBE::new, SHOCK_TRAP_BLOCK.get()).build(null));

    public static final RegistryObject<BlockEntityType<ChargingStationBE>> CHARGING_STATION_BLOCK_ENTITY = BLOCK_ENTITIES.register("charging_station_be", () -> BlockEntityType.Builder.of(ChargingStationBE::new, CHARGING_STATION_BLOCK.get()).build(null));

    protected static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        return registerBlock(name, block, b -> () -> new BlockItem(b.get(), ItemInit.getItemProperties()));
    }

    protected static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block, Function<RegistryObject<T>, Supplier<? extends BlockItem>> item) {
        var reg = BLOCKS.register(name, block);
        ItemInit.ITEMS.register(name, () -> item.apply(reg).get());
        return reg;
    }
}
