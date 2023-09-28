package com.divinity.hmedia.rgrmechawarden.item;

import com.divinity.hmedia.rgrmechawarden.RGRMechaWarden;
import com.divinity.hmedia.rgrmechawarden.utils.MechaWardenUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev._100media.hundredmediamorphs.capability.MorphHolderAttacher;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Optional;
import java.util.function.Consumer;

public class MegaMagnetItem extends Item implements GeoItem {

    private final AnimatableInstanceCache instanceCache = GeckoLibUtil.createInstanceCache(this);

    public MegaMagnetItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void onUseTick(Level pLevel, LivingEntity pLivingEntity, ItemStack pStack, int pRemainingUseDuration) {
        if (!pLevel.isClientSide) {
            if (pLivingEntity instanceof ServerPlayer player) {
                boolean pullEntity = pStack.getOrCreateTag().getBoolean("pullEntity");
                if (pullEntity) {
                    Optional<ServerPlayer> playerOptional = MechaWardenUtils
                            .getEntitiesInRange(player, ServerPlayer.class, 30, 30, 30, p -> p != player && MorphHolderAttacher.getCurrentMorph(p).isPresent())
                            .stream()
                            .findAny();
                    if (playerOptional.isPresent()) {
                        if (pRemainingUseDuration % 20 == 0) {
                            pStack.setDamageValue(Math.min(pStack.getDamageValue() + 1, pStack.getMaxDamage()));
                            if (pStack.getDamageValue() >= pStack.getMaxDamage()) {
                                player.stopUsingItem();
                                return;
                            }
                        }
                        MechaWardenUtils.pullEntityToPoint(playerOptional.get(), player.position(), 3);
                    }
                }
                else {
                    BlockHitResult result = MechaWardenUtils.blockTrace(player, ClipContext.Fluid.NONE, 30, false);
                    if (result != null) {
                        if (!pLevel.getBlockState(result.getBlockPos()).isAir()) {
                            if (pRemainingUseDuration % 100 == 0) {
                                pStack.setDamageValue(Math.min(pStack.getDamageValue() + 1, pStack.getMaxDamage()));
                                if (pStack.getDamageValue() >= pStack.getMaxDamage()) {
                                    player.stopUsingItem();
                                    return;
                                }
                            }
                            MechaWardenUtils.pullEntityToPoint(player, result.getLocation(), 3);
                        }
                    }
                }
            }
        }
        super.onUseTick(pLevel, pLivingEntity, pStack, pRemainingUseDuration);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);
        if (pLevel.isClientSide) {
            return itemStack.getDamageValue() >= itemStack.getMaxDamage() ? InteractionResultHolder.pass(itemStack) : InteractionResultHolder.consume(itemStack);
        }
        CompoundTag tag = itemStack.getOrCreateTag();
        if (pPlayer.isShiftKeyDown()) {
            boolean original = tag.getBoolean("pullEntity");
            tag.putBoolean("pullEntity", !original);
            ((ServerPlayer) pPlayer).sendSystemMessage(Component.literal("Mode Switched To: %s".formatted(tag.getBoolean("pullEntity") ? "Entity Pull" : "Self Pull"))
                    .withStyle(ChatFormatting.GREEN), true);
        }
        if (itemStack.getDamageValue() >= itemStack.getMaxDamage()) {
            ((ServerPlayer) pPlayer).sendSystemMessage(Component.literal("Magnet out of charge!")
                    .withStyle(ChatFormatting.RED), true);
            return InteractionResultHolder.pass(itemStack);
        }
        return ItemUtils.startUsingInstantly(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return instanceCache;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private BlockEntityWithoutLevelRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null)
                    this.renderer = new GeoItemRenderer<MegaMagnetItem>(new DefaultedItemGeoModel<>(new ResourceLocation(RGRMechaWarden.MODID, "mega_magnet"))) {

                        @Override
                        public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
                            poseStack.pushPose();
                            switch (transformType) {
                                case FIRST_PERSON_LEFT_HAND, FIRST_PERSON_RIGHT_HAND -> {
                                    poseStack.translate(0.2, -0.95, -0.2);
                                }
                                case THIRD_PERSON_LEFT_HAND, THIRD_PERSON_RIGHT_HAND -> {
                                    poseStack.translate(0, -0.75, 0.05);
                                }
                                case GROUND -> {
                                    poseStack.translate(0, -1, 0);
                                }
                            }
                            super.renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
                            poseStack.popPose();
                        }

                        @Override
                        protected void renderInGui(ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
                            poseStack.pushPose();
                            poseStack.scale(1.5f, 1.5f, 1.5f);
                            poseStack.translate(-0.17, -1.2, -0.1);
                            super.renderInGui(transformType, poseStack, bufferSource, packedLight, packedOverlay);
                            poseStack.popPose();
                        }
                    };
                return this.renderer;
            }
        });
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 72000;
    }
}
