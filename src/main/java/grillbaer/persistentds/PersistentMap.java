package grillbaer.persistentds;

import java.io.Serializable;

public interface PersistentMap<K, V> extends
		Iterable<PersistentMap.Entry<K, V>>, Serializable {

	PersistentMap<K, V> put(K key, V value);

	V get(K key);

	PersistentMap<K, V> remove(K key);

	boolean containsKey(K key);

	int size();

	boolean isEmpty();

	PersistentSet<Entry<K, V>> entrySet();

	interface Entry<K, V> {
		K getKey();

		V getValue();
	}
}
