package com.divinity.hmedia.rgrmechawarden.event;

import com.divinity.hmedia.rgrmechawarden.RGRMechaWarden;
import com.divinity.hmedia.rgrmechawarden.cap.SkulkHolderAttacher;
import com.divinity.hmedia.rgrmechawarden.client.animatable.WardenAnimatable;
import com.divinity.hmedia.rgrmechawarden.client.renderer.DirectMissileEntityRenderer;
import com.divinity.hmedia.rgrmechawarden.client.renderer.MissileEntityRenderer;
import com.divinity.hmedia.rgrmechawarden.entity.DeepDarkDestroyerEntity;
import com.divinity.hmedia.rgrmechawarden.entity.LaserEntity;
import com.divinity.hmedia.rgrmechawarden.init.*;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import dev._100media.hundredmediageckolib.client.animatable.IHasGeoRenderer;
import dev._100media.hundredmediageckolib.client.model.SimpleGeoEntityModel;
import dev._100media.hundredmediageckolib.client.model.SimpleGeoPlayerModel;
import dev._100media.hundredmediageckolib.client.renderer.GeoPlayerRenderer;
import dev._100media.hundredmediamorphs.capability.MorphHolderAttacher;
import dev._100media.hundredmediamorphs.client.renderer.MorphRenderers;
import dev._100media.hundredmediamorphs.morph.Morph;
import dev._100media.hundredmediaquests.client.screen.QuestSkillScreen;
import dev._100media.hundredmediaquests.client.screen.SkillScreen;
import dev._100media.hundredmediaquests.client.screen.TreeScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.util.Arrays;

@Mod.EventBusSubscriber(modid = RGRMechaWarden.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientModEvents {

    public static final KeyMapping SKILL_TREE_KEY = new KeyMapping("key." + RGRMechaWarden.MODID + ".skill_tree", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_G, "key.category." + RGRMechaWarden.MODID);

    @SubscribeEvent
    public static void registerKeybind(RegisterKeyMappingsEvent event) {
        event.register(SKILL_TREE_KEY);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {

        event.registerEntityRenderer(EntityInit.MISSILE.get(), MissileEntityRenderer::new);
        event.registerEntityRenderer(EntityInit.DIRECT_MISSILE.get(), DirectMissileEntityRenderer::new);
        event.registerEntityRenderer(EntityInit.NUKE.get(), ctx -> new GeoEntityRenderer<>(ctx, new SimpleGeoEntityModel<>(RGRMechaWarden.MODID, "nuke")));
        event.registerEntityRenderer(EntityInit.LASER.get(), ctx -> new GeoEntityRenderer<>(ctx, new SimpleGeoEntityModel<>(RGRMechaWarden.MODID, "laser")) {
            @Override
            public void render(LaserEntity entity, float entityYaw, float partialTick, PoseStack pMatrixStack, MultiBufferSource bufferSource, int packedLight) {
                pMatrixStack.pushPose();
                pMatrixStack.mulPose(Axis.YN.rotationDegrees(Mth.lerp(partialTick, -entity.yRotO, -entity.getYRot()) - 90.0F));
                pMatrixStack.mulPose(Axis.ZN.rotationDegrees(Mth.lerp(partialTick, -entity.xRotO,-entity.getXRot())));
                pMatrixStack.mulPose(Axis.YP.rotationDegrees(-90));
                super.render(entity, entityYaw, partialTick, pMatrixStack, bufferSource, packedLight);
                pMatrixStack.popPose();
            }
        });
        event.registerEntityRenderer(EntityInit.EMP_ORB.get(), ctx -> new GeoEntityRenderer<>(ctx, new SimpleGeoEntityModel<>(RGRMechaWarden.MODID, "nuke")));
        event.registerEntityRenderer(EntityInit.DEEP_DARK_DESTROYER.get(), ctx -> new GeoEntityRenderer<>(ctx, new SimpleGeoEntityModel<>(RGRMechaWarden.MODID, "deep_dark_destroyer")) {
            @Override
            public void render(DeepDarkDestroyerEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
                poseStack.pushPose();
                float f = 1.0f + ((float) (entity.getGrowTicks() / 50) * 2);
                poseStack.scale(f, f, f);
                super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
                poseStack.popPose();
            }
        });

        event.registerBlockEntityRenderer(BlockInit.CHARGING_STATION_BLOCK_ENTITY.get(), ctx -> new GeoBlockRenderer<>(
                new DefaultedBlockGeoModel<>(new ResourceLocation(RGRMechaWarden.MODID, "charging_station_be"))
        ));
        event.registerBlockEntityRenderer(BlockInit.SHOCK_TRAP_BLOCK_ENTITY.get(), ctx -> new GeoBlockRenderer<>(
                new DefaultedBlockGeoModel<>(new ResourceLocation(RGRMechaWarden.MODID, "shock_trap_be"))
        ));
        event.registerBlockEntityRenderer(BlockInit.MECHA_MINES_BLOCK_ENTITY.get(), ctx -> new GeoBlockRenderer<>(
                new DefaultedBlockGeoModel<>(new ResourceLocation(RGRMechaWarden.MODID, "sculky_mecha_mines_be"))
        ));

        createSimpleMorphRenderer(MorphInit.BABY_MECHA.get(), "baby_mecha", new WardenAnimatable()
                .runAnim(RawAnimation.begin().thenLoop("walk"))
                .hoverboardAnim(RawAnimation.begin().then("hoverboard spawn", Animation.LoopType.PLAY_ONCE).thenLoop("hoverboard on")), 0.5f);
        createSimpleMorphRenderer(MorphInit.MECHA_TEEN.get(), "mecha_teen", new WardenAnimatable()
                .hoverboardAnim(RawAnimation.begin().then("hoverboard on", Animation.LoopType.PLAY_ONCE).thenLoop("hoverboard")), 1.0f);
        createSimpleMorphRenderer(MorphInit.MECHA_WARDEN.get(), "mecha_warden", new WardenAnimatable(), 1.0f);
        createSimpleMorphRenderer(MorphInit.MECHA_KING.get(), "mecha_king", new WardenAnimatable(), 1.0f);
        createSimpleMorphRenderer(MorphInit.MECHA_SCULK.get(), "mecha_sculk", new WardenAnimatable().sitAnim(RawAnimation.begin().thenLoop("crouch"))
                .hoverboardAnim(RawAnimation.begin().then("hoverboard on", Animation.LoopType.PLAY_ONCE).thenLoop("Hoverboard")), 3f);
    }

    @SubscribeEvent
    public static void initClient(FMLClientSetupEvent event) {
        MenuScreens.register(MenuInit.SKILL_TREE.get(), (AbstractContainerMenu menu, Inventory inv, Component title) -> new TreeScreen(menu, inv, title,
                new ResourceLocation(RGRMechaWarden.MODID, "textures/gui/screen/skill_tree.png"), 23, 23,
                Arrays.asList(
                        new Pair<>(SkillInit.EVOLUTION_TREE, new Pair<>(66, 87)),
                        new Pair<>(SkillInit.COMBAT_TREE, new Pair<>(114, 87)),
                        new Pair<>(SkillInit.UTILITY_TREE, new Pair<>(163, 87))
                ), 256, 256, 256, 174
        ));
        MenuScreens.register(MenuInit.EVOLUTION_TREE.get(), (AbstractContainerMenu menu, Inventory inv, Component title) -> new SkillScreen(menu, inv, title,
                new ResourceLocation(RGRMechaWarden.MODID, "textures/gui/screen/evolution.png"), 27, 29,
                Arrays.asList(
                        new Pair<>(37, 45),
                        new Pair<>(75, 45),
                        new Pair<>(113, 45),
                        new Pair<>(150, 45),
                        new Pair<>(188, 45)
                ), SkillInit.EVOLUTION_TREE.get(), 256, 256, 256, 163
        ));
        MenuScreens.register(MenuInit.COMBAT_TREE.get(), (AbstractContainerMenu menu, Inventory inv, Component title) -> new QuestSkillScreen(menu, inv, title,
                new ResourceLocation(RGRMechaWarden.MODID, "textures/gui/screen/combat.png"), 30, 30,
                Arrays.asList(
                        new Pair<>(36, 67),
                        new Pair<>(74, 67),
                        new Pair<>(112, 67),
                        new Pair<>(150, 67),
                        new Pair<>(188, 67)
                ), SkillInit.COMBAT_TREE.get(), 256, 256, 256, 161
        ));
        MenuScreens.register(MenuInit.UTILITY_TREE.get(), (AbstractContainerMenu menu, Inventory inv, Component title) -> new SkillScreen(menu, inv, title,
                new ResourceLocation(RGRMechaWarden.MODID, "textures/gui/screen/utility.png"), 27, 29,
                Arrays.asList(
                        new Pair<>(37, 57),
                        new Pair<>(75, 57),
                        new Pair<>(113, 57),
                        new Pair<>(150, 57),
                        new Pair<>(188, 57)
                ), SkillInit.UTILITY_TREE.get(), 256, 256, 256, 163
        ));
    }

    @SubscribeEvent
    public static void registerOverlays(RegisterGuiOverlaysEvent event) {

    }

    private static <T extends IHasGeoRenderer & GeoAnimatable> void createSimpleMorphRenderer(Morph morph, String name, T animatable, float scale) {
        MorphRenderers.registerPlayerMorphRenderer(morph, context -> {
            var renderer = new GeoPlayerRenderer<>(context, new SimpleGeoPlayerModel<>(RGRMechaWarden.MODID, name) {
                private static final ResourceLocation TURRET_MODEL = new ResourceLocation(RGRMechaWarden.MODID, "geo/entity/turret.geo.json");
                private static final ResourceLocation TURRET_TEXTURE = new ResourceLocation(RGRMechaWarden.MODID, "textures/entity/turret.png");
                private final ResourceLocation defaultLocation = new ResourceLocation(RGRMechaWarden.MODID, "textures/entity/" + name + ".png");
                @Override
                public ResourceLocation getTextureResource(T animatable1, @Nullable AbstractClientPlayer player) {
                    if (player != null) {
                        var holder = SkulkHolderAttacher.getSkulkHolderUnwrap(player);
                        if (holder != null && holder.isMechaMorphed()) {
                            return TURRET_TEXTURE;
                        }
                    }
                    return defaultLocation;
                }

                @Override
                public ResourceLocation getModelResource(T animatable, @Nullable AbstractClientPlayer player) {
                    if (player != null) {
                        var holder = SkulkHolderAttacher.getSkulkHolderUnwrap(player);
                        if (holder != null && holder.isMechaMorphed()) {
                            return TURRET_MODEL;
                        }
                    }
                    return super.getModelResource(animatable, player);
                }
            }, animatable) {

                @Override
                protected void moveAndRotateMatrixToMatchBone(PoseStack stack, GeoBone bone) {
                    stack.translate(bone.getPivotX() / 16, bone.getPivotY() / 16, bone.getPivotZ() / 16);
                    float xRot = bone.getRotX() * (180 / (float) Math.PI);
                    float yRot = bone.getRotY() * (180 / (float) Math.PI);
                    float zRot = bone.getRotZ() * (180 / (float) Math.PI);
                    stack.mulPose(Axis.XP.rotationDegrees(xRot));
                    stack.mulPose(Axis.YP.rotationDegrees(yRot));
                    stack.mulPose(Axis.ZP.rotationDegrees(zRot));
                }

                @Override
                public void renderRecursively(PoseStack stack, T animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
                    if (MorphHolderAttacher.getCurrentMorphUnwrap(getCurrentRenderingEntity()) == MorphInit.MECHA_KING.get()) {
                        if ("bone12".equals(bone.getName())) {
                            stack.pushPose();
                            stack.translate(0.8, 0.5, 0);
                            stack.mulPose(Axis.XP.rotationDegrees(90));
                            stack.mulPose(Axis.ZN.rotationDegrees(180));
                            Minecraft.getInstance().gameRenderer.itemInHandRenderer.renderItem(getCurrentRenderingEntity(), getCurrentRenderingEntity().getItemInHand(InteractionHand.MAIN_HAND),
                                    ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, false, stack, bufferSource,
                                    packedLight);
                            stack.popPose();
                            buffer = bufferSource.getBuffer(currentRenderType);
                        }
                    }
                    else if ("finallarm2".equals(bone.getName()) || "right_arm".equals(bone.getName())) {
                        stack.pushPose();
                        moveAndRotateMatrixToMatchBone(stack, bone);
                        if ("right_arm".equals(bone.getName())) {
                            stack.translate(0, -1.5, -0.30);
                            stack.scale(1.25f, 1.25f, 1.25f);
                        }
                        stack.mulPose(Axis.XP.rotationDegrees(90));
                        stack.mulPose(Axis.ZN.rotationDegrees(180));
                        Minecraft.getInstance().gameRenderer.itemInHandRenderer.renderItem(getCurrentRenderingEntity(), getCurrentRenderingEntity().getItemInHand(InteractionHand.MAIN_HAND),
                                ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, false, stack, bufferSource,
                                packedLight);
                        stack.popPose();
                        buffer = bufferSource.getBuffer(currentRenderType);
                    }
                    super.renderRecursively(stack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
                }

                @Override
                public void render(AbstractClientPlayer player, T animatable1, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
                    setCurrentRenderingEntity(player);
                    if (!player.hasEffect(MobEffects.INVISIBILITY)) {
                        var holder = SkulkHolderAttacher.getSkulkHolderUnwrap(player);
                        if (holder != null) {
                            poseStack.pushPose();
                            if (!holder.isMechaMorphed() && holder.getCamouflagedBlock() != Blocks.AIR) {
                                poseStack.translate(-0.5, 0, -0.5);
                                Minecraft.getInstance().getBlockRenderer().renderSingleBlock(holder.getCamouflagedBlock().defaultBlockState(), poseStack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY);
                            }
                            else {
                                if (player.getVehicle() != null) {
                                    if (morph == MorphInit.BABY_MECHA.get()) {
                                        poseStack.translate(0, 0.15, 0);
                                    }
                                    else poseStack.translate(0, 0.8, 0);
                                }
                                if (holder.isMechaBoard()) {
                                    poseStack.mulPose(Axis.YN.rotationDegrees(90));
                                }
                                poseStack.scale(scale, scale, scale);
                                RenderType renderType = getRenderType(animatable1, getTextureLocation(animatable1), bufferSource, partialTick);
                                this.currentRenderType = renderType;
                                super.render(player, animatable1, entityYaw, partialTick, poseStack, bufferSource, packedLight);
                                currentRenderType = null;
                            }
                            poseStack.popPose();
                        }
                    }
                }
            };
            return renderer;
        });
    }
}
