package app;

import java.io.*;

import Huffman.Huffman;

public class HuffmanMain {
    private static final String dbFilePath = "DataBase\\films.db"; // Path to the database file
    private static final String compressedFile = "DataBase\\filmsCompressedHuffman.db";
    private static final String decodedFile = "dataBase\\filmsDecodedHuffman.db";

    public static void main(String[] args) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(dbFilePath, "rw");
        byte[] inputData = new byte[(int) raf.length()];
        raf.read(inputData);
        byte[] compressedByteArray = Huffman.compress(inputData);
        raf.close();

        raf = new RandomAccessFile(compressedFile, "rw");
        raf.write(compressedByteArray);
        raf.close();

        byte[] decodedByteArray = Huffman.decode(compressedByteArray);
        raf = new RandomAccessFile(decodedFile, "rw");
        raf.write(decodedByteArray);
        raf.close();

       

    }
}
