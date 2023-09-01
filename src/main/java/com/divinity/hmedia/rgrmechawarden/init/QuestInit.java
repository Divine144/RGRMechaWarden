package com.divinity.hmedia.rgrmechawarden.init;

import com.divinity.hmedia.rgrmechawarden.RGRMechaWarden;
import com.divinity.hmedia.rgrmechawarden.quest.*;
import dev._100media.hundredmediaquests.init.HMQQuestInit;
import dev._100media.hundredmediaquests.quest.QuestType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class QuestInit {

    public static final DeferredRegister<QuestType<?>> QUESTS = DeferredRegister.create(HMQQuestInit.QUESTS.getRegistryKey(), RGRMechaWarden.MODID);

    public static final RegistryObject<QuestType<?>> MOUNTED_WRIST_ROCKETS = QUESTS.register("mounted_wrist_rockets", () -> QuestType.Builder.of(MountedWristRocketsQuest::new).repeatable(false).instantTurnIn(false).build());
    public static final RegistryObject<QuestType<?>> MECHA_WARDEN_LASER = QUESTS.register("mecha_warden_laser", () -> QuestType.Builder.of(MechaWardenLaserQuest::new).repeatable(false).instantTurnIn(false).build());
    public static final RegistryObject<QuestType<?>> SCULKY_MECHA_MINES = QUESTS.register("sculky_mecha_mines", () -> QuestType.Builder.of(SculkyMechaMinesQuest::new).repeatable(false).instantTurnIn(false).build());
    public static final RegistryObject<QuestType<?>> MECHA_MORPH = QUESTS.register("mecha_morph", () -> QuestType.Builder.of(MechaMorphQuest::new).repeatable(false).instantTurnIn(false).build());
    public static final RegistryObject<QuestType<?>> PORTABLE_TESLA_COIL = QUESTS.register("portable_tesla_coil", () -> QuestType.Builder.of(PortableTeslaCoilQuest::new).repeatable(false).instantTurnIn(false).build());

}
