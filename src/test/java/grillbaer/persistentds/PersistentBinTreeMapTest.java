package grillbaer.persistentds;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

import grillbaer.persistentds.PersistentBinTreeMap;
import grillbaer.persistentds.PersistentCollections;
import grillbaer.persistentds.PersistentMap;

public class PersistentBinTreeMapTest {

	@Test
	public void testPutAndGetAndContainsKey() {
		PersistentMap<Integer, String> map = PersistentCollections
				.persistentBinTreeMap();
		map = map.put(1, "1");
		map = map.put(10, "10");
		map = map.put(-6, "-6");
		map = map.put(-3, "-3");

		assertEquals("1", map.get(1));
		assertTrue(map.containsKey(1));

		assertEquals("10", map.get(10));
		assertTrue(map.containsKey(10));

		assertEquals("-6", map.get(-6));
		assertTrue(map.containsKey(new Integer(-6)));

		assertEquals("-3", map.get(-3));
		assertTrue(map.containsKey(-3));

		assertEquals(null, map.get(2));
		assertFalse(map.containsKey(2));

		assertEquals(null, map.get(null));
		assertFalse(map.containsKey(null));

		map = map.put(null, "null");
		assertEquals("null", map.get(null));
		assertTrue(map.containsKey(null));

		map = map.put(1, "eins");
		assertEquals("eins", map.get(1));

		map = map.put(1, "eins");
		assertEquals("eins", map.get(1));

		map = map.remove(1);
		assertEquals(null, map.get(1));
		assertFalse(map.containsKey(1));
	}

	@Test
	public void testIsEmptyAndSize() {
		PersistentMap<String, String> map0 = PersistentCollections
				.persistentBinTreeMap();
		assertTrue(map0.isEmpty());
		assertEquals(0, map0.size());

		PersistentMap<String, String> map3 = map0.put("c", "C").put("a", "A")
				.put("b", "B");
		assertTrue(map0.isEmpty());
		assertEquals(0, map0.size());
		assertFalse(map3.isEmpty());
		assertEquals(3, map3.size());
	}

	@Test
	public void testRandomPutsAndRemoves() {
		PersistentMap<Integer, String> map = PersistentCollections
				.persistentBinTreeMap();
		Map<Integer, String> reference = new TreeMap<>();

		int j = 0;
		int k = 0;
		for (int i = 0; i < 10000; i++) {
			j = (101 * j + 13) % 8000;
			reference.put(j, String.valueOf(i));
			map = map.put(j, String.valueOf(i));

			if (i % 2 == 1) {
				k = (151 * k + 7) % 8000;
				reference.remove(k);
				map = map.remove(k);
			}
		}

		assertEquals(reference.size(), map.size());

		for (int i = 0; i < 8000; i++) {
			assertEquals(reference.get(i), map.get(i));
		}

		System.err.println(map.size() + " "
				+ ((PersistentBinTreeMap<Integer, String>) map).depth());
	}

	@Test
	public void testSerialization() throws IOException, ClassNotFoundException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);

		PersistentMap<Integer, String> map = PersistentCollections
				.<Integer, String> persistentBinTreeMap().put(0, "0")
				.put(1, "1").put(2, "2").put(null, "null").put(-1, null);
		oos.writeObject(map);

		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
				baos.toByteArray()));
		@SuppressWarnings("unchecked")
		PersistentMap<Integer, String> deserialized = (PersistentMap<Integer, String>) ois
				.readObject();

		assertEquals(map, deserialized);
	}

	@Test
	public void testHashCodeAndEquals() {
		PersistentMap<String, String> map0 = PersistentCollections
				.persistentBinTreeMap();
		PersistentMap<String, String> map1 = PersistentCollections
				.persistentBinTreeMap();

		assertEquals(map0, map1);
		assertEquals(map0.hashCode(), map1.hashCode());

		map0 = map0.put("a", "A");
		map1 = map1.put("b", "B");

		assertFalse(map0.equals(map1));
		assertFalse(map1.equals(map0));
		assertEquals(map0, map0);
		assertFalse(map0.equals(null));
		assertFalse(map0.equals(new Object()));
		assertFalse(map0.equals(map1.put("x", "Y")));

		PersistentMap.Entry<String, String> entry0 = map0.entrySet().iterator()
				.next();
		PersistentMap.Entry<String, String> entry1 = map1.entrySet().iterator()
				.next();

		assertFalse(entry0.equals(entry1));
		assertFalse(entry1.equals(entry0));
		assertEquals(entry0, entry0);
		assertFalse(entry0.equals(null));
		assertFalse(entry0.equals(new Object()));

		assertFalse(entry0.equals(PersistentCollections
				.<String, String> persistentBinTreeMap().put(null, "X")
				.iterator().next()));
		assertTrue(PersistentCollections
				.persistentBinTreeMap()
				.put(null, "X")
				.iterator()
				.next()
				.equals(PersistentCollections
						.<String, String> persistentBinTreeMap().put(null, "X")
						.iterator().next()));
		assertFalse(PersistentCollections
				.persistentBinTreeMap()
				.put(null, "X")
				.iterator()
				.next()
				.equals(PersistentCollections
						.<String, String> persistentBinTreeMap().put("y", null)
						.iterator().next()));
		assertFalse(PersistentCollections
				.persistentBinTreeMap()
				.put(null, "X")
				.iterator()
				.next()
				.equals(PersistentCollections
						.<String, String> persistentBinTreeMap().put(null, "Y")
						.iterator().next()));

		assertFalse(entry0.equals(PersistentCollections
				.<String, String> persistentBinTreeMap().put("x", null)
				.iterator().next()));
		assertTrue(PersistentCollections
				.<String, String> persistentBinTreeMap()
				.put("x", null)
				.iterator()
				.next()
				.equals(PersistentCollections
						.<String, String> persistentBinTreeMap().put("x", null)
						.iterator().next()));

		assertFalse(entry0.equals(PersistentCollections
				.<String, String> persistentBinTreeMap().put(null, null)
				.iterator().next()));
		assertFalse(PersistentCollections
				.<String, String> persistentBinTreeMap().put(null, null)
				.iterator().next().equals(entry0));
		assertTrue(PersistentCollections
				.<String, String> persistentBinTreeMap()
				.put(null, null)
				.iterator()
				.next()
				.equals(PersistentCollections
						.<String, String> persistentBinTreeMap()
						.put(null, null).iterator().next()));
		assertFalse(PersistentCollections
				.<String, String> persistentBinTreeMap()
				.put("x", null)
				.iterator()
				.next()
				.equals(PersistentCollections
						.<String, String> persistentBinTreeMap().put("x", "Y")
						.iterator().next()));

		assertEquals("a".hashCode() ^ "A".hashCode(), map0.hashCode());
		assertEquals("a".hashCode() ^ "A".hashCode(), map0.put(null, null)
				.hashCode());

		map0 = map0.put("b", "B");
		map1 = map1.put("a", "A");
		assertEquals(map0, map0);
		assertFalse(map0.equals(null));
		assertFalse(map0.equals(new Object()));
		assertFalse(map0.equals(map1.put("c", "C")));
		assertFalse(map0.equals(map1.put("a", "AAAA")));
		assertTrue(map0.equals(map1));
		assertTrue(map1.equals(map0));

		assertEquals(
				("a".hashCode() ^ "A".hashCode())
						+ ("b".hashCode() ^ "B".hashCode()), map0.hashCode());

		for (int i = 0; i <= 20; i++) {
			map0 = map0.put("k" + i, "v" + i);
			map1 = map1.put("k" + (20 - i), "v" + (20 - i));
		}

		assertEquals(map0, map1);
		assertEquals(map0.hashCode(), map1.hashCode());
	}
}
