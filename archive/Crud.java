package archive;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import Model.Film;

public class Crud {
    private static final String dbFilePath = "DataBase/films.db"; // Path to the database file
    private static final char tombstone = '¬'; // Tombstone character used for marking deleted records
    static private DataOutputStream dos; // DataOutputStream for writing to the database file

    // Constructor for the Crud class
    public Crud() {
        try {
            // Create a new FileOutputStream for the database file
            FileOutputStream arq = new FileOutputStream(dbFilePath);
            dos = new DataOutputStream(arq);
            dos.writeInt(0); // Write an initial value of 0 to the file
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Constructor for the Crud class with a boolean parameter (not used in the
    // code)

    public Crud(boolean startThis) {
    }

    // Method to create a new film record in the database
    public void create(Film film) {
        try (RandomAccessFile raf = new RandomAccessFile(dbFilePath, "rw")) {
            raf.seek(0);
            int lastID = raf.readInt(); // Read the last ID from the file
            raf.seek(0);
            film.setId(lastID + 1); // Set the ID of the new film
            raf.writeInt(film.getId()); // Write the new ID to the file

            byte[] array = film.toByteArray(); // Convert the film object to a byte array
            raf.seek(raf.length());
            raf.writeChar(tombstone); // Write the tombstone character
            raf.writeInt(array.length); // Write the length of the film data
            raf.write(array); // Write the film data
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Overloaded create method that takes a custom database file path as a
    // parameter
    public void create(Film film, String dbFilePath) {
        try (RandomAccessFile raf = new RandomAccessFile(dbFilePath, "rw")) {
            int lastID = 0;
            if (raf.length() != 0) {
                raf.seek(0);
                lastID = raf.readInt();
            }
            raf.seek(0);
            film.setId(lastID + 1);
            raf.writeInt(film.getId());

            byte[] array = film.toByteArray();
            raf.seek(raf.length());
            raf.writeChar(tombstone);
            raf.writeInt(array.length);
            raf.write(array);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to search for a film by ID
    public Film sequencialSearch(int id) {
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(dbFilePath, "rw")) {
            randomAccessFile.seek(4);
            while (randomAccessFile.getFilePointer() < randomAccessFile.length()) {
                char tombstone = randomAccessFile.readChar();
                int filmSize = randomAccessFile.readInt();
                byte[] filmData = new byte[filmSize];
                randomAccessFile.read(filmData);

                Film film = new Film();
                film.fromByteArray(filmData);
                film.setTombstone(tombstone);
                if (film.getTombstone() != '&' && film.getId() == id) {
                    return film;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Method to get the file pointer position of a film record by ID
    public static long getFilePointerFilm(int id) {
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(dbFilePath, "rw")) {
            randomAccessFile.seek(4);
            while (randomAccessFile.getFilePointer() < randomAccessFile.length()) {
                long pos = randomAccessFile.getFilePointer();
                char tombstone = randomAccessFile.readChar();
                int filmSize = randomAccessFile.readInt();
                byte[] filmData = new byte[filmSize];
                randomAccessFile.read(filmData);

                Film film = new Film();
                film.fromByteArray(filmData);
                film.setTombstone(tombstone);
                if (film.getTombstone() != '&' && film.getId() == id) {
                    return pos;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Method to read and print all film records from the database
    public void readAll(String dbFilePath) {
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(dbFilePath, "rw")) {
            randomAccessFile.seek(4);
            while (randomAccessFile.getFilePointer() < randomAccessFile.length()) {
                char tombstone = randomAccessFile.readChar();
                int filmSize = randomAccessFile.readInt();
                byte[] filmData = new byte[filmSize];
                randomAccessFile.read(filmData);

                Film film = new Film();
                film.fromByteArray(filmData);
                film.setTombstone(tombstone);
                if (film.getTombstone() != '&') {
                    System.out.println(film.toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to update an existing film record
    public boolean update(Film newFilm) {
        try (RandomAccessFile raf = new RandomAccessFile(dbFilePath, "rw")) {
            raf.seek(4);

            long index = raf.getFilePointer();
            long EOF = raf.length();

            while (index < EOF) {
                if (raf.readChar() == tombstone) {
                    int filmSize = raf.readInt();
                    byte[] filmData = new byte[filmSize];
                    raf.read(filmData);

                    Film film = new Film();
                    film.fromByteArray(filmData);

                    if (film.getId() == newFilm.getId()) {
                        byte[] newFilmData = newFilm.toByteArray();
                        if (newFilmData.length <= filmData.length) {
                            raf.seek(index);
                            raf.writeChar(tombstone);
                            raf.writeInt(newFilmData.length);
                            raf.write(newFilmData);
                        } else {
                            raf.seek(index);
                            raf.writeChar('&');
                            create(newFilm);
                        }
                        return true;
                    }
                }
                index = raf.getFilePointer();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Method to delete a film record by ID
    public boolean delete(int id) {
        try (RandomAccessFile raf = new RandomAccessFile(dbFilePath, "rw")) {
            raf.seek(4); // Move the file pointer past the initial integer (not explained in the code)

            while (raf.getFilePointer() < raf.length()) { 
                long pos = raf.getFilePointer(); 

                Film film = new Film();
                film.setTombstone(raf.readChar()); 

                int filmSize = raf.readInt(); 
                byte[] filmData = new byte[filmSize]; 

                raf.read(filmData); 
                film.fromByteArray(filmData); 
                // Check if the film is marked for deletion (tombstone character) and has the
                // specified ID
                if (film.getTombstone() == tombstone && film.getId() == id) {
                    raf.seek(pos);
                    raf.writeChar('&'); 
                    return true; // Return true to indicate successful deletion
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); // Handle any IOException by printing the stack trace
        }
        return false; // Return false if the specified ID was not found or if there was an error
    }

    // Method to read a Film object from a RandomAccessFile
    public static Film readFilm(RandomAccessFile raf) throws IOException {
        if (raf.getFilePointer() < raf.length()) { 
            Film film = new Film();
            film.setTombstone(raf.readChar());

            // Read and skip through records until a tombstone character is encountered
            while (film.getTombstone() != tombstone) {
                int registerSize = raf.readInt();
                raf.skipBytes(registerSize); 
                film.setTombstone(raf.readChar()); 
            }

            int registerSize = raf.readInt(); 
            byte[] filmData = new byte[registerSize]; 

            raf.read(filmData); 
            film.fromByteArray(filmData); 

            return film; // Return the deserialized Film object
        }
        return null; // Return null if there is no more data to read
    }

    // Method to create a film record in a RandomAccessFile (not explained in the
    // code)
    public static void createInTempFiles(Film film, RandomAccessFile raf) {
        try {
            byte[] array = film.toByteArray(); 
            raf.seek(raf.length()); 
            raf.writeChar(tombstone);
            raf.writeInt(array.length); 
            raf.write(array); 
        } catch (IOException e) {
            e.printStackTrace(); // Handle any IOException by printing the stack trace
        }
    }
}