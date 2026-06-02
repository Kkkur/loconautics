/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  com.simibubi.create.AllSpecialTextures
 *  com.simibubi.create.content.schematics.client.SchematicAndQuillHandler
 *  dev.ryanhcode.sable.companion.math.BoundingBox3d
 *  dev.ryanhcode.sable.companion.math.BoundingBox3dc
 *  dev.ryanhcode.sable.companion.math.BoundingBox3ic
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  net.createmod.catnip.outliner.Outliner
 *  net.createmod.catnip.render.BindableTexture
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.schematics;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.AllSpecialTextures;
import com.simibubi.create.content.schematics.client.SchematicAndQuillHandler;
import dev.ryanhcode.sable.ActiveSableCompanion;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.sublevel.ClientSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.BoundingBox3dc;
import dev.ryanhcode.sable.companion.math.BoundingBox3ic;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.createmod.catnip.outliner.Outliner;
import net.createmod.catnip.render.BindableTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={SchematicAndQuillHandler.class})
public abstract class SchematicAndQuillHandlerMixin {
    @Shadow
    public BlockPos firstPos;
    @Shadow
    private Object outlineSlot;
    @Shadow
    public BlockPos secondPos;

    @Shadow
    protected abstract AABB getCurrentSelectionBox();

    @Shadow
    protected abstract Outliner outliner();

    @Redirect(method={"tick"}, at=@At(value="INVOKE", target="Lnet/minecraft/core/BlockPos;containing(Lnet/minecraft/core/Position;)Lnet/minecraft/core/BlockPos;"))
    private BlockPos sable$containing(Position position) {
        ClientSubLevel subLevel;
        if (this.firstPos != null && (subLevel = Sable.HELPER.getContainingClient((Vec3i)this.firstPos)) != null) {
            position = subLevel.logicalPose().transformPositionInverse(new Vec3(position.x(), position.y(), position.z()));
        }
        return BlockPos.containing((Position)position);
    }

    @WrapOperation(method={"tick"}, at={@At(value="INVOKE", target="Lnet/minecraft/world/phys/BlockHitResult;getBlockPos()Lnet/minecraft/core/BlockPos;")})
    public BlockPos sable$preventMovingSelectedOutOfPlot(BlockHitResult instance, Operation<BlockPos> original) {
        if (this.firstPos != null) {
            ClientSubLevel selectedSublevel = Sable.HELPER.getContainingClient((Vec3i)this.firstPos);
            Vec3 loc = instance.getBlockPos().getCenter();
            ClientSubLevel hitSublevel = Sable.HELPER.getContainingClient((Position)loc);
            if (hitSublevel != selectedSublevel) {
                if (hitSublevel != null) {
                    loc = hitSublevel.logicalPose().transformPosition(loc);
                }
                if (selectedSublevel != null) {
                    loc = selectedSublevel.logicalPose().transformPositionInverse(loc);
                }
            }
            return BlockPos.containing((Position)loc);
        }
        return (BlockPos)original.call(new Object[]{instance});
    }

    @Inject(method={"tick"}, at={@At(value="TAIL")})
    private void sable$renderSubLevelBoxes(CallbackInfo ci) {
        AABB currentSelectionBox;
        ClientLevel level = Minecraft.getInstance().level;
        ClientSubLevelContainer container = SubLevelContainer.getContainer(level);
        ActiveSableCompanion helper = Sable.HELPER;
        if (this.firstPos != null && container.inBounds(this.firstPos) && helper.getContaining((Level)level, (Vec3i)this.firstPos) == null) {
            this.firstPos = null;
        }
        if (this.secondPos != null && container.inBounds(this.secondPos) && helper.getContaining((Level)level, (Vec3i)this.secondPos) == null) {
            this.secondPos = null;
        }
        if ((currentSelectionBox = this.getCurrentSelectionBox()) != null) {
            BoundingBox3d bounds = new BoundingBox3d(currentSelectionBox);
            ClientSubLevel containingSubLevel = helper.getContainingClient((Vector3dc)bounds.center(new Vector3d()));
            if (containingSubLevel != null) {
                bounds.transform((Pose3dc)containingSubLevel.logicalPose(), bounds);
            }
            Iterable<SubLevel> intersecting = helper.getAllIntersecting((Level)level, (BoundingBox3dc)bounds);
            for (SubLevel subLevel : intersecting) {
                if (subLevel == containingSubLevel) continue;
                BoundingBox3ic plotBounds = subLevel.getPlot().getBoundingBox();
                this.outliner().chaseAABB((Object)(this.outlineSlot.hashCode() + " sub_level " + String.valueOf(subLevel.getUniqueId())), plotBounds.toAABB()).colored(8824035).withFaceTextures((BindableTexture)AllSpecialTextures.CHECKERED, (BindableTexture)AllSpecialTextures.HIGHLIGHT_CHECKERED).lineWidth(0.0625f);
            }
        }
    }
}
