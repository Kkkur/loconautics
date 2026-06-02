/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.data.IntAttached
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.UUIDUtil
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.world.level.LevelAccessor
 *  org.jetbrains.annotations.Nullable
 *  org.jetbrains.annotations.Unmodifiable
 */
package com.simibubi.create.content.logistics.tableCloth;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.packager.InventorySummary;
import com.simibubi.create.content.logistics.tableCloth.TableClothBlockEntity;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.IntAttached;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.LevelAccessor;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

public record ShoppingListItem.ShoppingList(@Unmodifiable List<IntAttached<BlockPos>> purchases, UUID shopOwner, UUID shopNetwork) {
    public static final Codec<ShoppingListItem.ShoppingList> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.list((Codec)IntAttached.codec((Codec)BlockPos.CODEC)).fieldOf("purchases").forGetter(ShoppingListItem.ShoppingList::purchases), (App)UUIDUtil.CODEC.fieldOf("shop_owner").forGetter(ShoppingListItem.ShoppingList::shopOwner), (App)UUIDUtil.CODEC.fieldOf("shop_network").forGetter(ShoppingListItem.ShoppingList::shopNetwork)).apply((Applicative)instance, ShoppingListItem.ShoppingList::new));
    public static final StreamCodec<ByteBuf, ShoppingListItem.ShoppingList> STREAM_CODEC = StreamCodec.composite((StreamCodec)CatnipStreamCodecBuilders.list((StreamCodec)IntAttached.streamCodec((StreamCodec)BlockPos.STREAM_CODEC)), ShoppingListItem.ShoppingList::purchases, (StreamCodec)UUIDUtil.STREAM_CODEC, ShoppingListItem.ShoppingList::shopOwner, (StreamCodec)UUIDUtil.STREAM_CODEC, ShoppingListItem.ShoppingList::shopNetwork, ShoppingListItem.ShoppingList::new);

    public ShoppingListItem.ShoppingList duplicate() {
        return new ShoppingListItem.ShoppingList(new ArrayList<IntAttached<BlockPos>>(this.purchases.stream().map(ia -> IntAttached.with((int)((Integer)ia.getFirst()), (Object)((BlockPos)ia.getSecond()))).toList()), this.shopOwner, this.shopNetwork);
    }

    public int getPurchases(BlockPos clothPos) {
        for (IntAttached<BlockPos> entry : this.purchases) {
            if (!clothPos.equals(entry.getValue())) continue;
            return (Integer)entry.getFirst();
        }
        return 0;
    }

    public Couple<InventorySummary> bakeEntries(LevelAccessor level, @Nullable BlockPos clothPosToIgnore) {
        InventorySummary input = new InventorySummary();
        InventorySummary output = new InventorySummary();
        for (IntAttached<BlockPos> entry : this.purchases) {
            Object object;
            if (clothPosToIgnore != null && clothPosToIgnore.equals(entry.getValue()) || !((object = level.getBlockEntity((BlockPos)entry.getValue())) instanceof TableClothBlockEntity)) continue;
            TableClothBlockEntity dcbe = (TableClothBlockEntity)object;
            input.add(dcbe.getPaymentItem(), dcbe.getPaymentAmount() * (Integer)entry.getFirst());
            object = dcbe.requestData.encodedRequest().stacks().iterator();
            while (object.hasNext()) {
                BigItemStack stackEntry = (BigItemStack)object.next();
                output.add(stackEntry.stack, stackEntry.count * (Integer)entry.getFirst());
            }
        }
        return Couple.create((Object)output, (Object)input);
    }

    public static class Mutable {
        private final List<IntAttached<BlockPos>> purchases = new ArrayList<IntAttached<BlockPos>>();
        private final UUID shopOwner;
        private final UUID shopNetwork;

        public Mutable(ShoppingListItem.ShoppingList list) {
            this.purchases.addAll(list.purchases);
            this.shopOwner = list.shopOwner;
            this.shopNetwork = list.shopNetwork;
        }

        public void addPurchases(BlockPos clothPos, int amount) {
            for (IntAttached<BlockPos> entry : this.purchases) {
                if (!clothPos.equals(entry.getValue())) continue;
                entry.setFirst((Object)((Integer)entry.getFirst() + amount));
                return;
            }
            this.purchases.add((IntAttached<BlockPos>)IntAttached.with((int)amount, (Object)clothPos));
        }

        public ShoppingListItem.ShoppingList toImmutable() {
            return new ShoppingListItem.ShoppingList(this.purchases, this.shopOwner, this.shopNetwork);
        }
    }
}
