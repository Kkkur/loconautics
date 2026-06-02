/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.commands.CommandBuildContext
 *  net.minecraft.commands.synchronization.ArgumentTypeInfo
 *  net.minecraft.commands.synchronization.ArgumentTypeInfo$Template
 */
package dev.ryanhcode.sable.api.command;

import dev.ryanhcode.sable.api.command.SubLevelArgumentType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;

public final class SubLevelArgumentType.Info.Template
implements ArgumentTypeInfo.Template<SubLevelArgumentType> {
    final boolean allowMultiple;
    final boolean allowStaticLevel;

    SubLevelArgumentType.Info.Template(boolean allowMultiple, boolean allowStaticLevel) {
        this.allowMultiple = allowMultiple;
        this.allowStaticLevel = allowStaticLevel;
    }

    public SubLevelArgumentType instantiate(CommandBuildContext commandBuildContext) {
        return new SubLevelArgumentType(this.allowStaticLevel, this.allowMultiple);
    }

    public ArgumentTypeInfo<SubLevelArgumentType, ?> type() {
        return Info.this;
    }
}
