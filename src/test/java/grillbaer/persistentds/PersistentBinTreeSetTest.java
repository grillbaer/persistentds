package grillbaer.persistentds;

import org.junit.Test;

import java.io.*;
import java.util.*;

import static org.junit.Assert.*;

public class PersistentBinTreeSetTest {

    @Test
    public void testEmpty() {
        PersistentSet<String> set = PersistentCollections
                .persistentBinTreeSet();
        assertTrue(set.isEmpty());
        assertEquals(0, set.size());

        assertFalse(set.contains(null));
        assertFalse(set.contains(""));
        assertFalse(set.contains("x"));

        assertFalse(set.iterator().hasNext());

        try {
            set.iterator().next();
            fail("Expected NoSuchElementException");
        } catch (NoSuchElementException e) {
        }
    }

    @Test
    public void testAdd() {
        PersistentSet<String> set0 = PersistentCollections
                .persistentBinTreeSet();
        PersistentSet<String> set1 = set0.add("0");
        PersistentSet<String> set2 = set1.add("1");
        PersistentSet<String> set3 = set2.add("2");
        PersistentSet<String> set4 = set3.add("1");

        assertEquals(0, set0.size());
        assertEquals(1, set1.size());
        assertEquals(2, set2.size());
        assertEquals(3, set3.size());
        assertEquals(3, set4.size());

        assertTrue(set3.contains("0"));
        assertTrue(set3.contains("1"));
        assertTrue(set3.contains("2"));

        assertTrue(set4.contains("0"));
        assertTrue(set4.contains("1"));
        assertTrue(set4.contains("2"));

        assertFalse(set2.contains("2"));
    }

    @Test
    public void testMultiplePutSameElement() {
        PersistentSet<String> set = PersistentCollections
                .persistentBinTreeSet();
        String x1 = new String("x");
        String x2 = new String("x");
        set = set.put(x1);
        assertSame(x1, set.get(x2));
        set = set.put(x2);
        assertSame(x2, set.get(x1));

        String y1 = new String("y");
        String y2 = new String("y");
        set = set.put(y1);
        assertSame(y1, set.get(y2));
        set = set.put(y2);
        assertSame(y2, set.get(y1));

        String v1 = new String("v");
        String v2 = new String("v");
        set = set.put(v1);
        assertSame(v1, set.get(v2));
        set = set.put(v2);
        assertSame(v2, set.get(v1));

        set = set.put(x1);
        assertSame(x1, set.get(x2));
        set = set.put(x2);
        assertSame(x2, set.get(x1));
    }

    @Test
    public void testMultipleAddSameElement() {
        PersistentSet<String> set = PersistentCollections
                .persistentBinTreeSet();
        String x1 = new String("x");
        String x2 = new String("x");
        set = set.add(x1);
        assertSame(x1, set.get(x2));
        set = set.add(x2);
        assertSame(x1, set.get(x2));

        String y1 = new String("y");
        String y2 = new String("y");
        set = set.add(y1);
        assertSame(y1, set.get(y2));
        set = set.add(y2);
        assertSame(y1, set.get(y2));

        String v1 = new String("v");
        String v2 = new String("v");
        set = set.add(v1);
        assertSame(v1, set.get(v2));
        set = set.add(v2);
        assertSame(v1, set.get(v2));

        set = set.add(x1);
        assertSame(x1, set.get(x2));
        set = set.add(x2);
        assertSame(x1, set.get(x2));
    }

    @Test
    public void testAddTailAndIterator() {
        PersistentSet<Integer> set = PersistentCollections
                .persistentBinTreeSet();
        for (int i = 0; i < 1000; i++) {
            set = set.add(i);
        }

        Iterator<Integer> iter = set.iterator();
        for (int i = 0; i < 1000; i++) {
            assertTrue(iter.hasNext());
            assertEquals(Integer.valueOf(i), iter.next());
        }
        assertFalse(iter.hasNext());

        try {
            iter.next();
            fail("Expected NoSuchElementException");
        } catch (NoSuchElementException e) {
        }
    }

    @Test
    public void testEmptyIterator() {
        Iterator<Integer> iter = PersistentCollections
                .<Integer>persistentBinTreeSet().iterator();
        assertFalse(iter.hasNext());
        try {
            iter.next();
            fail("Expected NoSuchElementException");
        } catch (NoSuchElementException e) {
        }
    }

    @Test
    public void testAddHeadAndIterator() {
        PersistentSet<Integer> set = PersistentCollections
                .persistentBinTreeSet();
        for (int i = 999; i >= 0; i--) {
            set = set.add(i);
        }

        Iterator<Integer> iter = set.iterator();
        for (int i = 0; i < 1000; i++) {
            assertTrue(iter.hasNext());
            assertEquals(Integer.valueOf(i), iter.next());
        }
        assertFalse(iter.hasNext());
    }

    @Test
    public void testAddHead() {
        PersistentSet<Integer> set = PersistentCollections
                .persistentBinTreeSet();
        set = set.add(2);
        set = set.add(1);
        set = set.add(0);
        assertEquals(Arrays.asList(0, 1, 2), set.toArrayList());
    }

    @Test
    public void testAddTail() {
        PersistentSet<Integer> set = PersistentCollections
                .persistentBinTreeSet();
        set = set.add(0);
        set = set.add(1);
        set = set.add(2);
        assertEquals(Arrays.asList(0, 1, 2), set.toArrayList());
    }

    @Test
    public void testMultipleAdd() {
        PersistentSet<Integer> set = PersistentCollections
                .persistentBinTreeSet();
        set = set.add(null);
        set = set.add(0);
        set = set.add(1);
        set = set.add(0);
        set = set.add(2);
        set = set.add(2);
        set = set.add(1);
        set = set.add(1);
        set = set.add(0);
        set = set.add(null);
        assertEquals(Arrays.asList(null, 0, 1, 2), set.toArrayList());
    }

    @Test
    public void testHashCodeAndEquals() {
        PersistentSet<Integer> set1 = PersistentCollections
                .persistentBinTreeSet();
        PersistentSet<Integer> set2 = PersistentCollections
                .persistentBinTreeSet();
        PersistentSet<Integer> set3 = PersistentCollections
                .persistentBinTreeSet();
        PersistentSet<Integer> set4 = PersistentCollections
                .persistentBinTreeSet();
        for (int i = 0; i < 10; i++) {
            set2 = set2.add(i);
            set3 = set3.add(i);
            set4 = set4.add(i);
        }
        for (int i = 0; i < 10; i++) {
            set3 = set3.add(i);
            set4 = set4.add(i);
        }

        assertNotEquals(null, set1);
        assertNotEquals(null, set2);
        assertNotEquals(null, set3);
        assertNotEquals(null, set4);

        assertNotEquals(set1, null);
        assertNotEquals(set2, null);
        assertNotEquals(set3, null);
        assertNotEquals(set4, null);

        assertNotEquals(new Object(), set1);
        assertNotEquals(Integer.valueOf(10), set4);

        assertNotEquals(set1, new Object());
        assertNotEquals(set4, Integer.valueOf(10));

        assertEquals(set1, set1);
        assertEquals(set2, set2);
        assertEquals(set3, set3);
        assertEquals(set4, set4);

        assertEquals(set3, set4);
        assertEquals(set3.hashCode(), set4.hashCode());
        assertEquals(set3.add(null).hashCode(), set4.add(null).hashCode());

        assertNotEquals(set1, set2);
        assertNotEquals(set1.hashCode(), set2.hashCode());

        assertEquals(set2, set3);
        assertEquals(set2.hashCode(), set3.hashCode());

        assertNotEquals(set3, set3.remove(5));
        assertEquals(set3, set3.add(5));
        assertEquals(set3, set3.remove(5).add(5));

        assertEquals(set3, set4);
        assertEquals(set4, set3);
        assertNotEquals(set4, set3.remove(5).add(100));
    }

    @Test
    public void testHashCodeAndEqualsWithHashSet() {
        PersistentSet<Integer> hashSet = PersistentCollections
                .persistentHashSet();
        PersistentSet<Integer> binTreeSet = PersistentCollections
                .persistentBinTreeSet();

        for (int i = -10; i < 100; i++) {
            hashSet = hashSet.add(i);
            binTreeSet = binTreeSet.add(i);
        }

        assertEquals(binTreeSet, hashSet);
        assertEquals(binTreeSet.hashCode(), hashSet.hashCode());
    }

    @Test
    public void testAddHeadAndTail() {
        PersistentSet<Integer> set = PersistentCollections
                .persistentBinTreeSet();
        for (int i = 10; i < 20; i++) {
            set = set.add(i);
        }
        for (int i = 0; i < 10; i++) {
            set = set.add(i);
        }
        assertEquals(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12,
                13, 14, 15, 16, 17, 18, 19), set.toArrayList());

        System.err.println(set.size() + " "
                + ((PersistentBinTreeSet<Integer>) set).depth());
    }

    @Test
    public void testAddCenter1() {
        PersistentSet<Integer> set = PersistentCollections
                .persistentBinTreeSet();
        set = set.add(0);
        set = set.add(2);
        set = set.add(1);
        assertEquals(Arrays.asList(0, 1, 2), set.toArrayList());
    }

    @Test
    public void testAddCenter2() {
        PersistentSet<Integer> set = PersistentCollections
                .persistentBinTreeSet();
        set = set.add(0);
        set = set.add(1);
        set = set.add(3);
        set = set.add(2);

        assertEquals(Arrays.asList(0, 1, 2, 3), set.toArrayList());
    }

    @Test
    public void testAddCenter3() {
        PersistentSet<Integer> set = PersistentCollections
                .persistentBinTreeSet();
        set = set.add(0);
        set = set.add(2);
        set = set.add(3);
        set = set.add(1);

        assertEquals(Arrays.asList(0, 1, 2, 3), set.toArrayList());
    }

    @Test
    public void testAddInRightPart() {
        PersistentSet<Integer> set = PersistentCollections
                .persistentBinTreeSet();
        for (int i = 0; i < 20; i += 2) {
            set = set.add(i);
        }

        set = set.add(15);

        assertEquals(Arrays.asList(0, 2, 4, 6, 8, 10, 12, 14, 15, 16, 18),
                set.toArrayList());
    }

    @Test
    public void testAddInLeftPart() {
        PersistentSet<Integer> set = PersistentCollections
                .persistentBinTreeSet();
        for (int i = 0; i < 20; i += 2) {
            set = set.add(i);
        }

        set = set.add(5);

        assertEquals(Arrays.asList(0, 2, 4, 5, 6, 8, 10, 12, 14, 16, 18),
                set.toArrayList());
    }

    @Test
    public void testRandomAdds() {
        PersistentSet<Integer> set = PersistentCollections
                .persistentBinTreeSet();
        Set<Integer> reference = new TreeSet<>();

        int j = 0;
        for (int i = 0; i < 4000; i++) {
            j = (101 * j + 13) % 2000;
            Integer jInteger = Integer.valueOf(j);
            reference.add(jInteger);
            set = set.add(jInteger);
        }

        assertEquals(reference.size(), set.size());

        Iterator<Integer> iter = set.iterator();
        for (Integer element : reference) {
            assertTrue(iter.hasNext());
            assertEquals(element, iter.next());
        }
        assertFalse(iter.hasNext());

        System.err.println(set.size() + " "
                + ((PersistentBinTreeSet<Integer>) set).depth());
    }

    @Test
    public void testRandomPuts() {
        PersistentSet<Integer> set = PersistentCollections
                .persistentBinTreeSet();
        Set<Integer> reference = new TreeSet<>();

        int j = 0;
        for (int i = 0; i < 4000; i++) {
            j = (101 * j + 13) % 2000;
            Integer jInteger = Integer.valueOf(j);
            reference.add(jInteger);
            set = set.add(jInteger);
        }

        assertEquals(reference.size(), set.size());

        Iterator<Integer> iter = set.iterator();
        for (Integer element : reference) {
            assertTrue(iter.hasNext());
            assertSame(element, iter.next());
        }
        assertFalse(iter.hasNext());

        System.err.println(set.size() + " "
                + ((PersistentBinTreeSet<Integer>) set).depth());
    }

    @Test
    public void testRandomAddsAndRemoves() {
        PersistentSet<Integer> set = PersistentCollections
                .persistentBinTreeSet();
        Set<Integer> reference = new TreeSet<>();

        int j = 0;
        int k = 0;
        for (int i = 0; i < 10000; i++) {
            j = (101 * j + 13) % 2000;
            Integer jInteger = Integer.valueOf(j);
            reference.add(jInteger);
            set = set.add(jInteger);

            if (i % 2 == 1) {
                k = (151 * k + 7) % 2000;
                Integer kInteger = Integer.valueOf(k);
                reference.remove(kInteger);
                set = set.remove(kInteger);
            }
        }

        assertEquals(reference.size(), set.size());

        Iterator<Integer> iter = set.iterator();
        for (Integer element : reference) {
            assertTrue(iter.hasNext());
            assertEquals(element, iter.next());
        }
        assertFalse(iter.hasNext());
    }

    @Test
    public void testRandomPutsAndRemoves() {
        PersistentSet<Integer> set = PersistentCollections
                .persistentBinTreeSet();
        Set<Integer> reference = new TreeSet<>();

        int j = 0;
        int k = 0;
        for (int i = 0; i < 10000; i++) {
            j = (101 * j + 13) % 2000;
            Integer jInteger = Integer.valueOf(j);
            reference.add(jInteger);
            set = set.add(jInteger);

            if (i % 2 == 1) {
                k = (151 * k + 7) % 2000;
                Integer kInteger = Integer.valueOf(k);
                reference.remove(kInteger);
                set = set.remove(kInteger);
            }
        }

        assertEquals(reference.size(), set.size());

        Iterator<Integer> iter = set.iterator();
        for (Integer element : reference) {
            assertTrue(iter.hasNext());
            assertSame(element, iter.next());
        }
        assertFalse(iter.hasNext());
    }

    // System.err.println(list.size() + " "
    // + ((PersistentBinTreeSet<Integer>) list).depth());
    // }
    //
    // private int performanceSamples = 500_000;
    //
    // @Test
    // public void performanceRandomInsertAndRemovePersistentTreeSet() {
    // PersistentSet<Integer> list = PersistentCollections
    // .createBinTreeSet();
    //
    // int j = 0;
    // int k = 0;
    // for (int i = 0; i < performanceSamples; i++) {
    // j = i > 0 ? (101 * j + 13) % list.size() : 0;
    // list = list.add(j, i);
    //
    // if (i % 2 == 1) {
    // k = i > 0 ? (151 * k + 7) % list.size() : 0;
    // list = list.remove(k);
    // }
    // }
    // }
    //
    // @Test
    // public void performanceRandomInsertAndRemoveArraySet() {
    // Set<Integer> list = new ArraySet<>();
    //
    // int j = 0;
    // int k = 0;
    // for (int i = 0; i < performanceSamples; i++) {
    // j = i > 0 ? (101 * j + 13) % list.size() : 0;
    // list.add(j, i);
    //
    // if (i % 2 == 1) {
    // k = i > 0 ? (151 * k + 7) % list.size() : 0;
    // list.remove(k);
    // }
    // }
    // }
    //
    // @Test
    // public void performanceAndTailPersistentTreeSet() {
    // PersistentSet<Integer> list = PersistentCollections
    // .createBinTreeSet();
    //
    // for (int i = 0; i < performanceSamples; i++) {
    // list = list.add(10);
    // }
    // }
    //
    // @Test
    // public void performanceAddTailArraySet() {
    // Set<Integer> list = new ArraySet<>();
    //
    // for (int i = 0; i < performanceSamples; i++) {
    // list.add(10);
    // }
    // }
    //
    // @Test
    // public void testAddAll() {
    // Set<Integer> elements = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    // PersistentSet<Integer> list = PersistentCollections
    // .<Integer> createBinTreeSet().addAll(elements);
    // assertEquals(elements, list.toArrayList());
    // }

    @Test
    public void testRemove() {
        PersistentSet<Integer> set = PersistentCollections
                .<Integer>persistentBinTreeSet()
                .addAll(Arrays.asList(0, 1, 2));

        assertEquals(Arrays.asList(0, 1), set.remove(2).toArrayList());
        assertEquals(Arrays.asList(0), set.remove(2).remove(1).toArrayList());
        assertEquals(Arrays.asList(), set.remove(2).remove(1).remove(0)
                .toArrayList());

        assertEquals(Arrays.asList(1), set.remove(2).remove(0).toArrayList());
        assertEquals(Arrays.asList(), set.remove(2).remove(0).remove(1)
                .toArrayList());

        assertEquals(Arrays.asList(0, 2), set.remove(1).toArrayList());
        assertEquals(Arrays.asList(0), set.remove(1).remove(2).toArrayList());
        assertEquals(Arrays.asList(2), set.remove(1).remove(0).toArrayList());

        assertEquals(Arrays.asList(1, 2), set.remove(0).toArrayList());
        assertEquals(Arrays.asList(1), set.remove(0).remove(2).toArrayList());
        assertEquals(Arrays.asList(2), set.remove(0).remove(1).toArrayList());
    }

    @Test
    public void testAddAndRemove() {
        PersistentSet<Integer> set = PersistentCollections
                .<Integer>persistentBinTreeSet();
        for (int i = 0; i < 4; i++) {
            set = set.add(i);
        }
        for (int i = 0; i < 4; i++) {
            set = set.remove(i);
        }

        assertEquals(0, set.size());
    }

    @Test
    public void testRemoveHead() {
        PersistentSet<Integer> set = PersistentCollections
                .persistentBinTreeSet();
        for (int i = 0; i < 20; i++) {
            set = set.add(i);
        }

        for (int i = 0; i < 20; i++) {
            set = set.remove(i);
        }

        assertEquals(0, set.size());
    }

    @Test
    public void testRemoveTail() {
        PersistentSet<Integer> set = PersistentCollections
                .persistentBinTreeSet();
        for (int i = 0; i < 20; i++) {
            set = set.add(i);
        }

        for (int i = 19; i >= 0; i--) {
            set = set.remove(i);
        }

        assertEquals(0, set.size());
    }

    @Test
    public void testToString() {
        PersistentSet<Integer> list = PersistentCollections
                .persistentBinTreeSet();
        assertEquals("{}", list.toString());

        for (int i = 0; i < 5; i++) {
            list = list.add(i);
        }

        assertEquals("{0,1,2,3,4}", list.toString());
    }

    @Test
    public void testSerialization() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);

        PersistentSet<Integer> set = PersistentCollections
                .<Integer>persistentBinTreeSet().addAll(
                        Arrays.<Integer>asList(1, 2, 3, 4, 5, 6, 7, 8, 9,
                                null, 0));
        oos.writeObject(set);

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        @SuppressWarnings("unchecked")
        PersistentSet<Integer> deserialized = (PersistentSet<Integer>) ois
                .readObject();

        assertEquals(set, deserialized);
        assertEquals(Arrays.asList(null, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
                deserialized.toArrayList());
    }
}
