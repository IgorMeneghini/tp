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
 * InvertedList2 class represents an inverted list data structure for storing and searching films by duration.
 */
public class InvertedList2 {
    private Map<String, List<Long>> index;
    private RandomAccessFile raf;

    /**
     * Constructor initializes the inverted list and opens the database file.
     */
    public InvertedList2() {
        index = new HashMap<>();
        try {
            raf = new RandomAccessFile("DataBase/films.db", "r");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes the inverted list by reading films from the database file and indexing them by duration.
     *
     * @throws IOException if there is an issue reading the file.
     */
    public void startList() throws IOException {
        raf.seek(4);
        while (raf.getFilePointer() < raf.length()) {
            long address = raf.getFilePointer();
            Film film = Crud.readFilm(raf);
            String duration = film.getTimeDuration();
            insert(duration, address);
        }
    }

    /**
     * Inserts a film into the inverted list indexed by duration.
     *
     * @param film The film to insert.
     */
    public void insert(Film film) {
        Crud crud = new Crud(false);
        crud.create(film);
        long address = Crud.getFilePointerFilm(film.getId());
        String duration = film.getTimeDuration();
        insert(duration, address);
    }

    /**
     * Inserts a film address associated with its duration into the inverted list.
     *
     * @param duration The duration of the film.
     * @param address  The address of the film in the database.
     */
    public void insert(String duration, long address) {
        if (!index.containsKey(duration)) {
            index.put(duration, new ArrayList<>());
        }
        index.get(duration).add(address);
    }

    /**
     * Removes a film from the inverted list.
     *
     * @param film The film to remove.
     * @return True if the film was removed successfully, false otherwise.
     */
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

    /**
     * Searches for a film in the inverted list.
     *
     * @param film The film to search for.
     * @return True if the film is found in the inverted list, false otherwise.
     */
    public boolean search(Film film) {
        long pos = Crud.getFilePointerFilm(film.getId());

        if (pos == 0) {
            return false;
        }
        if (index.containsKey(film.getTimeDuration())) {
            return search(film.getTimeDuration(), pos);
        } else {
            return false;
        }
    }

    /**
     * Searches for a film address associated with its duration in the inverted list.
     *
     * @param duration The duration of the film.
     * @param pos      The address of the film in the database.
     * @return True if the film address is found in the inverted list, false otherwise.
     */
    public boolean search(String duration, long pos) {
        return index.get(duration).contains(pos);
    }
}
