/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  net.minecraft.commands.CommandBuildContext
 *  net.minecraft.commands.synchronization.ArgumentTypeInfo
 *  net.minecraft.commands.synchronization.ArgumentTypeInfo$Template
 *  net.minecraft.network.FriendlyByteBuf
 */
package dev.ryanhcode.sable.api.command;

import com.google.gson.JsonObject;
import dev.ryanhcode.sable.api.command.SubLevelArgumentType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;

public static class SubLevelArgumentType.Info
implements ArgumentTypeInfo<SubLevelArgumentType, Template> {
    private static final byte FLAG_MULTIPLE = 1;
    private static final byte FLAG_STATIC_ALLOWED = 2;

    public void serializeToNetwork(Template template, FriendlyByteBuf byteBuf) {
        int serialized = 0;
        if (template.allowMultiple) {
            serialized |= 1;
        }
        if (template.allowStaticLevel) {
            serialized |= 2;
        }
        byteBuf.writeByte(serialized);
    }

    public Template deserializeFromNetwork(FriendlyByteBuf arg) {
        byte serialized = arg.readByte();
        return new Template((serialized & 1) != 0, (serialized & 2) != 0);
    }

    public void serializeToJson(Template arg, JsonObject jsonObject) {
        jsonObject.addProperty("amount", arg.allowMultiple ? "single" : "multiple");
        jsonObject.addProperty("type", arg.allowStaticLevel ? "players" : "entities");
    }

    public Template unpack(SubLevelArgumentType arg) {
        return new Template(arg.allowMultiple, arg.allowStaticLevel);
    }

    public final class Template
    implements ArgumentTypeInfo.Template<SubLevelArgumentType> {
        final boolean allowMultiple;
        final boolean allowStaticLevel;

        Template(boolean allowMultiple, boolean allowStaticLevel) {
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
}
