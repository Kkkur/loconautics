/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.contraptions.actors.AttachedActorBlock
 *  com.simibubi.create.content.contraptions.actors.harvester.HarvesterBlock
 *  com.simibubi.create.content.contraptions.actors.harvester.HarvesterBlockEntity
 *  com.simibubi.create.foundation.block.IBE
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityTicker
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  org.spongepowered.asm.mixin.Mixin
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.behaviour_compatibility.harvester_block_entity;

import com.simibubi.create.content.contraptions.actors.AttachedActorBlock;
import com.simibubi.create.content.contraptions.actors.harvester.HarvesterBlock;
import com.simibubi.create.content.contraptions.actors.harvester.HarvesterBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import dev.ryanhcode.sable.neoforge.mixinhelper.compatibility.create.harvester.HarvesterTicker;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value={HarvesterBlock.class})
public abstract class HarvesterBlockMixin
extends AttachedActorBlock
implements IBE<HarvesterBlockEntity> {
    protected HarvesterBlockMixin(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public <S extends BlockEntity> BlockEntityTicker<S> getTicker(Level level, BlockState state, BlockEntityType<S> blockEntityType) {
        if (level.isClientSide) {
            return new HarvesterTicker();
        }
        return null;
    }
}
