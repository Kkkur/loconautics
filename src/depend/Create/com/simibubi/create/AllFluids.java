/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.shaders.FogShape
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.tterrag.registrate.builders.FluidBuilder
 *  com.tterrag.registrate.builders.FluidBuilder$FluidTypeFactory
 *  com.tterrag.registrate.builders.ItemBuilder
 *  com.tterrag.registrate.util.entry.FluidEntry
 *  net.createmod.catnip.theme.Color
 *  net.minecraft.client.Camera
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.renderer.FogRenderer$FogMode
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.dispenser.BlockSource
 *  net.minecraft.core.dispenser.DefaultDispenseItemBehavior
 *  net.minecraft.core.dispenser.DispenseItemBehavior
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.item.BucketItem
 *  net.minecraft.world.item.DispensibleContainerItem
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.DispenserBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.level.material.MapColor
 *  net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions
 *  net.neoforged.neoforge.common.NeoForgeMod
 *  net.neoforged.neoforge.common.Tags$Fluids
 *  net.neoforged.neoforge.common.Tags$Items
 *  net.neoforged.neoforge.fluids.BaseFlowingFluid$Flowing
 *  net.neoforged.neoforge.fluids.BaseFlowingFluid$Source
 *  net.neoforged.neoforge.fluids.FluidInteractionRegistry
 *  net.neoforged.neoforge.fluids.FluidInteractionRegistry$InteractionInformation
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.FluidType
 *  net.neoforged.neoforge.fluids.FluidType$Properties
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3f
 */
package com.simibubi.create;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import com.simibubi.create.AllCreativeModeTabs;
import com.simibubi.create.AllTags;
import com.simibubi.create.Create;
import com.simibubi.create.content.decoration.palettes.AllPaletteStoneTypes;
import com.simibubi.create.content.fluids.VirtualFluid;
import com.simibubi.create.content.fluids.potion.PotionFluid;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.infrastructure.config.AllConfigs;
import com.tterrag.registrate.builders.FluidBuilder;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.util.entry.FluidEntry;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidInteractionRegistry;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class AllFluids {
    private static final CreateRegistrate REGISTRATE = Create.registrate();
    public static final FluidEntry<PotionFluid> POTION;
    public static final FluidEntry<VirtualFluid> TEA;
    public static final FluidEntry<BaseFlowingFluid.Flowing> HONEY;
    public static final FluidEntry<BaseFlowingFluid.Flowing> CHOCOLATE;
    private static final DispenseItemBehavior DEFAULT;
    private static final DispenseItemBehavior DISPENSE_FLUID;

    public static void register() {
    }

    public static void registerFluidInteractions() {
        FluidInteractionRegistry.addInteraction((FluidType)((FluidType)NeoForgeMod.LAVA_TYPE.value()), (FluidInteractionRegistry.InteractionInformation)new FluidInteractionRegistry.InteractionInformation(((BaseFlowingFluid.Flowing)HONEY.get()).getFluidType(), fluidState -> {
            if (fluidState.isSource()) {
                return Blocks.OBSIDIAN.defaultBlockState();
            }
            return ((Block)AllPaletteStoneTypes.LIMESTONE.getBaseBlock().get()).defaultBlockState();
        }));
        FluidInteractionRegistry.addInteraction((FluidType)((FluidType)NeoForgeMod.LAVA_TYPE.value()), (FluidInteractionRegistry.InteractionInformation)new FluidInteractionRegistry.InteractionInformation(((BaseFlowingFluid.Flowing)CHOCOLATE.get()).getFluidType(), fluidState -> {
            if (fluidState.isSource()) {
                return Blocks.OBSIDIAN.defaultBlockState();
            }
            return ((Block)AllPaletteStoneTypes.SCORIA.getBaseBlock().get()).defaultBlockState();
        }));
    }

    @Nullable
    public static BlockState getLavaInteraction(FluidState fluidState) {
        Fluid fluid = fluidState.getType();
        if (fluid.isSame((Fluid)HONEY.get())) {
            return ((Block)AllPaletteStoneTypes.LIMESTONE.getBaseBlock().get()).defaultBlockState();
        }
        if (fluid.isSame((Fluid)CHOCOLATE.get())) {
            return ((Block)AllPaletteStoneTypes.SCORIA.getBaseBlock().get()).defaultBlockState();
        }
        return null;
    }

    private static void registerFluidDispenseBehavior(BucketItem bucket) {
        DispenserBlock.registerBehavior((ItemLike)bucket, (DispenseItemBehavior)DISPENSE_FLUID);
    }

    static {
        REGISTRATE.setCreativeTab(AllCreativeModeTabs.BASE_CREATIVE_TAB);
        POTION = REGISTRATE.virtualFluid("potion", PotionFluid.PotionFluidType::new, PotionFluid::createSource, PotionFluid::createFlowing).lang("Potion").register();
        TEA = REGISTRATE.virtualFluid("tea").lang("Builder's Tea").tag(new TagKey[]{AllTags.AllFluidTags.TEA.tag}).register();
        HONEY = ((FluidBuilder)((ItemBuilder)((FluidBuilder)REGISTRATE.standardFluid("honey", SolidRenderedPlaceableFluidType.create(15380015, () -> Float.valueOf(0.125f * AllConfigs.client().honeyTransparencyMultiplier.getF()))).lang("Honey").properties(b -> b.viscosity(2000).density(1400)).fluidProperties(p -> p.levelDecreasePerBlock(2).tickRate(25).slopeFindDistance(3).explosionResistance(100.0f)).tag(new TagKey[]{Tags.Fluids.HONEY}).source(BaseFlowingFluid.Source::new).block().properties(p -> p.mapColor(MapColor.TERRACOTTA_YELLOW)).build()).bucket().onRegister(AllFluids::registerFluidDispenseBehavior)).tag(new TagKey[]{Tags.Items.BUCKETS, AllTags.AllItemTags.HONEY_BUCKETS.tag}).build()).register();
        CHOCOLATE = ((FluidBuilder)((ItemBuilder)((FluidBuilder)REGISTRATE.standardFluid("chocolate", SolidRenderedPlaceableFluidType.create(0x622020, () -> Float.valueOf(0.03125f * AllConfigs.client().chocolateTransparencyMultiplier.getF()))).lang("Chocolate").tag(new TagKey[]{AllTags.AllFluidTags.CHOCOLATE.tag}).properties(b -> b.viscosity(1500).density(1400)).fluidProperties(p -> p.levelDecreasePerBlock(2).tickRate(25).slopeFindDistance(3).explosionResistance(100.0f)).source(BaseFlowingFluid.Source::new).block().properties(p -> p.mapColor(MapColor.TERRACOTTA_BROWN)).build()).bucket().onRegister(AllFluids::registerFluidDispenseBehavior)).tag(new TagKey[]{Tags.Items.BUCKETS, AllTags.AllItemTags.CHOCOLATE_BUCKETS.tag}).build()).register();
        DEFAULT = new DefaultDispenseItemBehavior();
        DISPENSE_FLUID = new DefaultDispenseItemBehavior(){

            protected ItemStack execute(BlockSource pSource, ItemStack pStack) {
                DispensibleContainerItem dispensibleContainerItem = (DispensibleContainerItem)pStack.getItem();
                BlockPos pos = pSource.pos().relative((Direction)pSource.state().getValue((Property)DispenserBlock.FACING));
                ServerLevel level = pSource.level();
                if (dispensibleContainerItem.emptyContents(null, (Level)level, pos, null, pStack)) {
                    return new ItemStack((ItemLike)Items.BUCKET);
                }
                return DEFAULT.dispense(pSource, pStack);
            }
        };
    }

    private static class SolidRenderedPlaceableFluidType
    extends TintedFluidType {
        private Vector3f fogColor;
        private Supplier<Float> fogDistance;

        public static FluidBuilder.FluidTypeFactory create(int fogColor, Supplier<Float> fogDistance) {
            return (p, s, f) -> {
                SolidRenderedPlaceableFluidType fluidType = new SolidRenderedPlaceableFluidType(p, s, f);
                fluidType.fogColor = new Color(fogColor, false).asVectorF();
                fluidType.fogDistance = fogDistance;
                return fluidType;
            };
        }

        private SolidRenderedPlaceableFluidType(FluidType.Properties properties, ResourceLocation stillTexture, ResourceLocation flowingTexture) {
            super(properties, stillTexture, flowingTexture);
        }

        @Override
        protected int getTintColor(FluidStack stack) {
            return -1;
        }

        @Override
        public int getTintColor(FluidState state, BlockAndTintGetter world, BlockPos pos) {
            return 0xFFFFFF;
        }

        @Override
        protected Vector3f getCustomFogColor() {
            return this.fogColor;
        }

        @Override
        protected float getFogDistanceModifier() {
            return this.fogDistance.get().floatValue();
        }
    }

    public static abstract class TintedFluidType
    extends FluidType {
        protected static final int NO_TINT = -1;
        private final ResourceLocation stillTexture;
        private final ResourceLocation flowingTexture;

        public TintedFluidType(FluidType.Properties properties, ResourceLocation stillTexture, ResourceLocation flowingTexture) {
            super(properties);
            this.stillTexture = stillTexture;
            this.flowingTexture = flowingTexture;
        }

        public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
            consumer.accept(new IClientFluidTypeExtensions(){

                public ResourceLocation getStillTexture() {
                    return stillTexture;
                }

                public ResourceLocation getFlowingTexture() {
                    return flowingTexture;
                }

                public int getTintColor(FluidStack stack) {
                    return this.getTintColor(stack);
                }

                public int getTintColor(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
                    return this.getTintColor(state, getter, pos);
                }

                @NotNull
                public Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor) {
                    Vector3f customFogColor = this.getCustomFogColor();
                    return customFogColor == null ? fluidFogColor : customFogColor;
                }

                public void modifyFogRender(Camera camera, FogRenderer.FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, FogShape shape) {
                    float modifier = this.getFogDistanceModifier();
                    float baseWaterFog = 96.0f;
                    if (modifier != 1.0f) {
                        RenderSystem.setShaderFogShape((FogShape)FogShape.CYLINDER);
                        RenderSystem.setShaderFogStart((float)-8.0f);
                        RenderSystem.setShaderFogEnd((float)(baseWaterFog * modifier));
                    }
                }
            });
        }

        protected abstract int getTintColor(FluidStack var1);

        protected abstract int getTintColor(FluidState var1, BlockAndTintGetter var2, BlockPos var3);

        protected Vector3f getCustomFogColor() {
            return null;
        }

        protected float getFogDistanceModifier() {
            return 1.0f;
        }
    }
}
