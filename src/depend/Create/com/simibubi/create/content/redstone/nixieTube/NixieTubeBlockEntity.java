/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dan200.computercraft.api.peripheral.PeripheralCapability
 *  net.createmod.catnip.data.Couple
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.redstone.nixieTube;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.compat.Mods;
import com.simibubi.create.compat.computercraft.AbstractComputerBehaviour;
import com.simibubi.create.compat.computercraft.ComputerCraftProxy;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlock;
import com.simibubi.create.content.redstone.nixieTube.NixieTubeBlock;
import com.simibubi.create.content.trains.signal.SignalBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.DynamicComponent;
import dan200.computercraft.api.peripheral.PeripheralCapability;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Optional;
import net.createmod.catnip.data.Couple;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NixieTubeBlockEntity
extends SmartBlockEntity {
    private static final Couple<String> EMPTY = Couple.create((Object)"", (Object)"");
    private static final String EMPTY_COMPONENT_JSON = "\"\"";
    private int redstoneStrength = 0;
    private Optional<DynamicComponent> customText = Optional.empty();
    private int nixieIndex;
    private Couple<String> displayedStrings;
    public AbstractComputerBehaviour computerBehaviour;
    private WeakReference<SignalBlockEntity> cachedSignalTE = new WeakReference<Object>(null);
    @Nullable
    public SignalBlockEntity.SignalState signalState;
    @Nullable
    public ComputerSignal computerSignal;

    public NixieTubeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        if (Mods.COMPUTERCRAFT.isLoaded()) {
            event.registerBlockEntity(PeripheralCapability.get(), (BlockEntityType)AllBlockEntityTypes.NIXIE_TUBE.get(), (be, context) -> be.computerBehaviour.getPeripheralCapability());
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide) {
            return;
        }
        this.signalState = null;
        if (this.computerBehaviour.hasAttachedComputer()) {
            if (this.level.isClientSide && this.cachedSignalTE.get() != null) {
                this.cachedSignalTE = new WeakReference<Object>(null);
            }
            return;
        }
        this.computerSignal = null;
        SignalBlockEntity signalBlockEntity = (SignalBlockEntity)this.cachedSignalTE.get();
        if (signalBlockEntity == null || signalBlockEntity.isRemoved()) {
            Direction facing = NixieTubeBlock.getFacing(this.getBlockState());
            BlockEntity blockEntity = this.level.getBlockEntity(this.worldPosition.relative(facing.getOpposite()));
            if (blockEntity instanceof SignalBlockEntity) {
                SignalBlockEntity signal = (SignalBlockEntity)blockEntity;
                this.signalState = signal.getState();
                this.cachedSignalTE = new WeakReference<SignalBlockEntity>(signal);
            }
            return;
        }
        this.signalState = signalBlockEntity.getState();
    }

    @Override
    public void initialize() {
        if (this.level.isClientSide) {
            this.updateDisplayedStrings();
        }
    }

    public boolean reactsToRedstone() {
        return !this.computerBehaviour.hasAttachedComputer() && this.customText.isEmpty();
    }

    public Couple<String> getDisplayedStrings() {
        if (this.displayedStrings == null) {
            return EMPTY;
        }
        return this.displayedStrings;
    }

    public MutableComponent getFullText() {
        return this.customText.map(DynamicComponent::get).orElse(Component.literal((String)("" + this.redstoneStrength)));
    }

    public void updateRedstoneStrength(int signalStrength) {
        this.clearCustomText();
        this.redstoneStrength = signalStrength;
        DisplayLinkBlock.notifyGatherers((LevelAccessor)this.level, this.worldPosition);
        this.notifyUpdate();
    }

    public void displayCustomText(String tagElement, int nixiePositionInRow) {
        if (tagElement == null) {
            return;
        }
        if (this.customText.filter(d -> d.sameAs(tagElement)).isPresent()) {
            return;
        }
        DynamicComponent component = this.customText.orElseGet(DynamicComponent::new);
        component.displayCustomText(this.level, this.worldPosition, tagElement);
        this.customText = Optional.of(component);
        this.nixieIndex = nixiePositionInRow;
        DisplayLinkBlock.notifyGatherers((LevelAccessor)this.level, this.worldPosition);
        this.notifyUpdate();
    }

    public void displayEmptyText(int nixiePositionInRow) {
        this.displayCustomText(EMPTY_COMPONENT_JSON, nixiePositionInRow);
    }

    public void updateDisplayedStrings() {
        if (this.signalState != null || this.computerSignal != null) {
            return;
        }
        this.customText.map(DynamicComponent::resolve).ifPresentOrElse(fullText -> {
            this.displayedStrings = Couple.create((Object)this.charOrEmpty((String)fullText, this.nixieIndex * 2), (Object)this.charOrEmpty((String)fullText, this.nixieIndex * 2 + 1));
        }, () -> {
            this.displayedStrings = Couple.create((Object)(this.redstoneStrength < 10 ? "0" : "1"), (Object)String.valueOf(this.redstoneStrength % 10));
        });
    }

    public void clearCustomText() {
        this.nixieIndex = 0;
        this.customText = Optional.empty();
    }

    public int getRedstoneStrength() {
        return this.redstoneStrength;
    }

    @Override
    protected void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(nbt, registries, clientPacket);
        if (nbt.contains("CustomText")) {
            DynamicComponent component = this.customText.orElseGet(DynamicComponent::new);
            component.read(this.worldPosition, nbt, registries);
            if (component.isValid()) {
                this.customText = Optional.of(component);
                this.nixieIndex = nbt.getInt("CustomTextIndex");
            } else {
                this.customText = Optional.empty();
                this.nixieIndex = 0;
            }
        } else {
            this.customText = Optional.empty();
            this.nixieIndex = 0;
        }
        if (this.customText.isEmpty()) {
            this.redstoneStrength = nbt.getInt("RedstoneStrength");
        }
        if (clientPacket || this.isVirtual()) {
            if (nbt.contains("ComputerSignal")) {
                byte[] encodedComputerSignal = nbt.getByteArray("ComputerSignal");
                if (this.computerSignal == null) {
                    this.computerSignal = new ComputerSignal();
                }
                this.computerSignal.decode(encodedComputerSignal);
            } else {
                this.computerSignal = null;
            }
            this.updateDisplayedStrings();
        }
    }

    @Override
    protected void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(nbt, registries, clientPacket);
        if (this.customText.isPresent()) {
            nbt.putInt("CustomTextIndex", this.nixieIndex);
            this.customText.get().write(nbt, registries);
        } else {
            nbt.putInt("RedstoneStrength", this.redstoneStrength);
        }
        if (clientPacket && this.computerSignal != null) {
            nbt.putByteArray("ComputerSignal", this.computerSignal.encode());
        }
    }

    private String charOrEmpty(String string, int index) {
        return string.length() <= index ? " " : string.substring(index, index + 1);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        this.computerBehaviour = ComputerCraftProxy.behaviour(this);
        behaviours.add(this.computerBehaviour);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        this.computerBehaviour.removePeripheral();
    }

    public static final class ComputerSignal {
        @NotNull
        public TubeDisplay first = new TubeDisplay();
        @NotNull
        public TubeDisplay second = new TubeDisplay();

        public void decode(byte[] encoded) {
            this.first.decode(encoded, 0);
            this.second.decode(encoded, 7);
        }

        public byte[] encode() {
            byte[] encoded = new byte[14];
            this.first.encode(encoded, 0);
            this.second.encode(encoded, 7);
            return encoded;
        }

        public static final class TubeDisplay {
            public static final int ENCODED_SIZE = 7;
            public byte r = (byte)63;
            public byte g = (byte)63;
            public byte b = (byte)63;
            public byte blinkPeriod = 0;
            public byte blinkOffTime = 0;
            public byte glowWidth = 1;
            public byte glowHeight = 1;

            public void decode(byte[] data, int offset) {
                this.r = data[offset];
                this.g = data[offset + 1];
                this.b = data[offset + 2];
                this.blinkPeriod = data[offset + 3];
                this.blinkOffTime = data[offset + 4];
                this.glowWidth = data[offset + 5];
                this.glowHeight = data[offset + 6];
            }

            public void encode(byte[] data, int offset) {
                data[offset] = this.r;
                data[offset + 1] = this.g;
                data[offset + 2] = this.b;
                data[offset + 3] = this.blinkPeriod;
                data[offset + 4] = this.blinkOffTime;
                data[offset + 5] = this.glowWidth;
                data[offset + 6] = this.glowHeight;
            }
        }
    }
}
