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

    public void insert(Film film) {
        Crud crud = new Crud(false);
        crud.create(film);
        long pos = Crud.getFilePointerFilm(film.getId());
        set(film, pos);
    }

    public void set(Film film, Long address) {
        int key = film.getId();
        int bucketIndex = calculateBucketIndex(key);
        HashMap<Integer, Long> bucket = directory.get(bucketIndex);
        bucket.put(key, address);
        if (bucket.size() > bucketSize) {
            extendDirectory(bucketIndex);
        }
    }

    private int calculateBucketIndex(int key) {
        int bucketIndex = key * (int) Math.pow(2, globalDepth - 1); // Cálculo do índice do bucket
        return bucketIndex % directory.size(); // Retorna o índice ajustado para o tamanho da lista de buckets
    }

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
        return null; // Returns null if the film is not found
    }

}
