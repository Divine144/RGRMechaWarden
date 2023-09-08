package com.divinity.hmedia.rgrmechawarden.event;

import com.divinity.hmedia.rgrmechawarden.RGRMechaWarden;
import com.divinity.hmedia.rgrmechawarden.cap.SkulkHolderAttacher;
import com.divinity.hmedia.rgrmechawarden.init.MenuInit;
import com.divinity.hmedia.rgrmechawarden.init.SkillInit;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import dev._100media.hundredmediageckolib.client.animatable.IHasGeoRenderer;
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
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
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
import software.bernie.geckolib.core.animatable.GeoAnimatable;

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

        // TODO : Change these
 /*       createSimpleMorphRenderer(MorphInit.BABY_ANT.get(), "baby_ant", new AntAnimatable(), 1.0f);
        createSimpleMorphRenderer(MorphInit.BLACK_ANT.get(), "black_ant", new AntAnimatable(), 1.0f);
        createSimpleMorphRenderer(MorphInit.FIRE_ANT.get(), "baby_ant", new SimpleAnimatable(), 1.0f);
        createSimpleMorphRenderer(MorphInit.KING_ANT.get(), "baby_ant", new SimpleAnimatable(), 1.0f);
        createSimpleMorphRenderer(MorphInit.OMEGA_ANT.get(), "baby_ant", new SimpleAnimatable(), 1.0f);*/
    }

    @SubscribeEvent
    public static void initClient(FMLClientSetupEvent event) {
        MenuScreens.register(MenuInit.SKILL_TREE.get(), (AbstractContainerMenu menu, Inventory inv, Component title) -> new TreeScreen(menu, inv, title,
                new ResourceLocation(RGRMechaWarden.MODID, "textures/gui/screen/skill_tree.png"), 21, 22,
                Arrays.asList(
                        new Pair<>(SkillInit.EVOLUTION_TREE, new Pair<>(56, 80)),
                        new Pair<>(SkillInit.COMBAT_TREE, new Pair<>(115, 80)),
                        new Pair<>(SkillInit.UTILITY_TREE, new Pair<>(170, 80))
                ), 256, 256, 256, 165
        ));
        MenuScreens.register(MenuInit.EVOLUTION_TREE.get(), (AbstractContainerMenu menu, Inventory inv, Component title) -> new SkillScreen(menu, inv, title,
                new ResourceLocation(RGRMechaWarden.MODID, "textures/gui/screen/evolution.png"), 20, 20,
                Arrays.asList(
                        new Pair<>(38, 87),
                        new Pair<>(76, 87),
                        new Pair<>(114, 87),
                        new Pair<>(152, 87),
                        new Pair<>(188, 87)
                ), SkillInit.EVOLUTION_TREE.get(), 256, 256, 256, 230
        ));
        MenuScreens.register(MenuInit.COMBAT_TREE.get(), (AbstractContainerMenu menu, Inventory inv, Component title) -> new QuestSkillScreen(menu, inv, title,
                new ResourceLocation(RGRMechaWarden.MODID, "textures/gui/screen/combat.png"), 17, 16,
                Arrays.asList(
                        new Pair<>(57, 74),
                        new Pair<>(98, 69),
                        new Pair<>(133, 64),
                        new Pair<>(168, 69),
                        new Pair<>(197, 75)
                ), SkillInit.COMBAT_TREE.get(), 256, 256, 256, 189
        ));
        MenuScreens.register(MenuInit.UTILITY_TREE.get(), (AbstractContainerMenu menu, Inventory inv, Component title) -> new SkillScreen(menu, inv, title,
                new ResourceLocation(RGRMechaWarden.MODID, "textures/gui/screen/utility.png"), 21, 20,
                Arrays.asList(
                        new Pair<>(28, 90),
                        new Pair<>(76, 88),
                        new Pair<>(119, 91),
                        new Pair<>(167, 89),
                        new Pair<>(209, 91)
                ), SkillInit.UTILITY_TREE.get(), 256, 256, 256, 192
        ));
    }

    @SubscribeEvent
    public static void registerOverlays(RegisterGuiOverlaysEvent event) {

    }

    // TODO: Make mecha morph acutally morph into the turret if active
    private static <T extends IHasGeoRenderer & GeoAnimatable> void createSimpleMorphRenderer(Morph morph, String name, T animatable, float scale) {
        MorphRenderers.registerPlayerMorphRenderer(morph, context -> {
            var renderer = new GeoPlayerRenderer<>(context, new SimpleGeoPlayerModel<>(RGRMechaWarden.MODID, name) {
                @Override
                public ResourceLocation getTextureResource(T animatable1, @Nullable AbstractClientPlayer player) {
                    return new ResourceLocation(RGRMechaWarden.MODID, "textures/entity/" + name + ".png");
                }
            }, animatable) {

                @Override
                public void render(AbstractClientPlayer player, T animatable1, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
                    if (!player.hasEffect(MobEffects.INVISIBILITY)) {
                        var holder = SkulkHolderAttacher.getSkulkHolderUnwrap(player);
                        if (holder != null) {
                            poseStack.pushPose();
                            if (holder.getCamouflagedBlock() != Blocks.AIR) {
                                poseStack.translate(-0.5, 0, -0.5);
                                Minecraft.getInstance().getBlockRenderer().renderSingleBlock(holder.getCamouflagedBlock().defaultBlockState(), poseStack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY);
                            }
                            else {
                                if (player.getVehicle() != null) {
                                    poseStack.translate(0, 0.10, 0);
                                }
                                poseStack.scale(scale, scale, scale);
                                super.render(player, animatable1, entityYaw, partialTick, poseStack, bufferSource, packedLight);
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