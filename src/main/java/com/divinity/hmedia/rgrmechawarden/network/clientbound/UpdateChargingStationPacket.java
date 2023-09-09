package com.divinity.hmedia.rgrmechawarden.network.clientbound;

import com.divinity.hmedia.rgrmechawarden.network.ClientHandler;
import dev._100media.capabilitysyncer.network.IPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

public record UpdateChargingStationPacket(BlockPos pos, boolean isEmpty, boolean isCharged) implements IPacket {

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> ClientHandler.syncChargingStation(pos, isEmpty, isCharged));
    }

    @Override
    public void write(FriendlyByteBuf packetBuf) {
        packetBuf.writeBlockPos(pos);
        packetBuf.writeBoolean(isEmpty);
        packetBuf.writeBoolean(isCharged);
    }

    public static UpdateChargingStationPacket read(FriendlyByteBuf buf) {
        return new UpdateChargingStationPacket(buf.readBlockPos(), buf.readBoolean(), buf.readBoolean());
    }

    public static void register(SimpleChannel channel, int id) {
        IPacket.register(channel, id, NetworkDirection.PLAY_TO_CLIENT, UpdateChargingStationPacket.class, UpdateChargingStationPacket::read);
    }
}
