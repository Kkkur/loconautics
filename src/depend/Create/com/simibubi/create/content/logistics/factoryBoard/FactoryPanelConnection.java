/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.logistics.factoryBoard;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelPosition;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class FactoryPanelConnection {
    public static final Codec<FactoryPanelConnection> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)FactoryPanelPosition.CODEC.fieldOf("position").forGetter(i -> i.from), (App)Codec.INT.fieldOf("amount").forGetter(i -> i.amount), (App)Codec.INT.fieldOf("arrow_bending").forGetter(i -> i.arrowBendMode)).apply((Applicative)instance, FactoryPanelConnection::new));
    public FactoryPanelPosition from;
    public int amount;
    public List<Direction> path;
    public int arrowBendMode;
    public boolean success;
    public WeakReference<Object> cachedSource;
    private int arrowBendModeCurrentPathUses;

    public FactoryPanelConnection(FactoryPanelPosition from, int amount) {
        this(from, amount, -1);
    }

    public FactoryPanelConnection(FactoryPanelPosition from, int amount, int arrowBendMode) {
        this.from = from;
        this.amount = amount;
        this.arrowBendMode = arrowBendMode;
        this.path = new ArrayList<Direction>();
        this.success = true;
        this.arrowBendModeCurrentPathUses = 0;
        this.cachedSource = new WeakReference<Object>(null);
    }

    public List<Direction> getPath(Level level, BlockState state, FactoryPanelPosition to) {
        if (!this.path.isEmpty() && this.arrowBendModeCurrentPathUses == this.arrowBendMode) {
            return this.path;
        }
        boolean findSuitable = this.arrowBendMode == -1;
        this.arrowBendModeCurrentPathUses = this.arrowBendMode;
        FactoryPanelBehaviour fromBehaviour = FactoryPanelBehaviour.at((BlockAndTintGetter)level, to);
        Vec3 diff = this.calculatePathDiff(state, to);
        Vec3 start = fromBehaviour != null ? fromBehaviour.getSlotPositioning().getLocalOffset((LevelAccessor)level, to.pos(), state).add(Vec3.atLowerCornerOf((Vec3i)to.pos())) : Vec3.ZERO;
        float xRot = 57.295776f * FactoryPanelBlock.getXRot(state);
        float yRot = 57.295776f * FactoryPanelBlock.getYRot(state);
        block0: for (int actualMode = 0; actualMode <= 4; ++actualMode) {
            this.path.clear();
            if (!findSuitable && actualMode != this.arrowBendMode) continue;
            boolean desperateOption = actualMode == 4;
            BlockPos toTravelFirst = BlockPos.ZERO;
            BlockPos toTravelLast = BlockPos.containing((Position)diff.scale(2.0).add(0.1, 0.1, 0.1));
            if (actualMode > 1) {
                boolean flipX = diff.x > 0.0 ^ actualMode % 2 == 1;
                boolean flipZ = diff.z > 0.0 ^ actualMode % 2 == 0;
                int ceilX = Mth.positiveCeilDiv((int)toTravelLast.getX(), (int)2);
                int ceilZ = Mth.positiveCeilDiv((int)toTravelLast.getZ(), (int)2);
                int floorZ = Mth.floorDiv((int)toTravelLast.getZ(), (int)2);
                int floorX = Mth.floorDiv((int)toTravelLast.getX(), (int)2);
                toTravelFirst = new BlockPos(flipX ? floorX : ceilX, 0, flipZ ? floorZ : ceilZ);
                toTravelLast = new BlockPos(!flipX ? floorX : ceilX, 0, !flipZ ? floorZ : ceilZ);
            }
            Direction lastDirection = null;
            Direction currentDirection = null;
            for (BlockPos toTravel : List.of(toTravelFirst, toTravelLast)) {
                boolean zIsFarther;
                boolean bl = zIsFarther = Math.abs(toTravel.getZ()) > Math.abs(toTravel.getX());
                boolean zIsPreferred = desperateOption ? zIsFarther : actualMode % 2 == 1;
                List<Direction> directionOrder = zIsPreferred ? List.of(Direction.SOUTH, Direction.NORTH, Direction.WEST, Direction.EAST) : List.of(Direction.WEST, Direction.EAST, Direction.SOUTH, Direction.NORTH);
                for (int i = 0; i < 100 && !toTravel.equals((Object)BlockPos.ZERO); ++i) {
                    for (Direction d : directionOrder) {
                        if (lastDirection != null && d == lastDirection.getOpposite() || currentDirection != null && toTravel.relative(d).distManhattan((Vec3i)BlockPos.ZERO) >= toTravel.relative(currentDirection).distManhattan((Vec3i)BlockPos.ZERO)) continue;
                        currentDirection = d;
                    }
                    lastDirection = currentDirection;
                    toTravel = toTravel.relative(currentDirection);
                    this.path.add(currentDirection);
                }
            }
            if (!findSuitable || desperateOption) break;
            BlockPos travelled = BlockPos.ZERO;
            for (int i = 0; i < this.path.size() - 1; ++i) {
                Direction d = this.path.get(i);
                travelled = travelled.relative(d);
                Vec3 testOffset = Vec3.atLowerCornerOf((Vec3i)travelled).scale(0.5);
                testOffset = VecHelper.rotate((Vec3)testOffset, (double)180.0, (Direction.Axis)Direction.Axis.Y);
                testOffset = VecHelper.rotate((Vec3)testOffset, (double)(xRot + 90.0f), (Direction.Axis)Direction.Axis.X);
                Vec3 v = start.add(testOffset = VecHelper.rotate((Vec3)testOffset, (double)yRot, (Direction.Axis)Direction.Axis.Y));
                if (!level.noCollision(new AABB(v, v).inflate(0.0078125))) continue block0;
            }
        }
        return this.path;
    }

    public Vec3 calculatePathDiff(BlockState state, FactoryPanelPosition to) {
        float xRot = 57.295776f * FactoryPanelBlock.getXRot(state);
        float yRot = 57.295776f * FactoryPanelBlock.getYRot(state);
        int slotDiffx = to.slot().xOffset - this.from.slot().xOffset;
        int slotDiffY = to.slot().yOffset - this.from.slot().yOffset;
        Vec3 diff = Vec3.atLowerCornerOf((Vec3i)to.pos().subtract((Vec3i)this.from.pos()));
        diff = VecHelper.rotate((Vec3)diff, (double)(-yRot), (Direction.Axis)Direction.Axis.Y);
        diff = VecHelper.rotate((Vec3)diff, (double)(-xRot - 90.0f), (Direction.Axis)Direction.Axis.X);
        diff = VecHelper.rotate((Vec3)diff, (double)-180.0, (Direction.Axis)Direction.Axis.Y);
        diff = diff.add((double)slotDiffx * 0.5, 0.0, (double)slotDiffY * 0.5);
        diff = diff.multiply(1.0, 0.0, 1.0);
        return diff;
    }
}
