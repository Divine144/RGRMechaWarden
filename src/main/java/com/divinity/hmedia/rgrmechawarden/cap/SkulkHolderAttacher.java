package com.divinity.hmedia.rgrmechawarden.cap;

import com.divinity.hmedia.rgrmechawarden.RGRMechaWarden;
import dev._100media.capabilitysyncer.core.CapabilityAttacher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RGRMechaWarden.MODID)
public class SkulkHolderAttacher extends CapabilityAttacher {
    public static final Capability<SkulkHolder> EXAMPLE_CAPABILITY = getCapability(new CapabilityToken<>() {});
    public static final ResourceLocation EXAMPLE_RL = new ResourceLocation(RGRMechaWarden.MODID, "skulk");
    private static final Class<SkulkHolder> CAPABILITY_CLASS = SkulkHolder.class;

    @SuppressWarnings("ConstantConditions")
    public static SkulkHolder getSkulkHolderUnwrap(Entity player) {
        return getSkulkHolder(player).orElse(null);
    }

    public static LazyOptional<SkulkHolder> getSkulkHolder(Entity player) {
        return player.getCapability(EXAMPLE_CAPABILITY);
    }

    private static void attach(AttachCapabilitiesEvent<Entity> event, Entity entity) {
        genericAttachCapability(event, new SkulkHolder(entity), EXAMPLE_CAPABILITY, EXAMPLE_RL);
    }

    public static void register() {
        CapabilityAttacher.registerCapability(CAPABILITY_CLASS);
        CapabilityAttacher.registerEntityAttacher(LivingEntity.class, SkulkHolderAttacher::attach, SkulkHolderAttacher::getSkulkHolder);
    }
}
