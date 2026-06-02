/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.fluids.OpenEndedPipe
 *  net.createmod.catnip.math.BlockFace
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.fluid_handling;

import com.simibubi.create.content.fluids.OpenEndedPipe;
import dev.ryanhcode.sable.ActiveSableCompanion;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.createmod.catnip.math.BlockFace;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={OpenEndedPipe.class})
public abstract class OpenEndedPipeMixin {
    @Shadow
    private BlockPos outputPos;
    @Shadow
    private Level world;
    @Unique
    private BlockPos sable$plotOutputPos;

    @Shadow
    public abstract BlockPos getPos();

    @Inject(method={"<init>"}, at={@At(value="TAIL")}, remap=false)
    private void sable$saveCurrentPos(BlockFace face, CallbackInfo ci) {
        this.sable$plotOutputPos = this.outputPos;
    }

    @Redirect(method={"*"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"))
    private BlockState sable$getBlockstateInclSublevels(Level level, BlockPos pos) {
        this.outputPos = this.sable$plotOutputPos;
        ActiveSableCompanion helper = Sable.HELPER;
        Vec3 checkPos = Vec3.atCenterOf((Vec3i)this.sable$plotOutputPos);
        BlockState gatheredState = helper.runIncludingSubLevels(level, checkPos, true, helper.getContaining(level, (Position)checkPos), this::sable$gatherState);
        if (gatheredState == null) {
            this.outputPos = this.sable$plotOutputPos;
            gatheredState = level.getBlockState(this.sable$plotOutputPos);
        }
        return gatheredState;
    }

    @Unique
    private BlockState sable$gatherState(SubLevel level, BlockPos b) {
        BlockState checkedState = this.world.getBlockState(b);
        if (!checkedState.isAir()) {
            this.outputPos = b;
            return checkedState;
        }
        return null;
    }

    @Redirect(method={"provideFluidToSpace"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", ordinal=1))
    private boolean sable$preventInWorldPlace(Level instance, BlockPos pPos, BlockState pNesubleveltate, int pFlags) {
        return instance.setBlock(this.sable$plotOutputPos, pNesubleveltate, 3);
    }
}
