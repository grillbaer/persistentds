package grillbaer.persistentds;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

final class PersistentBinTreeSet<E> extends AbstractPersistentSet<E> {
	private static final long serialVersionUID = 1L;

	private final Comparator<? super E> comparator;
	private final Node<E> root;

	private PersistentBinTreeSet(Comparator<? super E> comparator, Node<E> root) {
		this.comparator = comparator;
		this.root = root;
	}

	static <E> PersistentBinTreeSet<E> create(Comparator<? super E> comparator) {
		Objects.requireNonNull(comparator, "comparator must not be null");
		return new PersistentBinTreeSet<>(comparator, EmptyNode.getInstance());
	}

	public Comparator<? super E> getComparator() {
		return this.comparator;
	}

	private PersistentBinTreeSet<E> newIfChanged(Node<E> newRoot) {
		return this.root == newRoot ? this : new PersistentBinTreeSet<>(
				this.comparator, newRoot);
	}

	public PersistentSet<E> add(E element) {
		return newIfChanged(this.root.add(this.comparator, element, false));
	}

	public PersistentBinTreeSet<E> put(E element) {
		return newIfChanged(this.root.add(this.comparator, element, true));
	}

	public PersistentBinTreeSet<E> addAll(Iterable<? extends E> elements) {
		return newIfChanged(this.root.addAll(this.comparator, elements, false));
	}

	public PersistentBinTreeSet<E> putAll(Iterable<? extends E> elements) {
		return newIfChanged(this.root.addAll(this.comparator, elements, true));
	}

	public PersistentBinTreeSet<E> remove(E element) {
		return newIfChanged(this.root.remove(this.comparator, element));
	}

	public E get(E element) {
		return this.root.get(this.comparator, element);
	}

	@Override
	public Iterator<E> iterator() {
		return this.root.iterator();
	}

	public boolean contains(E element) {
		return this.root.contains(this.comparator, element);
	}

	public int size() {
		return this.root.size();
	}

	public boolean isEmpty() {
		return this.root.isEmpty();
	}

	int depth() {
		return this.root.depth();
	}

	public String toDebugString() {
		return this.root.toDebugString();
	}

	private static abstract class Node<E> extends PersistentBinTree<E, Node<E>> {

		private static final long serialVersionUID = 1L;

		@Override
		Node<E> empty() {
			return EmptyNode.getInstance();
		}

		@Override
		Node<E> leaf(E element) {
			return new LeafNode<>(element);
		}

		@Override
		Node<E> inner(Node<E> left, E element, Node<E> right) {
			return new InnerNode<>(left, element, right);
		}

		@Override
		Node<E> thisNode() {
			return this;
		}

		abstract E first();

		abstract E last();

		final BinTreePos compare(Comparator<? super E> comparator, E element) {
			final int pos = comparator.compare(element(), element);
			if (pos < 0)
				return BinTreePos.RIGHT;
			else if (pos == 0)
				return BinTreePos.THIS;
			else
				return BinTreePos.LEFT;
		}

		abstract E get(Comparator<? super E> comparator, E element);

		abstract boolean contains(Comparator<? super E> comparator, E element);

		abstract Node<E> add(Comparator<? super E> comparator, E element,
				boolean replaceIfExists);

		public Node<E> addAll(Comparator<? super E> comparator,
				Iterable<? extends E> elements, boolean replaceIfExists) {
			Node<E> set = this;
			for (E element : elements) {
				set = set.add(comparator, element, replaceIfExists);
			}

			return set;
		}

		abstract Node<E> remove(Comparator<? super E> comparator, E element);
	}

	final static class InnerNode<E> extends Node<E> {
		private static final long serialVersionUID = 1L;

		private final Node<E> left;
		private final E element;
		private final Node<E> right;
		private final int size;

		InnerNode(Node<E> left, E element, Node<E> right) {
			assert left != null;
			assert left != this;
			assert right != null;
			assert right != this;

			this.left = left;
			this.element = element;
			this.right = right;
			this.size = left.size() + 1 + right.size();
		}

		@Override
		Node<E> left() {
			return this.left;
		}

		@Override
		E element() {
			return this.element;
		}

		@Override
		Node<E> right() {
			return this.right;
		}

		E first() {
			if (left().isEmpty()) {
				return element();
			} else {
				return left.first();
			}
		}

		E last() {
			if (right().isEmpty()) {
				return element();
			} else {
				return right.last();
			}
		}

		@Override
		public Node<E> add(Comparator<? super E> comparator, E element,
				boolean replaceIfExists) {
			final BinTreePos pos = compare(comparator, element);
			if (pos == BinTreePos.LEFT) {
				// insert into left subtree
				return inner(left().add(comparator, element, replaceIfExists),
						element(), right()).balanceThisNode();
			} else if (pos == BinTreePos.THIS) {
				return replaceIfExists ? inner(left(), element, right())
						: thisNode();

			} else {
				// insert into right subtree
				return inner(left(), element(),
						right().add(comparator, element, replaceIfExists))
						.balanceThisNode();
			}
		}

		public Node<E> remove(Comparator<? super E> comparator, E element) {
			final BinTreePos pos = compare(comparator, element);
			if (pos == BinTreePos.LEFT) {

				final Node<E> newLeft = left().remove(comparator, element);
				if (newLeft != left()) {
					return inner(newLeft, element(), right()).balanceThisNode();
				} else {
					return thisNode();
				}

			} else if (pos == BinTreePos.THIS) {

				final int leftSize = left().size();
				final int rightSize = right().size();

				Node<E> newLeft;
				E newElement;
				Node<E> newRight;

				if (leftSize < rightSize) {
					newLeft = left();
					newElement = right().first();
					newRight = right().remove(comparator, right().first());
				} else if (/* leftSize >= rightSize && */leftSize > 0) {
					newLeft = left().remove(comparator, left().last());
					newElement = left().last();
					newRight = right();
				} else /* if (leftSize == 0 && rightSize == 0) */{
					return empty();
				}

				if (newLeft.isEmpty() && newRight.isEmpty()) {
					return leaf(newElement);
				} else {
					return inner(newLeft, newElement, newRight);
				}

			} else {

				final Node<E> newRight = right().remove(comparator, element);
				if (newRight != right()) {
					return inner(left(), element(), newRight).balanceThisNode();
				} else {
					return thisNode();
				}

			}
		}

		@Override
		public E get(Comparator<? super E> comparator, E element) {
			final BinTreePos pos = compare(comparator, element);
			if (pos == BinTreePos.LEFT) {
				return left().get(comparator, element);
			} else if (pos == BinTreePos.THIS) {
				return element();
			} else {
				return right().get(comparator, element);
			}
		}

		@Override
		public boolean contains(Comparator<? super E> comparator, E element) {
			final BinTreePos pos = compare(comparator, element);
			if (pos == BinTreePos.LEFT) {
				return left().contains(comparator, element);
			} else if (pos == BinTreePos.THIS) {
				return true;
			} else {
				return right().contains(comparator, element);
			}
		}

		@Override
		public int size() {
			return this.size;
		}

		@Override
		public boolean isEmpty() {
			return false;
		}
	}

	final static class LeafNode<E> extends Node<E> {
		private static final long serialVersionUID = 1L;

		private final E element;

		LeafNode(E element) {
			this.element = element;
		}

		@Override
		Node<E> left() {
			return empty();
		}

		@Override
		E element() {
			return this.element;
		}

		@Override
		Node<E> right() {
			return empty();
		}

		@Override
		E first() {
			return element();
		}

		@Override
		E last() {
			return element();
		}

		@Override
		E get(Comparator<? super E> comparator, E element) {
			return compare(comparator, element) == BinTreePos.THIS ? element()
					: null;
		}

		@Override
		boolean contains(Comparator<? super E> comparator, E element) {
			return compare(comparator, element) == BinTreePos.THIS;
		}

		@Override
		public int size() {
			return 1;
		}

		@Override
		public boolean isEmpty() {
			return false;
		}

		@Override
		public Node<E> add(Comparator<? super E> comparator, E element,
				boolean replaceIfExists) {
			final BinTreePos pos = compare(comparator, element);
			if (pos == BinTreePos.LEFT)
				return inner(empty(), element, this);
			else if (pos == BinTreePos.THIS)
				return replaceIfExists ? leaf(element) : thisNode();
			else
				return inner(this, element, empty());
		}

		@Override
		public Node<E> remove(Comparator<? super E> comparator, E element) {
			final BinTreePos pos = compare(comparator, element);
			if (pos == BinTreePos.THIS)
				return empty();
			else
				return thisNode();
		}
	}

	@SuppressWarnings("rawtypes")
	final static class EmptyNode extends Node {
		private static final long serialVersionUID = 1L;

		private final static EmptyNode INSTANCE = new EmptyNode();

		private final static Iterator EMPTY_ITERATOR = new Iterator() {

			@Override
			public boolean hasNext() {
				return false;
			}

			@Override
			public Object next() {
				throw new NoSuchElementException();
			}
		};

		@SuppressWarnings("unchecked")
		static <E> Node<E> getInstance() {
			return INSTANCE;
		}

		private EmptyNode() {
		}

		Node left() {
			return this;
		};

		@Override
		Object element() {
			throw new NoSuchElementException("no such element");
		}

		@Override
		Node right() {
			return this;
		}

		@Override
		Object first() {
			return new NoSuchElementException();
		}

		@Override
		Object last() {
			return new NoSuchElementException();
		}

		@SuppressWarnings("unchecked")
		@Override
		public Node add(Comparator comparator, Object element,
				boolean replaceIfExists) {
			return leaf(element);
		}

		@Override
		public Node remove(Comparator comparator, Object element) {
			return thisNode();
		}

		@Override
		public Object get(Comparator comparator, Object element) {
			return null;
		}

		@Override
		boolean contains(Comparator comparator, Object element) {
			return false;
		}

		@Override
		public Iterator iterator() {
			return EMPTY_ITERATOR;
		}

		@Override
		public int size() {
			return 0;
		}

		@Override
		public boolean isEmpty() {
			return true;
		}

		private Object readResolve() {
			return INSTANCE;
		}

	}
}