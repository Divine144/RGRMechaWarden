package com.divinity.hmedia.rgrmechawarden.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LightningBoltRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.shadowed.eliotlash.mclib.math.functions.limit.Min;

@Mixin(LightningBoltRenderer.class)
public abstract class LightningBoldRendererMixin extends EntityRenderer<LightningBolt> {

    protected LightningBoldRendererMixin(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Inject(
            method = "render(Lnet/minecraft/world/entity/LightningBolt;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD"),
            cancellable = true
    )
    public void rgrmechawarden$render_HEAD(LightningBolt pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight, CallbackInfo ci) {
        float[] afloat = new float[8];
        float[] afloat1 = new float[8];
        float f = 0.0F;
        float f1 = 0.0F;
        RandomSource randomsource = RandomSource.create(pEntity.seed);

        for(int i = 7; i >= 0; --i) {
            afloat[i] = f;
            afloat1[i] = f1;
            f += (float)(randomsource.nextInt(11) - 5);
            f1 += (float)(randomsource.nextInt(11) - 5);
        }

        VertexConsumer vertexconsumer = pBuffer.getBuffer(RenderType.lightning());
        Matrix4f matrix4f = pMatrixStack.last().pose();

        for(int j = 0; j < 4; ++j) {
            RandomSource randomsource1 = RandomSource.create(pEntity.seed);

            for(int k = 0; k < 3; ++k) {
                int l = 7;
                int i1 = 0;
                if (k > 0) {
                    l = 7 - k;
                }

                if (k > 0) {
                    i1 = l - 2;
                }

                float f2 = afloat[l] - f;
                float f3 = afloat1[l] - f1;

                for(int j1 = l; j1 >= i1; --j1) {
                    float f4 = f2;
                    float f5 = f3;
                    if (k == 0) {
                        f2 += (float)(randomsource1.nextInt(11) - 5);
                        f3 += (float)(randomsource1.nextInt(11) - 5);
                    } else {
                        f2 += (float)(randomsource1.nextInt(31) - 15);
                        f3 += (float)(randomsource1.nextInt(31) - 15);
                    }

                    float f6 = 0.5F;
                    float f7 = 0.45F;
                    float f8 = 0.45F;
                    float f9 = 0.5F;
                    float f10 = 0.1F + (float)j * 0.2F;
                    if (k == 0) {
                        f10 *= (float)j1 * 0.1F + 1.0F;
                    }

                    float f11 = 0.1F + (float)j * 0.2F;
                    if (k == 0) {
                        f11 *= ((float)j1 - 1.0F) * 0.1F + 1.0F;
                    }

                    quad(matrix4f, vertexconsumer, f2, f3, j1, f4, f5, 0.45F, 0.45F, 0.5F, f10, f11, false, false, true, false);
                    quad(matrix4f, vertexconsumer, f2, f3, j1, f4, f5, 0.45F, 0.45F, 0.5F, f10, f11, false, true, false, false);
                }
            }
        }
        ci.cancel();
    }

    @Unique
    private static float fraction(int pNumerator, int pDenominator) {
        return (float)pNumerator / (float)pDenominator;
    }

    @Unique
    private static void vertex(VertexConsumer pConsumer, Matrix4f pPose, Matrix3f pNormal, int pLightmapUV, float pX, int pY, int pU, int pV) {
        pConsumer.vertex(pPose, pX - 0.5F, (float)pY - 0.5F, 0.0F).color(255, 255, 255, 255).uv((float)pU, (float)pV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(pLightmapUV).normal(pNormal, 0.0F, 1.0F, 0.0F).endVertex();
    }

    @Unique
    private static void stringVertex(float pX, float pY, float pZ, VertexConsumer pConsumer, PoseStack.Pose pPose, float p_174124_, float p_174125_) {
        float f = pX * p_174124_;
        float f1 = pY * (p_174124_ * p_174124_ + p_174124_) * 0.5F + 0.25F;
        float f2 = pZ * p_174124_;
        float f3 = pX * p_174125_ - f;
        float f4 = pY * (p_174125_ * p_174125_ + p_174125_) * 0.5F + 0.25F - f1;
        float f5 = pZ * p_174125_ - f2;
        float f6 = Mth.sqrt(f3 * f3 + f4 * f4 + f5 * f5);
        f3 /= f6;
        f4 /= f6;
        f5 /= f6;
        pConsumer.vertex(pPose.pose(), f, f1, f2).color(0.45F, 0.45F, 0.5F, 0.3F).normal(pPose.normal(), f3, f4, f5).endVertex();
    }
    @Unique
    private static void quad(Matrix4f pMatrix, VertexConsumer pConsumer, float pX1, float pZ1, int pIndex, float pX2, float pZ2, float pRed, float pGreen, float pBlue, float p_115283_, float p_115284_, boolean p_115285_, boolean p_115286_, boolean p_115287_, boolean p_115288_) {

/*        pConsumer.vertex(pMatrix, f, f1, f2).color(pRed, pGreen, pBlue, 0.3F).endVertex();*/

        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            pConsumer.vertex(pMatrix, (float) (pX1 + (p_115285_ ? p_115284_ : -p_115284_)), (float) player.getY(), (float) (pZ1 + (p_115286_ ? p_115284_ : -p_115284_))).color(pRed, pGreen, pBlue, 0.3F).endVertex();
            pConsumer.vertex(pMatrix, (float) (pX2 + (p_115285_ ? p_115283_ : -p_115283_)), (float) player.getY() + 20 + pIndex, (float) (pZ2 + (p_115286_ ? p_115283_ : -p_115283_))).color(pRed, pGreen, pBlue, 0.3F).endVertex();
            pConsumer.vertex(pMatrix, (float) (pX2 + (p_115287_ ? p_115283_ : -p_115283_)), (float) player.getY() + 20 + pIndex, (float) (pZ2 + (p_115288_ ? p_115283_ : -p_115283_))).color(pRed, pGreen, pBlue, 0.3F).endVertex();
            pConsumer.vertex(pMatrix, (float) (pX1 + (p_115287_ ? p_115284_ : -p_115284_)), (float) player.getY(), (float) (pZ1 + (p_115288_ ? p_115284_ : -p_115284_))).color(pRed, pGreen, pBlue, 0.3F).endVertex();
        }
    }
}
