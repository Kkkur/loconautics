/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.contraptions.bearing.SailBlock
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  org.jetbrains.annotations.NotNull
 *  org.joml.Vector3dc
 *  org.spongepowered.asm.mixin.Mixin
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.sails_providing_lift;

import com.simibubi.create.content.contraptions.bearing.SailBlock;
import dev.ryanhcode.sable.api.block.BlockSubLevelCustomCenterOfMass;
import dev.ryanhcode.sable.api.block.BlockSubLevelLiftProvider;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value={SailBlock.class})
public class SailBlockMixin
implements BlockSubLevelLiftProvider,
BlockSubLevelCustomCenterOfMass {
    @Override
    @NotNull
    public Direction sable$getNormal(BlockState state) {
        return ((Direction)state.getValue((Property)BlockStateProperties.FACING)).getOpposite();
    }

    @Override
    public Vector3dc getCenterOfMass(BlockGetter blockGetter, BlockState state) {
        return JOMLConversion.HALF;
    }
}
