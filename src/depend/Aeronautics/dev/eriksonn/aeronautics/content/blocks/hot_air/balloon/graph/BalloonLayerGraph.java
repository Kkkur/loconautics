/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.minecraft.core.BlockPos
 *  org.jetbrains.annotations.Nullable
 */
package dev.eriksonn.aeronautics.content.blocks.hot_air.balloon.graph;

import dev.eriksonn.aeronautics.content.blocks.hot_air.balloon.graph.BalloonLayerData;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

public class BalloonLayerGraph {
    private List<BalloonLayerData>[] layerMap;
    private int minY;

    public BalloonLayerGraph(int yLevel) {
        this.minY = yLevel;
        this.layerMap = new List[]{new ObjectArrayList()};
    }

    public void addLayer(int y, BalloonLayerData layer) {
        int index = this.getIndex(y);
        if (index < 0) {
            this.resizeDownwards(y);
            index = this.getIndex(y);
        } else if (index >= this.layerMap.length) {
            this.resizeUpwards(y);
        }
        this.layerMap[index].add(layer);
    }

    public void removeLayer(BalloonLayerData layer) {
        int index = this.getIndex(layer.getYLevel());
        List<BalloonLayerData> layers = this.layerMap[index];
        layers.remove(layer);
    }

    public void trim() {
        int start;
        int end = this.layerMap.length - 1;
        for (start = 0; start <= end && this.layerMap[start].isEmpty(); ++start) {
        }
        while (end >= start && this.layerMap[end].isEmpty()) {
            --end;
        }
        if (start == 0 && end == this.layerMap.length - 1) {
            return;
        }
        int newSize = end - start + 1;
        if (newSize <= 0) {
            return;
        }
        List[] newLayerMap = new List[newSize];
        System.arraycopy(this.layerMap, start, newLayerMap, 0, newSize);
        this.layerMap = newLayerMap;
        this.minY += start;
    }

    private int getIndex(int y) {
        return y - this.minY;
    }

    public List<BalloonLayerData> getLayersAtY(int y) {
        int index = this.getIndex(y);
        if (index >= 0 && index < this.layerMap.length) {
            return this.layerMap[index];
        }
        return Collections.emptyList();
    }

    private void resizeUpwards(int targetY) {
        int requiredSize = targetY - this.minY + 1;
        if (requiredSize <= this.layerMap.length) {
            return;
        }
        List[] newLayers = new List[requiredSize];
        System.arraycopy(this.layerMap, 0, newLayers, 0, this.layerMap.length);
        for (int i = this.layerMap.length; i < requiredSize; ++i) {
            newLayers[i] = new ObjectArrayList();
        }
        this.layerMap = newLayers;
    }

    private void resizeDownwards(int newMinY) {
        int deltaY = this.minY - newMinY;
        int oldLength = this.layerMap.length;
        int newLength = oldLength + deltaY;
        List[] newLayers = new List[newLength];
        for (int i = 0; i < deltaY; ++i) {
            newLayers[i] = new ObjectArrayList();
        }
        System.arraycopy(this.layerMap, 0, newLayers, deltaY, oldLength);
        this.layerMap = newLayers;
        this.minY = newMinY;
    }

    public List<BalloonLayerData>[] getAllLayers() {
        return this.layerMap;
    }

    public int getMinY() {
        return this.minY;
    }

    public int getMaxY() {
        return this.minY + this.layerMap.length - 1;
    }

    public void addAll(BalloonLayerGraph otherGraph) {
        List<BalloonLayerData>[] otherLayerMap = otherGraph.getAllLayers();
        for (int index = 0; index < otherLayerMap.length; ++index) {
            List<BalloonLayerData> layersAtY = otherLayerMap[index];
            int layerY = index + otherGraph.getMinY();
            for (BalloonLayerData otherLayer : layersAtY) {
                this.addLayer(layerY, otherLayer);
            }
        }
    }

    @Nullable
    public BalloonLayerData getLayerAt(BlockPos pos) {
        List<BalloonLayerData> layers = this.getLayersAtY(pos.getY());
        for (BalloonLayerData layer : layers) {
            int z;
            int x;
            if (!layer.getHotAirBlock(x = pos.getX(), z = pos.getZ())) continue;
            return layer;
        }
        return null;
    }

    public boolean hasBlockAt(BlockPos pos) {
        return this.getLayerAt(pos) != null;
    }

    public void rebuildConnections(BlockPos startPos) {
        for (List<BalloonLayerData> layersAtY : this.layerMap) {
            for (BalloonLayerData layer : layersAtY) {
                layer.inwardConnections.clear();
                layer.outwardConnections.clear();
            }
        }
        BalloonLayerData startLayer = this.getLayerAt(startPos);
        if (startLayer == null) {
            return;
        }
        ObjectArrayList queue = new ObjectArrayList();
        ObjectArrayList visited = new ObjectArrayList();
        queue.add((Object)startLayer);
        visited.add((Object)startLayer);
        while (!queue.isEmpty()) {
            BalloonLayerData current = (BalloonLayerData)queue.removeLast();
            int currentY = current.getYLevel();
            for (int dy = -1; dy <= 1; dy += 2) {
                int neighborY = currentY + dy;
                List<BalloonLayerData> neighborLayers = this.getLayersAtY(neighborY);
                if (neighborLayers.isEmpty()) continue;
                for (BalloonLayerData neighbor : neighborLayers) {
                    if (!current.overlaps(neighbor) || neighbor.outwardConnections.contains(current) || neighbor.inwardConnections.contains(current)) continue;
                    current.outwardConnections.add(neighbor);
                    neighbor.inwardConnections.add(current);
                    if (visited.contains((Object)neighbor)) continue;
                    visited.add((Object)neighbor);
                    queue.add((Object)neighbor);
                }
            }
        }
    }

    public Iterable<BalloonLayerData> propagateRemoval(BalloonLayerData startLayer) {
        if (startLayer == null) {
            return null;
        }
        ObjectArrayList frontier = new ObjectArrayList();
        ObjectArrayList visited = new ObjectArrayList();
        frontier.add((Object)startLayer);
        visited.add((Object)startLayer);
        while (!frontier.isEmpty()) {
            BalloonLayerData current = (BalloonLayerData)frontier.removeLast();
            for (BalloonLayerData outward : current.outwardConnections) {
                if (visited.contains((Object)outward)) continue;
                visited.add((Object)outward);
                frontier.add((Object)outward);
            }
            for (BalloonLayerData inward : current.inwardConnections) {
                if (current.getYLevel() <= inward.getYLevel() || visited.contains((Object)inward)) continue;
                visited.add((Object)inward);
                frontier.add((Object)inward);
            }
        }
        for (BalloonLayerData layer : visited) {
            this.removeLayer(layer);
        }
        this.trim();
        return visited;
    }
}
