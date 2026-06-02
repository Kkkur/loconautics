/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.sugar.Local
 *  com.llamalad7.mixinextras.sugar.Share
 *  com.llamalad7.mixinextras.sugar.ref.LocalIntRef
 *  com.llamalad7.mixinextras.sugar.ref.LocalRef
 *  dev.ryanhcode.sable.companion.math.BoundingBox3d
 *  dev.ryanhcode.sable.companion.math.BoundingBox3dc
 *  dev.ryanhcode.sable.companion.math.BoundingBox3i
 *  dev.ryanhcode.sable.companion.math.BoundingBox3ic
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Holder
 *  net.minecraft.core.Position
 *  net.minecraft.core.SectionPos
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.gameevent.GameEvent
 *  net.minecraft.world.level.gameevent.GameEvent$Context
 *  net.minecraft.world.level.gameevent.GameEventDispatcher
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.ryanhcode.sable.mixin.sculk_vibrations;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.BoundingBox3dc;
import dev.ryanhcode.sable.companion.math.BoundingBox3i;
import dev.ryanhcode.sable.companion.math.BoundingBox3ic;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Position;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventDispatcher;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={GameEventDispatcher.class})
public class GameEventDispatcherMixin {
    @Final
    @Shadow
    private ServerLevel level;

    @Inject(method={"post"}, at={@At(value="NEW", target="java/util/ArrayList")})
    private void sable$useBBIntersection(Holder<GameEvent> gameEvent, Vec3 pos, GameEvent.Context context, CallbackInfo ci, @Share(value="bb") LocalRef<BoundingBox3ic> bbRef, @Local(ordinal=1) LocalIntRef x1, @Local(ordinal=2) LocalIntRef y1, @Local(ordinal=3) LocalIntRef z1, @Local(ordinal=4) LocalIntRef x2, @Local(ordinal=5) LocalIntRef y2, @Local(ordinal=6) LocalIntRef z2) {
        BoundingBox3ic bb = (BoundingBox3ic)bbRef.get();
        if (bb != null) {
            x1.set(SectionPos.blockToSectionCoord((int)bb.minX()));
            y1.set(SectionPos.blockToSectionCoord((int)bb.minY()));
            z1.set(SectionPos.blockToSectionCoord((int)bb.minZ()));
            x2.set(SectionPos.blockToSectionCoord((int)bb.maxX()));
            y2.set(SectionPos.blockToSectionCoord((int)bb.maxY()));
            z2.set(SectionPos.blockToSectionCoord((int)bb.maxZ()));
        }
    }

    @WrapMethod(method={"post"})
    private void sable$visitShipListeners(Holder<GameEvent> gameEvent, Vec3 pos, GameEvent.Context context, Operation<Void> original, @Share(value="bb") LocalRef<BoundingBox3ic> bbRef) {
        Vec3 globalPos = Sable.HELPER.projectOutOfSubLevel((Level)this.level, pos);
        original.call(new Object[]{gameEvent, globalPos, context});
        if (bbRef.get() != null) {
            return;
        }
        int radius = ((GameEvent)gameEvent.value()).notificationRadius();
        BoundingBox3d sourceBB = new BoundingBox3d(BlockPos.containing((Position)globalPos)).expand((double)radius);
        BoundingBox3i intersection = new BoundingBox3i();
        Sable.HELPER.getAllIntersecting((Level)this.level, (BoundingBox3dc)sourceBB).forEach(arg_0 -> GameEventDispatcherMixin.lambda$sable$visitShipListeners$0((BoundingBox3dc)sourceBB, bbRef, intersection, original, gameEvent, globalPos, context, arg_0));
    }

    private static /* synthetic */ void lambda$sable$visitShipListeners$0(BoundingBox3dc sourceBB, LocalRef bbRef, BoundingBox3i intersection, Operation original, Holder gameEvent, Vec3 globalPos, GameEvent.Context context, SubLevel subLevel) {
        BoundingBox3d plotBB = new BoundingBox3d(subLevel.getPlot().getBoundingBox());
        BoundingBox3d sourceInPlotBB = sourceBB.transformInverse((Pose3dc)subLevel.logicalPose(), new BoundingBox3d());
        bbRef.set((Object)intersection.set(plotBB.intersect((BoundingBox3dc)sourceInPlotBB)));
        original.call(new Object[]{gameEvent, globalPos, context});
    }
}
