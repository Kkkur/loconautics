/*
 * Decompiled with CFR 0.152.
 */
package com.simibubi.create.content.contraptions.mounted;

public static enum CartAssemblerBlock.CartAssemblerAction {
    ASSEMBLE,
    DISASSEMBLE,
    ASSEMBLE_ACCELERATE,
    DISASSEMBLE_BRAKE,
    ASSEMBLE_ACCELERATE_DIRECTIONAL,
    PASS;


    public boolean shouldAssemble() {
        return this == ASSEMBLE || this == ASSEMBLE_ACCELERATE || this == ASSEMBLE_ACCELERATE_DIRECTIONAL;
    }

    public boolean shouldDisassemble() {
        return this == DISASSEMBLE || this == DISASSEMBLE_BRAKE;
    }
}
