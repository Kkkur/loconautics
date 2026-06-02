/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.server.network.Filterable
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.component.WritableBookContent
 *  net.minecraft.world.item.component.WrittenBookContent
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.LecternBlockEntity
 */
package com.simibubi.create.content.redstone.displayLink.target;

import com.simibubi.create.api.behaviour.display.DisplayTarget;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.WritableBookContent;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;

public class LecternDisplayTarget
extends DisplayTarget {
    @Override
    public void acceptText(int line, List<MutableComponent> text, DisplayLinkContext context) {
        BlockEntity be = context.getTargetBlockEntity();
        if (!(be instanceof LecternBlockEntity)) {
            return;
        }
        LecternBlockEntity lectern = (LecternBlockEntity)be;
        ItemStack book = lectern.getBook();
        if (book.isEmpty()) {
            return;
        }
        if (book.is(Items.WRITABLE_BOOK)) {
            book = this.signBook(book);
            lectern.setBook(book);
        }
        if (!book.is(Items.WRITTEN_BOOK)) {
            return;
        }
        WrittenBookContent writtenBookContent = (WrittenBookContent)book.getOrDefault(DataComponents.WRITTEN_BOOK_CONTENT, (Object)WrittenBookContent.EMPTY);
        ArrayList<Filterable> pages = new ArrayList<Filterable>(writtenBookContent.pages());
        boolean changed = false;
        for (int i = 0; i - line < text.size() && i < 50; ++i) {
            if (pages.size() <= i) {
                pages.add(Filterable.passThrough((Object)(i < line ? Component.empty() : (Component)text.get(i - line))));
            } else if (i >= line) {
                if (i - line == 0) {
                    LecternDisplayTarget.reserve(i, (BlockEntity)lectern, context);
                }
                if (i - line > 0 && this.isReserved(i - line, (BlockEntity)lectern, context)) break;
                pages.set(i, Filterable.passThrough((Object)((Component)text.get(i - line))));
            }
            changed = true;
        }
        book.set(DataComponents.WRITTEN_BOOK_CONTENT, (Object)writtenBookContent.withReplacedPages(pages));
        lectern.setBook(book);
        if (changed) {
            context.level().sendBlockUpdated(context.getTargetPos(), lectern.getBlockState(), lectern.getBlockState(), 2);
        }
    }

    @Override
    public DisplayTargetStats provideStats(DisplayLinkContext context) {
        return new DisplayTargetStats(50, 256, this);
    }

    @Override
    public Component getLineOptionText(int line) {
        return CreateLang.translateDirect("display_target.page", line + 1);
    }

    private ItemStack signBook(ItemStack book) {
        ItemStack written = new ItemStack((ItemLike)Items.WRITTEN_BOOK);
        WritableBookContent bookContents = (WritableBookContent)book.get(DataComponents.WRITABLE_BOOK_CONTENT);
        List<Filterable> list = bookContents.pages().stream().map(filterable -> filterable.map(Component::literal)).toList();
        WrittenBookContent writtenContent = new WrittenBookContent(Filterable.passThrough((Object)"Printed Book"), "Data Gatherer", 0, list, true);
        written.set(DataComponents.WRITTEN_BOOK_CONTENT, (Object)writtenContent);
        return written;
    }

    @Override
    public boolean requiresComponentSanitization() {
        return true;
    }
}
