/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.AllBlocks
 *  com.simibubi.create.content.contraptions.AbstractContraptionEntity
 *  com.simibubi.create.content.redstone.contact.RedstoneContactBlock
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate$StructureBlockInfo
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.ryanhcode.sable.neoforge.mixinhelper.compatibility.create.redstone_contact;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.redstone.contact.RedstoneContactBlock;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.sublevel.SubLevel;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class RedstoneContactBlockEntity
extends SmartBlockEntity {
    public static final double CONTRAPTION_CHECK_BOUNDS = 1.0;

    public RedstoneContactBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    public void tick() {
        super.tick();
        if (!this.isRemoved() && this.getLevel() != null) {
            boolean found;
            SubLevel parentSublevel = Sable.HELPER.getContaining((BlockEntity)this);
            Direction facing = (Direction)this.getBlockState().getValue((Property)RedstoneContactBlock.FACING);
            Vector3d facingDir = JOMLConversion.atLowerCornerOf((Vec3i)facing.getNormal());
            if (parentSublevel != null) {
                parentSublevel.logicalPose().transformNormal(facingDir);
            }
            Vector3d frontWorldPosition = JOMLConversion.atCenterOf((Vec3i)this.getBlockPos().relative(facing));
            if (parentSublevel != null) {
                parentSublevel.logicalPose().transformPosition(frontWorldPosition);
            }
            boolean bl = found = this.checkForContactsInWorldOrSubLevel(frontWorldPosition, facing, parentSublevel, facingDir) || this.checkForContactsInContraption(frontWorldPosition, facingDir);
            if (found != (Boolean)this.getBlockState().getValue((Property)RedstoneContactBlock.POWERED)) {
                if (found) {
                    this.getLevel().setBlockAndUpdate(this.getBlockPos(), (BlockState)this.getBlockState().setValue((Property)RedstoneContactBlock.POWERED, (Comparable)Boolean.valueOf(true)));
                } else {
                    this.getLevel().setBlockAndUpdate(this.getBlockPos(), (BlockState)this.getBlockState().setValue((Property)RedstoneContactBlock.POWERED, (Comparable)Boolean.valueOf(false)));
                }
            }
        }
    }

    private boolean checkForContactsInContraption(Vector3d frontWorldPosition, Vector3d facingDir) {
        Vec3 frontMoj = JOMLConversion.toMojang((Vector3dc)frontWorldPosition);
        Vec3 min = frontMoj.subtract(0.5, 0.5, 0.5);
        Vec3 max = min.add(1.0, 1.0, 1.0);
        AABB searchBounds = new AABB(min, max);
        List contraptions = this.getLevel().getEntitiesOfClass(AbstractContraptionEntity.class, searchBounds);
        for (AbstractContraptionEntity ace : contraptions) {
            BlockState otherState;
            Vec3 contactLocalPos = ace.toLocalVector(frontMoj, 1.0f);
            StructureTemplate.StructureBlockInfo candidateBlock = (StructureTemplate.StructureBlockInfo)ace.getContraption().getBlocks().get(BlockPos.containing((Position)contactLocalPos));
            if (candidateBlock == null || !AllBlocks.REDSTONE_CONTACT.has(otherState = candidateBlock.state()) && !AllBlocks.ELEVATOR_CONTACT.has(otherState)) continue;
            Direction otherFacingDirection = (Direction)otherState.getValue((Property)RedstoneContactBlock.FACING);
            Vec3 otherFacingMoj = Vec3.atLowerCornerOf((Vec3i)otherFacingDirection.getNormal());
            Vector3d otherFacing = JOMLConversion.toJOML((Position)(otherFacingMoj = ace.applyRotation(otherFacingMoj, 1.0f)));
            if (!(facingDir.dot((Vector3dc)otherFacing) < -0.95)) continue;
            return true;
        }
        return false;
    }

    private boolean checkForContactsInWorldOrSubLevel(Vector3d frontWorldPosition, Direction facing, SubLevel parentSublevel, Vector3d facingDir) {
        return Sable.HELPER.findIncludingSubLevels(this.getLevel(), this.getBlockPos().getCenter().relative(facing, 1.0), true, parentSublevel, (subLevel, pos) -> {
            Vector3d localFacingDir;
            Vector3d localFrontWorldPosition;
            if (subLevel != null && this.checkForContactsInContraption(localFrontWorldPosition = subLevel.logicalPose().transformPositionInverse((Vector3dc)frontWorldPosition, new Vector3d()), localFacingDir = subLevel.logicalPose().transformNormalInverse((Vector3dc)facingDir, new Vector3d()))) {
                return true;
            }
            BlockState otherState = this.getLevel().getBlockState(pos);
            if (!AllBlocks.REDSTONE_CONTACT.has(otherState) && !AllBlocks.ELEVATOR_CONTACT.has(otherState)) {
                return false;
            }
            Direction otherFacing = (Direction)otherState.getValue((Property)RedstoneContactBlock.FACING);
            Vector3d otherFacingDir = JOMLConversion.atLowerCornerOf((Vec3i)otherFacing.getNormal());
            if (subLevel != null) {
                subLevel.logicalPose().transformNormal(otherFacingDir);
            }
            return facingDir.dot((Vector3dc)otherFacingDir) < -0.99;
        });
    }
}
