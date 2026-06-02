/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.builders.BlockBuilder
 *  com.tterrag.registrate.builders.ItemBuilder
 *  com.tterrag.registrate.providers.DataGenContext
 *  com.tterrag.registrate.providers.RegistrateBlockstateProvider
 *  com.tterrag.registrate.providers.RegistrateRecipeProvider
 *  com.tterrag.registrate.util.DataIngredient
 *  com.tterrag.registrate.util.entry.BlockEntry
 *  com.tterrag.registrate.util.nullness.NonNullBiConsumer
 *  com.tterrag.registrate.util.nullness.NonNullSupplier
 *  net.createmod.catnip.data.Couple
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.Holder
 *  net.minecraft.core.HolderLookup$RegistryLookup
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.data.loot.BlockLootSubProvider
 *  net.minecraft.data.recipes.RecipeCategory
 *  net.minecraft.data.recipes.RecipeOutput
 *  net.minecraft.data.recipes.ShapedRecipeBuilder
 *  net.minecraft.data.recipes.ShapelessRecipeBuilder
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.tags.ItemTags
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.item.DyeColor
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.Rarity
 *  net.minecraft.world.item.enchantment.Enchantments
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.IronBarsBlock
 *  net.minecraft.world.level.block.RedStoneWireBlock
 *  net.minecraft.world.level.block.RotatedPillarBlock
 *  net.minecraft.world.level.block.SoundType
 *  net.minecraft.world.level.block.WeatheringCopper$WeatherState
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.PistonType
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.MapColor
 *  net.minecraft.world.level.material.PushReaction
 *  net.minecraft.world.level.storage.loot.LootPool
 *  net.minecraft.world.level.storage.loot.LootPool$Builder
 *  net.minecraft.world.level.storage.loot.LootTable
 *  net.minecraft.world.level.storage.loot.LootTable$Builder
 *  net.minecraft.world.level.storage.loot.entries.LootItem
 *  net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer$Builder
 *  net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer$Builder
 *  net.minecraft.world.level.storage.loot.functions.ApplyBonusCount
 *  net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction
 *  net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction$Source
 *  net.minecraft.world.level.storage.loot.functions.CopyNameFunction
 *  net.minecraft.world.level.storage.loot.functions.CopyNameFunction$NameSource
 *  net.minecraft.world.level.storage.loot.functions.FunctionUserBuilder
 *  net.minecraft.world.level.storage.loot.functions.LootItemFunction$Builder
 *  net.minecraft.world.level.storage.loot.predicates.ConditionUserBuilder
 *  net.minecraft.world.level.storage.loot.predicates.ExplosionCondition
 *  net.minecraft.world.level.storage.loot.predicates.LootItemCondition$Builder
 *  net.minecraft.world.level.storage.loot.providers.number.ConstantValue
 *  net.minecraft.world.level.storage.loot.providers.number.NumberProvider
 *  net.neoforged.neoforge.client.model.generators.BlockModelBuilder
 *  net.neoforged.neoforge.client.model.generators.ConfiguredModel
 *  net.neoforged.neoforge.client.model.generators.ItemModelBuilder
 *  net.neoforged.neoforge.client.model.generators.ModelFile
 *  net.neoforged.neoforge.common.Tags$Blocks
 *  net.neoforged.neoforge.common.Tags$Items
 *  net.neoforged.neoforge.common.util.DeferredSoundType
 */
package com.simibubi.create;

import com.simibubi.create.AllCreativeModeTabs;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllDisplaySources;
import com.simibubi.create.AllDisplayTargets;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllMountedStorageTypes;
import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.AllTags;
import com.simibubi.create.Create;
import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.simibubi.create.api.behaviour.display.DisplayTarget;
import com.simibubi.create.api.behaviour.interaction.ConductorBlockInteractionBehavior;
import com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorageType;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType;
import com.simibubi.create.api.stress.BlockStressValues;
import com.simibubi.create.content.contraptions.actors.contraptionControls.ContraptionControlsBlock;
import com.simibubi.create.content.contraptions.actors.contraptionControls.ContraptionControlsMovement;
import com.simibubi.create.content.contraptions.actors.contraptionControls.ContraptionControlsMovingInteraction;
import com.simibubi.create.content.contraptions.actors.harvester.HarvesterBlock;
import com.simibubi.create.content.contraptions.actors.harvester.HarvesterMovementBehaviour;
import com.simibubi.create.content.contraptions.actors.plough.PloughBlock;
import com.simibubi.create.content.contraptions.actors.plough.PloughMovementBehaviour;
import com.simibubi.create.content.contraptions.actors.psi.PortableStorageInterfaceBlock;
import com.simibubi.create.content.contraptions.actors.psi.PortableStorageInterfaceMovement;
import com.simibubi.create.content.contraptions.actors.roller.RollerBlock;
import com.simibubi.create.content.contraptions.actors.roller.RollerBlockItem;
import com.simibubi.create.content.contraptions.actors.roller.RollerMovementBehaviour;
import com.simibubi.create.content.contraptions.actors.seat.SeatBlock;
import com.simibubi.create.content.contraptions.actors.seat.SeatInteractionBehaviour;
import com.simibubi.create.content.contraptions.actors.seat.SeatMovementBehaviour;
import com.simibubi.create.content.contraptions.actors.trainControls.ControlsBlock;
import com.simibubi.create.content.contraptions.actors.trainControls.ControlsInteractionBehaviour;
import com.simibubi.create.content.contraptions.actors.trainControls.ControlsMovementBehaviour;
import com.simibubi.create.content.contraptions.bearing.BlankSailBlockItem;
import com.simibubi.create.content.contraptions.bearing.ClockworkBearingBlock;
import com.simibubi.create.content.contraptions.bearing.MechanicalBearingBlock;
import com.simibubi.create.content.contraptions.bearing.SailBlock;
import com.simibubi.create.content.contraptions.bearing.StabilizedBearingMovementBehaviour;
import com.simibubi.create.content.contraptions.bearing.WindmillBearingBlock;
import com.simibubi.create.content.contraptions.behaviour.BellMovementBehaviour;
import com.simibubi.create.content.contraptions.chassis.LinearChassisBlock;
import com.simibubi.create.content.contraptions.chassis.RadialChassisBlock;
import com.simibubi.create.content.contraptions.chassis.StickerBlock;
import com.simibubi.create.content.contraptions.elevator.ElevatorContactBlock;
import com.simibubi.create.content.contraptions.elevator.ElevatorPulleyBlock;
import com.simibubi.create.content.contraptions.gantry.GantryCarriageBlock;
import com.simibubi.create.content.contraptions.mounted.CartAssemblerBlock;
import com.simibubi.create.content.contraptions.mounted.CartAssemblerBlockItem;
import com.simibubi.create.content.contraptions.piston.MechanicalPistonBlock;
import com.simibubi.create.content.contraptions.piston.MechanicalPistonHeadBlock;
import com.simibubi.create.content.contraptions.piston.PistonExtensionPoleBlock;
import com.simibubi.create.content.contraptions.pulley.PulleyBlock;
import com.simibubi.create.content.decoration.CardboardBlock;
import com.simibubi.create.content.decoration.MetalLadderBlock;
import com.simibubi.create.content.decoration.MetalScaffoldingBlock;
import com.simibubi.create.content.decoration.RoofBlockCTBehaviour;
import com.simibubi.create.content.decoration.TrainTrapdoorBlock;
import com.simibubi.create.content.decoration.TrapdoorCTBehaviour;
import com.simibubi.create.content.decoration.bracket.BracketBlock;
import com.simibubi.create.content.decoration.bracket.BracketBlockItem;
import com.simibubi.create.content.decoration.bracket.BracketGenerator;
import com.simibubi.create.content.decoration.copycat.CopycatBarsModel;
import com.simibubi.create.content.decoration.copycat.CopycatPanelBlock;
import com.simibubi.create.content.decoration.copycat.CopycatPanelModel;
import com.simibubi.create.content.decoration.copycat.CopycatStepBlock;
import com.simibubi.create.content.decoration.copycat.CopycatStepModel;
import com.simibubi.create.content.decoration.copycat.SpecialCopycatPanelBlockState;
import com.simibubi.create.content.decoration.encasing.CasingBlock;
import com.simibubi.create.content.decoration.encasing.EncasedCTBehaviour;
import com.simibubi.create.content.decoration.encasing.EncasingRegistry;
import com.simibubi.create.content.decoration.girder.ConnectedGirderModel;
import com.simibubi.create.content.decoration.girder.GirderBlock;
import com.simibubi.create.content.decoration.girder.GirderBlockStateGenerator;
import com.simibubi.create.content.decoration.girder.GirderEncasedShaftBlock;
import com.simibubi.create.content.decoration.placard.PlacardBlock;
import com.simibubi.create.content.decoration.slidingDoor.SlidingDoorBlock;
import com.simibubi.create.content.decoration.steamWhistle.WhistleBlock;
import com.simibubi.create.content.decoration.steamWhistle.WhistleExtenderBlock;
import com.simibubi.create.content.decoration.steamWhistle.WhistleGenerator;
import com.simibubi.create.content.equipment.armor.BacktankBlock;
import com.simibubi.create.content.equipment.bell.HauntedBellBlock;
import com.simibubi.create.content.equipment.bell.HauntedBellMovementBehaviour;
import com.simibubi.create.content.equipment.bell.PeculiarBellBlock;
import com.simibubi.create.content.equipment.clipboard.ClipboardBlock;
import com.simibubi.create.content.equipment.clipboard.ClipboardBlockItem;
import com.simibubi.create.content.equipment.clipboard.ClipboardOverrides;
import com.simibubi.create.content.equipment.toolbox.ToolboxBlock;
import com.simibubi.create.content.fluids.PipeAttachmentModel;
import com.simibubi.create.content.fluids.drain.ItemDrainBlock;
import com.simibubi.create.content.fluids.hosePulley.HosePulleyBlock;
import com.simibubi.create.content.fluids.pipes.EncasedPipeBlock;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlock;
import com.simibubi.create.content.fluids.pipes.GlassFluidPipeBlock;
import com.simibubi.create.content.fluids.pipes.SmartFluidPipeBlock;
import com.simibubi.create.content.fluids.pipes.SmartFluidPipeGenerator;
import com.simibubi.create.content.fluids.pipes.valve.FluidValveBlock;
import com.simibubi.create.content.fluids.pump.PumpBlock;
import com.simibubi.create.content.fluids.spout.SpoutBlock;
import com.simibubi.create.content.fluids.tank.FluidTankBlock;
import com.simibubi.create.content.fluids.tank.FluidTankGenerator;
import com.simibubi.create.content.fluids.tank.FluidTankItem;
import com.simibubi.create.content.fluids.tank.FluidTankModel;
import com.simibubi.create.content.fluids.tank.FluidTankMovementBehavior;
import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.belt.BeltGenerator;
import com.simibubi.create.content.kinetics.belt.BeltModel;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlock;
import com.simibubi.create.content.kinetics.chainDrive.ChainDriveBlock;
import com.simibubi.create.content.kinetics.chainDrive.ChainDriveGenerator;
import com.simibubi.create.content.kinetics.chainDrive.ChainGearshiftBlock;
import com.simibubi.create.content.kinetics.clock.CuckooClockBlock;
import com.simibubi.create.content.kinetics.crafter.CrafterCTBehaviour;
import com.simibubi.create.content.kinetics.crafter.MechanicalCrafterBlock;
import com.simibubi.create.content.kinetics.crank.HandCrankBlock;
import com.simibubi.create.content.kinetics.crank.ValveHandleBlock;
import com.simibubi.create.content.kinetics.crusher.CrushingWheelBlock;
import com.simibubi.create.content.kinetics.crusher.CrushingWheelControllerBlock;
import com.simibubi.create.content.kinetics.deployer.DeployerBlock;
import com.simibubi.create.content.kinetics.deployer.DeployerMovementBehaviour;
import com.simibubi.create.content.kinetics.deployer.DeployerMovingInteraction;
import com.simibubi.create.content.kinetics.drill.DrillBlock;
import com.simibubi.create.content.kinetics.drill.DrillMovementBehaviour;
import com.simibubi.create.content.kinetics.fan.EncasedFanBlock;
import com.simibubi.create.content.kinetics.fan.NozzleBlock;
import com.simibubi.create.content.kinetics.flywheel.FlywheelBlock;
import com.simibubi.create.content.kinetics.gantry.GantryShaftBlock;
import com.simibubi.create.content.kinetics.gauge.GaugeBlock;
import com.simibubi.create.content.kinetics.gauge.GaugeGenerator;
import com.simibubi.create.content.kinetics.gearbox.GearboxBlock;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlock;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmItem;
import com.simibubi.create.content.kinetics.millstone.MillstoneBlock;
import com.simibubi.create.content.kinetics.mixer.MechanicalMixerBlock;
import com.simibubi.create.content.kinetics.motor.CreativeMotorBlock;
import com.simibubi.create.content.kinetics.motor.CreativeMotorGenerator;
import com.simibubi.create.content.kinetics.press.MechanicalPressBlock;
import com.simibubi.create.content.kinetics.saw.SawBlock;
import com.simibubi.create.content.kinetics.saw.SawGenerator;
import com.simibubi.create.content.kinetics.saw.SawMovementBehaviour;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockModel;
import com.simibubi.create.content.kinetics.simpleRelays.CogWheelBlock;
import com.simibubi.create.content.kinetics.simpleRelays.CogwheelBlockItem;
import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedCogCTBehaviour;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedCogwheelBlock;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedShaftBlock;
import com.simibubi.create.content.kinetics.speedController.SpeedControllerBlock;
import com.simibubi.create.content.kinetics.steamEngine.PoweredShaftBlock;
import com.simibubi.create.content.kinetics.steamEngine.SteamEngineBlock;
import com.simibubi.create.content.kinetics.transmission.ClutchBlock;
import com.simibubi.create.content.kinetics.transmission.GearshiftBlock;
import com.simibubi.create.content.kinetics.transmission.sequencer.SequencedGearshiftBlock;
import com.simibubi.create.content.kinetics.transmission.sequencer.SequencedGearshiftGenerator;
import com.simibubi.create.content.kinetics.turntable.TurntableBlock;
import com.simibubi.create.content.kinetics.waterwheel.LargeWaterWheelBlock;
import com.simibubi.create.content.kinetics.waterwheel.LargeWaterWheelBlockItem;
import com.simibubi.create.content.kinetics.waterwheel.WaterWheelBlock;
import com.simibubi.create.content.kinetics.waterwheel.WaterWheelStructuralBlock;
import com.simibubi.create.content.logistics.chute.ChuteBlock;
import com.simibubi.create.content.logistics.chute.ChuteGenerator;
import com.simibubi.create.content.logistics.chute.ChuteItem;
import com.simibubi.create.content.logistics.chute.SmartChuteBlock;
import com.simibubi.create.content.logistics.crate.CreativeCrateBlock;
import com.simibubi.create.content.logistics.depot.DepotBlock;
import com.simibubi.create.content.logistics.depot.EjectorBlock;
import com.simibubi.create.content.logistics.depot.EjectorItem;
import com.simibubi.create.content.logistics.depot.MountedDepotInteractionBehaviour;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockItem;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelModel;
import com.simibubi.create.content.logistics.funnel.AndesiteFunnelBlock;
import com.simibubi.create.content.logistics.funnel.BeltFunnelBlock;
import com.simibubi.create.content.logistics.funnel.BeltFunnelGenerator;
import com.simibubi.create.content.logistics.funnel.BrassFunnelBlock;
import com.simibubi.create.content.logistics.funnel.FunnelBlock;
import com.simibubi.create.content.logistics.funnel.FunnelGenerator;
import com.simibubi.create.content.logistics.funnel.FunnelItem;
import com.simibubi.create.content.logistics.funnel.FunnelMovementBehaviour;
import com.simibubi.create.content.logistics.itemHatch.ItemHatchBlock;
import com.simibubi.create.content.logistics.packagePort.PackagePortItem;
import com.simibubi.create.content.logistics.packagePort.frogport.FrogportBlock;
import com.simibubi.create.content.logistics.packagePort.postbox.PostboxBlock;
import com.simibubi.create.content.logistics.packager.PackagerBlock;
import com.simibubi.create.content.logistics.packager.repackager.RepackagerBlock;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBlockItem;
import com.simibubi.create.content.logistics.packagerLink.PackagerLinkBlock;
import com.simibubi.create.content.logistics.packagerLink.PackagerLinkGenerator;
import com.simibubi.create.content.logistics.redstoneRequester.RedstoneRequesterBlock;
import com.simibubi.create.content.logistics.redstoneRequester.RedstoneRequesterBlockItem;
import com.simibubi.create.content.logistics.stockTicker.StockTickerBlock;
import com.simibubi.create.content.logistics.tableCloth.TableClothBlock;
import com.simibubi.create.content.logistics.tunnel.BeltTunnelBlock;
import com.simibubi.create.content.logistics.tunnel.BrassTunnelBlock;
import com.simibubi.create.content.logistics.tunnel.BrassTunnelCTBehaviour;
import com.simibubi.create.content.logistics.vault.ItemVaultBlock;
import com.simibubi.create.content.logistics.vault.ItemVaultCTBehaviour;
import com.simibubi.create.content.logistics.vault.ItemVaultItem;
import com.simibubi.create.content.materials.ExperienceBlock;
import com.simibubi.create.content.processing.AssemblyOperatorBlockItem;
import com.simibubi.create.content.processing.basin.BasinBlock;
import com.simibubi.create.content.processing.basin.BasinGenerator;
import com.simibubi.create.content.processing.basin.BasinMovementBehaviour;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlockItem;
import com.simibubi.create.content.processing.burner.BlazeBurnerMovementBehaviour;
import com.simibubi.create.content.processing.burner.LitBlazeBurnerBlock;
import com.simibubi.create.content.redstone.RoseQuartzLampBlock;
import com.simibubi.create.content.redstone.analogLever.AnalogLeverBlock;
import com.simibubi.create.content.redstone.contact.ContactMovementBehaviour;
import com.simibubi.create.content.redstone.contact.RedstoneContactBlock;
import com.simibubi.create.content.redstone.contact.RedstoneContactItem;
import com.simibubi.create.content.redstone.deskBell.DeskBellBlock;
import com.simibubi.create.content.redstone.diodes.AbstractDiodeGenerator;
import com.simibubi.create.content.redstone.diodes.BrassDiodeBlock;
import com.simibubi.create.content.redstone.diodes.BrassDiodeGenerator;
import com.simibubi.create.content.redstone.diodes.PoweredLatchBlock;
import com.simibubi.create.content.redstone.diodes.PoweredLatchGenerator;
import com.simibubi.create.content.redstone.diodes.ToggleLatchBlock;
import com.simibubi.create.content.redstone.diodes.ToggleLatchGenerator;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlock;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlockItem;
import com.simibubi.create.content.redstone.link.RedstoneLinkBlock;
import com.simibubi.create.content.redstone.link.RedstoneLinkGenerator;
import com.simibubi.create.content.redstone.link.controller.LecternControllerBlock;
import com.simibubi.create.content.redstone.nixieTube.NixieTubeBlock;
import com.simibubi.create.content.redstone.nixieTube.NixieTubeGenerator;
import com.simibubi.create.content.redstone.rail.ControllerRailBlock;
import com.simibubi.create.content.redstone.rail.ControllerRailGenerator;
import com.simibubi.create.content.redstone.smartObserver.SmartObserverBlock;
import com.simibubi.create.content.redstone.smartObserver.SmartObserverGenerator;
import com.simibubi.create.content.redstone.thresholdSwitch.ThresholdSwitchBlock;
import com.simibubi.create.content.redstone.thresholdSwitch.ThresholdSwitchGenerator;
import com.simibubi.create.content.schematics.cannon.SchematicannonBlock;
import com.simibubi.create.content.schematics.table.SchematicTableBlock;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.bogey.StandardBogeyBlock;
import com.simibubi.create.content.trains.display.FlapDisplayBlock;
import com.simibubi.create.content.trains.graph.EdgePointType;
import com.simibubi.create.content.trains.observer.TrackObserverBlock;
import com.simibubi.create.content.trains.signal.SignalBlock;
import com.simibubi.create.content.trains.station.StationBlock;
import com.simibubi.create.content.trains.track.FakeTrackBlock;
import com.simibubi.create.content.trains.track.TrackBlock;
import com.simibubi.create.content.trains.track.TrackBlockItem;
import com.simibubi.create.content.trains.track.TrackBlockStateGenerator;
import com.simibubi.create.content.trains.track.TrackMaterial;
import com.simibubi.create.content.trains.track.TrackModel;
import com.simibubi.create.content.trains.track.TrackTargetingBlockItem;
import com.simibubi.create.foundation.block.CopperBlockSet;
import com.simibubi.create.foundation.block.DyedBlockList;
import com.simibubi.create.foundation.block.ItemUseOverrides;
import com.simibubi.create.foundation.block.WrenchableDirectionalBlock;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.render.ReducedDestroyEffects;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.simibubi.create.foundation.data.BuilderTransformers;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.MetalBarsGen;
import com.simibubi.create.foundation.data.ModelGen;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.foundation.data.TagGen;
import com.simibubi.create.foundation.data.recipe.CommonMetal;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.UncontainableBlockItem;
import com.simibubi.create.foundation.mixin.accessor.BlockLootSubProviderAccessor;
import com.simibubi.create.foundation.utility.DyeHelper;
import com.simibubi.create.infrastructure.config.CStress;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.DataIngredient;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import java.util.Map;
import net.createmod.catnip.data.Couple;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction;
import net.minecraft.world.level.storage.loot.functions.FunctionUserBuilder;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.ConditionUserBuilder;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.util.DeferredSoundType;

public class AllBlocks {
    private static final CreateRegistrate REGISTRATE = Create.registrate();
    public static final BlockEntry<SchematicannonBlock> SCHEMATICANNON;
    public static final BlockEntry<SchematicTableBlock> SCHEMATIC_TABLE;
    public static final BlockEntry<ShaftBlock> SHAFT;
    public static final BlockEntry<CogWheelBlock> COGWHEEL;
    public static final BlockEntry<CogWheelBlock> LARGE_COGWHEEL;
    public static final BlockEntry<EncasedShaftBlock> ANDESITE_ENCASED_SHAFT;
    public static final BlockEntry<EncasedShaftBlock> BRASS_ENCASED_SHAFT;
    public static final BlockEntry<EncasedCogwheelBlock> ANDESITE_ENCASED_COGWHEEL;
    public static final BlockEntry<EncasedCogwheelBlock> BRASS_ENCASED_COGWHEEL;
    public static final BlockEntry<EncasedCogwheelBlock> ANDESITE_ENCASED_LARGE_COGWHEEL;
    public static final BlockEntry<EncasedCogwheelBlock> BRASS_ENCASED_LARGE_COGWHEEL;
    public static final BlockEntry<GearboxBlock> GEARBOX;
    public static final BlockEntry<ClutchBlock> CLUTCH;
    public static final BlockEntry<GearshiftBlock> GEARSHIFT;
    public static final BlockEntry<ChainDriveBlock> ENCASED_CHAIN_DRIVE;
    public static final BlockEntry<ChainGearshiftBlock> ADJUSTABLE_CHAIN_GEARSHIFT;
    public static final BlockEntry<BeltBlock> BELT;
    public static final BlockEntry<ChainConveyorBlock> CHAIN_CONVEYOR;
    public static final BlockEntry<CreativeMotorBlock> CREATIVE_MOTOR;
    public static final BlockEntry<WaterWheelBlock> WATER_WHEEL;
    public static final BlockEntry<LargeWaterWheelBlock> LARGE_WATER_WHEEL;
    public static final BlockEntry<WaterWheelStructuralBlock> WATER_WHEEL_STRUCTURAL;
    public static final BlockEntry<EncasedFanBlock> ENCASED_FAN;
    public static final BlockEntry<NozzleBlock> NOZZLE;
    public static final BlockEntry<TurntableBlock> TURNTABLE;
    public static final BlockEntry<HandCrankBlock> HAND_CRANK;
    public static final BlockEntry<CuckooClockBlock> CUCKOO_CLOCK;
    public static final BlockEntry<CuckooClockBlock> MYSTERIOUS_CUCKOO_CLOCK;
    public static final BlockEntry<MillstoneBlock> MILLSTONE;
    public static final BlockEntry<CrushingWheelBlock> CRUSHING_WHEEL;
    public static final BlockEntry<CrushingWheelControllerBlock> CRUSHING_WHEEL_CONTROLLER;
    public static final BlockEntry<MechanicalPressBlock> MECHANICAL_PRESS;
    public static final BlockEntry<MechanicalMixerBlock> MECHANICAL_MIXER;
    public static final BlockEntry<BasinBlock> BASIN;
    public static final BlockEntry<BlazeBurnerBlock> BLAZE_BURNER;
    public static final BlockEntry<LitBlazeBurnerBlock> LIT_BLAZE_BURNER;
    public static final BlockEntry<DepotBlock> DEPOT;
    public static final BlockEntry<EjectorBlock> WEIGHTED_EJECTOR;
    public static final BlockEntry<ChuteBlock> CHUTE;
    public static final BlockEntry<SmartChuteBlock> SMART_CHUTE;
    public static final BlockEntry<GaugeBlock> SPEEDOMETER;
    public static final BlockEntry<GaugeBlock> STRESSOMETER;
    public static final BlockEntry<BracketBlock> WOODEN_BRACKET;
    public static final BlockEntry<BracketBlock> METAL_BRACKET;
    public static final BlockEntry<FluidPipeBlock> FLUID_PIPE;
    public static final BlockEntry<EncasedPipeBlock> ENCASED_FLUID_PIPE;
    public static final BlockEntry<GlassFluidPipeBlock> GLASS_FLUID_PIPE;
    public static final BlockEntry<PumpBlock> MECHANICAL_PUMP;
    public static final BlockEntry<SmartFluidPipeBlock> SMART_FLUID_PIPE;
    public static final BlockEntry<FluidValveBlock> FLUID_VALVE;
    public static final BlockEntry<ValveHandleBlock> COPPER_VALVE_HANDLE;
    public static final DyedBlockList<ValveHandleBlock> DYED_VALVE_HANDLES;
    public static final BlockEntry<FluidTankBlock> FLUID_TANK;
    public static final BlockEntry<FluidTankBlock> CREATIVE_FLUID_TANK;
    public static final BlockEntry<HosePulleyBlock> HOSE_PULLEY;
    public static final BlockEntry<ItemDrainBlock> ITEM_DRAIN;
    public static final BlockEntry<SpoutBlock> SPOUT;
    public static final BlockEntry<PortableStorageInterfaceBlock> PORTABLE_FLUID_INTERFACE;
    public static final BlockEntry<SteamEngineBlock> STEAM_ENGINE;
    public static final BlockEntry<WhistleBlock> STEAM_WHISTLE;
    public static final BlockEntry<WhistleExtenderBlock> STEAM_WHISTLE_EXTENSION;
    public static final BlockEntry<PoweredShaftBlock> POWERED_SHAFT;
    public static final BlockEntry<MechanicalPistonBlock> MECHANICAL_PISTON;
    public static final BlockEntry<MechanicalPistonBlock> STICKY_MECHANICAL_PISTON;
    public static final BlockEntry<PistonExtensionPoleBlock> PISTON_EXTENSION_POLE;
    public static final BlockEntry<MechanicalPistonHeadBlock> MECHANICAL_PISTON_HEAD;
    public static final BlockEntry<GantryCarriageBlock> GANTRY_CARRIAGE;
    public static final BlockEntry<GantryShaftBlock> GANTRY_SHAFT;
    public static final BlockEntry<WindmillBearingBlock> WINDMILL_BEARING;
    public static final BlockEntry<MechanicalBearingBlock> MECHANICAL_BEARING;
    public static final BlockEntry<ClockworkBearingBlock> CLOCKWORK_BEARING;
    public static final BlockEntry<PulleyBlock> ROPE_PULLEY;
    public static final BlockEntry<PulleyBlock.RopeBlock> ROPE;
    public static final BlockEntry<PulleyBlock.MagnetBlock> PULLEY_MAGNET;
    public static final BlockEntry<ElevatorPulleyBlock> ELEVATOR_PULLEY;
    public static final BlockEntry<CartAssemblerBlock> CART_ASSEMBLER;
    public static final BlockEntry<ControllerRailBlock> CONTROLLER_RAIL;
    public static final BlockEntry<CartAssemblerBlock.MinecartAnchorBlock> MINECART_ANCHOR;
    public static final BlockEntry<LinearChassisBlock> LINEAR_CHASSIS;
    public static final BlockEntry<LinearChassisBlock> SECONDARY_LINEAR_CHASSIS;
    public static final BlockEntry<RadialChassisBlock> RADIAL_CHASSIS;
    public static final BlockEntry<StickerBlock> STICKER;
    public static final BlockEntry<ContraptionControlsBlock> CONTRAPTION_CONTROLS;
    public static final BlockEntry<DrillBlock> MECHANICAL_DRILL;
    public static final BlockEntry<SawBlock> MECHANICAL_SAW;
    public static final BlockEntry<DeployerBlock> DEPLOYER;
    public static final BlockEntry<PortableStorageInterfaceBlock> PORTABLE_STORAGE_INTERFACE;
    public static final BlockEntry<RedstoneContactBlock> REDSTONE_CONTACT;
    public static final BlockEntry<ElevatorContactBlock> ELEVATOR_CONTACT;
    public static final BlockEntry<HarvesterBlock> MECHANICAL_HARVESTER;
    public static final BlockEntry<PloughBlock> MECHANICAL_PLOUGH;
    public static final BlockEntry<RollerBlock> MECHANICAL_ROLLER;
    public static final BlockEntry<SailBlock> SAIL_FRAME;
    public static final BlockEntry<SailBlock> SAIL;
    public static final DyedBlockList<SailBlock> DYED_SAILS;
    public static final BlockEntry<CasingBlock> ANDESITE_CASING;
    public static final BlockEntry<CasingBlock> BRASS_CASING;
    public static final BlockEntry<CasingBlock> COPPER_CASING;
    public static final BlockEntry<CasingBlock> SHADOW_STEEL_CASING;
    public static final BlockEntry<CasingBlock> REFINED_RADIANCE_CASING;
    public static final BlockEntry<MechanicalCrafterBlock> MECHANICAL_CRAFTER;
    public static final BlockEntry<SequencedGearshiftBlock> SEQUENCED_GEARSHIFT;
    public static final BlockEntry<FlywheelBlock> FLYWHEEL;
    public static final BlockEntry<SpeedControllerBlock> ROTATION_SPEED_CONTROLLER;
    public static final BlockEntry<ArmBlock> MECHANICAL_ARM;
    public static final BlockEntry<TrackBlock> TRACK;
    public static final BlockEntry<FakeTrackBlock> FAKE_TRACK;
    public static final BlockEntry<CasingBlock> RAILWAY_CASING;
    public static final BlockEntry<StationBlock> TRACK_STATION;
    public static final BlockEntry<SignalBlock> TRACK_SIGNAL;
    public static final BlockEntry<TrackObserverBlock> TRACK_OBSERVER;
    public static final BlockEntry<StandardBogeyBlock> SMALL_BOGEY;
    public static final BlockEntry<StandardBogeyBlock> LARGE_BOGEY;
    public static final BlockEntry<ControlsBlock> TRAIN_CONTROLS;
    public static final BlockEntry<AndesiteFunnelBlock> ANDESITE_FUNNEL;
    public static final BlockEntry<BeltFunnelBlock> ANDESITE_BELT_FUNNEL;
    public static final BlockEntry<BrassFunnelBlock> BRASS_FUNNEL;
    public static final BlockEntry<BeltFunnelBlock> BRASS_BELT_FUNNEL;
    public static final BlockEntry<BeltTunnelBlock> ANDESITE_TUNNEL;
    public static final BlockEntry<BrassTunnelBlock> BRASS_TUNNEL;
    public static final BlockEntry<SmartObserverBlock> SMART_OBSERVER;
    public static final BlockEntry<ThresholdSwitchBlock> THRESHOLD_SWITCH;
    public static final BlockEntry<CreativeCrateBlock> CREATIVE_CRATE;
    public static final BlockEntry<ItemVaultBlock> ITEM_VAULT;
    public static final BlockEntry<ItemHatchBlock> ITEM_HATCH;
    public static final BlockEntry<PackagerBlock> PACKAGER;
    public static final BlockEntry<RepackagerBlock> REPACKAGER;
    public static final BlockEntry<FrogportBlock> PACKAGE_FROGPORT;
    public static final DyedBlockList<PostboxBlock> PACKAGE_POSTBOXES;
    public static final BlockEntry<PackagerLinkBlock> STOCK_LINK;
    public static final BlockEntry<StockTickerBlock> STOCK_TICKER;
    public static final BlockEntry<RedstoneRequesterBlock> REDSTONE_REQUESTER;
    public static final BlockEntry<FactoryPanelBlock> FACTORY_GAUGE;
    public static final DyedBlockList<TableClothBlock> TABLE_CLOTHS;
    public static final BlockEntry<TableClothBlock> ANDESITE_TABLE_CLOTH;
    public static final BlockEntry<TableClothBlock> BRASS_TABLE_CLOTH;
    public static final BlockEntry<TableClothBlock> COPPER_TABLE_CLOTH;
    public static final BlockEntry<DisplayLinkBlock> DISPLAY_LINK;
    public static final BlockEntry<FlapDisplayBlock> DISPLAY_BOARD;
    public static final BlockEntry<NixieTubeBlock> ORANGE_NIXIE_TUBE;
    public static final DyedBlockList<NixieTubeBlock> NIXIE_TUBES;
    public static final BlockEntry<RoseQuartzLampBlock> ROSE_QUARTZ_LAMP;
    public static final BlockEntry<RedstoneLinkBlock> REDSTONE_LINK;
    public static final BlockEntry<AnalogLeverBlock> ANALOG_LEVER;
    public static final BlockEntry<PlacardBlock> PLACARD;
    public static final BlockEntry<BrassDiodeBlock> PULSE_REPEATER;
    public static final BlockEntry<BrassDiodeBlock> PULSE_EXTENDER;
    public static final BlockEntry<BrassDiodeBlock> PULSE_TIMER;
    public static final BlockEntry<PoweredLatchBlock> POWERED_LATCH;
    public static final BlockEntry<ToggleLatchBlock> POWERED_TOGGLE_LATCH;
    public static final BlockEntry<LecternControllerBlock> LECTERN_CONTROLLER;
    public static final BlockEntry<BacktankBlock> COPPER_BACKTANK;
    public static final BlockEntry<BacktankBlock> NETHERITE_BACKTANK;
    public static final BlockEntry<PeculiarBellBlock> PECULIAR_BELL;
    public static final BlockEntry<HauntedBellBlock> HAUNTED_BELL;
    public static final BlockEntry<DeskBellBlock> DESK_BELL;
    public static final DyedBlockList<ToolboxBlock> TOOLBOXES;
    public static final BlockEntry<ClipboardBlock> CLIPBOARD;
    public static final BlockEntry<MetalLadderBlock> ANDESITE_LADDER;
    public static final BlockEntry<MetalLadderBlock> BRASS_LADDER;
    public static final BlockEntry<MetalLadderBlock> COPPER_LADDER;
    public static final BlockEntry<IronBarsBlock> ANDESITE_BARS;
    public static final BlockEntry<IronBarsBlock> BRASS_BARS;
    public static final BlockEntry<IronBarsBlock> COPPER_BARS;
    public static final BlockEntry<MetalScaffoldingBlock> ANDESITE_SCAFFOLD;
    public static final BlockEntry<MetalScaffoldingBlock> BRASS_SCAFFOLD;
    public static final BlockEntry<MetalScaffoldingBlock> COPPER_SCAFFOLD;
    public static final BlockEntry<GirderBlock> METAL_GIRDER;
    public static final BlockEntry<GirderEncasedShaftBlock> METAL_GIRDER_ENCASED_SHAFT;
    public static final BlockEntry<Block> COPYCAT_BASE;
    public static final BlockEntry<CopycatStepBlock> COPYCAT_STEP;
    public static final BlockEntry<CopycatPanelBlock> COPYCAT_PANEL;
    public static final BlockEntry<WrenchableDirectionalBlock> COPYCAT_BARS;
    public static final DyedBlockList<SeatBlock> SEATS;
    public static final BlockEntry<SlidingDoorBlock> ANDESITE_DOOR;
    public static final BlockEntry<SlidingDoorBlock> BRASS_DOOR;
    public static final BlockEntry<SlidingDoorBlock> COPPER_DOOR;
    public static final BlockEntry<SlidingDoorBlock> TRAIN_DOOR;
    public static final BlockEntry<TrainTrapdoorBlock> TRAIN_TRAPDOOR;
    public static final BlockEntry<SlidingDoorBlock> FRAMED_GLASS_DOOR;
    public static final BlockEntry<TrainTrapdoorBlock> FRAMED_GLASS_TRAPDOOR;
    public static final BlockEntry<Block> ZINC_ORE;
    public static final BlockEntry<Block> DEEPSLATE_ZINC_ORE;
    public static final BlockEntry<Block> RAW_ZINC_BLOCK;
    public static final BlockEntry<Block> ZINC_BLOCK;
    public static final BlockEntry<Block> ANDESITE_ALLOY_BLOCK;
    public static final BlockEntry<Block> INDUSTRIAL_IRON_BLOCK;
    public static final BlockEntry<Block> WEATHERED_IRON_BLOCK;
    public static final BlockEntry<Block> BRASS_BLOCK;
    public static final BlockEntry<CardboardBlock> CARDBOARD_BLOCK;
    public static final BlockEntry<CardboardBlock> BOUND_CARDBOARD_BLOCK;
    public static final BlockEntry<ExperienceBlock> EXPERIENCE_BLOCK;
    public static final BlockEntry<RotatedPillarBlock> ROSE_QUARTZ_BLOCK;
    public static final BlockEntry<Block> ROSE_QUARTZ_TILES;
    public static final BlockEntry<Block> SMALL_ROSE_QUARTZ_TILES;
    public static final CopperBlockSet COPPER_SHINGLES;
    public static final CopperBlockSet COPPER_TILES;

    public static void register() {
    }

    static {
        REGISTRATE.setCreativeTab(AllCreativeModeTabs.BASE_CREATIVE_TAB);
        SCHEMATICANNON = ((BlockBuilder)((BlockBuilder)REGISTRATE.block("schematicannon", SchematicannonBlock::new).initialProperties(() -> Blocks.DISPENSER).properties(p -> p.mapColor(MapColor.COLOR_GRAY)).transform(TagGen.pickaxeOnly())).blockstate((ctx, prov) -> prov.simpleBlock((Block)ctx.getEntry(), AssetLookup.partialBaseModel(ctx, prov, new String[0]))).loot((lt, block) -> {
            LootTable.Builder builder = LootTable.lootTable();
            LootItemCondition.Builder survivesExplosion = ExplosionCondition.survivesExplosion();
            lt.add((Block)block, builder.withPool(LootPool.lootPool().when(survivesExplosion).setRolls((NumberProvider)ConstantValue.exactly((float)1.0f)).add((LootPoolEntryContainer.Builder)LootItem.lootTableItem((ItemLike)((SchematicannonBlock)SCHEMATICANNON.get()).asItem()).apply((LootItemFunction.Builder)CopyComponentsFunction.copyComponents((CopyComponentsFunction.Source)CopyComponentsFunction.Source.BLOCK_ENTITY).include(AllDataComponents.SCHEMATICANNON_OPTIONS)))));
        }).item().transform(ModelGen.customItemModel())).register();
        SCHEMATIC_TABLE = ((BlockBuilder)REGISTRATE.block("schematic_table", SchematicTableBlock::new).initialProperties(() -> Blocks.LECTERN).properties(p -> p.mapColor(MapColor.PODZOL).forceSolidOn()).transform(TagGen.axeOrPickaxe())).blockstate((ctx, prov) -> prov.horizontalBlock((Block)ctx.getEntry(), (ModelFile)prov.models().getExistingFile(ctx.getId()), 0)).simpleItem().register();
        SHAFT = ((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("shaft", ShaftBlock::new).initialProperties(SharedProperties::stone).properties(p -> p.mapColor(MapColor.METAL).forceSolidOff()).transform(CStress.setNoImpact())).transform(TagGen.pickaxeOnly())).blockstate(BlockStateGen.axisBlockProvider(false)).onRegister(CreateRegistrate.blockModel(() -> BracketedKineticBlockModel::new))).simpleItem().register();
        COGWHEEL = ((BlockBuilder)((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("cogwheel", CogWheelBlock::small).initialProperties(SharedProperties::stone).properties(p -> p.sound(SoundType.WOOD).mapColor(MapColor.DIRT)).transform(CStress.setNoImpact())).transform(TagGen.axeOrPickaxe())).blockstate(BlockStateGen.axisBlockProvider(false)).onRegister(CreateRegistrate.blockModel(() -> BracketedKineticBlockModel::new))).item(CogwheelBlockItem::new).build()).register();
        LARGE_COGWHEEL = ((BlockBuilder)((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("large_cogwheel", CogWheelBlock::large).initialProperties(SharedProperties::stone).properties(p -> p.sound(SoundType.WOOD).mapColor(MapColor.DIRT)).transform(TagGen.axeOrPickaxe())).transform(CStress.setNoImpact())).blockstate(BlockStateGen.axisBlockProvider(false)).onRegister(CreateRegistrate.blockModel(() -> BracketedKineticBlockModel::new))).item(CogwheelBlockItem::new).build()).register();
        ANDESITE_ENCASED_SHAFT = ((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("andesite_encased_shaft", p -> new EncasedShaftBlock((BlockBehaviour.Properties)p, () -> ANDESITE_CASING.get())).properties(p -> p.mapColor(MapColor.PODZOL)).transform(BuilderTransformers.encasedShaft("andesite", () -> AllSpriteShifts.ANDESITE_CASING))).transform(EncasingRegistry.addVariantTo(SHAFT))).transform(TagGen.axeOrPickaxe())).register();
        BRASS_ENCASED_SHAFT = ((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("brass_encased_shaft", p -> new EncasedShaftBlock((BlockBehaviour.Properties)p, () -> BRASS_CASING.get())).properties(p -> p.mapColor(MapColor.TERRACOTTA_BROWN)).transform(BuilderTransformers.encasedShaft("brass", () -> AllSpriteShifts.BRASS_CASING))).transform(EncasingRegistry.addVariantTo(SHAFT))).transform(TagGen.axeOrPickaxe())).register();
        ANDESITE_ENCASED_COGWHEEL = ((BlockBuilder)((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("andesite_encased_cogwheel", p -> new EncasedCogwheelBlock((BlockBehaviour.Properties)p, false, () -> ANDESITE_CASING.get())).properties(p -> p.mapColor(MapColor.PODZOL)).transform(BuilderTransformers.encasedCogwheel("andesite", () -> AllSpriteShifts.ANDESITE_CASING))).transform(EncasingRegistry.addVariantTo(COGWHEEL))).onRegister(CreateRegistrate.connectedTextures(() -> new EncasedCogCTBehaviour(AllSpriteShifts.ANDESITE_CASING, (Couple<CTSpriteShiftEntry>)Couple.create((Object)((Object)AllSpriteShifts.ANDESITE_ENCASED_COGWHEEL_SIDE), (Object)((Object)AllSpriteShifts.ANDESITE_ENCASED_COGWHEEL_OTHERSIDE)))))).transform(TagGen.axeOrPickaxe())).register();
        BRASS_ENCASED_COGWHEEL = ((BlockBuilder)((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("brass_encased_cogwheel", p -> new EncasedCogwheelBlock((BlockBehaviour.Properties)p, false, () -> BRASS_CASING.get())).properties(p -> p.mapColor(MapColor.TERRACOTTA_BROWN)).transform(BuilderTransformers.encasedCogwheel("brass", () -> AllSpriteShifts.BRASS_CASING))).transform(EncasingRegistry.addVariantTo(COGWHEEL))).onRegister(CreateRegistrate.connectedTextures(() -> new EncasedCogCTBehaviour(AllSpriteShifts.BRASS_CASING, (Couple<CTSpriteShiftEntry>)Couple.create((Object)((Object)AllSpriteShifts.BRASS_ENCASED_COGWHEEL_SIDE), (Object)((Object)AllSpriteShifts.BRASS_ENCASED_COGWHEEL_OTHERSIDE)))))).transform(TagGen.axeOrPickaxe())).register();
        ANDESITE_ENCASED_LARGE_COGWHEEL = ((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("andesite_encased_large_cogwheel", p -> new EncasedCogwheelBlock((BlockBehaviour.Properties)p, true, () -> ANDESITE_CASING.get())).properties(p -> p.mapColor(MapColor.PODZOL)).transform(BuilderTransformers.encasedLargeCogwheel("andesite", () -> AllSpriteShifts.ANDESITE_CASING))).transform(EncasingRegistry.addVariantTo(LARGE_COGWHEEL))).transform(TagGen.axeOrPickaxe())).register();
        BRASS_ENCASED_LARGE_COGWHEEL = ((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("brass_encased_large_cogwheel", p -> new EncasedCogwheelBlock((BlockBehaviour.Properties)p, true, () -> BRASS_CASING.get())).properties(p -> p.mapColor(MapColor.TERRACOTTA_BROWN)).transform(BuilderTransformers.encasedLargeCogwheel("brass", () -> AllSpriteShifts.BRASS_CASING))).transform(EncasingRegistry.addVariantTo(LARGE_COGWHEEL))).transform(TagGen.axeOrPickaxe())).register();
        GEARBOX = ((BlockBuilder)((BlockBuilder)((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("gearbox", GearboxBlock::new).initialProperties(SharedProperties::stone).properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL)).transform(CStress.setNoImpact())).transform(TagGen.axeOrPickaxe())).onRegister(CreateRegistrate.connectedTextures(() -> new EncasedCTBehaviour(AllSpriteShifts.ANDESITE_CASING)))).onRegister(CreateRegistrate.casingConnectivity((block, cc) -> cc.make((Block)block, AllSpriteShifts.ANDESITE_CASING, (s, f) -> f.getAxis() == s.getValue((Property)GearboxBlock.AXIS))))).blockstate((c, p) -> BlockStateGen.axisBlock(c, p, $ -> AssetLookup.partialBaseModel(c, p, new String[0]), true)).item().transform(ModelGen.customItemModel())).register();
        CLUTCH = ((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("clutch", ClutchBlock::new).initialProperties(SharedProperties::stone).properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL)).addLayer(() -> RenderType::cutoutMipped).transform(CStress.setNoImpact())).transform(TagGen.axeOrPickaxe())).blockstate((c, p) -> BlockStateGen.axisBlock(c, p, AssetLookup.forPowered(c, p))).item().transform(ModelGen.customItemModel())).register();
        GEARSHIFT = ((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("gearshift", GearshiftBlock::new).initialProperties(SharedProperties::stone).properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL)).addLayer(() -> RenderType::cutoutMipped).transform(CStress.setNoImpact())).transform(TagGen.axeOrPickaxe())).blockstate((c, p) -> BlockStateGen.axisBlock(c, p, AssetLookup.forPowered(c, p))).item().transform(ModelGen.customItemModel())).register();
        ENCASED_CHAIN_DRIVE = ((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("encased_chain_drive", ChainDriveBlock::new).initialProperties(SharedProperties::stone).properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL)).transform(CStress.setNoImpact())).transform(TagGen.axeOrPickaxe())).blockstate((c, p) -> new ChainDriveGenerator((state, suffix) -> p.models().getExistingFile(p.modLoc("block/" + c.getName() + "/" + suffix))).generate(c, (RegistrateBlockstateProvider)p)).item().transform(ModelGen.customItemModel())).register();
        ADJUSTABLE_CHAIN_GEARSHIFT = ((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("adjustable_chain_gearshift", ChainGearshiftBlock::new).initialProperties(SharedProperties::stone).properties(p -> p.noOcclusion().mapColor(MapColor.NETHER)).transform(CStress.setNoImpact())).transform(TagGen.axeOrPickaxe())).blockstate((c, p) -> new ChainDriveGenerator((state, suffix) -> {
            String powered = (Boolean)state.getValue((Property)ChainGearshiftBlock.POWERED) != false ? "_powered" : "";
            return ((BlockModelBuilder)p.models().withExistingParent(c.getName() + "_" + suffix + powered, p.modLoc("block/encased_chain_drive/" + suffix))).texture("side", p.modLoc("block/" + c.getName() + powered));
        }).generate(c, (RegistrateBlockstateProvider)p)).item().model((c, p) -> ((ItemModelBuilder)p.withExistingParent(c.getName(), p.modLoc("block/encased_chain_drive/item"))).texture("side", p.modLoc("block/" + c.getName()))).build()).register();
        BELT = ((BlockBuilder)((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("belt", BeltBlock::new).properties(p -> p.sound(SoundType.WOOL).strength(0.8f).mapColor(MapColor.COLOR_GRAY)).addLayer(() -> RenderType::cutoutMipped).transform(TagGen.axeOrPickaxe())).blockstate(new BeltGenerator()::generate).transform(CStress.setNoImpact())).transform(DisplaySource.displaySource(AllDisplaySources.ITEM_NAMES))).onRegister(CreateRegistrate.blockModel(() -> BeltModel::new))).clientExtension(() -> () -> new BeltBlock.RenderProperties()).register();
        CHAIN_CONVEYOR = ((BlockBuilder)((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("chain_conveyor", ChainConveyorBlock::new).initialProperties(SharedProperties::stone).properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL)).transform(TagGen.axeOrPickaxe())).transform(CStress.setImpact(1.0))).transform(CStress.setImpact(1.0))).blockstate((c, p) -> p.simpleBlock((Block)c.getEntry(), AssetLookup.partialBaseModel(c, p, new String[0]))).item().transform(ModelGen.customItemModel())).register();
        CREATIVE_MOTOR = ((BlockBuilder)((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("creative_motor", CreativeMotorBlock::new).initialProperties(SharedProperties::stone).properties(p -> p.mapColor(MapColor.COLOR_PURPLE).forceSolidOn()).tag(new TagKey[]{AllTags.AllBlockTags.SAFE_NBT.tag}).transform(TagGen.pickaxeOnly())).blockstate(new CreativeMotorGenerator()::generate).transform(CStress.setCapacity(16384.0))).onRegister(BlockStressValues.setGeneratorSpeed(256, true))).item().properties(p -> p.rarity(Rarity.EPIC)).transform(ModelGen.customItemModel())).register();
        WATER_WHEEL = ((BlockBuilder)((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("water_wheel", WaterWheelBlock::new).initialProperties(SharedProperties::wooden).properties(p -> p.noOcclusion().mapColor(MapColor.DIRT)).transform(TagGen.axeOrPickaxe())).blockstate((c, p) -> BlockStateGen.directionalBlockIgnoresWaterlogged(c, p, s -> AssetLookup.partialBaseModel(c, p, new String[0]))).addLayer(() -> RenderType::cutoutMipped).transform(CStress.setCapacity(32.0))).onRegister(BlockStressValues.setGeneratorSpeed(8))).item().transform(ModelGen.customItemModel())).register();
        LARGE_WATER_WHEEL = ((BlockBuilder)((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("large_water_wheel", LargeWaterWheelBlock::new).initialProperties(SharedProperties::wooden).properties(p -> p.noOcclusion().mapColor(MapColor.DIRT)).transform(TagGen.axeOrPickaxe())).blockstate((c, p) -> BlockStateGen.axisBlock(c, p, s -> (Boolean)s.getValue((Property)LargeWaterWheelBlock.EXTENSION) != false ? AssetLookup.partialBaseModel(c, p, "extension") : AssetLookup.partialBaseModel(c, p, new String[0]))).transform(CStress.setCapacity(128.0))).onRegister(BlockStressValues.setGeneratorSpeed(4))).item(LargeWaterWheelBlockItem::new).transform(ModelGen.customItemModel())).register();
        WATER_WHEEL_STRUCTURAL = ((BlockBuilder)REGISTRATE.block("water_wheel_structure", WaterWheelStructuralBlock::new).initialProperties(SharedProperties::wooden).clientExtension(() -> () -> new WaterWheelStructuralBlock.RenderProperties()).blockstate((c, p) -> p.getVariantBuilder((Block)c.get()).forAllStatesExcept(BlockStateGen.mapToAir(p), new Property[]{WaterWheelStructuralBlock.FACING})).properties(p -> p.noOcclusion().mapColor(MapColor.DIRT)).transform(TagGen.axeOrPickaxe())).lang("Large Water Wheel").register();
        ENCASED_FAN = ((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("encased_fan", EncasedFanBlock::new).initialProperties(SharedProperties::stone).properties(p -> p.mapColor(MapColor.PODZOL)).blockstate(BlockStateGen.directionalBlockProvider(true)).addLayer(() -> RenderType::cutoutMipped).transform(TagGen.axeOrPickaxe())).transform(CStress.setImpact(2.0))).item().transform(ModelGen.customItemModel())).register();
        NOZZLE = ((BlockBuilder)((BlockBuilder)REGISTRATE.block("nozzle", NozzleBlock::new).initialProperties(SharedProperties::stone).properties(p -> p.mapColor(MapColor.COLOR_LIGHT_GRAY)).tag(new TagKey[]{AllTags.AllBlockTags.BRITTLE.tag}).transform(TagGen.axeOrPickaxe())).blockstate(BlockStateGen.directionalBlockProvider(true)).addLayer(() -> RenderType::cutoutMipped).item().transform(ModelGen.customItemModel())).register();
        TURNTABLE = ((BlockBuilder)((BlockBuilder)REGISTRATE.block("turntable", TurntableBlock::new).initialProperties(SharedProperties::wooden).properties(p -> p.mapColor(MapColor.PODZOL)).transform(TagGen.axeOrPickaxe())).blockstate((c, p) -> p.simpleBlock((Block)c.getEntry(), AssetLookup.standardModel(c, p))).transform(CStress.setImpact(4.0))).simpleItem().register();
        HAND_CRANK = ((BlockBuilder)((BlockBuilder)((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("hand_crank", HandCrankBlock::new).initialProperties(SharedProperties::wooden).properties(p -> p.mapColor(MapColor.PODZOL)).transform(TagGen.axeOrPickaxe())).blockstate(BlockStateGen.directionalBlockProvider(true)).transform(CStress.setCapacity(8.0))).onRegister(BlockStressValues.setGeneratorSpeed(32))).tag(new TagKey[]{AllTags.AllBlockTags.BRITTLE.tag}).onRegister(ItemUseOverrides::addBlock)).item().transform(ModelGen.customItemModel())).register();
        CUCKOO_CLOCK = ((BlockBuilder)((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("cuckoo_clock", CuckooClockBlock::regular).properties(p -> p.mapColor(MapColor.TERRACOTTA_YELLOW)).transform(TagGen.axeOrPickaxe())).transform(BuilderTransformers.cuckooClock())).transform(DisplaySource.displaySource(AllDisplaySources.TIME_OF_DAY))).transform(DisplaySource.displaySource(AllDisplaySources.STOPWATCH))).register();
        MYSTERIOUS_CUCKOO_CLOCK = ((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("mysterious_cuckoo_clock", CuckooClockBlock::mysterious).properties(p -> p.mapColor(MapColor.TERRACOTTA_YELLOW)).transform(TagGen.axeOrPickaxe())).transform(BuilderTransformers.cuckooClock())).lang("Cuckoo Clock").onRegisterAfter(Registries.ITEM, c -> ItemDescription.referKey((ItemLike)c, CUCKOO_CLOCK))).register();
        MILLSTONE = ((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("millstone", MillstoneBlock::new).initialProperties(SharedProperties::stone).properties(p -> p.mapColor(MapColor.METAL)).transform(TagGen.pickaxeOnly())).blockstate((c, p) -> p.simpleBlock((Block)c.getEntry(), AssetLookup.partialBaseModel(c, p, new String[0]))).transform(CStress.setImpact(4.0))).item().transform(ModelGen.customItemModel())).register();
        CRUSHING_WHEEL = ((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("crushing_wheel", CrushingWheelBlock::new).properties(p -> p.mapColor(MapColor.METAL)).initialProperties(SharedProperties::stone).properties(BlockBehaviour.Properties::noOcclusion).transform(TagGen.pickaxeOnly())).blockstate((c, p) -> BlockStateGen.axisBlock(c, p, s -> AssetLookup.partialBaseModel(c, p, new String[0]))).addLayer(() -> RenderType::cutoutMipped).transform(CStress.setImpact(8.0))).item().transform(ModelGen.customItemModel())).register();
        CRUSHING_WHEEL_CONTROLLER = REGISTRATE.block("crushing_wheel_controller", CrushingWheelControllerBlock::new).properties(p -> p.mapColor(MapColor.STONE).noOcclusion().noLootTable().air().noCollission().pushReaction(PushReaction.BLOCK)).blockstate((c, p) -> p.getVariantBuilder((Block)c.get()).forAllStatesExcept(BlockStateGen.mapToAir(p), new Property[]{CrushingWheelControllerBlock.FACING})).register();
        MECHANICAL_PRESS = ((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("mechanical_press", MechanicalPressBlock::new).initialProperties(SharedProperties::stone).properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL)).transform(TagGen.axeOrPickaxe())).blockstate(BlockStateGen.horizontalBlockProvider(true)).transform(CStress.setImpact(8.0))).item(AssemblyOperatorBlockItem::new).transform(ModelGen.customItemModel())).register();
        MECHANICAL_MIXER = ((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("mechanical_mixer", MechanicalMixerBlock::new).initialProperties(SharedProperties::stone).properties(p -> p.noOcclusion().mapColor(MapColor.STONE)).transform(TagGen.axeOrPickaxe())).blockstate((c, p) -> p.simpleBlock((Block)c.getEntry(), AssetLookup.partialBaseModel(c, p, new String[0]))).addLayer(() -> RenderType::cutoutMipped).transform(CStress.setImpact(4.0))).item(AssemblyOperatorBlockItem::new).transform(ModelGen.customItemModel())).register();
        BASIN = ((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("basin", BasinBlock::new).initialProperties(SharedProperties::stone).properties(p -> p.mapColor(MapColor.COLOR_GRAY).sound(SoundType.NETHERITE_BLOCK)).transform(TagGen.pickaxeOnly())).blockstate(new BasinGenerator()::generate).addLayer(() -> RenderType::cutoutMipped).onRegister(MovementBehaviour.movementBehaviour(new BasinMovementBehaviour()))).item().transform(ModelGen.customItemModel("_", "block"))).register();
        BLAZE_BURNER = ((BlockBuilder)((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("blaze_burner", BlazeBurnerBlock::new).initialProperties(SharedProperties::softMetal).properties(p -> p.mapColor(MapColor.COLOR_GRAY).lightLevel(BlazeBurnerBlock::getLight)).transform(TagGen.pickaxeOnly())).addLayer(() -> RenderType::cutoutMipped).tag(new TagKey[]{AllTags.AllBlockTags.FAN_PROCESSING_CATALYSTS_BLASTING.tag, AllTags.AllBlockTags.FAN_PROCESSING_CATALYSTS_SMOKING.tag, AllTags.AllBlockTags.FAN_TRANSPARENT.tag, AllTags.AllBlockTags.PASSIVE_BOILER_HEATERS.tag}).loot((lt, block) -> lt.add((Block)block, BlazeBurnerBlock.buildLootTable())).blockstate((c, p) -> p.simpleBlock((Block)c.getEntry(), AssetLookup.partialBaseModel(c, p, new String[0]))).onRegister(MovementBehaviour.movementBehaviour(new BlazeBurnerMovementBehaviour()))).onRegister(MovingInteractionBehaviour.interactionBehaviour(new ConductorBlockInteractionBehavior.BlazeBurner()))).item(BlazeBurnerBlockItem::withBlaze).model(AssetLookup.customBlockItemModel("blaze_burner", "block_with_blaze")).build()).register();
        LIT_BLAZE_BURNER = ((BlockBuilder)REGISTRATE.block("lit_blaze_burner", LitBlazeBurnerBlock::new).initialProperties(SharedProperties::softMetal).properties(p -> p.mapColor(MapColor.COLOR_LIGHT_GRAY).lightLevel(LitBlazeBurnerBlock::getLight)).transform(TagGen.pickaxeOnly())).addLayer(() -> RenderType::cutoutMipped).tag(new TagKey[]{AllTags.AllBlockTags.FAN_PROCESSING_CATALYSTS_HAUNTING.tag, AllTags.AllBlockTags.FAN_PROCESSING_CATALYSTS_SMOKING.tag, AllTags.AllBlockTags.FAN_TRANSPARENT.tag, AllTags.AllBlockTags.PASSIVE_BOILER_HEATERS.tag}).loot((lt, block) -> lt.dropOther((Block)block, (ItemLike)AllItems.EMPTY_BLAZE_BURNER.get())).blockstate((c, p) -> p.getVariantBuilder((Block)c.get()).forAllStates(state -> ConfiguredModel.builder().modelFile((ModelFile)p.models().getExistingFile(p.modLoc("block/blaze_burner/" + (state.getValue(LitBlazeBurnerBlock.FLAME_TYPE) == LitBlazeBurnerBlock.FlameType.SOUL ? "block_with_soul_fire" : "block_with_fire")))).build())).register();
        DEPOT = ((BlockBuilder)((BlockBuilder)((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("depot", DepotBlock::new).initialProperties(SharedProperties::stone).properties(p -> p.mapColor(MapColor.COLOR_GRAY)).transform(TagGen.axeOrPickaxe())).blockstate((c, p) -> p.simpleBlock((Block)c.getEntry(), AssetLookup.partialBaseModel(c, p, new String[0]))).transform(DisplaySource.displaySource(AllDisplaySources.ITEM_NAMES))).onRegister(MovingInteractionBehaviour.interactionBehaviour(new MountedDepotInteractionBehaviour()))).transform(MountedItemStorageType.mountedItemStorage(AllMountedStorageTypes.DEPOT))).item().transform(ModelGen.customItemModel("_", "block"))).register();
        WEIGHTED_EJECTOR = ((BlockBuilder)((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("weighted_ejector", EjectorBlock::new).initialProperties(SharedProperties::stone).properties(p -> p.noOcclusion().mapColor(MapColor.COLOR_GRAY)).transform(TagGen.axeOrPickaxe())).blockstate((c, p) -> p.horizontalBlock((Block)c.getEntry(), AssetLookup.partialBaseModel(c, p, new String[0]), 180)).transform(CStress.setImpact(2.0))).transform(DisplaySource.displaySource(AllDisplaySources.ITEM_NAMES))).item(EjectorItem::new).transform(ModelGen.customItemModel())).register();
        CHUTE = ((BlockBuilder)((BlockBuilder)REGISTRATE.block("chute", ChuteBlock::new).initialProperties(SharedProperties::softMetal).properties(p -> p.mapColor(MapColor.COLOR_GRAY).sound(SoundType.NETHERITE_BLOCK).noOcclusion().isSuffocating((state, level, pos) -> false)).transform(TagGen.pickaxeOnly())).addLayer(() -> RenderType::cutoutMipped).clientExtension(() -> () -> new ReducedDestroyEffects()).blockstate(new ChuteGenerator()::generate).item(ChuteItem::new).transform(ModelGen.customItemModel("_", "block"))).register();
        SMART_CHUTE = ((BlockBuilder)((BlockBuilder)REGISTRATE.block("smart_chute", SmartChuteBlock::new).initialProperties(SharedProperties::softMetal).properties(p -> p.mapColor(MapColor.COLOR_GRAY).sound(SoundType.NETHERITE_BLOCK).noOcclusion().isSuffocating((state, level, pos) -> false).isRedstoneConductor((state, level, pos) -> false)).addLayer(() -> RenderType::cutoutMipped).clientExtension(() -> () -> new ReducedDestroyEffects()).transform(TagGen.pickaxeOnly())).blockstate((c, p) -> BlockStateGen.simpleBlock(c, p, AssetLookup.forPowered(c, p))).item().transform(ModelGen.customItemModel("_", "block"))).register();
        SPEEDOMETER = ((BlockBuilder)((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("speedometer", GaugeBlock::speed).initialProperties(SharedProperties::wooden).properties(p -> p.mapColor(MapColor.PODZOL)).transform(TagGen.axeOrPickaxe())).transform(CStress.setNoImpact())).blockstate(new GaugeGenerator()::generate).transform(DisplaySource.displaySource(AllDisplaySources.KINETIC_SPEED))).item().transform(ModelGen.customItemModel("gauge", "_", "item"))).register();
        STRESSOMETER = ((BlockBuilder)((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("stressometer", GaugeBlock::stress).initialProperties(SharedProperties::wooden).properties(p -> p.mapColor(MapColor.PODZOL)).transform(TagGen.axeOrPickaxe())).transform(CStress.setNoImpact())).blockstate(new GaugeGenerator()::generate).transform(DisplaySource.displaySource(AllDisplaySources.KINETIC_STRESS))).item().transform(ModelGen.customItemModel("gauge", "_", "item"))).register();
        WOODEN_BRACKET = ((BlockBuilder)((BlockBuilder)REGISTRATE.block("wooden_bracket", BracketBlock::new).blockstate(new BracketGenerator("wooden")::generate).properties(p -> p.sound(SoundType.SCAFFOLDING)).transform(TagGen.axeOrPickaxe())).item(BracketBlockItem::new).tag(new TagKey[]{AllTags.AllItemTags.INVALID_FOR_TRACK_PAVING.tag}).transform(BracketGenerator.itemModel("wooden"))).register();
        METAL_BRACKET = ((BlockBuilder)((BlockBuilder)REGISTRATE.block("metal_bracket", BracketBlock::new).blockstate(new BracketGenerator("metal")::generate).properties(p -> p.sound(SoundType.NETHERITE_BLOCK)).transform(TagGen.pickaxeOnly())).item(BracketBlockItem::new).tag(new TagKey[]{AllTags.AllItemTags.INVALID_FOR_TRACK_PAVING.tag}).transform(BracketGenerator.itemModel("metal"))).register();
        FLUID_PIPE = ((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("fluid_pipe", FluidPipeBlock::new).initialProperties(SharedProperties::copperMetal).properties(p -> p.forceSolidOff()).transform(TagGen.pickaxeOnly())).blockstate(BlockStateGen.pipe()).onRegister(CreateRegistrate.blockModel(() -> PipeAttachmentModel::withAO))).item().transform(ModelGen.customItemModel())).register();
        ENCASED_FLUID_PIPE = ((BlockBuilder)((BlockBuilder)((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("encased_fluid_pipe", p -> new EncasedPipeBlock((BlockBehaviour.Properties)p, () -> COPPER_CASING.get())).initialProperties(SharedProperties::copperMetal).properties(p -> p.noOcclusion().mapColor(MapColor.TERRACOTTA_LIGHT_GRAY)).transform(TagGen.axeOrPickaxe())).blockstate(BlockStateGen.encasedPipe()).onRegister(CreateRegistrate.connectedTextures(() -> new EncasedCTBehaviour(AllSpriteShifts.COPPER_CASING)))).onRegister(CreateRegistrate.casingConnectivity((block, cc) -> cc.make((Block)block, AllSpriteShifts.COPPER_CASING, (s, f) -> (Boolean)s.getValue((Property)EncasedPipeBlock.FACING_TO_PROPERTY_MAP.get(f)) == false)))).onRegister(CreateRegistrate.blockModel(() -> PipeAttachmentModel::withAO))).loot((p, b) -> p.dropOther((Block)b, (ItemLike)FLUID_PIPE.get())).transform(EncasingRegistry.addVariantTo(FLUID_PIPE))).register();
        GLASS_FLUID_PIPE = ((BlockBuilder)((BlockBuilder)REGISTRATE.block("glass_fluid_pipe", GlassFluidPipeBlock::new).initialProperties(SharedProperties::copperMetal).properties(p -> p.noOcclusion()).addLayer(() -> RenderType::cutoutMipped).transform(TagGen.pickaxeOnly())).blockstate((c, p) -> p.getVariantBuilder((Block)c.getEntry()).forAllStatesExcept(state -> {
            Direction.Axis axis = (Direction.Axis)state.getValue((Property)BlockStateProperties.AXIS);
            return ConfiguredModel.builder().modelFile((ModelFile)p.models().getExistingFile(p.modLoc("block/fluid_pipe/window"))).uvLock(false).rotationX(axis == Direction.Axis.Y ? 0 : 90).rotationY(axis == Direction.Axis.X ? 90 : 0).build();
        }, new Property[]{BlockStateProperties.WATERLOGGED})).onRegister(CreateRegistrate.blockModel(() -> PipeAttachmentModel::withAO))).loot((p, b) -> p.dropOther((Block)b, (ItemLike)FLUID_PIPE.get())).register();
        MECHANICAL_PUMP = ((BlockBuilder)((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("mechanical_pump", PumpBlock::new).initialProperties(SharedProperties::copperMetal).properties(p -> p.mapColor(MapColor.STONE)).transform(TagGen.pickaxeOnly())).blockstate(BlockStateGen.directionalBlockProviderIgnoresWaterlogged(true)).onRegister(CreateRegistrate.blockModel(() -> PipeAttachmentModel::withAO))).transform(CStress.setImpact(4.0))).item().transform(ModelGen.customItemModel())).register();
        SMART_FLUID_PIPE = ((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("smart_fluid_pipe", SmartFluidPipeBlock::new).initialProperties(SharedProperties::copperMetal).properties(p -> p.mapColor(MapColor.TERRACOTTA_YELLOW)).transform(TagGen.pickaxeOnly())).blockstate(new SmartFluidPipeGenerator()::generate).onRegister(CreateRegistrate.blockModel(() -> PipeAttachmentModel::withAO))).item().transform(ModelGen.customItemModel())).register();
        FLUID_VALVE = ((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("fluid_valve", FluidValveBlock::new).initialProperties(SharedProperties::copperMetal).transform(TagGen.pickaxeOnly())).addLayer(() -> RenderType::cutoutMipped).blockstate((c, p) -> BlockStateGen.directionalAxisBlock(c, p, (state, vertical) -> AssetLookup.partialBaseModel(c, p, vertical != false ? "vertical" : "horizontal", (Boolean)state.getValue((Property)FluidValveBlock.ENABLED) != false ? "open" : "closed"))).onRegister(CreateRegistrate.blockModel(() -> PipeAttachmentModel::withAO))).item().transform(ModelGen.customItemModel())).register();
        COPPER_VALVE_HANDLE = ((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("copper_valve_handle", ValveHandleBlock::copper).transform(TagGen.pickaxeOnly())).transform(BuilderTransformers.valveHandle(null))).transform(CStress.setCapacity(8.0))).register();
        DYED_VALVE_HANDLES = new DyedBlockList<ValveHandleBlock>(colour -> {
            String colourName = colour.getSerializedName();
            return ((BlockBuilder)((BlockBuilder)REGISTRATE.block(colourName + "_valve_handle", p -> ValveHandleBlock.dyed(p, colour)).properties(p -> p.mapColor(colour.getMapColor())).transform(TagGen.pickaxeOnly())).transform(BuilderTransformers.valveHandle(colour))).recipe((c, p) -> ShapelessRecipeBuilder.shapeless((RecipeCategory)RecipeCategory.MISC, (ItemLike)((ItemLike)c.get())).requires(colour.getTag()).requires(AllTags.AllItemTags.VALVE_HANDLES.tag).unlockedBy("has_valve", RegistrateRecipeProvider.has(AllTags.AllItemTags.VALVE_HANDLES.tag)).save((RecipeOutput)p, Create.asResource("crafting/kinetics/" + c.getName() + "_from_other_valve_handle"))).register();
        });
        FLUID_TANK = ((BlockBuilder)((BlockBuilder)((BlockBuilder)((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("fluid_tank", FluidTankBlock::regular).initialProperties(SharedProperties::copperMetal).properties(p -> p.noOcclusion().isRedstoneConductor((p1, p2, p3) -> true)).transform(TagGen.pickaxeOnly())).blockstate(new FluidTankGenerator()::generate).onRegister(CreateRegistrate.blockModel(() -> FluidTankModel::standard))).transform(DisplaySource.displaySource(AllDisplaySources.BOILER))).transform(MountedFluidStorageType.mountedFluidStorage(AllMountedStorageTypes.FLUID_TANK))).onRegister(MovementBehaviour.movementBehaviour(new FluidTankMovementBehavior()))).addLayer(() -> RenderType::cutoutMipped).item(FluidTankItem::new).model(AssetLookup.customBlockItemModel("_", "block_single_window")).build()).register();
        CREATIVE_FLUID_TANK = ((BlockBuilder)((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("creative_fluid_tank", FluidTankBlock::creative).initialProperties(SharedProperties::copperMetal).properties(p -> p.noOcclusion().mapColor(MapColor.COLOR_PURPLE)).transform(TagGen.pickaxeOnly())).tag(new TagKey[]{AllTags.AllBlockTags.SAFE_NBT.tag}).blockstate(new FluidTankGenerator("creative_")::generate).onRegister(CreateRegistrate.blockModel(() -> FluidTankModel::creative))).transform(MountedFluidStorageType.mountedFluidStorage(AllMountedStorageTypes.CREATIVE_FLUID_TANK))).addLayer(() -> RenderType::cutoutMipped).item(FluidTankItem::new).properties(p -> p.rarity(Rarity.EPIC)).model((c, p) -> ((ItemModelBuilder)((ItemModelBuilder)((ItemModelBuilder)((ItemModelBuilder)((ItemModelBuilder)p.withExistingParent(c.getName(), p.modLoc("block/fluid_tank/block_single_window"))).texture("5", p.modLoc("block/creative_fluid_tank_window_single"))).texture("1", p.modLoc("block/creative_fluid_tank"))).texture("particle", p.modLoc("block/creative_fluid_tank"))).texture("4", p.modLoc("block/creative_casing"))).texture("0", p.modLoc("block/creative_casing"))).build()).register();
        HOSE_PULLEY = ((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("hose_pulley", HosePulleyBlock::new).initialProperties(SharedProperties::copperMetal).properties(BlockBehaviour.Properties::noOcclusion).addLayer(() -> RenderType::cutoutMipped).transform(TagGen.pickaxeOnly())).blockstate(BlockStateGen.horizontalBlockProvider(true)).transform(CStress.setImpact(4.0))).item().transform(ModelGen.customItemModel())).register();
        ITEM_DRAIN = ((BlockBuilder)REGISTRATE.block("item_drain", ItemDrainBlock::new).initialProperties(SharedProperties::copperMetal).transform(TagGen.pickaxeOnly())).addLayer(() -> RenderType::cutoutMipped).blockstate((c, p) -> p.simpleBlock((Block)c.get(), AssetLookup.standardModel(c, p))).simpleItem().register();
        SPOUT = ((BlockBuilder)((BlockBuilder)REGISTRATE.block("spout", SpoutBlock::new).initialProperties(SharedProperties::copperMetal).transform(TagGen.pickaxeOnly())).blockstate((ctx, prov) -> prov.simpleBlock((Block)ctx.getEntry(), AssetLookup.partialBaseModel(ctx, prov, new String[0]))).addLayer(() -> RenderType::cutoutMipped).item(AssemblyOperatorBlockItem::new).transform(ModelGen.customItemModel())).register();
        PORTABLE_FLUID_INTERFACE = ((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("portable_fluid_interface", PortableStorageInterfaceBlock::forFluids).initialProperties(SharedProperties::copperMetal).properties(p -> p.mapColor(MapColor.TERRACOTTA_LIGHT_GRAY)).transform(TagGen.axeOrPickaxe())).blockstate((c, p) -> p.directionalBlock((Block)c.get(), AssetLookup.partialBaseModel(c, p, new String[0]))).onRegister(MovementBehaviour.movementBehaviour(new PortableStorageInterfaceMovement()))).item().tag(new TagKey[]{AllTags.AllItemTags.CONTRAPTION_CONTROLLED.tag}).transform(ModelGen.customItemModel())).register();
        STEAM_ENGINE = ((BlockBuilder)((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("steam_engine", SteamEngineBlock::new).initialProperties(SharedProperties::copperMetal).transform(TagGen.pickaxeOnly())).blockstate((c, p) -> p.horizontalFaceBlock((Block)c.get(), AssetLookup.partialBaseModel(c, p, new String[0]))).transform(CStress.setCapacity(1024.0))).onRegister(BlockStressValues.setGeneratorSpeed(64, true))).item().transform(ModelGen.customItemModel())).register();
        STEAM_WHISTLE = ((BlockBuilder)((BlockBuilder)REGISTRATE.block("steam_whistle", WhistleBlock::new).initialProperties(SharedProperties::copperMetal).properties(p -> p.mapColor(MapColor.GOLD)).transform(TagGen.pickaxeOnly())).blockstate(new WhistleGenerator()::generate).item().transform(ModelGen.customItemModel())).register();
        STEAM_WHISTLE_EXTENSION = ((BlockBuilder)REGISTRATE.block("steam_whistle_extension", WhistleExtenderBlock::new).initialProperties(SharedProperties::copperMetal).properties(p -> p.mapColor(MapColor.GOLD).forceSolidOn()).transform(TagGen.pickaxeOnly())).blockstate(BlockStateGen.whistleExtender()).register();
        POWERED_SHAFT = ((BlockBuilder)REGISTRATE.block("powered_shaft", PoweredShaftBlock::new).initialProperties(SharedProperties::stone).properties(p -> p.mapColor(MapColor.METAL).forceSolidOn()).transform(TagGen.pickaxeOnly())).blockstate(BlockStateGen.axisBlockProvider(false)).loot((lt, block) -> lt.dropOther((Block)block, (ItemLike)SHAFT.get())).register();
        MECHANICAL_PISTON = ((BlockBuilder)((BlockBuilder)REGISTRATE.block("mechanical_piston", MechanicalPistonBlock::normal).properties(p -> p.mapColor(MapColor.PODZOL)).transform(TagGen.axeOrPickaxe())).transform(BuilderTransformers.mechanicalPiston(PistonType.DEFAULT))).tag(new TagKey[]{AllTags.AllBlockTags.SAFE_NBT.tag}).register();
        STICKY_MECHANICAL_PISTON = ((BlockBuilder)((BlockBuilder)REGISTRATE.block("sticky_mechanical_piston", MechanicalPistonBlock::sticky).properties(p -> p.mapColor(MapColor.PODZOL)).transform(TagGen.axeOrPickaxe())).transform(BuilderTransformers.mechanicalPiston(PistonType.STICKY))).tag(new TagKey[]{AllTags.AllBlockTags.SAFE_NBT.tag}).register();
        PISTON_EXTENSION_POLE = ((BlockBuilder)REGISTRATE.block("piston_extension_pole", PistonExtensionPoleBlock::new).initialProperties(() -> Blocks.PISTON_HEAD).properties(p -> p.sound(SoundType.SCAFFOLDING).mapColor(MapColor.DIRT).forceSolidOn()).transform(TagGen.axeOrPickaxe())).blockstate(BlockStateGen.directionalBlockProviderIgnoresWaterlogged(false)).simpleItem().register();
        MECHANICAL_PISTON_HEAD = ((BlockBuilder)REGISTRATE.block("mechanical_piston_head", MechanicalPistonHeadBlock::new).initialProperties(() -> Blocks.PISTON_HEAD).properties(p -> p.mapColor(MapColor.DIRT)).transform(TagGen.axeOrPickaxe())).loot((p, b) -> p.dropOther((Block)b, (ItemLike)PISTON_EXTENSION_POLE.get())).blockstate((c, p) -> BlockStateGen.directionalBlockIgnoresWaterlogged(c, p, state -> p.models().getExistingFile(p.modLoc("block/mechanical_piston/" + ((PistonType)state.getValue(MechanicalPistonHeadBlock.TYPE)).getSerializedName() + "/head")))).register();
        GANTRY_CARRIAGE = ((BlockBuilder)((BlockBuilder)REGISTRATE.block("gantry_carriage", GantryCarriageBlock::new).initialProperties(SharedProperties::stone).properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL)).transform(TagGen.axeOrPickaxe())).blockstate(BlockStateGen.directionalAxisBlockProvider()).item().transform(ModelGen.customItemModel())).register();
        GANTRY_SHAFT = ((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("gantry_shaft", GantryShaftBlock::new).initialProperties(SharedProperties::stone).properties(p -> p.mapColor(MapColor.NETHER).forceSolidOn()).transform(TagGen.axeOrPickaxe())).blockstate((c, p) -> p.directionalBlock((Block)c.get(), s -> {
            boolean isPowered = (Boolean)s.getValue((Property)GantryShaftBlock.POWERED);
            boolean isFlipped = ((Direction)s.getValue((Property)GantryShaftBlock.FACING)).getAxisDirection() == Direction.AxisDirection.NEGATIVE;
            String partName = ((GantryShaftBlock.Part)((Object)((Object)((Object)s.getValue(GantryShaftBlock.PART))))).getSerializedName();
            String flipped = isFlipped ? "_flipped" : "";
            String powered = isPowered ? "_powered" : "";
            ModelFile existing = AssetLookup.partialBaseModel(c, p, partName);
            if (!isPowered && !isFlipped) {
                return existing;
            }
            return ((BlockModelBuilder)p.models().withExistingParent("block/" + c.getName() + "_" + partName + powered + flipped, existing.getLocation())).texture("2", p.modLoc("block/" + c.getName() + powered + flipped));
        })).transform(CStress.setNoImpact())).item().transform(ModelGen.customItemModel("_", "block_single"))).register();
        WINDMILL_BEARING = ((BlockBuilder)((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("windmill_bearing", WindmillBearingBlock::new).transform(TagGen.axeOrPickaxe())).properties(p -> p.mapColor(MapColor.PODZOL)).transform(BuilderTransformers.bearing("windmill", "gearbox"))).transform(CStress.setCapacity(512.0))).onRegister(BlockStressValues.setGeneratorSpeed(16, true))).tag(new TagKey[]{AllTags.AllBlockTags.SAFE_NBT.tag}).register();
        MECHANICAL_BEARING = ((BlockBuilder)((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("mechanical_bearing", MechanicalBearingBlock::new).properties(p -> p.mapColor(MapColor.PODZOL)).transform(TagGen.axeOrPickaxe())).transform(BuilderTransformers.bearing("mechanical", "gearbox"))).transform(CStress.setImpact(4.0))).tag(new TagKey[]{AllTags.AllBlockTags.SAFE_NBT.tag}).onRegister(MovementBehaviour.movementBehaviour(new StabilizedBearingMovementBehaviour()))).register();
        CLOCKWORK_BEARING = ((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("clockwork_bearing", ClockworkBearingBlock::new).properties(p -> p.mapColor(MapColor.TERRACOTTA_BROWN)).transform(TagGen.axeOrPickaxe())).transform(BuilderTransformers.bearing("clockwork", "brass_gearbox"))).transform(CStress.setImpact(4.0))).tag(new TagKey[]{AllTags.AllBlockTags.SAFE_NBT.tag}).register();
        ROPE_PULLEY = ((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("rope_pulley", PulleyBlock::new).initialProperties(SharedProperties::stone).properties(p -> p.mapColor(MapColor.PODZOL)).properties(p -> p.noOcclusion()).addLayer(() -> RenderType::cutoutMipped).transform(TagGen.axeOrPickaxe())).tag(new TagKey[]{AllTags.AllBlockTags.SAFE_NBT.tag}).blockstate(BlockStateGen.horizontalAxisBlockProvider(true)).transform(CStress.setImpact(4.0))).item().transform(ModelGen.customItemModel())).register();
        ROPE = REGISTRATE.block("rope", PulleyBlock.RopeBlock::new).properties(p -> p.sound(SoundType.WOOL).mapColor(MapColor.COLOR_BROWN)).tag(new TagKey[]{AllTags.AllBlockTags.BRITTLE.tag}).tag(new TagKey[]{BlockTags.CLIMBABLE}).blockstate((c, p) -> p.simpleBlock((Block)c.get(), (ModelFile)p.models().getExistingFile(p.modLoc("block/rope_pulley/" + c.getName())))).register();
        PULLEY_MAGNET = REGISTRATE.block("pulley_magnet", PulleyBlock.MagnetBlock::new).initialProperties(SharedProperties::stone).tag(new TagKey[]{AllTags.AllBlockTags.BRITTLE.tag}).tag(new TagKey[]{BlockTags.CLIMBABLE}).blockstate((c, p) -> p.simpleBlock((Block)c.get(), (ModelFile)p.models().getExistingFile(p.modLoc("block/rope_pulley/" + c.getName())))).register();
        ELEVATOR_PULLEY = ((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("elevator_pulley", ElevatorPulleyBlock::new).initialProperties(SharedProperties::softMetal).properties(p -> p.mapColor(MapColor.TERRACOTTA_BROWN)).transform(TagGen.axeOrPickaxe())).blockstate(BlockStateGen.horizontalBlockProvider(true)).transform(CStress.setImpact(4.0))).item().transform(ModelGen.customItemModel())).register();
        CART_ASSEMBLER = ((BlockBuilder)((BlockBuilder)REGISTRATE.block("cart_assembler", CartAssemblerBlock::new).initialProperties(SharedProperties::stone).properties(p -> p.noOcclusion().mapColor(MapColor.COLOR_GRAY)).transform(TagGen.axeOrPickaxe())).blockstate(BlockStateGen.cartAssembler()).addLayer(() -> RenderType::cutoutMipped).tag(new TagKey[]{BlockTags.RAILS, AllTags.AllBlockTags.SAFE_NBT.tag}).item(CartAssemblerBlockItem::new).transform(ModelGen.customItemModel())).register();
        CONTROLLER_RAIL = ((BlockBuilder)((BlockBuilder)REGISTRATE.block("controller_rail", ControllerRailBlock::new).initialProperties(() -> Blocks.POWERED_RAIL).transform(TagGen.pickaxeOnly())).blockstate(new ControllerRailGenerator()::generate).addLayer(() -> RenderType::cutoutMipped).color(() -> () -> (state, world, pos, layer) -> RedStoneWireBlock.getColorForPower((int)(pos != null && world != null ? (Integer)state.getValue((Property)BlockStateProperties.POWER) : 0))).tag(new TagKey[]{BlockTags.RAILS}).item().model((c, p) -> p.generated((NonNullSupplier)c, new ResourceLocation[]{Create.asResource("block/" + c.getName())})).build()).register();
        MINECART_ANCHOR = REGISTRATE.block("minecart_anchor", CartAssemblerBlock.MinecartAnchorBlock::new).initialProperties(SharedProperties::stone).blockstate((c, p) -> p.simpleBlock((Block)c.get(), (ModelFile)p.models().getExistingFile(p.modLoc("block/cart_assembler/" + c.getName())))).register();
        LINEAR_CHASSIS = ((BlockBuilder)((BlockBuilder)REGISTRATE.block("linear_chassis", LinearChassisBlock::new).initialProperties(SharedProperties::wooden).properties(p -> p.mapColor(MapColor.TERRACOTTA_BROWN)).transform(TagGen.axeOrPickaxe())).tag(new TagKey[]{AllTags.AllBlockTags.SAFE_NBT.tag}).blockstate(BlockStateGen.linearChassis()).onRegister(CreateRegistrate.connectedTextures(LinearChassisBlock.ChassisCTBehaviour::new))).lang("Linear Chassis").simpleItem().register();
        SECONDARY_LINEAR_CHASSIS = ((BlockBuilder)((BlockBuilder)REGISTRATE.block("secondary_linear_chassis", LinearChassisBlock::new).initialProperties(SharedProperties::wooden).properties(p -> p.mapColor(MapColor.PODZOL)).transform(TagGen.axeOrPickaxe())).tag(new TagKey[]{AllTags.AllBlockTags.SAFE_NBT.tag}).blockstate(BlockStateGen.linearChassis()).onRegister(CreateRegistrate.connectedTextures(LinearChassisBlock.ChassisCTBehaviour::new))).simpleItem().register();
        RADIAL_CHASSIS = ((BlockBuilder)((BlockBuilder)REGISTRATE.block("radial_chassis", RadialChassisBlock::new).initialProperties(SharedProperties::wooden).properties(p -> p.mapColor(MapColor.DIRT)).transform(TagGen.axeOrPickaxe())).tag(new TagKey[]{AllTags.AllBlockTags.SAFE_NBT.tag}).blockstate(BlockStateGen.radialChassis()).item().model((c, p) -> {
            String path = "block/" + c.getName();
            p.cubeColumn(c.getName(), p.modLoc(path + "_side"), p.modLoc(path + "_end"));
        }).build()).register();
        STICKER = ((BlockBuilder)((BlockBuilder)REGISTRATE.block("sticker", StickerBlock::new).initialProperties(SharedProperties::stone).transform(TagGen.pickaxeOnly())).properties(BlockBehaviour.Properties::noOcclusion).addLayer(() -> RenderType::cutoutMipped).blockstate((c, p) -> p.directionalBlock((Block)c.get(), AssetLookup.forPowered(c, p))).item().transform(ModelGen.customItemModel())).register();
        CONTRAPTION_CONTROLS = ((BlockBuilder)((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("contraption_controls", ContraptionControlsBlock::new).initialProperties(SharedProperties::stone).properties(p -> p.mapColor(MapColor.PODZOL)).addLayer(() -> RenderType::cutoutMipped).transform(TagGen.axeOrPickaxe())).blockstate((c, p) -> p.horizontalBlock((Block)c.get(), s -> AssetLookup.partialBaseModel(c, p, new String[0]))).onRegister(MovementBehaviour.movementBehaviour(new ContraptionControlsMovement()))).onRegister(MovingInteractionBehaviour.interactionBehaviour(new ContraptionControlsMovingInteraction()))).item().transform(ModelGen.customItemModel())).register();
        MECHANICAL_DRILL = ((BlockBuilder)((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("mechanical_drill", DrillBlock::new).initialProperties(SharedProperties::stone).properties(p -> p.mapColor(MapColor.PODZOL)).transform(TagGen.axeOrPickaxe())).blockstate(BlockStateGen.directionalBlockProvider(true)).transform(CStress.setImpact(4.0))).onRegister(MovementBehaviour.movementBehaviour(new DrillMovementBehaviour()))).item().tag(new TagKey[]{AllTags.AllItemTags.CONTRAPTION_CONTROLLED.tag}).transform(ModelGen.customItemModel())).register();
        MECHANICAL_SAW = ((BlockBuilder)((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("mechanical_saw", SawBlock::new).initialProperties(SharedProperties::stone).addLayer(() -> RenderType::cutoutMipped).properties(p -> p.mapColor(MapColor.PODZOL)).transform(TagGen.axeOrPickaxe())).blockstate(new SawGenerator()::generate).transform(CStress.setImpact(4.0))).onRegister(MovementBehaviour.movementBehaviour(new SawMovementBehaviour()))).addLayer(() -> RenderType::cutoutMipped).item().tag(new TagKey[]{AllTags.AllItemTags.CONTRAPTION_CONTROLLED.tag}).transform(ModelGen.customItemModel())).register();
        DEPLOYER = ((BlockBuilder)((BlockBuilder)((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("deployer", DeployerBlock::new).initialProperties(SharedProperties::stone).properties(p -> p.mapColor(MapColor.PODZOL)).transform(TagGen.axeOrPickaxe())).blockstate(BlockStateGen.directionalAxisBlockProvider()).transform(CStress.setImpact(4.0))).onRegister(MovementBehaviour.movementBehaviour(new DeployerMovementBehaviour()))).onRegister(MovingInteractionBehaviour.interactionBehaviour(new DeployerMovingInteraction()))).item(AssemblyOperatorBlockItem::new).tag(new TagKey[]{AllTags.AllItemTags.CONTRAPTION_CONTROLLED.tag}).transform(ModelGen.customItemModel())).register();
        PORTABLE_STORAGE_INTERFACE = ((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("portable_storage_interface", PortableStorageInterfaceBlock::forItems).initialProperties(SharedProperties::stone).properties(p -> p.mapColor(MapColor.PODZOL)).transform(TagGen.axeOrPickaxe())).blockstate((c, p) -> p.directionalBlock((Block)c.get(), AssetLookup.partialBaseModel(c, p, new String[0]))).onRegister(MovementBehaviour.movementBehaviour(new PortableStorageInterfaceMovement()))).item().tag(new TagKey[]{AllTags.AllItemTags.CONTRAPTION_CONTROLLED.tag}).transform(ModelGen.customItemModel())).register();
        REDSTONE_CONTACT = ((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("redstone_contact", RedstoneContactBlock::new).initialProperties(SharedProperties::stone).properties(p -> p.mapColor(MapColor.COLOR_GRAY)).transform(TagGen.axeOrPickaxe())).onRegister(MovementBehaviour.movementBehaviour(new ContactMovementBehaviour()))).blockstate((c, p) -> p.directionalBlock((Block)c.get(), AssetLookup.forPowered(c, p))).item(RedstoneContactItem::new).tag(new TagKey[]{AllTags.AllItemTags.CONTRAPTION_CONTROLLED.tag}).transform(ModelGen.customItemModel("_", "block"))).register();
        ELEVATOR_CONTACT = ((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("elevator_contact", ElevatorContactBlock::new).initialProperties(SharedProperties::softMetal).properties(p -> p.mapColor(MapColor.TERRACOTTA_YELLOW).lightLevel(ElevatorContactBlock::getLight)).transform(TagGen.axeOrPickaxe())).blockstate((c, p) -> p.directionalBlock((Block)c.get(), state -> {
            Boolean calling = (Boolean)state.getValue((Property)ElevatorContactBlock.CALLING);
            Boolean powering = (Boolean)state.getValue((Property)ElevatorContactBlock.POWERING);
            return powering != false ? AssetLookup.partialBaseModel(c, p, "powered") : (calling != false ? AssetLookup.partialBaseModel(c, p, "dim") : AssetLookup.partialBaseModel(c, p, new String[0]));
        })).loot((p, b) -> p.dropOther((Block)b, (ItemLike)REDSTONE_CONTACT.get())).transform(DisplaySource.displaySource(AllDisplaySources.CURRENT_FLOOR))).item().transform(ModelGen.customItemModel("_", "block"))).register();
        MECHANICAL_HARVESTER = ((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("mechanical_harvester", HarvesterBlock::new).initialProperties(SharedProperties::stone).properties(p -> p.mapColor(MapColor.METAL).forceSolidOn()).transform(TagGen.axeOrPickaxe())).onRegister(MovementBehaviour.movementBehaviour(new HarvesterMovementBehaviour()))).blockstate(BlockStateGen.horizontalBlockProvider(true)).addLayer(() -> RenderType::cutoutMipped).item().tag(new TagKey[]{AllTags.AllItemTags.CONTRAPTION_CONTROLLED.tag}).transform(ModelGen.customItemModel())).register();
        MECHANICAL_PLOUGH = ((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("mechanical_plough", PloughBlock::new).initialProperties(SharedProperties::stone).properties(p -> p.mapColor(MapColor.COLOR_GRAY).forceSolidOn()).transform(TagGen.axeOrPickaxe())).onRegister(MovementBehaviour.movementBehaviour(new PloughMovementBehaviour()))).blockstate(BlockStateGen.horizontalBlockProvider(false)).item().tag(new TagKey[]{AllTags.AllItemTags.CONTRAPTION_CONTROLLED.tag}).build()).register();
        MECHANICAL_ROLLER = ((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("mechanical_roller", RollerBlock::new).initialProperties(SharedProperties::stone).properties(p -> p.mapColor(MapColor.COLOR_GRAY).noOcclusion()).transform(TagGen.axeOrPickaxe())).onRegister(MovementBehaviour.movementBehaviour(new RollerMovementBehaviour()))).blockstate(BlockStateGen.horizontalBlockProvider(true)).addLayer(() -> RenderType::cutoutMipped).item(RollerBlockItem::new).tag(new TagKey[]{AllTags.AllItemTags.CONTRAPTION_CONTROLLED.tag}).transform(ModelGen.customItemModel())).register();
        SAIL_FRAME = ((BlockBuilder)REGISTRATE.block("sail_frame", p -> SailBlock.frame(p)).initialProperties(SharedProperties::wooden).properties(p -> p.mapColor(MapColor.DIRT).sound(SoundType.SCAFFOLDING).noOcclusion()).transform(TagGen.axeOnly())).blockstate(BlockStateGen.directionalBlockProvider(false)).lang("Windmill Sail Frame").tag(new TagKey[]{AllTags.AllBlockTags.WINDMILL_SAILS.tag}).tag(new TagKey[]{AllTags.AllBlockTags.FAN_TRANSPARENT.tag}).simpleItem().register();
        SAIL = ((BlockBuilder)((BlockBuilder)REGISTRATE.block("white_sail", p -> SailBlock.withCanvas(p, DyeColor.WHITE)).initialProperties(SharedProperties::wooden).properties(p -> p.mapColor(MapColor.SNOW).sound(SoundType.SCAFFOLDING).noOcclusion()).transform(TagGen.axeOnly())).blockstate(BlockStateGen.directionalBlockProvider(false)).lang("Windmill Sail").tag(new TagKey[]{AllTags.AllBlockTags.WINDMILL_SAILS.tag}).item(BlankSailBlockItem::new).build()).register();
        DYED_SAILS = new DyedBlockList<SailBlock>(colour -> {
            if (colour == DyeColor.WHITE) {
                return SAIL;
            }
            String colourName = colour.getSerializedName();
            return ((BlockBuilder)REGISTRATE.block(colourName + "_sail", p -> SailBlock.withCanvas(p, colour)).initialProperties(SharedProperties::wooden).properties(p -> p.mapColor(colour.getMapColor()).sound(SoundType.SCAFFOLDING).noOcclusion()).transform(TagGen.axeOnly())).blockstate((c, p) -> p.directionalBlock((Block)c.get(), (ModelFile)((BlockModelBuilder)p.models().withExistingParent(colourName + "_sail", p.modLoc("block/white_sail"))).texture("0", p.modLoc("block/sail/canvas_" + colourName)))).tag(new TagKey[]{AllTags.AllBlockTags.WINDMILL_SAILS.tag}).loot((p, b) -> p.dropOther((Block)b, (ItemLike)SAIL.get())).register();
        });
        ANDESITE_CASING = ((BlockBuilder)REGISTRATE.block("andesite_casing", CasingBlock::new).properties(p -> p.mapColor(MapColor.PODZOL)).transform(BuilderTransformers.casing(() -> AllSpriteShifts.ANDESITE_CASING))).register();
        BRASS_CASING = ((BlockBuilder)REGISTRATE.block("brass_casing", CasingBlock::new).properties(p -> p.mapColor(MapColor.TERRACOTTA_BROWN)).transform(BuilderTransformers.casing(() -> AllSpriteShifts.BRASS_CASING))).register();
        COPPER_CASING = ((BlockBuilder)REGISTRATE.block("copper_casing", CasingBlock::new).properties(p -> p.mapColor(MapColor.TERRACOTTA_LIGHT_GRAY).sound(SoundType.COPPER)).transform(BuilderTransformers.casing(() -> AllSpriteShifts.COPPER_CASING))).register();
        SHADOW_STEEL_CASING = ((BlockBuilder)REGISTRATE.block("shadow_steel_casing", CasingBlock::new).properties(p -> p.mapColor(MapColor.COLOR_BLACK)).transform(BuilderTransformers.casing(() -> AllSpriteShifts.SHADOW_STEEL_CASING))).lang("Shadow Casing").register();
        REFINED_RADIANCE_CASING = ((BlockBuilder)REGISTRATE.block("refined_radiance_casing", CasingBlock::new).properties(p -> p.mapColor(MapColor.SNOW)).transform(BuilderTransformers.casing(() -> AllSpriteShifts.REFINED_RADIANCE_CASING))).properties(p -> p.lightLevel($ -> 12)).lang("Radiant Casing").register();
        MECHANICAL_CRAFTER = ((BlockBuilder)((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("mechanical_crafter", MechanicalCrafterBlock::new).initialProperties(SharedProperties::softMetal).properties(p -> p.noOcclusion().mapColor(MapColor.TERRACOTTA_YELLOW)).transform(TagGen.axeOrPickaxe())).blockstate(BlockStateGen.horizontalBlockProvider(true)).transform(CStress.setImpact(2.0))).onRegister(CreateRegistrate.connectedTextures(CrafterCTBehaviour::new))).addLayer(() -> RenderType::cutoutMipped).item().transform(ModelGen.customItemModel())).register();
        SEQUENCED_GEARSHIFT = ((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("sequenced_gearshift", SequencedGearshiftBlock::new).initialProperties(SharedProperties::stone).properties(p -> p.mapColor(MapColor.TERRACOTTA_BROWN)).transform(TagGen.axeOrPickaxe())).tag(new TagKey[]{AllTags.AllBlockTags.SAFE_NBT.tag}).properties(BlockBehaviour.Properties::noOcclusion).transform(CStress.setNoImpact())).blockstate(new SequencedGearshiftGenerator()::generate).item().transform(ModelGen.customItemModel())).register();
        FLYWHEEL = ((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("flywheel", FlywheelBlock::new).initialProperties(SharedProperties::softMetal).properties(p -> p.noOcclusion().mapColor(MapColor.TERRACOTTA_YELLOW)).transform(TagGen.axeOrPickaxe())).transform(CStress.setNoImpact())).blockstate(BlockStateGen.axisBlockProvider(true)).item().transform(ModelGen.customItemModel())).register();
        ROTATION_SPEED_CONTROLLER = ((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("rotation_speed_controller", SpeedControllerBlock::new).initialProperties(SharedProperties::softMetal).properties(p -> p.mapColor(MapColor.TERRACOTTA_YELLOW)).transform(TagGen.axeOrPickaxe())).tag(new TagKey[]{AllTags.AllBlockTags.SAFE_NBT.tag}).transform(CStress.setNoImpact())).blockstate(BlockStateGen.horizontalAxisBlockProvider(true)).item().transform(ModelGen.customItemModel())).register();
        MECHANICAL_ARM = ((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("mechanical_arm", ArmBlock::new).initialProperties(SharedProperties::softMetal).properties(p -> p.mapColor(MapColor.TERRACOTTA_YELLOW)).transform(TagGen.axeOrPickaxe())).blockstate((c, p) -> p.getVariantBuilder((Block)c.get()).forAllStates(s -> ConfiguredModel.builder().modelFile(AssetLookup.partialBaseModel(c, p, new String[0])).rotationX((Boolean)s.getValue((Property)ArmBlock.CEILING) != false ? 180 : 0).build())).transform(CStress.setImpact(2.0))).item(ArmItem::new).transform(ModelGen.customItemModel())).register();
        TRACK = ((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("track", TrackMaterial.ANDESITE::createBlock).initialProperties(SharedProperties::stone).properties(p -> p.mapColor(MapColor.METAL).strength(0.8f).sound(SoundType.METAL).noOcclusion().forceSolidOn()).addLayer(() -> RenderType::cutoutMipped).transform(TagGen.pickaxeOnly())).clientExtension(() -> () -> new TrackBlock.RenderProperties()).onRegister(CreateRegistrate.blockModel(() -> TrackModel::new))).blockstate(new TrackBlockStateGenerator()::generate).tag(new TagKey[]{Tags.Blocks.RELOCATION_NOT_SUPPORTED}).tag(new TagKey[]{AllTags.AllBlockTags.TRACKS.tag}).tag(new TagKey[]{AllTags.AllBlockTags.GIRDABLE_TRACKS.tag}).lang("Train Track").item(TrackBlockItem::new).tag(new TagKey[]{AllTags.AllItemTags.TRACKS.tag}).model((c, p) -> p.generated((NonNullSupplier)c, new ResourceLocation[]{Create.asResource("item/" + c.getName())})).build()).register();
        FAKE_TRACK = REGISTRATE.block("fake_track", FakeTrackBlock::new).properties(p -> p.mapColor(MapColor.METAL).noCollission().noOcclusion().replaceable()).blockstate((c, p) -> p.simpleBlock((Block)c.get(), (ModelFile)p.models().withExistingParent(c.getName(), p.mcLoc("block/air")))).lang("Track Marker for Maps").register();
        RAILWAY_CASING = ((BlockBuilder)REGISTRATE.block("railway_casing", CasingBlock::new).transform(BuilderTransformers.layeredCasing(() -> AllSpriteShifts.RAILWAY_CASING_SIDE, () -> AllSpriteShifts.RAILWAY_CASING))).properties(p -> p.mapColor(MapColor.TERRACOTTA_CYAN).sound(SoundType.NETHERITE_BLOCK)).lang("Train Casing").register();
        TRACK_STATION = ((BlockBuilder)((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("track_station", StationBlock::new).initialProperties(SharedProperties::softMetal).properties(p -> p.mapColor(MapColor.PODZOL).sound(SoundType.NETHERITE_BLOCK)).transform(TagGen.pickaxeOnly())).blockstate((c, p) -> p.simpleBlock((Block)c.get(), AssetLookup.partialBaseModel(c, p, new String[0]))).transform(DisplaySource.displaySource(AllDisplaySources.STATION_SUMMARY))).transform(DisplaySource.displaySource(AllDisplaySources.TRAIN_STATUS))).lang("Train Station").item(TrackTargetingBlockItem.ofType(EdgePointType.STATION)).transform(ModelGen.customItemModel())).register();
        TRACK_SIGNAL = ((BlockBuilder)((BlockBuilder)REGISTRATE.block("track_signal", SignalBlock::new).initialProperties(SharedProperties::softMetal).properties(p -> p.mapColor(MapColor.PODZOL).noOcclusion().sound(SoundType.NETHERITE_BLOCK)).transform(TagGen.pickaxeOnly())).blockstate((c, p) -> p.getVariantBuilder((Block)c.get()).forAllStates(state -> ConfiguredModel.builder().modelFile(AssetLookup.partialBaseModel(c, p, ((SignalBlock.SignalType)((Object)((Object)((Object)state.getValue(SignalBlock.TYPE))))).getSerializedName())).build())).lang("Train Signal").item(TrackTargetingBlockItem.ofType(EdgePointType.SIGNAL)).transform(ModelGen.customItemModel())).register();
        TRACK_OBSERVER = ((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("track_observer", TrackObserverBlock::new).initialProperties(SharedProperties::softMetal).properties(p -> p.mapColor(MapColor.PODZOL).noOcclusion().sound(SoundType.NETHERITE_BLOCK)).blockstate((c, p) -> BlockStateGen.simpleBlock(c, p, AssetLookup.forPowered(c, p))).transform(TagGen.pickaxeOnly())).transform(DisplaySource.displaySource(AllDisplaySources.OBSERVED_TRAIN_NAME))).lang("Train Observer").item(TrackTargetingBlockItem.ofType(EdgePointType.OBSERVER)).transform(ModelGen.customItemModel("_", "block"))).register();
        SMALL_BOGEY = ((BlockBuilder)REGISTRATE.block("small_bogey", p -> new StandardBogeyBlock((BlockBehaviour.Properties)p, BogeySizes.SMALL)).properties(p -> p.mapColor(MapColor.PODZOL)).transform(BuilderTransformers.bogey())).register();
        LARGE_BOGEY = ((BlockBuilder)REGISTRATE.block("large_bogey", p -> new StandardBogeyBlock((BlockBehaviour.Properties)p, BogeySizes.LARGE)).properties(p -> p.mapColor(MapColor.PODZOL)).transform(BuilderTransformers.bogey())).register();
        TRAIN_CONTROLS = ((BlockBuilder)((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("controls", ControlsBlock::new).initialProperties(SharedProperties::softMetal).properties(p -> p.mapColor(MapColor.TERRACOTTA_BROWN).sound(SoundType.NETHERITE_BLOCK)).addLayer(() -> RenderType::cutoutMipped).transform(TagGen.pickaxeOnly())).blockstate((c, p) -> p.horizontalBlock((Block)c.get(), s -> AssetLookup.partialBaseModel(c, p, (Boolean)s.getValue((Property)ControlsBlock.VIRTUAL) != false ? "virtual" : ((Boolean)s.getValue((Property)ControlsBlock.OPEN) != false ? "open" : "closed")))).onRegister(MovementBehaviour.movementBehaviour(new ControlsMovementBehaviour()))).onRegister(MovingInteractionBehaviour.interactionBehaviour(new ControlsInteractionBehaviour()))).lang("Train Controls").item().transform(ModelGen.customItemModel())).register();
        ANDESITE_FUNNEL = ((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("andesite_funnel", AndesiteFunnelBlock::new).addLayer(() -> RenderType::cutoutMipped).initialProperties(SharedProperties::stone).properties(p -> p.mapColor(MapColor.STONE)).transform(TagGen.pickaxeOnly())).tag(new TagKey[]{AllTags.AllBlockTags.SAFE_NBT.tag}).clientExtension(() -> () -> new ReducedDestroyEffects()).onRegister(MovementBehaviour.movementBehaviour(FunnelMovementBehaviour.andesite()))).blockstate(new FunnelGenerator("andesite", false)::generate).item(FunnelItem::new).tag(new TagKey[]{AllTags.AllItemTags.CONTRAPTION_CONTROLLED.tag}).model(FunnelGenerator.itemModel("andesite")).build()).register();
        ANDESITE_BELT_FUNNEL = ((BlockBuilder)REGISTRATE.block("andesite_belt_funnel", p -> new BeltFunnelBlock((BlockEntry<? extends FunnelBlock>)ANDESITE_FUNNEL, (BlockBehaviour.Properties)p)).addLayer(() -> RenderType::cutoutMipped).initialProperties(SharedProperties::stone).properties(p -> p.mapColor(MapColor.STONE)).transform(TagGen.pickaxeOnly())).tag(new TagKey[]{AllTags.AllBlockTags.SAFE_NBT.tag}).clientExtension(() -> () -> new ReducedDestroyEffects()).blockstate(new BeltFunnelGenerator("andesite")::generate).loot((p, b) -> p.dropOther((Block)b, (ItemLike)ANDESITE_FUNNEL.get())).register();
        BRASS_FUNNEL = ((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("brass_funnel", BrassFunnelBlock::new).addLayer(() -> RenderType::cutoutMipped).initialProperties(SharedProperties::softMetal).properties(p -> p.mapColor(MapColor.TERRACOTTA_YELLOW)).transform(TagGen.pickaxeOnly())).tag(new TagKey[]{AllTags.AllBlockTags.SAFE_NBT.tag}).clientExtension(() -> () -> new ReducedDestroyEffects()).onRegister(MovementBehaviour.movementBehaviour(FunnelMovementBehaviour.brass()))).blockstate(new FunnelGenerator("brass", true)::generate).item(FunnelItem::new).tag(new TagKey[]{AllTags.AllItemTags.CONTRAPTION_CONTROLLED.tag}).model(FunnelGenerator.itemModel("brass")).build()).register();
        BRASS_BELT_FUNNEL = ((BlockBuilder)REGISTRATE.block("brass_belt_funnel", p -> new BeltFunnelBlock((BlockEntry<? extends FunnelBlock>)BRASS_FUNNEL, (BlockBehaviour.Properties)p)).addLayer(() -> RenderType::cutoutMipped).initialProperties(SharedProperties::softMetal).properties(p -> p.mapColor(MapColor.TERRACOTTA_YELLOW)).transform(TagGen.pickaxeOnly())).tag(new TagKey[]{AllTags.AllBlockTags.SAFE_NBT.tag}).clientExtension(() -> () -> new ReducedDestroyEffects()).blockstate(new BeltFunnelGenerator("brass")::generate).loot((p, b) -> p.dropOther((Block)b, (ItemLike)BRASS_FUNNEL.get())).register();
        ANDESITE_TUNNEL = ((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("andesite_tunnel", BeltTunnelBlock::new).properties(p -> p.mapColor(MapColor.STONE)).transform(BuilderTransformers.beltTunnel("andesite", ResourceLocation.withDefaultNamespace((String)"block/polished_andesite")))).transform(DisplaySource.displaySource(AllDisplaySources.ACCUMULATE_ITEMS))).transform(DisplaySource.displaySource(AllDisplaySources.ITEM_THROUGHPUT))).register();
        BRASS_TUNNEL = ((BlockBuilder)((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("brass_tunnel", BrassTunnelBlock::new).properties(p -> p.mapColor(MapColor.TERRACOTTA_YELLOW)).transform(BuilderTransformers.beltTunnel("brass", Create.asResource("block/brass_block")))).transform(DisplaySource.displaySource(AllDisplaySources.ACCUMULATE_ITEMS))).transform(DisplaySource.displaySource(AllDisplaySources.ITEM_THROUGHPUT))).onRegister(CreateRegistrate.connectedTextures(BrassTunnelCTBehaviour::new))).register();
        SMART_OBSERVER = ((BlockBuilder)((BlockBuilder)((BlockBuilder)((BlockBuilder)((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("content_observer", SmartObserverBlock::new).initialProperties(SharedProperties::stone).properties(p -> p.mapColor(MapColor.TERRACOTTA_BROWN).noOcclusion()).properties(p -> p.isRedstoneConductor(($1, $2, $3) -> false)).transform(TagGen.axeOrPickaxe())).blockstate(new SmartObserverGenerator()::generate).transform(DisplaySource.displaySource(AllDisplaySources.COUNT_ITEMS))).transform(DisplaySource.displaySource(AllDisplaySources.LIST_ITEMS))).transform(DisplaySource.displaySource(AllDisplaySources.COUNT_FLUIDS))).transform(DisplaySource.displaySource(AllDisplaySources.LIST_FLUIDS))).transform(DisplaySource.displaySource(AllDisplaySources.READ_PACKAGE_ADDRESS))).lang("Smart Observer").item().transform(ModelGen.customItemModel("_", "block"))).register();
        THRESHOLD_SWITCH = ((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("stockpile_switch", ThresholdSwitchBlock::new).initialProperties(SharedProperties::stone).properties(p -> p.mapColor(MapColor.TERRACOTTA_BROWN).noOcclusion()).properties(p -> p.isRedstoneConductor(($1, $2, $3) -> false)).transform(TagGen.axeOrPickaxe())).blockstate(new ThresholdSwitchGenerator()::generate).transform(DisplaySource.displaySource(AllDisplaySources.FILL_LEVEL))).lang("Threshold Switch").item().transform(ModelGen.customItemModel("threshold_switch", "block_wall"))).register();
        CREATIVE_CRATE = ((BlockBuilder)((BlockBuilder)REGISTRATE.block("creative_crate", CreativeCrateBlock::new).transform(BuilderTransformers.crate("creative"))).properties(p -> p.mapColor(MapColor.COLOR_PURPLE)).transform(MountedItemStorageType.mountedItemStorage(AllMountedStorageTypes.CREATIVE_CRATE))).register();
        ITEM_VAULT = ((BlockBuilder)((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("item_vault", ItemVaultBlock::new).initialProperties(SharedProperties::softMetal).properties(p -> p.mapColor(MapColor.TERRACOTTA_BLUE).sound(SoundType.NETHERITE_BLOCK).explosionResistance(1200.0f)).transform(TagGen.pickaxeOnly())).blockstate((c, p) -> p.getVariantBuilder((Block)c.get()).forAllStates(s -> ConfiguredModel.builder().modelFile(AssetLookup.standardModel(c, p)).rotationY(s.getValue(ItemVaultBlock.HORIZONTAL_AXIS) == Direction.Axis.X ? 90 : 0).build())).onRegister(CreateRegistrate.connectedTextures(ItemVaultCTBehaviour::new))).transform(MountedItemStorageType.mountedItemStorage(AllMountedStorageTypes.VAULT))).item(ItemVaultItem::new).build()).register();
        ITEM_HATCH = ((BlockBuilder)((BlockBuilder)REGISTRATE.block("item_hatch", ItemHatchBlock::new).initialProperties(SharedProperties::softMetal).properties(p -> p.mapColor(MapColor.TERRACOTTA_BLUE).sound(SoundType.NETHERITE_BLOCK)).transform(TagGen.pickaxeOnly())).addLayer(() -> RenderType::cutoutMipped).blockstate((c, p) -> p.horizontalBlock((Block)c.get(), s -> AssetLookup.partialBaseModel(c, p, (Boolean)s.getValue((Property)ItemHatchBlock.OPEN) != false ? "open" : "closed"))).item().transform(ModelGen.customItemModel("_", "block_closed"))).register();
        PACKAGER = ((BlockBuilder)REGISTRATE.block("packager", PackagerBlock::new).transform(BuilderTransformers.packager())).register();
        REPACKAGER = ((BlockBuilder)REGISTRATE.block("repackager", RepackagerBlock::new).transform(BuilderTransformers.packager())).lang("Re-Packager").register();
        PACKAGE_FROGPORT = ((BlockBuilder)((BlockBuilder)REGISTRATE.block("package_frogport", FrogportBlock::new).initialProperties(SharedProperties::softMetal).properties(p -> p.noOcclusion()).properties(p -> p.mapColor(MapColor.TERRACOTTA_BLUE).sound(SoundType.NETHERITE_BLOCK)).transform(TagGen.pickaxeOnly())).addLayer(() -> RenderType::cutoutMipped).blockstate((c, p) -> p.simpleBlock((Block)c.getEntry(), AssetLookup.partialBaseModel(c, p, new String[0]))).item(PackagePortItem::new).model(AssetLookup::customItemModel).build()).register();
        PACKAGE_POSTBOXES = new DyedBlockList<PostboxBlock>(colour -> {
            String colourName = colour.getSerializedName();
            return ((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block(colourName + "_postbox", p -> new PostboxBlock((BlockBehaviour.Properties)p, (DyeColor)colour)).initialProperties(SharedProperties::wooden).properties(p -> p.mapColor(colour)).transform(TagGen.axeOnly())).blockstate((c, p) -> p.horizontalBlock((Block)c.get(), s -> {
                String suffix = (Boolean)s.getValue((Property)PostboxBlock.OPEN) != false ? "open" : "closed";
                return ((BlockModelBuilder)((BlockModelBuilder)p.models().withExistingParent(colourName + "_postbox_" + suffix, p.modLoc("block/package_postbox/block_" + suffix))).texture("0", p.modLoc("block/post_box/post_box_" + colourName))).texture("1", p.modLoc("block/post_box/post_box_" + colourName + "_" + suffix));
            })).tag(new TagKey[]{AllTags.AllBlockTags.POSTBOXES.tag}).onRegisterAfter(Registries.ITEM, v -> ItemDescription.useKey((ItemLike)v, "block.create.package_postbox"))).item(PackagePortItem::new).recipe((c, p) -> {
                ShapedRecipeBuilder.shaped((RecipeCategory)RecipeCategory.BUILDING_BLOCKS, (ItemLike)((ItemLike)c.get())).define(Character.valueOf('D'), colour.getTag()).define(Character.valueOf('B'), (ItemLike)Items.BARREL).define(Character.valueOf('A'), AllItems.ANDESITE_ALLOY).pattern("D").pattern("B").pattern("A").unlockedBy("has_barrel", RegistrateRecipeProvider.has((ItemLike)Items.BARREL)).save((RecipeOutput)p, Create.asResource("crafting/logistics/" + c.getName()));
                ShapelessRecipeBuilder.shapeless((RecipeCategory)RecipeCategory.BUILDING_BLOCKS, (ItemLike)((ItemLike)c.get())).requires(colour.getTag()).requires(AllTags.AllItemTags.POSTBOXES.tag).unlockedBy("has_postbox", RegistrateRecipeProvider.has(AllTags.AllItemTags.POSTBOXES.tag)).save((RecipeOutput)p, Create.asResource("crafting/logistics/" + c.getName() + "_from_other_postbox"));
            }).model((c, p) -> ((ItemModelBuilder)((ItemModelBuilder)p.withExistingParent(colourName + "_postbox", p.modLoc("block/package_postbox/item"))).texture("0", p.modLoc("block/post_box/post_box_" + colourName))).texture("1", p.modLoc("block/post_box/post_box_" + colourName + "_closed"))).tag(new TagKey[]{AllTags.AllItemTags.POSTBOXES.tag}).build()).register();
        });
        STOCK_LINK = ((BlockBuilder)((BlockBuilder)REGISTRATE.block("stock_link", PackagerLinkBlock::new).initialProperties(SharedProperties::softMetal).properties(p -> p.mapColor(MapColor.TERRACOTTA_BLUE).sound(SoundType.NETHERITE_BLOCK)).transform(TagGen.pickaxeOnly())).blockstate(new PackagerLinkGenerator()::generate).item(LogisticallyLinkedBlockItem::new).transform(ModelGen.customItemModel("_", "block_vertical"))).register();
        STOCK_TICKER = ((BlockBuilder)((BlockBuilder)REGISTRATE.block("stock_ticker", StockTickerBlock::new).initialProperties(SharedProperties::softMetal).properties(p -> p.sound(SoundType.GLASS)).transform(TagGen.axeOrPickaxe())).addLayer(() -> RenderType::cutoutMipped).blockstate((c, p) -> p.horizontalBlock((Block)c.get(), AssetLookup.standardModel(c, p))).item(LogisticallyLinkedBlockItem::new).build()).register();
        REDSTONE_REQUESTER = ((BlockBuilder)((BlockBuilder)REGISTRATE.block("redstone_requester", RedstoneRequesterBlock::new).initialProperties(SharedProperties::stone).properties(p -> p.sound(SoundType.NETHERITE_BLOCK)).properties(p -> p.noOcclusion()).transform(TagGen.pickaxeOnly())).blockstate((c, p) -> BlockStateGen.horizontalAxisBlock(c, p, AssetLookup.forPowered(c, p))).item(RedstoneRequesterBlockItem::new).transform(ModelGen.customItemModel("_", "block"))).register();
        FACTORY_GAUGE = ((BlockBuilder)((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("factory_gauge", FactoryPanelBlock::new).addLayer(() -> RenderType::cutoutMipped).initialProperties(SharedProperties::copperMetal).properties(p -> p.noOcclusion()).properties(p -> p.forceSolidOn()).transform(TagGen.pickaxeOnly())).blockstate((c, p) -> p.horizontalFaceBlock((Block)c.get(), AssetLookup.partialBaseModel(c, p, new String[0]))).onRegister(CreateRegistrate.blockModel(() -> FactoryPanelModel::new))).transform(DisplaySource.displaySource(AllDisplaySources.GAUGE_STATUS))).item(FactoryPanelBlockItem::new).model(AssetLookup::customItemModel).build()).register();
        TABLE_CLOTHS = new DyedBlockList<TableClothBlock>(colour -> {
            String colourName = colour.getSerializedName();
            return ((BlockBuilder)REGISTRATE.block(colourName + "_table_cloth", p -> new TableClothBlock((BlockBehaviour.Properties)p, (DyeColor)colour)).transform(BuilderTransformers.tableCloth(colourName, (NonNullSupplier<? extends Block>)((NonNullSupplier)() -> Blocks.BLACK_CARPET), true))).properties(p -> p.mapColor(colour)).recipe((c, p) -> {
                ShapelessRecipeBuilder.shapeless((RecipeCategory)RecipeCategory.BUILDING_BLOCKS, (ItemLike)((ItemLike)c.get()), (int)2).requires(DyeHelper.getWoolOfDye(colour)).requires(AllItems.ANDESITE_ALLOY).unlockedBy("has_wool", RegistrateRecipeProvider.has((TagKey)ItemTags.WOOL)).save((RecipeOutput)p, Create.asResource("crafting/logistics/" + c.getName()));
                ShapelessRecipeBuilder.shapeless((RecipeCategory)RecipeCategory.BUILDING_BLOCKS, (ItemLike)((ItemLike)c.get())).requires(colour.getTag()).requires(AllTags.AllItemTags.DYED_TABLE_CLOTHS.tag).unlockedBy("has_postbox", RegistrateRecipeProvider.has(AllTags.AllItemTags.DYED_TABLE_CLOTHS.tag)).save((RecipeOutput)p, Create.asResource("crafting/logistics/" + c.getName() + "_from_other_table_cloth"));
            }).register();
        });
        ANDESITE_TABLE_CLOTH = ((BlockBuilder)((BlockBuilder)REGISTRATE.block("andesite_table_cloth", p -> new TableClothBlock((BlockBehaviour.Properties)p, "andesite")).transform(BuilderTransformers.tableCloth("andesite", (NonNullSupplier<? extends Block>)((NonNullSupplier)SharedProperties::stone), false))).properties(p -> p.mapColor(MapColor.STONE).requiresCorrectToolForDrops()).recipe((c, p) -> p.stonecutting(DataIngredient.items((ItemLike)((Item)AllItems.ANDESITE_ALLOY.get()), (ItemLike[])new Item[0]), RecipeCategory.DECORATIONS, () -> ((DataGenContext)c).get(), 2)).transform(TagGen.pickaxeOnly())).lang("Andesite Table Cover").register();
        BRASS_TABLE_CLOTH = ((BlockBuilder)((BlockBuilder)REGISTRATE.block("brass_table_cloth", p -> new TableClothBlock((BlockBehaviour.Properties)p, "brass")).transform(BuilderTransformers.tableCloth("brass", (NonNullSupplier<? extends Block>)((NonNullSupplier)SharedProperties::softMetal), false))).properties(p -> p.mapColor(MapColor.TERRACOTTA_YELLOW).requiresCorrectToolForDrops()).recipe((c, p) -> p.stonecutting(DataIngredient.tag(CommonMetal.BRASS.ingots), RecipeCategory.DECORATIONS, () -> ((DataGenContext)c).get(), 2)).transform(TagGen.pickaxeOnly())).lang("Brass Table Cover").register();
        COPPER_TABLE_CLOTH = ((BlockBuilder)((BlockBuilder)REGISTRATE.block("copper_table_cloth", p -> new TableClothBlock((BlockBehaviour.Properties)p, "copper")).transform(BuilderTransformers.tableCloth("copper", (NonNullSupplier<? extends Block>)((NonNullSupplier)SharedProperties::copperMetal), false))).properties(p -> p.requiresCorrectToolForDrops()).recipe((c, p) -> p.stonecutting(DataIngredient.tag(CommonMetal.COPPER.ingots), RecipeCategory.DECORATIONS, () -> ((DataGenContext)c).get(), 2)).transform(TagGen.pickaxeOnly())).lang("Copper Table Cover").register();
        DISPLAY_LINK = ((BlockBuilder)((BlockBuilder)REGISTRATE.block("display_link", DisplayLinkBlock::new).initialProperties(SharedProperties::softMetal).properties(p -> p.mapColor(MapColor.TERRACOTTA_BROWN)).addLayer(() -> RenderType::translucent).transform(TagGen.axeOrPickaxe())).blockstate((c, p) -> p.directionalBlock((Block)c.get(), AssetLookup.forPowered(c, p))).item(DisplayLinkBlockItem::new).transform(ModelGen.customItemModel("_", "block"))).register();
        DISPLAY_BOARD = ((BlockBuilder)((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("display_board", FlapDisplayBlock::new).initialProperties(SharedProperties::softMetal).properties(p -> p.mapColor(MapColor.COLOR_GRAY)).addLayer(() -> RenderType::cutoutMipped).transform(TagGen.pickaxeOnly())).transform(CStress.setNoImpact())).blockstate((c, p) -> p.horizontalBlock((Block)c.get(), AssetLookup.partialBaseModel(c, p, new String[0]))).transform(DisplayTarget.displayTarget(AllDisplayTargets.DISPLAY_BOARD))).lang("Display Board").item().transform(ModelGen.customItemModel())).register();
        ORANGE_NIXIE_TUBE = ((BlockBuilder)((BlockBuilder)REGISTRATE.block("nixie_tube", p -> new NixieTubeBlock((BlockBehaviour.Properties)p, DyeColor.ORANGE)).initialProperties(SharedProperties::softMetal).properties(p -> p.lightLevel($ -> 5).mapColor(DyeColor.ORANGE).forceSolidOn()).transform(TagGen.pickaxeOnly())).blockstate(new NixieTubeGenerator()::generate).addLayer(() -> RenderType::translucent).item().transform(ModelGen.customItemModel())).register();
        NIXIE_TUBES = new DyedBlockList<NixieTubeBlock>(colour -> {
            if (colour == DyeColor.ORANGE) {
                return ORANGE_NIXIE_TUBE;
            }
            String colourName = colour.getSerializedName();
            return ((BlockBuilder)REGISTRATE.block(colourName + "_nixie_tube", p -> new NixieTubeBlock((BlockBehaviour.Properties)p, (DyeColor)colour)).initialProperties(SharedProperties::softMetal).properties(p -> p.lightLevel($ -> 5).mapColor(colour).forceSolidOn()).transform(TagGen.pickaxeOnly())).blockstate(new NixieTubeGenerator()::generate).loot((p, b) -> p.dropOther((Block)b, (ItemLike)ORANGE_NIXIE_TUBE.get())).addLayer(() -> RenderType::translucent).register();
        });
        ROSE_QUARTZ_LAMP = ((BlockBuilder)REGISTRATE.block("rose_quartz_lamp", RoseQuartzLampBlock::new).initialProperties(() -> Blocks.REDSTONE_LAMP).properties(p -> p.mapColor(MapColor.TERRACOTTA_PINK).lightLevel(s -> (Boolean)s.getValue((Property)RoseQuartzLampBlock.POWERING) != false ? 15 : 0)).blockstate((c, p) -> BlockStateGen.simpleBlock(c, p, s -> {
            boolean powered = (Boolean)s.getValue((Property)RoseQuartzLampBlock.POWERING);
            String name = c.getName() + (powered ? "_powered" : "");
            return p.models().cubeAll(name, p.modLoc("block/" + name));
        })).transform(TagGen.pickaxeOnly())).simpleItem().register();
        REDSTONE_LINK = ((BlockBuilder)((BlockBuilder)REGISTRATE.block("redstone_link", RedstoneLinkBlock::new).initialProperties(SharedProperties::wooden).properties(p -> p.mapColor(MapColor.TERRACOTTA_BROWN).forceSolidOn()).transform(TagGen.axeOrPickaxe())).tag(new TagKey[]{AllTags.AllBlockTags.BRITTLE.tag, AllTags.AllBlockTags.SAFE_NBT.tag}).blockstate(new RedstoneLinkGenerator()::generate).addLayer(() -> RenderType::cutoutMipped).item().transform(ModelGen.customItemModel("_", "transmitter"))).register();
        ANALOG_LEVER = ((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("analog_lever", AnalogLeverBlock::new).initialProperties(() -> Blocks.LEVER).transform(TagGen.axeOrPickaxe())).tag(new TagKey[]{AllTags.AllBlockTags.SAFE_NBT.tag}).blockstate((c, p) -> p.horizontalFaceBlock((Block)c.get(), AssetLookup.partialBaseModel(c, p, new String[0]))).onRegister(ItemUseOverrides::addBlock)).item().transform(ModelGen.customItemModel())).register();
        PLACARD = ((BlockBuilder)REGISTRATE.block("placard", PlacardBlock::new).initialProperties(SharedProperties::copperMetal).properties(p -> p.forceSolidOn()).transform(TagGen.pickaxeOnly())).tag(new TagKey[]{AllTags.AllBlockTags.SAFE_NBT.tag}).blockstate((c, p) -> p.horizontalFaceBlock((Block)c.get(), AssetLookup.standardModel(c, p))).simpleItem().register();
        PULSE_REPEATER = ((BlockBuilder)REGISTRATE.block("pulse_repeater", BrassDiodeBlock::new).initialProperties(() -> Blocks.REPEATER).tag(new TagKey[]{AllTags.AllBlockTags.SAFE_NBT.tag}).blockstate(new BrassDiodeGenerator()::generate).addLayer(() -> RenderType::cutoutMipped).item().model(AbstractDiodeGenerator::diodeItemModel).build()).register();
        PULSE_EXTENDER = ((BlockBuilder)REGISTRATE.block("pulse_extender", BrassDiodeBlock::new).initialProperties(() -> Blocks.REPEATER).tag(new TagKey[]{AllTags.AllBlockTags.SAFE_NBT.tag}).blockstate(new BrassDiodeGenerator()::generate).addLayer(() -> RenderType::cutoutMipped).item().model(AbstractDiodeGenerator::diodeItemModel).build()).register();
        PULSE_TIMER = ((BlockBuilder)REGISTRATE.block("pulse_timer", BrassDiodeBlock::new).initialProperties(() -> Blocks.REPEATER).tag(new TagKey[]{AllTags.AllBlockTags.SAFE_NBT.tag}).blockstate(new BrassDiodeGenerator()::generate).addLayer(() -> RenderType::cutoutMipped).item().model(AbstractDiodeGenerator::diodeItemModel).build()).register();
        POWERED_LATCH = REGISTRATE.block("powered_latch", PoweredLatchBlock::new).initialProperties(() -> Blocks.REPEATER).blockstate(new PoweredLatchGenerator()::generate).addLayer(() -> RenderType::cutoutMipped).simpleItem().register();
        POWERED_TOGGLE_LATCH = ((BlockBuilder)REGISTRATE.block("powered_toggle_latch", ToggleLatchBlock::new).initialProperties(() -> Blocks.REPEATER).blockstate(new ToggleLatchGenerator()::generate).addLayer(() -> RenderType::cutoutMipped).item().transform(ModelGen.customItemModel("diodes", "latch_off"))).register();
        LECTERN_CONTROLLER = ((BlockBuilder)REGISTRATE.block("lectern_controller", LecternControllerBlock::new).initialProperties(() -> Blocks.LECTERN).transform(TagGen.axeOnly())).blockstate((c, p) -> p.horizontalBlock((Block)c.get(), (ModelFile)p.models().getExistingFile(p.mcLoc("block/lectern")))).loot((lt, block) -> lt.dropOther((Block)block, (ItemLike)Blocks.LECTERN)).register();
        COPPER_BACKTANK = ((BlockBuilder)REGISTRATE.block("copper_backtank", BacktankBlock::new).initialProperties(SharedProperties::copperMetal).transform(BuilderTransformers.backtank(() -> AllItems.COPPER_BACKTANK.get()))).register();
        NETHERITE_BACKTANK = ((BlockBuilder)REGISTRATE.block("netherite_backtank", BacktankBlock::new).initialProperties(SharedProperties::netheriteMetal).transform(BuilderTransformers.backtank(() -> AllItems.NETHERITE_BACKTANK.get()))).register();
        PECULIAR_BELL = ((BlockBuilder)((BlockBuilder)REGISTRATE.block("peculiar_bell", PeculiarBellBlock::new).properties(p -> p.mapColor(MapColor.GOLD).forceSolidOn()).transform(BuilderTransformers.bell())).onRegister(MovementBehaviour.movementBehaviour(new BellMovementBehaviour()))).register();
        HAUNTED_BELL = ((BlockBuilder)((BlockBuilder)REGISTRATE.block("haunted_bell", HauntedBellBlock::new).properties(p -> p.mapColor(MapColor.SAND).forceSolidOn()).transform(BuilderTransformers.bell())).onRegister(MovementBehaviour.movementBehaviour(new HauntedBellMovementBehaviour()))).register();
        DESK_BELL = ((BlockBuilder)((BlockBuilder)REGISTRATE.block("desk_bell", DeskBellBlock::new).properties(p -> p.mapColor(MapColor.SAND)).blockstate((c, p) -> p.directionalBlock((Block)c.get(), AssetLookup.forPowered(c, p))).item().transform(ModelGen.customItemModel("_", "block"))).onRegister(MovementBehaviour.movementBehaviour(new BellMovementBehaviour()))).register();
        TOOLBOXES = new DyedBlockList<ToolboxBlock>(colour -> {
            String colourName = colour.getSerializedName();
            return ((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block(colourName + "_toolbox", p -> new ToolboxBlock((BlockBehaviour.Properties)p, (DyeColor)colour)).initialProperties(SharedProperties::wooden).properties(p -> p.sound(SoundType.WOOD).mapColor(colour).forceSolidOn()).addLayer(() -> RenderType::cutoutMipped).loot((lt, block) -> lt.add((Block)block, LootTable.lootTable().withPool(LootPool.lootPool().when(ExplosionCondition.survivesExplosion()).setRolls((NumberProvider)ConstantValue.exactly((float)1.0f)).add((LootPoolEntryContainer.Builder)LootItem.lootTableItem((ItemLike)block).apply((LootItemFunction.Builder)CopyNameFunction.copyName((CopyNameFunction.NameSource)CopyNameFunction.NameSource.BLOCK_ENTITY)).apply((LootItemFunction.Builder)CopyComponentsFunction.copyComponents((CopyComponentsFunction.Source)CopyComponentsFunction.Source.BLOCK_ENTITY).include(AllDataComponents.TOOLBOX_UUID).include(AllDataComponents.TOOLBOX_INVENTORY)))))).blockstate((c, p) -> p.horizontalBlock((Block)c.get(), (ModelFile)((BlockModelBuilder)p.models().withExistingParent(colourName + "_toolbox", p.modLoc("block/toolbox/block"))).texture("0", p.modLoc("block/toolbox/" + colourName)))).onRegisterAfter(Registries.ITEM, v -> ItemDescription.useKey((ItemLike)v, "block.create.toolbox"))).transform(MountedItemStorageType.mountedItemStorage(AllMountedStorageTypes.TOOLBOX))).tag(new TagKey[]{AllTags.AllBlockTags.TOOLBOXES.tag}).item(UncontainableBlockItem::new).model((c, p) -> ((ItemModelBuilder)p.withExistingParent(colourName + "_toolbox", p.modLoc("block/toolbox/item"))).texture("0", p.modLoc("block/toolbox/" + colourName))).tag(new TagKey[]{AllTags.AllItemTags.TOOLBOXES.tag}).build()).register();
        });
        CLIPBOARD = ((BlockBuilder)((ItemBuilder)((BlockBuilder)REGISTRATE.block("clipboard", ClipboardBlock::new).initialProperties(SharedProperties::wooden).properties(p -> p.forceSolidOn()).transform(TagGen.axeOrPickaxe())).tag(new TagKey[]{AllTags.AllBlockTags.SAFE_NBT.tag}).blockstate((c, p) -> p.horizontalFaceBlock((Block)c.get(), s -> AssetLookup.partialBaseModel(c, p, (Boolean)s.getValue((Property)ClipboardBlock.WRITTEN) != false ? "written" : "empty"))).loot((lt, b) -> lt.add((Block)b, BlockLootSubProvider.noDrop())).item(ClipboardBlockItem::new).onRegister(ClipboardBlockItem::registerModelOverrides)).model((c, p) -> ClipboardOverrides.addOverrideModels((DataGenContext<Item, ClipboardBlockItem>)c, p)).build()).register();
        REGISTRATE.setCreativeTab(AllCreativeModeTabs.PALETTES_CREATIVE_TAB);
        ANDESITE_LADDER = ((BlockBuilder)REGISTRATE.block("andesite_ladder", MetalLadderBlock::new).transform(BuilderTransformers.ladder("andesite", () -> DataIngredient.items((ItemLike)((Item)AllItems.ANDESITE_ALLOY.get()), (ItemLike[])new Item[0]), MapColor.STONE))).register();
        BRASS_LADDER = ((BlockBuilder)REGISTRATE.block("brass_ladder", MetalLadderBlock::new).transform(BuilderTransformers.ladder("brass", () -> DataIngredient.tag(CommonMetal.BRASS.ingots), MapColor.TERRACOTTA_YELLOW))).register();
        COPPER_LADDER = ((BlockBuilder)REGISTRATE.block("copper_ladder", MetalLadderBlock::new).transform(BuilderTransformers.ladder("copper", () -> DataIngredient.tag(CommonMetal.COPPER.ingots), MapColor.COLOR_ORANGE))).register();
        ANDESITE_BARS = MetalBarsGen.createBars("andesite", true, () -> DataIngredient.items((ItemLike)((Item)AllItems.ANDESITE_ALLOY.get()), (ItemLike[])new Item[0]), MapColor.STONE);
        BRASS_BARS = MetalBarsGen.createBars("brass", true, () -> DataIngredient.tag(CommonMetal.BRASS.ingots), MapColor.TERRACOTTA_YELLOW);
        COPPER_BARS = MetalBarsGen.createBars("copper", true, () -> DataIngredient.tag(CommonMetal.COPPER.ingots), MapColor.COLOR_ORANGE);
        ANDESITE_SCAFFOLD = ((BlockBuilder)REGISTRATE.block("andesite_scaffolding", MetalScaffoldingBlock::new).transform(BuilderTransformers.scaffold("andesite", () -> DataIngredient.items((ItemLike)((Item)AllItems.ANDESITE_ALLOY.get()), (ItemLike[])new Item[0]), MapColor.STONE, AllSpriteShifts.ANDESITE_SCAFFOLD, AllSpriteShifts.ANDESITE_SCAFFOLD_INSIDE, AllSpriteShifts.ANDESITE_CASING))).register();
        BRASS_SCAFFOLD = ((BlockBuilder)REGISTRATE.block("brass_scaffolding", MetalScaffoldingBlock::new).transform(BuilderTransformers.scaffold("brass", () -> DataIngredient.tag(CommonMetal.BRASS.ingots), MapColor.TERRACOTTA_YELLOW, AllSpriteShifts.BRASS_SCAFFOLD, AllSpriteShifts.BRASS_SCAFFOLD_INSIDE, AllSpriteShifts.BRASS_CASING))).register();
        COPPER_SCAFFOLD = ((BlockBuilder)REGISTRATE.block("copper_scaffolding", MetalScaffoldingBlock::new).transform(BuilderTransformers.scaffold("copper", () -> DataIngredient.tag(CommonMetal.COPPER.ingots), MapColor.COLOR_ORANGE, AllSpriteShifts.COPPER_SCAFFOLD, AllSpriteShifts.COPPER_SCAFFOLD_INSIDE, AllSpriteShifts.COPPER_CASING))).register();
        METAL_GIRDER = ((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("metal_girder", GirderBlock::new).initialProperties(SharedProperties::softMetal).properties(p -> p.mapColor(MapColor.COLOR_GRAY).sound(SoundType.NETHERITE_BLOCK)).transform(TagGen.pickaxeOnly())).blockstate(GirderBlockStateGenerator::blockState).onRegister(CreateRegistrate.blockModel(() -> ConnectedGirderModel::new))).item().transform(ModelGen.customItemModel())).register();
        METAL_GIRDER_ENCASED_SHAFT = ((BlockBuilder)((BlockBuilder)REGISTRATE.block("metal_girder_encased_shaft", GirderEncasedShaftBlock::new).initialProperties(SharedProperties::softMetal).properties(p -> p.mapColor(MapColor.COLOR_GRAY).sound(SoundType.NETHERITE_BLOCK)).transform(TagGen.pickaxeOnly())).blockstate(GirderBlockStateGenerator::blockStateWithShaft).loot((p, b) -> p.add((Block)b, p.createSingleItemTable((ItemLike)METAL_GIRDER.get()).withPool((LootPool.Builder)p.applyExplosionCondition((ItemLike)SHAFT.get(), (ConditionUserBuilder)LootPool.lootPool().setRolls((NumberProvider)ConstantValue.exactly((float)1.0f)).add((LootPoolEntryContainer.Builder)LootItem.lootTableItem((ItemLike)((ItemLike)SHAFT.get()))))))).onRegister(CreateRegistrate.blockModel(() -> ConnectedGirderModel::new))).register();
        COPYCAT_BASE = ((BlockBuilder)REGISTRATE.block("copycat_base", Block::new).initialProperties(SharedProperties::softMetal).properties(p -> p.mapColor(MapColor.GLOW_LICHEN)).addLayer(() -> RenderType::cutoutMipped).tag(new TagKey[]{AllTags.AllBlockTags.FAN_TRANSPARENT.tag}).transform(TagGen.pickaxeOnly())).blockstate((c, p) -> p.simpleBlock((Block)c.get(), AssetLookup.partialBaseModel(c, p, new String[0]))).register();
        COPYCAT_STEP = ((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("copycat_step", CopycatStepBlock::new).properties(p -> p.forceSolidOn()).transform(BuilderTransformers.copycat())).onRegister(CreateRegistrate.blockModel(() -> CopycatStepModel::new))).item().recipe((c, p) -> p.stonecutting(DataIngredient.tag(CommonMetal.ZINC.ingots), RecipeCategory.BUILDING_BLOCKS, () -> ((DataGenContext)c).get(), 4)).transform(ModelGen.customItemModel("copycat_base", "step"))).register();
        COPYCAT_PANEL = ((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("copycat_panel", CopycatPanelBlock::new).transform(BuilderTransformers.copycat())).onRegister(CreateRegistrate.blockModel(() -> CopycatPanelModel::new))).item().recipe((c, p) -> p.stonecutting(DataIngredient.tag(CommonMetal.ZINC.ingots), RecipeCategory.BUILDING_BLOCKS, () -> ((DataGenContext)c).get(), 4)).transform(ModelGen.customItemModel("copycat_base", "panel"))).register();
        COPYCAT_BARS = ((BlockBuilder)REGISTRATE.block("copycat_bars", WrenchableDirectionalBlock::new).blockstate(new SpecialCopycatPanelBlockState("bars")::generate).onRegister(CreateRegistrate.blockModel(() -> CopycatBarsModel::new))).register();
        SEATS = new DyedBlockList<SeatBlock>(colour -> {
            String colourName = colour.getSerializedName();
            SeatMovementBehaviour movementBehaviour = new SeatMovementBehaviour();
            SeatInteractionBehaviour interactionBehaviour = new SeatInteractionBehaviour();
            return ((BlockBuilder)((BlockBuilder)((BlockBuilder)((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block(colourName + "_seat", p -> new SeatBlock((BlockBehaviour.Properties)p, (DyeColor)colour)).initialProperties(SharedProperties::wooden).properties(p -> p.mapColor(colour)).transform(TagGen.axeOnly())).onRegister(MovementBehaviour.movementBehaviour(movementBehaviour))).onRegister(MovingInteractionBehaviour.interactionBehaviour(interactionBehaviour))).transform(DisplaySource.displaySource(AllDisplaySources.ENTITY_NAME))).blockstate((c, p) -> p.simpleBlock((Block)c.get(), (ModelFile)((BlockModelBuilder)((BlockModelBuilder)p.models().withExistingParent(colourName + "_seat", p.modLoc("block/seat"))).texture("1", p.modLoc("block/seat/top_" + colourName))).texture("2", p.modLoc("block/seat/side_" + colourName)))).recipe((c, p) -> {
                ShapelessRecipeBuilder.shapeless((RecipeCategory)RecipeCategory.BUILDING_BLOCKS, (ItemLike)((ItemLike)c.get())).requires(DyeHelper.getWoolOfDye(colour)).requires(ItemTags.WOODEN_SLABS).unlockedBy("has_wool", RegistrateRecipeProvider.has((TagKey)ItemTags.WOOL)).save((RecipeOutput)p, Create.asResource("crafting/kinetics/" + c.getName()));
                ShapelessRecipeBuilder.shapeless((RecipeCategory)RecipeCategory.BUILDING_BLOCKS, (ItemLike)((ItemLike)c.get())).requires(colour.getTag()).requires(AllTags.AllItemTags.SEATS.tag).unlockedBy("has_seat", RegistrateRecipeProvider.has(AllTags.AllItemTags.SEATS.tag)).save((RecipeOutput)p, Create.asResource("crafting/kinetics/" + c.getName() + "_from_other_seat"));
            }).onRegisterAfter(Registries.ITEM, v -> ItemDescription.useKey((ItemLike)v, "block.create.seat"))).tag(new TagKey[]{AllTags.AllBlockTags.SEATS.tag}).item().tag(new TagKey[]{AllTags.AllItemTags.SEATS.tag}).build()).register();
        });
        ANDESITE_DOOR = ((BlockBuilder)REGISTRATE.block("andesite_door", p -> SlidingDoorBlock.stone(p, true)).transform(BuilderTransformers.slidingDoor("andesite"))).properties(p -> p.mapColor(MapColor.STONE).noOcclusion()).register();
        BRASS_DOOR = ((BlockBuilder)REGISTRATE.block("brass_door", p -> SlidingDoorBlock.stone(p, false)).transform(BuilderTransformers.slidingDoor("brass"))).properties(p -> p.mapColor(MapColor.TERRACOTTA_YELLOW).noOcclusion()).register();
        COPPER_DOOR = ((BlockBuilder)REGISTRATE.block("copper_door", p -> SlidingDoorBlock.stone(p, true)).transform(BuilderTransformers.slidingDoor("copper"))).properties(p -> p.mapColor(MapColor.COLOR_ORANGE).noOcclusion()).register();
        TRAIN_DOOR = ((BlockBuilder)REGISTRATE.block("train_door", p -> SlidingDoorBlock.metal(p, false)).transform(BuilderTransformers.slidingDoor("train"))).properties(p -> p.mapColor(MapColor.TERRACOTTA_CYAN).noOcclusion()).register();
        TRAIN_TRAPDOOR = ((BlockBuilder)REGISTRATE.block("train_trapdoor", TrainTrapdoorBlock::metal).initialProperties(SharedProperties::softMetal).properties(p -> p.mapColor(MapColor.TERRACOTTA_CYAN)).transform(BuilderTransformers.trapdoor(true))).register();
        FRAMED_GLASS_DOOR = ((BlockBuilder)REGISTRATE.block("framed_glass_door", p -> SlidingDoorBlock.glass(p, false)).transform(BuilderTransformers.slidingDoor("glass"))).properties(p -> p.mapColor(MapColor.NONE).noOcclusion()).register();
        FRAMED_GLASS_TRAPDOOR = ((BlockBuilder)((BlockBuilder)REGISTRATE.block("framed_glass_trapdoor", TrainTrapdoorBlock::glass).initialProperties(SharedProperties::softMetal).transform(BuilderTransformers.trapdoor(false))).properties(p -> p.mapColor(MapColor.NONE).noOcclusion()).onRegister(CreateRegistrate.connectedTextures(TrapdoorCTBehaviour::new))).addLayer(() -> RenderType::cutoutMipped).register();
        ZINC_ORE = ((BlockBuilder)((ItemBuilder)((BlockBuilder)REGISTRATE.block("zinc_ore", Block::new).initialProperties(() -> Blocks.GOLD_ORE).properties(p -> p.mapColor(MapColor.METAL).requiresCorrectToolForDrops().sound(SoundType.STONE)).transform(TagGen.pickaxeOnly())).loot((lt, b) -> {
            HolderLookup.RegistryLookup enchantmentRegistryLookup = lt.getRegistries().lookupOrThrow(Registries.ENCHANTMENT);
            lt.add(b, lt.createSilkTouchDispatchTable(b, (LootPoolEntryContainer.Builder)lt.applyExplosionDecay((ItemLike)b, (FunctionUserBuilder)LootItem.lootTableItem((ItemLike)((ItemLike)AllItems.RAW_ZINC.get())).apply((LootItemFunction.Builder)ApplyBonusCount.addOreBonusCount((Holder)enchantmentRegistryLookup.getOrThrow(Enchantments.FORTUNE))))));
        }).tag(new TagKey[]{BlockTags.NEEDS_IRON_TOOL}).tag(new TagKey[]{Tags.Blocks.ORES}).transform(TagGen.tagBlockAndItem(Map.of(CommonMetal.ZINC.ores.blocks(), CommonMetal.ZINC.ores.items(), Tags.Blocks.ORES_IN_GROUND_STONE, Tags.Items.ORES_IN_GROUND_STONE)))).tag(new TagKey[]{Tags.Items.ORES}).build()).register();
        DEEPSLATE_ZINC_ORE = ((BlockBuilder)((ItemBuilder)((BlockBuilder)REGISTRATE.block("deepslate_zinc_ore", Block::new).initialProperties(() -> Blocks.DEEPSLATE_GOLD_ORE).properties(p -> p.mapColor(MapColor.STONE).requiresCorrectToolForDrops().sound(SoundType.DEEPSLATE)).transform(TagGen.pickaxeOnly())).loot((lt, b) -> {
            HolderLookup.RegistryLookup enchantmentRegistryLookup = lt.getRegistries().lookupOrThrow(Registries.ENCHANTMENT);
            lt.add(b, lt.createSilkTouchDispatchTable(b, (LootPoolEntryContainer.Builder)lt.applyExplosionDecay((ItemLike)b, (FunctionUserBuilder)LootItem.lootTableItem((ItemLike)((ItemLike)AllItems.RAW_ZINC.get())).apply((LootItemFunction.Builder)ApplyBonusCount.addOreBonusCount((Holder)enchantmentRegistryLookup.getOrThrow(Enchantments.FORTUNE))))));
        }).tag(new TagKey[]{BlockTags.NEEDS_IRON_TOOL}).tag(new TagKey[]{Tags.Blocks.ORES}).transform(TagGen.tagBlockAndItem(Map.of(CommonMetal.ZINC.ores.blocks(), CommonMetal.ZINC.ores.items(), Tags.Blocks.ORES_IN_GROUND_DEEPSLATE, Tags.Items.ORES_IN_GROUND_DEEPSLATE)))).tag(new TagKey[]{Tags.Items.ORES}).build()).register();
        RAW_ZINC_BLOCK = ((BlockBuilder)((ItemBuilder)((BlockBuilder)REGISTRATE.block("raw_zinc_block", Block::new).initialProperties(() -> Blocks.RAW_GOLD_BLOCK).properties(p -> p.mapColor(MapColor.GLOW_LICHEN).requiresCorrectToolForDrops()).transform(TagGen.pickaxeOnly())).tag(new TagKey[]{Tags.Blocks.STORAGE_BLOCKS}).tag(new TagKey[]{BlockTags.NEEDS_IRON_TOOL}).lang("Block of Raw Zinc").transform(TagGen.tagBlockAndItem(CommonMetal.ZINC.rawStorageBlocks))).tag(new TagKey[]{Tags.Items.STORAGE_BLOCKS}).build()).register();
        ZINC_BLOCK = ((BlockBuilder)((ItemBuilder)((BlockBuilder)REGISTRATE.block("zinc_block", Block::new).initialProperties(() -> Blocks.IRON_BLOCK).properties(p -> p.mapColor(MapColor.GLOW_LICHEN).requiresCorrectToolForDrops()).transform(TagGen.pickaxeOnly())).tag(new TagKey[]{BlockTags.NEEDS_IRON_TOOL}).tag(new TagKey[]{Tags.Blocks.STORAGE_BLOCKS}).tag(new TagKey[]{BlockTags.BEACON_BASE_BLOCKS}).transform(TagGen.tagBlockAndItem(CommonMetal.ZINC.storageBlocks))).tag(new TagKey[]{Tags.Items.STORAGE_BLOCKS}).build()).lang("Block of Zinc").register();
        ANDESITE_ALLOY_BLOCK = ((BlockBuilder)((ItemBuilder)((BlockBuilder)REGISTRATE.block("andesite_alloy_block", Block::new).initialProperties(() -> Blocks.ANDESITE).properties(p -> p.mapColor(MapColor.STONE).requiresCorrectToolForDrops()).transform(TagGen.pickaxeOnly())).blockstate(BlockStateGen.simpleCubeAll("andesite_block")).tag(new TagKey[]{Tags.Blocks.STORAGE_BLOCKS}).transform(TagGen.tagBlockAndItem(AllTags.AllBlockTags.ANDESITE_ALLOY_STORAGE_BLOCKS.tag, AllTags.AllItemTags.ANDESITE_ALLOY_STORAGE_BLOCKS.tag))).tag(new TagKey[]{Tags.Items.STORAGE_BLOCKS}).build()).lang("Block of Andesite Alloy").register();
        INDUSTRIAL_IRON_BLOCK = ((BlockBuilder)REGISTRATE.block("industrial_iron_block", Block::new).transform(BuilderTransformers.palettesIronBlock())).lang("Block of Industrial Iron").register();
        WEATHERED_IRON_BLOCK = ((BlockBuilder)REGISTRATE.block("weathered_iron_block", Block::new).transform(BuilderTransformers.palettesIronBlock())).lang("Block of Weathered Iron").register();
        BRASS_BLOCK = ((BlockBuilder)((ItemBuilder)((BlockBuilder)REGISTRATE.block("brass_block", Block::new).initialProperties(() -> Blocks.IRON_BLOCK).properties(p -> p.mapColor(MapColor.TERRACOTTA_YELLOW).requiresCorrectToolForDrops()).transform(TagGen.pickaxeOnly())).blockstate(BlockStateGen.simpleCubeAll("brass_block")).tag(new TagKey[]{BlockTags.NEEDS_IRON_TOOL}).tag(new TagKey[]{Tags.Blocks.STORAGE_BLOCKS}).tag(new TagKey[]{BlockTags.BEACON_BASE_BLOCKS}).transform(TagGen.tagBlockAndItem(CommonMetal.BRASS.storageBlocks))).tag(new TagKey[]{Tags.Items.STORAGE_BLOCKS}).build()).lang("Block of Brass").register();
        CARDBOARD_BLOCK = ((BlockBuilder)((BlockBuilder)REGISTRATE.block("cardboard_block", CardboardBlock::new).initialProperties(() -> Blocks.MUSHROOM_STEM).properties(p -> p.mapColor(MapColor.COLOR_BROWN).sound(SoundType.CHISELED_BOOKSHELF).ignitedByLava()).transform(TagGen.axeOnly())).blockstate(BlockStateGen.horizontalAxisBlockProvider(false)).tag(new TagKey[]{Tags.Blocks.STORAGE_BLOCKS}).tag(new TagKey[]{AllTags.AllBlockTags.CARDBOARD_STORAGE_BLOCKS.tag}).item().burnTime(4000).tag(new TagKey[]{AllTags.AllItemTags.CARDBOARD_STORAGE_BLOCKS.tag}).tag(new TagKey[]{Tags.Items.STORAGE_BLOCKS}).build()).lang("Block of Cardboard").register();
        BOUND_CARDBOARD_BLOCK = ((BlockBuilder)((BlockBuilder)REGISTRATE.block("bound_cardboard_block", CardboardBlock::new).initialProperties(() -> Blocks.MUSHROOM_STEM).properties(p -> p.mapColor(MapColor.COLOR_BROWN).sound(SoundType.CHISELED_BOOKSHELF).ignitedByLava()).transform(TagGen.axeOnly())).blockstate(BlockStateGen.horizontalAxisBlockProvider(false)).loot((r, b) -> r.add((Block)b, LootTable.lootTable().withPool(LootPool.lootPool().setRolls((NumberProvider)ConstantValue.exactly((float)1.0f)).add((LootPoolEntryContainer.Builder)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem((ItemLike)b).when(((BlockLootSubProviderAccessor)r).create$hasSilkTouch())).otherwise((LootPoolEntryContainer.Builder)r.applyExplosionCondition((ItemLike)b, (ConditionUserBuilder)LootItem.lootTableItem((ItemLike)Items.STRING))))).withPool((LootPool.Builder)r.applyExplosionCondition((ItemLike)b, (ConditionUserBuilder)LootPool.lootPool().setRolls((NumberProvider)ConstantValue.exactly((float)1.0f)).add((LootPoolEntryContainer.Builder)LootItem.lootTableItem((ItemLike)CARDBOARD_BLOCK.asItem())).when(((BlockLootSubProviderAccessor)r).create$hasSilkTouch().invert()))))).item().burnTime(4000).build()).lang("Bound Block of Cardboard").register();
        EXPERIENCE_BLOCK = ((BlockBuilder)((BlockBuilder)REGISTRATE.block("experience_block", ExperienceBlock::new).initialProperties(SharedProperties::softMetal).properties(p -> p.mapColor(MapColor.PLANT).sound((SoundType)new DeferredSoundType(1.0f, 0.5f, () -> SoundEvents.AMETHYST_BLOCK_BREAK, () -> SoundEvents.AMETHYST_BLOCK_STEP, () -> SoundEvents.AMETHYST_BLOCK_PLACE, () -> SoundEvents.AMETHYST_BLOCK_HIT, () -> SoundEvents.AMETHYST_BLOCK_FALL)).requiresCorrectToolForDrops().lightLevel(s -> 15)).blockstate((c, p) -> p.simpleBlock((Block)c.get(), AssetLookup.standardModel(c, p))).transform(TagGen.pickaxeOnly())).lang("Block of Experience").tag(new TagKey[]{Tags.Blocks.STORAGE_BLOCKS}).tag(new TagKey[]{BlockTags.BEACON_BASE_BLOCKS}).item().properties(p -> p.rarity(Rarity.UNCOMMON)).tag(new TagKey[]{Tags.Items.STORAGE_BLOCKS}).build()).register();
        ROSE_QUARTZ_BLOCK = ((BlockBuilder)REGISTRATE.block("rose_quartz_block", RotatedPillarBlock::new).initialProperties(() -> Blocks.AMETHYST_BLOCK).properties(p -> p.mapColor(MapColor.TERRACOTTA_PINK).requiresCorrectToolForDrops().sound(SoundType.DEEPSLATE)).transform(TagGen.pickaxeOnly())).blockstate((c, p) -> p.axisBlock((RotatedPillarBlock)c.get(), p.modLoc("block/palettes/rose_quartz_side"), p.modLoc("block/palettes/rose_quartz_top"))).recipe((c, p) -> p.stonecutting(DataIngredient.items((ItemLike)((Item)AllItems.ROSE_QUARTZ.get()), (ItemLike[])new Item[0]), RecipeCategory.BUILDING_BLOCKS, () -> ((DataGenContext)c).get(), 2)).simpleItem().lang("Block of Rose Quartz").register();
        ROSE_QUARTZ_TILES = ((BlockBuilder)REGISTRATE.block("rose_quartz_tiles", Block::new).initialProperties(() -> Blocks.DEEPSLATE).properties(p -> p.mapColor(MapColor.TERRACOTTA_PINK).requiresCorrectToolForDrops()).transform(TagGen.pickaxeOnly())).blockstate(BlockStateGen.simpleCubeAll("palettes/rose_quartz_tiles")).recipe((c, p) -> p.stonecutting(DataIngredient.items((ItemLike)((Item)AllItems.POLISHED_ROSE_QUARTZ.get()), (ItemLike[])new Item[0]), RecipeCategory.BUILDING_BLOCKS, () -> ((DataGenContext)c).get(), 2)).simpleItem().register();
        SMALL_ROSE_QUARTZ_TILES = ((BlockBuilder)REGISTRATE.block("small_rose_quartz_tiles", Block::new).initialProperties(() -> Blocks.DEEPSLATE).properties(p -> p.mapColor(MapColor.TERRACOTTA_PINK).requiresCorrectToolForDrops()).transform(TagGen.pickaxeOnly())).blockstate(BlockStateGen.simpleCubeAll("palettes/small_rose_quartz_tiles")).recipe((c, p) -> p.stonecutting(DataIngredient.items((ItemLike)((Item)AllItems.POLISHED_ROSE_QUARTZ.get()), (ItemLike[])new Item[0]), RecipeCategory.BUILDING_BLOCKS, () -> ((DataGenContext)c).get(), 2)).simpleItem().register();
        COPPER_SHINGLES = new CopperBlockSet(REGISTRATE, "copper_shingles", "copper_roof_top", CopperBlockSet.DEFAULT_VARIANTS, (c, p) -> p.stonecutting(DataIngredient.tag(CommonMetal.COPPER.ingots), RecipeCategory.BUILDING_BLOCKS, () -> ((DataGenContext)c).get(), 2), (NonNullBiConsumer<WeatheringCopper.WeatherState, Block>)((NonNullBiConsumer)(ws, block) -> CreateRegistrate.connectedTextures(() -> new RoofBlockCTBehaviour(AllSpriteShifts.COPPER_SHINGLES.get(ws))).accept(block)));
        COPPER_TILES = new CopperBlockSet(REGISTRATE, "copper_tiles", "copper_roof_top", CopperBlockSet.DEFAULT_VARIANTS, (c, p) -> p.stonecutting(DataIngredient.tag(CommonMetal.COPPER.ingots), RecipeCategory.BUILDING_BLOCKS, () -> ((DataGenContext)c).get(), 2), (NonNullBiConsumer<WeatheringCopper.WeatherState, Block>)((NonNullBiConsumer)(ws, block) -> CreateRegistrate.connectedTextures(() -> new RoofBlockCTBehaviour(AllSpriteShifts.COPPER_TILES.get(ws))).accept(block)));
    }
}
