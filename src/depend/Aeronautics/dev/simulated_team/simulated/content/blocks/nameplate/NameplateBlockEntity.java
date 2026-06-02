/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.equipment.clipboard.ClipboardCloneable
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
 *  dev.ryanhcode.sable.Sable
 *  dev.ryanhcode.sable.sublevel.SubLevel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.NbtUtils
 *  net.minecraft.util.FastColor$ARGB32
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.DyeColor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package dev.simulated_team.simulated.content.blocks.nameplate;

import com.simibubi.create.content.equipment.clipboard.ClipboardCloneable;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.simulated_team.simulated.content.blocks.nameplate.NameplateBlock;
import dev.simulated_team.simulated.data.advancements.SimAdvancements;
import java.util.List;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NameplateBlockEntity
extends SmartBlockEntity
implements ClipboardCloneable {
    protected boolean glowing;
    protected boolean waxed;
    private DyeColor textColor = DyeColor.BLACK;
    private String name = null;
    private SubLevel connectedSubLevel;
    private BlockPos controllerPos;
    private BlockPos supportingPos = null;
    private boolean controller = false;
    private int controllerWidth = -1;

    public NameplateBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.controllerPos = pos;
    }

    private static boolean checkNameplate(DyeColor color, Direction facing, BlockState state) {
        NameplateBlock npb;
        Block block = state.getBlock();
        return block instanceof NameplateBlock && (npb = (NameplateBlock)block).getColor() == color && state.getValue((Property)NameplateBlock.FACING) == facing;
    }

    public static boolean canPlayerReach(NameplateBlockEntity be, Player player) {
        return NameplateBlockEntity.getClosestDistance(be, player.getEyePosition()) < player.blockInteractionRange() + 4.0;
    }

    public void initialize() {
        super.initialize();
        DyeColor color = this.getColor();
        Direction facing = (Direction)this.getBlockState().getValue((Property)NameplateBlock.FACING);
        this.checkAndUpdateController(color, facing);
        this.connectedSubLevel = Sable.HELPER.getContaining((BlockEntity)this);
        if (this.connectedSubLevel != null && this.allowsEditing()) {
            this.name = this.connectedSubLevel.getName();
        }
    }

    public void tick() {
        super.tick();
        if (!this.level.isClientSide) {
            DyeColor color = this.getColor();
            Direction facing = (Direction)this.getBlockState().getValue((Property)NameplateBlock.FACING);
            if (this.controller && (this.controllerWidth == -1 || this.controllerWidth == 0)) {
                this.updateNameplates(color, facing);
            }
        }
    }

    public void lazyTick() {
        super.lazyTick();
        if (!this.level.isClientSide && this.controller && this.allowsEditing() && this.connectedSubLevel != null && !Objects.equals(this.connectedSubLevel.getName(), this.name)) {
            this.setName(this.connectedSubLevel.getName(), true, null);
        }
    }

    public void checkAndUpdateController(DyeColor color, Direction facing) {
        NameplateBlock npb;
        BlockPos leftPos = this.getBlockPos().offset(facing.getClockWise(Direction.Axis.Y).getNormal());
        boolean wasController = this.controller;
        BlockState leftState = this.getLevel().getBlockState(leftPos);
        Block block = leftState.getBlock();
        boolean bl = this.controller = !(block instanceof NameplateBlock && (npb = (NameplateBlock)block).getColor() == color && leftState.getValue((Property)NameplateBlock.FACING) == facing);
        if (wasController && !this.controller && NameplateBlockEntity.checkNameplate(color, facing, leftState)) {
            NameplateBlockEntity leftBE = (NameplateBlockEntity)this.getLevel().getBlockEntity(leftPos);
            this.moveController(leftBE);
            leftBE.checkAndUpdateController(color, facing);
        }
        if (this.controller) {
            this.controllerPos = this.getBlockPos();
            this.updateNameplates(color, facing);
            this.invalidateRenderBoundingBox();
        }
        this.notifyUpdate();
    }

    public void updateNameplates(DyeColor color, Direction facing) {
        int preControllerWidth = this.controllerWidth;
        this.controllerWidth = 1;
        BlockPos.MutableBlockPos p = this.getBlockPos().mutable();
        while (NameplateBlockEntity.checkNameplate(color, facing, this.level.getBlockState((BlockPos)p.setWithOffset((Vec3i)p, facing.getCounterClockWise(Direction.Axis.Y))))) {
            this.transferData((NameplateBlockEntity)this.getLevel().getBlockEntity((BlockPos)p));
            ++this.controllerWidth;
        }
        this.invalidateRenderBoundingBox();
        if (this.controllerWidth != preControllerWidth) {
            this.notifyUpdate();
        }
    }

    private void transferData(NameplateBlockEntity namePlate) {
        namePlate.resetData();
        namePlate.controllerPos = this.controllerPos;
        namePlate.name = this.getName();
        namePlate.textColor = this.textColor;
        namePlate.glowing = this.glowing;
        namePlate.invalidateRenderBoundingBox();
        namePlate.sendData();
    }

    private void moveController(NameplateBlockEntity other) {
        other.controller = true;
        other.glowing = this.glowing;
        other.setName(this.getName(), false, null);
        other.setTextColor(this.textColor, false);
        this.resetData();
    }

    public static double getClosestDistance(NameplateBlockEntity nbe, Vec3 point) {
        if (!nbe.controller) {
            return NameplateBlockEntity.getClosestDistance(nbe.findController(), point);
        }
        Vec3i dir = ((Direction)nbe.getBlockState().getValue((Property)NameplateBlock.FACING)).getCounterClockWise().getNormal();
        Vec3 A = nbe.getBlockPos().getCenter();
        Vec3 B = A.add((double)(dir.getX() * nbe.controllerWidth), (double)(dir.getY() * nbe.controllerWidth), (double)(dir.getZ() * nbe.controllerWidth));
        SubLevel subLevel = Sable.HELPER.getContaining((BlockEntity)nbe);
        if (subLevel != null) {
            A = subLevel.logicalPose().transformPosition(A);
            B = subLevel.logicalPose().transformPosition(B);
        }
        Vec3 v = B.subtract(A);
        Vec3 u = A.subtract(point);
        double t = Math.clamp(-v.dot(u) / v.dot(v), 0.0, 1.0);
        Vec3 closest = A.add(v.scale(t));
        return point.distanceTo(closest);
    }

    public boolean allowsEditing() {
        return !this.waxed;
    }

    private void resetData() {
        this.controller = false;
        this.name = null;
        this.glowing = false;
        this.controllerWidth = -1;
    }

    private DyeColor getColor() {
        return ((NameplateBlock)this.getBlockState().getBlock()).getColor();
    }

    public DyeColor getTextColor() {
        return this.textColor;
    }

    public void setTextColor(DyeColor textColor, boolean updateNameplates) {
        if (this.controller) {
            this.textColor = textColor;
            if (updateNameplates) {
                this.updateNameplates(this.getColor(), (Direction)this.getBlockState().getValue((Property)NameplateBlock.FACING));
            }
        }
    }

    public int getDarkColor(DyeColor textColor) {
        int i = textColor.getTextColor();
        if (i == DyeColor.BLACK.getTextColor() && this.glowing) {
            return -988212;
        }
        double d = 0.4;
        int j = (int)((double)FastColor.ARGB32.red((int)i) * 0.4);
        int k = (int)((double)FastColor.ARGB32.green((int)i) * 0.4);
        int l = (int)((double)FastColor.ARGB32.blue((int)i) * 0.4);
        return FastColor.ARGB32.color((int)0, (int)j, (int)k, (int)l);
    }

    public NameplateBlockEntity findController() {
        if (!this.controller) {
            BlockEntity blockEntity = this.getLevel().getBlockEntity(this.controllerPos);
            if (blockEntity instanceof NameplateBlockEntity) {
                NameplateBlockEntity nbe = (NameplateBlockEntity)blockEntity;
                nbe.controller = true;
                return nbe;
            }
            this.controller = true;
        }
        return this;
    }

    public boolean isController() {
        return this.getBlockPos().equals((Object)this.controllerPos);
    }

    public int getControllerWidth() {
        return this.controllerWidth;
    }

    public String getName() {
        if (this.connectedSubLevel != null && this.connectedSubLevel.getName() != null && this.allowsEditing()) {
            return this.connectedSubLevel.getName();
        }
        return this.name == null ? "" : this.name;
    }

    public void setName(String name, boolean updateNameplates, @Nullable Player player) {
        this.name = name;
        if (this.connectedSubLevel != null && !this.waxed) {
            this.connectedSubLevel.setName(name);
            if (player != null) {
                SimAdvancements.I_DECLARE_THEE.awardTo(player);
            }
        }
        if (updateNameplates) {
            this.updateNameplates(this.getColor(), (Direction)this.getBlockState().getValue((Property)NameplateBlock.FACING));
            this.sendData();
        }
    }

    public static boolean hasSupport(NameplateBlockEntity nbe) {
        if (!nbe.controller) {
            return NameplateBlockEntity.hasSupport(nbe.findController());
        }
        Direction facing = (Direction)nbe.getBlockState().getValue((Property)NameplateBlock.FACING);
        if (nbe.supportingPos != null) {
            if (nbe.level.getBlockState(nbe.supportingPos).is(nbe.getBlockState().getBlock()) && NameplateBlock.hasBackSupport(facing, (LevelReader)nbe.level, nbe.supportingPos)) {
                return true;
            }
            nbe.supportingPos = null;
        }
        Direction next = facing.getCounterClockWise();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        pos.set((Vec3i)nbe.getBlockPos());
        for (int i = 0; i < nbe.controllerWidth; ++i) {
            if (NameplateBlock.hasBackSupport(facing, (LevelReader)nbe.level, (BlockPos)pos)) {
                nbe.supportingPos = pos.immutable();
                break;
            }
            pos.move(next);
        }
        return nbe.supportingPos != null;
    }

    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.putInt("TextColor", this.textColor.getId());
        tag.putBoolean("Glow", this.glowing);
        tag.putBoolean("Waxed", this.waxed);
        if (this.name != null) {
            tag.putString("Name", this.name);
        }
        if (this.controller) {
            tag.putInt("Width", this.controllerWidth);
        } else {
            tag.put("ControllerPos", NbtUtils.writeBlockPos((BlockPos)this.controllerPos));
        }
    }

    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        this.textColor = DyeColor.byId((int)tag.getInt("TextColor"));
        this.glowing = tag.getBoolean("Glow");
        this.waxed = tag.getBoolean("Waxed");
        if (tag.contains("Name")) {
            this.name = tag.getString("Name");
        }
        if (tag.contains("ControllerPos")) {
            this.controller = false;
            this.controllerPos = (BlockPos)NbtUtils.readBlockPos((CompoundTag)tag, (String)"ControllerPos").get();
        } else {
            this.controller = true;
            this.controllerPos = this.getBlockPos();
            this.controllerWidth = tag.getInt("Width");
        }
        if (clientPacket) {
            this.invalidateRenderBoundingBox();
        }
    }

    protected AABB createRenderBoundingBox() {
        if (!this.controller) {
            return new AABB(this.getBlockPos());
        }
        Direction facing = (Direction)this.getBlockState().getValue((Property)NameplateBlock.FACING);
        Vec3i off = facing.getCounterClockWise(Direction.Axis.Y).getNormal();
        AABB bounds = AABB.encapsulatingFullBlocks((BlockPos)this.getBlockPos(), (BlockPos)this.getBlockPos().offset(off.multiply(this.controllerWidth - 1)));
        return bounds;
    }

    public String getClipboardKey() {
        return "Name";
    }

    public boolean writeToClipboard(// Could not load outer class - annotation placement on inner may be incorrect
     @NotNull HolderLookup.Provider var1, CompoundTag tag, Direction var3) {
        NameplateBlockEntity controller = this.findController();
        tag.putString("StoredName", controller.getName());
        tag.putInt("TextColor", controller.textColor.getId());
        return true;
    }

    public boolean readFromClipboard(// Could not load outer class - annotation placement on inner may be incorrect
     @NotNull HolderLookup.Provider var1, CompoundTag tag, Player player, Direction var4, boolean simulate) {
        NameplateBlockEntity controller = this.findController();
        if (!controller.allowsEditing()) {
            return false;
        }
        if (!tag.contains("StoredName")) {
            return false;
        }
        if (simulate) {
            return true;
        }
        controller.setName(tag.getString("StoredName"), true, player);
        controller.textColor = DyeColor.byId((int)tag.getInt("TextColor"));
        this.sendData();
        return true;
    }

    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }
}
