/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Suppliers
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate$StructureBlockInfo
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.contraptions.behaviour;

import com.google.common.base.Suppliers;
import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorage;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorage;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class MovementContext {
    public Vec3 position;
    public Vec3 motion;
    public Vec3 relativeMotion;
    public UnaryOperator<Vec3> rotation;
    public Level world;
    public BlockState state;
    public BlockPos localPos;
    public CompoundTag blockEntityData;
    public boolean stall;
    public boolean disabled;
    public boolean firstMovement;
    public CompoundTag data;
    public Contraption contraption;
    public Object temporaryData;
    private FilterItemStack filter;
    private final Supplier<MountedItemStorage> itemStorage;
    private final Supplier<MountedFluidStorage> fluidStorage;

    public MovementContext(Level world, StructureTemplate.StructureBlockInfo info, Contraption contraption) {
        this.world = world;
        this.state = info.state();
        this.blockEntityData = info.nbt();
        this.contraption = contraption;
        this.localPos = info.pos();
        this.disabled = false;
        this.firstMovement = true;
        this.motion = Vec3.ZERO;
        this.relativeMotion = Vec3.ZERO;
        this.rotation = v -> v;
        this.position = null;
        this.data = new CompoundTag();
        this.stall = false;
        this.filter = null;
        this.itemStorage = Suppliers.memoize(() -> (MountedItemStorage)contraption.getStorage().getAllItemStorages().get((Object)this.localPos));
        this.fluidStorage = Suppliers.memoize(() -> (MountedFluidStorage)contraption.getStorage().getFluids().storages.get((Object)this.localPos));
    }

    public float getAnimationSpeed() {
        int modifier = 1000;
        double length = -this.motion.length();
        if (this.disabled) {
            return 0.0f;
        }
        if (this.world.isClientSide && this.contraption.stalled) {
            return 700.0f;
        }
        if (Math.abs(length) < 0.001953125) {
            return 0.0f;
        }
        return (int)(length * (double)modifier + 100.0 * Math.signum(length)) / 100 * 100;
    }

    public static MovementContext readNBT(Level world, StructureTemplate.StructureBlockInfo info, CompoundTag nbt, Contraption contraption) {
        MovementContext context = new MovementContext(world, info, contraption);
        context.motion = VecHelper.readNBT((ListTag)nbt.getList("Motion", 6));
        context.relativeMotion = VecHelper.readNBT((ListTag)nbt.getList("RelativeMotion", 6));
        if (nbt.contains("Position")) {
            context.position = VecHelper.readNBT((ListTag)nbt.getList("Position", 6));
        }
        context.stall = nbt.getBoolean("Stall");
        context.firstMovement = nbt.getBoolean("FirstMovement");
        context.data = nbt.getCompound("Data");
        return context;
    }

    public CompoundTag writeToNBT(CompoundTag nbt) {
        nbt.put("Motion", (Tag)VecHelper.writeNBT((Vec3)this.motion));
        nbt.put("RelativeMotion", (Tag)VecHelper.writeNBT((Vec3)this.relativeMotion));
        if (this.position != null) {
            nbt.put("Position", (Tag)VecHelper.writeNBT((Vec3)this.position));
        }
        nbt.putBoolean("Stall", this.stall);
        nbt.putBoolean("FirstMovement", this.firstMovement);
        nbt.put("Data", (Tag)this.data.copy());
        return nbt;
    }

    public FilterItemStack getFilterFromBE() {
        if (this.filter != null) {
            return this.filter;
        }
        this.filter = FilterItemStack.of((HolderLookup.Provider)this.world.registryAccess(), this.blockEntityData.getCompound("Filter"));
        return this.filter;
    }

    @Nullable
    public MountedItemStorage getItemStorage() {
        return this.itemStorage.get();
    }

    @Nullable
    public MountedFluidStorage getFluidStorage() {
        return this.fluidStorage.get();
    }
}
