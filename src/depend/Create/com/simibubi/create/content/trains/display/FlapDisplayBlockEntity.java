/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.Mth
 *  net.minecraft.world.item.DyeColor
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.AABB
 */
package com.simibubi.create.content.trains.display;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.trains.display.FlapDisplayBlock;
import com.simibubi.create.content.trains.display.FlapDisplayLayout;
import com.simibubi.create.content.trains.display.FlapDisplaySection;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.DyeHelper;
import com.simibubi.create.foundation.utility.DynamicComponent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;

public class FlapDisplayBlockEntity
extends KineticBlockEntity {
    public List<FlapDisplayLayout> lines;
    public boolean isController;
    public boolean isRunning;
    public int xSize;
    public int ySize;
    public DyeColor[] colour;
    public boolean[] glowingLines;
    public boolean[] manualLines;

    public FlapDisplayBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.setLazyTickRate(10);
        this.isController = false;
        this.xSize = 1;
        this.ySize = 1;
        this.colour = new DyeColor[2];
        this.manualLines = new boolean[2];
        this.glowingLines = new boolean[2];
    }

    @Override
    public void initialize() {
        super.initialize();
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        this.updateControllerStatus();
    }

    public void updateControllerStatus() {
        if (this.level.isClientSide) {
            return;
        }
        BlockState blockState = this.getBlockState();
        if (!(blockState.getBlock() instanceof FlapDisplayBlock)) {
            return;
        }
        Direction leftDirection = ((Direction)blockState.getValue(FlapDisplayBlock.HORIZONTAL_FACING)).getClockWise();
        boolean shouldBeController = (Boolean)blockState.getValue((Property)FlapDisplayBlock.UP) == false && this.level.getBlockState(this.worldPosition.relative(leftDirection)) != blockState;
        int newXSize = 1;
        int newYSize = 1;
        if (shouldBeController) {
            for (int xOffset = 1; xOffset < 32 && this.level.getBlockState(this.worldPosition.relative(leftDirection.getOpposite(), xOffset)) == blockState; ++xOffset) {
                ++newXSize;
            }
            for (int yOffset = 0; yOffset < 32 && this.level.getBlockState(this.worldPosition.relative(Direction.DOWN, yOffset)).getOptionalValue((Property)FlapDisplayBlock.DOWN).orElse(false).booleanValue(); ++yOffset) {
                ++newYSize;
            }
        }
        if (this.isController == shouldBeController && newXSize == this.xSize && newYSize == this.ySize) {
            return;
        }
        this.isController = shouldBeController;
        this.xSize = newXSize;
        this.ySize = newYSize;
        this.colour = Arrays.copyOf(this.colour, this.ySize * 2);
        this.glowingLines = Arrays.copyOf(this.glowingLines, this.ySize * 2);
        this.manualLines = new boolean[this.ySize * 2];
        this.lines = null;
        this.sendData();
    }

    @Override
    public void tick() {
        super.tick();
        this.isRunning = super.isSpeedRequirementFulfilled();
        if (!(this.level.isClientSide && this.isRunning || this.isVirtual())) {
            return;
        }
        int activeFlaps = 0;
        boolean instant = Math.abs(this.getSpeed()) > 128.0f;
        for (FlapDisplayLayout line : this.lines) {
            for (FlapDisplaySection section : line.getSections()) {
                activeFlaps += section.tick(instant, this.level.random);
            }
        }
        if (activeFlaps == 0) {
            return;
        }
        float volume = Mth.clamp((float)((float)activeFlaps / 20.0f), (float)0.25f, (float)1.5f);
        float bgVolume = Mth.clamp((float)((float)activeFlaps / 40.0f), (float)0.25f, (float)1.0f);
        BlockPos middle = this.worldPosition.relative(this.getDirection().getClockWise(), this.xSize / 2).relative(Direction.DOWN, this.ySize / 2);
        AllSoundEvents.SCROLL_VALUE.playAt(this.level, (Vec3i)middle, volume, 0.56f, false);
        this.level.playLocalSound((double)middle.getX(), (double)middle.getY(), (double)middle.getZ(), SoundEvents.CALCITE_HIT, SoundSource.BLOCKS, 0.35f * bgVolume, 1.95f, false);
    }

    @Override
    protected boolean isNoisy() {
        return false;
    }

    @Override
    public boolean isSpeedRequirementFulfilled() {
        return this.isRunning;
    }

    public void applyTextManually(int lineIndex, Component componentText) {
        List<FlapDisplayLayout> lines = this.getLines();
        if (lineIndex >= lines.size()) {
            return;
        }
        FlapDisplayLayout layout = lines.get(lineIndex);
        if (!layout.isLayout("Default")) {
            layout.loadDefault(this.getMaxCharCount());
        }
        List<FlapDisplaySection> sections = layout.getSections();
        FlapDisplaySection flapDisplaySection = sections.get(0);
        if (componentText == null) {
            this.manualLines[lineIndex] = false;
            flapDisplaySection.setText(CommonComponents.EMPTY);
            this.notifyUpdate();
            return;
        }
        this.manualLines[lineIndex] = true;
        Component text = this.isVirtual() ? componentText : DynamicComponent.parseCustomText(this.level, this.worldPosition, componentText);
        flapDisplaySection.setText(text);
        if (this.isVirtual()) {
            flapDisplaySection.refresh(true);
        } else {
            this.notifyUpdate();
        }
    }

    public void setColour(int lineIndex, DyeColor color) {
        this.colour[lineIndex] = color == DyeColor.WHITE ? null : color;
        this.notifyUpdate();
    }

    public void setGlowing(int lineIndex) {
        this.glowingLines[lineIndex] = true;
        this.notifyUpdate();
    }

    public List<FlapDisplayLayout> getLines() {
        if (this.lines == null) {
            this.initDefaultSections();
        }
        return this.lines;
    }

    public void initDefaultSections() {
        this.lines = new ArrayList<FlapDisplayLayout>();
        for (int i = 0; i < this.ySize * 2; ++i) {
            this.lines.add(new FlapDisplayLayout(this.getMaxCharCount()));
        }
    }

    public int getMaxCharCount() {
        return this.getMaxCharCount(0);
    }

    public int getMaxCharCount(int gaps) {
        return (int)(((float)this.xSize * 16.0f - 2.0f - 4.0f * (float)gaps) / 3.5f);
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        int j;
        super.write(tag, registries, clientPacket);
        tag.putBoolean("Controller", this.isController);
        tag.putInt("XSize", this.xSize);
        tag.putInt("YSize", this.ySize);
        for (j = 0; j < this.manualLines.length; ++j) {
            if (!this.manualLines[j]) continue;
            NBTHelper.putMarker((CompoundTag)tag, (String)("CustomLine" + j));
        }
        for (j = 0; j < this.glowingLines.length; ++j) {
            if (!this.glowingLines[j]) continue;
            NBTHelper.putMarker((CompoundTag)tag, (String)("GlowingLine" + j));
        }
        for (j = 0; j < this.colour.length; ++j) {
            if (this.colour[j] == null) continue;
            NBTHelper.writeEnum((CompoundTag)tag, (String)("Dye" + j), (Enum)this.colour[j]);
        }
        List<FlapDisplayLayout> lines = this.getLines();
        for (int i = 0; i < lines.size(); ++i) {
            tag.put("Display" + i, (Tag)lines.get(i).write(registries));
        }
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        int i;
        super.read(tag, registries, clientPacket);
        boolean wasActive = this.isController;
        int prevX = this.xSize;
        int prevY = this.ySize;
        this.isController = tag.getBoolean("Controller");
        this.xSize = tag.getInt("XSize");
        this.ySize = tag.getInt("YSize");
        this.manualLines = new boolean[this.ySize * 2];
        for (i = 0; i < this.ySize * 2; ++i) {
            this.manualLines[i] = tag.contains("CustomLine" + i);
        }
        this.glowingLines = new boolean[this.ySize * 2];
        for (i = 0; i < this.ySize * 2; ++i) {
            this.glowingLines[i] = tag.contains("GlowingLine" + i);
        }
        this.colour = new DyeColor[this.ySize * 2];
        for (i = 0; i < this.ySize * 2; ++i) {
            this.colour[i] = tag.contains("Dye" + i) ? (DyeColor)NBTHelper.readEnum((CompoundTag)tag, (String)("Dye" + i), DyeColor.class) : null;
        }
        if (clientPacket && wasActive != this.isController || prevX != this.xSize || prevY != this.ySize) {
            this.invalidateRenderBoundingBox();
            this.lines = null;
        }
        List<FlapDisplayLayout> lines = this.getLines();
        for (int i2 = 0; i2 < lines.size(); ++i2) {
            lines.get(i2).read(tag.getCompound("Display" + i2), registries);
        }
    }

    public int getLineIndexAt(double yCoord) {
        return (int)Mth.clamp((double)Math.floor(2.0 * ((double)this.worldPosition.getY() - yCoord + 1.0)), (double)0.0, (double)(this.ySize * 2));
    }

    public FlapDisplayBlockEntity getController() {
        if (this.isController) {
            return this;
        }
        BlockState blockState = this.getBlockState();
        if (!(blockState.getBlock() instanceof FlapDisplayBlock)) {
            return null;
        }
        BlockPos.MutableBlockPos pos = this.getBlockPos().mutable();
        Direction side = ((Direction)blockState.getValue(FlapDisplayBlock.HORIZONTAL_FACING)).getClockWise();
        for (int i = 0; i < 64; ++i) {
            BlockState other = this.level.getBlockState((BlockPos)pos);
            if (other.getOptionalValue((Property)FlapDisplayBlock.UP).orElse(false).booleanValue()) {
                pos.move(Direction.UP);
                continue;
            }
            if (!this.level.getBlockState(pos.relative(side)).getOptionalValue((Property)FlapDisplayBlock.UP).orElse(true).booleanValue()) {
                pos.move(side);
                continue;
            }
            BlockEntity found = this.level.getBlockEntity((BlockPos)pos);
            if (!(found instanceof FlapDisplayBlockEntity)) break;
            FlapDisplayBlockEntity flap = (FlapDisplayBlockEntity)found;
            if (!flap.isController) break;
            return flap;
        }
        return null;
    }

    @Override
    protected AABB createRenderBoundingBox() {
        AABB aabb = new AABB(this.worldPosition);
        if (!this.isController) {
            return aabb;
        }
        Vec3i normal = this.getDirection().getClockWise().getNormal();
        return aabb.expandTowards((double)(normal.getX() * this.xSize), (double)(-this.ySize), (double)(normal.getZ() * this.xSize));
    }

    public Direction getDirection() {
        return this.getBlockState().getOptionalValue(FlapDisplayBlock.HORIZONTAL_FACING).orElse(Direction.SOUTH).getOpposite();
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    public int getLineColor(int line) {
        DyeColor color = this.colour[line];
        return color == null ? -2898246 : (Integer)DyeHelper.getDyeColors(color).getFirst() | 0xFF000000;
    }

    public boolean isLineGlowing(int line) {
        return this.glowingLines[line];
    }
}
