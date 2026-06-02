/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition
 */
package dev.ryanhcode.sable.mixinterface.block_properties;

import dev.ryanhcode.sable.physics.config.block_properties.PhysicsBlockPropertiesDefinition;
import dev.ryanhcode.sable.physics.config.block_properties.PhysicsBlockPropertyTypes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public interface BlockStateExtension {
    public void sable$loadProperties(StateDefinition<Block, BlockState> var1, PhysicsBlockPropertiesDefinition var2);

    public <T> T sable$getProperty(PhysicsBlockPropertyTypes.PhysicsBlockPropertyType<T> var1);
}
