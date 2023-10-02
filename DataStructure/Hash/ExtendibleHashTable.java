package DataStructure.Hash;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Model.Film;
import archive.Crud;

public class ExtendibleHashTable<K, V> {
    private List<HashMap<Integer, Long>> directory;
    private int globalDepth;
    private int bucketSize;

    public ExtendibleHashTable(int bucketSize) {
        this.globalDepth = 0;
        this.bucketSize = bucketSize;
        this.directory = new ArrayList<>();
        this.directory.add(new HashMap<>());
    }

    // Populate the hash table by reading data from a file
    public void startHash() throws IOException {
        RandomAccessFile raf = new RandomAccessFile("DataBase/films.db", "r");
        raf.seek(4);

        while (raf.getFilePointer() < raf.length()) {
            long address = raf.getFilePointer();
            Film film = Crud.readFilm(raf);
            set(film, address);
        }

        raf.close(); // Close the file after reading
    }

    // Insert a film into the hash table
    public void insert(Film film) {
        Crud crud = new Crud(false);
        crud.create(film);
        long pos = Crud.getFilePointerFilm(film.getId());
        set(film, pos);
    }

    // Set a film in the hash table
    private void set(Film film, Long address) {
        int key = film.getId();
        int bucketIndex = calculateBucketIndex(key);
        HashMap<Integer, Long> bucket = directory.get(bucketIndex);
        bucket.put(key, address);
        if (bucket.size() > bucketSize) {
            extendDirectory(bucketIndex);
        }
    }

    // Calculate the bucket index for a given key
    private int calculateBucketIndex(int key) {
        int bucketIndex = key * (int) Math.pow(2, globalDepth - 1); // Calculate the bucket index
        return bucketIndex % directory.size(); // Adjust the index to fit within the directory size
    }

    // Extend the directory by splitting a bucket
    private void extendDirectory(int bucketIndex) {
        HashMap<Integer, Long> oldBucket = directory.get(bucketIndex);
        HashMap<Integer, Long> newBucket = new HashMap<>();
        directory.add(newBucket);
        for (Map.Entry<Integer, Long> entry : oldBucket.entrySet()) {
            Integer key = entry.getKey();
            long value = entry.getValue();
            int newBucketIndex = calculateBucketIndex(key);
            if (bucketIndex != newBucketIndex) {
                newBucket.put(key, value);
                oldBucket.remove(key);
            }
        }
        if (globalDepth == Math.ceil(Math.log(directory.size()) / Math.log(2))) {
            resizeHashTable();
        }
    }

    // Resize the hash table when the global depth matches the directory size
    private void resizeHashTable() {
        List<HashMap<Integer, Long>> oldDirectory = directory;
        globalDepth++;
        bucketSize *= 2;
        directory = new ArrayList<>();
        for (int i = 0; i < bucketSize; i++) {
            directory.add(new HashMap<Integer, Long>());
        }
        for (HashMap<Integer, Long> bucket : oldDirectory) {
            for (Map.Entry<Integer, Long> entry : bucket.entrySet()) {
                Integer key = entry.getKey();
                long value = entry.getValue();
                Film film = new Film(key);
                set(film, value);
            }
        }
    }

    // Get a film from the hash table based on its key
    public Film get(int key) throws IOException {
        RandomAccessFile raf = new RandomAccessFile("DataBase/films.db", "r");

        int bucketIndex = calculateBucketIndex(key);
        HashMap<Integer, Long> bucket = directory.get(bucketIndex);

        for (Map.Entry<Integer, Long> entry : bucket.entrySet()) {
            Integer tempKey = entry.getKey();
            if (tempKey.equals(key)) { // Use equals for object comparison
                raf.seek(entry.getValue());
                Film film = Crud.readFilm(raf);
                raf.close(); // Close the file here
                return film;
            }
        }

        raf.close(); // Close the file in case the film is not found
        return null; // Return null if the film is not found
    }
}
