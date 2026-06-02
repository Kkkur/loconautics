/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.lib.math.MoreMath
 *  it.unimi.dsi.fastutil.bytes.ByteArrayList
 *  it.unimi.dsi.fastutil.bytes.ByteList
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  net.minecraft.client.renderer.LightTexture
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.SectionPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.LightLayer
 */
package com.simibubi.create.content.contraptions.pulley;

import dev.engine_room.flywheel.lib.math.MoreMath;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.LightLayer;

private class AbstractPulleyVisual.LightCache {
    private final ByteList data = new ByteArrayList();
    private final LongSet sections = new LongOpenHashSet();
    private final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
    private int sectionCount;

    private AbstractPulleyVisual.LightCache() {
    }

    public void setSize(int size) {
        if (size != this.data.size()) {
            this.data.size(size);
            this.update();
            int sectionCount = MoreMath.ceilingDiv((int)(size + 15 - AbstractPulleyVisual.this.pos.getY() + AbstractPulleyVisual.this.pos.getY() / 4 * 4), (int)16);
            if (sectionCount != this.sectionCount) {
                this.sectionCount = sectionCount;
                this.sections.clear();
                int sectionX = SectionPos.blockToSectionCoord((int)AbstractPulleyVisual.this.pos.getX());
                int sectionY = SectionPos.blockToSectionCoord((int)AbstractPulleyVisual.this.pos.getY());
                int sectionZ = SectionPos.blockToSectionCoord((int)AbstractPulleyVisual.this.pos.getZ());
                for (int i = 0; i < sectionCount; ++i) {
                    this.sections.add(SectionPos.asLong((int)sectionX, (int)(sectionY - i), (int)sectionZ));
                }
                if (AbstractPulleyVisual.this.lightSections != null) {
                    this.updateSections();
                }
            }
        }
    }

    public void updateSections() {
        AbstractPulleyVisual.this.lightSections.sections(this.sections);
    }

    public void update() {
        this.mutablePos.set((Vec3i)AbstractPulleyVisual.this.pos);
        for (int i = 0; i < this.data.size(); ++i) {
            int blockLight = AbstractPulleyVisual.this.level.getBrightness(LightLayer.BLOCK, (BlockPos)this.mutablePos);
            int skyLight = AbstractPulleyVisual.this.level.getBrightness(LightLayer.SKY, (BlockPos)this.mutablePos);
            int light = (skyLight & 0xF) << 4 | blockLight & 0xF;
            this.data.set(i, (byte)light);
            this.mutablePos.move(Direction.DOWN);
        }
    }

    public int getPackedLight(int offset) {
        if (offset < 0 || offset >= this.data.size()) {
            return 0;
        }
        int light = Byte.toUnsignedInt(this.data.getByte(offset));
        int blockLight = light & 0xF;
        int skyLight = light >>> 4 & 0xF;
        return LightTexture.pack((int)blockLight, (int)skyLight);
    }
}
