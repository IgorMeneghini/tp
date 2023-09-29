package DataStructure;

import java.util.ArrayList;
import java.util.List;

public class ExtendibleHashTable<K, V> {
    private List<Bucket<Integer, Long>> directory;
    private int directoryDepth;
    private int bucketSize;

    public ExtendibleHashTable(int bucketSize) {
        this.directoryDepth = 0;
        this.bucketSize = bucketSize;
        this.directory = new ArrayList<>();
        for (int i = 0; i < bucketSize; i++) {
            this.directory.add(new Bucket<>()); // Inicializa os buckets como listas vazias
        }
    }

    public void startHash(){
        
    }

    public void set(int key, Long value) {
        int bucketIndex = calculateBucketIndex(key); 
        Bucket<Integer, Long> bucket = directory.get(bucketIndex); 
        bucket.put(key, value);
        if (bucket.size() > bucketSize) { // checking if the memory has run out
            extendDirectory(bucketIndex); // expand the director increasing the depth and rehashing the bucket that blew out the memory
        }
    }

    private void extendDirectory(int bucketIndex) {
        Bucket<Integer, Long> oldBucket = directory.get(bucketIndex);
        directory.get(bucketIndex).entries.clear();
        this.directoryDepth++;
        for (int i = directory.size(); i < (int) Math.pow(2, directoryDepth); i++) {
            this.directory.add(new Bucket<>());
        }
        for (Integer key : oldBucket.entries.keySet()) {
            Long value = oldBucket.get(key);
            set(key,value);
        }
    }

    private int calculateBucketIndex(int key) {
        return key % (int) Math.pow(2, directoryDepth);
    }

}
