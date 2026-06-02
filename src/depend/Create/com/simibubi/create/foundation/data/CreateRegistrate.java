/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.AbstractRegistrate
 *  com.tterrag.registrate.builders.BlockBuilder
 *  com.tterrag.registrate.builders.BlockEntityBuilder$BlockEntityFactory
 *  com.tterrag.registrate.builders.Builder
 *  com.tterrag.registrate.builders.BuilderCallback
 *  com.tterrag.registrate.builders.FluidBuilder
 *  com.tterrag.registrate.builders.FluidBuilder$FluidTypeFactory
 *  com.tterrag.registrate.util.entry.RegistryEntry
 *  com.tterrag.registrate.util.nullness.NonNullConsumer
 *  com.tterrag.registrate.util.nullness.NonNullFunction
 *  com.tterrag.registrate.util.nullness.NonNullSupplier
 *  net.createmod.catnip.platform.CatnipServices
 *  net.createmod.catnip.registry.RegisteredObjectsHelper
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.core.Registry
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType$EntityFactory
 *  net.minecraft.world.entity.MobCategory
 *  net.minecraft.world.item.CreativeModeTab
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.bus.api.IEventBus
 *  net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions
 *  net.neoforged.neoforge.client.model.generators.ModelFile
 *  net.neoforged.neoforge.fluids.BaseFlowingFluid
 *  net.neoforged.neoforge.fluids.BaseFlowingFluid$Flowing
 *  net.neoforged.neoforge.fluids.BaseFlowingFluid$Properties
 *  net.neoforged.neoforge.fluids.FluidType
 *  net.neoforged.neoforge.fluids.FluidType$Properties
 *  net.neoforged.neoforge.registries.DeferredHolder
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.foundation.data;

import com.simibubi.create.CreateClient;
import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.simibubi.create.api.behaviour.display.DisplayTarget;
import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorageType;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType;
import com.simibubi.create.api.registrate.CreateRegistrateRegistrationCallback;
import com.simibubi.create.api.registry.CreateRegistries;
import com.simibubi.create.api.registry.registrate.SimpleBuilder;
import com.simibubi.create.content.decoration.encasing.CasingConnectivity;
import com.simibubi.create.content.fluids.VirtualFluid;
import com.simibubi.create.foundation.block.connected.CTModel;
import com.simibubi.create.foundation.block.connected.ConnectedTextureBehaviour;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.simibubi.create.foundation.data.CreateBlockEntityBuilder;
import com.simibubi.create.foundation.data.CreateEntityBuilder;
import com.simibubi.create.foundation.data.TagGen;
import com.simibubi.create.foundation.data.VirtualFluidBuilder;
import com.simibubi.create.foundation.item.TooltipModifier;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.builders.BlockEntityBuilder;
import com.tterrag.registrate.builders.Builder;
import com.tterrag.registrate.builders.BuilderCallback;
import com.tterrag.registrate.builders.FluidBuilder;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.createmod.catnip.platform.CatnipServices;
import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

public class CreateRegistrate
extends AbstractRegistrate<CreateRegistrate> {
    private static final Map<RegistryEntry<?, ?>, DeferredHolder<CreativeModeTab, CreativeModeTab>> TAB_LOOKUP = Collections.synchronizedMap(new IdentityHashMap());
    @Nullable
    protected Function<Item, TooltipModifier> currentTooltipModifierFactory;
    protected DeferredHolder<CreativeModeTab, CreativeModeTab> currentTab;

    protected CreateRegistrate(String modid) {
        super(modid);
    }

    public static CreateRegistrate create(String modid) {
        CreateRegistrate registrate = new CreateRegistrate(modid);
        CreateRegistrateRegistrationCallback.provideRegistrate(registrate);
        return registrate;
    }

    public static boolean isInCreativeTab(RegistryEntry<?, ?> entry, DeferredHolder<CreativeModeTab, CreativeModeTab> tab) {
        return TAB_LOOKUP.get(entry) == tab;
    }

    public CreateRegistrate setTooltipModifierFactory(@Nullable Function<Item, TooltipModifier> factory) {
        this.currentTooltipModifierFactory = factory;
        return (CreateRegistrate)this.self();
    }

    @Nullable
    public Function<Item, TooltipModifier> getTooltipModifierFactory() {
        return this.currentTooltipModifierFactory;
    }

    @Nullable
    public CreateRegistrate setCreativeTab(DeferredHolder<CreativeModeTab, CreativeModeTab> tab) {
        this.currentTab = tab;
        return (CreateRegistrate)this.self();
    }

    public DeferredHolder<CreativeModeTab, CreativeModeTab> getCreativeTab() {
        return this.currentTab;
    }

    public CreateRegistrate registerEventListeners(IEventBus bus) {
        return (CreateRegistrate)super.registerEventListeners(bus);
    }

    protected <R, T extends R> RegistryEntry<R, T> accept(String name, ResourceKey<? extends Registry<R>> type, Builder<R, T, ?, ?> builder, NonNullSupplier<? extends T> creator, NonNullFunction<DeferredHolder<R, T>, ? extends RegistryEntry<R, T>> entryFactory) {
        RegistryEntry entry = super.accept(name, type, builder, creator, entryFactory);
        if (type.equals((Object)Registries.ITEM) && this.currentTooltipModifierFactory != null) {
            Function<Item, TooltipModifier> factory = this.currentTooltipModifierFactory;
            this.addRegisterCallback(name, Registries.ITEM, item -> {
                TooltipModifier modifier = (TooltipModifier)factory.apply((Item)item);
                TooltipModifier.REGISTRY.register((Item)item, modifier);
            });
        }
        if (this.currentTab != null) {
            TAB_LOOKUP.put(entry, this.currentTab);
        }
        return entry;
    }

    public <T extends BlockEntity> CreateBlockEntityBuilder<T, CreateRegistrate> blockEntity(String name, BlockEntityBuilder.BlockEntityFactory<T> factory) {
        return this.blockEntity((Object)((CreateRegistrate)this.self()), name, (BlockEntityBuilder.BlockEntityFactory)factory);
    }

    public <T extends BlockEntity, P> CreateBlockEntityBuilder<T, P> blockEntity(P parent, String name, BlockEntityBuilder.BlockEntityFactory<T> factory) {
        return (CreateBlockEntityBuilder)this.entry(name, callback -> CreateBlockEntityBuilder.create(this, parent, name, callback, factory));
    }

    public <T extends Entity> CreateEntityBuilder<T, CreateRegistrate> entity(String name, EntityType.EntityFactory<T> factory, MobCategory classification) {
        return this.entity((Object)((CreateRegistrate)this.self()), name, (EntityType.EntityFactory)factory, classification);
    }

    public <T extends Entity, P> CreateEntityBuilder<T, P> entity(P parent, String name, EntityType.EntityFactory<T> factory, MobCategory classification) {
        return (CreateEntityBuilder)this.entry(name, callback -> CreateEntityBuilder.create(this, parent, name, callback, factory, classification));
    }

    public <T extends MountedItemStorageType<?>> SimpleBuilder<MountedItemStorageType<?>, T, CreateRegistrate> mountedItemStorage(String name, Supplier<T> supplier) {
        return (SimpleBuilder)this.entry(name, callback -> new SimpleBuilder(this, this, name, (BuilderCallback)callback, CreateRegistries.MOUNTED_ITEM_STORAGE_TYPE, supplier).byBlock(MountedItemStorageType.REGISTRY));
    }

    public <T extends MountedFluidStorageType<?>> SimpleBuilder<MountedFluidStorageType<?>, T, CreateRegistrate> mountedFluidStorage(String name, Supplier<T> supplier) {
        return (SimpleBuilder)this.entry(name, callback -> new SimpleBuilder(this, this, name, (BuilderCallback)callback, CreateRegistries.MOUNTED_FLUID_STORAGE_TYPE, supplier).byBlock(MountedFluidStorageType.REGISTRY));
    }

    public <T extends DisplaySource> SimpleBuilder<DisplaySource, T, CreateRegistrate> displaySource(String name, Supplier<T> supplier) {
        return (SimpleBuilder)this.entry(name, callback -> new SimpleBuilder(this, this, name, (BuilderCallback)callback, CreateRegistries.DISPLAY_SOURCE, supplier).byBlock(DisplaySource.BY_BLOCK).byBlockEntity(DisplaySource.BY_BLOCK_ENTITY));
    }

    public <T extends DisplayTarget> SimpleBuilder<DisplayTarget, T, CreateRegistrate> displayTarget(String name, Supplier<T> supplier) {
        return (SimpleBuilder)this.entry(name, callback -> new SimpleBuilder(this, this, name, (BuilderCallback)callback, CreateRegistries.DISPLAY_TARGET, supplier).byBlock(DisplayTarget.BY_BLOCK).byBlockEntity(DisplayTarget.BY_BLOCK_ENTITY));
    }

    public <T extends Block> BlockBuilder<T, CreateRegistrate> paletteStoneBlock(String name, NonNullFunction<BlockBehaviour.Properties, T> factory, NonNullSupplier<Block> propertiesFrom, boolean worldGenStone, boolean hasNaturalVariants) {
        BlockBuilder builder = (BlockBuilder)((BlockBuilder)super.block(name, factory).initialProperties(propertiesFrom).transform(TagGen.pickaxeOnly())).blockstate(hasNaturalVariants ? BlockStateGen.naturalStoneTypeBlock(name) : (c, p) -> {
            String location = "block/palettes/stone_types/" + c.getName();
            p.simpleBlock((Block)c.get(), (ModelFile)p.models().cubeAll(c.getName(), p.modLoc(location)));
        }).tag(new TagKey[]{BlockTags.DRIPSTONE_REPLACEABLE}).tag(new TagKey[]{BlockTags.AZALEA_ROOT_REPLACEABLE}).tag(new TagKey[]{BlockTags.MOSS_REPLACEABLE}).tag(new TagKey[]{BlockTags.LUSH_GROUND_REPLACEABLE}).item().model((c, p) -> p.cubeAll(c.getName(), p.modLoc(hasNaturalVariants ? "block/palettes/stone_types/natural/" + name + "_1" : "block/palettes/stone_types/" + c.getName()))).build();
        return builder;
    }

    public BlockBuilder<Block, CreateRegistrate> paletteStoneBlock(String name, NonNullSupplier<Block> propertiesFrom, boolean worldGenStone, boolean hasNaturalVariants) {
        return this.paletteStoneBlock(name, Block::new, propertiesFrom, worldGenStone, hasNaturalVariants);
    }

    public <T extends BaseFlowingFluid> FluidBuilder<T, CreateRegistrate> virtualFluid(String name, FluidBuilder.FluidTypeFactory typeFactory, NonNullFunction<BaseFlowingFluid.Properties, T> sourceFactory, NonNullFunction<BaseFlowingFluid.Properties, T> flowingFactory) {
        return (FluidBuilder)this.entry(name, c -> new VirtualFluidBuilder((AbstractRegistrate<?>)this.self(), (CreateRegistrate)this.self(), name, (BuilderCallback)c, ResourceLocation.fromNamespaceAndPath((String)this.getModid(), (String)("fluid/" + name + "_still")), ResourceLocation.fromNamespaceAndPath((String)this.getModid(), (String)("fluid/" + name + "_flow")), typeFactory, sourceFactory, flowingFactory));
    }

    public <T extends BaseFlowingFluid> FluidBuilder<T, CreateRegistrate> virtualFluid(String name, ResourceLocation still, ResourceLocation flow, FluidBuilder.FluidTypeFactory typeFactory, NonNullFunction<BaseFlowingFluid.Properties, T> sourceFactory, NonNullFunction<BaseFlowingFluid.Properties, T> flowingFactory) {
        return (FluidBuilder)this.entry(name, c -> new VirtualFluidBuilder((AbstractRegistrate<?>)this.self(), (CreateRegistrate)this.self(), name, (BuilderCallback)c, still, flow, typeFactory, sourceFactory, flowingFactory));
    }

    public FluidBuilder<VirtualFluid, CreateRegistrate> virtualFluid(String name) {
        return (FluidBuilder)this.entry(name, c -> new VirtualFluidBuilder((AbstractRegistrate<?>)this.self(), (CreateRegistrate)this.self(), name, (BuilderCallback)c, ResourceLocation.fromNamespaceAndPath((String)this.getModid(), (String)("fluid/" + name + "_still")), ResourceLocation.fromNamespaceAndPath((String)this.getModid(), (String)("fluid/" + name + "_flow")), CreateRegistrate::defaultFluidType, VirtualFluid::createSource, VirtualFluid::createFlowing));
    }

    public FluidBuilder<VirtualFluid, CreateRegistrate> virtualFluid(String name, ResourceLocation still, ResourceLocation flow) {
        return (FluidBuilder)this.entry(name, c -> new VirtualFluidBuilder((AbstractRegistrate<?>)this.self(), (CreateRegistrate)this.self(), name, (BuilderCallback)c, still, flow, CreateRegistrate::defaultFluidType, VirtualFluid::createSource, VirtualFluid::createFlowing));
    }

    public FluidBuilder<BaseFlowingFluid.Flowing, CreateRegistrate> standardFluid(String name) {
        return this.fluid(name, ResourceLocation.fromNamespaceAndPath((String)this.getModid(), (String)("fluid/" + name + "_still")), ResourceLocation.fromNamespaceAndPath((String)this.getModid(), (String)("fluid/" + name + "_flow")));
    }

    public FluidBuilder<BaseFlowingFluid.Flowing, CreateRegistrate> standardFluid(String name, FluidBuilder.FluidTypeFactory typeFactory) {
        return this.fluid(name, ResourceLocation.fromNamespaceAndPath((String)this.getModid(), (String)("fluid/" + name + "_still")), ResourceLocation.fromNamespaceAndPath((String)this.getModid(), (String)("fluid/" + name + "_flow")), typeFactory);
    }

    public static FluidType defaultFluidType(FluidType.Properties properties, final ResourceLocation stillTexture, final ResourceLocation flowingTexture) {
        return new FluidType(properties){

            public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                consumer.accept(new IClientFluidTypeExtensions(){

                    public ResourceLocation getStillTexture() {
                        return stillTexture;
                    }

                    public ResourceLocation getFlowingTexture() {
                        return flowingTexture;
                    }
                });
            }
        };
    }

    public static <T extends Block> NonNullConsumer<? super T> casingConnectivity(BiConsumer<T, CasingConnectivity> consumer) {
        return entry -> CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> CreateRegistrate.registerCasingConnectivity(entry, consumer));
    }

    public static <T extends Block> NonNullConsumer<? super T> blockModel(Supplier<NonNullFunction<BakedModel, ? extends BakedModel>> func) {
        return entry -> CatnipServices.PLATFORM.executeOnClientOnly(() -> CreateRegistrate.lambda$blockModel$17(entry, (Supplier)func));
    }

    public static <T extends Item> NonNullConsumer<? super T> itemModel(Supplier<NonNullFunction<BakedModel, ? extends BakedModel>> func) {
        return entry -> CatnipServices.PLATFORM.executeOnClientOnly(() -> CreateRegistrate.lambda$itemModel$20(entry, (Supplier)func));
    }

    public static NonNullConsumer<? super Block> connectedTextures(Supplier<ConnectedTextureBehaviour> behavior) {
        return entry -> CatnipServices.PLATFORM.executeOnClientOnly(() -> CreateRegistrate.lambda$connectedTextures$23(entry, (Supplier)behavior));
    }

    @OnlyIn(value=Dist.CLIENT)
    private static <T extends Block> void registerCasingConnectivity(T entry, BiConsumer<T, CasingConnectivity> consumer) {
        consumer.accept(entry, CreateClient.CASING_CONNECTIVITY);
    }

    @OnlyIn(value=Dist.CLIENT)
    private static void registerBlockModel(Block entry, Supplier<NonNullFunction<BakedModel, ? extends BakedModel>> func) {
        CreateClient.MODEL_SWAPPER.getCustomBlockModels().register(RegisteredObjectsHelper.getKeyOrThrow((Block)entry), func.get());
    }

    @OnlyIn(value=Dist.CLIENT)
    private static void registerItemModel(Item entry, Supplier<NonNullFunction<BakedModel, ? extends BakedModel>> func) {
        CreateClient.MODEL_SWAPPER.getCustomItemModels().register(RegisteredObjectsHelper.getKeyOrThrow((Item)entry), func.get());
    }

    @OnlyIn(value=Dist.CLIENT)
    private static void registerCTBehviour(Block entry, Supplier<ConnectedTextureBehaviour> behaviorSupplier) {
        ConnectedTextureBehaviour behavior = behaviorSupplier.get();
        CreateClient.MODEL_SWAPPER.getCustomBlockModels().register(RegisteredObjectsHelper.getKeyOrThrow((Block)entry), (NonNullFunction<BakedModel, ? extends BakedModel>)((NonNullFunction)model -> new CTModel((BakedModel)model, behavior)));
    }

    private static /* synthetic */ Runnable lambda$connectedTextures$23(Block entry, Supplier behavior) {
        return () -> CreateRegistrate.lambda$connectedTextures$22(entry, (Supplier)behavior);
    }

    private static /* synthetic */ void lambda$connectedTextures$22(Block entry, Supplier behavior) {
        CreateRegistrate.registerCTBehviour(entry, behavior);
    }

    private static /* synthetic */ Runnable lambda$itemModel$20(Item entry, Supplier func) {
        return () -> CreateRegistrate.lambda$itemModel$19(entry, (Supplier)func);
    }

    private static /* synthetic */ void lambda$itemModel$19(Item entry, Supplier func) {
        CreateRegistrate.registerItemModel(entry, func);
    }

    private static /* synthetic */ Runnable lambda$blockModel$17(Block entry, Supplier func) {
        return () -> CreateRegistrate.lambda$blockModel$16(entry, (Supplier)func);
    }

    private static /* synthetic */ void lambda$blockModel$16(Block entry, Supplier func) {
        CreateRegistrate.registerBlockModel(entry, func);
    }
}
