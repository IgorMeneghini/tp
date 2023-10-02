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
 * InvertedList class represents an inverted list data structure for storing and
 * searching films by type.
 */
public class InvertedList {
    private Map<String, List<Long>> index;
    private RandomAccessFile raf;

    /**
     * Constructor initializes the inverted list and opens the database file.
     */
    public InvertedList() {
        index = new HashMap<>();
        try {
            raf = new RandomAccessFile("DataBase/films.db", "r");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes the inverted list by reading films from the database file.
     *
     * @throws IOException if there is an issue reading the file.
     */
    public void startList() throws IOException {
        raf.seek(4);
        while (raf.getFilePointer() < raf.length()) {
            long address = raf.getFilePointer();
            Film film = Crud.readFilm(raf);
            String type = film.getType();
            insert(type, address);
        }
    }

    /**
     * Inserts a film into the inverted list.
     *
     * @param film The film to insert.
     */
    public void insert(Film film) {
        Crud crud = new Crud(false);
        crud.create(film);
        long address = Crud.getFilePointerFilm(film.getId());
        String type = film.getType();
        insert(type, address);
    }

    /**
     * Inserts a film address associated with its type into the inverted list.
     *
     * @param type    The type of the film.
     * @param address The address of the film in the database.
     */
    public void insert(String type, long address) {
        if (!index.containsKey(type)) {
            index.put(type, new ArrayList<>());
        }
        index.get(type).add(address);
    }

    /**
     * Removes a film from the inverted list.
     *
     * @param film The film to remove.
     * @return True if the film was removed successfully, false otherwise.
     */
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
        if (index.containsKey(film.getType())) {
            return search(film.getType(), pos);
        } else {
            return false;
        }
    }

    /**
     * Searches for a film address associated with its type in the inverted list.
     *
     * @param type The type of the film.
     * @param pos  The address of the film in the database.
     * @return True if the film address is found in the inverted list, false
     *         otherwise.
     */
    public boolean search(String type, long pos) {
        return index.get(type).contains(pos);
    }
}
