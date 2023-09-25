package archive;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import Model.Film;

public class ExternalSorting {
    private static char tombstone = '¬';
    private static RandomAccessFile raf;
    private static final String dbFilePath = "DataBase/films.db";

    ExternalSorting() {
        try {
            raf = new RandomAccessFile(dbFilePath, "rw");
            raf.seek(4);
        } catch (Exception e) {
        }
    }

    private static Film readFilm(RandomAccessFile raf) throws IOException {
        if (raf.getFilePointer() < raf.length()) {
            Film film = new Film();
            film.setTombstone(raf.readChar());

            while (film.getTombstone() != tombstone) {
                int registerSize = raf.readInt();
                raf.skipBytes(registerSize);
            }
            film.setTombstone(raf.readChar());
            int registerSize = raf.readInt();
            byte[] filmData = new byte[registerSize];
            raf.read(filmData);
            return film;
        }
        return null;
    }

    void externalMergeSortSort(int numberOfWays, int memorySize) throws IOException {
        List<String> tempFilesName = new ArrayList<>();
        List<String> tempSubFilesName = new ArrayList<>();
        List<RandomAccessFile> tempRaf = new ArrayList<>();
        List<RandomAccessFile> tempSubRaf = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            tempSubFilesName.add("tempSubFile" + i + ".db");
            tempFilesName.add("tempFile" + i + ".db");
            tempRaf.add(new RandomAccessFile(tempFilesName.get(i), "rw"));
            tempSubRaf.add(new RandomAccessFile(tempSubFilesName.get(i), "rw"));
        }
        distribution(memorySize, numberOfWays, tempRaf);
        intercalation(tempRaf, tempSubRaf, numberOfWays, memorySize);
    }

    void distribution(int memorySize, int numberOfWays, List<RandomAccessFile> tempRaf) throws IOException {
        int circularCounter = 0;
        while (true) {
            if (raf.getFilePointer() >= raf.length()) {
                break;
            }
            List<Film> buffer = new ArrayList<>();

            for (int i = 0; i < memorySize; i++) {
                Film film = readFilm(raf);
                if (film == null) {
                    break;
                }
                buffer.add(film);
            }
            for (Film item : buffer) {
                Crud.createInTempFiles(item, tempRaf.get(circularCounter));
            }
            circularCounter = (++circularCounter) % numberOfWays;
        }
    }

    void intercalation(List<RandomAccessFile> tempRaf, List<RandomAccessFile> tempSubRaf, int numberOfWays,
            int memorySize)
            throws IOException {
        int circularCounter = 0;
        int subCircularCounter = 0;
        for (int i = 0; i < numberOfWays * memorySize; i++) {// vai percorrer o numero de elementos que tem que ser add
                                                             // ao novoarquivo exemplo intercarlar 3 registros ai vão
                                                             // ser 6 com 2 caminhos
            long getFilePointerLast = tempRaf.get(0).getFilePointer();
            int getIndexFromLastLower = 0;
            Film lower = readFilm(tempRaf.get(0));
            for (int j = 1; j < numberOfWays; j++) {
                long tempGetFilePointerLast = tempRaf.get(j).getFilePointer();
                Film tempFilm = readFilm(tempRaf.get(j));

                if (lower.getTitle().compareTo(tempFilm.getTitle()) > 0) {
                    lower = tempFilm;
                    tempRaf.get(getIndexFromLastLower).seek(getFilePointerLast);
                    getIndexFromLastLower = j;
                    getFilePointerLast = tempRaf.get(j).getFilePointer();
                } else {
                    tempRaf.get(j).seek(tempGetFilePointerLast);
                }
            }

        }

        circularCounter = (++circularCounter) % numberOfWays;
        subCircularCounter = (++subCircularCounter) % numberOfWays;
    }

    boolean isSorted(List<RandomAccessFile> tempRaf) throws IOException {
        int count = 0;
        for (RandomAccessFile raf : tempRaf) {
            if (raf.length() != 0) {
                count++;
            }
        }
        if (count == 1) {
            return true;
        }
        return false;
    }

    public void quickSort(List<Film> arr) {
        if (arr == null || arr.size() <= 1) {
            return; // No need to sort
        }
        quickSort(arr, 0, arr.size() - 1);
    }   

    private void quickSort(List<Film> arr, int low, int high) {
        if (low < high) {
            int pivotIndex = partition(arr, low, high);
            quickSort(arr, low, pivotIndex - 1);
            quickSort(arr, pivotIndex + 1, high);
        }
    }

    private int partition(List<Film> arr, int low, int high) {
        Film pivot = arr.get(high);
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (arr.get(j).getTitle().compareTo(pivot.getTitle()) < 0) {
                i++;
                swap(arr, i, j);
            }
        }

        swap(arr, i + 1, high);
        return i + 1;
    }

    private void swap(List<Film> arr, int i, int j) {
        Film temp = arr.get(i);
        arr.set(i, arr.get(j));
        arr.set(j, temp);
    }
}
