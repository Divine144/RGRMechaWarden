package com.divinity.hmedia.rgrmechawarden.network.serverbound;

import com.divinity.hmedia.rgrmechawarden.cap.SkulkHolder;
import com.divinity.hmedia.rgrmechawarden.cap.SkulkHolderAttacher;
import com.divinity.hmedia.rgrmechawarden.init.EffectInit;
import dev._100media.capabilitysyncer.network.IPacket;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

public class EscapeNetPacket implements IPacket {

    public EscapeNetPacket() {
    }

    @Override
    public void write(FriendlyByteBuf packetBuf) {
    }

    public static EscapeNetPacket read(FriendlyByteBuf buffer) {
        return new EscapeNetPacket();
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) {
                return;
            }
            player.removeEffect(EffectInit.NETTED.get());
            SkulkHolderAttacher.getSkulkHolder(player).ifPresent(cap -> cap.setNettedInvulnTicks(60));
        });
        context.setPacketHandled(true);
    }

    public static void register(SimpleChannel channel, int id) {
        IPacket.register(channel, id, NetworkDirection.PLAY_TO_SERVER, EscapeNetPacket.class, EscapeNetPacket::read);
    }
}
