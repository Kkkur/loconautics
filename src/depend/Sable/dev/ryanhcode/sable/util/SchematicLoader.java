/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.Util
 *  net.minecraft.core.HolderGetter
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.NbtAccounter
 *  net.minecraft.nbt.NbtIo
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.packs.resources.Resource
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate
 *  org.jetbrains.annotations.Nullable
 */
package dev.ryanhcode.sable.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.minecraft.Util;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.Nullable;

public class SchematicLoader {
    @Nullable
    public static StructureTemplate loadSchematic(ServerLevel level, ResourceLocation location) {
        StructureTemplate structureTemplate;
        block9: {
            String namespace = location.getNamespace();
            String path = "schematics/" + location.getPath() + ".nbt";
            ResourceLocation location1 = ResourceLocation.fromNamespaceAndPath((String)namespace, (String)path);
            Optional option = level.getServer().getResourceManager().getResource(location1);
            if (option.isEmpty()) {
                return null;
            }
            Resource resource = (Resource)option.get();
            InputStream stream = resource.open();
            try {
                StructureTemplate template = new StructureTemplate();
                CompoundTag nbt = NbtIo.readCompressed((InputStream)stream, (NbtAccounter)NbtAccounter.create((long)0x20000000L));
                template.load((HolderGetter)level.holderLookup(Registries.BLOCK), nbt);
                structureTemplate = template;
                if (stream == null) break block9;
            }
            catch (Throwable throwable) {
                try {
                    if (stream != null) {
                        try {
                            stream.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (IOException e) {
                    return null;
                }
            }
            stream.close();
        }
        return structureTemplate;
    }

    public static CompletableFuture<Set<ResourceLocation>> getSchematics(MinecraftServer server) {
        return CompletableFuture.supplyAsync(() -> server.getResourceManager().listResources("schematics", path -> path.getPath().endsWith(".nbt")).keySet(), Util.backgroundExecutor());
    }
}
