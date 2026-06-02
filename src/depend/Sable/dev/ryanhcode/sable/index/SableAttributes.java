/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  net.minecraft.core.Holder
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.ai.attributes.Attribute
 *  net.minecraft.world.entity.ai.attributes.AttributeSupplier
 *  net.minecraft.world.entity.ai.attributes.DefaultAttributes
 *  net.minecraft.world.entity.ai.attributes.RangedAttribute
 */
package dev.ryanhcode.sable.index;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Objects;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

public class SableAttributes {
    public static final String PUNCH_STRENGTH_NAME = "player.sub_level_punch_strength";
    public static final Attribute PUNCH_STRENGTH_ATTRIBUTE = new RangedAttribute("attribute.name.player.sub_level_punch_strength", 1.0, -100.0, 100.0).setSyncable(true);
    public static final String PUNCH_COOLDOWN_NAME = "player.sub_level_punch_cooldown";
    public static final Attribute PUNCH_COOLDOWN_ATTRIBUTE = new RangedAttribute("attribute.name.player.sub_level_punch_cooldown", 0.0, 0.0, 10.0).setSyncable(true);
    public static Holder<Attribute> PUNCH_STRENGTH;
    public static Holder<Attribute> PUNCH_COOLDOWN;

    public static void register() {
        AttributeSupplier supplier = DefaultAttributes.getSupplier((EntityType)EntityType.PLAYER);
        Map additionalInstances = AttributeSupplier.builder().add(SableAttributes.PUNCH_STRENGTH).add(SableAttributes.PUNCH_COOLDOWN).build().instances;
        supplier.instances = ImmutableMap.builder().putAll(supplier.instances).putAll(additionalInstances).buildKeepingLast();
    }

    public static int getPushCooldownTicks(LivingEntity entity) {
        return Mth.ceil((double)(Objects.requireNonNull(entity.getAttribute(PUNCH_COOLDOWN)).getValue() * 20.0));
    }
}
