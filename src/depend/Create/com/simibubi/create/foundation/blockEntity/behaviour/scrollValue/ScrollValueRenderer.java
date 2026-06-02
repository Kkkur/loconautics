/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.outliner.Outline
 *  net.createmod.catnip.outliner.Outliner
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.foundation.blockEntity.behaviour.scrollValue;

import com.simibubi.create.AllItems;
import com.simibubi.create.AllKeys;
import com.simibubi.create.CreateClient;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBox;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.BulkScrollValueBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.ArrayList;
import net.createmod.catnip.outliner.Outline;
import net.createmod.catnip.outliner.Outliner;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class ScrollValueRenderer {
    /*
     * Could not resolve type clashes
     * Unable to fully structure code
     */
    public static void tick() {
        mc = Minecraft.getInstance();
        target = mc.hitResult;
        if (target == null || !(target instanceof BlockHitResult)) {
            return;
        }
        result = (BlockHitResult)target;
        world = mc.level;
        pos = result.getBlockPos();
        face = result.getDirection();
        highlightFound = false;
        var8_7 = world.getBlockEntity(pos);
        if (!(var8_7 instanceof SmartBlockEntity)) {
            return;
        }
        sbe = (SmartBlockEntity)var8_7;
        for (BlockEntityBehaviour blockEntityBehaviour : sbe.getAllBehaviours()) {
            if (!(blockEntityBehaviour instanceof ScrollValueBehaviour)) continue;
            behaviour = (ScrollValueBehaviour)blockEntityBehaviour;
            if (!behaviour.isActive()) {
                Outliner.getInstance().remove((Object)behaviour);
                continue;
            }
            mainhandItem = mc.player.getItemInHand(InteractionHand.MAIN_HAND);
            clipboard = behaviour.bypassesInput(mainhandItem);
            if (behaviour.needsWrench && !AllItems.WRENCH.isIn(mainhandItem) && !clipboard) continue;
            v0 = highlight = behaviour.testHit(target.getLocation()) != false && clipboard == false && highlightFound == false;
            if (!(behaviour instanceof BulkScrollValueBehaviour)) ** GOTO lbl-1000
            bulkScrolling = (BulkScrollValueBehaviour)behaviour;
            if (AllKeys.ctrlDown()) {
                for (SmartBlockEntity smartBlockEntity : bulkScrolling.getBulk()) {
                    other = smartBlockEntity.getBehaviour(ScrollValueBehaviour.TYPE);
                    if (other == null) continue;
                    ScrollValueRenderer.addBox(world, smartBlockEntity.getBlockPos(), face, other, highlight);
                }
            } else lbl-1000:
            // 2 sources

            {
                ScrollValueRenderer.addBox(world, pos, face, behaviour, highlight);
            }
            if (!highlight) continue;
            highlightFound = true;
            tip = new ArrayList<MutableComponent>();
            tip.add(behaviour.label.copy());
            tip.add(CreateLang.translateDirect("gui.value_settings.hold_to_edit", new Object[0]));
            CreateClient.VALUE_SETTINGS_HANDLER.showHoverTip(tip);
        }
    }

    protected static void addBox(ClientLevel world, BlockPos pos, Direction face, ScrollValueBehaviour behaviour, boolean highlight) {
        AABB bb = new AABB(Vec3.ZERO, Vec3.ZERO).inflate(0.5).contract(0.0, 0.0, -0.5).move(0.0, 0.0, -0.125);
        Component label = behaviour.label;
        ValueBox box = behaviour instanceof ScrollOptionBehaviour ? new ValueBox.IconValueBox(label, ((ScrollOptionBehaviour)behaviour).getIconForSelected(), bb, pos) : new ValueBox.TextValueBox(label, bb, pos, (Component)Component.literal((String)behaviour.formatValue()));
        box.passive(!highlight).wideOutline();
        Outliner.getInstance().showOutline((Object)behaviour, (Outline)box.transform(behaviour.slotPositioning)).highlightFace(face);
    }
}
