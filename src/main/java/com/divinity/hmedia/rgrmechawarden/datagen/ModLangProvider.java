package com.divinity.hmedia.rgrmechawarden.datagen;

import com.divinity.hmedia.rgrmechawarden.RGRMechaWarden;
import com.divinity.hmedia.rgrmechawarden.init.*;
import com.google.common.collect.ImmutableMap;
import dev._100media.hundredmediaabilities.ability.Ability;
import dev._100media.hundredmediaabilities.marker.Marker;
import dev._100media.hundredmediamorphs.morph.Morph;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class ModLangProvider extends LanguageProvider {
    protected static final Map<String, String> REPLACE_LIST = ImmutableMap.of(
            "tnt", "TNT",
            "sus", ""
    );

    public ModLangProvider(PackOutput gen) {
        super(gen, RGRMechaWarden.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        EntityInit.ENTITIES.getEntries().forEach(this::entityLang);
        ItemInit.ITEMS.getEntries().forEach(this::itemLang);
        BlockInit.BLOCKS.getEntries().forEach(this::blockLang);
        AbilityInit.ABILITIES.getEntries().forEach(this::abilityLang);
        MarkerInit.MARKERS.getEntries().forEach(this::markerLang);
        MorphInit.MORPHS.getEntries().forEach(this::morphLang);
        add("itemGroup.hundredMediaTab", "100 Media");
        add("key.rgrmechawarden.skill_tree", "Open Skill Tree");
        add("key.category.rgrmechawarden", "RGRMechaWarden");
    }

    protected void itemLang(RegistryObject<Item> entry) {
        if (!(entry.get() instanceof BlockItem) || entry.get() instanceof ItemNameBlockItem) {
            addItem(entry, checkReplace(entry));
        }
    }

    protected void blockLang(RegistryObject<Block> entry) {
        addBlock(entry, checkReplace(entry));
    }

    protected void entityLang(RegistryObject<EntityType<?>> entry) {
        addEntityType(entry, checkReplace(entry));
    }

    protected void abilityLang(RegistryObject<Ability> entry) {
        add(entry.get().getDescriptionId(), checkReplace(entry));
    }

    protected void markerLang(RegistryObject<Marker> entry) {
        add(entry.get().getDescriptionId(), checkReplace(entry));
    }

    protected void morphLang(RegistryObject<Morph> entry) {
        add(entry.get().getDescriptionId(), checkReplace(entry));
    }

    protected String checkReplace(RegistryObject<?> registryObject) {
        return Arrays.stream(registryObject.getId().getPath().split("_"))
                .map(this::checkReplace)
                .filter(s -> !s.isBlank())
                .collect(Collectors.joining(" "))
                .trim();
    }

    protected String checkReplace(String string) {
        return REPLACE_LIST.containsKey(string) ? REPLACE_LIST.get(string) : StringUtils.capitalize(string);
    }
}
