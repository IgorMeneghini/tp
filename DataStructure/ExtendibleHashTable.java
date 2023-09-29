package DataStructure;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Model.Film;
import archive.Crud;

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

    public void startHash() throws IOException {
        RandomAccessFile raf = new RandomAccessFile("DataBase/films.db", "r");
        raf.seek(4);
        while (raf.getFilePointer() < raf.length()) {
            long address = raf.getFilePointer();
            Film film = Crud.readFilm(raf);
            int key = film.getId();
            set(key, address);
        }
    }

    public void set(int key, Long value) {
        int bucketIndex = calculateBucketIndex(key);
        Bucket<Integer, Long> bucket = directory.get(bucketIndex);
        bucket.put(key, value);
        if (bucket.size() > bucketSize) { // checking if the memory has run out
            extendDirectory(bucketIndex); // expand the director increasing the depth and rehashing the bucket that blew
                                          // out the memory
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
            set(key, value);
        }
    }

    public Film get(int key) throws IOException {
    RandomAccessFile raf = new RandomAccessFile("DataBase/films.db", "r");

    int bucketIndex = calculateBucketIndex(key);
    Bucket<Integer, Long> bucket = directory.get(bucketIndex);
    
    for (Map.Entry<Integer, Long> entry : bucket.entries.entrySet()) {
        Integer tempKey = entry.getKey();
        if (tempKey.equals(key)) {
            raf.seek(entry.getValue());
            Film film = Crud.readFilm(raf);
            raf.close(); // Close the file here
            return film;
        }
    }
    
    raf.close(); // Close the file in case the film is not found
    return null; // Returns null if the film is not found
}


    private int calculateBucketIndex(int key) {
        int bucketIndex = key * (int) Math.pow(2, directoryDepth - 1);
        return bucketIndex % directory.size();
    }

}
