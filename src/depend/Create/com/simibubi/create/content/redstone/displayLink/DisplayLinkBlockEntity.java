/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dan200.computercraft.api.peripheral.PeripheralCapability
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.NbtUtils
 *  net.minecraft.nbt.Tag
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
 */
package com.simibubi.create.content.redstone.displayLink;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.simibubi.create.api.behaviour.display.DisplayTarget;
import com.simibubi.create.api.contraption.transformable.TransformableBlockEntity;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.compat.Mods;
import com.simibubi.create.compat.computercraft.AbstractComputerBehaviour;
import com.simibubi.create.compat.computercraft.ComputerCraftProxy;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelPosition;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelSupportBehaviour;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlock;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.LinkWithBulbBlockEntity;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import dan200.computercraft.api.peripheral.PeripheralCapability;
import java.util.Iterator;
import java.util.List;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class DisplayLinkBlockEntity
extends LinkWithBulbBlockEntity
implements TransformableBlockEntity {
    protected BlockPos targetOffset = BlockPos.ZERO;
    public DisplaySource activeSource;
    private CompoundTag sourceConfig = new CompoundTag();
    public DisplayTarget activeTarget;
    public int targetLine = 0;
    public int refreshTicks;
    public AbstractComputerBehaviour computerBehaviour;
    public FactoryPanelSupportBehaviour factoryPanelSupport;
    private static final Vec3 bulbOffset = VecHelper.voxelSpace((double)11.0, (double)7.0, (double)5.0);
    private static final Vec3 bulbOffsetVertical = VecHelper.voxelSpace((double)5.0, (double)7.0, (double)11.0);

    public DisplayLinkBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        if (Mods.COMPUTERCRAFT.isLoaded()) {
            event.registerBlockEntity(PeripheralCapability.get(), (BlockEntityType)AllBlockEntityTypes.DISPLAY_LINK.get(), (be, context) -> be.computerBehaviour.getPeripheralCapability());
        }
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        this.computerBehaviour = ComputerCraftProxy.behaviour(this);
        behaviours.add(this.computerBehaviour);
        this.factoryPanelSupport = new FactoryPanelSupportBehaviour(this, () -> false, () -> false, () -> this.updateGatheredData());
        behaviours.add(this.factoryPanelSupport);
        this.registerAwardables(behaviours, AllAdvancements.DISPLAY_LINK, AllAdvancements.DISPLAY_BOARD);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isVirtual()) {
            return;
        }
        if (this.activeSource == null) {
            return;
        }
        if (this.level.isClientSide) {
            return;
        }
        ++this.refreshTicks;
        if (this.refreshTicks < this.activeSource.getPassiveRefreshTicks() || !this.activeSource.shouldPassiveReset()) {
            return;
        }
        this.tickSource();
    }

    public void tickSource() {
        this.refreshTicks = 0;
        if (this.getBlockState().getOptionalValue((Property)DisplayLinkBlock.POWERED).orElse(true).booleanValue()) {
            return;
        }
        if (!this.level.isClientSide) {
            this.updateGatheredData();
        }
    }

    public void onNoLongerPowered() {
        if (this.activeSource == null) {
            return;
        }
        this.refreshTicks = 0;
        this.activeSource.onSignalReset(new DisplayLinkContext(this.level, this));
        this.updateGatheredData();
    }

    public void updateGatheredData() {
        BlockPos sourcePosition = this.getSourcePosition();
        BlockPos targetPosition = this.getTargetPosition();
        if (!this.level.isLoaded(targetPosition) || !this.level.isLoaded(sourcePosition)) {
            return;
        }
        DisplayTarget target = DisplayTarget.get((LevelAccessor)this.level, targetPosition);
        List<DisplaySource> sources = DisplaySource.getAll((LevelAccessor)this.level, sourcePosition);
        boolean notify = false;
        if (this.activeTarget != target) {
            this.activeTarget = target;
            notify = true;
        }
        if (this.activeSource != null && !sources.contains(this.activeSource)) {
            this.activeSource = null;
            this.sourceConfig = new CompoundTag();
            notify = true;
        }
        if (notify) {
            this.notifyUpdate();
        }
        if (this.activeSource == null || this.activeTarget == null) {
            return;
        }
        DisplayLinkContext context = new DisplayLinkContext(this.level, this);
        this.activeSource.transferData(context, this.activeTarget, this.targetLine);
        this.sendPulseNextSync();
        this.sendData();
        this.award(AllAdvancements.DISPLAY_LINK);
    }

    @Override
    public void writeSafe(CompoundTag tag, HolderLookup.Provider registries) {
        super.writeSafe(tag, registries);
        this.writeGatheredData(tag);
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        ResourceLocation id;
        super.write(tag, registries, clientPacket);
        this.writeGatheredData(tag);
        if (clientPacket && this.activeTarget != null && (id = CreateBuiltInRegistries.DISPLAY_TARGET.getKey((Object)this.activeTarget)) != null) {
            tag.putString("TargetType", id.toString());
        }
    }

    private void writeGatheredData(CompoundTag tag) {
        tag.put("TargetOffset", NbtUtils.writeBlockPos((BlockPos)this.targetOffset));
        tag.putInt("TargetLine", this.targetLine);
        if (this.activeSource != null) {
            CompoundTag data = this.sourceConfig.copy();
            ResourceLocation id = CreateBuiltInRegistries.DISPLAY_SOURCE.getKey((Object)this.activeSource);
            if (id != null) {
                data.putString("Id", id.toString());
            }
            tag.put("Source", (Tag)data);
        }
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        this.targetOffset = NBTHelper.readBlockPos((CompoundTag)tag, (String)"TargetOffset");
        this.targetLine = tag.getInt("TargetLine");
        if (clientPacket && tag.contains("TargetType")) {
            this.activeTarget = DisplayTarget.get(ResourceLocation.tryParse((String)tag.getString("TargetType")));
        }
        if (!tag.contains("Source")) {
            return;
        }
        CompoundTag data = tag.getCompound("Source");
        this.activeSource = DisplaySource.get(ResourceLocation.tryParse((String)data.getString("Id")));
        this.sourceConfig = new CompoundTag();
        if (this.activeSource != null) {
            this.sourceConfig = data.copy();
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        this.computerBehaviour.removePeripheral();
    }

    public void target(BlockPos targetPosition) {
        this.targetOffset = targetPosition.subtract((Vec3i)this.worldPosition);
    }

    public BlockPos getSourcePosition() {
        Iterator<FactoryPanelPosition> iterator = this.factoryPanelSupport.getLinkedPanels().iterator();
        if (iterator.hasNext()) {
            FactoryPanelPosition position = iterator.next();
            return position.pos();
        }
        return this.worldPosition.relative(this.getDirection());
    }

    public CompoundTag getSourceConfig() {
        return this.sourceConfig;
    }

    public void setSourceConfig(CompoundTag sourceConfig) {
        this.sourceConfig = sourceConfig;
    }

    public Direction getDirection() {
        return this.getBlockState().getOptionalValue((Property)DisplayLinkBlock.FACING).orElse(Direction.UP).getOpposite();
    }

    public BlockPos getTargetPosition() {
        return this.worldPosition.offset((Vec3i)this.targetOffset);
    }

    @Override
    public Vec3 getBulbOffset(BlockState state) {
        if (state.getOptionalValue((Property)DisplayLinkBlock.FACING).orElse(Direction.UP).getAxis().isVertical()) {
            return bulbOffsetVertical;
        }
        return bulbOffset;
    }

    @Override
    public void transform(BlockEntity be, StructureTransform transform) {
        this.targetOffset = transform.applyWithoutOffset(this.targetOffset);
        this.notifyUpdate();
    }
}
