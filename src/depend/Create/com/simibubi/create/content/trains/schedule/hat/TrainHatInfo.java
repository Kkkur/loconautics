/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.client.model.geom.ModelPart
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.trains.schedule.hat;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.phys.Vec3;

public record TrainHatInfo(String part, int cubeIndex, Vec3 offset, float scale) {
    public static final Codec<TrainHatInfo> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.STRING.optionalFieldOf("model_part", (Object)"").forGetter(TrainHatInfo::part), (App)Codec.INT.optionalFieldOf("cube_index", (Object)0).forGetter(TrainHatInfo::cubeIndex), (App)Vec3.CODEC.fieldOf("offset").forGetter(TrainHatInfo::offset), (App)Codec.FLOAT.optionalFieldOf("scale", (Object)Float.valueOf(1.0f)).forGetter(TrainHatInfo::scale)).apply((Applicative)instance, TrainHatInfo::new));

    public static List<ModelPart> getAdjustedPart(TrainHatInfo info, ModelPart root, String defaultPart) {
        ArrayList<ModelPart> finalParts = new ArrayList<ModelPart>();
        finalParts.add(root);
        ModelPart parent = root;
        if (!info.part().isEmpty() && !info.part().equals(defaultPart)) {
            String[] partList;
            for (String part : partList = info.part().split("/")) {
                if (!parent.children.containsKey(part)) continue;
                finalParts.add((ModelPart)parent.children.get(part));
                parent = (ModelPart)parent.children.get(part);
            }
        } else if (parent.children.containsKey(defaultPart)) {
            finalParts.add((ModelPart)parent.children.get(defaultPart));
        }
        return finalParts;
    }
}
