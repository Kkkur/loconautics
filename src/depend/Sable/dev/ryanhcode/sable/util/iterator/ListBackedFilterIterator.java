/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.Nullable
 */
package dev.ryanhcode.sable.util.iterator;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public class ListBackedFilterIterator<T>
implements Iterator<T> {
    private final Predicate<T> filter;
    private final List<T> backingList;
    private int index;
    private T nextObject;

    public ListBackedFilterIterator(Predicate<T> filter, List<T> backingList) {
        this.filter = filter;
        this.backingList = backingList;
    }

    @Nullable
    public T findNextObject() {
        if (this.nextObject != null) {
            return this.nextObject;
        }
        while (this.index < this.backingList.size()) {
            T next = this.backingList.get(this.index);
            if (this.filter.test(next)) {
                ++this.index;
                this.nextObject = next;
                return this.nextObject;
            }
            ++this.index;
        }
        return null;
    }

    @Override
    public boolean hasNext() {
        return this.findNextObject() != null;
    }

    @Override
    public T next() {
        if (this.findNextObject() == null) {
            throw new NoSuchElementException();
        }
        T result = this.nextObject;
        this.nextObject = null;
        return result;
    }
}
