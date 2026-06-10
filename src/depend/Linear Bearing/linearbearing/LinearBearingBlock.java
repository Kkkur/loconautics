/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.equipment.wrench.IWrenchable
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.StringRepresentable
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.AttachFace
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.DirectionProperty
 *  net.minecraft.world.level.block.state.properties.EnumProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 */
package com.bearing.linearbearing;

import com.bearing.linearbearing.LinearBearing;
import com.bearing.linearbearing.LinearCasingBlock;
import com.bearing.linearbearing.LinearMovingBlock;
import com.bearing.linearbearing.ModBlocks;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import java.lang.reflect.Method;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;

public class LinearBearingBlock
extends Block
implements IWrenchable {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<AttachFace> FACE = BlockStateProperties.ATTACH_FACE;
    public static final EnumProperty<SliderSide> SIDE = EnumProperty.create((String)"side", SliderSide.class);

    public LinearBearingBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue((Property)FACING, (Comparable)Direction.NORTH)).setValue(FACE, (Comparable)AttachFace.FLOOR)).setValue(SIDE, (Comparable)((Object)SliderSide.FRONT)));
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction clickedFace = context.getClickedFace();
        Direction playerFacing = context.getHorizontalDirection();
        BlockState state = (BlockState)((BlockState)this.defaultBlockState().setValue((Property)FACING, (Comparable)playerFacing.getOpposite())).setValue(SIDE, (Comparable)((Object)SliderSide.FRONT));
        if (clickedFace == Direction.DOWN) {
            return (BlockState)state.setValue(FACE, (Comparable)AttachFace.CEILING);
        }
        if (clickedFace == Direction.UP) {
            return (BlockState)state.setValue(FACE, (Comparable)AttachFace.FLOOR);
        }
        return (BlockState)((BlockState)state.setValue(FACE, (Comparable)AttachFace.WALL)).setValue((Property)FACING, (Comparable)clickedFace);
    }

    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (!level.isClientSide) {
            AttachFace currentFace = (AttachFace)state.getValue(FACE);
            if (currentFace == AttachFace.WALL) {
                SliderSide currentSide = (SliderSide)((Object)state.getValue(SIDE));
                SliderSide nextSide = currentSide == SliderSide.FRONT ? SliderSide.BACK : SliderSide.FRONT;
                level.setBlock(pos, (BlockState)state.setValue(SIDE, (Comparable)((Object)nextSide)), 3);
            } else {
                Direction currentFacing = (Direction)state.getValue((Property)FACING);
                Direction nextFacing = currentFacing.getClockWise();
                level.setBlock(pos, (BlockState)state.setValue((Property)FACING, (Comparable)nextFacing), 3);
            }
        }
        return InteractionResult.SUCCESS;
    }

    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        if (!level.isClientSide && player != null) {
            if (!player.isCreative()) {
                ItemStack dropStack = new ItemStack((ItemLike)this.asItem());
                if (!player.getInventory().add(dropStack)) {
                    player.drop(dropStack, false);
                }
            }
            level.destroyBlock(pos, false, (Entity)player);
        }
        return InteractionResult.SUCCESS;
    }

    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        boolean isWrench;
        ItemStack itemInHand = player.getItemInHand(InteractionHand.MAIN_HAND);
        boolean bl = isWrench = itemInHand.getItem().toString().contains("wrench") || itemInHand.getItem().getClass().getSimpleName().contains("Wrench");
        if (!itemInHand.isEmpty() && !isWrench) {
            return InteractionResult.PASS;
        }
        if (isWrench) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide) {
            BlockState casingState;
            block16: {
                AttachFace currentFace = (AttachFace)state.getValue(FACE);
                Direction currentFacing = (Direction)state.getValue((Property)FACING);
                SliderSide currentSide = (SliderSide)((Object)state.getValue(SIDE));
                Direction frontDirection = currentFace == AttachFace.FLOOR ? Direction.UP : (currentFace == AttachFace.CEILING ? Direction.DOWN : currentFacing);
                BlockPos targetFrontPos = pos.relative(frontDirection);
                BlockState originalFrontState = level.getBlockState(targetFrontPos);
                String sideName = currentSide.name();
                BlockState movingState = (BlockState)((BlockState)((BlockState)ModBlocks.LINEAR_MOVING.get().defaultBlockState().setValue(LinearMovingBlock.FACE, (Comparable)currentFace)).setValue((Property)LinearMovingBlock.FACING, (Comparable)currentFacing)).setValue(LinearMovingBlock.SIDE, (Comparable)((Object)LinearMovingBlock.SliderSide.valueOf(sideName)));
                casingState = (BlockState)((BlockState)((BlockState)ModBlocks.LINEAR_CASING.get().defaultBlockState().setValue(LinearCasingBlock.FACE, (Comparable)currentFace)).setValue((Property)LinearCasingBlock.FACING, (Comparable)currentFacing)).setValue(LinearCasingBlock.SIDE, (Comparable)((Object)LinearCasingBlock.SliderSide.valueOf(sideName)));
                try {
                    ServerLevel serverLevel;
                    block18: {
                        boolean wasAir = originalFrontState.isAir();
                        if (wasAir) {
                            level.setBlock(targetFrontPos, Blocks.STONE.defaultBlockState(), 3);
                        }
                        if (!(level instanceof ServerLevel)) break block16;
                        serverLevel = (ServerLevel)level;
                        Class<?> assemblyHelperClass = Class.forName("dev.simulated_team.simulated.util.SimAssemblyHelper");
                        Method assembleMethod = assemblyHelperClass.getMethod("assembleFromSingleBlock", Level.class, BlockPos.class, BlockPos.class, Boolean.TYPE, Boolean.TYPE);
                        Object assemblyResult = assembleMethod.invoke(null, serverLevel, pos, targetFrontPos, false, false);
                        LinearBearing.LOGGER.info("Aeronautics core successfully assembled glued sub-level structures!");
                        if (assemblyResult != null) {
                            try {
                                Class<?> levelPlotClass;
                                Method accessorMethod;
                                LevelAccessor embeddedLevel;
                                Class<?> serverSubLevelClass;
                                Method plotMethod;
                                Object plotInstance;
                                Method subLevelMethod;
                                Object subLevelInstance;
                                BlockPos assembleOffset;
                                block17: {
                                    Method offsetMethod = assemblyResult.getClass().getMethod("offset", new Class[0]);
                                    assembleOffset = (BlockPos)offsetMethod.invoke(assemblyResult, new Object[0]);
                                    try {
                                        Method subLevelMethod2 = assemblyResult.getClass().getMethod("subLevel", new Class[0]);
                                        Object subLevelInstance2 = subLevelMethod2.invoke(assemblyResult, new Object[0]);
                                        if (subLevelInstance2 == null) break block17;
                                        for (Method m : subLevelInstance2.getClass().getMethods()) {
                                            Object physicsBody;
                                            if (!m.getName().toLowerCase().contains("body") && !m.getName().toLowerCase().contains("entity") || (physicsBody = m.invoke(subLevelInstance2, new Object[0])) == null) continue;
                                            try {
                                                Method setCcdMethod = physicsBody.getClass().getMethod("setCcdMotionThreshold", Float.TYPE);
                                                setCcdMethod.invoke(physicsBody, Float.valueOf(0.1f));
                                                LinearBearing.LOGGER.info("SUCCESS! Activated High-Velocity CCD Collisions for Sable sub-level!");
                                                break;
                                            }
                                            catch (NoSuchMethodException setCcdMethod) {
                                                // empty catch block
                                            }
                                        }
                                    }
                                    catch (Exception e) {
                                        LinearBearing.LOGGER.error("Failed to inject CCD bullet-proofing into Sable engine: ", (Throwable)e);
                                    }
                                }
                                if (assembleOffset == null) break block18;
                                BlockPos plotPos = pos.offset((Vec3i)assembleOffset);
                                level.setBlock(plotPos, movingState, 3);
                                LinearBearing.LOGGER.info("SUCCESS! Injected Linear Moving block via original SwivelBearing method at: " + String.valueOf(plotPos));
                                if (!wasAir || (subLevelInstance = (subLevelMethod = assemblyResult.getClass().getMethod("subLevel", new Class[0])).invoke(assemblyResult, new Object[0])) == null || (plotInstance = (plotMethod = (serverSubLevelClass = Class.forName("dev.ryanhcode.sable.sublevel.ServerSubLevel")).getMethod("getPlot", new Class[0])).invoke(subLevelInstance, new Object[0])) == null || (embeddedLevel = (LevelAccessor)(accessorMethod = (levelPlotClass = Class.forName("dev.ryanhcode.sable.sublevel.plot.LevelPlot")).getMethod("getEmbeddedLevelAccessor", new Class[0])).invoke(plotInstance, new Object[0])) == null) break block18;
                                embeddedLevel.setBlock(BlockPos.ZERO, Blocks.AIR.defaultBlockState(), 3);
                                serverLevel.getChunkSource().blockChanged(BlockPos.ZERO);
                                try {
                                    Method setChangedMethod = levelPlotClass.getMethod("setChanged", new Class[0]);
                                    setChangedMethod.invoke(plotInstance, new Object[0]);
                                }
                                catch (Exception exception) {
                                    // empty catch block
                                }
                                LinearBearing.LOGGER.info("SUCCESS! Seamlessly vanished the temporary ghost stone from physical sub-level!");
                            }
                            catch (Exception e) {
                                LinearBearing.LOGGER.error("Failed to inject carriage block using SwivelBearing logic: ", (Throwable)e);
                            }
                        }
                    }
                    SoundEvent customSound = SoundEvent.createVariableRangeEvent((ResourceLocation)ResourceLocation.fromNamespaceAndPath((String)"simulated", (String)"block.physics_assembler.assemble"));
                    serverLevel.playSound(null, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, customSound, SoundSource.BLOCKS, 1.0f, 1.0f);
                }
                catch (Exception e) {
                    LinearBearing.LOGGER.error("Aeronautics SimAssemblyHelper assembly failed: ", (Throwable)e);
                }
            }
            level.setBlock(pos, casingState, 3);
        }
        return InteractionResult.SUCCESS;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FACING, FACE, SIDE});
    }

    public static enum SliderSide implements StringRepresentable
    {
        FRONT("front"),
        BACK("back");

        private final String name;

        private SliderSide(String name) {
            this.name = name;
        }

        public String getSerializedName() {
            return this.name;
        }
    }
}
