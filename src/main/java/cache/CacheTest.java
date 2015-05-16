package cache;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

public class CacheTest {

    int size;
    int[] array;

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
    }

    public static int randInt(int min, int max) {
        return min + (int) (Math.random() * ((max - min) + 1));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test() {
        FifoCache<Integer, Integer> fifoCache = new FifoCache<>(size);
        LruCache<Integer, Integer> lruCache = new LruCache<>(size);

        for (int val : array) {
            fifoCache.put(val, val);
            lruCache.put(val, val);
        }

        Assert.assertEquals(fifoCache.size(), lruCache.size(), "Size must be equals");

        for (int i : array) {
            Integer lru = fifoCache.getQuiet(i);
            Integer fifo = lruCache.getQuiet(i);
            Assert.assertEquals(lru, fifo, "Object must be equals");
        }

        Set<Integer> goalSet = new HashSet<>();
        Set<Integer> exceptionSet = new HashSet<>();
        for (int i = 0; i < array.length; i++) {
            int val = array[array.length - i - 1];
            if (goalSet.size() < size) {
                goalSet.add(val);
            } else {
                if (!goalSet.contains(val))
                exceptionSet.add(val);
            }
        }

        for (Integer val : goalSet) {
            Integer lru = fifoCache.getQuiet(val);
            Assert.assertTrue(lru != null, "Object can't be null");

            Integer fifo = lruCache.getQuiet(val);
            Assert.assertTrue(fifo != null, "Object can't be null");

            Assert.assertEquals(lru, fifo, "Object must be equals");
        }

        for (Integer val : exceptionSet) {
            Integer lru = fifoCache.getQuiet(val);
            Assert.assertTrue(lru == null, "Object must be null");

            Integer fifo = lruCache.getQuiet(val);
            Assert.assertTrue(fifo == null, "Object must be null");
        }
    }
}
