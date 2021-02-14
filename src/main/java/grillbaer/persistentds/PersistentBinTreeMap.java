package grillbaer.persistentds;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;

class PersistentBinTreeMap<K, V> extends AbstractPersistentMap<K, V> {

	private static final long serialVersionUID = 1L;

	private final PersistentBinTreeSet<DefaultEntry<K, V>> entrySet;

	static <K, V> PersistentBinTreeMap<K, V> create(
			Comparator<? super K> keyComparator) {
		return new PersistentBinTreeMap<>(
				(PersistentBinTreeSet<DefaultEntry<K, V>>) PersistentCollections
						.persistentBinTreeSet(new EntryByKeyComparator<K, V, DefaultEntry<K, V>>(
								keyComparator)));
	}

	private PersistentBinTreeMap(
			PersistentBinTreeSet<DefaultEntry<K, V>> entrySet) {
		this.entrySet = entrySet;
	}

	@Override
	public PersistentBinTreeMap<K, V> put(K key, V value) {
		return new PersistentBinTreeMap<>(this.entrySet.put(new DefaultEntry<>(
				key, value)));
	}

	private DefaultEntry<K, V> keyLookupEntry(K key) {
		return new DefaultEntry<>(key, null);
	}

	@Override
	public V get(K key) {
		DefaultEntry<K, V> entry = this.entrySet.get(keyLookupEntry(key));
		if (entry != null) {
			return entry.getValue();
		} else {
			return null;
		}
	}

	@Override
	public PersistentBinTreeMap<K, V> remove(K key) {
		PersistentBinTreeSet<DefaultEntry<K, V>> newEntrySet = this.entrySet
				.remove(keyLookupEntry(key));
		if (newEntrySet != this.entrySet) {
			return new PersistentBinTreeMap<>(newEntrySet);
		} else {
			return this;
		}
	}

	@Override
	public boolean containsKey(K key) {
		return this.entrySet.contains(keyLookupEntry(key));
	}

	@Override
	public int size() {
		return this.entrySet.size();
	}

	public int depth() {
		return this.entrySet.depth();
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public Iterator<Entry<K, V>> iterator() {
		return (Iterator) this.entrySet.iterator();
	}

	@Override
	public boolean isEmpty() {
		return this.entrySet.isEmpty();
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public PersistentSet<Entry<K, V>> entrySet() {
		return (PersistentSet) this.entrySet;
	}

	@Override
	public String toString() {
		return this.entrySet.toString();
	}

	private static class EntryByKeyComparator<K, V, E extends Entry<K, V>>
			implements Comparator<E>, Serializable {
		private static final long serialVersionUID = 1L;

		private final Comparator<? super K> keyComparator;

		public EntryByKeyComparator(Comparator<? super K> keyComparator) {
			Objects.requireNonNull(keyComparator,
					"keyComparator must not be null");
			this.keyComparator = keyComparator;
		}

		@Override
		public int compare(E o1, E o2) {
			return this.keyComparator.compare(o1.getKey(), o2.getKey());
		}
	}
}
