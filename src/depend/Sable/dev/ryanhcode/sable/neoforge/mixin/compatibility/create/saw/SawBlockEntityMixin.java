/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.base.BlockBreakingKineticBlockEntity
 *  com.simibubi.create.content.kinetics.saw.SawBlockEntity
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.saw;

import com.simibubi.create.content.kinetics.base.BlockBreakingKineticBlockEntity;
import com.simibubi.create.content.kinetics.saw.SawBlockEntity;
import dev.ryanhcode.sable.ActiveSableCompanion;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={SawBlockEntity.class})
public abstract class SawBlockEntityMixin
extends BlockBreakingKineticBlockEntity {
    public SawBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Redirect(method={"dropItemFromCutTree"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/phys/Vec3;atLowerCornerOf(Lnet/minecraft/core/Vec3i;)Lnet/minecraft/world/phys/Vec3;"))
    private Vec3 sable$itemDeltaMovement(Vec3i vec3i) {
        ActiveSableCompanion helper = Sable.HELPER;
        Vector3d diff = helper.projectOutOfSubLevel(this.level, JOMLConversion.atCenterOf((Vec3i)this.breakingPos)).sub((Vector3dc)helper.projectOutOfSubLevel(this.level, JOMLConversion.atCenterOf((Vec3i)this.worldPosition)));
        SubLevel subLevel = helper.getContaining((BlockEntity)this);
        if (subLevel != null) {
            subLevel.logicalPose().transformNormalInverse(diff);
        }
        return JOMLConversion.toMojang((Vector3dc)diff);
    }
}
