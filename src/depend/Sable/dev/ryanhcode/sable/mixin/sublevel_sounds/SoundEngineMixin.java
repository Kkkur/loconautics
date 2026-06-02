/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.resources.sounds.SoundInstance
 *  net.minecraft.client.resources.sounds.TickableSoundInstance
 *  net.minecraft.client.sounds.ChannelAccess$ChannelHandle
 *  net.minecraft.client.sounds.SoundEngine
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.ModifyVariable
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.LocalCapture
 */
package dev.ryanhcode.sable.mixin.sublevel_sounds;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sound.MovingSoundInstanceDelegate;
import dev.ryanhcode.sable.sound.SoundInstanceDelegated;
import dev.ryanhcode.sable.sublevel.SubLevel;
import java.util.Iterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value={SoundEngine.class})
public class SoundEngineMixin {
    @ModifyVariable(method={"play"}, at=@At(value="HEAD"), argsOnly=true)
    private SoundInstance sable$play(SoundInstance instance) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return instance;
        }
        SubLevel subLevel = Sable.HELPER.getContaining((Level)level, instance.getX(), instance.getZ());
        if (subLevel != null) {
            return new MovingSoundInstanceDelegate(instance, subLevel);
        }
        return instance;
    }

    @ModifyVariable(method={"stop(Lnet/minecraft/client/resources/sounds/SoundInstance;)V"}, at=@At(value="HEAD"), argsOnly=true)
    private SoundInstance sable$stop(SoundInstance instance) {
        SoundInstanceDelegated delegated;
        if (instance instanceof SoundInstanceDelegated && (delegated = (SoundInstanceDelegated)instance).getDelegate() != null) {
            return delegated.getDelegate();
        }
        return instance;
    }

    @Inject(method={"tickNonPaused"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/sounds/ChannelAccess$ChannelHandle;execute(Ljava/util/function/Consumer;)V", shift=At.Shift.AFTER, ordinal=0)}, locals=LocalCapture.CAPTURE_FAILEXCEPTION)
    private void sable$tick(CallbackInfo ci, Iterator<TickableSoundInstance> sounds, TickableSoundInstance sound, float volume, float pitch, Vec3 pos, ChannelAccess.ChannelHandle access) {
        if (sound instanceof MovingSoundInstanceDelegate) {
            MovingSoundInstanceDelegate delegated = (MovingSoundInstanceDelegate)sound;
            access.execute(delegated::tickWithChannel);
        }
    }

    @Inject(method={"stop(Lnet/minecraft/client/resources/sounds/SoundInstance;)V"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/sounds/ChannelAccess$ChannelHandle;execute(Ljava/util/function/Consumer;)V", shift=At.Shift.AFTER)}, locals=LocalCapture.CAPTURE_FAILEXCEPTION)
    private void sable$clear(SoundInstance sound, CallbackInfo ci, ChannelAccess.ChannelHandle access) {
        if (sound instanceof MovingSoundInstanceDelegate) {
            MovingSoundInstanceDelegate delegated = (MovingSoundInstanceDelegate)sound;
            access.execute(delegated::unload);
        }
    }
}
