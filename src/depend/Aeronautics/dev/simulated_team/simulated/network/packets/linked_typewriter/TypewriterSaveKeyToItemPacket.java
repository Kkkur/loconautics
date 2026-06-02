/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DataResult$Error
 *  com.mojang.serialization.DynamicOps
 *  foundry.veil.api.network.handler.ServerPacketContext
 *  net.minecraft.core.component.DataComponentType
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.NbtOps
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 *  net.minecraft.resources.RegistryOps
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.component.CustomData
 */
package dev.simulated_team.simulated.network.packets.linked_typewriter;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.LinkedTypewriterEntries;
import dev.simulated_team.simulated.index.SimBlocks;
import foundry.veil.api.network.handler.ServerPacketContext;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public record TypewriterSaveKeyToItemPacket(InteractionHand hand, LinkedTypewriterEntries.KeyboardEntry entry) implements CustomPacketPayload
{
    public static CustomPacketPayload.Type<TypewriterSaveKeyToItemPacket> TYPE = new CustomPacketPayload.Type(Simulated.path("linked_typewriter_bind_item"));
    public static StreamCodec<RegistryFriendlyByteBuf, TypewriterSaveKeyToItemPacket> CODEC = StreamCodec.composite((StreamCodec)ByteBufCodecs.INT, packet -> packet.hand.ordinal(), LinkedTypewriterEntries.KeyboardEntry.STREAM_CODEC, TypewriterSaveKeyToItemPacket::entry, (h, e) -> new TypewriterSaveKeyToItemPacket(InteractionHand.values()[h], (LinkedTypewriterEntries.KeyboardEntry)e));

    public void handle(ServerPacketContext context) {
        ServerPlayer player = context.player();
        ItemStack item = player.getItemInHand(this.hand);
        CompoundTag currentTag = new CompoundTag();
        if (item.has(DataComponents.BLOCK_ENTITY_DATA)) {
            currentTag = ((CustomData)item.get(DataComponents.BLOCK_ENTITY_DATA)).copyTag();
        } else {
            currentTag.putString("id", item.getItem().toString());
        }
        RegistryOps ops = context.level().registryAccess().createSerializationContext((DynamicOps)NbtOps.INSTANCE);
        DataResult result = LinkedTypewriterEntries.KeyboardEntry.CODEC.encodeStart((DynamicOps)ops, (Object)this.entry);
        if (result.isError()) {
            Simulated.LOGGER.warn("Unable to process entry for item saving!: {}", (Object)((DataResult.Error)result.error().get()).message());
            return;
        }
        CompoundTag entryTag = (CompoundTag)result.getOrThrow();
        if (!currentTag.contains("Keys")) {
            currentTag.put("Keys", (Tag)new ListTag());
        }
        ListTag keys = currentTag.getList("Keys", 10);
        boolean alreadyPresent = false;
        for (int i = 0; i < keys.size(); ++i) {
            Tag key = keys.get(i);
            int glfwKey = ((CompoundTag)key).getInt("GLFWKey");
            if (glfwKey != this.entry.glfwKeyCode) continue;
            alreadyPresent = true;
            keys.set(i, (Tag)entryTag);
            break;
        }
        if (!alreadyPresent) {
            keys.add((Object)entryTag);
        }
        currentTag.put("Keys", (Tag)keys);
        if (item.is(SimBlocks.LINKED_TYPEWRITER.asItem())) {
            CustomData.set((DataComponentType)DataComponents.BLOCK_ENTITY_DATA, (ItemStack)item, (CompoundTag)currentTag);
        }
    }

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
