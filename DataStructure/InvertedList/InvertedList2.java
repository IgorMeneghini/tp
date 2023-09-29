package DataStructure.InvertedList;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Model.Film;
import archive.Crud;

public class InvertedList2 {
    private Map<String, List<Long>> index;
    RandomAccessFile raf;

    // Construtor
    public InvertedList2() {
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
            String duration = film.getTimeDuration();
            insert(duration, address);
        }
    }

    public void insert(Film film) {
        long address = Crud.getFilePointerFilm(film.getId());
        String duration = film.getTimeDuration();
        insert(duration, address);
    }

    public void insert(String duration, long address) {
        if (!index.containsKey(duration)) {
            index.put(duration, new ArrayList<>());
        }
        index.get(duration).add(address);
    }

    public boolean remove(Film film) {
        boolean resp = false;
        String duration = film.getTimeDuration();

        if (index.containsKey(duration)) {
            long pos = Crud.getFilePointerFilm(film.getId());
            index.get(duration).remove(pos);

            if (index.get(duration).isEmpty()) {
                index.remove(duration);
            }
            resp = true;
        }
        return resp;
    }

    public boolean search(Film film) {
        long pos = Crud.getFilePointerFilm(film.getId());
        if (index.containsKey(film.getTimeDuration())) {
            return search(film.getTimeDuration(), pos);
        } else {
            return false;
        }
    }

    public boolean search(String duration, long pos) {
        return index.get(duration).contains(pos);
    }
}
