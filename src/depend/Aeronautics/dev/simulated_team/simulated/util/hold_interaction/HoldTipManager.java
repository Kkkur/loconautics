/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.CreateClient
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult
 */
package dev.simulated_team.simulated.util.hold_interaction;

import com.simibubi.create.CreateClient;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import dev.simulated_team.simulated.content.blocks.behaviour.HoldTipBehaviour;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class HoldTipManager {
    public static void tick() {
        Minecraft mc = Minecraft.getInstance();
        HitResult target = mc.hitResult;
        if (target == null || !(target instanceof BlockHitResult)) {
            return;
        }
        BlockHitResult result = (BlockHitResult)target;
        ClientLevel world = mc.level;
        BlockPos pos = result.getBlockPos();
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof SmartBlockEntity)) {
            return;
        }
        SmartBlockEntity sbe = (SmartBlockEntity)blockEntity;
        for (BlockEntityBehaviour blockEntityBehaviour : sbe.getAllBehaviours()) {
            HoldTipBehaviour behaviour;
            MutableComponent hoverTip;
            if (!(blockEntityBehaviour instanceof HoldTipBehaviour) || (hoverTip = (behaviour = (HoldTipBehaviour)blockEntityBehaviour).getHoverTip((Player)mc.player, pos, sbe.getBlockState())) == null) continue;
            ArrayList<MutableComponent> tip = new ArrayList<MutableComponent>();
            tip.add(Component.literal((String)""));
            tip.add(hoverTip);
            CreateClient.VALUE_SETTINGS_HANDLER.showHoverTip(tip);
        }
    }
}
