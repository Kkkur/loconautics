/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 */
package com.simibubi.create.content.logistics.redstoneRequester;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.logistics.stockTicker.PackageOrderWithCrafts;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public record AutoRequestData(PackageOrderWithCrafts encodedRequest, String encodedTargetAddress, BlockPos targetOffset, String targetDim, boolean isValid) {
    public static final Codec<AutoRequestData> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)PackageOrderWithCrafts.CODEC.fieldOf("encoded_request").forGetter(i -> i.encodedRequest), (App)Codec.STRING.fieldOf("encoded_target_address").forGetter(i -> i.encodedTargetAddress), (App)BlockPos.CODEC.fieldOf("target_offset").forGetter(i -> i.targetOffset), (App)Codec.STRING.fieldOf("target_dim").forGetter(i -> i.targetDim), (App)Codec.BOOL.fieldOf("is_valid").forGetter(i -> i.isValid)).apply((Applicative)instance, AutoRequestData::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, AutoRequestData> STREAM_CODEC = StreamCodec.composite(PackageOrderWithCrafts.STREAM_CODEC, i -> i.encodedRequest, (StreamCodec)ByteBufCodecs.STRING_UTF8, i -> i.encodedTargetAddress, (StreamCodec)BlockPos.STREAM_CODEC, i -> i.targetOffset, (StreamCodec)ByteBufCodecs.STRING_UTF8, i -> i.targetDim, (StreamCodec)ByteBufCodecs.BOOL, i -> i.isValid, AutoRequestData::new);

    public AutoRequestData() {
        this(PackageOrderWithCrafts.empty(), "", BlockPos.ZERO, "null", false);
    }

    public void writeToItem(BlockPos position, ItemStack itemStack) {
        Mutable mutable = new Mutable(this);
        mutable.targetOffset = position.offset((Vec3i)this.targetOffset);
        itemStack.set(AllDataComponents.AUTO_REQUEST_DATA, (Object)mutable.toImmutable());
    }

    public static AutoRequestData readFromItem(Level level, Player player, BlockPos position, ItemStack itemStack) {
        AutoRequestData requestData = (AutoRequestData)itemStack.get(AllDataComponents.AUTO_REQUEST_DATA);
        if (requestData == null) {
            return null;
        }
        Mutable mutable = new Mutable(requestData);
        mutable.targetOffset = mutable.targetOffset.subtract((Vec3i)position);
        boolean bl = mutable.isValid = mutable.targetOffset.closerThan((Vec3i)BlockPos.ZERO, 128.0) && requestData.targetDim.equals(level.dimension().location().toString());
        if (player != null) {
            CreateLang.translate(mutable.isValid ? "redstone_requester.keeper_connected" : "redstone_requester.keeper_too_far_away", new Object[0]).style(mutable.isValid ? ChatFormatting.WHITE : ChatFormatting.RED).sendStatus(player);
        }
        return mutable.toImmutable();
    }

    public static class Mutable {
        public PackageOrderWithCrafts encodedRequest = PackageOrderWithCrafts.empty();
        public String encodedTargetAddress = "";
        public BlockPos targetOffset = BlockPos.ZERO;
        public String targetDim = "null";
        public boolean isValid = false;

        public Mutable() {
        }

        public Mutable(AutoRequestData data) {
            this.encodedRequest = data.encodedRequest;
            this.encodedTargetAddress = data.encodedTargetAddress;
            this.targetOffset = data.targetOffset;
            this.targetDim = data.targetDim;
            this.isValid = data.isValid;
        }

        public AutoRequestData toImmutable() {
            return new AutoRequestData(this.encodedRequest, this.encodedTargetAddress, this.targetOffset, this.targetDim, this.isValid);
        }
    }
}
