/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  com.simibubi.create.AllBlocks
 *  com.simibubi.create.content.kinetics.belt.BeltBlock
 *  com.simibubi.create.content.kinetics.millstone.MillstoneBlock
 *  com.tterrag.registrate.util.entry.BlockEntry
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.entity_falls_on_block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.millstone.MillstoneBlock;
import com.tterrag.registrate.util.entry.BlockEntry;
import dev.ryanhcode.sable.ActiveSableCompanion;
import dev.ryanhcode.sable.Sable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value={BeltBlock.class, MillstoneBlock.class})
public class BeltMillstoneBlocksMixin
extends Block {
    public BeltMillstoneBlocksMixin(BlockBehaviour.Properties pProperties) {
        super(pProperties);
    }

    @WrapOperation(method={"updateEntityAfterFallOn"}, at={@At(value="INVOKE", target="Lnet/minecraft/world/entity/Entity;blockPosition()Lnet/minecraft/core/BlockPos;")})
    public BlockPos sable$checkForSubLevels(Entity instance, Operation<BlockPos> original) {
        Level level = instance.level();
        BlockEntry entry = this instanceof BeltBlock ? AllBlocks.BELT : AllBlocks.MILLSTONE;
        ActiveSableCompanion helper = Sable.HELPER;
        BlockPos gatheredBeltPos = helper.runIncludingSubLevels(level, instance.position(), true, null, (subLevel, internalPos) -> {
            if (entry.has(level.getBlockState(internalPos))) {
                return internalPos;
            }
            if (entry.has(level.getBlockState(internalPos.below()))) {
                return internalPos.below();
            }
            return null;
        });
        if (gatheredBeltPos != null) {
            return gatheredBeltPos;
        }
        return (BlockPos)original.call(new Object[]{instance});
    }
}
