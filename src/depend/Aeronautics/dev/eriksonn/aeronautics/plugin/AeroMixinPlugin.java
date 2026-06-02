/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  foundry.veil.api.compat.IrisCompat
 *  foundry.veil.api.compat.SodiumCompat
 *  org.objectweb.asm.tree.ClassNode
 *  org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin
 *  org.spongepowered.asm.mixin.extensibility.IMixinInfo
 */
package dev.eriksonn.aeronautics.plugin;

import foundry.veil.api.compat.IrisCompat;
import foundry.veil.api.compat.SodiumCompat;
import java.util.List;
import java.util.Set;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public class AeroMixinPlugin
implements IMixinConfigPlugin {
    private boolean sodiumPresent;
    private boolean irisPresent;

    public void onLoad(String mixinPackage) {
        this.sodiumPresent = SodiumCompat.isLoaded();
        this.irisPresent = IrisCompat.isLoaded();
    }

    public String getRefMapperConfig() {
        return null;
    }

    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.startsWith("dev.eriksonn.aeronautics.mixin.render.vanilla")) {
            return !this.sodiumPresent;
        }
        if (mixinClassName.startsWith("dev.eriksonn.aeronautics.mixin.render.sodium")) {
            return this.sodiumPresent;
        }
        if (mixinClassName.startsWith("dev.eriksonn.aeronautics.mixin.render.iris")) {
            return this.irisPresent;
        }
        return true;
    }

    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    public List<String> getMixins() {
        return null;
    }

    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
