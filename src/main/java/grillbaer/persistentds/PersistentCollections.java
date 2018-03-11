package grillbaer.persistentds;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Creates different types of persistent collections. A persistent collection
 * always is immutable. Modification methods will return a new instance of the
 * collection without changing the existing instance. However, it will share
 * parts of the data structure with the original instance in order to keep the
 * copy overhead as low as possible.
 * <p>
 * Persistent collections simplify synchronization of shared data in
 * multi-threaded applications because they guarantee immutability of already
 * existing instances. They also make it easy to pass old and new states to
 * observers.
 * <p>
 * Syntax and behaviour of the persistent collections' interfaces are similar to
 * {@link java.util.Collection}s. However, the modification methods return a
 * modified version of the collection and will not change the original one.
 * <p>
 * Note on serialization: the collections will be serializable if all contained
 * elements and the passed comparators are serializable.
 * 
 * @author Holger Fleischmann
 */
public final class PersistentCollections {

	/**
	 * Returns an empty persistent list based on balanced binary trees.
	 */
	public static <E> PersistentBinTreeList<E> persistentBinTreeList() {
		return PersistentBinTreeList.create();
	}

	/**
	 * Returns an empty persistent set for comparable elements. It is based on
	 * balanced binary trees. This set will sort and check elements for equality
	 * using their {@linkplain Comparable#compareTo(Object)} methods. Note that
	 * <code>null</code> elements are always supported. They are treated to be
	 * less than any non- <code>null</code> element and will not be passed to
	 * the elements' {@linkplain Comparable#compareTo(Object)} methods.
	 * 
	 * @see #persistentBinTreeSet(Comparator)
	 */
	public static <E extends Comparable<E>> PersistentBinTreeSet<E> persistentBinTreeSet() {
		return persistentBinTreeSet(ComparableComparator.getInstance());
	}

	/**
	 * Returns an empty persistent set using a custom comparator. It is based on
	 * balanced binary trees. Note that <code>null</code> elements are supported
	 * if the {@linkplain Comparator} supports them.
	 * 
	 * @param comparator
	 *            the comparator used to sort and check elements for equality
	 * @see #persistentBinTreeSet()
	 */
	public static <E> PersistentBinTreeSet<E> persistentBinTreeSet(
			Comparator<? super E> comparator) {
		return PersistentBinTreeSet.create(comparator);
	}

	/**
	 * Returns an empty persistent map using comparable keys. It is based on
	 * balanced binary trees. Note that the same rules apply for sorting and
	 * checking keys for equality as defined for sets in
	 * {@linkplain #persistentBinTreeSet()} .
	 */
	public static <K extends Comparable<K>, V> PersistentBinTreeMap<K, V> persistentBinTreeMap() {
		return persistentBinTreeMap(ComparableComparator.getInstance());
	}

	/**
	 * Returns an empty persistent map using a custom comparator for its keys.
	 * It is based on balanced binary trees.Note that the same rules apply for
	 * sorting and checking keys for equality as defined for sets in
	 * {@linkplain #persistentBinTreeSet(Comparator)}.
	 */
	public static <K, V> PersistentBinTreeMap<K, V> persistentBinTreeMap(
			Comparator<? super K> keyComparator) {
		return PersistentBinTreeMap.create(keyComparator);
	}

	/**
	 * Returns an empty persistent set that stores elements using their
	 * {@linkplain Object#hashCode()} and {@linkplain Object#equals(Object)}
	 * properties. Two elements are considered equal is their
	 * {@linkplain Object#equals(Object)} method returns true. The elements must
	 * correctly fulfill the contract for {@linkplain #hashCode()} and
	 * {@linkplain #equals(Object)}.
	 * <p>
	 * Internally it is based on two levels of balanced binary trees. A first
	 * level tree contains one node for each hash-code of the elements contained
	 * in the set. Each such first-level node contains a list, also based on
	 * balanced binary trees, that holds all non-equal elements with the same
	 * hash-code as the first-level node.
	 */
	public static <E> PersistentHashSet<E> persistentHashSet() {
		return PersistentHashSet.create();
	}

	/*
	 * Comparator for comparable objects. <code>null</code> objects are less
	 * than any other object.
	 */
	private static class ComparableComparator<T extends Comparable<T>>
			implements Comparator<T>, Serializable {
		private static final long serialVersionUID = 1L;

		@SuppressWarnings("rawtypes")
		private final static Comparator COMPARABLE_COMPARATOR = new ComparableComparator();

		@SuppressWarnings("unchecked")
		static <T> Comparator<T> getInstance() {
			return (Comparator<T>) COMPARABLE_COMPARATOR;
		}

		@Override
		public int compare(T o1, T o2) {
			if (o1 == null && o2 == null)
				return 0;
			else if (o1 == null)
				return -1;
			else if (o2 == null)
				return 1;
			else
				return o1.compareTo(o2);
		}

		private Object readResolve() {
			return COMPARABLE_COMPARATOR;
		}
	};
}
