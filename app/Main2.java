package app;

import java.io.*;
import java.nio.charset.StandardCharsets;

import Compression.Huffman.Huffman;
import archive.Crud;

public class Main2 {
    private static final String dbFilePath = "DataBase\\films.db"; // Path to the database file
    private static final String fileName = "Compression\\Huffman\\Films.txt";

    public static void main(String[] args) throws IOException {
        File file = new File(fileName);

        try (FileOutputStream fos = new FileOutputStream(file);
                OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
                BufferedWriter writer = new BufferedWriter(osw)) {
            RandomAccessFile raf = new RandomAccessFile(dbFilePath, "rw");
            raf.seek(4);
            while (raf.getFilePointer() < raf.length()) {
                writer.append(Crud.readFilm(raf).toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        BufferedWriter writer = new BufferedWriter(new FileWriter("Compression\\Huffman\\FilmsHuffman.db"));
        String line;
        StringBuilder fileString = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            fileString.append(line + "\n");
        }
        Huffman huffman = new Huffman(fileString.toString());
        String encodedString = huffman.encode();
        
        
    }
}
