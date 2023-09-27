package archive;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import Model.Film;

public class ExternalMergeSorting {
    private RandomAccessFile raf;
    private final String dbFilePath = "DataBase/films.db";
    private String outputFilePath;

    public ExternalMergeSorting() {
        try {
            raf = new RandomAccessFile(dbFilePath, "r");
            raf.seek(4);
            this.outputFilePath = "sortedFilms.db";
            RandomAccessFile raf = new RandomAccessFile(outputFilePath, "rw");
            raf.setLength(0);
            raf.close();
        } catch (Exception e) {
        }
    }

    public void externalMergeSortSort(int chunkSize) throws IOException {
        List<String> chunkFileNames = splitIntoSortedChunks(chunkSize);
        mergeSortedChunks2(chunkFileNames, outputFilePath);
        cleanupTempFiles(chunkFileNames);
    }

    List<String> splitIntoSortedChunks(int chunkSize) {
        List<String> chunkFileNames = new ArrayList<>();
        int chunkCount = 0;

        try {
            List<Film> chunk = new ArrayList<>();
            while (raf != null && raf.getFilePointer() < raf.length()) {
                Film film = Crud.readFilm(raf);
                chunk.add(film);
                if (chunk.size() >= chunkSize) {
                    quickSort(chunk);
                    String chunkFileName = "DataBase/chunk" + chunkCount + ".db";
                    chunkFileNames.add(chunkFileName);
                    try (RandomAccessFile tempRaf = new RandomAccessFile(chunkFileName, "rw")) {
                        for (Film item : chunk) {
                            Crud.createInTempFiles(item, tempRaf);
                        }
                    }
                    chunk.clear();
                    chunkCount++;
                }
            }
            if (!chunk.isEmpty()) {
                quickSort(chunk);
                String chunkFileName = "DataBase/chunk" + chunkCount + ".db";
                chunkFileNames.add(chunkFileName);
                try (RandomAccessFile tempRaf = new RandomAccessFile(chunkFileName, "rw")) {
                    for (Film item : chunk) {
                        Crud.createInTempFiles(item, tempRaf);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return chunkFileNames;
    }

    private void mergeSortedChunks2(List<String> chunkFileNames, String outputFile) throws IOException {
        Crud crud = new Crud();
        List<RandomAccessFile> tempRaf = createRandomAccessFiles(chunkFileNames);
        List<Film> queue = startQueue(tempRaf);

        while (notEmpty(queue, chunkFileNames.size())) {
            Film film = lower(queue);
            if (film != null) {
                int index = queue.indexOf(film);
                if (tempRaf.get(index).getFilePointer() < tempRaf.get(index).length()) {
                    queue.set(index, Crud.readFilm(tempRaf.get(index)));
                } else {
                    queue.set(index, null);
                }
                crud.create(film, outputFile);
            }
        }
        crud.readAll(outputFile);
    }

    boolean notEmpty(List<Film> queue, int size) {
        int cont = 0;
        for (Film film : queue) {
            if (film == null) {
                cont++;
            }
        }
        if (cont == size) {
            return false;
        }
        return true;
    }

    private Film lower(List<Film> queue) {
        Film lower = queue.get(0);
        if (lower != null) {
            for (Film film : queue) {
                if (film != null && lower.getTitle().compareTo(film.getTitle()) > 0) {
                    lower = film;
                }
            }
        } else {
            for (int i = 1; i < queue.size(); i++) {
                lower = queue.get(i);
                if (lower != null) {
                    break;
                }
            }
            if (lower != null) {
                for (Film film : queue) {
                    if (film != null && lower.getTitle().compareTo(film.getTitle()) > 0) {
                        lower = film;
                    }
                }
            }
        }
        return lower;
    }

    private List<Film> startQueue(List<RandomAccessFile> tempRaf) throws IOException {
        List<Film> queue = new ArrayList<>();
        for (RandomAccessFile randomAccessFile : tempRaf) {
            queue.add(Crud.readFilm(randomAccessFile));
        }
        return queue;
    }

    private List<RandomAccessFile> createRandomAccessFiles(List<String> chunkFileNames) throws FileNotFoundException {
        List<RandomAccessFile> tempRaf = new ArrayList<>();
        for (String chunkFileName : chunkFileNames) {
            tempRaf.add(new RandomAccessFile(chunkFileName, "rw"));
        }
        return tempRaf;
    }

    private void cleanupTempFiles(List<String> chunkFileNames) {
        for (String fileName : chunkFileNames) {
            File file = new File(fileName);
            if (file.exists()) {
                file.delete();
            }
        }
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
