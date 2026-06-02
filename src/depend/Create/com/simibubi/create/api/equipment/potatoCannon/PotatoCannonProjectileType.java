/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.core.Holder
 *  net.minecraft.core.Holder$Reference
 *  net.minecraft.core.HolderSet
 *  net.minecraft.core.RegistryAccess
 *  net.minecraft.core.RegistryCodecs
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.EntityHitResult
 */
package com.simibubi.create.api.equipment.potatoCannon;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.api.equipment.potatoCannon.PotatoProjectileBlockHitAction;
import com.simibubi.create.api.equipment.potatoCannon.PotatoProjectileEntityHitAction;
import com.simibubi.create.api.equipment.potatoCannon.PotatoProjectileRenderMode;
import com.simibubi.create.api.registry.CreateRegistries;
import com.simibubi.create.content.equipment.potatoCannon.AllPotatoProjectileRenderModes;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public record PotatoCannonProjectileType(HolderSet<Item> items, int reloadTicks, int damage, int split, float knockback, float drag, float velocityMultiplier, float gravityMultiplier, float soundPitch, boolean sticky, ItemStack dropStack, PotatoProjectileRenderMode renderMode, Optional<PotatoProjectileEntityHitAction> preEntityHit, Optional<PotatoProjectileEntityHitAction> onEntityHit, Optional<PotatoProjectileBlockHitAction> onBlockHit) {
    private final ItemStack dropStack;
    public static final Codec<PotatoCannonProjectileType> CODEC = RecordCodecBuilder.create(i -> i.group((App)RegistryCodecs.homogeneousList((ResourceKey)Registries.ITEM).fieldOf("items").forGetter(PotatoCannonProjectileType::items), (App)Codec.INT.optionalFieldOf("reload_ticks", (Object)10).forGetter(PotatoCannonProjectileType::reloadTicks), (App)Codec.INT.optionalFieldOf("damage", (Object)1).forGetter(PotatoCannonProjectileType::damage), (App)Codec.INT.optionalFieldOf("split", (Object)1).forGetter(PotatoCannonProjectileType::split), (App)Codec.FLOAT.optionalFieldOf("knockback", (Object)Float.valueOf(1.0f)).forGetter(PotatoCannonProjectileType::knockback), (App)Codec.FLOAT.optionalFieldOf("drag", (Object)Float.valueOf(0.99f)).forGetter(PotatoCannonProjectileType::drag), (App)Codec.FLOAT.optionalFieldOf("velocity_multiplier", (Object)Float.valueOf(1.0f)).forGetter(PotatoCannonProjectileType::velocityMultiplier), (App)Codec.FLOAT.optionalFieldOf("gravity_multiplier", (Object)Float.valueOf(1.0f)).forGetter(PotatoCannonProjectileType::gravityMultiplier), (App)Codec.FLOAT.optionalFieldOf("sound_pitch", (Object)Float.valueOf(1.0f)).forGetter(PotatoCannonProjectileType::soundPitch), (App)Codec.BOOL.optionalFieldOf("sticky", (Object)false).forGetter(PotatoCannonProjectileType::sticky), (App)ItemStack.CODEC.optionalFieldOf("drop_stack", (Object)ItemStack.EMPTY).forGetter(PotatoCannonProjectileType::dropStack), (App)PotatoProjectileRenderMode.CODEC.optionalFieldOf("render_mode", (Object)AllPotatoProjectileRenderModes.Billboard.INSTANCE).forGetter(PotatoCannonProjectileType::renderMode), (App)PotatoProjectileEntityHitAction.CODEC.optionalFieldOf("pre_entity_hit").forGetter(p -> p.preEntityHit), (App)PotatoProjectileEntityHitAction.CODEC.optionalFieldOf("on_entity_hit").forGetter(p -> p.onEntityHit), (App)PotatoProjectileBlockHitAction.CODEC.optionalFieldOf("on_block_hit").forGetter(p -> p.onBlockHit)).apply((Applicative)i, PotatoCannonProjectileType::new));

    public static Optional<Holder.Reference<PotatoCannonProjectileType>> getTypeForItem(RegistryAccess registryAccess, Item item) {
        return registryAccess.lookupOrThrow(CreateRegistries.POTATO_PROJECTILE_TYPE).listElements().filter(ref -> ((PotatoCannonProjectileType)ref.value()).items.contains((Holder)item.builtInRegistryHolder())).findFirst();
    }

    public boolean preEntityHit(ItemStack stack, EntityHitResult ray) {
        return this.preEntityHit.map(i -> i.execute(stack, ray, PotatoProjectileEntityHitAction.Type.PRE_HIT)).orElse(false);
    }

    public boolean onEntityHit(ItemStack stack, EntityHitResult ray) {
        return this.onEntityHit.map(i -> i.execute(stack, ray, PotatoProjectileEntityHitAction.Type.ON_HIT)).orElse(false);
    }

    public boolean onBlockHit(LevelAccessor level, ItemStack stack, BlockHitResult ray) {
        return this.onBlockHit.map(i -> i.execute(level, stack, ray)).orElse(false);
    }

    public ItemStack dropStack() {
        return this.dropStack.copy();
    }

    public static class Builder {
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

        public Builder reloadTicks(int reload) {
            this.reloadTicks = reload;
            return this;
        }

        public Builder damage(int damage) {
            this.damage = damage;
            return this;
        }

        public Builder splitInto(int split) {
            this.split = split;
            return this;
        }

        public Builder knockback(float knockback) {
            this.knockback = knockback;
            return this;
        }

        public Builder drag(float drag) {
            this.drag = drag;
            return this;
        }

        public Builder velocity(float velocity) {
            this.velocityMultiplier = velocity;
            return this;
        }

        public Builder gravity(float modifier) {
            this.gravityMultiplier = modifier;
            return this;
        }

        public Builder soundPitch(float pitch) {
            this.soundPitch = pitch;
            return this;
        }

        public Builder sticky() {
            this.sticky = true;
            return this;
        }

        public Builder dropStack(ItemStack stack) {
            this.dropStack = stack;
            return this;
        }

        public Builder renderMode(PotatoProjectileRenderMode renderMode) {
            this.renderMode = renderMode;
            return this;
        }

        public Builder renderBillboard() {
            this.renderMode(AllPotatoProjectileRenderModes.Billboard.INSTANCE);
            return this;
        }

        public Builder renderTumbling() {
            this.renderMode(AllPotatoProjectileRenderModes.Tumble.INSTANCE);
            return this;
        }

        public Builder renderTowardMotion(int spriteAngle, float spin) {
            this.renderMode(new AllPotatoProjectileRenderModes.TowardMotion(spriteAngle, spin));
            return this;
        }

        public Builder preEntityHit(PotatoProjectileEntityHitAction entityHitAction) {
            this.preEntityHit = entityHitAction;
            return this;
        }

        public Builder onEntityHit(PotatoProjectileEntityHitAction entityHitAction) {
            this.onEntityHit = entityHitAction;
            return this;
        }

        public Builder onBlockHit(PotatoProjectileBlockHitAction blockHitAction) {
            this.onBlockHit = blockHitAction;
            return this;
        }

        public Builder addItems(ItemLike ... items) {
            for (ItemLike provider : items) {
                this.items.add((Holder<Item>)provider.asItem().builtInRegistryHolder());
            }
            return this;
        }

        public PotatoCannonProjectileType build() {
            return new PotatoCannonProjectileType((HolderSet<Item>)HolderSet.direct(this.items), this.reloadTicks, this.damage, this.split, this.knockback, this.drag, this.velocityMultiplier, this.gravityMultiplier, this.soundPitch, this.sticky, this.dropStack, this.renderMode, Optional.ofNullable(this.preEntityHit), Optional.ofNullable(this.onEntityHit), Optional.ofNullable(this.onBlockHit));
        }
    }
}
