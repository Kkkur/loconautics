/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.data.PackOutput
 *  net.minecraft.data.tags.TagsProvider
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.tags.DamageTypeTags
 *  net.minecraft.world.damagesource.DamageType
 *  net.neoforged.neoforge.common.data.ExistingFileHelper
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.foundation.data;

import com.simibubi.create.AllDamageTypes;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class DamageTypeTagGen
extends TagsProvider<DamageType> {
    public DamageTypeTagGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, Registries.DAMAGE_TYPE, lookupProvider, "create", existingFileHelper);
    }

    protected void addTags(HolderLookup.Provider provider) {
        this.tag(DamageTypeTags.BYPASSES_ARMOR).add(new ResourceKey[]{AllDamageTypes.CRUSH, AllDamageTypes.FAN_FIRE, AllDamageTypes.FAN_LAVA, AllDamageTypes.DRILL, AllDamageTypes.SAW});
        this.tag(DamageTypeTags.IS_FIRE).add(new ResourceKey[]{AllDamageTypes.FAN_FIRE, AllDamageTypes.FAN_LAVA});
        this.tag(DamageTypeTags.IS_EXPLOSION).add(AllDamageTypes.CUCKOO_SURPRISE);
    }

    public String getName() {
        return "Create's Damage Type Tags";
    }
}
