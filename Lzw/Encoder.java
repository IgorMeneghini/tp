package Lzw;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Encoder {
    private Map<Integer, Byte[]> dictionary;
    private byte[] inputData;

    public Encoder(byte[] input) {
        this.dictionary = startDictionary(input);
        this.inputData = input;
    }

    private Map<Integer, Byte[]> startDictionary(byte[] input) {
        this.dictionary = new LinkedHashMap<>();
        Integer position = 1;

        for (Byte b : input) {
            Byte[] ByteArray = new Byte[1];
            ByteArray[0] = b;

            if (!contains(ByteArray)) {
                dictionary.put(position, ByteArray);
                position += 1;
            }
        }

        dictionary.forEach((key, value) -> {
            System.out.println(key + " " + Arrays.toString(value));
        });

        return dictionary;
    }

    public byte[] encode() {
        List<Integer> result = new ArrayList<>();
        List<Byte> current = new ArrayList<>();

        int code = dictionary.size() + 1;// position

        for (Byte b : inputData) {
            System.out.println(result.size());
            List<Byte> next = new ArrayList<>();
            for (Byte tempByte : current) {
                next.add(tempByte);
            }
            next.add(b);
            if (contains(toByteArray(next))) {
                current.clear();
                for (Byte tempByte : next) {
                    current.add(tempByte);
                }
            } else {
                result.add(searchDictionary(toByteArray(current)));
                dictionary.put(code++, toByteArray(next));
                current.clear();
                current.add(b);
            }
        }
        if (!current.isEmpty()) {
            result.add(searchDictionary(toByteArray(current)));
        }
        for (int i : result) {
            System.out.print(i + " ");

        }
        System.out.println();

        
        return fromIntArrayToByteArray(result);
    }

    private byte[] fromIntArrayToByteArray(List<Integer> array) {
        byte[] result = new byte[array.size()];
        for (int i = 0; i < array.size(); i++) {
            int temp = array.get(i);
            result[i] = (byte) temp;
        }
        return result;
    }

    private int searchDictionary(Byte[] array) {
        for (Entry<Integer, Byte[]> mapElement : dictionary.entrySet()) {
            if (Arrays.equals(array, mapElement.getValue())) {
                return mapElement.getKey();
            }
        }
        return -1; // Indicate that the array is not found in the dictionary
    }

    private Byte[] toByteArray(List<Byte> list) {
        Byte[] newByteArray = new Byte[list.size()];
        int cont = 0;
        for (Byte element : list) {
            newByteArray[cont++] = element;
        }
        return newByteArray;
    }

    private boolean contains(Byte[] ByteArray) {
        for (Entry<Integer, Byte[]> mapElement : dictionary.entrySet()) {
            if (Arrays.equals(ByteArray, mapElement.getValue())) {
                return true;
            }
        }
        return false;
    }

}
