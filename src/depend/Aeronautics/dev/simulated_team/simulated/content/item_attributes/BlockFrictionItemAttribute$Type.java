/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  com.simibubi.create.content.logistics.item.filter.attribute.ItemAttribute
 *  com.simibubi.create.content.logistics.item.filter.attribute.ItemAttributeType
 *  dev.ryanhcode.sable.mixinterface.block_properties.BlockStateExtension
 *  dev.ryanhcode.sable.physics.config.block_properties.PhysicsBlockPropertyTypes
 *  dev.ryanhcode.sable.physics.config.block_properties.PhysicsBlockPropertyTypes$PhysicsBlockPropertyType
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  org.jetbrains.annotations.NotNull
 */
package dev.simulated_team.simulated.content.item_attributes;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttribute;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttributeType;
import dev.ryanhcode.sable.mixinterface.block_properties.BlockStateExtension;
import dev.ryanhcode.sable.physics.config.block_properties.PhysicsBlockPropertyTypes;
import dev.simulated_team.simulated.content.item_attributes.BlockFrictionItemAttribute;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public static class BlockFrictionItemAttribute.Type
implements ItemAttributeType {
    @NotNull
    public ItemAttribute createAttribute() {
        return new BlockFrictionItemAttribute(1.0);
    }

    public List<ItemAttribute> getAllAttributes(ItemStack stack, Level level) {
        Item item = stack.getItem();
        if (item instanceof BlockItem) {
            BlockItem item2 = (BlockItem)item;
            BlockStateExtension extension = (BlockStateExtension)item2.getBlock().defaultBlockState();
            double mass = (Double)extension.sable$getProperty((PhysicsBlockPropertyTypes.PhysicsBlockPropertyType)PhysicsBlockPropertyTypes.FRICTION.get());
            return List.of(new BlockFrictionItemAttribute(mass));
        }
        return List.of();
    }

    public MapCodec<? extends ItemAttribute> codec() {
        return CODEC;
    }

    public StreamCodec<? super RegistryFriendlyByteBuf, ? extends ItemAttribute> streamCodec() {
        return STREAM_CODEC;
    }
}
