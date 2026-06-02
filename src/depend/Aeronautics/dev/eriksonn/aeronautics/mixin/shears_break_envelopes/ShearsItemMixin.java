/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  net.minecraft.world.item.ShearsItem
 *  net.minecraft.world.item.component.Tool$Rule
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 */
package dev.eriksonn.aeronautics.mixin.shears_break_envelopes;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.eriksonn.aeronautics.index.AeroTags;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.component.Tool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value={ShearsItem.class})
public class ShearsItemMixin {
    @WrapOperation(method={"createToolProperties"}, at={@At(value="INVOKE", target="Ljava/util/List;of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/List;")})
    private static <E> List<E> aeronautics$createToolProperties(E e1, E e2, E e3, E e4, Operation<List<E>> original) {
        ArrayList<Tool.Rule> newList = new ArrayList<Tool.Rule>((Collection)original.call(new Object[]{e1, e2, e3, e4}));
        newList.add(Tool.Rule.overrideSpeed(AeroTags.BlockTags.ENVELOPE, (float)5.0f));
        return newList;
    }
}
