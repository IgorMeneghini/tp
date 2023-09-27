package app;

import java.io.IOException;

import archive.FilmParser;
import archive.ExternalMergeSorting;

class Main {

    public static void main(String[] args) throws IOException {
        FilmParser.start();
        ExternalMergeSorting externalMergeSorting = new ExternalMergeSorting();

        // Film film = new Film(8449, "Igor O criador", "Movie", 9999999, "R,90 min");
        // crud.update(film);
        // System.out.println(crud.delete(8449));
        // System.out.println(crud.sequencialSearch(8450));
        externalMergeSorting.externalMergeSortSort(1000);
    }

}