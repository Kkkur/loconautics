/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.foundation.data.CreateBlockEntityBuilder
 *  com.tterrag.registrate.AbstractRegistrate
 *  com.tterrag.registrate.builders.BlockEntityBuilder$BlockEntityFactory
 *  com.tterrag.registrate.builders.BuilderCallback
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package dev.simulated_team.simulated.registrate;

import com.simibubi.create.foundation.data.CreateBlockEntityBuilder;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.builders.BlockEntityBuilder;
import com.tterrag.registrate.builders.BuilderCallback;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class SimBlockEntityBuilder<T extends BlockEntity, P>
extends CreateBlockEntityBuilder<T, P> {
    public SimBlockEntityBuilder(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, BlockEntityBuilder.BlockEntityFactory<T> factory) {
        super(owner, parent, name, callback, factory);
    }
}
