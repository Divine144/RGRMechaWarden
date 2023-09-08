package com.divinity.hmedia.rgrmechawarden.init;

import com.divinity.hmedia.rgrmechawarden.RGRMechaWarden;
import com.divinity.hmedia.rgrmechawarden.marker.MechaBoardMarker;
import dev._100media.hundredmediaabilities.HundredMediaAbilitiesMod;
import dev._100media.hundredmediaabilities.capability.MarkerHolderAttacher;
import dev._100media.hundredmediaabilities.marker.Marker;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class MarkerInit {

    public static final DeferredRegister<Marker> MARKERS = DeferredRegister.create(new ResourceLocation(HundredMediaAbilitiesMod.MODID, "markers"), RGRMechaWarden.MODID);
    public static final RegistryObject<Marker> MECHA_BOARD_MARKER = MARKERS.register("mecha_board", MechaBoardMarker::new);
    public static final RegistryObject<Marker> SHOCK_TRAP = MARKERS.register("shock_trap", Marker::new);
}
