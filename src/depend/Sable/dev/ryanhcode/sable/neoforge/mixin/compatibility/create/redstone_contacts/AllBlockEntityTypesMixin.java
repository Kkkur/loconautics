/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.AllBlockEntityTypes
 *  com.simibubi.create.AllBlocks
 *  com.simibubi.create.foundation.data.CreateRegistrate
 *  com.tterrag.registrate.util.entry.BlockEntityEntry
 *  com.tterrag.registrate.util.nullness.NonNullSupplier
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.redstone_contacts;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import dev.ryanhcode.sable.neoforge.mixinhelper.compatibility.create.redstone_contact.RedstoneContactBlockEntity;
import dev.ryanhcode.sable.neoforge.mixinhelper.compatibility.create.redstone_contact.RedstoneContactBlockEntityTypeGetter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value={AllBlockEntityTypes.class})
public class AllBlockEntityTypesMixin
implements RedstoneContactBlockEntityTypeGetter {
    @Shadow
    @Final
    private static CreateRegistrate REGISTRATE;
    @Unique
    private static final BlockEntityEntry<RedstoneContactBlockEntity> REDSTONE_CONTACT;

    @Override
    public BlockEntityEntry<RedstoneContactBlockEntity> sable$getRedstoneContactType() {
        return REDSTONE_CONTACT;
    }

    static {
        REDSTONE_CONTACT = REGISTRATE.blockEntity("redstone_contact", RedstoneContactBlockEntity::new).validBlock((NonNullSupplier)AllBlocks.REDSTONE_CONTACT).register();
    }
}
