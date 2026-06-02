/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.base.BlockBreakingKineticBlockEntity
 *  com.simibubi.create.content.kinetics.saw.SawBlock
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.block_breakers;

import com.simibubi.create.content.kinetics.base.BlockBreakingKineticBlockEntity;
import com.simibubi.create.content.kinetics.saw.SawBlock;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.neoforge.mixinhelper.compatibility.create.block_breakers.SubLevelBlockBreakingUtility;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={BlockBreakingKineticBlockEntity.class})
public abstract class BlockBreakingKineticBlockEntityMixin
extends BlockEntity {
    public BlockBreakingKineticBlockEntityMixin(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    @Shadow
    public abstract boolean canBreak(BlockState var1, float var2);

    @Redirect(remap=false, method={"tick"}, at=@At(value="INVOKE", target="Lcom/simibubi/create/content/kinetics/base/BlockBreakingKineticBlockEntity;getBreakingPos()Lnet/minecraft/core/BlockPos;"))
    private BlockPos sable$preGetBlockToBreak(BlockBreakingKineticBlockEntity be) {
        assert (this.level != null);
        BlockPos breakingPos = this.getBlockPos().relative((Direction)this.getBlockState().getValue((Property)BlockStateProperties.FACING));
        BlockState originalStateToBreak = this.level.getBlockState(breakingPos);
        if (!this.canBreak(originalStateToBreak, originalStateToBreak.getDestroySpeed((BlockGetter)this.level, breakingPos))) {
            return SubLevelBlockBreakingUtility.findBreakingPos((pos, state) -> this.canBreak((BlockState)state, state.getDestroySpeed((BlockGetter)this.level, pos)), Sable.HELPER.getContaining(this), this.getLevel(), Vec3.atLowerCornerOf((Vec3i)((Direction)this.getBlockState().getValue((Property)SawBlock.FACING)).getNormal()), this.getBlockPos().getCenter(), breakingPos);
        }
        return breakingPos;
    }
}
