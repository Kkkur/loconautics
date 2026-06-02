/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.NbtUtils
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.capabilities.BlockCapability
 *  net.neoforged.neoforge.capabilities.BlockCapabilityCache
 *  net.neoforged.neoforge.capabilities.Capabilities$ItemHandler
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.ItemHandlerHelper
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.kinetics.mechanicalArm;

import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmAngleTarget;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

public class ArmInteractionPoint {
    protected final ArmInteractionPointType type;
    protected Level level;
    protected final BlockPos pos;
    protected Mode mode = Mode.DEPOSIT;
    protected BlockState cachedState;
    protected BlockCapabilityCache<IItemHandler, Direction> cachedHandler;
    protected ArmAngleTarget cachedAngles;

    public ArmInteractionPoint(ArmInteractionPointType type, Level level, BlockPos pos, BlockState state) {
        this.type = type;
        this.level = level;
        this.pos = pos;
        this.cachedState = state;
    }

    public ArmInteractionPointType getType() {
        return this.type;
    }

    public Level getLevel() {
        return this.level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public Mode getMode() {
        return this.mode;
    }

    public void cycleMode() {
        this.mode = this.mode == Mode.DEPOSIT ? Mode.TAKE : Mode.DEPOSIT;
    }

    protected Vec3 getInteractionPositionVector() {
        return VecHelper.getCenterOf((Vec3i)this.pos);
    }

    protected Direction getInteractionDirection() {
        return Direction.DOWN;
    }

    public ArmAngleTarget getTargetAngles(BlockPos armPos, boolean ceiling) {
        if (this.cachedAngles == null) {
            this.cachedAngles = new ArmAngleTarget(armPos, this.getInteractionPositionVector(), this.getInteractionDirection(), ceiling);
        }
        return this.cachedAngles;
    }

    public void updateCachedState() {
        this.cachedState = this.level.getBlockState(this.pos);
    }

    public boolean isValid() {
        this.updateCachedState();
        return this.type.canCreatePoint(this.level, this.pos, this.cachedState);
    }

    public void keepAlive() {
    }

    @Nullable
    protected IItemHandler getHandler(ArmBlockEntity armBlockEntity) {
        Level level;
        if (this.cachedHandler == null && (level = this.level) instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel)level;
            BlockEntity be = this.level.getBlockEntity(this.pos);
            if (be == null) {
                return null;
            }
            this.cachedHandler = BlockCapabilityCache.create((BlockCapability)Capabilities.ItemHandler.BLOCK, (ServerLevel)serverLevel, (BlockPos)this.pos, (Object)Direction.UP, () -> !armBlockEntity.isRemoved(), () -> {
                this.cachedHandler = null;
            });
        }
        return (IItemHandler)this.cachedHandler.getCapability();
    }

    public ItemStack insert(ArmBlockEntity armBlockEntity, ItemStack stack, boolean simulate) {
        IItemHandler handler = this.getHandler(armBlockEntity);
        if (handler == null) {
            return stack;
        }
        return ItemHandlerHelper.insertItem((IItemHandler)handler, (ItemStack)stack, (boolean)simulate);
    }

    public ItemStack extract(ArmBlockEntity armBlockEntity, int slot, int amount, boolean simulate) {
        IItemHandler handler = this.getHandler(armBlockEntity);
        if (handler == null) {
            return ItemStack.EMPTY;
        }
        return handler.extractItem(slot, amount, simulate);
    }

    public ItemStack extract(ArmBlockEntity armBlockEntity, int slot, boolean simulate) {
        return this.extract(armBlockEntity, slot, 64, simulate);
    }

    public int getSlotCount(ArmBlockEntity armBlockEntity) {
        IItemHandler handler = this.getHandler(armBlockEntity);
        if (handler == null) {
            return 0;
        }
        return handler.getSlots();
    }

    protected void serialize(CompoundTag nbt, BlockPos anchor) {
        NBTHelper.writeEnum((CompoundTag)nbt, (String)"Mode", (Enum)this.mode);
    }

    protected void deserialize(CompoundTag nbt, BlockPos anchor) {
        this.mode = (Mode)NBTHelper.readEnum((CompoundTag)nbt, (String)"Mode", Mode.class);
    }

    public final CompoundTag serialize(BlockPos anchor) {
        ResourceLocation key = CreateBuiltInRegistries.ARM_INTERACTION_POINT_TYPE.getKey((Object)this.type);
        if (key == null) {
            throw new IllegalArgumentException("Could not get id for ArmInteractionPointType " + String.valueOf(this.type) + "!");
        }
        CompoundTag nbt = new CompoundTag();
        nbt.putString("Type", key.toString());
        nbt.put("Pos", NbtUtils.writeBlockPos((BlockPos)this.pos.subtract((Vec3i)anchor)));
        this.serialize(nbt, anchor);
        return nbt;
    }

    @Nullable
    public static ArmInteractionPoint deserialize(CompoundTag nbt, Level level, BlockPos anchor) {
        BlockState state;
        ResourceLocation id = ResourceLocation.tryParse((String)nbt.getString("Type"));
        if (id == null) {
            return null;
        }
        ArmInteractionPointType type = (ArmInteractionPointType)CreateBuiltInRegistries.ARM_INTERACTION_POINT_TYPE.get(id);
        if (type == null) {
            return null;
        }
        BlockPos pos = NBTHelper.readBlockPos((CompoundTag)nbt, (String)"Pos").offset((Vec3i)anchor);
        if (!type.canCreatePoint(level, pos, state = level.getBlockState(pos))) {
            return null;
        }
        ArmInteractionPoint point = type.createPoint(level, pos, state);
        if (point == null) {
            return null;
        }
        point.deserialize(nbt, anchor);
        return point;
    }

    public static void transformPos(CompoundTag nbt, StructureTransform transform) {
        BlockPos pos = NBTHelper.readBlockPos((CompoundTag)nbt, (String)"Pos");
        pos = transform.applyWithoutOffset(pos);
        nbt.put("Pos", NbtUtils.writeBlockPos((BlockPos)pos));
    }

    public static boolean isInteractable(Level level, BlockPos pos, BlockState state) {
        return ArmInteractionPointType.getPrimaryType(level, pos, state) != null;
    }

    @Nullable
    public static ArmInteractionPoint create(Level level, BlockPos pos, BlockState state) {
        ArmInteractionPointType type = ArmInteractionPointType.getPrimaryType(level, pos, state);
        if (type == null) {
            return null;
        }
        return type.createPoint(level, pos, state);
    }

    public static enum Mode {
        DEPOSIT("mechanical_arm.deposit_to", 14532966),
        TAKE("mechanical_arm.extract_from", 8375776);

        private final String translationKey;
        private final int color;

        private Mode(String translationKey, int color) {
            this.translationKey = translationKey;
            this.color = color;
        }

        public String getTranslationKey() {
            return this.translationKey;
        }

        public int getColor() {
            return this.color;
        }
    }
}
