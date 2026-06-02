/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.renderer.Rect2i
 *  net.minecraft.network.chat.Style
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.equipment.clipboard;

import com.simibubi.create.content.equipment.clipboard.ClipboardScreen;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Style;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(value=Dist.CLIENT)
static class ClipboardScreen.DisplayCache {
    static final ClipboardScreen.DisplayCache EMPTY = new ClipboardScreen.DisplayCache("", new ClipboardScreen.Pos2i(0, 0), true, new int[]{0}, new ClipboardScreen.LineInfo[]{new ClipboardScreen.LineInfo(Style.EMPTY, "", 0, 0)}, new Rect2i[0]);
    private final String fullText;
    final ClipboardScreen.Pos2i cursor;
    final boolean cursorAtEnd;
    private final int[] lineStarts;
    final ClipboardScreen.LineInfo[] lines;
    final Rect2i[] selection;

    public ClipboardScreen.DisplayCache(String pFullText, ClipboardScreen.Pos2i pCursor, boolean pCursorAtEnd, int[] pLineStarts, ClipboardScreen.LineInfo[] pLines, Rect2i[] pSelection) {
        this.fullText = pFullText;
        this.cursor = pCursor;
        this.cursorAtEnd = pCursorAtEnd;
        this.lineStarts = pLineStarts;
        this.lines = pLines;
        this.selection = pSelection;
    }

    public int getIndexAtPosition(Font pFont, ClipboardScreen.Pos2i pCursorPosition) {
        int i = pCursorPosition.y / 9;
        if (i < 0) {
            return 0;
        }
        if (i >= this.lines.length) {
            return this.fullText.length();
        }
        ClipboardScreen.LineInfo line = this.lines[i];
        return this.lineStarts[i] + pFont.getSplitter().plainIndexAtWidth(line.contents, pCursorPosition.x, line.style);
    }

    public int changeLine(int pXChange, int pYChange) {
        int k;
        int i = ClipboardScreen.findLineFromPos(this.lineStarts, pXChange);
        int j = i + pYChange;
        if (0 <= j && j < this.lineStarts.length) {
            int l = pXChange - this.lineStarts[i];
            int i1 = this.lines[j].contents.length();
            k = this.lineStarts[j] + Math.min(l, i1);
        } else {
            k = pXChange;
        }
        return k;
    }

    public int findLineStart(int pLine) {
        int i = ClipboardScreen.findLineFromPos(this.lineStarts, pLine);
        return this.lineStarts[i];
    }

    public int findLineEnd(int pLine) {
        int i = ClipboardScreen.findLineFromPos(this.lineStarts, pLine);
        return this.lineStarts[i] + this.lines[i].contents.length();
    }
}
