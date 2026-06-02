/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DataResult$Error
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.simibubi.create.Create
 *  com.simibubi.create.content.redstone.link.IRedstoneLinkable
 *  com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler$Frequency
 *  it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap
 *  net.createmod.catnip.data.Couple
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.NbtOps
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.resources.RegistryOps
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.Create;
import com.simibubi.create.content.redstone.link.IRedstoneLinkable;
import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler;
import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.LinkedTypewriterBlockEntity;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.createmod.catnip.data.Couple;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LinkedTypewriterEntries {
    private final Int2ObjectLinkedOpenHashMap<KeyboardEntry> keyMap = new Int2ObjectLinkedOpenHashMap();
    private final Set<KeyboardEntry> newlyActivatedKeyboardEntries = new HashSet<KeyboardEntry>();
    private final Set<KeyboardEntry> newlyDeactivatedKeyboardEntries = new HashSet<KeyboardEntry>();

    public static LinkedTypewriterEntries readKeys(HolderLookup.Provider registryAccess, ListTag tags, BlockPos pos) {
        LinkedTypewriterEntries keys = new LinkedTypewriterEntries();
        for (Tag tag : tags) {
            RegistryOps ops = registryAccess.createSerializationContext((DynamicOps)NbtOps.INSTANCE);
            DataResult result = KeyboardEntry.CODEC.decode((DynamicOps)ops, (Object)tag);
            if (result.isError()) {
                Simulated.LOGGER.error(((DataResult.Error)result.error().get()).message());
                continue;
            }
            KeyboardEntry entry = (KeyboardEntry)((Pair)result.getOrThrow()).getFirst();
            entry.setLocation(pos);
            keys.setKey(entry.glfwKeyCode, entry);
        }
        return keys;
    }

    public void updateNetworks(Level level) {
        if (!level.isClientSide) {
            for (KeyboardEntry keyboardEntry : this.newlyActivatedKeyboardEntries) {
                Create.REDSTONE_LINK_NETWORK_HANDLER.addToNetwork((LevelAccessor)level, (IRedstoneLinkable)keyboardEntry);
            }
            this.newlyActivatedKeyboardEntries.clear();
            for (KeyboardEntry keyboardEntry : this.newlyDeactivatedKeyboardEntries) {
                Create.REDSTONE_LINK_NETWORK_HANDLER.removeFromNetwork((LevelAccessor)level, (IRedstoneLinkable)keyboardEntry);
            }
            this.newlyDeactivatedKeyboardEntries.clear();
        }
    }

    public void activateKey(int index, LinkedTypewriterBlockEntity lbe) {
        KeyboardEntry frequency = this.getEntry(index);
        if (frequency != null) {
            frequency.activate();
            this.newlyActivatedKeyboardEntries.add(frequency);
        }
    }

    public void deactivateKey(int index) {
        KeyboardEntry frequency = this.getEntry(index);
        if (frequency != null) {
            frequency.deactivate();
            this.newlyDeactivatedKeyboardEntries.add(frequency);
        }
    }

    public void deactivateAll() {
        this.keyMap.forEach((index, key) -> {
            if (key.isAlive()) {
                this.newlyDeactivatedKeyboardEntries.add((KeyboardEntry)key);
            }
            key.deactivate();
        });
    }

    public void setKey(int index, @Nullable KeyboardEntry keyboardEntry) {
        if (keyboardEntry == null) {
            this.keyMap.remove(index);
            return;
        }
        if (this.keyMap.containsKey(index)) {
            ((KeyboardEntry)this.keyMap.get(index)).deactivate();
        }
        this.keyMap.put(index, (Object)keyboardEntry);
    }

    public KeyboardEntry getEntry(int key) {
        return (KeyboardEntry)this.keyMap.get(key);
    }

    public void clearAll() {
        this.deactivateAll();
        this.keyMap.clear();
        this.newlyDeactivatedKeyboardEntries.clear();
        this.newlyActivatedKeyboardEntries.clear();
    }

    public void addAll(Map<Integer, KeyboardEntry> newMap) {
        this.keyMap.putAll(newMap);
    }

    public ListTag saveKeys(HolderLookup.Provider registryAccess) {
        ListTag tags = new ListTag();
        if (this.keyMap.isEmpty()) {
            return tags;
        }
        for (Map.Entry set : this.keyMap.entrySet()) {
            RegistryOps ops = registryAccess.createSerializationContext((DynamicOps)NbtOps.INSTANCE);
            DataResult result = KeyboardEntry.CODEC.encodeStart((DynamicOps)ops, (Object)((KeyboardEntry)set.getValue()));
            if (result.isError()) {
                Simulated.LOGGER.error(((DataResult.Error)result.error().get()).message());
                continue;
            }
            tags.add((Object)((Tag)result.getOrThrow()));
        }
        return tags;
    }

    public List<KeyboardEntry> getEntries() {
        return List.copyOf(this.keyMap.sequencedValues());
    }

    public int getSize() {
        return this.keyMap.size();
    }

    public Map<Integer, KeyboardEntry> getKeyMap() {
        return this.keyMap;
    }

    public static class KeyboardEntry
    implements IRedstoneLinkable {
        public static final Codec<KeyboardEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)ItemStack.OPTIONAL_CODEC.fieldOf("FirstItem").forGetter(KeyboardEntry::getFirstAsItemStack), (App)ItemStack.OPTIONAL_CODEC.fieldOf("SecondItem").forGetter(KeyboardEntry::getSecondAsItemStack), (App)Codec.INT.fieldOf("GLFWKey").forGetter(KeyboardEntry::getGLFWKeyCode)).apply((Applicative)instance, KeyboardEntry::createFromCodec));
        public static final StreamCodec<RegistryFriendlyByteBuf, KeyboardEntry> STREAM_CODEC = StreamCodec.composite((StreamCodec)ItemStack.OPTIONAL_STREAM_CODEC, KeyboardEntry::getFirstAsItemStack, (StreamCodec)ItemStack.OPTIONAL_STREAM_CODEC, KeyboardEntry::getSecondAsItemStack, (StreamCodec)ByteBufCodecs.INT, KeyboardEntry::getGLFWKeyCode, KeyboardEntry::createFromCodec);
        public final int glfwKeyCode;
        private final RedstoneLinkNetworkHandler.Frequency first;
        private final RedstoneLinkNetworkHandler.Frequency second;
        private boolean currentlyActive;
        private BlockPos pos;

        public KeyboardEntry(RedstoneLinkNetworkHandler.Frequency first, RedstoneLinkNetworkHandler.Frequency second, int constant, BlockPos pos) {
            if (first == null) {
                first = RedstoneLinkNetworkHandler.Frequency.EMPTY;
            }
            if (second == null) {
                second = RedstoneLinkNetworkHandler.Frequency.EMPTY;
            }
            this.first = first;
            this.second = second;
            this.currentlyActive = false;
            this.pos = pos;
            this.glfwKeyCode = constant;
        }

        private KeyboardEntry(RedstoneLinkNetworkHandler.Frequency first, RedstoneLinkNetworkHandler.Frequency second, int constant) {
            this(first, second, constant, null);
        }

        public static KeyboardEntry createFromCodec(ItemStack first, ItemStack second, int glfwKey) {
            RedstoneLinkNetworkHandler.Frequency firstFreq = RedstoneLinkNetworkHandler.Frequency.of((ItemStack)first);
            RedstoneLinkNetworkHandler.Frequency secondFreq = RedstoneLinkNetworkHandler.Frequency.of((ItemStack)second);
            return new KeyboardEntry(firstFreq, secondFreq, glfwKey);
        }

        private static Optional<Item> mapItem(Item item) {
            return item == Items.AIR ? Optional.empty() : Optional.of(item);
        }

        @NotNull
        private static Item mapOptional(Optional<Item> optional) {
            return optional.orElse(Items.AIR);
        }

        public void activate() {
            this.currentlyActive = true;
        }

        public void deactivate() {
            this.currentlyActive = false;
        }

        public Couple<RedstoneLinkNetworkHandler.Frequency> getAsCouple() {
            return Couple.create((Object)this.first, (Object)this.second);
        }

        public int getGLFWKeyCode() {
            return this.glfwKeyCode;
        }

        public RedstoneLinkNetworkHandler.Frequency getFirst() {
            return this.first;
        }

        public ItemStack getFirstAsItemStack() {
            return this.getFirst().getStack();
        }

        private Item getFirstItem() {
            return this.getFirstAsItemStack().getItem();
        }

        public RedstoneLinkNetworkHandler.Frequency getSecond() {
            return this.second;
        }

        public ItemStack getSecondAsItemStack() {
            return this.getSecond().getStack();
        }

        private Item getSecondItem() {
            return this.getSecondAsItemStack().getItem();
        }

        public int getTransmittedStrength() {
            return this.isAlive() ? 15 : 0;
        }

        public void setReceivedStrength(int i) {
        }

        public boolean isListening() {
            return false;
        }

        public boolean isAlive() {
            return this.currentlyActive;
        }

        public Couple<RedstoneLinkNetworkHandler.Frequency> getNetworkKey() {
            return this.getAsCouple();
        }

        public BlockPos getLocation() {
            return this.pos;
        }

        public void setLocation(BlockPos newPos) {
            this.pos = newPos;
        }
    }
}
