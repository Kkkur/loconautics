/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType
 *  com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.block.state.BlockState
 *  org.jetbrains.annotations.Nullable
 */
package dev.simulated_team.simulated.content.blocks.behaviour;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class HoldTipBehaviour
extends BlockEntityBehaviour {
    public static final BehaviourType<HoldTipBehaviour> TYPE = new BehaviourType();
    private HoldTipGetter hoverTip;

    public HoldTipBehaviour(SmartBlockEntity be, MutableComponent hoverTip) {
        super(be);
        this.setHoverTip(hoverTip);
    }

    public HoldTipBehaviour(SmartBlockEntity be, HoldTipGetter hoverTip) {
        super(be);
        this.setHoverTip(hoverTip);
    }

    public void setHoverTip(MutableComponent hoverTip) {
        this.hoverTip = (player, pos, state) -> hoverTip;
    }

    public void setHoverTip(HoldTipGetter hoverTip) {
        this.hoverTip = hoverTip;
    }

    public MutableComponent getHoverTip(Player player, BlockPos pos, BlockState state) {
        return this.hoverTip.getTip(player, pos, state);
    }

    public BehaviourType<?> getType() {
        return TYPE;
    }

    @FunctionalInterface
    public static interface HoldTipGetter {
        @Nullable
        public MutableComponent getTip(Player var1, BlockPos var2, BlockState var3);
    }
}
