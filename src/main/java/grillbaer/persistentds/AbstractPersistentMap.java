package grillbaer.persistentds;

import java.io.Serializable;
import java.util.Objects;

abstract class AbstractPersistentMap<K, V> implements PersistentMap<K, V> {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof PersistentMap))
            return false;

        PersistentMap other = (PersistentMap) obj;

        if (size() != other.size())
            return false;

        for (Entry entry : this) {
            if (!other.containsKey(entry.getKey()))
                return false;
            if (!Objects.equals(entry.getValue(), other.get(entry.getKey())))
                return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hashCode = 0;
        for (Entry<K, V> entry : this) {
            hashCode += entry.hashCode();
        }
        return hashCode;
    }

    static class DefaultEntry<K, V> implements Entry<K, V>, Serializable {
        private static final long serialVersionUID = 1L;

        private final K key;
        private final V value;

        public DefaultEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public int hashCode() {
            return (getKey() == null ? 0 : getKey().hashCode())
                    ^ (getValue() == null ? 0 : getValue().hashCode());
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (!(obj instanceof PersistentMap.Entry))
                return false;

            Entry<?, ?> other = (Entry<?, ?>) obj;

            if (getKey() == null) {
                if (other.getKey() != null)
                    return false;
            } else if (!getKey().equals(other.getKey()))
                return false;

            if (getValue() == null) {
                return other.getValue() == null;
            } else return getValue().equals(other.getValue());
        }

        @Override
        public K getKey() {
            return this.key;
        }

        @Override
        public V getValue() {
            return this.value;
        }

        @Override
        public String toString() {
            return "[" + this.key + " -> " + this.value + "]";
        }
    }
}
