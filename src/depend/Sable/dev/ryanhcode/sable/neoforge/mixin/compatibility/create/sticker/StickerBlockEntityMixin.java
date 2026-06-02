/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.contraptions.chassis.StickerBlock
 *  com.simibubi.create.content.contraptions.chassis.StickerBlockEntity
 *  com.simibubi.create.content.contraptions.glue.SuperGlueEntity
 *  com.simibubi.create.content.contraptions.glue.SuperGlueItem
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.ClipContext
 *  net.minecraft.world.level.ClipContext$Block
 *  net.minecraft.world.level.ClipContext$Fluid
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  org.joml.Quaterniond
 *  org.joml.Quaterniondc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.sticker;

import com.simibubi.create.content.contraptions.chassis.StickerBlock;
import com.simibubi.create.content.contraptions.chassis.StickerBlockEntity;
import com.simibubi.create.content.contraptions.glue.SuperGlueEntity;
import com.simibubi.create.content.contraptions.glue.SuperGlueItem;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import dev.ryanhcode.sable.ActiveSableCompanion;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.physics.constraint.fixed.FixedConstraintConfiguration;
import dev.ryanhcode.sable.api.physics.constraint.fixed.FixedConstraintHandle;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.neoforge.mixinterface.compatibility.create.StickerBlockEntityExtension;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import java.util.Objects;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={StickerBlockEntity.class})
public abstract class StickerBlockEntityMixin
extends SmartBlockEntity
implements StickerBlockEntityExtension {
    @Unique
    private static final double DISTANCE_TOLERANCE = 0.0625;
    @Unique
    private static final double ANGLE_TOLERANCE = 30.0;
    @Unique
    private FixedConstraintHandle sable$handle;
    @Unique
    private BlockPos sable$attachedPos;
    @Unique
    private Vector3d sable$constraintPos1;
    @Unique
    private Vector3d sable$constraintPos2;
    @Unique
    private Quaterniond sable$constraintOrientation;
    @Unique
    private boolean sable$hadConstraint;
    @Unique
    private boolean sable$hasConstraint;

    private StickerBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Shadow
    public abstract boolean isBlockStateExtended();

    @Shadow
    public abstract void playSound(boolean var1);

    @Override
    public void sable$removeConstraint() {
        if (this.sable$handle != null) {
            this.sable$handle.remove();
            this.sable$handle = null;
        }
        this.sable$attachedPos = null;
        this.sable$constraintPos1 = null;
        this.sable$constraintPos2 = null;
        this.sable$constraintOrientation = null;
        this.sable$hasConstraint = false;
        this.sendData();
    }

    @Override
    public void sable$tickConstraint() {
        if (this.isBlockStateExtended()) {
            Direction direction = (Direction)this.getBlockState().getValue((Property)StickerBlock.FACING);
            if (this.sable$attachedPos != null && !SuperGlueEntity.isValidFace((Level)this.level, (BlockPos)this.sable$attachedPos, (Direction)direction.getOpposite())) {
                this.sable$removeConstraint();
            }
            if (this.sable$handle == null || !this.sable$handle.isValid()) {
                if (this.sable$attachedPos != null) {
                    ServerSubLevel otherSubLevel;
                    ActiveSableCompanion helper = Sable.HELPER;
                    ServerSubLevel thisSubLevel = (ServerSubLevel)helper.getContaining(this.level, (Vec3i)this.getBlockPos());
                    if (thisSubLevel != (otherSubLevel = (ServerSubLevel)helper.getContaining(this.level, (Vec3i)this.sable$attachedPos))) {
                        this.sable$applyConstraint(thisSubLevel, otherSubLevel);
                    }
                    return;
                }
                this.sable$removeConstraint();
                double gridHalfSize = 0.375;
                Vector3d rayStartPosition = JOMLConversion.atCenterOf((Vec3i)this.getBlockPos()).add((double)direction.getStepX() * 0.5, (double)direction.getStepY() * 0.5, (double)direction.getStepZ() * 0.5);
                this.sable$tryAttach((Vector3dc)rayStartPosition, direction);
                Vector3d gridRayStartPosition = new Vector3d();
                for (int xOffset = -1; xOffset <= 1; xOffset += 2) {
                    for (int zOffset = -1; zOffset <= 1; zOffset += 2) {
                        gridRayStartPosition.set((Vector3dc)rayStartPosition);
                        Direction secondaryDirection = direction.getAxis().isVertical() ? Direction.NORTH : direction.getClockWise();
                        Direction tertiaryDirection = direction.getAxis().isVertical() ? Direction.EAST : Direction.UP;
                        gridRayStartPosition.add((double)(secondaryDirection.getStepX() * xOffset) * 0.375, (double)(secondaryDirection.getStepY() * xOffset) * 0.375, (double)(secondaryDirection.getStepZ() * xOffset) * 0.375);
                        gridRayStartPosition.add((double)(tertiaryDirection.getStepX() * zOffset) * 0.375, (double)(tertiaryDirection.getStepY() * zOffset) * 0.375, (double)(tertiaryDirection.getStepZ() * zOffset) * 0.375);
                        this.sable$tryAttach((Vector3dc)gridRayStartPosition, direction);
                    }
                }
            }
        } else {
            this.sable$removeConstraint();
        }
    }

    @Unique
    private void sable$tryAttach(Vector3dc rayStartPosition, Direction direction) {
        Vec3 end;
        if (this.sable$handle != null || this.sable$hasConstraint) {
            return;
        }
        int dx = direction.getStepX();
        int dy = direction.getStepY();
        int dz = direction.getStepZ();
        Vec3 start = JOMLConversion.toMojang((Vector3dc)rayStartPosition);
        BlockHitResult clip = this.level.clip(new ClipContext(start, end = start.add((double)dx * 0.0625, (double)dy * 0.0625, (double)dz * 0.0625), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty()));
        if (clip.getType() != HitResult.Type.MISS && SuperGlueEntity.isValidFace((Level)this.level, (BlockPos)clip.getBlockPos(), (Direction)direction.getOpposite())) {
            Vec3 hitLocation = clip.getLocation();
            BlockPos otherPos = clip.getBlockPos();
            Vector3d from = JOMLConversion.toJOML((Position)start);
            Vector3d to = JOMLConversion.toJOML((Position)hitLocation);
            ActiveSableCompanion helper = Sable.HELPER;
            ServerSubLevel thisSubLevel = (ServerSubLevel)helper.getContaining(this.level, (Vec3i)this.getBlockPos());
            ServerSubLevel otherSubLevel = (ServerSubLevel)helper.getContaining(this.level, (Vec3i)otherPos);
            Quaterniondc first = thisSubLevel != null ? thisSubLevel.logicalPose().orientation() : JOMLConversion.QUAT_IDENTITY;
            Quaterniondc second = otherSubLevel != null ? otherSubLevel.logicalPose().orientation() : JOMLConversion.QUAT_IDENTITY;
            Direction hitDirection = clip.getDirection().getOpposite();
            Vector3d globalDirectionA = first.transform(new Vector3d((double)dx, (double)dy, (double)dz));
            Vector3d globalDirectionB = second.transform(new Vector3d((double)hitDirection.getStepX(), (double)hitDirection.getStepY(), (double)hitDirection.getStepZ()));
            Vector3d axis = new Vector3d((Vector3dc)globalDirectionA).cross((Vector3dc)globalDirectionB).normalize();
            double dot = globalDirectionA.dot((Vector3dc)globalDirectionB);
            if (dot < 1.0E-6 || dot > 1.0) {
                return;
            }
            double angle = Math.acos(dot);
            if (angle > Math.toRadians(30.0)) {
                return;
            }
            this.sable$attachedPos = otherPos;
            this.sable$constraintPos1 = from;
            this.sable$constraintPos2 = to;
            this.sable$constraintOrientation = new Quaterniond().rotateAxis(-angle, (Vector3dc)axis).mul(second).premul((Quaterniondc)first.conjugate(new Quaterniond()));
            this.sable$applyConstraint(thisSubLevel, otherSubLevel);
        }
    }

    @Unique
    private void sable$applyConstraint(ServerSubLevel thisSubLevel, ServerSubLevel otherSubLevel) {
        Level level = this.level;
        if (!(level instanceof ServerLevel)) {
            throw new IllegalStateException("StickerBlockEntity must be on a ServerLevel to apply constraints.");
        }
        ServerLevel serverLevel = (ServerLevel)level;
        FixedConstraintConfiguration constraint = new FixedConstraintConfiguration((Vector3dc)this.sable$constraintPos1, (Vector3dc)this.sable$constraintPos2, (Quaterniondc)this.sable$constraintOrientation);
        ServerSubLevelContainer container = SubLevelContainer.getContainer(serverLevel);
        this.sable$handle = container.physicsSystem().getPipeline().addConstraint(thisSubLevel, otherSubLevel, constraint);
        this.sable$hasConstraint = true;
        this.sendData();
    }

    public void remove() {
        super.remove();
        this.sable$removeConstraint();
    }

    @Inject(method={"tick"}, at={@At(value="HEAD")})
    public void tick(CallbackInfo ci) {
        if (this.level.isClientSide()) {
            if (this.sable$hadConstraint != this.sable$hasConstraint) {
                this.sable$hadConstraint = this.sable$hasConstraint;
                if (this.sable$hasConstraint) {
                    SuperGlueItem.spawnParticles((Level)this.level, (BlockPos)this.worldPosition, (Direction)((Direction)this.getBlockState().getValue((Property)StickerBlock.FACING)), (boolean)true);
                    CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> this.playSound(true));
                } else {
                    CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> this.playSound(false));
                }
            }
            return;
        }
        this.sable$tickConstraint();
    }

    @Inject(method={"write"}, at={@At(value="TAIL")})
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci) {
        if (clientPacket) {
            compound.putBoolean("SableHasConstraint", this.sable$handle != null);
        } else if (this.sable$handle != null) {
            CompoundTag constraint = new CompoundTag();
            BlockPos blockPos = this.getBlockPos();
            constraint.putInt("ThisX", blockPos.getX());
            constraint.putInt("ThisY", blockPos.getY());
            constraint.putInt("ThisZ", blockPos.getZ());
            constraint.putInt("X", this.sable$attachedPos.getX());
            constraint.putInt("Y", this.sable$attachedPos.getY());
            constraint.putInt("Z", this.sable$attachedPos.getZ());
            constraint.putDouble("FromX", this.sable$constraintPos1.x);
            constraint.putDouble("FromY", this.sable$constraintPos1.y);
            constraint.putDouble("FromZ", this.sable$constraintPos1.z);
            constraint.putDouble("ToX", this.sable$constraintPos2.x);
            constraint.putDouble("ToY", this.sable$constraintPos2.y);
            constraint.putDouble("ToZ", this.sable$constraintPos2.z);
            constraint.putDouble("QuatX", this.sable$constraintOrientation.x);
            constraint.putDouble("QuatY", this.sable$constraintOrientation.y);
            constraint.putDouble("QuatZ", this.sable$constraintOrientation.z);
            constraint.putDouble("QuatW", this.sable$constraintOrientation.w);
            compound.put("SableConstraint", (Tag)constraint);
        }
    }

    @Inject(method={"read"}, at={@At(value="TAIL")})
    public void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci) {
        if (clientPacket) {
            this.sable$hasConstraint = compound.getBoolean("SableHasConstraint");
        } else if (compound.contains("SableConstraint", 10)) {
            CompoundTag constraint = compound.getCompound("SableConstraint");
            BlockPos thisPos = new BlockPos(constraint.getInt("ThisX"), constraint.getInt("ThisY"), constraint.getInt("ThisZ"));
            if (!Objects.equals(this.getBlockPos(), thisPos)) {
                this.sable$removeConstraint();
                return;
            }
            this.sable$attachedPos = new BlockPos(constraint.getInt("X"), constraint.getInt("Y"), constraint.getInt("Z"));
            this.sable$constraintPos1 = new Vector3d(constraint.getDouble("FromX"), constraint.getDouble("FromY"), constraint.getDouble("FromZ"));
            this.sable$constraintPos2 = new Vector3d(constraint.getDouble("ToX"), constraint.getDouble("ToY"), constraint.getDouble("ToZ"));
            this.sable$constraintOrientation = new Quaterniond(constraint.getDouble("QuatX"), constraint.getDouble("QuatY"), constraint.getDouble("QuatZ"), constraint.getDouble("QuatW"));
        } else {
            this.sable$attachedPos = null;
            this.sable$constraintPos1 = null;
            this.sable$constraintPos2 = null;
            this.sable$constraintOrientation = null;
        }
    }
}
