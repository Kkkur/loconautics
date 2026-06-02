/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  org.apache.maven.artifact.versioning.ArtifactVersion
 *  org.apache.maven.artifact.versioning.DefaultArtifactVersion
 *  org.apache.maven.artifact.versioning.InvalidVersionSpecificationException
 *  org.apache.maven.artifact.versioning.VersionRange
 *  org.objectweb.asm.Type
 *  org.objectweb.asm.tree.AnnotationNode
 *  org.spongepowered.asm.service.MixinService
 *  org.spongepowered.asm.util.Annotations
 */
package dev.ryanhcode.sable.mixin;

import dev.ryanhcode.sable.annotation.MixinModVersionConstraint;
import dev.ryanhcode.sable.platform.SableLoaderPlatform;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.List;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.spongepowered.asm.service.MixinService;
import org.spongepowered.asm.util.Annotations;

static class AbstractSableMixinPlugin.MixinConstraints {
    private static final Object2ObjectMap<String, String> MOD_VERSION_CACHE = new Object2ObjectOpenHashMap();

    AbstractSableMixinPlugin.MixinConstraints() {
    }

    static boolean handleClassAnnotation(String mixinClassName, String modId) {
        try {
            List nodes = MixinService.getService().getBytecodeProvider().getClassNode((String)mixinClassName).visibleAnnotations;
            if (nodes == null) {
                return true;
            }
            return AbstractSableMixinPlugin.MixinConstraints.shouldApply(nodes, modId);
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
