package com.divinity.hmedia.rgrmechawarden.item;

import com.divinity.hmedia.rgrmechawarden.RGRMechaWarden;
import com.divinity.hmedia.rgrmechawarden.entity.EmpOrbEntity;
import com.divinity.hmedia.rgrmechawarden.init.EntityInit;
import com.divinity.hmedia.rgrmechawarden.init.SoundInit;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class DisruptorRayItem extends Item implements GeoItem {

    private final AnimatableInstanceCache instanceCache = GeckoLibUtil.createInstanceCache(this);

    public DisruptorRayItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);
        if (pLevel.isClientSide) {
            return InteractionResultHolder.consume(itemStack);
        }
        EmpOrbEntity entity = new EmpOrbEntity(EntityInit.EMP_ORB.get(), pLevel);
        entity.setPos(pPlayer.getX(), pPlayer.getEyeY() - 0.1D, pPlayer.getZ());
        entity.setOwner(pPlayer);
        entity.shootFromRotation(pPlayer, pPlayer.getXRot(), pPlayer.getYRot(), 0, 1.5F, 0);
        pLevel.playSound(null, pPlayer.blockPosition(), SoundInit.RAY.get(), SoundSource.PLAYERS, 0.5f, 1.0f);
        pLevel.addFreshEntity(entity);
      /*  pLevel.playSound(null, pPlayer.blockPosition(), SoundInit.ACID_SPRAY.get(), SoundSource.PLAYERS, 0.3f, 1f);*/
        return InteractionResultHolder.consume(itemStack);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private BlockEntityWithoutLevelRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null)
                    this.renderer = new GeoItemRenderer<DisruptorRayItem>(new DefaultedItemGeoModel<>(new ResourceLocation(RGRMechaWarden.MODID, "disruptor_ray"))) {
                        @Override
                        public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
                            poseStack.pushPose();
                            switch (transformType) {
                                case THIRD_PERSON_LEFT_HAND, THIRD_PERSON_RIGHT_HAND -> {
                                    poseStack.translate(-0.07, -0.2, -0.3);
                                }
                            }
                            super.renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
                            poseStack.popPose();
                        }

                        @Override
                        protected void renderInGui(ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
                            poseStack.pushPose();
                            poseStack.mulPose(Axis.YP.rotationDegrees(90));
                            poseStack.scale(1f, 1f, 1f);
                            poseStack.translate(0.05, -0.45, -0.1);
                            super.renderInGui(transformType, poseStack, bufferSource, packedLight, packedOverlay);
                            poseStack.popPose();
                        }
                    };
                return this.renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return instanceCache;
    }
}
