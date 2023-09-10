package com.divinity.hmedia.rgrmechawarden.event;

import com.divinity.hmedia.rgrmechawarden.RGRMechaWarden;
import com.divinity.hmedia.rgrmechawarden.cap.SkulkHolder;
import com.divinity.hmedia.rgrmechawarden.cap.SkulkHolderAttacher;
import com.divinity.hmedia.rgrmechawarden.init.BlockInit;
import com.divinity.hmedia.rgrmechawarden.init.ItemInit;
import com.divinity.hmedia.rgrmechawarden.init.MorphInit;
import com.divinity.hmedia.rgrmechawarden.quest.goal.*;
import com.divinity.hmedia.rgrmechawarden.utils.MechaWardenUtils;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import dev._100media.hundredmediamorphs.capability.MorphHolderAttacher;
import dev._100media.hundredmediaquests.cap.QuestHolderAttacher;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.SpawnerBlock;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
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
                .then(Commands.literal("energy")
                        .then(Commands.literal("set")
                                .then(Commands.argument("value", IntegerArgumentType.integer())
                                        .then(Commands.argument("player", EntityArgument.player())
                                                .executes(context -> {
                                                    int value = IntegerArgumentType.getInteger(context, "value");
                                                    Player player = EntityArgument.getPlayer(context, "player");
                                                    var holder = SkulkHolderAttacher.getSkulkHolderUnwrap(player);
                                                    if (holder != null) {
                                                        holder.setSkulk(value);
                                                    }
                                                    return Command.SINGLE_SUCCESS;
                                                })
                                        )
                                )
                        )
                        .then(Commands.literal("add")
                                .then(Commands.argument("value", IntegerArgumentType.integer())
                                        .then(Commands.argument("player", EntityArgument.player())
                                                .executes(context -> {
                                                    int value = IntegerArgumentType.getInteger(context, "value");
                                                    Player player = EntityArgument.getPlayer(context, "player");
                                                    var holder = SkulkHolderAttacher.getSkulkHolderUnwrap(player);
                                                    if (holder != null) {
                                                        holder.setSkulk(holder.getSkulk() + value);
                                                    }
                                                    return Command.SINGLE_SUCCESS;
                                                })
                                        )
                                )
                        )
                        .then(Commands.literal("infinite")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(context -> {
                                            Player player = EntityArgument.getPlayer(context, "player");
                                            var holder = SkulkHolderAttacher.getSkulkHolderUnwrap(player);
                                            if (holder != null) {
                                                holder.setInfinite(!holder.isInfinite());
                                            }
                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )
                )
        );
    }

    @SubscribeEvent
    public static void projectileHit(ProjectileImpactEvent event) {
        if (event.getEntity().level().isClientSide) {
            return;
        }
        if (event.getRayTraceResult() instanceof EntityHitResult eRes && event.getProjectile() instanceof AbstractArrow arrow && arrow.getOwner() instanceof ServerPlayer player && eRes.getEntity() instanceof Bat bat && bat.isDeadOrDying()) {
            if (MorphHolderAttacher.getCurrentMorph(player).isPresent()) {
                MechaWardenUtils.addToGenericQuestGoal(player, KillBatBowGoal.class);
            }
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getPlayer() instanceof ServerPlayer player) {
            if (event.getState().getBlock() instanceof SpawnerBlock) {
                if (player.serverLevel().dimension() == Level.NETHER) {
                    MechaWardenUtils.addToGenericQuestGoal(player, MineSpawnerNetherGoal.class);
                }
            }
        }
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
        if (event.getEntity() instanceof ServerPlayer player) {
            if (MorphHolderAttacher.getCurrentMorph(player).isPresent()) {
                SkulkHolderAttacher.getSkulkHolder(player).ifPresent(cap -> {
                    if (cap.isMechaMorphed()) {
                        event.setAmount(event.getAmount() / 2);
                    }
                });
            }
        }
    }

    @SubscribeEvent
    public static void onJoinLevel(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof ServerPlayer entity) {
            var morph = MorphHolderAttacher.getCurrentMorphUnwrap(entity);
            if (morph != null) {
                morph.onMorphedTo(entity);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player instanceof ServerPlayer player && event.phase == TickEvent.Phase.END) {
            if (player.getFeetBlockState().is(BlockInit.SPECIAL_SCULK_BLOCK.get())) {
                if (MorphHolderAttacher.getCurrentMorph(player).isEmpty()) {
                    if (!player.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) {
                        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 1));
                    }
                }
            }
            SkulkHolderAttacher.getSkulkHolder(player).ifPresent(cap -> {
                if (cap.getNettedInvulnTicks() > 0) {
                    cap.setNettedInvulnTicks(cap.getNettedInvulnTicks() - 1);
                }
                if (!cap.isInfinite()) {
                    if (player.tickCount % 20 == 0) {
                        cap.setSkulk(cap.getSkulk() + cap.getSkulkRegen());
                    }
                }
                else {
                    if (cap.getSkulk() != cap.getSkulkCap()) {
                        cap.setSkulk(cap.getSkulkCap());
                    }
                }
            });
        }
    }

    @SubscribeEvent
    public static void onCrit(CriticalHitEvent event) {
        if (event.getTarget() instanceof ServerPlayer player) {
            if (player.getFeetBlockState().is(BlockInit.SPECIAL_SCULK_BLOCK.get())) {
                event.setResult(Event.Result.ALLOW);
            }
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
    public static void onEffectGain(MobEffectEvent.Added event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            MobEffectInstance instance = event.getEffectInstance();
            if (instance != null && instance.getEffect() == MobEffects.DOLPHINS_GRACE) {
                MechaWardenUtils.addToGenericQuestGoal(serverPlayer, DolphinGraceEffectGoal.class);
            }
        }
    }

    @SubscribeEvent
    public static void playerLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getEntity().level().isClientSide) return;
        if (event.getHand() == InteractionHand.MAIN_HAND) {
            Player player = event.getEntity();
            ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
            BlockPos pos = event.getPos();
            if (stack.is(ItemInit.BLOCK_MORPH.get())) {
                CompoundTag tag = stack.getOrCreateTag();
                tag.putInt("block", Block.getId(player.level().getBlockState(pos)));
                ((ServerPlayer) player).sendSystemMessage(Component.literal("Selected Block: ").append(Block.stateById(tag.getInt("block")).getBlock().getName()).withStyle(ChatFormatting.GREEN), true);
            }
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
