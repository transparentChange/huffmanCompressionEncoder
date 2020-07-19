package encoder;

import java.util.Iterator;
import java.util.Objects;

/*
 * CustomHashMap
 * This class is an implementation of an iterable hashmap.
 * Admittedly, some ideas behind this code have been taken from
 * https://blog.miyozinc.com/algorithms/custom-hashmap-implementation-in-java/, 
 * and the getHash() and hashCode() functions were copied from the official
 * Oracle source code
 */
public class CustomHashMap<K, V> implements Iterable<K> {
	private Entry<K, V>[] buckets;
	private static final int INITIAL_CAPACITY = 1 << 4;
	private int size = 0;
	
	public CustomHashMap() {
		this(INITIAL_CAPACITY);
	}
	
	public CustomHashMap(int capacity) {
		this.buckets = new Entry[capacity];
	}
	
	public int size() {
		return size;
	}
	
	public void put(K key, V value) {
		Entry<K, V> newEntry = new Entry<K, V>(key, value, null);
		
		int bucket = getHash(key) % buckets.length;
		Entry<K, V> currEntry = buckets[bucket];
		if (currEntry == null) {
			buckets[bucket] = newEntry;
			size++;
		} else {
			while (currEntry.getNext() != null) {
				if (key.equals(currEntry.getKey())) {
					currEntry.setValue(value);
					return;
				}
				
				currEntry = currEntry.getNext();
			}
			
			if (key.equals(currEntry.getKey())) {
				currEntry.setValue(value);
			} else {
				currEntry.next = newEntry;
				size++;
			}
		}
	}
	
	public V get(K key) {
		int bucket = getHash(key) % buckets.length;
		Entry<K, V> currEntry = buckets[bucket];
		while (currEntry != null) {
			if (currEntry.getKey().equals(key)) {
				return currEntry.getValue();
			} 
			
			currEntry = currEntry.getNext();
		}
			
		return null;
	}
	
	static final int getHash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }
	
	@Override
	public Iterator<K> iterator() {
		return new MapItr();
	}
	
	public class Entry<K, V> {
		private final K key;
		private V value;
		private Entry<K, V> next;
		
		public Entry(K key, V value, Entry<K, V> next) {
			this.key = key;
			this.value = value;
			this.next = next;
		}

		public K getKey() {
			return key;
		}
		
		public V getValue() {
			return value;
		}
		
		public V setValue(V value) {
			V prevValue = value;
			this.value = value;
			
			return prevValue;
		}
		
		public Entry<K, V> getNext() {
			return next;
		}
		
		public final int hashCode() {
            return Objects.hashCode(key) ^ Objects.hashCode(value);
        }
	}
	
	private class MapItr implements Iterator<K> {
		private int index = -1;
		private int count = 0;
		private Entry<K, V> currEntry; 
		
		@Override
		public boolean hasNext() {
			if (count < size) {
				return true;
			} else {
				return false;
			}
		}
		
		@Override
		public K next() {
			if (hasNext()) {
				count++;
				if ((currEntry == null) || (currEntry.getNext() == null)) {
					do {
						index++;
						currEntry = buckets[index];
					} while (currEntry == null);
				} else {
					currEntry = currEntry.getNext();
				} 
				return currEntry.getKey();
			} else {
				return null;
			}
		}
	}
}
