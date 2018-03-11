package grillbaer.persistentds;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

abstract class PersistentBinTreeList<E> extends
		PersistentBinTree<E, PersistentBinTreeList<E>> implements
		PersistentList<E> {

	private static final long serialVersionUID = 1L;

	static <E> PersistentBinTreeList<E> create() {
		return EmptyNode.getInstance();
	}

	@Override
	PersistentBinTreeList<E> empty() {
		return EmptyNode.getInstance();
	}

	@Override
	PersistentBinTreeList<E> leaf(E element) {
		return new LeafNode<>(element);
	}

	@Override
	PersistentBinTreeList<E> inner(PersistentBinTreeList<E> left, E element,
			PersistentBinTreeList<E> right) {
		return new InnerNode<>(left, element, right);
	}

	@Override
	PersistentBinTreeList<E> thisNode() {
		return this;
	}

	@Override
	public abstract PersistentBinTreeList<E> add(int index, E element);

	@Override
	public PersistentBinTreeList<E> add(E element) {
		return add(size(), element);
	}

	@Override
	public PersistentBinTreeList<E> addAll(Iterable<E> elements) {
		PersistentBinTreeList<E> list = this;
		for (E element : elements) {
			list = list.add(element);
		}

		return list;
	}

	@Override
	public abstract PersistentBinTreeList<E> remove(int index);

	@Override
	public PersistentBinTreeList<E> remove(E element) {
		int index = indexOf(element);
		if (index < 0)
			return this;
		else
			return remove(index);
	}

	@Override
	public abstract PersistentBinTreeList<E> set(int index, E element);

	public E getFirstEqualElement(E element) {
		if (isEmpty() || element == null)
			return null;

		E found = left().getFirstEqualElement(element);
		if (found != null)
			return found;

		if (Objects.equals(element(), element))
			return element();

		return right().getFirstEqualElement(element);
	}

	@Override
	public boolean contains(E element) {
		return indexOf(element) >= 0;
	}

	final static class InnerNode<E> extends PersistentBinTreeList<E> {
		private static final long serialVersionUID = 1L;

		private final PersistentBinTreeList<E> left;
		private final E element;
		private final PersistentBinTreeList<E> right;
		private final int size;

		InnerNode(PersistentBinTreeList<E> left, E element,
				PersistentBinTreeList<E> right) {
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
		PersistentBinTreeList<E> left() {
			return this.left;
		}

		@Override
		E element() {
			return this.element;
		}

		@Override
		PersistentBinTreeList<E> right() {
			return this.right;
		}

		@Override
		public PersistentBinTreeList<E> add(int index, E element) {
			if (index < 0 || index > size())
				throw new IndexOutOfBoundsException("illegal index");

			final int leftSize = left().size();
			final int rightSize = right().size();

			if (index < leftSize) {

				// insert into left subtree
				return inner(left().add(index, element), element(), right())
						.balanceThisNode();

			} else if (index == leftSize) {

				if (leftSize <= rightSize) {
					// insert right-most into left subtree
					return inner(left().add(index, element), element(), right())
							.balanceThisNode();
				} else {
					// insert left-most into right subtree
					return inner(left(), element, right().add(0, element()))
							.balanceThisNode();
				}

			} else {

				// insert into right subtree
				return inner(left(), element(),
						right().add(index - leftSize - 1, element))
						.balanceThisNode();

			}
		}

		@Override
		public PersistentBinTreeList<E> set(int index, E element) {
			final int leftSize = left().size();
			PersistentBinTreeList<E> newLeft;
			E newElement;
			PersistentBinTreeList<E> newRight;

			if (index < leftSize) {
				newLeft = left().set(index, element);
				newElement = element();
				newRight = right();
			} else if (index == leftSize) {
				newLeft = left();
				newElement = element;
				newRight = right();
			} else {
				newLeft = left();
				newElement = element();
				newRight = right().set(index - leftSize - 1, element);
			}

			if (newLeft != left() || newElement != element
					|| newRight != right()) {
				return inner(newLeft, newElement, newRight);
			} else {
				return this;
			}
		}

		public PersistentBinTreeList<E> remove(int index) {
			if (index < 0 || index > size())
				throw new IndexOutOfBoundsException("illegal index");

			final int leftSize = left().size();
			final int rightSize = right().size();

			if (index < leftSize) {
				return inner(left().remove(index), element(), right())
						.balanceThisNode();
			} else if (index == leftSize) {

				PersistentBinTreeList<E> newLeft;
				E newElement;
				PersistentBinTreeList<E> newRight;

				if (leftSize < rightSize) {
					newLeft = left();
					newElement = right().get(0);
					newRight = right().remove(0);
				} else if (/* leftSize >= rightSize && */leftSize > 0) {
					newLeft = left().remove(leftSize - 1);
					newElement = left().get(leftSize - 1);
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
				return inner(left(), element(),
						right().remove(index - leftSize - 1)).balanceThisNode();
			}
		}

		@Override
		public E get(int index) {
			final int leftSize = left().size();
			if (index < leftSize)
				return left().get(index);
			else if (index == leftSize)
				return element();
			else
				return right().get(index - leftSize - 1);
		}

		@Override
		public int indexOf(E element) {
			int index = left().indexOf(element);
			if (index >= 0)
				return index;

			if (Objects.equals(element(), element))
				return left().size();

			index = right().indexOf(element);
			if (index >= 0)
				return left().size() + 1 + index;

			return -1;
		}

		@Override
		public int lastIndexOf(E element) {
			int index = right().lastIndexOf(element);
			if (index >= 0)
				return left().size() + 1 + index;

			if (Objects.equals(element(), element))
				return left().size();

			index = left().lastIndexOf(element);
			if (index >= 0)
				return index;

			return -1;
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

	final static class LeafNode<E> extends PersistentBinTreeList<E> {
		private static final long serialVersionUID = 1L;

		private final E element;

		LeafNode(E element) {
			this.element = element;
		}

		@Override
		PersistentBinTreeList<E> left() {
			return empty();
		}

		@Override
		E element() {
			return this.element;
		}

		@Override
		PersistentBinTreeList<E> right() {
			return empty();
		}

		@Override
		public E get(int index) {
			if (index != 0)
				throw new IndexOutOfBoundsException("illegal index");
			return element();
		}

		@Override
		public int indexOf(E element) {
			return Objects.equals(element(), element) ? 0 : -1;
		}

		@Override
		public int lastIndexOf(E element) {
			return Objects.equals(element(), element) ? 0 : -1;
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
		public PersistentBinTreeList<E> add(int index, E element) {
			if (index == 0)
				return inner(empty(), element, this);
			else if (index == 1)
				return inner(this, element, empty());
			else
				throw new IndexOutOfBoundsException("illegal index");

		}

		@Override
		public PersistentBinTreeList<E> remove(int index) {
			if (index != 0)
				throw new IndexOutOfBoundsException("illegal index");
			return empty();
		}

		@Override
		public PersistentBinTreeList<E> set(int index, E element) {
			if (index == 0) {
				return element() == element ? this : leaf(element);
			} else {
				throw new IndexOutOfBoundsException("illegal index");
			}
		}

	}

	@SuppressWarnings("rawtypes")
	final static class EmptyNode<E> extends PersistentBinTreeList<E> {

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
		static <E> PersistentBinTreeList<E> getInstance() {
			return INSTANCE;
		}

		private EmptyNode() {
		}

		PersistentBinTreeList left() {
			return this;
		};

		@Override
		E element() {
			throw new NoSuchElementException("no such element");
		}

		@Override
		PersistentBinTreeList right() {
			return this;
		}

		@Override
		public PersistentBinTreeList<E> add(int index, E element) {
			if (index != 0)
				throw new IndexOutOfBoundsException("illegal index");
			return leaf(element);
		}

		@Override
		public PersistentBinTreeList<E> remove(int index) {
			throw new IndexOutOfBoundsException("illegal index");
		}

		@Override
		public PersistentBinTreeList<E> set(int index, Object element) {
			throw new IndexOutOfBoundsException("illegal index");
		}

		@Override
		public E get(int index) {
			throw new IndexOutOfBoundsException("illegal index");
		}

		@Override
		public int indexOf(Object element) {
			return -1;
		}

		@Override
		public int lastIndexOf(Object element) {
			return -1;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Iterator<E> iterator() {
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