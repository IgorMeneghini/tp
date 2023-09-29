package archive;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import Model.Film;

public class Crud {
    private static final String dbFilePath = "DataBase/films.db";
    private static final char tombstone = '¬';
    static private DataOutputStream dos;

    public Crud() {
        try {
            FileOutputStream arq = new FileOutputStream(dbFilePath);
            dos = new DataOutputStream(arq);
            dos.writeInt(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Crud(boolean startThis) {
    }

    public void create(Film film) {
        try (RandomAccessFile raf = new RandomAccessFile(dbFilePath, "rw")) {
            raf.seek(0);
            int lastID = raf.readInt();
            raf.seek(0);
            film.setId(lastID + 1);
            raf.writeInt(film.getId());

            byte[] array = film.toByteArray();
            raf.seek(raf.length());
            raf.writeChar(tombstone);
            raf.writeInt(array.length);
            raf.write(array);
            // System.out.println(film.getId());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
            // System.out.println(film.getId());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    public boolean delete(int id) {
        try (RandomAccessFile raf = new RandomAccessFile(dbFilePath, "rw")) {
            raf.seek(4);

            while (raf.getFilePointer() < raf.length()) {
                long pos = raf.getFilePointer();

                Film film = new Film();
                film.setTombstone(raf.readChar());

                int filmSize = raf.readInt();
                byte[] filmData = new byte[filmSize];

                raf.read(filmData);
                film.fromByteArray(filmData);

                if (film.getTombstone() == tombstone && film.getId() == id) {
                    raf.seek(pos);
                    raf.writeChar('&');
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Film readFilm(RandomAccessFile raf) throws IOException {
        if (raf.getFilePointer() < raf.length()) {
            Film film = new Film();
            film.setTombstone(raf.readChar());
            while (film.getTombstone() != tombstone) {
                int registerSize = raf.readInt();
                raf.skipBytes(registerSize);
                film.setTombstone(raf.readChar());
            }
            int registerSize = raf.readInt();
            byte[] filmData = new byte[registerSize];
            raf.read(filmData);
            film.fromByteArray(filmData);

            return film;
        }
        return null;
    }

    public static void createInTempFiles(Film film, RandomAccessFile raf) {
        try {
            byte[] array = film.toByteArray();
            raf.seek(raf.length());
            raf.writeChar(tombstone);
            raf.writeInt(array.length);
            raf.write(array);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}