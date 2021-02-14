package grillbaer.persistentds;

import java.util.ArrayList;

public interface PersistentList<E> extends Iterable<E> {
    PersistentList<E> add(int index, E element);

    PersistentList<E> add(E element);

    PersistentList<E> addAll(Iterable<E> elements);

    PersistentList<E> remove(int index);

    PersistentList<E> remove(E element);

    PersistentList<E> set(int index, E element);

    E get(int index);

    E getFirstEqualElement(E element);

    boolean contains(E element);

    int indexOf(E element);

    int lastIndexOf(E element);

    int size();

    boolean isEmpty();

    ArrayList<E> toArrayList();
}
