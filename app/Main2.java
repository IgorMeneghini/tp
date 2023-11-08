package app;

import java.io.*;

import Compression.Huffman.Huffman;

public class Main2 {
    private static final String dbFilePath = "DataBase\\films.db"; // Path to the database file
    private static final String compressedFile = "DataBase\\filmsCompressedHuffman.db";
    private static final String decodedFile = "dataBase\\filmsDecodedHuffman.db";

    public static void main(String[] args) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(dbFilePath, "rw");
        byte[] byteArray = new byte[(int) raf.length()];
        raf.read(byteArray);
        byte[] compressedByteArray = Huffman.compress(byteArray);
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
