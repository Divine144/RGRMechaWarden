package com.divinity.hmedia.rgrmechawarden.item;

import com.divinity.hmedia.rgrmechawarden.cap.SkulkHolderAttacher;
import com.divinity.hmedia.rgrmechawarden.init.EffectInit;
import com.divinity.hmedia.rgrmechawarden.utils.MechaWardenUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Comparator;
import java.util.Optional;

public class MechoLocationItem extends Item {

    public MechoLocationItem(Properties pProperties) {
        super(pProperties);
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);
        if (pLevel.isClientSide) {
            return InteractionResultHolder.consume(itemStack);
        }
        var holder = SkulkHolderAttacher.getSkulkHolderUnwrap(pPlayer);
        if (holder != null && holder.removeSkulk(20)) {
            var list = MechaWardenUtils.getEntitiesInRange(pPlayer, Player.class, 35, 25, 35, p -> p != pPlayer);
            if (!list.isEmpty()) {
                ((ServerPlayer) pPlayer).connection.send(new ClientboundSetTitleTextPacket(Component.literal("There are hunters nearby").withStyle(ChatFormatting.RED)));
                for (Player player : list) {
                    player.addEffect(new MobEffectInstance(EffectInit.LOCK_ON.get(), -1, 0, false, false, false));
                }
            }
            else {
                Optional<ServerPlayer> playerOptional = ((ServerPlayer) pPlayer).serverLevel()
                        .players()
                        .stream()
                        .filter(p -> p != pPlayer)
                        .min(Comparator.comparing(entity -> entity.distanceToSqr(pPlayer.getX(), pPlayer.getY(), pPlayer.getZ())));
                if (playerOptional.isPresent()) {
                    int distance = (int) pPlayer.distanceTo(playerOptional.get());
                    Direction playerDirection = MechaWardenUtils.getDirectionFromTwoPoints(pPlayer.getEyePosition(0), playerOptional.get().getEyePosition(0));
                    if (playerDirection != null) {
                        ((ServerPlayer) pPlayer).connection.send(new ClientboundSetTitleTextPacket(Component.literal("The nearest hunter is").withStyle(ChatFormatting.RED)));
                        ((ServerPlayer) pPlayer).connection.send(new ClientboundSetSubtitleTextPacket(Component.literal("%s blocks away to the %s".formatted(distance, playerDirection.getOpposite().getName())).withStyle(ChatFormatting.RED)));
                    }
                }
            }
            pPlayer.getCooldowns().addCooldown(this, 20 * 10);
        }
        return InteractionResultHolder.consume(itemStack);
    }
}