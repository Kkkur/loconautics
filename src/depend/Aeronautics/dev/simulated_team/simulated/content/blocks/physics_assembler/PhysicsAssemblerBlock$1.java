/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.state.properties.AttachFace
 */
package dev.simulated_team.simulated.content.blocks.physics_assembler;

import net.minecraft.world.level.block.state.properties.AttachFace;

static class PhysicsAssemblerBlock.1 {
    static final /* synthetic */ int[] $SwitchMap$net$minecraft$world$level$block$state$properties$AttachFace;

    static {
        $SwitchMap$net$minecraft$world$level$block$state$properties$AttachFace = new int[AttachFace.values().length];
        try {
            PhysicsAssemblerBlock.1.$SwitchMap$net$minecraft$world$level$block$state$properties$AttachFace[AttachFace.CEILING.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            PhysicsAssemblerBlock.1.$SwitchMap$net$minecraft$world$level$block$state$properties$AttachFace[AttachFace.FLOOR.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            PhysicsAssemblerBlock.1.$SwitchMap$net$minecraft$world$level$block$state$properties$AttachFace[AttachFace.WALL.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
