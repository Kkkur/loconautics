/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 *  net.minecraft.commands.synchronization.ArgumentTypeInfo
 *  net.minecraft.commands.synchronization.ArgumentTypeInfo$Template
 *  net.minecraft.commands.synchronization.ArgumentTypeInfos
 *  net.minecraft.commands.synchronization.SingletonArgumentInfo
 *  net.minecraft.core.Registry
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.ryanhcode.sable.mixin.command;

import com.mojang.brigadier.arguments.ArgumentType;
import dev.ryanhcode.sable.api.command.SubLevelArgumentType;
import dev.ryanhcode.sable.command.Vec3ArgumentAbsolute;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={ArgumentTypeInfos.class})
public abstract class ArgumentTypeInfosMixin {
    @Shadow
    private static <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>> ArgumentTypeInfo<A, T> register(Registry<ArgumentTypeInfo<?, ?>> arg, String string, Class<? extends A> class_, ArgumentTypeInfo<A, T> arg2) {
        return null;
    }

    @Inject(method={"bootstrap"}, at={@At(value="TAIL")})
    private static void sable$bootstrap(Registry<ArgumentTypeInfo<?, ?>> registry, CallbackInfoReturnable<ArgumentTypeInfo<?, ?>> cir) {
        ArgumentTypeInfosMixin.register(registry, "sable:sub_level", SubLevelArgumentType.class, new SubLevelArgumentType.Info());
        ArgumentTypeInfosMixin.register(registry, "sable:vec3_absolute", Vec3ArgumentAbsolute.class, SingletonArgumentInfo.contextFree(Vec3ArgumentAbsolute::vec3));
    }
}
