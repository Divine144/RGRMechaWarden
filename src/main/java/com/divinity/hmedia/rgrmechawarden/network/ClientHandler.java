package com.divinity.hmedia.rgrmechawarden.network;

import com.divinity.hmedia.rgrmechawarden.block.be.ChargingStationBE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ClientHandler {

    public static void syncChargingStation(BlockPos pos, boolean isEmpty, boolean isCharged) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level != null) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof ChargingStationBE be) {
                be.setEmpty(isEmpty);
                be.setCharged(isCharged);
            }
        }
    }
}
