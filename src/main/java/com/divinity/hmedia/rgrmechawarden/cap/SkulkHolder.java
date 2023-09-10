package com.divinity.hmedia.rgrmechawarden.cap;

import com.divinity.hmedia.rgrmechawarden.network.NetworkHandler;
import dev._100media.capabilitysyncer.core.EntityCapability;
import dev._100media.capabilitysyncer.network.EntityCapabilityStatusPacket;
import dev._100media.capabilitysyncer.network.SimpleEntityCapabilityStatusPacket;
import dev._100media.hundredmediamorphs.capability.MorphHolderAttacher;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.simple.SimpleChannel;

public class SkulkHolder extends EntityCapability {
    private int skulk;
    private int skulkCap = 1;
    private int skulkRegen;
    private int nettedInvulnTicks = 0;
    private boolean isMechaMorphed = false;
    private Block camouflagedBlock = Blocks.AIR;
    private boolean isMechaBoard = false;
    private boolean hasLost = false;
    private boolean coolDownsReduced = false;
    private boolean isInfinite = false;

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

    public boolean removeSkulk(int skulk) {
        if (this.isInfinite) return true;
        int old = this.skulk;
        if (this.skulk - skulk < 0) {
            return false;
        }
        this.skulk -= skulk;
        if (this.skulk != old) updateTracking();
        return true;
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

    public boolean isMechaMorphed() {
        return isMechaMorphed;
    }

    public void setMechaMorphed(boolean mechaMorphed) {
        isMechaMorphed = mechaMorphed;
        if (!entity.level().isClientSide) {
            if (mechaMorphed) {
                setCurrentSize(EntityDimensions.scalable(0.65f, 0.65f));
            }
            else resetCurrentSize();
        }
        updateTracking();
    }

    public Block getCamouflagedBlock() {
        return camouflagedBlock;
    }

    public void setCamouflagedBlock(Block camouflagedBlock) {
        this.camouflagedBlock = camouflagedBlock;
        if (!entity.level().isClientSide) {
            if (camouflagedBlock != Blocks.AIR) {
                setCurrentSize(EntityDimensions.scalable(0.65f, 0.65f));
            }
            else if (!this.isMechaMorphed) resetCurrentSize();
        }
        updateTracking();
    }

    public boolean isMechaBoard() {
        return isMechaBoard;
    }

    public void setMechaBoard(boolean mechaBoard) {
        isMechaBoard = mechaBoard;
        updateTracking();
    }

    public int getNettedInvulnTicks() {
        return nettedInvulnTicks;
    }

    public void setNettedInvulnTicks(int nettedInvulnTicks) {
        int temp = this.nettedInvulnTicks;
        this.nettedInvulnTicks = nettedInvulnTicks;
        if (this.nettedInvulnTicks < 0) this.nettedInvulnTicks = 0;
        if (temp != this.nettedInvulnTicks) updateTracking();
    }

    public boolean isHasLost() {
        return hasLost;
    }

    public void setHasLost(boolean hasLost) {
        this.hasLost = hasLost;
    }

    public boolean isCoolDownsReduced() {
        return coolDownsReduced;
    }

    public void setCoolDownsReduced(boolean coolDownsReduced) {
        this.coolDownsReduced = coolDownsReduced;
        updateTracking();
    }

    public void resetCurrentSize() {
        if (this.entity instanceof ServerPlayer player) {
            var holder = MorphHolderAttacher.getMorphHolderUnwrap(player);
            if (holder != null) {
                holder.setDimensionsOverride(null, true);
            }
        }
    }

    public void setCurrentSize(EntityDimensions dimensions) {
        if (entity instanceof ServerPlayer player) {
            var holder = MorphHolderAttacher.getMorphHolderUnwrap(player);
            if (holder != null) {
                holder.setDimensionsOverride(dimensions, true);
            }
        }
    }

    public boolean isInfinite() {
        return isInfinite;
    }

    public void setInfinite(boolean infinite) {
        isInfinite = infinite;
        updateTracking();
    }

    @Override
    public CompoundTag serializeNBT(boolean savingToDisk) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("skulk", this.skulk);
        tag.putInt("skulkRegen", this.skulkRegen);
        tag.putInt("skulkCap", this.skulkCap);
        tag.putBoolean("isMechaMorphed", this.isMechaMorphed);
        tag.putInt("camouflagedBlock", Block.getId(this.camouflagedBlock.defaultBlockState()));
        tag.putBoolean("isMechaBoard", this.isMechaBoard);
        tag.putInt("nettedTicks", this.nettedInvulnTicks);
        tag.putBoolean("hasLost", this.hasLost);
        tag.putBoolean("reducedCooldown", this.coolDownsReduced);
        tag.putBoolean("infinite", this.isInfinite);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt, boolean readingFromDisk) {
        this.skulk = nbt.getInt("skulk");
        this.skulkRegen = nbt.getInt("skulkRegen");
        this.skulkCap = nbt.getInt("skulkCap");
        this.isMechaMorphed = nbt.getBoolean("isMechaMorphed");
        this.camouflagedBlock = Block.stateById(nbt.getInt("camouflagedBlock")).getBlock();
        this.isMechaBoard = nbt.getBoolean("isMechaBoard");
        this.nettedInvulnTicks = nbt.getInt("nettedTicks");
        this.hasLost = nbt.getBoolean("hasLost");
        this.coolDownsReduced = nbt.getBoolean("reducedCooldown");
        this.isInfinite = nbt.getBoolean("infinite");
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
