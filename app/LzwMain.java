package app;

import java.io.*;

import Compressions.Lzw.*;

public class LzwMain {


    public static void main(String[] args) throws IOException {
        RandomAccessFile raf = new RandomAccessFile("DataBase/films.db", "rw");
        byte[] inputData = new byte[(int) raf.length()];
        raf.read(inputData);
        Lzw lzwEncoder = new Lzw(inputData);
        lzwEncoder.encode();
        lzwEncoder.decode();
        raf.close();
        //File excluder = new File(lzwCompressedFile);
        //excluder.delete();
        // excluder = new File(decodedHuffmanFile);
        // excluder.delete();
    }
}
