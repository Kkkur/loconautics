/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.SoundType
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.Shapes
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.logistics.factoryBoard;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelPosition;
import com.simibubi.create.content.logistics.packager.PackagerBlockEntity;
import com.simibubi.create.content.logistics.packager.repackager.RepackagerBlockEntity;
import com.simibubi.create.foundation.advancement.AdvancementBehaviour;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import java.util.EnumMap;
import java.util.List;
import java.util.UUID;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class FactoryPanelBlockEntity
extends SmartBlockEntity {
    public EnumMap<FactoryPanelBlock.PanelSlot, FactoryPanelBehaviour> panels;
    public boolean redraw;
    public boolean restocker = false;
    public VoxelShape lastShape;
    public AdvancementBehaviour advancements;

    public FactoryPanelBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected AABB createRenderBoundingBox() {
        return new AABB(this.worldPosition).inflate(8.0);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        this.panels = new EnumMap(FactoryPanelBlock.PanelSlot.class);
        this.redraw = true;
        for (FactoryPanelBlock.PanelSlot slot : FactoryPanelBlock.PanelSlot.values()) {
            FactoryPanelBehaviour e = new FactoryPanelBehaviour(this, slot);
            this.panels.put(slot, e);
            behaviours.add(e);
        }
        this.advancements = new AdvancementBehaviour(this, AllAdvancements.FACTORY_GAUGE);
        behaviours.add(this.advancements);
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        if (this.level.isClientSide()) {
            return;
        }
        if (this.activePanels() == 0) {
            this.level.setBlockAndUpdate(this.worldPosition, Blocks.AIR.defaultBlockState());
        }
        if (AllBlocks.FACTORY_GAUGE.has(this.getBlockState())) {
            boolean shouldBeRestocker = AllBlocks.PACKAGER.has(this.level.getBlockState(this.worldPosition.relative(FactoryPanelBlock.connectedDirection(this.getBlockState()).getOpposite())));
            if (this.restocker == shouldBeRestocker) {
                return;
            }
            this.restocker = shouldBeRestocker;
            this.redraw = true;
            this.sendData();
        }
    }

    @Nullable
    public PackagerBlockEntity getRestockedPackager() {
        BlockState state = this.getBlockState();
        if (!this.restocker || !AllBlocks.FACTORY_GAUGE.has(state)) {
            return null;
        }
        BlockPos packagerPos = this.worldPosition.relative(FactoryPanelBlock.connectedDirection(state).getOpposite());
        if (!this.level.isLoaded(packagerPos)) {
            return null;
        }
        BlockEntity be = this.level.getBlockEntity(packagerPos);
        if (be == null || !(be instanceof PackagerBlockEntity)) {
            return null;
        }
        PackagerBlockEntity pbe = (PackagerBlockEntity)be;
        if (pbe instanceof RepackagerBlockEntity) {
            return null;
        }
        return pbe;
    }

    public int activePanels() {
        int result = 0;
        for (FactoryPanelBehaviour panelBehaviour : this.panels.values()) {
            if (!panelBehaviour.isActive()) continue;
            ++result;
        }
        return result;
    }

    @Override
    public void remove() {
        for (FactoryPanelBehaviour panelBehaviour : this.panels.values()) {
            if (!panelBehaviour.isActive()) continue;
            panelBehaviour.disconnectAll();
        }
        super.remove();
    }

    @Override
    public void destroy() {
        super.destroy();
        int panelCount = this.activePanels();
        if (panelCount > 1) {
            Block.popResource((Level)this.level, (BlockPos)this.worldPosition, (ItemStack)AllBlocks.FACTORY_GAUGE.asStack(panelCount - 1));
        }
    }

    public boolean addPanel(FactoryPanelBlock.PanelSlot slot, UUID frequency) {
        FactoryPanelBehaviour behaviour = this.panels.get((Object)slot);
        if (behaviour != null && !behaviour.isActive()) {
            behaviour.enable();
            if (frequency != null) {
                behaviour.setNetwork(frequency);
            }
            this.redraw = true;
            this.lastShape = null;
            if (this.activePanels() > 1) {
                SoundType soundType = this.getBlockState().getSoundType();
                this.level.playSound(null, this.worldPosition, soundType.getPlaceSound(), SoundSource.BLOCKS, (soundType.getVolume() + 1.0f) / 2.0f, soundType.getPitch() * 0.8f);
            }
            return true;
        }
        return false;
    }

    public boolean removePanel(FactoryPanelBlock.PanelSlot slot) {
        FactoryPanelBehaviour behaviour = this.panels.get((Object)slot);
        if (behaviour != null && behaviour.isActive()) {
            behaviour.disable();
            this.redraw = true;
            this.lastShape = null;
            if (this.activePanels() > 0) {
                SoundType soundType = this.getBlockState().getSoundType();
                this.level.playSound(null, this.worldPosition, soundType.getBreakSound(), SoundSource.BLOCKS, (soundType.getVolume() + 1.0f) / 2.0f, soundType.getPitch() * 0.8f);
            }
            return true;
        }
        return false;
    }

    public VoxelShape getShape() {
        if (this.lastShape != null) {
            return this.lastShape;
        }
        float xRot = 57.295776f * FactoryPanelBlock.getXRot(this.getBlockState()) + 90.0f;
        float yRot = 57.295776f * FactoryPanelBlock.getYRot(this.getBlockState());
        Direction connectedDirection = FactoryPanelBlock.connectedDirection(this.getBlockState());
        Vec3 inflateAxes = VecHelper.axisAlingedPlaneOf((Direction)connectedDirection);
        this.lastShape = Shapes.empty();
        for (FactoryPanelBehaviour behaviour : this.panels.values()) {
            if (!behaviour.isActive()) continue;
            FactoryPanelPosition panelPosition = behaviour.getPanelPosition();
            Vec3 vec = new Vec3(0.25 + (double)panelPosition.slot().xOffset * 0.5, 0.0625, 0.25 + (double)panelPosition.slot().yOffset * 0.5);
            vec = VecHelper.rotateCentered((Vec3)vec, (double)180.0, (Direction.Axis)Direction.Axis.Y);
            vec = VecHelper.rotateCentered((Vec3)vec, (double)xRot, (Direction.Axis)Direction.Axis.X);
            vec = VecHelper.rotateCentered((Vec3)vec, (double)yRot, (Direction.Axis)Direction.Axis.Y);
            AABB bb = new AABB(vec, vec).inflate(0.0625).inflate(inflateAxes.x * 3.0 / 16.0, inflateAxes.y * 3.0 / 16.0, inflateAxes.z * 3.0 / 16.0);
            this.lastShape = Shapes.or((VoxelShape)this.lastShape, (VoxelShape)Shapes.create((AABB)bb));
        }
        return this.lastShape;
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        this.restocker = tag.getBoolean("Restocker");
        if (clientPacket && tag.contains("Redraw")) {
            this.lastShape = null;
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 16);
        }
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.putBoolean("Restocker", this.restocker);
        if (clientPacket && this.redraw) {
            NBTHelper.putMarker((CompoundTag)tag, (String)"Redraw");
            this.redraw = false;
        }
    }
}
