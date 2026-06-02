/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.minecraft.server.commands.data.DataCommands
 *  net.minecraft.server.commands.data.DataCommands$DataProvider
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 */
package dev.ryanhcode.sable.mixin.command;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.ryanhcode.sable.command.data_accessor.SubLevelDataAccessor;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collection;
import java.util.function.Function;
import net.minecraft.server.commands.data.DataCommands;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value={DataCommands.class})
public class DataCommandsMixin {
    @WrapOperation(method={"<clinit>"}, at={@At(value="INVOKE", target="Lcom/google/common/collect/ImmutableList;of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableList;", remap=false)})
    private static <E> ImmutableList<Function<String, DataCommands.DataProvider>> sable$allProviders(E e1, E e2, E e3, Operation<ImmutableList<Function<String, DataCommands.DataProvider>>> original) {
        ImmutableList providers = (ImmutableList)original.call(new Object[]{e1, e2, e3});
        ObjectArrayList mutableList = new ObjectArrayList((Collection)providers);
        mutableList.add(SubLevelDataAccessor.PROVIDER);
        return ImmutableList.copyOf((Collection)mutableList);
    }
}
