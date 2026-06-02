/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders
 *  net.createmod.catnip.lang.Lang
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.StringRepresentable
 *  net.minecraft.world.item.alchemy.Potion
 *  net.minecraft.world.item.alchemy.PotionContents
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.material.FluidState
 *  net.neoforged.neoforge.fluids.BaseFlowingFluid$Properties
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.FluidType$Properties
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.content.fluids.potion;

import com.mojang.serialization.Codec;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllFluids;
import com.simibubi.create.content.fluids.VirtualFluid;
import com.simibubi.create.content.fluids.potion.PotionFluidHandler;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.lang.Lang;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

public class PotionFluid
extends VirtualFluid {
    public static PotionFluid createSource(BaseFlowingFluid.Properties properties) {
        return new PotionFluid(properties, true);
    }

    public static PotionFluid createFlowing(BaseFlowingFluid.Properties properties) {
        return new PotionFluid(properties, false);
    }

    public PotionFluid(BaseFlowingFluid.Properties properties, boolean source) {
        super(properties, source);
    }

    public static FluidStack of(int amount, PotionContents potionContents, BottleType bottleType) {
        FluidStack fluidStack = new FluidStack(((PotionFluid)((Object)AllFluids.POTION.get())).getSource(), amount);
        PotionFluid.addPotionToFluidStack(fluidStack, potionContents);
        fluidStack.set(AllDataComponents.POTION_FLUID_BOTTLE_TYPE, (Object)bottleType);
        return fluidStack;
    }

    public static FluidStack addPotionToFluidStack(FluidStack fs, PotionContents potionContents) {
        if (potionContents == PotionContents.EMPTY) {
            fs.remove(DataComponents.POTION_CONTENTS);
            return fs;
        }
        fs.set(DataComponents.POTION_CONTENTS, (Object)potionContents);
        return fs;
    }

    public static class PotionFluidType
    extends AllFluids.TintedFluidType {
        public PotionFluidType(FluidType.Properties properties, ResourceLocation stillTexture, ResourceLocation flowingTexture) {
            super(properties, stillTexture, flowingTexture);
        }

        @Override
        public int getTintColor(FluidStack stack) {
            return ((PotionContents)stack.getOrDefault(DataComponents.POTION_CONTENTS, (Object)PotionContents.EMPTY)).getColor() | 0xFF000000;
        }

        public String getDescriptionId(FluidStack stack) {
            PotionContents contents = (PotionContents)stack.getOrDefault(DataComponents.POTION_CONTENTS, (Object)PotionContents.EMPTY);
            ItemLike itemFromBottleType = PotionFluidHandler.itemFromBottleType((BottleType)((Object)stack.getOrDefault(AllDataComponents.POTION_FLUID_BOTTLE_TYPE, (Object)BottleType.REGULAR)));
            return Potion.getName((Optional)contents.potion(), (String)(itemFromBottleType.asItem().getDescriptionId() + ".effect."));
        }

        @Override
        protected int getTintColor(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
            return -1;
        }
    }

    public static enum BottleType implements StringRepresentable
    {
        REGULAR,
        SPLASH,
        LINGERING;

        public static final Codec<BottleType> CODEC;
        public static final StreamCodec<ByteBuf, BottleType> STREAM_CODEC;

        @NotNull
        public String getSerializedName() {
            return Lang.asId((String)this.name());
        }

        static {
            CODEC = StringRepresentable.fromEnum(BottleType::values);
            STREAM_CODEC = CatnipStreamCodecBuilders.ofEnum(BottleType.class);
        }
    }
}
