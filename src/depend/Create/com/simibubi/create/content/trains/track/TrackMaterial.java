/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.util.nullness.NonNullSupplier
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.trains.track;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.Create;
import com.simibubi.create.content.trains.track.ITrackBlock;
import com.simibubi.create.content.trains.track.TrackBlock;
import com.simibubi.create.content.trains.track.TrackMaterialFactory;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

public class TrackMaterial {
    public static final Map<ResourceLocation, TrackMaterial> ALL = new HashMap<ResourceLocation, TrackMaterial>();
    public static final TrackMaterial ANDESITE = TrackMaterialFactory.make(Create.asResource("andesite")).lang("Andesite").block((NonNullSupplier<NonNullSupplier<? extends TrackBlock>>)NonNullSupplier.lazy(() -> AllBlocks.TRACK)).particle(Create.asResource("block/palettes/stone_types/polished/andesite_cut_polished")).defaultModels().build();
    public final ResourceLocation id;
    public final String langName;
    public final NonNullSupplier<NonNullSupplier<? extends TrackBlock>> trackBlock;
    public final Ingredient sleeperIngredient;
    public final Ingredient railsIngredient;
    public final ResourceLocation particle;
    public final TrackType trackType;
    @Nullable
    private final TrackType.TrackBlockFactory customFactory;
    @OnlyIn(value=Dist.CLIENT)
    protected TrackModelHolder modelHolder;

    @OnlyIn(value=Dist.CLIENT)
    public TrackModelHolder getModelHolder() {
        return this.modelHolder;
    }

    public TrackMaterial(ResourceLocation id, String langName, NonNullSupplier<NonNullSupplier<? extends TrackBlock>> trackBlock, ResourceLocation particle, Ingredient sleeperIngredient, Ingredient railsIngredient, TrackType trackType, Supplier<Supplier<TrackModelHolder>> modelHolder) {
        this(id, langName, trackBlock, particle, sleeperIngredient, railsIngredient, trackType, modelHolder, null);
    }

    public TrackMaterial(ResourceLocation id, String langName, NonNullSupplier<NonNullSupplier<? extends TrackBlock>> trackBlock, ResourceLocation particle, Ingredient sleeperIngredient, Ingredient railsIngredient, TrackType trackType, Supplier<Supplier<TrackModelHolder>> modelHolder, @Nullable TrackType.TrackBlockFactory customFactory) {
        this.id = id;
        this.langName = langName;
        this.trackBlock = trackBlock;
        this.sleeperIngredient = sleeperIngredient;
        this.railsIngredient = railsIngredient;
        this.particle = particle;
        this.trackType = trackType;
        this.customFactory = customFactory;
        if (CatnipServices.PLATFORM.getEnv().isClient()) {
            this.modelHolder = modelHolder.get().get();
        }
        ALL.put(this.id, this);
    }

    public NonNullSupplier<? extends TrackBlock> getBlockSupplier() {
        return (NonNullSupplier)this.trackBlock.get();
    }

    public TrackBlock getBlock() {
        return (TrackBlock)this.getBlockSupplier().get();
    }

    public ItemStack asStack() {
        return this.asStack(1);
    }

    public ItemStack asStack(int count) {
        return new ItemStack((ItemLike)this.getBlock(), count);
    }

    public TrackBlock createBlock(BlockBehaviour.Properties properties) {
        return (this.customFactory != null ? this.customFactory : this.trackType.factory).create(properties, this);
    }

    public boolean isFromMod(String modId) {
        return this.id.getNamespace().equals(modId);
    }

    public static List<TrackMaterial> allFromMod(String modid) {
        return ALL.values().stream().filter(tm -> tm.isFromMod(modid)).toList();
    }

    public static List<NonNullSupplier<? extends Block>> allBlocksFromMod(String modid) {
        ArrayList<NonNullSupplier<? extends Block>> list = new ArrayList<NonNullSupplier<? extends Block>>();
        for (TrackMaterial material : TrackMaterial.allFromMod(modid)) {
            list.add(material.getBlockSupplier());
        }
        return list;
    }

    public static List<NonNullSupplier<? extends Block>> allBlocks() {
        ArrayList<NonNullSupplier<? extends Block>> list = new ArrayList<NonNullSupplier<? extends Block>>();
        for (TrackMaterial material : ALL.values()) {
            list.add(material.getBlockSupplier());
        }
        return list;
    }

    public String resourceName() {
        return this.id.getPath();
    }

    public static TrackMaterial deserialize(String serializedName) {
        if (serializedName.isBlank()) {
            return ANDESITE;
        }
        ResourceLocation id = ResourceLocation.tryParse((String)serializedName);
        if (ALL.containsKey(id)) {
            return ALL.get(id);
        }
        Create.LOGGER.error("Failed to locate serialized track material: " + serializedName);
        return ANDESITE;
    }

    public static TrackMaterial fromItem(Item item) {
        BlockItem blockItem;
        Block block;
        if (item instanceof BlockItem && (block = (blockItem = (BlockItem)item).getBlock()) instanceof ITrackBlock) {
            ITrackBlock trackBlock = (ITrackBlock)block;
            return trackBlock.getMaterial();
        }
        return ANDESITE;
    }

    @OnlyIn(value=Dist.CLIENT)
    public record TrackModelHolder(PartialModel tie, PartialModel leftSegment, PartialModel rightSegment) {
        static final TrackModelHolder DEFAULT = new TrackModelHolder(AllPartialModels.TRACK_TIE, AllPartialModels.TRACK_SEGMENT_LEFT, AllPartialModels.TRACK_SEGMENT_RIGHT);
    }

    public static class TrackType {
        public static final TrackType STANDARD = new TrackType(Create.asResource("standard"), TrackBlock::new);
        public final ResourceLocation id;
        protected final TrackBlockFactory factory;

        public TrackType(ResourceLocation id, TrackBlockFactory factory) {
            this.id = id;
            this.factory = factory;
        }

        @FunctionalInterface
        public static interface TrackBlockFactory {
            public TrackBlock create(BlockBehaviour.Properties var1, TrackMaterial var2);
        }
    }
}
