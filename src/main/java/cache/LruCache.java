package cache;

import java.util.LinkedHashMap;
import java.util.Map;

public class LruCache<K, V> implements Cache<K, V> {

    private Map<K, V> cache;

    public LruCache(final int size) {
        this.cache = new LinkedHashMap<K, V>(size, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return super.size() > size;
            }
        };
    }

    @Override
    public void put(K key, V value) {
        cache.put(key, value);
    }

    @Override
    public V get(K key) {
        return cache.get(key);
    }

    V getQuiet(K key) {
        V value = cache.get(key);
        if (value != null) {
            cache.remove(key);
            cache.put(key, value);
        }
        return value;
    }

    @Override
    public void remove(K key) {
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
