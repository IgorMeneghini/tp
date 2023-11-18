package app;

import java.io.*;
import java.util.*;

import DataStructure.Hash.ExtendibleHashTable;
import DataStructure.InvertedList.InvertedList;
import DataStructure.InvertedList.InvertedList2;
import archive.Crud;
import archive.FilmParser;
import Algorithm.ExternalMergeSorting;
import Compressions.Huffman.Huffman;
import Model.Film;
import Patterns.KMP.Kmp;

public class Main {

    private static final String dbFilePath = "DataBase/films.db"; // Path to the database file
    private static final String compressedHuffmanFile = "DataBase/HuffmanCompressed.db";
    private static final String decodedHuffmanFile = "DataBase/HuffmanUnzipped.db";

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
                        "6- Huffman\n" +
                        "7- KMP\n" +
                        "8- Exit");

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
                        boolean exit = false;
                        RandomAccessFile raf = new RandomAccessFile(dbFilePath, "rw");
                        byte[] inputData = new byte[(int) raf.length()];
                        raf.read(inputData);
                        Huffman huffman = new Huffman(inputData);
                        raf.close();

                        huffman.compress();
                        raf = new RandomAccessFile(compressedHuffmanFile, "rw");
                        raf.write(huffman.getCompressedBytes());
                        raf.close();
                        System.out.println("Successfully compressed file.");
                        while (!exit) {

                            System.out.println("1- Decompress\n2- Exit and print decompress");
                            option = sc.nextInt();
                            if (option == 1) {

                                huffman.decode(huffman.getCompressedBytes());
                                raf = new RandomAccessFile(decodedHuffmanFile, "rw");
                                raf.write(huffman.getDecodedBytes());

                            } else {
                                Crud crudHuffman = new Crud(true);
                                crudHuffman.readAll(decodedHuffmanFile);
                                System.out.println("Original size: " + huffman.getInputData().length + " bytes");
                                System.out
                                        .println("Compressed size: " + huffman.getCompressedBytes().length + " bytes");
                                if (huffman.getDecodedBytes() != null) {

                                    System.out.println("Unzipped size: " + huffman.getDecodedBytes().length + " bytes");
                                }
                                File excluder = new File(compressedHuffmanFile);
                                excluder.delete();
                                excluder = new File(decodedHuffmanFile);
                                excluder.delete();
                                break;
                            }
                            raf.close();

                        }
                    case 7:
                        try (Scanner scanner = new Scanner(System.in)) {
                            while (true) {
                                System.out.println("1. KMP");
                                System.out.println("2. Sair");

                                int choice = Integer.parseInt(scanner.nextLine());

                                if (choice == 1) {
                                    raf = new RandomAccessFile("DataBase/films.db", "rw");
                                    byte[] data = new byte[(int) raf.length()];
                                    raf.read(data);

                                    System.out.print("Input the pattern: ");
                                    String patternString = (scanner.nextLine());
                                    byte[] pattern = patternString.getBytes();
                                    Kmp kmp = new Kmp(pattern, data);

                                    int[] result = kmp.kmpSearch();

                                    if (result[1] != -1) {
                                        System.out.println(
                                                "Pattern found at position " + result[1] + " with " + result[0]
                                                        + " comparisons.");
                                    } else {
                                        System.out.println("Pattern not found.");
                                    }
                                    raf.close();
                                } else if (choice == 2) {
                                    System.out.println("Exiting...");
                                    break;
                                } else {
                                    System.out.println("Invalid option. Please try again.");
                                }
                            }
                        }
                        break;
                    case 8:
                        System.out.println("Exiting");
                        System.exit(0); // Exit the program
                        break;
                    default:
                        System.out.println("Invalid option. Please select a valid option.");
                        break;
                }
            }
        } catch (

        NumberFormatException e) {
            e.printStackTrace();
        }

    }
}
