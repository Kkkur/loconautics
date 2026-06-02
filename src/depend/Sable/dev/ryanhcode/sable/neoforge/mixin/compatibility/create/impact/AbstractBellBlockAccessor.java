/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.equipment.bell.AbstractBellBlock
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Invoker
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.impact;

import com.simibubi.create.content.equipment.bell.AbstractBellBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value={AbstractBellBlock.class})
public interface AbstractBellBlockAccessor {
    @Invoker
    public boolean invokeRing(Level var1, BlockPos var2, Direction var3, Player var4);
}
