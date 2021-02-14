package grillbaer.persistentds;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Common behavior that can be shared between all {@linkplain PersistentSet}s.
 *
 * @author Holger Fleischmann
 */
abstract class AbstractPersistentSet<E> implements PersistentSet<E>,
        Serializable {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof PersistentSet))
            return false;

        PersistentSet otherSet = (PersistentSet) obj;
        if (otherSet.size() != size())
            return false;
        for (E element : this) {
            if (!otherSet.contains(element))
                return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hashCode = 0;
        for (E element : this) {
            hashCode += element != null ? element.hashCode() : 0;
        }
        return hashCode;
    }

    @Override
    public ArrayList<E> toArrayList() {
        ArrayList<E> arrayList = new ArrayList<>(size());
        for (E element : this) {
            arrayList.add(element);
        }
        return arrayList;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(size() * 10 + 2);
        builder.append("{");

        boolean first = true;
        for (E e : this) {
            if (first) {
                first = false;
            } else {
                builder.append(",");
            }
            builder.append(e);
        }
        builder.append("}");

        return builder.toString();
    }
}
