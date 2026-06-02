/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  foundry.veil.Veil
 *  foundry.veil.api.compat.SodiumCompat
 *  it.unimi.dsi.fastutil.objects.Object2BooleanMap
 *  it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  org.apache.maven.artifact.versioning.ArtifactVersion
 *  org.apache.maven.artifact.versioning.DefaultArtifactVersion
 *  org.apache.maven.artifact.versioning.InvalidVersionSpecificationException
 *  org.apache.maven.artifact.versioning.VersionRange
 *  org.objectweb.asm.Type
 *  org.objectweb.asm.tree.AnnotationNode
 *  org.objectweb.asm.tree.ClassNode
 *  org.slf4j.Logger
 *  org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin
 *  org.spongepowered.asm.mixin.extensibility.IMixinInfo
 *  org.spongepowered.asm.service.MixinService
 *  org.spongepowered.asm.util.Annotations
 */
package dev.ryanhcode.sable.mixin;

import com.mojang.logging.LogUtils;
import dev.ryanhcode.sable.annotation.MixinModVersionConstraint;
import dev.ryanhcode.sable.platform.SableLoaderPlatform;
import foundry.veil.Veil;
import foundry.veil.api.compat.SodiumCompat;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.List;
import java.util.Set;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.MixinService;
import org.spongepowered.asm.util.Annotations;

public abstract class AbstractSableMixinPlugin
implements IMixinConfigPlugin {
    public static final Logger LOGGER = LogUtils.getLogger();
    private final Object2BooleanMap<String> modLoadedCache = new Object2BooleanOpenHashMap();
    private boolean sodiumPresent;

    public void onLoad(String mixinPackage) {
        this.sodiumPresent = SodiumCompat.isLoaded();
        LOGGER.info("Using {} renderer mixins", (Object)(this.sodiumPresent ? "Sodium" : "Vanilla"));
    }

    public String getRefMapperConfig() {
        return null;
    }

    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.startsWith("dev.ryanhcode.sable.mixin.sublevel_render.impl")) {
            return this.sodiumPresent ? mixinClassName.startsWith("dev.ryanhcode.sable.mixin.sublevel_render.impl.sodium") : mixinClassName.startsWith("dev.ryanhcode.sable.mixin.sublevel_render.impl.vanilla");
        }
        if (mixinClassName.startsWith("dev.ryanhcode.sable.mixin.compatibility.") || mixinClassName.startsWith("dev.ryanhcode.sable.neoforge.mixin.compatibility.") || mixinClassName.startsWith("dev.ryanhcode.sable.fabric.mixin.compatibility.")) {
            String[] parts = mixinClassName.split("\\.");
            if (parts.length < 5) {
                return true;
            }
            String modId = parts[3].equals("mixin") ? parts[5] : parts[6];
            boolean isModLoaded = this.modLoadedCache.computeIfAbsent((Object)modId, x -> Veil.platform().isModLoaded(modId));
            return isModLoaded && MixinConstraints.handleClassAnnotation(mixinClassName, modId);
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

    static class MixinConstraints {
        private static final Object2ObjectMap<String, String> MOD_VERSION_CACHE = new Object2ObjectOpenHashMap();

        MixinConstraints() {
        }

        static boolean handleClassAnnotation(String mixinClassName, String modId) {
            try {
                List nodes = MixinService.getService().getBytecodeProvider().getClassNode((String)mixinClassName).visibleAnnotations;
                if (nodes == null) {
                    return true;
                }
                return MixinConstraints.shouldApply(nodes, modId);
            }
            catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        static boolean shouldApply(List<AnnotationNode> nodes, String modId) throws InvalidVersionSpecificationException {
            for (AnnotationNode node : nodes) {
                if (!node.desc.equals(Type.getDescriptor(MixinModVersionConstraint.class))) continue;
                String range = (String)Annotations.getValue((AnnotationNode)node, (String)"value");
                VersionRange versionRange = VersionRange.createFromVersionSpec((String)range);
                String modVersion = (String)MOD_VERSION_CACHE.computeIfAbsent((Object)modId, x -> SableLoaderPlatform.INSTANCE.getModVersion(modId));
                DefaultArtifactVersion artifactVersion = new DefaultArtifactVersion(modVersion);
                return versionRange.containsVersion((ArtifactVersion)artifactVersion);
            }
            return true;
        }
    }
}
