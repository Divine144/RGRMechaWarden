package com.divinity.hmedia.rgrmechawarden.cap;

import com.divinity.hmedia.rgrmechawarden.network.NetworkHandler;
import dev._100media.capabilitysyncer.core.EntityCapability;
import dev._100media.capabilitysyncer.network.EntityCapabilityStatusPacket;
import dev._100media.capabilitysyncer.network.SimpleEntityCapabilityStatusPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.simple.SimpleChannel;

public class SkulkHolder extends EntityCapability {
    private int skulk;
    private int skulkCap;
    private int skulkRegen = 2;

    protected SkulkHolder(Entity entity) {
        super(entity);
    }

    public int getSkulk() {
        return skulk;
    }

    public void addSkulk(int amount) {
        int old = this.skulk;
        this.skulk += amount;
        if (skulk > skulkCap) skulk = skulkCap;
        else if (skulk < 0) skulk = 0;
        if (skulk != old) updateTracking();
    }

    public void setSkulk(int skulk) {
        int old = this.skulk;
        this.skulk = skulk;
        if (this.skulk > skulkCap) this.skulk = skulkCap;
        else if (this.skulk < 0) this.skulk = 0;
        if (this.skulk != old) updateTracking();
    }

    public int getBaseSkulkRegen() {
        return 2;
    }

    public int getSkulkRegen() {
        return skulkRegen;
    }

    public void setSkulkRegen(int skulkRegen) {
        this.skulkRegen = skulkRegen;
        updateTracking();
    }

    public int getSkulkCap() {
        return skulkCap;
    }

    public void setSkulkCap(int skulkCap) {
        this.skulkCap = skulkCap;
        updateTracking();
    }

    @Override
    public CompoundTag serializeNBT(boolean savingToDisk) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("skulk", this.skulk);
        tag.putInt("skulkRegen", this.skulkRegen);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt, boolean readingFromDisk) {
        this.skulk = nbt.getInt("skulk");
        this.skulkRegen = nbt.getInt("skulkRegen");
    }

    @Override
    public EntityCapabilityStatusPacket createUpdatePacket() {
        return new SimpleEntityCapabilityStatusPacket(this.entity.getId(), SkulkHolderAttacher.EXAMPLE_RL, this);
    }

    @Override
    public SimpleChannel getNetworkChannel() {
        return NetworkHandler.INSTANCE;
    }
}
