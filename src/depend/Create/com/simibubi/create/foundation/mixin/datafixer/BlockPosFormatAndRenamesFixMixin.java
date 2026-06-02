/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Local
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DSL$TypeReference
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.serialization.Dynamic
 *  net.minecraft.util.datafix.fixes.BlockPosFormatAndRenamesFix
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.simibubi.create.foundation.mixin.datafixer;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import com.simibubi.create.foundation.utility.DataFixerHelper;
import java.util.List;
import java.util.Map;
import net.minecraft.util.datafix.fixes.BlockPosFormatAndRenamesFix;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={BlockPosFormatAndRenamesFix.class})
public abstract class BlockPosFormatAndRenamesFixMixin
extends DataFix {
    private BlockPosFormatAndRenamesFixMixin(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType);
    }

    @Shadow
    protected abstract TypeRewriteRule createEntityFixer(DSL.TypeReference var1, String var2, Map<String, String> var3);

    @Inject(method={"makeRule"}, at={@At(value="INVOKE", target="Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal=0)})
    private void create$addFixers(CallbackInfoReturnable<TypeRewriteRule> cir, @Local List<TypeRewriteRule> output) {
        for (DataFixerHelper.BlockPosFixer fixer : DataFixerHelper.BLOCK_POS_FIXERS_VIEW) {
            TypeRewriteRule rule;
            DSL.TypeReference ref = fixer.reference();
            String id = fixer.id();
            if (fixer.customFixer() != null) {
                OpticFinder opticfinder = DSL.namedChoice((String)id, (Type)this.getInputSchema().getChoiceType(ref, id));
                rule = this.fixTypeEverywhereTyped("BlockPos format for " + id + " (" + ref.typeName() + ")", this.getInputSchema().getType(ref), typed -> typed.updateTyped(opticfinder, data -> data.update(DSL.remainderFinder(), dynamic -> fixer.customFixer().apply((Dynamic<?>)dynamic))));
            } else {
                rule = this.createEntityFixer(ref, id, fixer.renames());
            }
            output.add(rule);
        }
    }
}
