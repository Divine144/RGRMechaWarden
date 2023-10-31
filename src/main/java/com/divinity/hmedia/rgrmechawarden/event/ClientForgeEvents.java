package com.divinity.hmedia.rgrmechawarden.event;

import com.divinity.hmedia.rgrmechawarden.cap.SkulkHolderAttacher;
import com.divinity.hmedia.rgrmechawarden.init.EffectInit;
import com.divinity.hmedia.rgrmechawarden.init.MenuInit;
import com.divinity.hmedia.rgrmechawarden.network.NetworkHandler;
import com.divinity.hmedia.rgrmechawarden.network.serverbound.EscapeNetPacket;
import com.mojang.blaze3d.vertex.PoseStack;
import dev._100media.hundredmediamorphs.capability.MorphHolderAttacher;
import dev._100media.hundredmediaquests.network.HMQNetworkHandler;
import dev._100media.hundredmediaquests.network.packet.OpenMainTreePacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderNameTagEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;
import software.bernie.shadowed.eliotlash.mclib.math.functions.limit.Min;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientForgeEvents {
    private static final ResourceLocation FOOD_OVERLAY = new ResourceLocation("food_level");

    private static final List<PlayerInput> baseInputList = Arrays.asList(PlayerInput.SHIFTING, PlayerInput.JUMPING, PlayerInput.JUMPING, PlayerInput.SHIFTING, PlayerInput.JUMPING);

    private static final List<PlayerInput> inputList = new ArrayList<>(baseInputList);

    @SubscribeEvent
    public static void keyPressEvent(InputEvent.Key event) {
        if (ClientModEvents.SKILL_TREE_KEY.isDown()) {
            HMQNetworkHandler.INSTANCE.sendToServer(new OpenMainTreePacket(MenuInit.SKILL_TREE.get()));
        }
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player != null) {
            if (player.hasEffect(EffectInit.NETTED.get())) {
                if (inputList.isEmpty()) {
                    inputList.addAll(baseInputList);
                    Collections.shuffle(inputList);
                    NetworkHandler.INSTANCE.sendToServer(new EscapeNetPacket());
                }
                else {
                    PlayerInput playerInput = inputList.get(0);
                    if (event.getKey() == GLFW.GLFW_KEY_SPACE && event.getAction() == GLFW.GLFW_PRESS) {
                        if (playerInput == PlayerInput.JUMPING) {
                            inputList.remove(0);
                            String message = inputList.isEmpty() ? "You broke free!" : "Correct! %s more actions left!".formatted(inputList.size());
                            minecraft.getChatListener().handleSystemMessage(Component.literal(message).withStyle(ChatFormatting.GREEN), true);
                        }
                        else minecraft.getChatListener().handleSystemMessage(Component.literal("Press %s".formatted("Shift!")).withStyle(ChatFormatting.RED), true);
                    }
                    else if (event.getKey() == GLFW.GLFW_KEY_LEFT_SHIFT && event.getAction() == GLFW.GLFW_PRESS) {
                        if (playerInput == PlayerInput.SHIFTING) {
                            inputList.remove(0);
                            String message = inputList.isEmpty() ? "You broke free!" : "Correct! %s more actions left!".formatted(inputList.size());
                            minecraft.getChatListener().handleSystemMessage(Component.literal(message).withStyle(ChatFormatting.GREEN), true);
                        }
                        else minecraft.getChatListener().handleSystemMessage(Component.literal("Press %s".formatted("Space!")).withStyle(ChatFormatting.RED), true);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onRenderNameTag(RenderNameTagEvent event) {
        if (event.getEntity() instanceof Player player) {
            var holder = SkulkHolderAttacher.getSkulkHolderUnwrap(player);
            if (holder != null && holder.isMechaMorphed()) {
                event.setContent(Component.empty());
            }
        }
    }

    @SubscribeEvent
    public static void movementInputUpdate(MovementInputUpdateEvent event) {
        Player player = event.getEntity();
        Input input = event.getInput();
        var holder = SkulkHolderAttacher.getSkulkHolderUnwrap(player);
        if (player.hasEffect(EffectInit.NETTED.get()) || holder != null && holder.isMechaMorphed()) {
            input.up = false;
            input.down = false;
            input.left = false;
            input.right = false;
            input.jumping = false;
            input.shiftKeyDown = false;
            input.leftImpulse = 0;
            input.forwardImpulse = 0;
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
                    guiGraphics.fill(x - 80, y - 1, x - (80 - percent), y - 7, 0xFF064d4f);
                    poseStack.popPose();
                }
            });
        }
    }

    enum PlayerInput {
        SHIFTING,
        JUMPING
    }
}
