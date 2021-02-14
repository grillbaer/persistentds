package grillbaer.persistentds;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

class PersistentHashSet<E> extends AbstractPersistentSet<E> {
    private static final long serialVersionUID = 1L;

    private final PersistentSet<ListWithHashCode<E>> elementsByHashCodeSet;
    private final int size;

    static <E> PersistentHashSet<E> create() {
        return new PersistentHashSet<>(
                PersistentCollections.persistentBinTreeSet(HashCodeComparator
                        .getInstance()), 0);
    }

    private PersistentHashSet(
            PersistentSet<ListWithHashCode<E>> elementsByHashCodeSet,
            int size) {
        this.elementsByHashCodeSet = elementsByHashCodeSet;
        this.size = size;
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            Iterator<ListWithHashCode<E>> listsIter = elementsByHashCodeSet
                    .iterator();
            // when currentListIter == null: always hasNext() == true
            Iterator<E> currentListIter = listsIter.hasNext() ? listsIter
                    .next().iterator() : null;

            @Override
            public boolean hasNext() {
                return currentListIter != null;
            }

            @Override
            public E next() {
                if (currentListIter == null)
                    throw new NoSuchElementException();

                final E next = currentListIter.next();

                if (!currentListIter.hasNext()) {
                    if (listsIter.hasNext()) {
                        currentListIter = listsIter.next().iterator();
                    } else {
                        listsIter = null;
                        currentListIter = null;
                    }
                }

                return next;
            }
        };
    }

    @Override
    public PersistentHashSet<E> add(E element) {
        final ListWithHashCode<E> list = findOrCreateListForElement(element);
        final ListWithHashCode<E> newList = list.add(element);
        if (newList == list)
            return this;
        else
            return new PersistentHashSet<>(
                    this.elementsByHashCodeSet.put(newList), this.size
                    - list.size() + newList.size());
    }

    @Override
    public PersistentHashSet<E> put(E element) {
        ListWithHashCode<E> list = findOrCreateListForElement(element);
        ListWithHashCode<E> newList = list.put(element);
        if (newList == list)
            return this;
        else
            return new PersistentHashSet<>(
                    this.elementsByHashCodeSet.put(newList), this.size
                    - list.size() + newList.size());
    }

    @Override
    public PersistentHashSet<E> addAll(Iterable<? extends E> elements) {
        PersistentHashSet<E> newSet = this;
        for (E element : elements) {
            newSet = newSet.add(element);
        }
        return newSet;
    }

    @Override
    public PersistentHashSet<E> putAll(Iterable<? extends E> elements) {
        PersistentHashSet<E> newSet = this;
        for (E element : elements) {
            newSet = newSet.put(element);
        }
        return newSet;
    }

    @Override
    public PersistentHashSet<E> remove(E element) {
        ListWithHashCode<E> list = findListForElement(element);
        if (list != null) {
            ListWithHashCode<E> newList = list.remove(element);
            if (newList == list)
                return this;
            else {
                if (newList.isEmpty()) {
                    return new PersistentHashSet<>(
                            this.elementsByHashCodeSet.remove(list), this.size
                            - list.size());
                } else {
                    return new PersistentHashSet<>(
                            this.elementsByHashCodeSet.put(newList), this.size
                            - list.size() + newList.size());
                }
            }
        } else {
            return this;
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    ListWithHashCode<E> findListForElement(E element) {
        // direct lookup of an element is allowed by the comparator,
        // (avoids creating a List for each lookup)
        return (ListWithHashCode<E>) ((PersistentBinTreeSet) this.elementsByHashCodeSet)
                .get(element);
    }

    ListWithHashCode<E> findOrCreateListForElement(E element) {
        ListWithHashCode<E> list = findListForElement(element);
        if (list == null) {
            list = new ListWithHashCode<>(element);
        }
        return list;
    }

    @Override
    public E get(E element) {
        ListWithHashCode<E> listWithHashCode = findListForElement(element);
        return listWithHashCode == null ? null : listWithHashCode
                .findElement(element);
    }

    @Override
    public boolean contains(E element) {
        ListWithHashCode<E> listWithHashCode = findListForElement(element);
        return listWithHashCode != null && listWithHashCode.contains(element);
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.elementsByHashCodeSet.isEmpty();
    }

    private static int getHashCodeOf(Object o) {
        if (o == null) {
            return Integer.MIN_VALUE;
        } else if (o.getClass() == ListWithHashCode.class) {
            return ((ListWithHashCode<?>) o).getHashCode();
        } else {
            return o.hashCode();
        }
    }

    private static class ListWithHashCode<E> implements Serializable {
        private static final long serialVersionUID = 1L;

        private final int hashCode;
        private final PersistentList<E> elements;

        ListWithHashCode(E element) {
            this.hashCode = getHashCodeOf(element);
            this.elements = PersistentCollections.persistentBinTreeList();
        }

        public int size() {
            return this.elements.size();
        }

        public Iterator<E> iterator() {
            return this.elements.iterator();
        }

        ListWithHashCode(int hashCode, PersistentList<E> elements) {
            this.hashCode = hashCode;
            this.elements = elements;
        }

        int getHashCode() {
            return this.hashCode;
        }

        boolean isEmpty() {
            return this.elements.isEmpty();
        }

        E findElement(E element) {
            return this.elements.getFirstEqualElement(element);
        }

        boolean contains(E element) {
            return this.elements.contains(element);
        }

        ListWithHashCode<E> add(E element) {
            if (contains(element))
                return this;
            else
                return new ListWithHashCode<>(this.hashCode,
                        this.elements.add(element));
        }

        ListWithHashCode<E> put(E element) {
            final int index = this.elements.indexOf(element);
            if (index < 0) {
                return new ListWithHashCode<>(this.hashCode,
                        this.elements.add(element));
            } else {
                PersistentList<E> newList = this.elements.set(index, element);
                if (this.elements == newList)
                    return this;
                else
                    return new ListWithHashCode<>(this.hashCode, newList);
            }
        }

        ListWithHashCode<E> remove(E element) {
            PersistentList<E> newList = this.elements.remove(element);
            if (this.elements == newList)
                return this;
            else
                return new ListWithHashCode<>(this.hashCode, newList);
        }
    }

    private static class HashCodeComparator implements Comparator<Object>,
            Serializable {

        private static final long serialVersionUID = 1L;

        private final static HashCodeComparator INSTANCE = new HashCodeComparator();

        public static HashCodeComparator getInstance() {
            return INSTANCE;
        }

        @Override
        public int compare(Object o1, Object o2) {
            final int h1 = getHashCodeOf(o1);
            final int h2 = getHashCodeOf(o2);
            return Integer.compare(h1, h2);
        }

        public Object readResolve() {
            return INSTANCE;
        }
    }
}
