package cache;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class CacheTest {

    int size;
    int[] array;
    private FifoCache<Integer, Integer> fifoCache;
    private LruCache<Integer, Integer> lruCache;
    private Set<Integer> goalSet;
    private Set<Integer> exceptionSet;
    private Deque<Integer> goalList;

    @BeforeTest
    public void init() {
        int min = 0;
        int max = 20;
        size = randInt(min, max);
        int arraySize = size * 100;
        array = new int[arraySize];
        for (int i = 0; i < arraySize; i++) {
            array[i] = randInt(min, max);
        }
        fifoCache = new FifoCache<>(size);
        lruCache = new LruCache<>(size);

        for (int val : array) {
            fifoCache.put(val, val);
            lruCache.put(val, val);
        }

        goalSet = new HashSet<>();
        goalList = new LinkedList<>();
        exceptionSet = new HashSet<>();
        for (int i = 0; i < array.length; i++) {
            int val = array[array.length - i - 1];
            if (goalSet.size() < size) {
                if (!goalSet.contains(val))
                    goalList.addLast(val);
                goalSet.add(val);
            } else {
                if (!goalSet.contains(val))
                    exceptionSet.add(val);
            }
        }
    }

    public static int randInt(int min, int max) {
        return min + (int) (Math.random() * ((max - min) + 1));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void fifoCacheTest() {
        assertEquals(fifoCache.size(), lruCache.size(), "Size must be equals");

        for (Integer val : goalSet) {
            Integer fifo = fifoCache.get(val);
            assertTrue(fifo != null, "Object can't be null");
            assertEquals(fifo, val, "Objects must be equals");
        }

        for (Integer val : exceptionSet) {
            Integer fifo = fifoCache.get(val);
            assertTrue(fifo == null, "Object must be null");
        }

        for (Integer next : exceptionSet) {
            int key = goalList.getLast();
            Integer last = fifoCache.get(key);
            assertTrue(last != null, "Object can't be null");

            fifoCache.put(next, next);
            goalList.addFirst(next);
            goalList.removeLast();

            Integer miss = fifoCache.get(key);
            assertTrue(miss == null, "Object must be null");
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void lruCacheTest() {
        assertEquals(fifoCache.size(), lruCache.size(), "Size must be equals");
        goalList.clear();
        for (Integer val : goalSet) {
            Integer lru = lruCache.getQuiet(val);
            assertTrue(lru != null, "Object can't be null");
            assertEquals(lru, val, "Objects must be equals");
            goalList.addLast(val);
        }

        for (Integer val : exceptionSet) {
            Integer lru = lruCache.get(val);
            assertTrue(lru == null, "Object must be null");
        }

        for (Integer next : exceptionSet) {
            int key = goalList.getLast();
            Integer last = lruCache.get(key);
            assertTrue(last != null, "Object can't be null");

            goalList.removeFirstOccurrence(key);
            goalList.addLast(key);

            lruCache.put(next, next);

            goalList.addLast(next);
            Integer remove = goalList.removeFirst();

            Integer miss = lruCache.get(remove);
            assertTrue(miss == null, "Object must be null");
        }
    }
}
