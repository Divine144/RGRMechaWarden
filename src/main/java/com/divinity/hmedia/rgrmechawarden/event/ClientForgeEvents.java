package com.divinity.hmedia.rgrmechawarden.event;

import com.divinity.hmedia.rgrmechawarden.cap.SkulkHolderAttacher;
import com.divinity.hmedia.rgrmechawarden.init.MenuInit;
import com.mojang.blaze3d.vertex.PoseStack;
import dev._100media.hundredmediamorphs.capability.MorphHolderAttacher;
import dev._100media.hundredmediaquests.network.HMQNetworkHandler;
import dev._100media.hundredmediaquests.network.packet.OpenMainTreePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientForgeEvents {
    private static final ResourceLocation FOOD_OVERLAY = new ResourceLocation("food_level");

    @SubscribeEvent
    public static void keyPressEvent(InputEvent.Key event) {
        if (ClientModEvents.SKILL_TREE_KEY.isDown()) {
            HMQNetworkHandler.INSTANCE.sendToServer(new OpenMainTreePacket(MenuInit.SKILL_TREE.get()));
        }
    }

    @SubscribeEvent
    public static void onOverlayRender(RenderGuiOverlayEvent.Post event) {
        if (!event.getOverlay().id().equals(FOOD_OVERLAY)) return;
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            SkulkHolderAttacher.getSkulkHolder(player).ifPresent(holder -> {
                if (MorphHolderAttacher.getCurrentMorph(player).isPresent()) {
                    GuiGraphics guiGraphics = event.getGuiGraphics();
                    PoseStack poseStack = guiGraphics.pose();
                    poseStack.pushPose();
                    int x = event.getWindow().getGuiScaledWidth() / 2 + 91;
                    int y = event.getWindow().getGuiScaledHeight() - 39 - 2;
                    guiGraphics.fill(x - 81, y, x, y - 8, 0xFF000000);
                    int percent = (int) (((double) holder.getSkulk() / holder.getSkulkCap()) * 79);
                    guiGraphics.fill(x - 80, y - 1, x - (80 - percent), y - 7, 0x014d4e);
                    poseStack.popPose();
                }
            });
        }
    }
}
