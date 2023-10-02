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

class Main {
    private static final String dbFilePath = "DataBase\\films.db"; // Path to the database file

    public static void main(String[] args) throws IOException {
        FilmParser.start(); // Start parsing data

        int option = 0; // Initialize the option variable
        try (Scanner sc = new Scanner(System.in)) {
            while (true) { // Start an infinite loop for the main menu
                System.out.println("Selecione:\n" +
                        "1- CRUD\n" +
                        "2- Ordenação Externa\n" +
                        "3- Hash\n" +
                        "4- Lista Invertida\n" +
                        "5- Árvore B\n" +
                        "6- Sair");

                option = Integer.parseInt(sc.nextLine()); // Read user input as an integer

                switch (option) {
                    case 1:
                        Crud crud = new Crud(true); // Create a CRUD instance
                        while (true) { // Start an inner loop for CRUD operations
                            System.out.println("O que quer fazer?:\n" +
                                    "1- Create\n" +
                                    "2- Read\n" +
                                    "3- Update\n" +
                                    "4- Delete\n" +
                                    "5- Sair");

                            option = Integer.parseInt(sc.nextLine()); // Read user input for CRUD operation

                            switch (option) {
                                case 1:
                                    Film film = new Film(8555, "Igor O criador", "Movie", 9999999, "R,90 min");
                                    crud.create(film, dbFilePath);
                                    break;
                                case 2:
                                    crud.readAll(dbFilePath);
                                    break;
                                case 3:
                                    film = new Film(1, "Igor O criador", "Movie", 9999999, "R,90 min");
                                    crud.update(film);
                                    break;
                                case 4:
                                    crud.delete(1);
                                    break;
                                case 5:
                                    System.out.println("Saindo do CRUD.");
                                    break;
                                default:
                                    System.out.println("Opção inválida. Por favor, selecione uma opção válida.");
                                    break;
                            }
                            if (option == 5) {
                                break; // Exit the inner CRUD loop
                            }
                        }
                        break;
                    case 2:
                        ExternalMergeSorting externalMergeSorting = new ExternalMergeSorting();
                        externalMergeSorting.externalMergeSortSort(1000);
                        FilmParser.start();
                        break;
                    case 3:
                        ExtendibleHashTable<Integer, Long> hash = new ExtendibleHashTable<>(500);
                        hash.startHash();
                        System.out.println("Selecione um ID de busca: ");
                        int id = Integer.parseInt(sc.nextLine());
                        System.out.println(hash.get(id));
                        break;
                    case 4:
                        option = 1;
                        while (true) {
                            System.out.println("1- Lista 1\n2- Lista 2\n3- sair");
                            option = Integer.parseInt(sc.nextLine());
                            if (option == 1) {
                                InvertedList invertedList1 = new InvertedList();
                                Film film = new Film(8555, "Igor O criador", "Movie", 9999999, "R,90 min");
                                Film film2 = new Film(8556, "luis O criador", "Movie", 9999999, "R,70 min");
                                invertedList1.startList();
                                invertedList1.insert(film);
                                System.out.println(invertedList1.search(film));
                                System.out.println(invertedList1.search(film2));
                            } else if (option == 2) {
                                InvertedList2 invertedList2 = new InvertedList2();
                                Film film = new Film(8555, "Igor O criador", "Movie", 9999999, "R,50 min");
                                Film film2 = new Film(8556, "luis O criador", "Movie", 9999999, "R,70 min");
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
                        break;
                    case 6:
                        System.out.println("Saindo");
                        System.exit(0); // Exit the program
                        break;
                    default:
                        System.out.println("Opção inválida. Por favor, selecione uma opção válida.");
                        break;
                }
            }
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
