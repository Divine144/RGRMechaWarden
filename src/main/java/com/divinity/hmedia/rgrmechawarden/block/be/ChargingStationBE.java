package com.divinity.hmedia.rgrmechawarden.block.be;

import com.divinity.hmedia.rgrmechawarden.init.BlockInit;
import com.divinity.hmedia.rgrmechawarden.network.NetworkHandler;
import com.divinity.hmedia.rgrmechawarden.network.clientbound.UpdateChargingStationPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ChargingStationBE extends BlockEntity implements GeoBlockEntity {

    private final AnimatableInstanceCache instanceCache = GeckoLibUtil.createInstanceCache(this);
    private static final RawAnimation WITH_MAGNET_CHARGED = RawAnimation.begin().thenLoop("with_magnet_charged");
    private static final RawAnimation WITH_MAGNET_NOT_CHARGED = RawAnimation.begin().thenLoop("with_magnet_empty");
    private static final RawAnimation NO_MAGNET = RawAnimation.begin().thenLoop("no_magnet");
    private ItemStack storedStack = ItemStack.EMPTY;

    // Client flags
    private boolean isEmpty = true;
    private boolean isCharged = false;

    public ChargingStationBE(BlockPos pPos, BlockState pBlockState) {
        super(BlockInit.CHARGING_STATION_BLOCK_ENTITY.get(), pPos, pBlockState);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag pTag) {
        super.saveAdditional(pTag);
        if (storedStack != ItemStack.EMPTY) {
            pTag.put("storedItem", storedStack.save(new CompoundTag()));
            pTag.putInt("durability", storedStack.getDamageValue());
        }
    }

    @Override
    public void load(@NotNull CompoundTag pTag) {
        super.load(pTag);
        if (pTag.contains("storedItem", 10)) {
            ItemStack itemstack = ItemStack.of(pTag.getCompound("storedItem"));
            if (!itemstack.isEmpty()) {
                itemstack.setDamageValue(pTag.getInt("durability"));
                this.storedStack = itemstack;
            }
        }
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public BlockEntityType<?> getType() {
        return BlockInit.CHARGING_STATION_BLOCK_ENTITY.get();
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, event -> {
            if (event.getData(DataTickets.BLOCK_ENTITY) instanceof ChargingStationBE stationBE) {
                if (stationBE.isEmpty()) {
                    return event.setAndContinue(NO_MAGNET);
                }
                else {
                    if (stationBE.isCharged()) {
                        event.setAndContinue(WITH_MAGNET_CHARGED);
                    }
                    else event.setAndContinue(WITH_MAGNET_NOT_CHARGED);
                }
            }
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return instanceCache;
    }

    public ItemStack getStoredStack() {
        return storedStack;
    }

    public void setStoredStack(ItemStack stack) {
        this.storedStack = stack == null ? ItemStack.EMPTY : stack;
    }

    public void doCharge() {
        if (!this.storedStack.isEmpty()) {
            if (storedStack.isDamaged()) {
                storedStack.setDamageValue(storedStack.getDamageValue() - 1);
                if (!storedStack.isDamaged()) {
                    if (level != null && !level.isClientSide) {
                        setCharged(true);
                        NetworkHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new UpdateChargingStationPacket(this.worldPosition, isEmpty, isCharged));
                    }
                }
            }
        }
    }
    public boolean hasFinishedCharging() {
        return !storedStack.isEmpty() && !storedStack.isDamaged();
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public void setEmpty(boolean empty) {
        isEmpty = empty;
    }

    public boolean isCharged() {
        return isCharged;
    }

    public void setCharged(boolean charged) {
        isCharged = charged;
    }
}
