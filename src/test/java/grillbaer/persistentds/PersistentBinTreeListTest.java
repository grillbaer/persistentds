package grillbaer.persistentds;

import org.junit.Test;

import java.io.*;
import java.util.*;

import static org.junit.Assert.*;

public class PersistentBinTreeListTest {

    @Test
    public void testEmpty() {
        PersistentList<String> list = PersistentCollections
                .persistentBinTreeList();
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());

        try {
            list.get(0);
            fail("Expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
        }

        assertFalse(list.iterator().hasNext());

        try {
            list.iterator().next();
            fail("Expected NoSuchElementException");
        } catch (NoSuchElementException e) {
        }

        try {
            list.remove(0);
            fail("Expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
        }
    }

    @Test
    public void testAdd() {
        PersistentList<String> list0 = PersistentCollections
                .persistentBinTreeList();
        PersistentList<String> list1 = list0.add("0");
        PersistentList<String> list2 = list1.add("1");
        PersistentList<String> list3 = list2.add("2");

        assertEquals(0, list0.size());
        assertEquals(1, list1.size());
        assertEquals(2, list2.size());
        assertEquals(3, list3.size());

        assertEquals("0", list3.get(0));
        assertEquals("1", list3.get(1));
        assertEquals("2", list3.get(2));
        try {
            list3.get(3);
            fail("Expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
        }
    }

    @Test
    public void testAddTailAndIterator() {
        PersistentList<Integer> list = PersistentCollections
                .persistentBinTreeList();
        for (int i = 0; i < 1000; i++) {
            list = list.add(i);
        }

        Iterator<Integer> iter = list.iterator();
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
    public void testAddHead() {
        PersistentList<Integer> list = PersistentCollections
                .persistentBinTreeList();
        list = list.add(0, 2);
        list = list.add(0, 1);
        list = list.add(0, 0);
        assertEquals(Arrays.asList(0, 1, 2), list.toArrayList());
    }

    @Test
    public void testAddTail() {
        PersistentList<Integer> list = PersistentCollections
                .persistentBinTreeList();
        list = list.add(0);
        list = list.add(1);
        list = list.add(2);
        assertEquals(Arrays.asList(0, 1, 2), list.toArrayList());
    }

    @Test
    public void testHashCodeAndEquals() {
        PersistentList<Integer> list1 = PersistentCollections
                .persistentBinTreeList();
        PersistentList<Integer> list2 = PersistentCollections
                .persistentBinTreeList();
        PersistentList<Integer> list3 = PersistentCollections
                .persistentBinTreeList();
        PersistentList<Integer> list4 = PersistentCollections
                .persistentBinTreeList();
        for (int i = 0; i < 10; i++) {
            list2 = list2.add(i);
            list3 = list3.add(i);
            list4 = list4.add(i);
        }
        for (int i = 0; i < 10; i++) {
            list3 = list3.add(i);
            list4 = list4.add(i);
        }

        assertNotEquals(null, list1);
        assertNotEquals(null, list2);
        assertNotEquals(null, list3);
        assertNotEquals(null, list4);

        assertNotEquals(list1, null);
        assertNotEquals(list2, null);
        assertNotEquals(list3, null);
        assertNotEquals(list4, null);

        assertNotEquals(new Object(), list1);
        assertNotEquals(Integer.valueOf(10), list4);

        assertNotEquals(list1, new Object());
        assertNotEquals(list4, Integer.valueOf(10));

        assertEquals(list1, list1);
        assertEquals(list2, list2);
        assertEquals(list3, list3);
        assertEquals(list4, list4);

        assertNotEquals(list4, list4.remove(0).add(0, 1000));

        assertEquals(list3, list4);
        assertEquals(list3.hashCode(), list4.hashCode());
        assertEquals(list3.add(null).hashCode(), list4.add(null).hashCode());

        assertEquals(list3, list4);
        assertEquals(list3.hashCode(), list4.hashCode());

        assertNotEquals(list1, list2);
        assertNotEquals(list1.hashCode(), list2.hashCode());

        assertNotEquals(list2, list3);
        assertNotEquals(list2.hashCode(), list3.hashCode());

        assertNotEquals(list3, list3.remove(5));
        assertNotEquals(list3, list3.add(5, 5));
        assertEquals(list3, list3.remove(5).add(5, 5));
    }

    @Test
    public void testAddHeadAndTail() {
        PersistentList<Integer> list = PersistentCollections
                .persistentBinTreeList();
        for (int i = 10; i < 20; i++) {
            list = list.add(i);
        }
        for (int i = 0; i < 10; i++) {
            list = list.add(i, i);
        }
        assertEquals(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12,
                13, 14, 15, 16, 17, 18, 19), list.toArrayList());

        System.err.println(list.size() + " "
                + ((PersistentBinTreeList<Integer>) list).depth());
    }

    @Test
    public void testAddCenter1() {
        PersistentList<Integer> list = PersistentCollections
                .persistentBinTreeList();
        list = list.add(0);
        list = list.add(2);
        list = list.add(1, 1);
        assertEquals(Arrays.asList(0, 1, 2), list.toArrayList());
    }

    @Test
    public void testAddCenter2() {
        PersistentList<Integer> list = PersistentCollections
                .persistentBinTreeList();
        list = list.add(0);
        list = list.add(1);
        list = list.add(3);
        list = list.add(2, 2);

        assertEquals(Arrays.asList(0, 1, 2, 3), list.toArrayList());

        assertEquals(Integer.valueOf(0), list.get(0));
        assertEquals(Integer.valueOf(1), list.get(1));
        assertEquals(Integer.valueOf(2), list.get(2));
        assertEquals(Integer.valueOf(3), list.get(3));
    }

    @Test
    public void testAddCenter3() {
        PersistentList<Integer> list = PersistentCollections
                .persistentBinTreeList();
        list = list.add(0);
        list = list.add(2);
        list = list.add(3);
        list = list.add(1, 1);

        assertEquals(Arrays.asList(0, 1, 2, 3), list.toArrayList());

        assertEquals(Integer.valueOf(0), list.get(0));
        assertEquals(Integer.valueOf(1), list.get(1));
        assertEquals(Integer.valueOf(2), list.get(2));
        assertEquals(Integer.valueOf(3), list.get(3));
    }

    @Test
    public void testAddInRightPart() {
        PersistentList<Integer> list = PersistentCollections
                .persistentBinTreeList();
        for (int i = 0; i < 10; i++) {
            list = list.add(i);
        }

        list = list.add(8, 100);

        assertEquals(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 100, 8, 9),
                list.toArrayList());
    }

    @Test
    public void testAddInLeftPart() {
        PersistentList<Integer> list = PersistentCollections
                .persistentBinTreeList();
        for (int i = 0; i < 10; i++) {
            list = list.add(i);
        }

        list = list.add(3, 100);

        assertEquals(Arrays.asList(0, 1, 2, 100, 3, 4, 5, 6, 7, 8, 9),
                list.toArrayList());
    }

    @Test
    public void testRandomAdds() {
        PersistentList<Integer> list = PersistentCollections
                .persistentBinTreeList();
        List<Integer> reference = new ArrayList<>();

        int j = 0;
        for (int i = 0; i < 4000; i++) {
            j = i > 0 ? (101 * j + 13) % i : 0;
            reference.add(j, i);
            list = list.add(j, i);
        }

        assertEquals(reference.size(), list.size());

        for (int i = 0; i < reference.size(); i++) {
            assertEquals(reference.get(i), list.get(i));
        }

        System.err.println(list.size() + " "
                + ((PersistentBinTreeList<Integer>) list).depth());
    }

    @Test
    public void testRandomAddsAndRemoves() {
        PersistentList<Integer> list = PersistentCollections
                .persistentBinTreeList();
        List<Integer> reference = new ArrayList<>();

        int j = 0;
        int k = 0;
        for (int i = 0; i < 10000; i++) {
            j = i > 0 ? (101 * j + 13) % reference.size() : 0;
            reference.add(j, i);
            list = list.add(j, i);

            if (i % 2 == 1) {
                k = i > 0 ? (151 * k + 7) % reference.size() : 0;
                reference.remove(k);
                list = list.remove(k);
            }
        }

        assertEquals(reference.size(), list.size());

        for (int i = 0; i < reference.size(); i++) {
            assertEquals(reference.get(i), list.get(i));
        }

        System.err.println(list.size() + " "
                + ((PersistentBinTreeList<Integer>) list).depth());
    }

    private int performanceSamples = 5_000;

    @Test
    public void performanceRandomInsertAndRemovePersistentTreeList() {
        PersistentList<Integer> list = PersistentCollections
                .persistentBinTreeList();

        int j = 0;
        int k = 0;
        for (int i = 0; i < performanceSamples; i++) {
            j = i > 0 ? (101 * j + 13) % list.size() : 0;
            list = list.add(j, i);

            if (i % 2 == 1) {
                k = i > 0 ? (151 * k + 7) % list.size() : 0;
                list = list.remove(k);
            }
        }
    }

    @Test
    public void performanceRandomInsertAndRemoveArrayList() {
        List<Integer> list = new ArrayList<>();

        int j = 0;
        int k = 0;
        for (int i = 0; i < performanceSamples; i++) {
            j = i > 0 ? (101 * j + 13) % list.size() : 0;
            list.add(j, i);

            if (i % 2 == 1) {
                k = i > 0 ? (151 * k + 7) % list.size() : 0;
                list.remove(k);
            }
        }
    }

    @Test
    public void performanceAndTailPersistentTreeList() {
        PersistentList<Integer> list = PersistentCollections
                .persistentBinTreeList();

        for (int i = 0; i < performanceSamples; i++) {
            list = list.add(10);
        }
    }

    @Test
    public void performanceAddTailArrayList() {
        List<Integer> list = new ArrayList<>();

        for (int i = 0; i < performanceSamples; i++) {
            list.add(10);
        }
    }

    @Test
    public void testAddAll() {
        List<Integer> elements = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        PersistentList<Integer> list = PersistentCollections
                .<Integer>persistentBinTreeList().addAll(elements);
        assertEquals(elements, list.toArrayList());
    }

    @Test
    public void testIndexOf() {
        PersistentList<Integer> list = PersistentCollections
                .<Integer>persistentBinTreeList().addAll(
                        Arrays.asList(0, 1, 2, 3, 4, 5, null, 7, 8, 9, 0, 1, 2,
                                3, 4, 5, 6, 7, null, 9, 100));
        assertEquals(0, list.indexOf(0));
        assertEquals(1, list.indexOf(1));
        assertEquals(4, list.indexOf(4));
        assertEquals(6, list.indexOf(null));
        assertEquals(9, list.indexOf(9));
        assertEquals(20, list.indexOf(100));
        assertEquals(-1, list.indexOf(101));

        list = PersistentCollections.<Integer>persistentBinTreeList().addAll(
                Arrays.asList(0, 1, 2));
        assertEquals(0, list.indexOf(0));
        assertEquals(1, list.indexOf(1));
        assertEquals(2, list.indexOf(2));
        assertEquals(-1, list.indexOf(3));
        assertEquals(-1, list.indexOf(null));

        list = PersistentCollections.<Integer>persistentBinTreeList().add(0)
                .add(1);
        assertEquals(0, list.indexOf(0));
        assertEquals(1, list.indexOf(1));
        assertEquals(-1, list.indexOf(2));

        list = PersistentCollections.<Integer>persistentBinTreeList().add(10);
        assertEquals(0, list.indexOf(10));
        assertEquals(-1, list.indexOf(-1));

        list = PersistentCollections.<Integer>persistentBinTreeList().add(10)
                .add(10);
        assertEquals(0, list.indexOf(10));
        assertEquals(-1, list.indexOf(-1));

        list = PersistentCollections.<Integer>persistentBinTreeList();
        assertEquals(-1, list.indexOf(0));
        assertEquals(-1, list.indexOf(null));

        list = PersistentCollections.<Integer>persistentBinTreeList()
                .add(null);
        assertEquals(-1, list.indexOf(0));
        assertEquals(0, list.indexOf(null));
    }

    @Test
    public void testLastIndexOf() {
        PersistentList<Integer> list = PersistentCollections
                .<Integer>persistentBinTreeList().addAll(
                        Arrays.asList(0, 1, 2, 3, 4, 5, null, 7, 8, 9, 0, 1, 2,
                                3, 4, 5, 6, 7, null, 9, 100));
        assertEquals(10, list.lastIndexOf(0));
        assertEquals(11, list.lastIndexOf(1));
        assertEquals(14, list.lastIndexOf(4));
        assertEquals(18, list.lastIndexOf(null));
        assertEquals(19, list.lastIndexOf(9));
        assertEquals(20, list.lastIndexOf(100));
        assertEquals(-1, list.lastIndexOf(101));

        list = PersistentCollections.<Integer>persistentBinTreeList().addAll(
                Arrays.asList(0, 1, 2));
        assertEquals(0, list.lastIndexOf(0));
        assertEquals(1, list.lastIndexOf(1));
        assertEquals(2, list.lastIndexOf(2));
        assertEquals(-1, list.lastIndexOf(3));
        assertEquals(-1, list.lastIndexOf(null));

        list = PersistentCollections.<Integer>persistentBinTreeList().add(0)
                .add(1);
        assertEquals(0, list.lastIndexOf(0));
        assertEquals(1, list.lastIndexOf(1));
        assertEquals(-1, list.lastIndexOf(2));

        list = PersistentCollections.<Integer>persistentBinTreeList().add(10);
        assertEquals(0, list.lastIndexOf(10));
        assertEquals(-1, list.lastIndexOf(-1));

        list = PersistentCollections.<Integer>persistentBinTreeList().add(10)
                .add(10);
        assertEquals(1, list.lastIndexOf(10));
        assertEquals(-1, list.lastIndexOf(-1));

        list = PersistentCollections.<Integer>persistentBinTreeList();
        assertEquals(-1, list.lastIndexOf(0));
        assertEquals(-1, list.lastIndexOf(null));

        list = PersistentCollections.<Integer>persistentBinTreeList()
                .add(null);
        assertEquals(-1, list.lastIndexOf(0));
        assertEquals(0, list.lastIndexOf(null));
    }

    @Test
    public void testRemove() {
        PersistentList<Integer> list = PersistentCollections
                .<Integer>persistentBinTreeList().addAll(
                        Arrays.asList(0, 1, 2));

        assertEquals(Arrays.asList(0, 1), list.remove(2).toArrayList());
        assertEquals(Arrays.asList(0), list.remove(2).remove(1).toArrayList());
        assertEquals(Arrays.asList(), list.remove(2).remove(1).remove(0)
                .toArrayList());

        assertEquals(Arrays.asList(1), list.remove(2).remove(0).toArrayList());
        assertEquals(Arrays.asList(), list.remove(2).remove(0).remove(0)
                .toArrayList());

        assertEquals(Arrays.asList(0, 2), list.remove(1).toArrayList());
        assertEquals(Arrays.asList(0), list.remove(1).remove(1).toArrayList());
        assertEquals(Arrays.asList(2), list.remove(1).remove(0).toArrayList());

        assertEquals(Arrays.asList(1, 2), list.remove(0).toArrayList());
        assertEquals(Arrays.asList(1), list.remove(0).remove(1).toArrayList());
        assertEquals(Arrays.asList(2), list.remove(0).remove(0).toArrayList());

        try {
            list.remove(3);
            fail("Expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
        }
    }

    @Test
    public void testRemoveHead() {
        PersistentList<Integer> list = PersistentCollections
                .persistentBinTreeList();
        for (int i = 0; i < 20; i++) {
            list = list.add(i);
        }

        for (int i = 0; i < 20; i++) {
            list = list.remove(0);
        }

        assertEquals(0, list.size());
    }

    @Test
    public void testRemoveTail() {
        PersistentList<Integer> list = PersistentCollections
                .persistentBinTreeList();
        for (int i = 0; i < 20; i++) {
            list = list.add(i);
        }

        for (int i = 19; i >= 0; i--) {
            list = list.remove(i);
        }

        assertEquals(0, list.size());
    }

    @Test
    public void testToString() {
        PersistentList<Integer> list = PersistentCollections
                .persistentBinTreeList();
        assertEquals("{}", list.toString());

        for (int i = 0; i < 5; i++) {
            list = list.add(i);
        }

        assertEquals("{0,1,2,3,4}", list.toString());
    }

    @Test
    public void testAddHeadAndIterator() {
        PersistentList<Integer> list = PersistentCollections
                .persistentBinTreeList();
        for (int i = 0; i < 1000; i++) {
            list = list.add(0, i);
        }

        Iterator<Integer> iter = list.iterator();
        for (int i = 999; i >= 0; i--) {
            assertTrue(iter.hasNext());
            assertEquals(Integer.valueOf(i), iter.next());
        }
        assertFalse(iter.hasNext());
    }

    @Test
    public void testEmptyIterator() {
        Iterator<Integer> iter = PersistentCollections
                .<Integer>persistentBinTreeList().iterator();
        assertFalse(iter.hasNext());
        try {
            iter.next();
            fail("Expected NoSuchElementException");
        } catch (NoSuchElementException e) {
        }
    }

    @Test
    public void testSerialization() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);

        PersistentList<Integer> list = PersistentCollections
                .<Integer>persistentBinTreeList().addAll(
                        Arrays.<Integer>asList(1, 2, 3, 4, 5, 6, 7, 8, 9,
                                null, 0));
        oos.writeObject(list);

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        @SuppressWarnings("unchecked")
        PersistentList<Integer> deserialized = (PersistentList<Integer>) ois
                .readObject();

        assertEquals(list, deserialized);
        assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, null, 0),
                deserialized.toArrayList());
    }
}
