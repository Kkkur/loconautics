/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.alchemy.Potion
 *  net.minecraft.world.item.alchemy.PotionBrewing
 *  net.minecraft.world.item.alchemy.PotionBrewing$Mix
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 *  org.spongepowered.asm.mixin.gen.Invoker
 */
package com.simibubi.create.foundation.mixin.accessor;

import java.util.List;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value={PotionBrewing.class})
public interface PotionBrewingAccessor {
    @Accessor(value="potionMixes")
    public List<PotionBrewing.Mix<Potion>> create$getPotionMixes();

    @Accessor(value="containerMixes")
    public List<PotionBrewing.Mix<Item>> create$getContainerMixes();

    @Invoker(value="isContainer")
    public boolean create$isContainer(ItemStack var1);
}
