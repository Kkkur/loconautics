/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Local
 *  com.simibubi.create.content.redstone.link.IRedstoneLinkable
 *  com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler
 *  com.simibubi.create.infrastructure.config.AllConfigs
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.redstone_links;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.redstone.link.IRedstoneLinkable;
import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler;
import com.simibubi.create.infrastructure.config.AllConfigs;
import dev.ryanhcode.sable.ActiveSableCompanion;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={RedstoneLinkNetworkHandler.class})
public class RedstoneLinkNetworkHandlerMixin {
    @Redirect(method={"updateNetworkOf"}, at=@At(value="INVOKE", target="Lcom/simibubi/create/content/redstone/link/RedstoneLinkNetworkHandler;withinRange(Lcom/simibubi/create/content/redstone/link/IRedstoneLinkable;Lcom/simibubi/create/content/redstone/link/IRedstoneLinkable;)Z"), remap=false)
    private boolean sable$projectComparisons(IRedstoneLinkable from, IRedstoneLinkable to, @Local(argsOnly=true) LevelAccessor levelAccessor) {
        SubLevel toSublevel;
        Level level = (Level)levelAccessor;
        if (from == to) {
            return true;
        }
        Vector3d fromPos = JOMLConversion.atCenterOf((Vec3i)from.getLocation());
        Vector3d toPos = JOMLConversion.atCenterOf((Vec3i)to.getLocation());
        ActiveSableCompanion helper = Sable.HELPER;
        SubLevel fromSublevel = helper.getContaining(level, (Vector3dc)fromPos);
        if (fromSublevel != null) {
            fromSublevel.logicalPose().transformPosition(fromPos);
        }
        if ((toSublevel = helper.getContaining(level, (Vector3dc)toPos)) != null) {
            toSublevel.logicalPose().transformPosition(toPos);
        }
        int linkRange = (Integer)AllConfigs.server().logistics.linkRange.get();
        return fromPos.distanceSquared((Vector3dc)toPos) < (double)(linkRange * linkRange);
    }
}
