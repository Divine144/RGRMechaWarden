package com.divinity.hmedia.rgrmechawarden.datagen;

import com.divinity.hmedia.rgrmechawarden.RGRMechaWarden;
import com.divinity.hmedia.rgrmechawarden.init.SoundInit;
import net.minecraft.data.PackOutput;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SoundDefinition;
import net.minecraftforge.common.data.SoundDefinitionsProvider;
import net.minecraftforge.registries.RegistryObject;

public class ModSoundProvider extends SoundDefinitionsProvider {
    public ModSoundProvider(PackOutput generator, ExistingFileHelper helper) {
        super(generator, RGRMechaWarden.MODID, helper);
    }

    @Override
    public void registerSounds() {
        SoundInit.SOUNDS.getEntries().forEach(this::addSound);
    }

    public void addSound(RegistryObject<SoundEvent> entry) {
        add(entry, SoundDefinition.definition().with(sound(entry.getId())));
    }
}
