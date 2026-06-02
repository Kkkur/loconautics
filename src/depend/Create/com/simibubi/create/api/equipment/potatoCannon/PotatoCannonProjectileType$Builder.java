/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.core.HolderSet
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 */
package com.simibubi.create.api.equipment.potatoCannon;

import com.simibubi.create.api.equipment.potatoCannon.PotatoCannonProjectileType;
import com.simibubi.create.api.equipment.potatoCannon.PotatoProjectileBlockHitAction;
import com.simibubi.create.api.equipment.potatoCannon.PotatoProjectileEntityHitAction;
import com.simibubi.create.api.equipment.potatoCannon.PotatoProjectileRenderMode;
import com.simibubi.create.content.equipment.potatoCannon.AllPotatoProjectileRenderModes;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public static class PotatoCannonProjectileType.Builder {
    private final List<Holder<Item>> items = new ArrayList<Holder<Item>>();
    private int reloadTicks = 10;
    private int damage = 1;
    private int split = 1;
    private float knockback = 1.0f;
    private float drag = 0.99f;
    private float velocityMultiplier = 1.0f;
    private float gravityMultiplier = 1.0f;
    private float soundPitch = 1.0f;
    private boolean sticky = false;
    private ItemStack dropStack = ItemStack.EMPTY;
    private PotatoProjectileRenderMode renderMode = AllPotatoProjectileRenderModes.Billboard.INSTANCE;
    private PotatoProjectileEntityHitAction preEntityHit = null;
    private PotatoProjectileEntityHitAction onEntityHit = null;
    private PotatoProjectileBlockHitAction onBlockHit = null;

    public PotatoCannonProjectileType.Builder reloadTicks(int reload) {
        this.reloadTicks = reload;
        return this;
    }

    public PotatoCannonProjectileType.Builder damage(int damage) {
        this.damage = damage;
        return this;
    }

    public PotatoCannonProjectileType.Builder splitInto(int split) {
        this.split = split;
        return this;
    }

    public PotatoCannonProjectileType.Builder knockback(float knockback) {
        this.knockback = knockback;
        return this;
    }

    public PotatoCannonProjectileType.Builder drag(float drag) {
        this.drag = drag;
        return this;
    }

    public PotatoCannonProjectileType.Builder velocity(float velocity) {
        this.velocityMultiplier = velocity;
        return this;
    }

    public PotatoCannonProjectileType.Builder gravity(float modifier) {
        this.gravityMultiplier = modifier;
        return this;
    }

    public PotatoCannonProjectileType.Builder soundPitch(float pitch) {
        this.soundPitch = pitch;
        return this;
    }

    public PotatoCannonProjectileType.Builder sticky() {
        this.sticky = true;
        return this;
    }

    public PotatoCannonProjectileType.Builder dropStack(ItemStack stack) {
        this.dropStack = stack;
        return this;
    }

    public PotatoCannonProjectileType.Builder renderMode(PotatoProjectileRenderMode renderMode) {
        this.renderMode = renderMode;
        return this;
    }

    public PotatoCannonProjectileType.Builder renderBillboard() {
        this.renderMode(AllPotatoProjectileRenderModes.Billboard.INSTANCE);
        return this;
    }

    public PotatoCannonProjectileType.Builder renderTumbling() {
        this.renderMode(AllPotatoProjectileRenderModes.Tumble.INSTANCE);
        return this;
    }

    public PotatoCannonProjectileType.Builder renderTowardMotion(int spriteAngle, float spin) {
        this.renderMode(new AllPotatoProjectileRenderModes.TowardMotion(spriteAngle, spin));
        return this;
    }

    public PotatoCannonProjectileType.Builder preEntityHit(PotatoProjectileEntityHitAction entityHitAction) {
        this.preEntityHit = entityHitAction;
        return this;
    }

    public PotatoCannonProjectileType.Builder onEntityHit(PotatoProjectileEntityHitAction entityHitAction) {
        this.onEntityHit = entityHitAction;
        return this;
    }

    public PotatoCannonProjectileType.Builder onBlockHit(PotatoProjectileBlockHitAction blockHitAction) {
        this.onBlockHit = blockHitAction;
        return this;
    }

    public PotatoCannonProjectileType.Builder addItems(ItemLike ... items) {
        for (ItemLike provider : items) {
            this.items.add((Holder<Item>)provider.asItem().builtInRegistryHolder());
        }
        return this;
    }

    public PotatoCannonProjectileType build() {
        return new PotatoCannonProjectileType((HolderSet<Item>)HolderSet.direct(this.items), this.reloadTicks, this.damage, this.split, this.knockback, this.drag, this.velocityMultiplier, this.gravityMultiplier, this.soundPitch, this.sticky, this.dropStack, this.renderMode, Optional.ofNullable(this.preEntityHit), Optional.ofNullable(this.onEntityHit), Optional.ofNullable(this.onBlockHit));
    }
}
