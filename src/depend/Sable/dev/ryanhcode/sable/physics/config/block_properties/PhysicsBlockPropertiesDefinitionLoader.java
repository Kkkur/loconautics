/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.JsonElement
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectList
 *  net.minecraft.core.Holder
 *  net.minecraft.core.HolderSet$Named
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.resources.ResourceManager
 *  net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener
 *  net.minecraft.tags.TagKey
 *  net.minecraft.util.ExtraCodecs$TagOrElementLocation
 *  net.minecraft.util.profiling.ProfilerFiller
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition
 */
package dev.ryanhcode.sable.physics.config.block_properties;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.mixinterface.block_properties.BlockStateExtension;
import dev.ryanhcode.sable.physics.config.block_properties.PhysicsBlockPropertiesDefinition;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class PhysicsBlockPropertiesDefinitionLoader
extends SimpleJsonResourceReloadListener {
    public static final String NAME = "physics_block_properties";
    public static final ResourceLocation ID = Sable.sablePath("physics_block_properties");
    protected static final Gson GSON = new Gson();
    public static final PhysicsBlockPropertiesDefinitionLoader INSTANCE = new PhysicsBlockPropertiesDefinitionLoader();
    private final ObjectList<PhysicsBlockPropertiesDefinition> definitions = new ObjectArrayList();

    private PhysicsBlockPropertiesDefinitionLoader() {
        super(GSON, NAME);
    }

    public String getName() {
        return super.getName();
    }

    protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        this.definitions.clear();
        for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
            ResourceLocation file = entry.getKey();
            JsonElement json = entry.getValue();
            DataResult decoded = PhysicsBlockPropertiesDefinition.CODEC.decode((DynamicOps)JsonOps.INSTANCE, (Object)json);
            decoded.result().ifPresent(pair -> {
                PhysicsBlockPropertiesDefinition definition = (PhysicsBlockPropertiesDefinition)pair.getFirst();
                this.definitions.add((Object)definition);
            });
            decoded.error().ifPresent(error -> Sable.LOGGER.error("Error while loading physics block properties entry: {}", error));
        }
        this.definitions.sort(Comparator.comparingInt(PhysicsBlockPropertiesDefinition::priority));
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static void applyToBlocks(PhysicsBlockPropertiesDefinition definition) {
        ExtraCodecs.TagOrElementLocation selector = definition.selector();
        ObjectArrayList blocks = new ObjectArrayList(16);
        if (selector.tag()) {
            TagKey tagKey = TagKey.create((ResourceKey)Registries.BLOCK, (ResourceLocation)selector.id());
            Optional tagBlocks = BuiltInRegistries.BLOCK.getTag(tagKey);
            if (!tagBlocks.isPresent()) throw new IllegalStateException("Unknown tag: %s".formatted(selector.id()));
            HolderSet.Named blockHolders = (HolderSet.Named)tagBlocks.get();
            for (Holder blockHolder : blockHolders) {
                Block block = (Block)blockHolder.value();
                blocks.add((Object)block);
            }
        } else {
            Block block = (Block)BuiltInRegistries.BLOCK.get(selector.id());
            blocks.add((Object)block);
        }
        for (Block block : blocks) {
            StateDefinition stateDefinition = block.getStateDefinition();
            for (BlockState state : stateDefinition.getPossibleStates()) {
                ((BlockStateExtension)state).sable$loadProperties((StateDefinition<Block, BlockState>)stateDefinition, definition);
            }
        }
    }

    public void applyAll() {
        for (PhysicsBlockPropertiesDefinition definition : this.definitions) {
            PhysicsBlockPropertiesDefinitionLoader.applyToBlocks(definition);
        }
    }

    public Collection<PhysicsBlockPropertiesDefinition> getDefinitions() {
        return this.definitions;
    }
}
