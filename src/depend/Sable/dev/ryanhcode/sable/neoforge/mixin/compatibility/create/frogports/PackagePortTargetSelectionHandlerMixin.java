/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.logistics.packagePort.PackagePortTargetSelectionHandler
 *  com.simibubi.create.infrastructure.config.AllConfigs
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Vector3d
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 *  org.spongepowered.asm.mixin.Shadow
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.frogports;

import com.simibubi.create.content.logistics.packagePort.PackagePortTargetSelectionHandler;
import com.simibubi.create.infrastructure.config.AllConfigs;
import dev.ryanhcode.sable.ActiveSableCompanion;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={PackagePortTargetSelectionHandler.class})
public class PackagePortTargetSelectionHandlerMixin {
    @Shadow
    public static boolean isPostbox;

    @Overwrite
    public static String validateDiff(Vec3 nonProjectedTarget, BlockPos placedPos) {
        ActiveSableCompanion helper = Sable.HELPER;
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        Level level = player.level();
        Vector3d target = helper.projectOutOfSubLevel(level, JOMLConversion.toJOML((Position)nonProjectedTarget));
        SubLevel frogSubLevel = helper.getContaining(level, (Vec3i)placedPos);
        if (frogSubLevel != null) {
            frogSubLevel.logicalPose().transformPositionInverse(target);
        }
        Vector3d localDiff = target.sub((double)placedPos.getX() + 0.5, (double)placedPos.getY(), (double)placedPos.getZ() + 0.5);
        if (localDiff.y < 0.0 && !isPostbox) {
            return "package_port.cannot_reach_down";
        }
        double packagePortRange = ((Integer)AllConfigs.server().logistics.packagePortRange.get()).intValue();
        if (localDiff.lengthSquared() > packagePortRange * packagePortRange) {
            return "package_port.too_far";
        }
        return null;
    }
}
