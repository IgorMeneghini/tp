package Algorithm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import Model.Film;
import archive.Crud;

public class ExternalMergeSorting {
    private RandomAccessFile raf; // RandomAccessFile for reading the input database
    private final String dbFilePath = "DataBase/films.db"; // Path to the input database file
    private String outputFilePath; // Path to the output sorted database file

    public ExternalMergeSorting() {
        try {
            // Initialize the RandomAccessFile for reading the input database
            raf = new RandomAccessFile(dbFilePath, "r");
            raf.seek(4); // Skip the initial data
            this.outputFilePath = "DataBase/sortedFilms.db"; // Set the output file path

            // Create and initialize a new RandomAccessFile for writing the sorted output
            RandomAccessFile raf = new RandomAccessFile(outputFilePath, "rw");
            raf.setLength(0); // Clear the output file
            raf.close(); // Close the output file
        } catch (Exception e) {
            // Handle any exceptions that may occur during initialization
        }
    }

    public void externalMergeSortSort(int chunkSize) throws IOException {
        // Split the input data into sorted chunks
        List<String> chunkFileNames = splitIntoSortedChunks(chunkSize);

        // Merge the sorted chunks into the output file
        mergeSortedChunks2(chunkFileNames, outputFilePath);

        // Clean up temporary chunk files
        cleanupTempFiles(chunkFileNames);
    }

    List<String> splitIntoSortedChunks(int chunkSize) {
        List<String> chunkFileNames = new ArrayList<>();
        int chunkCount = 0;

        try {
            List<Film> chunk = new ArrayList<>();

            // Read and process the input data in chunks
            while (raf != null && raf.getFilePointer() < raf.length()) {
                Film film = Crud.readFilm(raf);
                chunk.add(film);

                if (chunk.size() >= chunkSize) {
                    // Sort the chunk and save it to a temporary file
                    quickSort(chunk);
                    String chunkFileName = "DataBase/chunk" + chunkCount + ".db";
                    chunkFileNames.add(chunkFileName);

                    // Create a new RandomAccessFile for the temporary chunk file
                    try (RandomAccessFile tempRaf = new RandomAccessFile(chunkFileName, "rw")) {
                        for (Film item : chunk) {
                            Crud.createInTempFiles(item, tempRaf);
                        }
                    }

                    chunk.clear();
                    chunkCount++;
                }
            }

            // Process the remaining data if any
            if (!chunk.isEmpty()) {
                quickSort(chunk);
                String chunkFileName = "DataBase/chunk" + chunkCount + ".db";
                chunkFileNames.add(chunkFileName);

                // Create a new RandomAccessFile for the remaining chunk data
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

    /**
     * Merge sorted chunks of data into a single output file.
     * 
     * @param chunkFileNames - The names of the chunk files to be merged.
     * @param outputFile     - The name of the output file.
     */
    private void mergeSortedChunks2(List<String> chunkFileNames, String outputFile) throws IOException {
        Crud crud = new Crud(true); // Initialize a CRUD instance for writing
        List<RandomAccessFile> tempRaf = createRandomAccessFiles(chunkFileNames); // Create RandomAccessFiles for
                                                                                  // reading chunks
        List<Film> queue = startQueue(tempRaf); // Initialize a queue for merging

        // Continue merging until there's data left in the queue
        while (notEmpty(queue, chunkFileNames.size())) {
            Film film = lower(queue); // Find the lowest film from the queue
            if (film != null) {
                int index = queue.indexOf(film); // Get the index of the lowest film in the queue
                if (tempRaf.get(index).getFilePointer() < tempRaf.get(index).length()) {
                    // Read the next film from the corresponding chunk if available
                    queue.set(index, Crud.readFilm(tempRaf.get(index)));
                } else {
                    queue.set(index, null); // Mark the queue as empty for this chunk
                }
                crud.create(film, outputFile); // Write the lowest film to the output file
            }
        }
        crud.readAll(outputFile); // Read and display all records from the sorted output file
    }

    /**
     * Check if there's any data left in the queue.
     * 
     * @param queue - The queue of films from different chunks.
     * @param size  - The total number of chunks.
     * @return True if there's data left in the queue; otherwise, false.
     */
    boolean notEmpty(List<Film> queue, int size) {
        int cont = 0;
        for (Film film : queue) {
            if (film == null) {
                cont++;
            }
        }
        if (cont == size) {
            return false; // All chunks are empty
        }
        return true; // At least one chunk has data remaining
    }

    /**
     * Find the lowest film based on the title from the queue.
     * 
     * @param queue - The queue of films from different chunks.
     * @return The lowest film in the queue.
     */
    private Film lower(List<Film> queue) {
        Film lower = queue.get(0); // Initialize with the first film in the queue
        if (lower != null) {
            for (Film film : queue) {
                if (film != null && lower.getTitle().compareTo(film.getTitle()) > 0) {
                    lower = film; // Update 'lower' if a lower film is found
                }
            }
        } else {
            for (int i = 1; i < queue.size(); i++) {
                lower = queue.get(i);
                if (lower != null) {
                    break; // Find the first non-null film
                }
            }
            if (lower != null) {
                for (Film film : queue) {
                    if (film != null && lower.getTitle().compareTo(film.getTitle()) > 0) {
                        lower = film; // Update 'lower' if a lower film is found
                    }
                }
            }
        }
        return lower; // Return the lowest film in the queue
    }

    /**
     * Initialize a queue of films by reading the first film from each
     * RandomAccessFile.
     * 
     * @param tempRaf - List of RandomAccessFiles representing chunk files.
     * @return List of films in the initial queue.
     */
    private List<Film> startQueue(List<RandomAccessFile> tempRaf) throws IOException {
        List<Film> queue = new ArrayList<>();
        for (RandomAccessFile randomAccessFile : tempRaf) {
            queue.add(Crud.readFilm(randomAccessFile)); // Read the first film from each chunk
        }
        return queue;
    }

    /**
     * Create a list of RandomAccessFiles for reading chunk files.
     * 
     * @param chunkFileNames - List of chunk file names.
     * @return List of RandomAccessFiles for reading the chunk files.
     */
    private List<RandomAccessFile> createRandomAccessFiles(List<String> chunkFileNames) throws FileNotFoundException {
        List<RandomAccessFile> tempRaf = new ArrayList<>();
        for (String chunkFileName : chunkFileNames) {
            tempRaf.add(new RandomAccessFile(chunkFileName, "rw")); // Create RandomAccessFiles for reading the chunks
        }
        return tempRaf;
    }

    /**
     * Clean up temporary chunk files by deleting them.
     * 
     * @param chunkFileNames - List of temporary chunk file names.
     */
    private void cleanupTempFiles(List<String> chunkFileNames) {
        for (String fileName : chunkFileNames) {
            File file = new File(fileName);
            if (file.exists()) {
                file.delete(); // Delete the temporary chunk files
            }
        }
    }

    /**
     * Perform a quicksort on the list of films.
     * 
     * @param arr - List of films to be sorted.
     */
    public void quickSort(List<Film> arr) {
        if (arr == null || arr.size() <= 1) {
            return; // No need to sort if the list is empty or has only one element
        }
        quickSort(arr, 0, arr.size() - 1);
    }

    /**
     * Recursive function to perform quicksort on a portion of the list.
     * 
     * @param arr  - List of films to be sorted.
     * @param low  - Starting index of the portion to be sorted.
     * @param high - Ending index of the portion to be sorted.
     */
    private void quickSort(List<Film> arr, int low, int high) {
        if (low < high) {
            int pivotIndex = partition(arr, low, high);
            quickSort(arr, low, pivotIndex - 1); // Recursively sort the left partition
            quickSort(arr, pivotIndex + 1, high); // Recursively sort the right partition
        }
    }

    /**
     * Partitioning step of the quicksort algorithm.
     * 
     * @param arr  - List of films to be sorted.
     * @param low  - Starting index of the partition.
     * @param high - Ending index of the partition.
     * @return Index of the pivot element.
     */
    private int partition(List<Film> arr, int low, int high) {
        Film pivot = arr.get(high); // Choose the pivot element (last element in the partition)
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (arr.get(j).getTitle().compareTo(pivot.getTitle()) < 0) {
                i++;
                swap(arr, i, j); // Swap elements if they are out of order
            }
        }

        swap(arr, i + 1, high); // Swap the pivot element into its correct position
        return i + 1;
    }

    /**
     * Helper method to swap two elements in a list.
     * 
     * @param arr - List of films.
     * @param i   - Index of the first element.
     * @param j   - Index of the second element.
     */
    private void swap(List<Film> arr, int i, int j) {
        Film temp = arr.get(i);
        arr.set(i, arr.get(j)); // Swap elements at index i and j
        arr.set(j, temp);
    }
}