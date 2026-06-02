/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.commands.arguments.NbtPathArgument$NbtPath
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.NbtUtils
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.commands.data.DataAccessor
 *  net.minecraft.server.commands.data.DataCommands$DataProvider
 */
package dev.ryanhcode.sable.command.data_accessor;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.ryanhcode.sable.api.command.SubLevelArgumentType;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import java.util.Locale;
import java.util.function.Function;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.data.DataAccessor;
import net.minecraft.server.commands.data.DataCommands;

public class SubLevelDataAccessor
implements DataAccessor {
    public static final Function<String, DataCommands.DataProvider> PROVIDER = string -> new DataCommands.DataProvider((String)string){
        final /* synthetic */ String val$string;
        {
            this.val$string = string;
        }

        public DataAccessor access(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
            return new SubLevelDataAccessor(SubLevelArgumentType.getSingleSubLevel(commandContext, this.val$string));
        }

        public ArgumentBuilder<CommandSourceStack, ?> wrap(ArgumentBuilder<CommandSourceStack, ?> argumentBuilder, Function<ArgumentBuilder<CommandSourceStack, ?>, ArgumentBuilder<CommandSourceStack, ?>> function) {
            return argumentBuilder.then(Commands.literal((String)"sub_level").then(function.apply((ArgumentBuilder<CommandSourceStack, ?>)Commands.argument((String)this.val$string, (ArgumentType)SubLevelArgumentType.singleSubLevel()))));
        }
    };
    private final ServerSubLevel subLevel;

    public SubLevelDataAccessor(ServerSubLevel subLevel) {
        this.subLevel = subLevel;
    }

    public void setData(CompoundTag compoundTag) {
        this.subLevel.setUserDataTag(compoundTag);
    }

    public CompoundTag getData() {
        CompoundTag userTag = this.subLevel.getUserDataTag();
        return userTag != null ? userTag : new CompoundTag();
    }

    public Component getModifiedSuccess() {
        return Component.translatable((String)"commands.data.sub_level.modified", (Object[])new Object[]{this.subLevel.toString()});
    }

    public Component getPrintSuccess(Tag tag) {
        return Component.translatable((String)"commands.data.sub_level.query", (Object[])new Object[]{this.subLevel.toString(), NbtUtils.toPrettyComponent((Tag)tag)});
    }

    public Component getPrintSuccess(NbtPathArgument.NbtPath nbtPath, double d, int i) {
        return Component.translatable((String)"commands.data.sub_level.get", (Object[])new Object[]{nbtPath.asString(), this.subLevel.toString(), String.format(Locale.ROOT, "%.2f", d), i});
    }
}
