/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.data.PackOutput
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.infrastructure.data;

import com.simibubi.create.api.data.TrainHatInfoProvider;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;

public class VanillaHatOffsetGenerator
extends TrainHatInfoProvider {
    public VanillaHatOffsetGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void createOffsets() {
        this.makeInfoFor(EntityType.ARMADILLO, new Vec3(0.0, 4.0, 0.0), "body/head/head_cube", 0.92f);
        this.makeInfoFor(EntityType.AXOLOTL, new Vec3(0.0, 1.0, -2.0), "head", 0.75f);
        this.makeInfoFor(EntityType.BAT, new Vec3(0.0, -0.2, 0.0), "head", 0.8f);
        this.makeInfoFor(EntityType.BEE, new Vec3(0.0, 2.0, -2.0), "body", 0.5f);
        this.makeInfoFor(EntityType.BLAZE, new Vec3(0.0, 4.0, 0.0));
        this.makeInfoFor(EntityType.BREEZE, new Vec3(0.0, -5.0, -0.1), "body/head", 0.8f);
        this.makeInfoFor(EntityType.CAMEL, new Vec3(0.0, -8.0, -11.5), "body/head", 1, 1.0f);
        this.makeInfoFor(EntityType.CAT, new Vec3(0.0, 1.0, -0.25));
        this.makeInfoFor(EntityType.CAVE_SPIDER, new Vec3(0.0, 2.0, -3.5));
        this.makeInfoFor(EntityType.CHICKEN, new Vec3(0.0, 0.0, -0.25));
        this.makeInfoFor(EntityType.COD, new Vec3(0.0, 10.0, 0.0));
        this.makeInfoFor(EntityType.COW, new Vec3(0.0, 3.0, -3.0), 0.87f);
        this.makeInfoFor(EntityType.DOLPHIN, new Vec3(0.0, 3.0, 0.0), "body/head", 0.75f);
        this.makeInfoFor(EntityType.DONKEY, new Vec3(0.0, -0.8, 2.0));
        this.makeInfoFor(EntityType.ELDER_GUARDIAN, new Vec3(0.0, 20.0, 0.0), 0.95f);
        this.makeInfoFor(EntityType.ENDERMITE, new Vec3(0.0, 2.5, 0.5), "segment0", 0.75f);
        this.makeInfoFor(EntityType.FOX, new Vec3(1.0, 2.5, -2.0), 0.9f);
        this.makeInfoFor(EntityType.FROG, new Vec3(0.0, -4.0, -2.5), "body/head", 0.75f);
        this.makeInfoFor(EntityType.GHAST, new Vec3(0.0, 6.0, 0.0), "body", 0.92f);
        this.makeInfoFor(EntityType.GLOW_SQUID, new Vec3(0.0, 7.0, 0.0), "body", 0.92f);
        this.makeInfoFor(EntityType.GOAT, new Vec3(-0.5, -8.5, -9.0), "head", 2.0f);
        this.makeInfoFor(EntityType.GUARDIAN, new Vec3(0.0, 20.0, 0.0), 0.9f);
        this.makeInfoFor(EntityType.HOGLIN, new Vec3(0.0, 0.0, -4.5), 0.5f);
        this.makeInfoFor(EntityType.HORSE, new Vec3(0.0, -0.8, 2.0));
        this.makeInfoFor(EntityType.IRON_GOLEM, new Vec3(0.0, -2.0, -1.5));
        this.makeInfoFor(EntityType.MAGMA_CUBE, new Vec3(0.0, 16.0, 0.0), "cube7");
        this.makeInfoFor(EntityType.MOOSHROOM, new Vec3(0.0, 3.0, -3.0), 0.87f);
        this.makeInfoFor(EntityType.MULE, new Vec3(0.0, -0.8, 2.0));
        this.makeInfoFor(EntityType.OCELOT, new Vec3(0.0, 1.0, -0.25));
        this.makeInfoFor(EntityType.PANDA, new Vec3(0.0, 4.0, 0.5), 0.75f);
        this.makeInfoFor(EntityType.PARROT, new Vec3(0.0, 0.0, -1.5));
        this.makeInfoFor(EntityType.PHANTOM, new Vec3(0.0, 0.0, -1.0), "body/head");
        this.makeInfoFor(EntityType.PIG, new Vec3(0.0, 3.0, -4.0));
        this.makeInfoFor(EntityType.PIGLIN, new Vec3(0.0, 0.0, 0.0), 0.92f);
        this.makeInfoFor(EntityType.PIGLIN_BRUTE, new Vec3(0.0, 0.0, 0.0), 0.92f);
        this.makeInfoFor(EntityType.POLAR_BEAR, new Vec3(0.0, 3.0, 0.0));
        this.makeInfoFor(EntityType.PUFFERFISH, new Vec3(0.0, -0.5, 0.0), "body", 0.75f);
        this.makeInfoFor(EntityType.RAVAGER, new Vec3(0.0, 0.0, -5.5), "neck/head");
        this.makeInfoFor(EntityType.SALMON, new Vec3(0.0, 1.0, 0.0));
        this.makeInfoFor(EntityType.SHEEP, new Vec3(0.0, 0.4, -1.0), 0.87f);
        this.makeInfoFor(EntityType.SILVERFISH, new Vec3(0.0, 3.0, 0.0), "segment1");
        this.makeInfoFor(EntityType.SKELETON_HORSE, new Vec3(0.0, -0.8, 2.0));
        this.makeInfoFor(EntityType.SLIME, new Vec3(0.0, 21.0, 0.0), "cube", 1.25f);
        this.makeInfoFor(EntityType.SNIFFER, new Vec3(0.0, 8.0, -5.0), "bone/body/head");
        this.makeInfoFor(EntityType.SNOW_GOLEM, new Vec3(0.0, -0.2, 0.0), 0.82f);
        this.makeInfoFor(EntityType.SPIDER, new Vec3(0.0, 2.0, -3.5));
        this.makeInfoFor(EntityType.SQUID, new Vec3(0.0, 7.0, 0.0), "body", 0.92f);
        this.makeInfoFor(EntityType.STRIDER, new Vec3(0.0, 8.0, 0.0), "body", 0.95f);
        this.makeInfoFor(EntityType.TADPOLE, new Vec3(0.0, 1.0, 1.5), "body");
        this.makeInfoFor(EntityType.TROPICAL_FISH, new Vec3(0.0, 1.0, -2.0), "body", 0.5f);
        this.makeInfoFor(EntityType.TURTLE, new Vec3(0.0, 3.0, 0.0));
        this.makeInfoFor(EntityType.WARDEN, new Vec3(0.0, 0.0, 0.5), "bone/body/head", 0.9f);
        this.makeInfoFor(EntityType.WITCH, new Vec3(0.0, -1.8, 0.0), 1.0f);
        this.makeInfoFor(EntityType.WITHER, new Vec3(0.0, 3.0, 0.0), "center_head");
        this.makeInfoFor(EntityType.WOLF, new Vec3(1.0, 2.5, 1.0), "real_head");
        this.makeInfoFor(EntityType.ZOGLIN, new Vec3(0.0, 0.0, -4.5), 0.5f);
        this.makeInfoFor(EntityType.ZOMBIE_HORSE, new Vec3(0.0, -0.8, 2.0));
        this.makeInfoFor(EntityType.ZOMBIFIED_PIGLIN, new Vec3(0.0, 0.0, 0.0), 0.92f);
    }
}
