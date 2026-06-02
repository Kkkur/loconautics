/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour$ValueSettings
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter
 *  com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour
 *  com.simibubi.create.foundation.utility.CreateLang
 *  net.minecraft.ChatFormatting
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.phys.BlockHitResult
 */
package dev.simulated_team.simulated.content.blocks.steering_wheel;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import dev.simulated_team.simulated.content.blocks.steering_wheel.SteeringWheelBlockEntity;
import dev.simulated_team.simulated.data.SimLang;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

private static class SteeringWheelBlockEntity.SteeringWheelScrollValueBehaviour
extends ScrollValueBehaviour {
    public SteeringWheelBlockEntity.SteeringWheelScrollValueBehaviour(SmartBlockEntity be) {
        super((Component)SimLang.translate("torsion_spring.angle_limit", new Object[0]).component(), be, (ValueBoxTransform)new SteeringWheelBlockEntity.SteeringWheelValueBoxTransform());
        this.withFormatter(v -> Math.abs(v) + CreateLang.translateDirect((String)"generic.unit.degrees", (Object[])new Object[0]).getString());
    }

    public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
        return new ValueSettingsBoard(this.label, 360, 45, (List)ImmutableList.of((Object)Component.literal((String)"\u27f3").withStyle(ChatFormatting.BOLD)), new ValueSettingsFormatter(this::formatValue));
    }

    public MutableComponent formatValue(ValueSettingsBehaviour.ValueSettings settings) {
        return SimLang.number(Math.max(1, Math.abs(settings.value()))).add(CreateLang.translateDirect((String)"generic.unit.degrees", (Object[])new Object[0])).component();
    }
}
