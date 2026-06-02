/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 */
package com.simibubi.create.foundation.recipe.trie;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.List;

static class IntArrayTrie.TrieNode<V> {
    final Int2ObjectMap<IntArrayTrie.TrieNode<V>> children = new Int2ObjectOpenHashMap();
    final List<V> values = new ArrayList<V>();

    IntArrayTrie.TrieNode() {
    }
}
