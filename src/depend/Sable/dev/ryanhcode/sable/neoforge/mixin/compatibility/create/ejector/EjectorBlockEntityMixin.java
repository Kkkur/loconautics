/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.logistics.depot.EjectorBlockEntity
 *  com.simibubi.create.content.logistics.depot.EjectorBlockEntity$State
 *  com.simibubi.create.content.logistics.depot.EntityLauncher
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.ClipContext
 *  net.minecraft.world.level.ClipContext$Block
 *  net.minecraft.world.level.ClipContext$Fluid
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  org.jetbrains.annotations.Nullable
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.ejector;

import com.simibubi.create.content.logistics.depot.EjectorBlockEntity;
import com.simibubi.create.content.logistics.depot.EntityLauncher;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import dev.ryanhcode.sable.ActiveSableCompanion;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.mixinterface.clip_overwrite.ClipContextExtension;
import dev.ryanhcode.sable.neoforge.mixinhelper.compatibility.create.ejector.SubLevelScanResult;
import dev.ryanhcode.sable.physics.config.dimension_physics.DimensionPhysicsData;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={EjectorBlockEntity.class})
public abstract class EjectorBlockEntityMixin
extends SmartBlockEntity {
    @Unique
    private static final int SUB_LEVEL_SCAN_TIME = 2;
    @Shadow
    private boolean launch;
    @Shadow
    private EjectorBlockEntity.State state;
    @Shadow
    private boolean powered;
    @Shadow
    private EntityLauncher launcher;
    @Unique
    private int sable$scanTimer = 2;
    @Unique
    private int sable$readyTimer = 0;

    public EjectorBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Shadow
    public abstract void activate();

    @Shadow
    protected abstract Direction getFacing();

    @Inject(method={"activateDeferred"}, at={@At(value="INVOKE", target="Lcom/simibubi/create/content/logistics/depot/EjectorBlockEntity;launchItems()V")})
    public void sable$launchSubLevels(CallbackInfo ci) {
        SubLevelScanResult scanResult = this.sable$lookForLaunchableSubLevels();
        if (scanResult == null) {
            return;
        }
        ServerSubLevel otherSubLevel = scanResult.serverSubLevel();
        SubLevelPhysicsSystem physicsSystem = SubLevelContainer.getContainer((ServerLevel)this.level).physicsSystem();
        BlockPos blockPos = this.getBlockPos();
        SubLevel containingSubLevel = Sable.HELPER.getContaining(this.level, (Vec3i)blockPos);
        double c = 3.0 * Math.max(1.0, (double)this.launcher.getHorizontalDistance() / 10.0);
        double px = this.launcher.getHorizontalDistance();
        double py = this.launcher.getVerticalDistance();
        double g = -DimensionPhysicsData.getGravity((Level)this.level).y;
        double vx = c;
        if (py > 0.0) {
            vx = Math.min(c, px * Math.sqrt(0.5 * g / py));
        }
        double vy = vx * py / px + 0.5 * g * px / vx;
        Vec3 verticalImpulse = new Vec3(0.0, vy, 0.0);
        Vec3 localHit = Vec3.atLowerCornerOf((Vec3i)this.getFacing().getNormal()).scale(vx).add(verticalImpulse);
        Vec3 globalHitDirection = containingSubLevel != null ? containingSubLevel.logicalPose().transformNormal(localHit) : localHit;
        RigidBodyHandle otherHandle = physicsSystem.getPhysicsHandle(otherSubLevel);
        otherHandle.applyImpulseAtPoint(scanResult.result().getBlockPos().getCenter(), otherSubLevel.logicalPose().transformNormalInverse(globalHitDirection));
        if (containingSubLevel != null) {
            RigidBodyHandle handle = physicsSystem.getPhysicsHandle((ServerSubLevel)containingSubLevel);
            handle.applyImpulseAtPoint(blockPos.getCenter(), containingSubLevel.logicalPose().transformNormalInverse(globalHitDirection).scale(-1.0));
        }
    }

    @Nullable
    private SubLevelScanResult sable$lookForLaunchableSubLevels() {
        ActiveSableCompanion helper = Sable.HELPER;
        SubLevel containingSubLevel = helper.getContaining((BlockEntity)this);
        BlockPos blockPos = this.getBlockPos();
        ClipContext clipContext = new ClipContext(blockPos.getCenter(), Vec3.upFromBottomCenterOf((Vec3i)blockPos, (double)1.0), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty());
        ClipContextExtension extension = (ClipContextExtension)clipContext;
        extension.sable$setIgnoreMainLevel(true);
        extension.sable$setIgnoredSubLevel(containingSubLevel);
        BlockHitResult result = this.level.clip(clipContext);
        if (result.getType() == HitResult.Type.MISS) {
            return null;
        }
        SubLevel subLevel = helper.getContaining(this.level, (Position)result.getLocation());
        if (!(subLevel instanceof ServerSubLevel)) {
            return null;
        }
        ServerSubLevel serverSubLevel = (ServerSubLevel)subLevel;
        return new SubLevelScanResult(result, serverSubLevel);
    }

    @Inject(method={"tick"}, at={@At(value="HEAD")})
    public void sable$tick(CallbackInfo ci) {
        if (this.level.isClientSide && !this.isVirtual()) {
            return;
        }
        --this.sable$scanTimer;
        if (this.sable$scanTimer <= 0) {
            this.sable$scanTimer = 2;
            if (this.state == EjectorBlockEntity.State.RETRACTING || this.powered || this.launcher.getHorizontalDistance() == 0) {
                this.sable$readyTimer = 0;
                return;
            }
            SubLevelScanResult result = this.sable$lookForLaunchableSubLevels();
            this.sable$readyTimer = result != null ? ++this.sable$readyTimer : 0;
            if (this.sable$readyTimer > 3) {
                this.activate();
                this.notifyUpdate();
                this.sable$readyTimer = 0;
            }
        }
    }
}
