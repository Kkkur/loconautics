/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntity
 *  com.simibubi.create.content.kinetics.belt.BeltBlockEntity
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  org.spongepowered.asm.mixin.Mixin
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.belt;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value={BeltBlockEntity.class})
public abstract class BeltBlockEntityMixin
extends KineticBlockEntity {
    public BeltBlockEntityMixin(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    public void onSpeedChanged(float previousSpeed) {
        super.onSpeedChanged(previousSpeed);
        SubLevelContainer container = SubLevelContainer.getContainer(this.level);
        if (container instanceof ServerSubLevelContainer) {
            ServerSubLevelContainer serverSubLevelContainer = (ServerSubLevelContainer)container;
            SubLevelPhysicsSystem physicsSystem = serverSubLevelContainer.physicsSystem();
            BlockPos blockPos = this.getBlockPos();
            physicsSystem.wakeUpObjectsAt(blockPos.getX(), blockPos.getY(), blockPos.getZ());
            SubLevel subLevel = Sable.HELPER.getContaining((BlockEntity)this);
            if (subLevel instanceof ServerSubLevel) {
                ServerSubLevel serverSubLevel = (ServerSubLevel)subLevel;
                physicsSystem.getPipeline().wakeUp(serverSubLevel);
            }
        }
    }
}
