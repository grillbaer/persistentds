package grillbaer.persistentds;

import java.util.ArrayList;
import java.util.Set;

/**
 * Interface for persistent sets. Instances are created with the factory methods
 * in {@linkplain PersistentCollections}. For a definition of common properties
 * of persistent collections see {@linkplain PersistentCollections}.
 * 
 * @param <E>
 *            type of the elements that may be contained in the set
 * 
 * @author Holger Fleischmann
 */
public interface PersistentSet<E> extends Iterable<E> {

	/**
	 * Adds an element to the set if the set does not yet contain an equal
	 * element. Similar to the definition of {@linkplain Set#add(Object)}.
	 */
	PersistentSet<E> add(E element);

	/**
	 * Puts an element into the set. If the set already contains an equal
	 * element it will be replaced by the new element.
	 */
	PersistentSet<E> put(E element);

	/**
	 * Adds all elements into the set. Similar to the definition of
	 * {@linkplain Set#addAll(java.util.Collection)}.
	 */
	PersistentSet<E> addAll(Iterable<? extends E> elements);

	/**
	 * Puts all elements into the set.
	 */
	PersistentSet<E> putAll(Iterable<? extends E> elements);

	/**
	 * Removes an elements from the set. If no equal element is contained in the
	 * set the same set will be returned. Similar to the definition of
	 * {@linkplain Set#remove(Object)}.
	 */
	PersistentSet<E> remove(E element);

	/**
	 * Returns the element contained in the set that is equal to the passed
	 * element. Returns <code>null</code> if no equal element is contained in
	 * the set.
	 */
	E get(E element);

	/**
	 * Returns <code>true</code> if the set contains an element equal to the
	 * passed element. Similar to the definition of
	 * {@linkplain Set#contains(Object)}.
	 */
	boolean contains(E element);

	/**
	 * Returns the number of elements contained in the set.
	 */
	int size();

	/**
	 * Returns <code>true</code> if the set contains no elements.
	 */
	boolean isEmpty();

	/**
	 * Returns a new {@linkplain ArrayList} with the elements contained in the
	 * set. The elements will be returned in the iteration order. Each call will
	 * return a new array list that will have no connection with the original
	 * set.
	 */
	ArrayList<E> toArrayList();

	/**
	 * Two sets are equal if they are both instances of
	 * {@linkplain PersistentSet} and contain equal elements. Similar to the
	 * definition in {@linkplain Set}.
	 */
	boolean equals(Object obj);

	/**
	 * Returns the sum of all hash-codes of the contained elements. The
	 * hash-code of a <code>null</code>-element is defined as 0. Similar to the
	 * definition in {@linkplain Set}.
	 */
	int hashCode();
}
