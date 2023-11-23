package Compressions.Lzw;

import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Lzw {
    private Map<Integer, List<Byte>> dictionary;
    private byte[] inputData;
    private String encodedFileLzw = "DataBase\\EncodedFileLzw.db";

    public Lzw(byte[] input) {
        startDictionary(input);
        this.inputData = input;
    }

    private void startDictionary(byte[] input) {
        this.dictionary = new LinkedHashMap<>();
        int dictSize = 256;

        for (int i = 1; i < dictSize; i++) {
            List<Byte> list = new ArrayList<>();
            list.add((byte) i);
            this.dictionary.put(i, list);
        }
    }

    public void encode() throws IOException {
        List<Integer> result = new ArrayList<>();
        List<Byte> current = new ArrayList<>();

        int dictSize = 255;

        for (Byte b : inputData) {
            List<Byte> next = new ArrayList<>(current);
            next.add(b);
            if (contains(next)) {
                current = new ArrayList<>(next);

            } else {
                result.add(searchDictionary(current));
                this.dictionary.put(++dictSize, next);
                current.clear();
                current.add(b);
            }
        }
        if (!(current.isEmpty())) {
            result.add(searchDictionary(current));
        }
        writeVLQToBinaryFile(result, "DataBase/EncodedFileLzw.db");
        System.out.println("Compression completed");
    }

    public static void writeVLQToBinaryFile(List<Integer> compressedData, String filePath) {
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(filePath))) {
            for (int value : compressedData) {
                writeVLQ(dos, value);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeVLQ(DataOutputStream dos, int value) throws IOException {
        do {
            int byteValue = value & 0x7F;
            value >>>= 7;
            if (value != 0) {
                byteValue |= 0x80;
            }
            dos.writeByte(byteValue);
        } while (value != 0);
    }

    public List<Integer> readFile() throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(encodedFileLzw, "rw")) {
            List<Integer> result = new ArrayList<>();
            while (true) {
                try {
                    result.add(raf.readInt());
                } catch (EOFException e) {
                    // End of file reached
                    break;
                }
            }
            return result;
        } catch (IOException e) {
            // Handle or log the exception
            throw e; // You can rethrow the exception or handle it as needed
        }
    }

    private int searchDictionary(List<Byte> array) {
        for (Map.Entry<Integer, List<Byte>> mapElement : dictionary.entrySet()) {
            if (mapElement.getValue().equals(array)) {
                return mapElement.getKey();
            }
        }
        return -1;
    }

    public void decode() throws IOException {
        List<Integer> integerList = readVLQFile();
        System.out.println();
        List<Byte> byteArray = new ArrayList<>();

        for (Integer integer : integerList) {
            System.out.print(integer + " ");
            List<Byte> dictionaryEntry = this.dictionary.get(integer);

            // Check if the dictionary contains an entry for the given integer
            if (dictionaryEntry != null) {
                for (byte b : dictionaryEntry) {
                    byteArray.add(b);
                }
            } else {
                System.out.println("Entry not found in dictionary for integer: " + integer);
            }
        }

        byte[] bytes = new byte[byteArray.size()];
        for (int i = 0; i < byteArray.size(); i++) {
            bytes[i] = byteArray.get(i);
        }

        RandomAccessFile raf = new RandomAccessFile("DataBase/DecodedFileLzw.db", "rw");
        raf.write(bytes);
        raf.close();
    }

    public List<Integer> readVLQFile() throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(encodedFileLzw, "rw")) {
            List<Integer> result = new ArrayList<>();
            while (true) {
                try {
                    result.add(readVLQ(raf));
                } catch (EOFException e) {
                    // End of file reached
                    break;
                }
            }
            return result;
        } catch (IOException e) {
            // Handle or log the exception
            throw e; // You can rethrow the exception or handle it as needed
        }
    }

    private static int readVLQ(RandomAccessFile raf) throws IOException {
        int value = 0;
        int shift = 0;
        byte b;
        do {
            b = raf.readByte();
            value |= (b & 0x7F) << shift;
            shift += 7;
        } while ((b & 0x80) != 0);
        return value;
    }

    private boolean contains(List<Byte> byteArray) {
        for (List<Byte> list : dictionary.values()) {
            if (list.equals(byteArray)) {
                return true;
            }
        }
        return false;
    }

}
