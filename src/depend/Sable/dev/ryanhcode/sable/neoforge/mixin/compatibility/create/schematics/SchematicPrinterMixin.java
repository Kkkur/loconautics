/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  com.llamalad7.mixinextras.sugar.Local
 *  com.simibubi.create.content.contraptions.StructureTransform
 *  com.simibubi.create.content.schematics.SchematicPrinter
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  net.createmod.catnip.levelWrappers.SchematicLevel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.ServerLevelAccessor
 *  net.minecraft.world.level.block.Rotation
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.schematics;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.schematics.SchematicPrinter;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.neoforge.mixinterface.compatibility.create.schematics.SchematicLevelExtension;
import dev.ryanhcode.sable.neoforge.mixinterface.compatibility.create.schematics.SchematicPrinterExtension;
import dev.ryanhcode.sable.neoforge.mixinterface.compatibility.create.schematics.StructureTemplateExtension;
import java.util.List;
import net.createmod.catnip.levelWrappers.SchematicLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={SchematicPrinter.class})
public class SchematicPrinterMixin
implements SchematicPrinterExtension {
    @Shadow
    private SchematicLevel blockReader;

    @Inject(method={"loadSchematic"}, at={@At(value="TAIL")})
    private void sable$loadSchematic(ItemStack blueprint, Level originalWorld, boolean processNBT, CallbackInfo ci, @Local StructureTransform transform) {
        List<SchematicLevelExtension.SchematicSubLevel> schematicSubLevels = ((SchematicLevelExtension)this.blockReader).sable$getSubLevels();
        for (SchematicLevelExtension.SchematicSubLevel schematicSubLevel : schematicSubLevels) {
            Vec3 transformedPos = transform.applyWithoutOffset(JOMLConversion.toMojang((Vector3dc)schematicSubLevel.position()));
            JOMLConversion.toJOML((Position)transformedPos, (Vector3d)schematicSubLevel.position());
            double radians = switch (transform.rotation) {
                default -> throw new MatchException(null, null);
                case Rotation.NONE -> 0.0;
                case Rotation.CLOCKWISE_90 -> -1.5707963267948966;
                case Rotation.CLOCKWISE_180 -> Math.PI;
                case Rotation.COUNTERCLOCKWISE_90 -> 1.5707963267948966;
            };
            schematicSubLevel.orientation().rotateLocalY(radians);
        }
    }

    @WrapOperation(method={"loadSchematic"}, at={@At(value="INVOKE", target="Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate;placeInWorld(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructurePlaceSettings;Lnet/minecraft/util/RandomSource;I)Z")})
    private boolean sable$setupRenderer(StructureTemplate template, ServerLevelAccessor serverLevelAccessor, BlockPos blockPos, BlockPos blockPos2, StructurePlaceSettings structurePlaceSettings, RandomSource randomSource, int i, Operation<Boolean> original, @Local(argsOnly=true) Level level) {
        if (serverLevelAccessor instanceof SchematicLevel) {
            SchematicLevel schematicLevel = (SchematicLevel)serverLevelAccessor;
            StructureTemplateExtension extension = (StructureTemplateExtension)template;
            List<StructureTemplateExtension.SubLevelTemplate> subLevelTemplates = extension.sable$getSubLevels();
            SchematicLevelExtension schematicLevelExtension = (SchematicLevelExtension)schematicLevel;
            for (StructureTemplateExtension.SubLevelTemplate subLevelTemplate : subLevelTemplates) {
                SchematicLevel subSchematicLevel = new SchematicLevel(level);
                subLevelTemplate.template().placeInWorld((ServerLevelAccessor)subSchematicLevel, BlockPos.ZERO, BlockPos.ZERO, new StructurePlaceSettings(), level.getRandom(), 2);
                schematicLevelExtension.sable$getSubLevels().add(new SchematicLevelExtension.SchematicSubLevel(subLevelTemplate.uuid(), subLevelTemplate.position(), subLevelTemplate.orientation(), subSchematicLevel));
            }
        }
        return (Boolean)original.call(new Object[]{template, serverLevelAccessor, blockPos, blockPos2, structurePlaceSettings, randomSource, i});
    }

    @Override
    public SchematicLevel sable$getSchematicLevel() {
        return this.blockReader;
    }
}
