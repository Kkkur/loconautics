/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.Nullable
 */
package dev.ryanhcode.sable.util.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public class IteratorBackedFilterIterator<T>
implements Iterator<T> {
    private final Predicate<T> filter;
    private final Iterator<T> backingIterator;
    private T nextObject;

    public IteratorBackedFilterIterator(Predicate<T> filter, Iterator<T> backingIterator) {
        this.filter = filter;
        this.backingIterator = backingIterator;
    }

    @Nullable
    public T findNextObject() {
        if (this.nextObject != null) {
            return this.nextObject;
        }
        while (this.backingIterator.hasNext()) {
            T next = this.backingIterator.next();
            if (!this.filter.test(next)) continue;
            this.nextObject = next;
            return this.nextObject;
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
