package app;

import java.io.IOException;
import java.util.Scanner;

import DataStructure.Hash.ExtendibleHashTable;
import DataStructure.InvertedList.InvertedList;
import DataStructure.InvertedList.InvertedList2;
import archive.Crud;
import archive.FilmParser;
import Algorithm.ExternalMergeSorting;
import Model.Film;

public class Main {
    private static final String dbFilePath = "DataBase\\films.db"; // Path to the database file

    public static void main(String[] args) throws IOException {
        // Start parsing data from CSV
        FilmParser.start();

        int option = 0; // Initialize the option variable

        try (Scanner sc = new Scanner(System.in)) {
            while (true) { // Start an infinite loop for the main menu
                // Display the main menu options
                System.out.println("Select an option:\n" +
                        "1- CRUD\n" +
                        "2- External Sorting\n" +
                        "3- Hash\n" +
                        "4- Inverted List\n" +
                        "5- B-Tree\n" +
                        "6- Exit");

                option = Integer.parseInt(sc.nextLine()); // Read user input as an integer

                switch (option) {
                    case 1:
                        // Create a CRUD instance
                        Crud crud = new Crud(true);

                        while (true) { // Start an inner loop for CRUD operations
                            // Display CRUD operation options
                            System.out.println("What would you like to do?:\n" +
                                    "1- Create\n" +
                                    "2- Read\n" +
                                    "3- Update\n" +
                                    "4- Delete\n" +
                                    "5- Exit");

                            option = Integer.parseInt(sc.nextLine()); // Read user input for CRUD operation

                            switch (option) {
                                case 1:
                                    // Create a Film object and add it to the database
                                    Film film = new Film(8555, "Igor O Criador", "Movie", 9999999, "R, 90 min");
                                    crud.create(film, dbFilePath);
                                    break;
                                case 2:
                                    // Read all films from the database
                                    crud.readAll(dbFilePath);
                                    break;
                                case 3:
                                    // Update a film in the database
                                    film = new Film(1, "Igor O Criador", "Movie", 9999999, "R, 90 min");
                                    crud.update(film);
                                    break;
                                case 4:
                                    // Delete a film from the database
                                    crud.delete(1);
                                    break;
                                case 5:
                                    System.out.println("Exiting CRUD.");
                                    break;
                                default:
                                    System.out.println("Invalid option. Please select a valid option.");
                                    break;
                            }

                            if (option == 5) {
                                break; // Exit the inner CRUD loop
                            }
                        }
                        break;

                    case 2:
                        // Perform external merge sorting on the data
                        ExternalMergeSorting externalMergeSorting = new ExternalMergeSorting();
                        externalMergeSorting.externalMergeSortSort(1000);
                        // Re-parse data after sorting
                        FilmParser.start();
                        break;

                    case 3:
                        // Initialize and perform operations on the Extendible Hash Table
                        ExtendibleHashTable<Integer, Long> hash = new ExtendibleHashTable<>(500);
                        hash.startHash();

                        System.out.println("Select an ID to search: ");
                        int id = Integer.parseInt(sc.nextLine());
                        System.out.println(hash.get(id));
                        break;

                    case 4:
                        option = 1;
                        while (true) {
                            System.out.println("1- List 1\n2- List 2\n3- Exit");
                            option = Integer.parseInt(sc.nextLine());
                            if (option == 1) {
                                // Initialize and perform operations on Inverted List 1
                                InvertedList invertedList1 = new InvertedList();
                                Film film = new Film(8555, "Igor O Criador", "Movie", 9999999, "R, 50 min");
                                Film film2 = new Film(8556, "Luis O Criador", "Movie", 9999999, "R, 70 min");
                                invertedList1.startList();
                                invertedList1.insert(film);
                                System.out.println(invertedList1.search(film));
                                System.out.println(invertedList1.search(film2));
                            } else if (option == 2) {
                                // Initialize and perform operations on Inverted List 2
                                InvertedList2 invertedList2 = new InvertedList2();
                                Film film = new Film(8555, "Igor O Criador", "Movie", 9999999, "R, 50 min");
                                Film film2 = new Film(8556, "Luis O Criador", "Movie", 9999999, "R, 70 min");
                                invertedList2.startList();
                                invertedList2.insert(film);
                                System.out.println(invertedList2.search(film));
                                System.out.println(invertedList2.search(film2));
                            } else {
                                break;
                            }
                        }
                        break;

                    case 5:
                        // Placeholder for B-Tree operations
                        break;

                    case 6:
                        System.out.println("Exiting");
                        System.exit(0); // Exit the program
                        break;

                    default:
                        System.out.println("Invalid option. Please select a valid option.");
                        break;
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
}
