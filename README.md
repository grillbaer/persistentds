[![Build](https://github.com/grillbaer/persistentds/workflows/Maven%20Build/badge.svg)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=grillbaer_persistentds&metric=coverage)](https://sonarcloud.io/dashboard?id=grillbaer_persistentds)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=grillbaer_persistentds&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=grillbaer_persistentds)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=grillbaer_persistentds&metric=bugs)](https://sonarcloud.io/dashboard?id=grillbaer_persistentds)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=grillbaer_persistentds&metric=code_smells)](https://sonarcloud.io/dashboard?id=grillbaer_persistentds)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=grillbaer_persistentds&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=grillbaer_persistentds)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=grillbaer_persistentds&metric=security_rating)](https://sonarcloud.io/dashboard?id=grillbaer_persistentds)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=grillbaer_persistentds&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=grillbaer_persistentds)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=grillbaer_persistentds&metric=alert_status)](https://sonarcloud.io/dashboard?id=grillbaer_persistentds)

# Persistent Data Structures
Immutable copy-on-write collections for lists, maps and sets in Java. Based on auto-balancing binary trees.

## Why use them?
Persistent collections simplify synchronization on shared data in multi-threaded applications because they guarantee immutability of already existing instances. They also make it easy to pass both old and new states to observers.

## What does 'persistent' mean here?
A persistent collection is always immutable. Modification methods return new instances of the collection without changing the existing instance. So you don't have to fiddle with error-prone and concurrency-restricting locking, because immutable data objects are thread-safe by nature.
For good performance, `peristentsds` shares common parts between modified and previous versions of the data structures. This keeps the copy overhead as low as possible.

For an in-detail definition see [Persistent Data Structure on WIKIPEDIA](https://en.wikipedia.org/wiki/Persistent_data_structure).

# How to use?
Syntax and behaviour of the persistent collections' interfaces are similar to `java.util.Collection`. However, all modification methods return modified versions of the collection and will not change the original one.

Simply start with the static factory `PersistentCollections` to create new instances of persistent data structures.

## Dependencies
Requires Java 8.

## Examples
### List
```java
PersistentList<Integer> list = PersistentCollections.persistentBinTreeList();
list = list.add(1).add(2).add(3);
PersistentList<Integer> modifiedList = list.add(4);
System.out.println("Original list=" + list + " => modified list=" + modifiedList);
```
prints

    Original list={1,2,3} => modified list={1,2,3,4}

### Set
```java
PersistentSet<String> set = PersistentCollections.persistentBinTreeSet();
// or PersistentCollections.persistentHashSet();
set = set.put("A").put("B").put("C");
PersistentSet<String> modifiedSet = set.remove("B");
System.out.println("Original set=" + set + " => modified set=" + modifiedSet);
```
prints

    Original set={A,B,C} => modified set={A,C}

### Map
```java
PersistentMap<Integer, String> map = PersistentCollections.persistentBinTreeMap();
map = map.put(1, "one").put(2, "two");
PersistentMap<Integer, String> modifiedMap = map.put(3, "three");
System.out.println("Original map=" + map + " => modified map=" + modifiedMap);
```
prints

    Original map={[1 -> one],[2 -> two]} => modified map={[1 -> one],[2 -> two],[3 -> three]}
