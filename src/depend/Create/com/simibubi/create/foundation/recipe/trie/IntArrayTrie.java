/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.ints.IntIterator
 *  it.unimi.dsi.fastutil.ints.IntSet
 */
package com.simibubi.create.foundation.recipe.trie;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.ArrayList;
import java.util.List;

public class IntArrayTrie<V> {
    private final TrieNode<V> root = new TrieNode();
    private int maxDepth = 0;
    private int nodeCount = 1;
    private int valueCount = 0;

    public int getMaxDepth() {
        return this.maxDepth;
    }

    public int getNodeCount() {
        return this.nodeCount;
    }

    public int getValueCount() {
        return this.valueCount;
    }

    public void insert(int[] key, V value) {
        TrieNode currentNode = this.root;
        for (int k : key) {
            currentNode = (TrieNode)currentNode.children.computeIfAbsent(k, k1 -> {
                ++this.nodeCount;
                return new TrieNode();
            });
        }
        currentNode.values.add(value);
        this.maxDepth = Math.max(this.maxDepth, key.length);
        ++this.valueCount;
    }

    public List<V> lookup(IntSet pool) {
        ArrayList result = new ArrayList();
        IntArrayTrie.dfs(this.root, pool, result);
        return result;
    }

    private static <V> void dfs(TrieNode<V> node, IntSet pool, List<V> out) {
        out.addAll(node.values);
        if (node.children.size() > pool.size()) {
            IntIterator intIterator = pool.iterator();
            while (intIterator.hasNext()) {
                int key = (Integer)intIterator.next();
                TrieNode child = (TrieNode)node.children.get(key);
                if (child == null) continue;
                IntArrayTrie.dfs(child, pool, out);
            }
        } else {
            for (Int2ObjectMap.Entry entry : node.children.int2ObjectEntrySet()) {
                if (!pool.contains(entry.getIntKey())) continue;
                IntArrayTrie.dfs((TrieNode)entry.getValue(), pool, out);
            }
        }
    }

    public String toString() {
        return "IntArrayTrie{maxDepth=" + this.maxDepth + ", nodeCount=" + this.nodeCount + ", valueCount=" + this.valueCount + "}";
    }

    static class TrieNode<V> {
        final Int2ObjectMap<TrieNode<V>> children = new Int2ObjectOpenHashMap();
        final List<V> values = new ArrayList<V>();

        TrieNode() {
        }
    }
}
