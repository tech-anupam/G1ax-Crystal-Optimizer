package dev.akatriggered.util.datastructure;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class EvictingList<T> extends LinkedList<T> {
    private final int maxSize;

    public EvictingList(int maxSize) {
        this.maxSize = maxSize;
    }

    public EvictingList(Collection<? extends T> c, int maxSize) {
        super(c);
        this.maxSize = maxSize;
    }

    @Override
    public boolean add(T t) {
        if (this.size() >= this.maxSize && this.maxSize > 0) {
            this.removeFirst();
        }
        return super.add(t);
    }

    public List<T> getLast(int count) {
        if (count <= 0) return List.of();
        int fromIndex = Math.max(0, this.size() - count);
        return new LinkedList<>(this.subList(fromIndex, this.size()));
    }

    public int getMaxSize() {
        return this.maxSize;
    }
}
