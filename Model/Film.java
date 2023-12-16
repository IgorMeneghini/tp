package Model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

public class Film {
    private int id;
    private String type;
    private String title;
    private String director;
    private String cast;
    private String country;
    private long dateAdded;
    private String rating;
    private String duration;
    private String list;
    private char tombstone = '¬';

    public Film() {
    }

    /**
     *
     * @param id        The ID of the film.
     * @param title     The title of the film.
     * @param director  The director of the film.
     * @param country   The country of the film.
     * @param dateAdded The date when the film was added.
     */
    public Film(int id, String title, String type, long dateAdded, String list) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.list = list;
        this.dateAdded = dateAdded;
    }

    /**
     * Constructor for the Film class with all available fields.
     *
     * @param id        The ID of the film.
     * @param type      The type of the film.
     * @param title     The title of the film.
     * @param director  The director of the film.
     * @param cast      The cast of the film.
     * @param country   The country of the film.
     * @param dateAdded The date when the film was added.
     * @param rating    The rating of the film.
     */
    public Film(int id, String type, String title, String director, String cast, String country,
            Date dateAdded, String rating) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.director = director;
        this.cast = cast;
        this.country = country;
        this.dateAdded = dateAdded.getTime();
        this.rating = rating;
        this.list = rating + "," + duration;
    }

    public Film(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getCast() {
        return cast;
    }

    public void setCast(String cast) {
        this.cast = cast;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public long getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded.getTime();
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public void setTimeDuration(String duration) {
        this.duration = duration;
    }

    public String getTimeDuration() {
        return duration;
    }

    public void setList() {
        list = rating + "," + duration;
    }

    public String getList() {
        return list;
    }

    public void setTombstone(char tombstone) {
        this.tombstone = tombstone;
    }

    public char getTombstone() {
        return this.tombstone;
    }

    public String toString() {
        return "ID: " + this.id + "\nTitle: " + this.title + "\nType: " + this.type + "\nDate Added: " + this.dateAdded
                + "\nRating and duration: " + this.list + "\n";
    }

    public byte[] toByteArray() throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(baos)) {
            dos.writeInt(this.id);
            dos.writeUTF(this.title);
            dos.writeUTF(this.type);
            dos.writeLong(this.dateAdded);
            dos.writeUTF(this.list);

            return baos.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void fromByteArray(byte[] ba) throws IOException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(ba);
                DataInputStream dis = new DataInputStream(bais)) {

            this.id = dis.readInt();
            this.title = dis.readUTF();
            this.type = dis.readUTF();
            this.dateAdded = dis.readLong();
            this.list = dis.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public Object getDescription() {
        return null;
    }

    public void setDescription(String encrypt) {
    }

}
