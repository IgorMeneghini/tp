package archive;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Model.Film;

public class FilmParser {
    // Define the path to the CSV file
    private static final String csvFilePath = "DataBase/netflix_titles.csv";
    // Create an instance of the Crud class
    private static Crud crud = new Crud();

    // Entry point for parsing
    public static void start() {
        // Read all lines from the CSV file
        String[] lines = getLines(csvFilePath);

        // Process each line
        for (String line : lines) {
            try {
                // Parse the line and create a Film object
                breakingTheLine(line);
            } catch (IOException e) {
                System.err.println("Error processing line: " + line);
                e.printStackTrace();
            }
        }
    }

    // Parse a single line and create a Film object
    public static void breakingTheLine(String line) throws IOException {
        Film film = createFilmFromLine(line);
        crud.create(film);
    }

    // Create a Film object from a line of CSV data
    private static Film createFilmFromLine(String line) {
        Film film = new Film();
        line = setId(line, film);
        line = setType(line, film);
        line = setTitle(line, film);
        line = setDirector(line, film);
        line = setCast(line, film);
        line = setCountry(line, film);
        line = setDate(line, film);
        line = setRating(line, film);
        line = setTimeDuration(line, film);
        film.setList();
        return film;
    }

    // Get all lines from a CSV file
    public static String[] getLines(String csvFilePath) {
        List<String> linesList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
            reader.readLine(); // Skip the header line
            String line;
            while ((line = reader.readLine()) != null) {
                linesList.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return linesList.toArray(new String[0]);
    }

    // Extract and set the ID field from a line
    public static String setId(String line, Film film) {
        int index = 0;
        StringBuilder sBuilder = new StringBuilder();
        while (line.charAt(index) != ',') {
            sBuilder.append(line.charAt(index++));
        }
        film.setId(Integer.parseInt(sBuilder.toString()));
        return line.substring(index + 1);
    }

    // Extract and set the type field from a line
    public static String setType(String line, Film film) {
        int index = 0;
        StringBuilder sBuilder = new StringBuilder();
        while (line.charAt(index) != ',') {
            sBuilder.append(line.charAt(index++));
        }
        if (sBuilder.charAt(0) == 'M') {
            sBuilder.append("\0\0");
        }
        film.setType(sBuilder.toString());
        if (index < line.length() && line.charAt(index) == ',') {
            index++;
        }
        return line.substring(index);
    }

    // Extract and set the title field from a line
    public static String setTitle(String line, Film film) {
        int index = 0;
        StringBuilder sBuilder = new StringBuilder();
        if (line.charAt(index) == '\"') {
            index++;
            while (index < line.length() && line.charAt(index) != '\"') {
                sBuilder.append(line.charAt(index++));
            }
            index++;
        } else {
            while (index < line.length() && line.charAt(index) != ',') {
                sBuilder.append(line.charAt(index++));
            }
        }
        String title = sBuilder.toString().trim();
        film.setTitle(title.isEmpty() ? "Unknown" : title);
        if (index < line.length() && line.charAt(index) == ',') {
            index++;
        }
        return line.substring(index);
    }

    // Extract and set the director field from a line
    public static String setDirector(String line, Film film) {
        int index = 0;
        StringBuilder sBuilder = new StringBuilder();
        if (line.charAt(index) == '\"') {
            index++;
            while (index < line.length() && line.charAt(index) != '\"') {
                sBuilder.append(line.charAt(index++));
            }
            index++;
        } else {
            while (index < line.length() && line.charAt(index) != ',') {
                sBuilder.append(line.charAt(index++));
            }
        }
        String directorName = sBuilder.toString().trim();
        film.setDirector(directorName.isEmpty() ? "Unknown" : directorName);
        if (index < line.length() && line.charAt(index) == ',') {
            index++;
        }
        return line.substring(index);
    }

    // Extract and set the cast field from a line
    public static String setCast(String line, Film film) {
        int index = 0;
        StringBuilder holdString = new StringBuilder();

        if (line.charAt(index) == '\"') {
            index++;
            while (index < line.length() && line.charAt(index) != '\"') {
                holdString.append(line.charAt(index));
                index++;
            }
            index++;
        } else {
            while (index < line.length() && line.charAt(index) != ',') {
                holdString.append(line.charAt(index));
                index++;
            }
        }

        String cast = holdString.toString().trim();
        if (cast.isEmpty()) {
            cast = "Unknown";
        }
        film.setCast(cast);

        if (index < line.length() && line.charAt(index) == ',') {
            index++;
        }
        return line.substring(index);
    }

    // Extract and set the country field from a line
    public static String setCountry(String line, Film film) {
        int index = 0;
        StringBuilder holdString = new StringBuilder();

        if (line.charAt(index) == '\"') {
            index++;
            while (index < line.length() && line.charAt(index) != '\"') {
                holdString.append(line.charAt(index));
                index++;
            }
            index++;
        } else {
            while (index < line.length() && line.charAt(index) != ',') {
                holdString.append(line.charAt(index));
                index++;
            }
        }

        String country = holdString.toString().trim();
        if (country.isEmpty()) {
            country = "Unknown";
        }
        film.setCountry(country);

        if (index < line.length() && line.charAt(index) == ',') {
            index++;
        }
        return line.substring(index);
    }

    // Extract and set the date field from a line
    public static String setDate(String line, Film film) {
        int index = 1;
        StringBuilder holdString = new StringBuilder();
        SimpleDateFormat inputFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
        Date date;

        if (line.charAt(index) == ' ') {
            index++;
        }

        if (line.charAt(0) == ',') {
            try {
                date = inputFormat.parse("January 1, 1800");
                film.setDateAdded(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return line.substring(index + 1);
        }

        while (index < line.length() && line.charAt(index) != '\"') {
            holdString.append(line.charAt(index));
            index++;
        }

        try {
            date = inputFormat.parse(holdString.toString());
            film.setDateAdded(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (index + 2 < line.length()) {
            return line.substring(index + 7);
        } else {
            return "";
        }
    }

    // Extract and set the rating field from a line
    public static String setRating(String line, Film film) {
        int index = 0;
        StringBuilder sBuilder = new StringBuilder();

        while (index < line.length() && line.charAt(index) != ',') {
            sBuilder.append(line.charAt(index++));
        }

        film.setRating(sBuilder.toString());

        if (index + 1 < line.length()) {
            return line.substring(index + 1);
        } else {
            return "";
        }
    }

    // Extract and set the time duration field from a line
    public static String setTimeDuration(String line, Film film) {
        int index = 0;
        StringBuilder sBuilder = new StringBuilder();
        while (index < line.length() && line.charAt(index) != ',') {
            sBuilder.append(line.charAt(index));
            index++;
        }
        film.setTimeDuration(sBuilder.toString());
        if (index + 1 < line.length()) {
            return line.substring(index + 1);
        } else {
            return "";
        }
    }
}

