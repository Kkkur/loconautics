/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.components.ComponentProcessors
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.decoration.ArmorStand
 *  net.minecraft.world.entity.decoration.ItemFrame
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.AbstractBannerBlock
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.DirtPathBlock
 *  net.minecraft.world.level.block.FarmBlock
 *  net.minecraft.world.level.block.SeaPickleBlock
 *  net.minecraft.world.level.block.SnowLayerBlock
 *  net.minecraft.world.level.block.TurtleEggBlock
 *  net.minecraft.world.level.block.entity.BannerBlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.block.state.properties.SlabType
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.schematics.requirement;

import com.simibubi.create.api.schematic.requirement.SchematicRequirementRegistries;
import com.simibubi.create.api.schematic.requirement.SpecialBlockEntityItemRequirement;
import com.simibubi.create.api.schematic.requirement.SpecialBlockItemRequirement;
import com.simibubi.create.api.schematic.requirement.SpecialEntityItemRequirement;
import com.simibubi.create.compat.Mods;
import com.simibubi.create.compat.framedblocks.FramedBlocksInSchematics;
import com.simibubi.create.foundation.mixin.accessor.ItemFrameAccessor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.createmod.catnip.components.ComponentProcessors;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.AbstractBannerBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirtPathBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.SeaPickleBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.TurtleEggBlock;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;
import org.jetbrains.annotations.Nullable;

public class ItemRequirement {
    public static final ItemRequirement NONE = new ItemRequirement(Collections.emptyList());
    public static final ItemRequirement INVALID = new ItemRequirement(Collections.emptyList());
    protected List<StackRequirement> requiredItems;

    public ItemRequirement(List<StackRequirement> requiredItems) {
        this.requiredItems = requiredItems;
    }

    public ItemRequirement(StackRequirement stackRequirement) {
        this(List.of(stackRequirement));
    }

    public ItemRequirement(ItemUseType usage, ItemStack stack) {
        this(new StackRequirement(stack, usage));
    }

    public ItemRequirement(ItemUseType usage, Item item) {
        this(usage, new ItemStack((ItemLike)item));
    }

    public ItemRequirement(ItemUseType usage, List<ItemStack> requiredItems) {
        this(requiredItems.stream().map(req -> new StackRequirement((ItemStack)req, usage)).collect(Collectors.toList()));
    }

    public static ItemRequirement of(BlockState state, @Nullable BlockEntity be) {
        ItemRequirement requirement;
        Block block = state.getBlock();
        SchematicRequirementRegistries.BlockRequirement blockRequirement = SchematicRequirementRegistries.BLOCKS.get(block);
        if (blockRequirement != null) {
            requirement = blockRequirement.getRequiredItems(state, be);
        } else if (block instanceof SpecialBlockItemRequirement) {
            SpecialBlockItemRequirement specialBlock = (SpecialBlockItemRequirement)block;
            requirement = specialBlock.getRequiredItems(state, be);
        } else {
            requirement = ItemRequirement.defaultOf(state, be);
        }
        if (be != null) {
            SchematicRequirementRegistries.BlockEntityRequirement beRequirement = SchematicRequirementRegistries.BLOCK_ENTITIES.get(be.getType());
            if (beRequirement != null) {
                requirement = requirement.union(beRequirement.getRequiredItems(be, state));
            } else if (be instanceof SpecialBlockEntityItemRequirement) {
                SpecialBlockEntityItemRequirement specialBE = (SpecialBlockEntityItemRequirement)be;
                requirement = requirement.union(specialBE.getRequiredItems(state));
            } else if (Mods.FRAMEDBLOCKS.contains((ItemLike)block)) {
                requirement = requirement.union(FramedBlocksInSchematics.getRequiredItems(state, be));
            }
        }
        return requirement;
    }

    private static ItemRequirement defaultOf(BlockState state, BlockEntity be) {
        Block block = state.getBlock();
        if (block == Blocks.AIR) {
            return NONE;
        }
        Item item = block.asItem();
        if (item == Items.AIR) {
            return INVALID;
        }
        if (state.hasProperty((Property)BlockStateProperties.SLAB_TYPE) && state.getValue((Property)BlockStateProperties.SLAB_TYPE) == SlabType.DOUBLE) {
            return new ItemRequirement(ItemUseType.CONSUME, new ItemStack((ItemLike)item, 2));
        }
        if (block instanceof TurtleEggBlock) {
            return new ItemRequirement(ItemUseType.CONSUME, new ItemStack((ItemLike)item, ((Integer)state.getValue((Property)TurtleEggBlock.EGGS)).intValue()));
        }
        if (block instanceof SeaPickleBlock) {
            return new ItemRequirement(ItemUseType.CONSUME, new ItemStack((ItemLike)item, ((Integer)state.getValue((Property)SeaPickleBlock.PICKLES)).intValue()));
        }
        if (block instanceof SnowLayerBlock) {
            return new ItemRequirement(ItemUseType.CONSUME, new ItemStack((ItemLike)item, ((Integer)state.getValue((Property)SnowLayerBlock.LAYERS)).intValue()));
        }
        if (block == BuiltInRegistries.BLOCK.get(com.simibubi.create.foundation.data.recipe.Mods.FD.asResource("rich_soil_farmland"))) {
            return new ItemRequirement(ItemUseType.CONSUME, (Item)BuiltInRegistries.ITEM.get(com.simibubi.create.foundation.data.recipe.Mods.FD.asResource("rich_soil")));
        }
        if (block instanceof FarmBlock || block instanceof DirtPathBlock) {
            return new ItemRequirement(ItemUseType.CONSUME, Items.DIRT);
        }
        if (block instanceof AbstractBannerBlock && be instanceof BannerBlockEntity) {
            BannerBlockEntity bannerBE = (BannerBlockEntity)be;
            return new ItemRequirement(new StrictNbtStackRequirement(bannerBE.getItem(), ItemUseType.CONSUME));
        }
        if (block == Blocks.TALL_GRASS) {
            return new ItemRequirement(ItemUseType.CONSUME, new ItemStack((ItemLike)Items.SHORT_GRASS, 2));
        }
        if (block == Blocks.LARGE_FERN) {
            return new ItemRequirement(ItemUseType.CONSUME, new ItemStack((ItemLike)Items.FERN, 2));
        }
        return new ItemRequirement(ItemUseType.CONSUME, item);
    }

    public static ItemRequirement of(Entity entity) {
        SchematicRequirementRegistries.EntityRequirement requirement = SchematicRequirementRegistries.ENTITIES.get(entity.getType());
        if (requirement != null) {
            return requirement.getRequiredItems(entity);
        }
        if (entity instanceof SpecialEntityItemRequirement) {
            SpecialEntityItemRequirement specialEntity = (SpecialEntityItemRequirement)entity;
            return specialEntity.getRequiredItems();
        }
        if (entity instanceof ItemFrame) {
            ItemFrame itemFrame = (ItemFrame)entity;
            ItemStack frame = ((ItemFrameAccessor)itemFrame).create$getFrameItemStack();
            ItemStack displayedItem = ComponentProcessors.withUnsafeComponentsDiscarded((ItemStack)itemFrame.getItem());
            if (displayedItem.isEmpty()) {
                return new ItemRequirement(ItemUseType.CONSUME, frame);
            }
            return new ItemRequirement(List.of(new StackRequirement(frame, ItemUseType.CONSUME), new StrictNbtStackRequirement(displayedItem, ItemUseType.CONSUME)));
        }
        if (entity instanceof ArmorStand) {
            ArmorStand armorStand = (ArmorStand)entity;
            ArrayList<StackRequirement> requirements = new ArrayList<StackRequirement>();
            requirements.add(new StackRequirement(new ItemStack((ItemLike)Items.ARMOR_STAND), ItemUseType.CONSUME));
            armorStand.getAllSlots().forEach(s -> requirements.add(new StrictNbtStackRequirement(ComponentProcessors.withUnsafeComponentsDiscarded((ItemStack)s), ItemUseType.CONSUME)));
            return new ItemRequirement(requirements);
        }
        return INVALID;
    }

    public boolean isEmpty() {
        return NONE == this;
    }

    public boolean isInvalid() {
        return INVALID == this;
    }

    public List<StackRequirement> getRequiredItems() {
        return this.requiredItems;
    }

    public ItemRequirement union(ItemRequirement other) {
        if (this.isInvalid() || other.isInvalid()) {
            return INVALID;
        }
        if (this.isEmpty()) {
            return other;
        }
        if (other.isEmpty()) {
            return this;
        }
        return new ItemRequirement(Stream.concat(this.requiredItems.stream(), other.requiredItems.stream()).collect(Collectors.toList()));
    }

    public static class StackRequirement {
        public final ItemStack stack;
        public final ItemUseType usage;

        public StackRequirement(ItemStack stack, ItemUseType usage) {
            this.stack = stack;
            this.usage = usage;
        }

        public boolean matches(ItemStack other) {
            return ItemStack.isSameItem((ItemStack)this.stack, (ItemStack)other);
        }
    }

    public static enum ItemUseType {
        CONSUME,
        DAMAGE;

    }

    public static class StrictNbtStackRequirement
    extends StackRequirement {
        public StrictNbtStackRequirement(ItemStack stack, ItemUseType usage) {
            super(stack, usage);
        }

        @Override
        public boolean matches(ItemStack other) {
            return ItemStack.isSameItemSameComponents((ItemStack)this.stack, (ItemStack)other);
        }
    }
}
