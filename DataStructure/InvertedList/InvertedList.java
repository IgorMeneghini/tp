package DataStructure.InvertedList;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Model.Film;
import archive.Crud;

/**
 * InvertedList
 */
public class InvertedList {
    private Map<String, List<Long>> index;
    RandomAccessFile raf;

    // Construtor
    public InvertedList() {
        index = new HashMap<>();
        try {
            raf = new RandomAccessFile("DataBase/films.db", "r");
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void startList() throws IOException {
        raf.seek(4);
        while (raf.getFilePointer() < raf.length()) {
            long address = raf.getFilePointer();
            Film film = Crud.readFilm(raf);
            String type = film.getType();
            insert(type, address);
        }
    }

    public void insert(Film film) {
        long address = Crud.getFilePointerFilm(film.getId());
        String type = film.getType();
        insert(type, address);
    }

    public void insert(String type, long address) {
        if (!index.containsKey(type)) {
            index.put(type, new ArrayList<>());
        }
        index.get(type).add(address);
    }

    public boolean remove(Film film) {
        boolean resp = false;
        String type = film.getType();

        if (index.containsKey(type)) {
            long pos = Crud.getFilePointerFilm(film.getId());
            index.get(type).remove(pos);

            if (index.get(type).isEmpty()) {
                index.remove(type);
            }
            resp = true;
        }
        return resp;
    }

    public boolean search(Film film) {
        long pos = Crud.getFilePointerFilm(film.getId());
        if (index.containsKey(film.getType())) {
            return search(film.getType(), pos);
        } else {
            return false;
        }
    }

    public boolean search(String type, long pos) {
        return index.get(type).contains(pos);
    }
}