/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.data.PackOutput
 *  net.neoforged.neoforge.common.data.ExistingFileHelper
 *  top.theillusivec4.curios.api.CuriosDataProvider
 */
package com.simibubi.create.compat.curios;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import top.theillusivec4.curios.api.CuriosDataProvider;

public class CuriosDataGenerator
extends CuriosDataProvider {
    public CuriosDataGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper fileHelper) {
        super("create", output, fileHelper, registries);
    }

    public void generate(HolderLookup.Provider registries, ExistingFileHelper fileHelper) {
        this.createEntities("players").addPlayer().addSlots(new String[]{"head"});
    }
}
