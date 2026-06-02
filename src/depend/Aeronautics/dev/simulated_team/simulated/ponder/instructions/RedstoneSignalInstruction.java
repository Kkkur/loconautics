/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.redstone.analogLever.AnalogLeverBlockEntity
 *  com.simibubi.create.content.redstone.nixieTube.NixieTubeBlockEntity
 *  net.createmod.ponder.api.level.PonderLevel
 *  net.createmod.ponder.api.scene.Selection
 *  net.createmod.ponder.foundation.PonderScene
 *  net.createmod.ponder.foundation.instruction.WorldModifyInstruction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.RedstoneTorchBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 */
package dev.simulated_team.simulated.ponder.instructions;

import com.simibubi.create.content.redstone.analogLever.AnalogLeverBlockEntity;
import com.simibubi.create.content.redstone.nixieTube.NixieTubeBlockEntity;
import net.createmod.ponder.api.level.PonderLevel;
import net.createmod.ponder.api.scene.Selection;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.instruction.WorldModifyInstruction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedstoneTorchBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;

public class RedstoneSignalInstruction
extends WorldModifyInstruction {
    protected final int signal;

    public RedstoneSignalInstruction(Selection selection, int signal) {
        super(selection);
        this.signal = signal;
    }

    protected void runModification(Selection selection, PonderScene scene) {
        PonderLevel level = scene.getWorld();
        selection.forEach(pos -> {
            if (!level.getBounds().isInside((Vec3i)pos)) {
                return;
            }
            BlockEntity BE = level.getBlockEntity(pos);
            if (BE instanceof NixieTubeBlockEntity) {
                NixieTubeBlockEntity nixie = (NixieTubeBlockEntity)BE;
                nixie.updateRedstoneStrength(this.signal);
                nixie.updateDisplayedStrings();
            }
            if (BE instanceof AnalogLeverBlockEntity) {
                AnalogLeverBlockEntity lever = (AnalogLeverBlockEntity)BE;
                CompoundTag tag = new CompoundTag();
                lever.write(tag, (HolderLookup.Provider)level.registryAccess(), false);
                tag.putInt("State", this.signal);
                lever.readClient(tag, (HolderLookup.Provider)level.registryAccess());
            }
            BlockState state = level.getBlockState(pos);
            BlockState newState = null;
            if (state == Blocks.AIR.defaultBlockState()) {
                return;
            }
            if (state.hasProperty((Property)BlockStateProperties.POWER)) {
                newState = (BlockState)state.setValue((Property)BlockStateProperties.POWER, (Comparable)Integer.valueOf(this.signal));
            }
            if (state.hasProperty((Property)BlockStateProperties.POWERED)) {
                newState = (BlockState)state.setValue((Property)BlockStateProperties.POWERED, (Comparable)Boolean.valueOf(this.signal > 0));
            }
            if (state.hasProperty((Property)RedstoneTorchBlock.LIT)) {
                newState = (BlockState)state.setValue((Property)RedstoneTorchBlock.LIT, (Comparable)Boolean.valueOf(this.signal > 0));
            }
            if (newState == null) {
                return;
            }
            level.setBlockAndUpdate(pos, newState);
        });
    }

    protected boolean needsRedraw() {
        return true;
    }
}
