/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.components.toasts.SystemToast
 *  net.minecraft.client.gui.components.toasts.SystemToast$SystemToastId
 *  net.minecraft.client.gui.components.toasts.ToastComponent
 *  net.minecraft.client.server.IntegratedServer
 *  net.minecraft.network.chat.Component
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 */
package dev.ryanhcode.sable.mixin.toast;

import dev.ryanhcode.sable.index.SableToasts;
import dev.ryanhcode.sable.mixinterface.toast.SableToastableServer;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.storage.holding.GlobalSavedSubLevelPointer;
import dev.ryanhcode.sable.sublevel.storage.serialization.SubLevelData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={IntegratedServer.class})
public class IntegratedServerMixin
implements SableToastableServer {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Override
    public void sable$reportSubLevelLoadFailure(GlobalSavedSubLevelPointer pointer) {
        SystemToast.addOrUpdate((ToastComponent)this.minecraft.getToasts(), (SystemToast.SystemToastId)SableToasts.SUB_LEVEL_LOAD_FAILURE, (Component)Component.translatable((String)"sub_level.toast.loadFailure", (Object[])new Object[]{Component.literal((String)pointer.toString())}).withStyle(ChatFormatting.RED), (Component)Component.translatable((String)"sub_level.toast.checkLog"));
    }

    @Override
    public void sable$reportSubLevelSaveFailure(SubLevelData data) {
        SystemToast.addOrUpdate((ToastComponent)this.minecraft.getToasts(), (SystemToast.SystemToastId)SableToasts.SUB_LEVEL_SAVE_FAILURE, (Component)Component.translatable((String)"sub_level.toast.saveFailure", (Object[])new Object[]{Component.literal((String)data.toString())}).withStyle(ChatFormatting.RED), (Component)Component.translatable((String)"sub_level.toast.checkLog"));
    }

    @Override
    public void sable$reportSubLevelPhysicsFailure(ServerSubLevel data) {
        SystemToast.addOrUpdate((ToastComponent)this.minecraft.getToasts(), (SystemToast.SystemToastId)SableToasts.SUB_LEVEL_PHYSICS_FAILURE, (Component)Component.translatable((String)"sub_level.toast.physicsFailure", (Object[])new Object[]{Component.literal((String)data.toString())}).withStyle(ChatFormatting.RED), (Component)Component.translatable((String)"sub_level.toast.attemptingRecovery"));
    }
}
