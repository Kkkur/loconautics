/*
 * Decompiled with CFR 0.152.
 */
package dev.ryanhcode.sable.api;

public static enum SubLevelAssemblyHelper.GatherResult.State {
    SUCCESS("commands.sable.sub_level.assemble.connected.success"),
    TOO_MANY_BLOCKS("commands.sable.sub_level.assemble.connected.too_many_blocks"),
    NO_BLOCKS("commands.sable.sub_level.assemble.no_blocks");

    public final String errorKey;

    private SubLevelAssemblyHelper.GatherResult.State(String errorKey) {
        this.errorKey = errorKey;
    }
}
