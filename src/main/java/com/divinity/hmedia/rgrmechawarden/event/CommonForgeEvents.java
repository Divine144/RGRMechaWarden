package com.divinity.hmedia.rgrmechawarden.event;

import com.divinity.hmedia.rgrmechawarden.RGRMechaWarden;
import com.divinity.hmedia.rgrmechawarden.cap.SkulkHolderAttacher;
import com.divinity.hmedia.rgrmechawarden.quest.goal.AquireAdvancementGoal;
import com.divinity.hmedia.rgrmechawarden.quest.goal.TradeWithVillagerGoal;
import com.divinity.hmedia.rgrmechawarden.utils.MechaWardenUtils;
import com.mojang.brigadier.Command;
import dev._100media.hundredmediaquests.cap.QuestHolderAttacher;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.AnimalTameEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.TradeWithVillagerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RGRMechaWarden.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonForgeEvents {

    @SubscribeEvent
    public static void onAdvancementEarn(AdvancementEvent.AdvancementEarnEvent event) {
        String advancementID = event.getAdvancement().getId().toString();
        QuestHolderAttacher.checkAllGoals(event.getEntity(), goal -> {
            if (goal instanceof AquireAdvancementGoal advancementGoal) {
                if (advancementID.contains(advancementGoal.getAdvancementID())) {
                    advancementGoal.addProgress(1);
                    return true;
                }
            }
            return false;
        });
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        var dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal(RGRMechaWarden.MODID)
                .then(Commands.literal("test")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(context -> {

                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                )
        );
    }

    @SubscribeEvent
    public static void onVillagerTrade(TradeWithVillagerEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (event.getAbstractVillager() instanceof Villager) {
                MechaWardenUtils.addToGenericQuestGoal(player, TradeWithVillagerGoal.class);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLeave(EntityLeaveLevelEvent event) {

    }

    @SubscribeEvent
    public static void onKill(LivingDeathEvent event) {

    }

    @SubscribeEvent
    public static void onLivingJump(LivingEvent.LivingJumpEvent event) {

    }

    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event) {


    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player instanceof ServerPlayer player && event.phase == TickEvent.Phase.END) {
            SkulkHolderAttacher.getSkulkHolder(player).ifPresent(cap -> {


            });
        }
    }

    @SubscribeEvent
    public static void playerRightClick(PlayerInteractEvent event) {
        if (event.getEntity().level().isClientSide) return;
    }

    @SubscribeEvent
    public static void netInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        if (event.isCanceled()) return;
        if (event.getTarget() instanceof LivingEntity living) {

        }
    }

    @SubscribeEvent
    public static void playerLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getEntity().level().isClientSide) return;
        if (event.getHand() == InteractionHand.MAIN_HAND) {

        }
    }

    @SubscribeEvent
    public static void playerLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
        if (!event.getEntity().level().isClientSide) return;
    }

    @SubscribeEvent
    public static void playerAttackEntity(AttackEntityEvent event) {
        if (event.getEntity().level().isClientSide) return;
    }

    @SubscribeEvent
    public static void onTame(AnimalTameEvent event) {
/*        if (event.getTamer() instanceof ServerPlayer player) {
            QuestHolderAttacher.checkAllGoals(player, goal -> {
                if (goal instanceof TameEntityGoal tameEntityGoal) {
                    return tameEntityGoal.mobsTamed(event.getAnimal().getType());
                }
                return false;
            });
        }*/
    }
}
