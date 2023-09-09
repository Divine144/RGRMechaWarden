package com.divinity.hmedia.rgrmechawarden.init;

import com.divinity.hmedia.rgrmechawarden.RGRMechaWarden;
import com.divinity.hmedia.rgrmechawarden.block.*;
import com.divinity.hmedia.rgrmechawarden.block.be.ChargingStationBE;
import com.divinity.hmedia.rgrmechawarden.block.be.MechaMineBE;
import com.divinity.hmedia.rgrmechawarden.block.be.ShockTrapBE;
import com.divinity.hmedia.rgrmechawarden.item.block.ChargingStationBlockItem;
import com.divinity.hmedia.rgrmechawarden.item.block.MechaMinesBlockItem;
import com.divinity.hmedia.rgrmechawarden.item.block.ShockTrapBlockItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
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

    public static final RegistryObject<Block> SHOCK_TRAP_BLOCK = registerShockTrap("shock_trap", () -> new ShockTrapBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F).noOcclusion().noCollission().isValidSpawn((a, b, c, d) -> false)));

    public static final RegistryObject<Block> CHARGING_STATION_BLOCK = registerChargingStation("charging_station", () -> new ChargingStationBlock(BlockBehaviour.Properties.of().mapColor(MapColor.RAW_IRON).strength(3F, 10.0F).noOcclusion().isValidSpawn((a, b, c, d) -> false)));

    public static final RegistryObject<Block> MECHA_MINE_BLOCK = registerMechaMines("sculky_mecha_mines", () -> new MechaMinesBlock(BlockBehaviour.Properties.of().mapColor(MapColor.RAW_IRON).strength(3F, 10.0F).noOcclusion().isValidSpawn((a, b, c, d) -> false)));

    public static final RegistryObject<Block> SPECIAL_SCULK_BLOCK = registerBlock("sculk", () -> new SpecialSculkBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).requiresCorrectToolForDrops().strength(0.2f).sound(SoundType.SCULK)));

    public static final RegistryObject<BlockEntityType<ShockTrapBE>> SHOCK_TRAP_BLOCK_ENTITY = BLOCK_ENTITIES.register("shock_trap_be", () -> BlockEntityType.Builder.of(ShockTrapBE::new, SHOCK_TRAP_BLOCK.get()).build(null));

    public static final RegistryObject<BlockEntityType<ChargingStationBE>> CHARGING_STATION_BLOCK_ENTITY = BLOCK_ENTITIES.register("charging_station_be", () -> BlockEntityType.Builder.of(ChargingStationBE::new, CHARGING_STATION_BLOCK.get()).build(null));

    public static final RegistryObject<BlockEntityType<MechaMineBE>> MECHA_MINES_BLOCK_ENTITY = BLOCK_ENTITIES.register("sculky_mecha_mines_be", () -> BlockEntityType.Builder.of(MechaMineBE::new, MECHA_MINE_BLOCK.get()).build(null));


    protected static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        return registerBlock(name, block, b -> () -> new BlockItem(b.get(), ItemInit.getItemProperties()));
    }

    protected static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block, Function<RegistryObject<T>, Supplier<? extends BlockItem>> item) {
        var reg = BLOCKS.register(name, block);
        ItemInit.ITEMS.register(name, () -> item.apply(reg).get());
        return reg;
    }

    protected static <T extends Block> RegistryObject<T> registerChargingStation(String name, Supplier<T> block) {
        return registerBlock(name, block, b -> () -> new ChargingStationBlockItem(b.get(), ItemInit.getItemProperties()));
    }

    protected static <T extends Block> RegistryObject<T> registerMechaMines(String name, Supplier<T> block) {
        return registerBlock(name, block, b -> () -> new MechaMinesBlockItem(b.get(), ItemInit.getItemProperties()));
    }

    protected static <T extends Block> RegistryObject<T> registerShockTrap(String name, Supplier<T> block) {
        return registerBlock(name, block, b -> () -> new ShockTrapBlockItem(b.get(), ItemInit.getItemProperties()));
    }
}
