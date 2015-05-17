package cache;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class FifoCache<K, V> implements Cache<K, V> {

    private Map<K, V> cache;
    private Deque<K> queue;
    private int size;

    public FifoCache(int size) {
        this.size = size;
        this.cache = new HashMap<>(size + 5);
        this.queue = new LinkedList<>();
    }

    @Override
    public void put(K key, V value) {
        V oldValue = cache.put(key, value);
        if (oldValue != null) {
            queue.removeFirstOccurrence(key);
        }
        queue.addFirst(key);

        if (cache.size() > size) {
            K removedKey = queue.removeLast();
            cache.remove(removedKey);
        }
    }

    @Override
    public V get(K key) {
        return cache.get(key);
    }

    @Override
    public void remove(K key) {
        queue.removeFirstOccurrence(key);
        cache.remove(key);
    }

    @Override
    public int size() {
        return cache.size();
    }

    @Override
    public String toString() {
        return cache.toString();
    }
}
