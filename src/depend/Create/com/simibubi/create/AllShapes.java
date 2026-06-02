/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VoxelShaper
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.DirectionalBlock
 *  net.minecraft.world.level.block.piston.PistonHeadBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.shapes.BooleanOp
 *  net.minecraft.world.phys.shapes.Shapes
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package com.simibubi.create;

import com.simibubi.create.content.logistics.chute.ChuteShapes;
import com.simibubi.create.content.trains.track.TrackVoxelShapes;
import java.util.function.BiFunction;
import net.createmod.catnip.math.VoxelShaper;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AllShapes {
    public static final VoxelShaper CASING_14PX = AllShapes.shape(0.0, 0.0, 0.0, 16.0, 14.0, 16.0).forDirectional();
    public static final VoxelShaper CASING_13PX = AllShapes.shape(0.0, 0.0, 0.0, 16.0, 13.0, 16.0).forDirectional();
    public static final VoxelShaper CASING_12PX = AllShapes.shape(0.0, 0.0, 0.0, 16.0, 12.0, 16.0).forDirectional();
    public static final VoxelShaper CASING_11PX = AllShapes.shape(0.0, 0.0, 0.0, 16.0, 11.0, 16.0).forDirectional();
    public static final VoxelShaper CASING_3PX = AllShapes.shape(0.0, 0.0, 0.0, 16.0, 3.0, 16.0).forDirectional();
    public static final VoxelShaper CASING_2PX = AllShapes.shape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0).forDirectional();
    public static final VoxelShaper MOTOR_BLOCK = AllShapes.shape(3.0, 0.0, 3.0, 13.0, 14.0, 13.0).forDirectional();
    public static final VoxelShaper FOUR_VOXEL_POLE = AllShapes.shape(6.0, 0.0, 6.0, 10.0, 16.0, 10.0).forAxis();
    public static final VoxelShaper SIX_VOXEL_POLE = AllShapes.shape(5.0, 0.0, 5.0, 11.0, 16.0, 11.0).forAxis();
    public static final VoxelShaper EIGHT_VOXEL_POLE = AllShapes.shape(4.0, 0.0, 4.0, 12.0, 16.0, 12.0).forAxis();
    public static final VoxelShaper TEN_VOXEL_POLE = AllShapes.shape(3.0, 0.0, 3.0, 13.0, 16.0, 13.0).forAxis();
    public static final VoxelShaper FURNACE_ENGINE = AllShapes.shape(1.0, 1.0, 0.0, 15.0, 15.0, 16.0).add(0.0, 0.0, 9.0, 16.0, 16.0, 14.0).forHorizontal(Direction.SOUTH);
    public static final VoxelShaper PORTABLE_STORAGE_INTERFACE = AllShapes.shape(0.0, 0.0, 0.0, 16.0, 14.0, 16.0).forDirectional();
    public static final VoxelShaper ELEVATOR_PULLEY = AllShapes.shape(0.0, 0.0, 0.0, 16.0, 16.0, 2.0).add(0.0, 0.0, 14.0, 16.0, 16.0, 16.0).add(2.0, 0.0, 2.0, 14.0, 14.0, 14.0).forHorizontal(Direction.EAST);
    public static final VoxelShaper SAIL_FRAME_COLLISION = AllShapes.shape(0.0, 5.0, 0.0, 16.0, 9.0, 16.0).erase(2.0, 0.0, 2.0, 14.0, 16.0, 14.0).forDirectional();
    public static final VoxelShaper SAIL_FRAME = AllShapes.shape(0.0, 5.0, 0.0, 16.0, 9.0, 16.0).forDirectional();
    public static final VoxelShaper SAIL = AllShapes.shape(0.0, 5.0, 0.0, 16.0, 10.0, 16.0).forDirectional();
    public static final VoxelShaper HARVESTER_BASE = AllShapes.shape(0.0, 2.0, 0.0, 16.0, 14.0, 3.0).forDirectional(Direction.SOUTH);
    public static final VoxelShaper ROLLER_BASE = AllShapes.shape(0.0, 0.0, 0.0, 16.0, 16.0, 10.0).forDirectional(Direction.SOUTH);
    public static final VoxelShaper NOZZLE = AllShapes.shape(2.0, 0.0, 2.0, 14.0, 14.0, 14.0).add(1.0, 13.0, 1.0, 15.0, 15.0, 15.0).erase(3.0, 13.0, 3.0, 13.0, 15.0, 13.0).forDirectional();
    public static final VoxelShaper CRANK = AllShapes.shape(5.0, 0.0, 5.0, 11.0, 6.0, 11.0).add(1.0, 3.0, 1.0, 15.0, 8.0, 15.0).forDirectional();
    public static final VoxelShaper VALVE_HANDLE = AllShapes.shape(5.0, 0.0, 5.0, 11.0, 4.0, 11.0).add(1.0, 3.0, 1.0, 15.0, 8.0, 15.0).forDirectional();
    public static final VoxelShaper CART_ASSEMBLER = AllShapes.shape(0.0, 12.0, 0.0, 16.0, 16.0, 16.0).add(-2.0, 0.0, 1.0, 18.0, 14.0, 15.0).forHorizontalAxis();
    public static final VoxelShaper CART_ASSEMBLER_PLAYER_COLLISION = AllShapes.shape(0.0, 0.0, 1.0, 16.0, 16.0, 15.0).forHorizontalAxis();
    public static final VoxelShaper STOCKPILE_SWITCH = AllShapes.shape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0).add(1.0, 0.0, 1.0, 15.0, 16.0, 15.0).add(0.0, 14.0, 0.0, 16.0, 16.0, 16.0).add(3.0, 3.0, -2.0, 13.0, 13.0, 2.0).forHorizontal(Direction.NORTH);
    public static final VoxelShaper CONTENT_OBSERVER = AllShapes.shape(0.0, 0.0, 0.0, 16.0, 6.0, 16.0).add(1.0, 0.0, 1.0, 15.0, 16.0, 15.0).add(0.0, 14.0, 0.0, 16.0, 16.0, 16.0).add(3.0, 3.0, -2.0, 13.0, 13.0, 2.0).forHorizontal(Direction.NORTH);
    public static final VoxelShaper FUNNEL_COLLISION = AllShapes.shape(0.0, 0.0, 0.0, 16.0, 4.0, 16.0).forDirectional(Direction.UP);
    public static final VoxelShaper BELT_FUNNEL_RETRACTED = AllShapes.shape(2.0, -2.0, 14.0, 14.0, 14.0, 18.0).add(0.0, -5.0, 8.0, 16.0, 16.0, 14.0).forHorizontal(Direction.NORTH);
    public static final VoxelShaper BELT_FUNNEL_EXTENDED = AllShapes.shape(2.0, -2.0, 14.0, 14.0, 14.0, 18.0).add(3.0, -4.0, 10.0, 13.0, 13.0, 14.0).add(2.0, -4.0, 6.0, 14.0, 14.0, 10.0).add(0.0, -5.0, 0.0, 16.0, 16.0, 6.0).forHorizontal(Direction.NORTH);
    public static final VoxelShaper BELT_FUNNEL_PERPENDICULAR = AllShapes.shape(2.0, -2.0, 14.0, 14.0, 14.0, 18.0).add(1.0, 8.0, 12.0, 15.0, 15.0, 14.0).add(0.1, 13.0, 7.0, 15.9, 15.0, 11.0).add(0.1, 9.0, 8.0, 15.9, 13.0, 12.0).add(0.1, 5.0, 9.0, 15.9, 9.0, 13.0).add(0.1, 1.0, 10.0, 15.9, 5.0, 14.0).add(0.1, -3.0, 11.0, 15.9, 1.0, 15.0).forHorizontal(Direction.NORTH);
    public static final VoxelShaper FUNNEL_WALL = AllShapes.shape(2.0, 2.0, 14.0, 14.0, 14.0, 18.0).add(1.0, 8.0, 12.0, 15.0, 15.0, 14.0).add(0.1, 13.0, 7.0, 15.9, 15.0, 11.0).add(0.1, 9.0, 8.0, 15.9, 13.0, 12.0).add(0.1, 5.0, 9.0, 15.9, 9.0, 13.0).add(0.1, 1.0, 10.0, 15.9, 5.0, 14.0).add(0.1, -1.0, 11.0, 15.9, 1.0, 15.0).forHorizontal(Direction.NORTH);
    public static final VoxelShaper FLUID_VALVE = AllShapes.shape(3.0, 0.0, 3.0, 13.0, 16.0, 13.0).add(2.0, 2.0, 2.0, 14.0, 14.0, 14.0).forAxis();
    public static final VoxelShaper TOOLBOX = AllShapes.shape(1.0, 0.0, 4.0, 15.0, 9.0, 12.0).forHorizontal(Direction.NORTH);
    public static final VoxelShaper SMART_FLUID_PIPE_FLOOR = AllShapes.shape(4.0, 4.0, 0.0, 12.0, 12.0, 16.0).add(3.0, 3.0, 3.0, 13.0, 13.0, 13.0).add(5.0, 13.0, 3.0, 11.0, 14.0, 11.0).add(5.0, 14.0, 4.0, 11.0, 15.0, 10.0).add(5.0, 15.0, 5.0, 11.0, 16.0, 9.0).add(5.0, 16.0, 6.0, 11.0, 17.0, 8.0).forHorizontal(Direction.SOUTH);
    public static final VoxelShaper SMART_FLUID_PIPE_WALL = AllShapes.shape(4.0, 0.0, 4.0, 12.0, 16.0, 12.0).add(3.0, 3.0, 3.0, 13.0, 13.0, 13.0).add(5.0, 5.0, 13.0, 11.0, 13.0, 14.0).add(5.0, 6.0, 14.0, 11.0, 12.0, 15.0).add(5.0, 7.0, 15.0, 11.0, 11.0, 16.0).add(5.0, 8.0, 16.0, 11.0, 10.0, 17.0).forHorizontal(Direction.SOUTH);
    public static final VoxelShaper SMART_FLUID_PIPE_CEILING = AllShapes.shape(4.0, 4.0, 0.0, 12.0, 12.0, 16.0).add(3.0, 3.0, 3.0, 13.0, 13.0, 13.0).add(5.0, 2.0, 3.0, 11.0, 3.0, 11.0).add(5.0, 1.0, 4.0, 11.0, 2.0, 10.0).add(5.0, 0.0, 5.0, 11.0, 1.0, 9.0).add(5.0, -1.0, 6.0, 11.0, 0.0, 8.0).forHorizontal(Direction.SOUTH);
    public static final VoxelShaper PUMP = AllShapes.shape(2.0, 0.0, 2.0, 14.0, 16.0, 14.0).forDirectional(Direction.UP);
    public static final VoxelShaper CRUSHING_WHEEL_CONTROLLER_COLLISION = AllShapes.shape(0.0, 0.0, 0.0, 16.0, 13.0, 16.0).forDirectional(Direction.DOWN);
    public static final VoxelShaper BELL_FLOOR = AllShapes.shape(0.0, 0.0, 5.0, 16.0, 11.0, 11.0).add(3.0, 1.0, 3.0, 13.0, 13.0, 13.0).forHorizontal(Direction.SOUTH);
    public static final VoxelShaper BELL_WALL = AllShapes.shape(5.0, 5.0, 8.0, 11.0, 11.0, 16.0).add(3.0, 1.0, 3.0, 13.0, 13.0, 13.0).forHorizontal(Direction.SOUTH);
    public static final VoxelShaper BELL_DOUBLE_WALL = AllShapes.shape(5.0, 5.0, 0.0, 11.0, 11.0, 16.0).add(3.0, 1.0, 3.0, 13.0, 13.0, 13.0).forHorizontal(Direction.SOUTH);
    public static final VoxelShaper BELL_CEILING = AllShapes.shape(0.0, 5.0, 5.0, 16.0, 16.0, 11.0).add(3.0, 1.0, 3.0, 13.0, 13.0, 13.0).forHorizontal(Direction.SOUTH);
    public static final VoxelShaper GIRDER_BEAM = AllShapes.shape(4.0, 2.0, 0.0, 12.0, 14.0, 16.0).forHorizontalAxis();
    public static final VoxelShaper GIRDER_BEAM_SHAFT = AllShapes.shape(GIRDER_BEAM.get(Direction.Axis.X)).add(SIX_VOXEL_POLE.get(Direction.Axis.Z)).forHorizontalAxis();
    public static final VoxelShaper STEP_BOTTOM = AllShapes.shape(0.0, 0.0, 8.0, 16.0, 8.0, 16.0).forHorizontal(Direction.SOUTH);
    public static final VoxelShaper STEP_TOP = AllShapes.shape(0.0, 8.0, 8.0, 16.0, 16.0, 16.0).forHorizontal(Direction.SOUTH);
    public static final VoxelShaper CONTROLS = AllShapes.shape(0.0, 0.0, 6.0, 16.0, 16.0, 16.0).add(0.0, 0.0, 4.0, 16.0, 2.0, 16.0).forHorizontal(Direction.NORTH);
    public static final VoxelShaper CONTROLS_COLLISION = AllShapes.shape(0.0, 0.0, 6.0, 16.0, 16.0, 16.0).forHorizontal(Direction.NORTH);
    public static final VoxelShaper CONTRAPTION_CONTROLS = AllShapes.shape(0.0, 0.0, 6.0, 2.0, 16.0, 16.0).add(14.0, 0.0, 6.0, 16.0, 16.0, 16.0).add(0.0, 0.0, 14.0, 16.0, 16.0, 16.0).add(0.0, 0.0, 6.0, 16.0, 12.0, 16.0).add(0.0, 0.0, 4.0, 16.0, 2.0, 16.0).forHorizontal(Direction.NORTH);
    public static final VoxelShaper CONTRAPTION_CONTROLS_COLLISION = AllShapes.shape(0.0, 0.0, 6.0, 2.0, 16.0, 16.0).add(14.0, 0.0, 6.0, 16.0, 16.0, 16.0).add(0.0, 0.0, 14.0, 16.0, 16.0, 16.0).add(0.0, 0.0, 7.0, 16.0, 12.0, 16.0).forHorizontal(Direction.NORTH);
    public static final VoxelShaper NIXIE_TUBE = AllShapes.shape(9.0, 0.0, 5.0, 15.0, 12.0, 11.0).add(1.0, 0.0, 5.0, 7.0, 12.0, 11.0).forHorizontalAxis();
    public static final VoxelShaper NIXIE_TUBE_CEILING = AllShapes.shape(9.0, 4.0, 5.0, 15.0, 16.0, 11.0).add(1.0, 4.0, 5.0, 7.0, 16.0, 11.0).forHorizontalAxis();
    public static final VoxelShaper NIXIE_TUBE_WALL = AllShapes.shape(5.0, 9.0, 0.0, 11.0, 15.0, 12.0).add(5.0, 1.0, 0.0, 11.0, 7.0, 12.0).forHorizontal(Direction.SOUTH);
    public static final VoxelShaper FLAP_DISPLAY = AllShapes.shape(0.0, 0.0, 3.0, 16.0, 16.0, 13.0).forHorizontal(Direction.SOUTH);
    public static final VoxelShaper DATA_GATHERER = AllShapes.shape(1.0, 0.0, 1.0, 15.0, 6.0, 15.0).add(3.0, 5.0, 3.0, 13.0, 9.0, 13.0).forDirectional();
    public static final VoxelShaper STOCK_LINK = AllShapes.shape(1.0, 0.0, 1.0, 15.0, 5.0, 15.0).forDirectional();
    public static final VoxelShaper STEAM_ENGINE = AllShapes.shape(1.0, 0.0, 1.0, 15.0, 3.0, 15.0).add(3.0, 0.0, 3.0, 13.0, 15.0, 13.0).add(1.0, 5.0, 4.0, 15.0, 13.0, 12.0).forHorizontalAxis();
    public static final VoxelShaper STEAM_ENGINE_CEILING = AllShapes.shape(1.0, 13.0, 1.0, 15.0, 16.0, 15.0).add(3.0, 1.0, 3.0, 13.0, 16.0, 13.0).add(1.0, 3.0, 4.0, 15.0, 11.0, 12.0).forHorizontalAxis();
    public static final VoxelShaper STEAM_ENGINE_WALL = AllShapes.shape(1.0, 1.0, 0.0, 15.0, 15.0, 3.0).add(3.0, 3.0, 0.0, 13.0, 13.0, 15.0).add(1.0, 4.0, 5.0, 15.0, 12.0, 13.0).forHorizontal(Direction.SOUTH);
    public static final VoxelShaper PLACARD = AllShapes.shape(2.0, 0.0, 2.0, 14.0, 3.0, 14.0).forDirectional(Direction.UP);
    public static final VoxelShaper FACTORY_PANEL_FALLBACK = AllShapes.shape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0).forDirectional(Direction.UP);
    public static final VoxelShaper CLIPBOARD_FLOOR = AllShapes.shape(3.0, 0.0, 1.0, 13.0, 1.0, 15.0).forHorizontal(Direction.SOUTH);
    public static final VoxelShaper CLIPBOARD_CEILING = AllShapes.shape(3.0, 15.0, 1.0, 13.0, 16.0, 15.0).forHorizontal(Direction.SOUTH);
    public static final VoxelShaper CLIPBOARD_WALL = AllShapes.shape(3.0, 1.0, 0.0, 13.0, 15.0, 1.0).forHorizontal(Direction.SOUTH);
    public static final VoxelShaper TRACK_ORTHO = AllShapes.shape(TrackVoxelShapes.orthogonal()).forHorizontal(Direction.NORTH);
    public static final VoxelShaper TRACK_ASC = AllShapes.shape(TrackVoxelShapes.ascending()).forHorizontal(Direction.SOUTH);
    public static final VoxelShaper TRACK_DIAG = AllShapes.shape(TrackVoxelShapes.diagonal()).forHorizontal(Direction.SOUTH);
    public static final VoxelShaper TRACK_ORTHO_LONG = AllShapes.shape(TrackVoxelShapes.longOrthogonalZOffset()).forHorizontal(Direction.SOUTH);
    public static final VoxelShaper DEPLOYER_INTERACTION = AllShapes.shape(CASING_12PX.get(Direction.UP)).add(SIX_VOXEL_POLE.get(Direction.Axis.Y)).forDirectional(Direction.UP);
    public static final VoxelShaper WHISTLE_BASE = AllShapes.shape(1.0, 0.0, 1.0, 15.0, 3.0, 15.0).add(5.0, 0.0, 5.0, 11.0, 8.0, 11.0).forDirectional(Direction.UP);
    public static final VoxelShaper DESK_BELL = AllShapes.shape(3.0, 0.0, 3.0, 13.0, 3.0, 13.0).add(4.0, 0.0, 4.0, 12.0, 9.0, 12.0).forDirectional(Direction.UP);
    public static final VoxelShaper ITEM_HATCH = AllShapes.shape(1.0, 0.0, 0.0, 15.0, 16.0, 2.0).add(2.0, 2.0, 0.0, 14.0, 13.0, 3.8).add(2.0, 4.0, 0.0, 14.0, 11.0, 5.8).add(2.0, 6.0, 0.0, 14.0, 9.0, 7.8).forHorizontal(Direction.SOUTH);
    public static final VoxelShaper POSTBOX = AllShapes.shape(2.0, 0.0, 0.0, 14.0, 14.0, 16.0).forHorizontal(Direction.SOUTH);
    private static final VoxelShape PISTON_HEAD = ((BlockState)((BlockState)Blocks.PISTON_HEAD.defaultBlockState().setValue((Property)DirectionalBlock.FACING, (Comparable)Direction.UP)).setValue((Property)PistonHeadBlock.SHORT, (Comparable)Boolean.valueOf(true))).getShape(null, null);
    private static final VoxelShape PISTON_EXTENDED = AllShapes.shape(CASING_12PX.get(Direction.UP)).add(FOUR_VOXEL_POLE.get(Direction.Axis.Y)).build();
    private static final VoxelShape SMALL_GEAR_SHAPE = AllShapes.cuboid(2.0, 6.0, 2.0, 14.0, 10.0, 14.0);
    private static final VoxelShape LARGE_GEAR_SHAPE = AllShapes.cuboid(0.0, 6.0, 0.0, 16.0, 10.0, 16.0);
    private static final VoxelShape VERTICAL_TABLET_SHAPE = AllShapes.cuboid(3.0, 1.0, -1.0, 13.0, 15.0, 3.0);
    private static final VoxelShape SQUARE_TABLET_SHAPE = AllShapes.cuboid(2.0, 2.0, -1.0, 14.0, 14.0, 3.0);
    private static final VoxelShape LOGISTICS_TABLE_SLOPE = AllShapes.shape(0.0, 10.0, 10.667, 16.0, 14.0, 15.0).add(0.0, 12.0, 6.333, 16.0, 16.0, 10.667).add(0.0, 14.0, 2.0, 16.0, 18.0, 6.333).build();
    private static final VoxelShape TANK_BOTTOM_LID = AllShapes.shape(0.0, 0.0, 0.0, 16.0, 4.0, 16.0).build();
    private static final VoxelShape TANK_TOP_LID = AllShapes.shape(0.0, 12.0, 0.0, 16.0, 16.0, 16.0).build();
    private static final VoxelShape WHISTLE_SMALL = AllShapes.shape(4.0, 3.0, 4.0, 12.0, 16.0, 12.0).build();
    private static final VoxelShape WHISTLE_MEDIUM = AllShapes.shape(3.0, 3.0, 3.0, 13.0, 16.0, 13.0).build();
    private static final VoxelShape WHISTLE_LARGE = AllShapes.shape(2.0, 3.0, 2.0, 14.0, 16.0, 14.0).build();
    public static final VoxelShape SCAFFOLD_HALF = AllShapes.shape(0.0, 8.0, 0.0, 16.0, 16.0, 16.0).build();
    public static final VoxelShape SCAFFOLD_FULL = AllShapes.shape(SCAFFOLD_HALF).add(0.0, 0.0, 0.0, 2.0, 16.0, 2.0).add(0.0, 0.0, 14.0, 2.0, 16.0, 16.0).add(14.0, 0.0, 0.0, 16.0, 16.0, 2.0).add(14.0, 0.0, 14.0, 16.0, 16.0, 16.0).build();
    public static final VoxelShape TRACK_CROSS = AllShapes.shape(TRACK_ORTHO.get(Direction.SOUTH)).add(TRACK_ORTHO.get(Direction.EAST)).build();
    public static final VoxelShape TRACK_CROSS_DIAG = AllShapes.shape(TRACK_DIAG.get(Direction.SOUTH)).add(TRACK_DIAG.get(Direction.EAST)).build();
    public static final VoxelShape TRACK_COLLISION = AllShapes.shape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0).build();
    public static final VoxelShape PACKAGE_PORT = AllShapes.shape(0.0, 0.0, 0.0, 16.0, 4.0, 16.0).add(2.0, 2.0, 2.0, 14.0, 14.0, 14.0).build();
    public static final VoxelShape TABLE_CLOTH = AllShapes.shape(-1.0, -9.0, -1.0, 17.0, 1.0, 17.0).build();
    public static final VoxelShape TABLE_CLOTH_OCCLUSION = AllShapes.shape(0.0, 0.0, 0.0, 16.0, 1.0, 16.0).build();
    public static final VoxelShape CHAIN_CONVEYOR_INTERACTION = AllShapes.shape(-10.0, 2.0, 0.0, 26.0, 14.0, 16.0).add(0.0, 2.0, -10.0, 16.0, 14.0, 26.0).add(-5.0, 2.0, -5.0, 21.0, 14.0, 21.0).add(Shapes.block()).build();
    public static final VoxelShape TRACK_FALLBACK = AllShapes.shape(0.0, 0.0, 0.0, 16.0, 4.0, 16.0).build();
    public static final VoxelShape BASIN_BLOCK_SHAPE = AllShapes.shape(0.0, 2.0, 0.0, 16.0, 16.0, 16.0).erase(2.0, 2.0, 2.0, 14.0, 16.0, 14.0).add(2.0, 0.0, 2.0, 14.0, 2.0, 14.0).build();
    public static final VoxelShape BASIN_RAYTRACE_SHAPE = AllShapes.shape(0.0, 2.0, 0.0, 16.0, 16.0, 16.0).add(2.0, 0.0, 2.0, 14.0, 2.0, 14.0).build();
    public static final VoxelShape BASIN_COLLISION_SHAPE = AllShapes.shape(0.0, 2.0, 0.0, 16.0, 13.0, 16.0).erase(2.0, 5.0, 2.0, 14.0, 16.0, 14.0).add(2.0, 0.0, 2.0, 14.0, 2.0, 14.0).build();
    public static final VoxelShape GIRDER_CROSS = AllShapes.shape(TEN_VOXEL_POLE.get(Direction.Axis.Y)).add(GIRDER_BEAM.get(Direction.Axis.X)).add(GIRDER_BEAM.get(Direction.Axis.Z)).build();
    public static final VoxelShape BACKTANK = AllShapes.shape(3.0, 0.0, 3.0, 13.0, 12.0, 13.0).add(SIX_VOXEL_POLE.get(Direction.Axis.Y)).build();
    public static final VoxelShape SPEED_CONTROLLER = AllShapes.shape(0.0, 0.0, 0.0, 16.0, 4.0, 16.0).add(1.0, 1.0, 1.0, 15.0, 13.0, 15.0).add(0.0, 8.0, 0.0, 16.0, 14.0, 16.0).build();
    public static final VoxelShape HEATER_BLOCK_SHAPE = AllShapes.shape(1.0, 0.0, 1.0, 15.0, 14.0, 15.0).build();
    public static final VoxelShape HEATER_BLOCK_SPECIAL_COLLISION_SHAPE = AllShapes.shape(0.0, 0.0, 0.0, 16.0, 4.0, 16.0).build();
    public static final VoxelShape CRUSHING_WHEEL_COLLISION_SHAPE = AllShapes.cuboid(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    public static final VoxelShape SEAT = AllShapes.cuboid(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
    public static final VoxelShape SEAT_COLLISION = AllShapes.cuboid(0.0, 0.0, 0.0, 16.0, 6.0, 16.0);
    public static final VoxelShape SEAT_COLLISION_PLAYERS = AllShapes.cuboid(0.0, 0.0, 0.0, 16.0, 3.0, 16.0);
    public static final VoxelShape MECHANICAL_PROCESSOR_SHAPE = AllShapes.shape(Shapes.block()).erase(4.0, 0.0, 4.0, 12.0, 16.0, 12.0).build();
    public static final VoxelShape TURNTABLE_SHAPE = AllShapes.shape(1.0, 4.0, 1.0, 15.0, 8.0, 15.0).add(5.0, 0.0, 5.0, 11.0, 4.0, 11.0).build();
    public static final VoxelShape CRATE_BLOCK_SHAPE = AllShapes.cuboid(1.0, 0.0, 1.0, 15.0, 14.0, 15.0);
    public static final VoxelShape TABLE_POLE_SHAPE = AllShapes.shape(4.0, 0.0, 4.0, 12.0, 2.0, 12.0).add(5.0, 2.0, 5.0, 11.0, 14.0, 11.0).build();
    public static final VoxelShape BELT_COLLISION_MASK = AllShapes.cuboid(0.0, 0.0, 0.0, 16.0, 19.0, 16.0);
    public static final VoxelShape SCHEMATICANNON_SHAPE = AllShapes.shape(1.0, 0.0, 1.0, 15.0, 8.0, 15.0).add(0.5, 8.0, 0.5, 15.5, 11.0, 15.5).build();
    public static final VoxelShape PULLEY_MAGNET = AllShapes.shape(3.0, -3.0, 3.0, 13.0, 2.0, 13.0).add(FOUR_VOXEL_POLE.get(Direction.UP)).build();
    public static final VoxelShape SPOUT = AllShapes.shape(1.0, 2.0, 1.0, 15.0, 14.0, 15.0).add(2.0, 0.0, 2.0, 14.0, 16.0, 14.0).build();
    public static final VoxelShape MILLSTONE = AllShapes.shape(0.0, 0.0, 0.0, 16.0, 6.0, 16.0).add(2.0, 6.0, 2.0, 14.0, 16.0, 14.0).build();
    public static final VoxelShape CUCKOO_CLOCK = AllShapes.shape(1.0, 0.0, 1.0, 15.0, 19.0, 15.0).build();
    public static final VoxelShape GAUGE_SHAPE_UP = AllShapes.shape(1.0, 0.0, 0.0, 15.0, 2.0, 16.0).add(2.0, 2.0, 1.0, 14.0, 14.0, 15.0).build();
    public static final VoxelShape MECHANICAL_ARM = AllShapes.shape(2.0, 0.0, 2.0, 14.0, 10.0, 14.0).add(3.0, 0.0, 3.0, 13.0, 14.0, 13.0).add(0.0, 0.0, 0.0, 16.0, 6.0, 16.0).build();
    public static final VoxelShape MECHANICAL_ARM_CEILING = AllShapes.shape(2.0, 6.0, 2.0, 14.0, 16.0, 14.0).add(3.0, 2.0, 3.0, 13.0, 16.0, 13.0).add(0.0, 10.0, 0.0, 16.0, 16.0, 16.0).build();
    public static final VoxelShape CHUTE = AllShapes.shape(1.0, 8.0, 1.0, 15.0, 16.0, 15.0).add(2.0, 0.0, 2.0, 14.0, 8.0, 14.0).build();
    public static final VoxelShape TANK = AllShapes.shape(1.0, 0.0, 1.0, 15.0, 16.0, 15.0).build();
    public static final VoxelShape TANK_TOP = AllShapes.shape(TANK_TOP_LID).add(TANK).build();
    public static final VoxelShape TANK_BOTTOM = AllShapes.shape(TANK_BOTTOM_LID).add(TANK).build();
    public static final VoxelShape TANK_TOP_BOTTOM = AllShapes.shape(TANK_BOTTOM_LID).add(TANK_TOP_LID).add(TANK).build();
    public static final VoxelShape FUNNEL_FLOOR = AllShapes.shape(2.0, -2.0, 2.0, 14.0, 8.0, 14.0).add(1.0, 1.0, 1.0, 15.0, 8.0, 15.0).add(0.0, 4.0, 0.0, 16.0, 10.0, 16.0).build();
    public static final VoxelShape FUNNEL_CEILING = AllShapes.shape(2.0, 8.0, 2.0, 14.0, 18.0, 14.0).add(1.0, 8.0, 1.0, 15.0, 15.0, 15.0).add(0.0, 6.0, 0.0, 16.0, 12.0, 16.0).build();
    public static final VoxelShape STATION = AllShapes.shape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0).add(1.0, 0.0, 1.0, 15.0, 13.0, 15.0).build();
    public static final VoxelShape STOCK_TICKER = AllShapes.shape(1.0, 0.0, 1.0, 15.0, 4.0, 15.0).add(2.0, 0.0, 2.0, 14.0, 16.0, 14.0).build();
    public static final VoxelShape WHISTLE_SMALL_FLOOR = AllShapes.shape(WHISTLE_SMALL).add(WHISTLE_BASE.get(Direction.UP)).build();
    public static final VoxelShape WHISTLE_MEDIUM_FLOOR = AllShapes.shape(WHISTLE_MEDIUM).add(WHISTLE_BASE.get(Direction.UP)).build();
    public static final VoxelShape WHISTLE_LARGE_FLOOR = AllShapes.shape(WHISTLE_LARGE).add(WHISTLE_BASE.get(Direction.UP)).build();
    public static final VoxelShape WHISTLE_EXTENDER_SMALL = AllShapes.shape(4.0, 0.0, 4.0, 12.0, 10.0, 12.0).build();
    public static final VoxelShape WHISTLE_EXTENDER_MEDIUM = AllShapes.shape(3.0, 0.0, 3.0, 13.0, 10.0, 13.0).build();
    public static final VoxelShape WHISTLE_EXTENDER_LARGE = AllShapes.shape(2.0, 0.0, 2.0, 14.0, 10.0, 14.0).build();
    public static final VoxelShape WHISTLE_EXTENDER_SMALL_DOUBLE = AllShapes.shape(4.0, 0.0, 4.0, 12.0, 18.0, 12.0).build();
    public static final VoxelShape WHISTLE_EXTENDER_MEDIUM_DOUBLE = AllShapes.shape(3.0, 0.0, 3.0, 13.0, 18.0, 13.0).build();
    public static final VoxelShape WHISTLE_EXTENDER_LARGE_DOUBLE = AllShapes.shape(2.0, 0.0, 2.0, 14.0, 18.0, 14.0).build();
    public static final VoxelShape WHISTLE_EXTENDER_SMALL_DOUBLE_CONNECTED = AllShapes.shape(4.0, 0.0, 4.0, 12.0, 16.0, 12.0).build();
    public static final VoxelShape WHISTLE_EXTENDER_MEDIUM_DOUBLE_CONNECTED = AllShapes.shape(3.0, 0.0, 3.0, 13.0, 16.0, 13.0).build();
    public static final VoxelShape WHISTLE_EXTENDER_LARGE_DOUBLE_CONNECTED = AllShapes.shape(2.0, 0.0, 2.0, 14.0, 16.0, 14.0).build();
    public static final VoxelShaper TRACK_CROSS_ORTHO_DIAG = AllShapes.shape(TRACK_DIAG.get(Direction.SOUTH)).add(TRACK_ORTHO.get(Direction.EAST)).forHorizontal(Direction.SOUTH);
    public static final VoxelShaper TRACK_CROSS_DIAG_ORTHO = AllShapes.shape(TRACK_DIAG.get(Direction.SOUTH)).add(TRACK_ORTHO.get(Direction.SOUTH)).forHorizontal(Direction.SOUTH);
    public static final VoxelShaper MECHANICAL_PISTON_HEAD = AllShapes.shape(PISTON_HEAD).forDirectional();
    public static final VoxelShaper MECHANICAL_PISTON = CASING_12PX;
    public static final VoxelShaper MECHANICAL_PISTON_EXTENDED = AllShapes.shape(PISTON_EXTENDED).forDirectional();
    public static final VoxelShaper SMALL_GEAR = AllShapes.shape(SMALL_GEAR_SHAPE).add(SIX_VOXEL_POLE.get(Direction.Axis.Y)).forAxis();
    public static final VoxelShaper LARGE_GEAR = AllShapes.shape(LARGE_GEAR_SHAPE).add(SIX_VOXEL_POLE.get(Direction.Axis.Y)).forAxis();
    public static final VoxelShaper LOGISTICAL_CONTROLLER = AllShapes.shape(SQUARE_TABLET_SHAPE).forDirectional(Direction.SOUTH);
    public static final VoxelShaper REDSTONE_BRIDGE = AllShapes.shape(VERTICAL_TABLET_SHAPE).forDirectional(Direction.SOUTH).withVerticalShapes(LOGISTICAL_CONTROLLER.get(Direction.UP));
    public static final VoxelShaper LOGISTICS_TABLE = AllShapes.shape(TABLE_POLE_SHAPE).add(LOGISTICS_TABLE_SLOPE).forHorizontal(Direction.SOUTH);
    public static final VoxelShaper SCHEMATICS_TABLE = AllShapes.shape(4.0, 0.0, 4.0, 12.0, 12.0, 12.0).add(0.0, 11.0, 2.0, 16.0, 14.0, 14.0).forDirectional(Direction.SOUTH);
    public static final VoxelShaper CHUTE_SLOPE = AllShapes.shape(ChuteShapes.createSlope()).forHorizontal(Direction.SOUTH);
    public static final VoxelShaper WHISTLE_SMALL_WALL = AllShapes.shape(WHISTLE_SMALL).add(WHISTLE_BASE.get(Direction.NORTH)).forHorizontal(Direction.SOUTH);
    public static final VoxelShaper WHISTLE_MEDIUM_WALL = AllShapes.shape(WHISTLE_MEDIUM).add(WHISTLE_BASE.get(Direction.NORTH)).forHorizontal(Direction.SOUTH);
    public static final VoxelShaper WHISTLE_LARGE_WALL = AllShapes.shape(WHISTLE_LARGE).add(WHISTLE_BASE.get(Direction.NORTH)).forHorizontal(Direction.SOUTH);

    private static Builder shape(VoxelShape shape) {
        return new Builder(shape);
    }

    private static Builder shape(double x1, double y1, double z1, double x2, double y2, double z2) {
        return AllShapes.shape(AllShapes.cuboid(x1, y1, z1, x2, y2, z2));
    }

    private static VoxelShape cuboid(double x1, double y1, double z1, double x2, double y2, double z2) {
        return Block.box((double)x1, (double)y1, (double)z1, (double)x2, (double)y2, (double)z2);
    }

    public static class Builder {
        private VoxelShape shape;

        public Builder(VoxelShape shape) {
            this.shape = shape;
        }

        public Builder add(VoxelShape shape) {
            this.shape = Shapes.or((VoxelShape)this.shape, (VoxelShape)shape);
            return this;
        }

        public Builder add(double x1, double y1, double z1, double x2, double y2, double z2) {
            return this.add(AllShapes.cuboid(x1, y1, z1, x2, y2, z2));
        }

        public Builder erase(double x1, double y1, double z1, double x2, double y2, double z2) {
            this.shape = Shapes.join((VoxelShape)this.shape, (VoxelShape)AllShapes.cuboid(x1, y1, z1, x2, y2, z2), (BooleanOp)BooleanOp.ONLY_FIRST);
            return this;
        }

        public VoxelShape build() {
            return this.shape;
        }

        public VoxelShaper build(BiFunction<VoxelShape, Direction, VoxelShaper> factory, Direction direction) {
            return factory.apply(this.shape, direction);
        }

        public VoxelShaper build(BiFunction<VoxelShape, Direction.Axis, VoxelShaper> factory, Direction.Axis axis) {
            return factory.apply(this.shape, axis);
        }

        public VoxelShaper forDirectional(Direction direction) {
            return this.build(VoxelShaper::forDirectional, direction);
        }

        public VoxelShaper forAxis() {
            return this.build(VoxelShaper::forAxis, Direction.Axis.Y);
        }

        public VoxelShaper forHorizontalAxis() {
            return this.build(VoxelShaper::forHorizontalAxis, Direction.Axis.Z);
        }

        public VoxelShaper forHorizontal(Direction direction) {
            return this.build(VoxelShaper::forHorizontal, direction);
        }

        public VoxelShaper forDirectional() {
            return this.forDirectional(Direction.UP);
        }
    }
}
