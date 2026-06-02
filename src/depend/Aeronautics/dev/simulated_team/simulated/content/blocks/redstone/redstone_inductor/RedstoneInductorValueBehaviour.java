/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.redstone.diodes.BrassDiodeScrollValueBehaviour
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour$ValueSettings
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard
 *  net.minecraft.core.Direction
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 */
package dev.simulated_team.simulated.content.blocks.redstone.redstone_inductor;

import com.simibubi.create.content.redstone.diodes.BrassDiodeScrollValueBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import dev.simulated_team.simulated.content.blocks.redstone.redstone_inductor.RedstoneInductorBlock;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;

public class RedstoneInductorValueBehaviour
extends BrassDiodeScrollValueBehaviour {
    public RedstoneInductorValueBehaviour(Component label, SmartBlockEntity be, ValueBoxTransform slot) {
        super(label, be, slot);
    }

    public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
        return super.createBoard(player, hitResult);
    }

    public void onShortInteract(Player player, InteractionHand hand, Direction side, BlockHitResult hitResult) {
        BlockState blockState = this.blockEntity.getBlockState();
        Block block = blockState.getBlock();
        if (block instanceof RedstoneInductorBlock) {
            RedstoneInductorBlock bdb = (RedstoneInductorBlock)block;
            bdb.toggle(this.getWorld(), this.getPos(), blockState, player, hand);
        }
    }

    public void setValueSettings(Player player, ValueSettingsBehaviour.ValueSettings valueSetting, boolean ctrlHeld) {
        int multiplier;
        int value = valueSetting.value();
        switch (valueSetting.row()) {
            case 0: {
                int n = 1;
                break;
            }
            case 1: {
                int n = 20;
                break;
            }
            default: {
                int n = multiplier = 1200;
            }
        }
        if (!valueSetting.equals((Object)this.getValueSettings())) {
            this.playFeedbackSound((BlockEntityBehaviour)this);
        }
        int clampingValue = valueSetting.row() == 0 ? 0 : 1;
        this.setValue(Math.max(clampingValue, Math.max(clampingValue, value) * multiplier));
    }

    public MutableComponent formatSettings(ValueSettingsBehaviour.ValueSettings settings) {
        BlockState blockState = this.blockEntity.getBlockState();
        Boolean inverted = (Boolean)blockState.getValue((Property)RedstoneInductorBlock.INVERTED);
        int row = settings.row();
        int column = settings.value();
        if (row == 0 && column == 0) {
            return Component.translatable((String)("block.simulated.redstone_inductor." + (inverted != false ? "invert" : "copy")));
        }
        return Component.literal((String)(switch (settings.row()) {
            case 1 -> "0:" + (column < 10 ? "0" : "") + column;
            case 2 -> column + ":00";
            default -> column + "t";
        }));
    }
}
