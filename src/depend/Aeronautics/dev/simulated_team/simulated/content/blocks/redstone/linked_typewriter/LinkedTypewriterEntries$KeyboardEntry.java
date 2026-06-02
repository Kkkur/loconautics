/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.simibubi.create.content.redstone.link.IRedstoneLinkable
 *  com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler$Frequency
 *  net.createmod.catnip.data.Couple
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  org.jetbrains.annotations.NotNull
 */
package dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.redstone.link.IRedstoneLinkable;
import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler;
import java.util.Optional;
import net.createmod.catnip.data.Couple;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public static class LinkedTypewriterEntries.KeyboardEntry
implements IRedstoneLinkable {
    public static final Codec<LinkedTypewriterEntries.KeyboardEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)ItemStack.OPTIONAL_CODEC.fieldOf("FirstItem").forGetter(LinkedTypewriterEntries.KeyboardEntry::getFirstAsItemStack), (App)ItemStack.OPTIONAL_CODEC.fieldOf("SecondItem").forGetter(LinkedTypewriterEntries.KeyboardEntry::getSecondAsItemStack), (App)Codec.INT.fieldOf("GLFWKey").forGetter(LinkedTypewriterEntries.KeyboardEntry::getGLFWKeyCode)).apply((Applicative)instance, LinkedTypewriterEntries.KeyboardEntry::createFromCodec));
    public static final StreamCodec<RegistryFriendlyByteBuf, LinkedTypewriterEntries.KeyboardEntry> STREAM_CODEC = StreamCodec.composite((StreamCodec)ItemStack.OPTIONAL_STREAM_CODEC, LinkedTypewriterEntries.KeyboardEntry::getFirstAsItemStack, (StreamCodec)ItemStack.OPTIONAL_STREAM_CODEC, LinkedTypewriterEntries.KeyboardEntry::getSecondAsItemStack, (StreamCodec)ByteBufCodecs.INT, LinkedTypewriterEntries.KeyboardEntry::getGLFWKeyCode, LinkedTypewriterEntries.KeyboardEntry::createFromCodec);
    public final int glfwKeyCode;
    private final RedstoneLinkNetworkHandler.Frequency first;
    private final RedstoneLinkNetworkHandler.Frequency second;
    private boolean currentlyActive;
    private BlockPos pos;

    public LinkedTypewriterEntries.KeyboardEntry(RedstoneLinkNetworkHandler.Frequency first, RedstoneLinkNetworkHandler.Frequency second, int constant, BlockPos pos) {
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

    private LinkedTypewriterEntries.KeyboardEntry(RedstoneLinkNetworkHandler.Frequency first, RedstoneLinkNetworkHandler.Frequency second, int constant) {
        this(first, second, constant, null);
    }

    public static LinkedTypewriterEntries.KeyboardEntry createFromCodec(ItemStack first, ItemStack second, int glfwKey) {
        RedstoneLinkNetworkHandler.Frequency firstFreq = RedstoneLinkNetworkHandler.Frequency.of((ItemStack)first);
        RedstoneLinkNetworkHandler.Frequency secondFreq = RedstoneLinkNetworkHandler.Frequency.of((ItemStack)second);
        return new LinkedTypewriterEntries.KeyboardEntry(firstFreq, secondFreq, glfwKey);
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
