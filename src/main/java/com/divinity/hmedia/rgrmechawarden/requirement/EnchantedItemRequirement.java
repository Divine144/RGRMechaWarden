package com.divinity.hmedia.rgrmechawarden.requirement;

import dev._100media.hundredmediaquests.skill.requirements.SkillRequirement;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public class EnchantedItemRequirement implements SkillRequirement {

    private final NonNullList<ItemStack> armorList = NonNullList.create();
    private Predicate<ItemStack> predicate = stack -> true;
    private String itemName = "";

    public EnchantedItemRequirement(Predicate<ItemStack> predicate) {
        this.predicate = predicate;
    }

    public EnchantedItemRequirement(Predicate<ItemStack> predicate, String itemName) {
        this.predicate = predicate;
        this.itemName = itemName;
    }

    @Override
    public boolean hasRequirement(Player player) {
        NonNullList<ItemStack> tempList = NonNullList.create();
        checkInventoryWithList(tempList, player.getInventory());
        return !tempList.isEmpty();
    }

    @Override
    public void consumeRequirement(ServerPlayer player) {
        checkInventoryWithList(armorList, player.getInventory());
        armorList.forEach(i -> i.setCount(0));
    }

    @Override
    public MutableComponent getFancyDescription(Player player) {
        NonNullList<ItemStack> tempList = NonNullList.create();
        checkInventoryWithList(tempList, player.getInventory());
        int current = !tempList.isEmpty() ? 1 : 0;
        return Component.literal("%s/%s %s".formatted(current, 1, itemName));
    }

    private void checkInventoryWithList(NonNullList<ItemStack> list, Inventory inventory) {
        inventory.items.forEach(s -> {
            if (predicate.test(s)) {
                list.add(s);
            }
        });
    }
}
