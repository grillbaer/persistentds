package grillbaer.persistentds;

/**
 * Examples how persistent data-structures can be used.
 *
 * @author Holger Fleischmann
 */
public class Examples {

    public static void main(String[] args) {

        // list

        PersistentList<Integer> list = PersistentCollections.persistentBinTreeList();
        list = list.add(1).add(2).add(3);
        PersistentList<Integer> furtherModifiedList = list.add(4);
        System.out.println("Original list=" + list + " => further modified list=" + furtherModifiedList);
        // prints "Original list={1,2,3} => further modified list={1,2,3,4}"

        // set

        PersistentSet<String> set = PersistentCollections.persistentBinTreeSet();
        // or PersistentCollections.persistentHashSet();
        set = set.put("A").put("B").put("C");
        PersistentSet<String> furtherModifiedSet = set.remove("B");
        System.out.println("Original set=" + set + " => further modified set=" + furtherModifiedSet);
        // prints "Original set={A,B,C} => further modified set={A,C}"

        // map

        PersistentMap<Integer, String> map = PersistentCollections.persistentBinTreeMap();
        map = map.put(1, "one").put(2, "two");
        PersistentMap<Integer, String> furtherModifiedMap = map.put(3, "three");
        System.out.println("Original map=" + map + " => further modified map=" + furtherModifiedMap);
        // prints "Original map={[1 -> one],[2 -> two]} => further modified map={[1 -> one],[2 -> two],[3 -> three]}"

    }
}
