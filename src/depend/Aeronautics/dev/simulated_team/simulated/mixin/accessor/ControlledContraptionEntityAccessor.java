/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.contraptions.ControlledContraptionEntity
 *  com.simibubi.create.content.contraptions.StructureTransform
 *  net.minecraft.core.BlockPos
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 *  org.spongepowered.asm.mixin.gen.Invoker
 */
package dev.simulated_team.simulated.mixin.accessor;

import com.simibubi.create.content.contraptions.ControlledContraptionEntity;
import com.simibubi.create.content.contraptions.StructureTransform;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value={ControlledContraptionEntity.class})
public interface ControlledContraptionEntityAccessor {
    @Accessor
    public BlockPos getControllerPos();

    @Invoker
    public StructureTransform invokeMakeStructureTransform();
}
