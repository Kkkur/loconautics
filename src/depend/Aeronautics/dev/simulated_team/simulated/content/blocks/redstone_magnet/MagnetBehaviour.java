/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType
 *  com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.SectionPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.phys.Vec3
 */
package dev.simulated_team.simulated.content.blocks.redstone_magnet;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import dev.simulated_team.simulated.content.blocks.redstone_magnet.MagnetMap;
import dev.simulated_team.simulated.util.SimMovementContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;

public class MagnetBehaviour
extends BlockEntityBehaviour {
    public static final BehaviourType<MagnetBehaviour> TYPE = new BehaviourType();
    private SectionPos currentSection;
    private final MagnetMap<?> map;

    public MagnetBehaviour(SmartBlockEntity te, MagnetMap<?> map) {
        super(te);
        this.map = map;
    }

    public BehaviourType<?> getType() {
        return TYPE;
    }

    public void initialize() {
        super.initialize();
        if (this.getWorld().isClientSide) {
            return;
        }
        this.currentSection = this.getCurrentSection();
        this.map.addMagnet((LevelAccessor)this.blockEntity.getLevel(), this.currentSection, this.blockEntity.getBlockPos());
    }

    public void tick() {
        super.tick();
        if (this.getWorld().isClientSide) {
            return;
        }
        SimMovementContext context = SimMovementContext.getMovementContext(this.getWorld(), Vec3.atCenterOf((Vec3i)this.blockEntity.getBlockPos()));
        SectionPos newSection = SectionPos.of((Position)context.globalPosition());
        if (newSection.x() != this.currentSection.x() || newSection.y() != this.currentSection.y() || newSection.z() != this.currentSection.z()) {
            this.map.removeMagnet((LevelAccessor)this.blockEntity.getLevel(), this.currentSection, this.blockEntity.getBlockPos());
            this.currentSection = newSection;
            this.map.addMagnet((LevelAccessor)this.blockEntity.getLevel(), this.currentSection, this.blockEntity.getBlockPos());
        }
    }

    public void unload() {
        super.unload();
        if (this.getWorld().isClientSide) {
            return;
        }
        this.map.removeMagnet((LevelAccessor)this.blockEntity.getLevel(), this.currentSection, this.blockEntity.getBlockPos());
    }

    private SectionPos getCurrentSection() {
        SimMovementContext context = SimMovementContext.getMovementContext(this.blockEntity.getLevel(), Vec3.atCenterOf((Vec3i)this.blockEntity.getBlockPos()));
        return SectionPos.of((BlockPos)BlockPos.containing((Position)context.globalPosition()));
    }
}
