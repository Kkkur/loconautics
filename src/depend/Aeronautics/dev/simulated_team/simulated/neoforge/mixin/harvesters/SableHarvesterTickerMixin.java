/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  dev.ryanhcode.sable.neoforge.mixinhelper.compatibility.create.harvester.HarvesterTicker
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  org.spongepowered.asm.mixin.Mixin
 */
package dev.simulated_team.simulated.neoforge.mixin.harvesters;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.ryanhcode.sable.neoforge.mixinhelper.compatibility.create.harvester.HarvesterTicker;
import dev.simulated_team.simulated.content.blocks.auger_shaft.BlockHarvester;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value={HarvesterTicker.class})
public class SableHarvesterTickerMixin {
    @WrapMethod(method={"dropItem"})
    private static void deferDrop(Level level, ItemStack dropped, BlockPos sable$selfPos, Operation<Void> original) {
        BlockHarvester bh;
        BlockEntity be = level.getBlockEntity(sable$selfPos);
        if (be instanceof BlockHarvester && !(dropped = (bh = (BlockHarvester)be).depositItemStack(sable$selfPos, dropped)).isEmpty()) {
            original.call(new Object[]{level, dropped, sable$selfPos});
        }
    }
}
