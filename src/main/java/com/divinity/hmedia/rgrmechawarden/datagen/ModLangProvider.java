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

        // Quest Descriptions
        add("quest.goal.rgrmechawarden.sound_of_music_advancement_goal.description", "Earn the Advancement \"Sound of Music\"");
        add("quest.goal.rgrmechawarden.voluntary_exile_advancement_goal.description", "Earn the Advancement \"Voluntary Exile\"");
        add("quest.goal.rgrmechawarden.kill_bat_bow_goal.description", "Kill Bats with a Bow 5 Times");
        add("quest.goal.rgrmechawarden.break_block_mecha_mines_goal.description", "Break 500 Blocks with Sculky Mecha Mines");
        add("quest.goal.rgrmechawarden.kill_players_wrist_rockets_goal.description", "Kill 3 Warden Hunters with your Wrist Rockets");
        add("quest.goal.rgrmechawarden.dolphin_grace_effect_goal.description", "Gain the Dolphin's Grace Effect");
        add("quest.goal.rgrmechawarden.trade_with_villager_goal.description", "Trade with a Villager");
        add("quest.goal.rgrmechawarden.kill_warden_hunters_goal.description", "Kill 2 Warden Hunters");
        add("quest.goal.rgrmechawarden.bring_home_the_beacon_advancement_goal.description", "Earn the Advancement \"Bring Home the Beacon\"");
        add("quest.goal.rgrmechawarden.damage_players_mecha_morph_goal.description", "Deal 200 Damage to Warden Hunters while Mechamorphed");
        add("quest.goal.rgrmechawarden.zombie_doctor_advancement_goal.description", "Earn the Advancement \"Zombie Doctor\"");
        add("quest.goal.rgrmechawarden.mine_spawner_nether_goal.description", "Mine 2 Spawners in the Nether");
        add("quest.goal.rgrmechawarden.kill_players_laser_goal.description", "Kill 4 Warden Hunters with your Laser");

        // Quest Display Descriptions
        add("quest.goal.rgrmechawarden.sound_of_music_advancement_goal", "Earn the Advancement \"Sound of Music\"");
        add("quest.goal.rgrmechawarden.voluntary_exile_advancement_goal", "Earn the Advancement \"Voluntary Exile\"");
        add("quest.goal.rgrmechawarden.kill_bat_bow_goal", "Kill Bats with a Bow");
        add("quest.goal.rgrmechawarden.break_block_mecha_mines_goal", "Break Blocks with Sculky Mecha Mines");
        add("quest.goal.rgrmechawarden.kill_players_wrist_rockets_goal", "Kill Warden Hunters with your Wrist Rockets");
        add("quest.goal.rgrmechawarden.dolphin_grace_effect_goal", "Gain the Dolphin's Grace Effect");
        add("quest.goal.rgrmechawarden.trade_with_villager_goal", "Trade with a Villager");
        add("quest.goal.rgrmechawarden.kill_warden_hunters_goal", "Kill Warden Hunters");
        add("quest.goal.rgrmechawarden.bring_home_the_beacon_advancement_goal", "Earn the Advancement \"Bring Home the Beacon\"");
        add("quest.goal.rgrmechawarden.damage_players_mecha_morph_goal", "Deal Damage to Warden Hunters while Mechamorphed");
        add("quest.goal.rgrmechawarden.zombie_doctor_advancement_goal", "Earn the Advancement \"Zombie Doctor\"");
        add("quest.goal.rgrmechawarden.mine_spawner_nether_goal", "Mine Spawners while in the Nether");
        add("quest.goal.rgrmechawarden.kill_players_laser_goal", "Kill Warden Hunters with your Laser");
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
