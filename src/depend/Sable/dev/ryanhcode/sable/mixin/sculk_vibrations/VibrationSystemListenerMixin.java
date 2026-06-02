/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  net.minecraft.core.Holder
 *  net.minecraft.core.Position
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.gameevent.GameEvent
 *  net.minecraft.world.level.gameevent.GameEvent$Context
 *  net.minecraft.world.level.gameevent.vibrations.VibrationSystem$Data
 *  net.minecraft.world.level.gameevent.vibrations.VibrationSystem$Listener
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 */
package dev.ryanhcode.sable.mixin.sculk_vibrations;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.ryanhcode.sable.ActiveSableCompanion;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value={VibrationSystem.Listener.class})
public class VibrationSystemListenerMixin {
    @WrapMethod(method={"scheduleVibration"})
    private void sable$useGlobalPos(ServerLevel level, VibrationSystem.Data data, Holder<GameEvent> gameEvent, GameEvent.Context context, Vec3 pos, Vec3 sensorPos, Operation<Void> original) {
        original.call(new Object[]{level, data, gameEvent, context, Sable.HELPER.projectOutOfSubLevel((Level)level, pos), Sable.HELPER.projectOutOfSubLevel((Level)level, sensorPos)});
    }

    @WrapMethod(method={"isOccluded"})
    private static boolean sable$occlusionChecks(Level level, Vec3 pos1, Vec3 pos2, Operation<Boolean> original) {
        SubLevel l2;
        Vec3 global2;
        ActiveSableCompanion helper = Sable.HELPER;
        Vec3 global1 = helper.projectOutOfSubLevel(level, pos1);
        if (((Boolean)original.call(new Object[]{level, global1, global2 = helper.projectOutOfSubLevel(level, pos2)})).booleanValue()) {
            return true;
        }
        SubLevel l1 = helper.getContaining(level, (Position)pos1);
        if (l1 == (l2 = helper.getContaining(level, (Position)pos2))) {
            if (l1 == null) {
                return false;
            }
            return (Boolean)original.call(new Object[]{level, pos1, pos2});
        }
        if (l2 != null && ((Boolean)original.call(new Object[]{level, l2.logicalPose().transformPositionInverse(global1), pos2})).booleanValue()) {
            return true;
        }
        return l1 != null && (Boolean)original.call(new Object[]{level, l1.logicalPose().transformPositionInverse(global2), pos2}) != false;
    }
}
