package com.divinity.hmedia.rgrmechawarden.client.renderer;

import com.divinity.hmedia.rgrmechawarden.RGRMechaWarden;
import com.divinity.hmedia.rgrmechawarden.entity.MissileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev._100media.hundredmediageckolib.client.model.SimpleGeoEntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MissileEntityRenderer extends GeoEntityRenderer<MissileEntity> {

    public MissileEntityRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SimpleGeoEntityModel<>(RGRMechaWarden.MODID, "missile"));
    }

    @Override
    public void render(MissileEntity entity, float entityYaw, float partialTick, PoseStack pMatrixStack, MultiBufferSource bufferSource, int packedLight) {
        pMatrixStack.pushPose();
        pMatrixStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, entity.yRotO, entity.getYRot()) - 90F));
        pMatrixStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTick, entity.xRotO, entity.getXRot()) - 90F));
        super.render(entity, entityYaw, partialTick, pMatrixStack, bufferSource, packedLight);
        pMatrixStack.popPose();
    }
}
