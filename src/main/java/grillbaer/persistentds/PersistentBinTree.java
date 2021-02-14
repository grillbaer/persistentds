package grillbaer.persistentds;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Base class for persistent binary trees. Supports balancing and
 * depth-first-iteration.
 * <p>
 * Supports serialization if the contained elements support serialization.
 *
 * @param <E> type of the elements stored in tree nodes
 * @param <T> type of the concrete binary tree implementation
 * @author Holger Fleischmann
 */
abstract class PersistentBinTree<E, T extends PersistentBinTree<E, T>>
		implements Iterable<E>, Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Returns an empty node without sub-trees and without contained element.
	 */
	abstract T empty();

	/**
	 * Returns a leaf-node containing an element but no (=empty) sub-trees.
	 *
	 * @param element the element contained in the node
	 */
	abstract T leaf(E element);

	/**
	 * Returns an inner node containing an element and a left and a right
	 * sub-tree.
	 *
	 * @param left    left sub-tree; must not be <code>null</code>, may be
	 *                {@linkplain #empty()}
	 * @param element the element at the node
	 * @param right   left sub-tree; must not be <code>null</code>, may be
	 *                {@linkplain #empty()}
	 */
	abstract T inner(T left, E element, T right);

	/**
	 * Returns the left sub-tree of this node. It will never be
	 * <code>null</code>, but it may be
	 * <code>{@linkplain #isEmpty()}==true</code>.
	 */
	abstract T left();

	/**
	 * Returns the element contained in this node.
	 */
	abstract E element();

	/**
	 * Returns the right sub-tree of this node (never <code>null</code>, but may
	 * be <code>{@linkplain #isEmpty()}==true</code>).
	 */
	abstract T right();

	/**
	 * Returns <code>this</code> as type T.
	 */
	abstract T thisNode();

	/**
	 * Returns whether this node is empty.
	 */
	public abstract boolean isEmpty();

	/**
	 * Returns the size of this node, i.e. the number of elements contained in
	 * this sub-tree.
	 */
	public abstract int size();

	/**
	 * Returns a balanced version of this tree regarding the sizes of the left
	 * and right sub-trees. It does not descending deeply into sub-trees. It
	 * only uses simple tree rotations around this node.
	 *
	 * @return a balanced version of this tree or simply the unchanged tree if
	 * tree rotations do not improve balance
	 */
	final T balanceThisNode() {
		if (!left().isEmpty() && !right().isEmpty()) {

			final int leftBias = left().size() - right().size();
			if (leftBias < 0) {

				// @formatter:off
				// right biased tree, balance with left rotation:
				//      n                 nR
				//     / \               / \
				//    nL  nR   ---->    n   nRR
				//        / \          / \
				//      nRL nRR       nL nRL
				// @formatter:on
				final int newBias = Math.abs((left().size() + 1 + right()
						.left().size()) - right().right().size());
				if (newBias < -leftBias) {
					return inner(inner(left(), element(), right().left()),
							right().element(), right().right());
				}

			} else if (leftBias > 0) {

				// @formatter:off
				// left biased tree, balance with right rotation:
				//      n                 nL
				//     / \               / \
				//    nL  nR   ---->  nLL   n
				//   / \                   / \
				// nLL nLR                nLR nR
				// @formatter:on
				final int newBias = Math.abs(left().left().size()
						- (left().right().size() + 1 + right().size()));
				if (newBias < leftBias) {
					return inner(left().left(), left().element(),
							inner(left().right(), element(), right()));
				}

			}

		} else if (left().isEmpty() && !right().isEmpty()
				&& right().left().isEmpty() && !right().right().isEmpty()) {

			// @formatter:off
			// right linear tree, balance with left rotation:
			//      n                 nR
			//       \               / \
			//       nR    ---->    n  nRR
			//         \
			//         nRR
			// @formatter:on
			return inner(leaf(element()), right().element(), right().right());

		} else if (right().isEmpty() && !left().isEmpty()
				&& !left().left().isEmpty() && left().right().isEmpty()) {

			// @formatter:off
			// left linear tree, balance with right rotation:
			//      n                 nL
			//     /                 / \
			//    nL       ---->   nLL  n
			//   /
			// nLL
			// @formatter:on
			return inner(left().left(), left().element(), leaf(element()));

		}

		return thisNode();
	}

	/**
	 * Returns an depth-first iterator for all elements contained in this tree.
	 * The iteration starts at the left-most element.
	 */
	@Override
	public Iterator<E> iterator() {
		return new Iterator<E>() {
			BinTreePos currentPos;
			Iterator<E> nonEmptyIteratorForCurrentPos;

			{
				if (size() > 0) {
					if (left().size() > 0) {
						currentPos = BinTreePos.LEFT;
						nonEmptyIteratorForCurrentPos = left().iterator();
					} else {
						currentPos = BinTreePos.THIS;
					}
				} else {
					currentPos = BinTreePos.AFTER_END;
				}
			}

			@Override
			public boolean hasNext() {
				return currentPos != BinTreePos.AFTER_END;
			}

			@Override
			public E next() {
				switch (currentPos) {

					case LEFT: {
						E next = nonEmptyIteratorForCurrentPos.next();
						if (!nonEmptyIteratorForCurrentPos.hasNext()) {
							currentPos = BinTreePos.THIS;
							nonEmptyIteratorForCurrentPos = null;
						}
						return next;
					}

					case THIS: {
						currentPos = BinTreePos.RIGHT;
						nonEmptyIteratorForCurrentPos = right().iterator();
						if (!nonEmptyIteratorForCurrentPos.hasNext()) {
							currentPos = BinTreePos.AFTER_END;
							nonEmptyIteratorForCurrentPos = null;
						}
						return element();
					}

					case RIGHT: {
						E next = nonEmptyIteratorForCurrentPos.next();
						if (!nonEmptyIteratorForCurrentPos.hasNext()) {
							currentPos = BinTreePos.AFTER_END;
							nonEmptyIteratorForCurrentPos = null;
						}
						return next;
					}

					case AFTER_END:
						throw new NoSuchElementException();

					default:
						throw new AssertionError("unreachable case");
				}

			}
		};
	}

	/**
	 * Creates a new {@linkplain ArrayList} containing all elements contained in
	 * this tree in left-to-right depth-first order.
	 */
	public ArrayList<E> toArrayList() {
		ArrayList<E> list = new ArrayList<>(size());
		addAllToCollection(list);
		return list;
	}

	/**
	 * Adds all elements contained in this tree to a collection in left-to-right
	 * depth-first order.
	 */
	void addAllToCollection(Collection<? super E> collection) {
		if (size() > 0) {
			left().addAllToCollection(collection);
			collection.add(element());
			right().addAllToCollection(collection);
		}
	}

	/**
	 * Two trees are equal exactly if both trees are
	 * {@linkplain PersistentBinTree}s and contain equal elements in the same
	 * depth-first order (as enumerated by the {@linkplain #iterator()}.
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public final boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;

		if (!(obj instanceof PersistentBinTree))
			return false;

		PersistentBinTree otherTree = ((PersistentBinTree) obj);
		if (size() != otherTree.size())
			return false;

		Iterator<Object> otherIter = otherTree.iterator();
		for (E element : this) {
			Object otherObject = otherIter.next();
			if (!Objects.equals(element, otherObject))
				return false;
		}

		return true;
	}

	/**
	 * Returns a hash-code calculated for the sequence and depth-first order of
	 * the elements contained in this tree.
	 */
	public final int hashCode() {
		int hashCode = 1;
		for (E e : this)
			hashCode = 31 * hashCode + (e == null ? 0 : e.hashCode());
		return hashCode;
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

	public String toDebugString() {
		return (left().isEmpty() ? "" : "(" + left().toDebugString() + ")<- ")
				+ element()
				+ (right().isEmpty() ? "" : " ->(" + right().toDebugString()
				+ ")");
	}

	public int depth() {
		return isEmpty() ? 0 : Math.max(left().depth(), right().depth()) + 1;
	}
}

/**
 * Position in the binary tree relative to a node.
 */
enum BinTreePos {
	LEFT, THIS, RIGHT, AFTER_END
}
