package app;

import java.io.IOException;
import java.io.RandomAccessFile;

import Compressions.Lzw.*;

public class LzwMain {

    public static void main(String[] args) throws IOException {
        RandomAccessFile raf = new RandomAccessFile("DataBase/films.db", "rw");
        byte[] inputData = new byte[(int) raf.length()];
        raf.read(inputData);
        Encoder lzwEncoder = new Encoder(inputData);
        byte[] encodedByteArray = lzwEncoder.encode();
        raf.close();
        raf = new RandomAccessFile("DataBase\\EncodedLZW.db", "rw");
        raf.write(encodedByteArray);
        raf.close();
    }
}
