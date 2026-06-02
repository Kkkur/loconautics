/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.neoforged.neoforge.entity.IEntityWithComplexSpawn
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 */
package dev.simulated_team.simulated.neoforge.mixin.self_mixins;

import dev.simulated_team.simulated.content.entities.honey_glue.HoneyGlueEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={HoneyGlueEntity.class})
public abstract class HoneyGlueEntityMixin
implements IEntityWithComplexSpawn {
    @Shadow
    public abstract void addAdditionalSaveData(CompoundTag var1);

    @Shadow
    public abstract void readAdditionalSaveData(CompoundTag var1);

    public void writeSpawnData(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
        CompoundTag compound = new CompoundTag();
        this.addAdditionalSaveData(compound);
        registryFriendlyByteBuf.writeNbt((Tag)compound);
    }

    public void readSpawnData(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
        this.readAdditionalSaveData(registryFriendlyByteBuf.readNbt());
    }
}
