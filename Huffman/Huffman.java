package Huffman;

import java.util.*;

public class Huffman {
    private static Map<Byte, String> huffmanCodes;
    private static Node root;

    public static byte[] compress(byte[] data) {
        Map<Byte, Integer> frequencyMap = new HashMap<>();
        for (byte b : data) {
            frequencyMap.put(b, frequencyMap.getOrDefault(b, 0) + 1);
        }

        root = buildHuffmanTree(frequencyMap);
        Map<Byte, String> huffmanCodes = generateHuffmanCodes(root);

        StringBuilder compressedData = new StringBuilder();
        for (byte b : data) {
            compressedData.append(huffmanCodes.get(b));
        }
        byte[] compressedBytes = new byte[(compressedData.length() + 7) / 8];
        int currentIndex = 0;
        for (int i = 0; i < compressedData.length(); i += 8) {
            String byteString = compressedData.substring(i, Math.min(i + 8, compressedData.length()));
            compressedBytes[currentIndex++] = (byte) Integer.parseInt(byteString, 2);
        }
        return compressedBytes;

    }

    public static Node buildHuffmanTree(Map<Byte, Integer> frequencyMap) {
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>();
        for (Map.Entry<Byte, Integer> entry : frequencyMap.entrySet()) {
            priorityQueue.add(new Node(entry.getKey(), entry.getValue()));
        }
        while (priorityQueue.size() > 1) {
            Node leftNode = priorityQueue.poll();
            Node rightNode = priorityQueue.poll();
            Node parent = new Node((byte) 0, leftNode.frequency + rightNode.frequency);
            parent.left = leftNode;
            parent.right = rightNode;
            priorityQueue.add(parent);
        }
        return priorityQueue.poll();
    }

    public static Map<Byte, String> generateHuffmanCodes(Node root) {
        huffmanCodes = new HashMap<>();
        generateHuffmanCodesRecursive(root, "", huffmanCodes);
        return huffmanCodes;

    }

    public static void generateHuffmanCodesRecursive(Node node, String code, Map<Byte, String> huffmanCodes) {
        if (node == null) {
            return;
        }

        if (node.left == null && node.right == null) {
            huffmanCodes.put(node.data, code);
        }
        generateHuffmanCodesRecursive(node.left, code.concat("0"), huffmanCodes);
        generateHuffmanCodesRecursive(node.right, code.concat("1"), huffmanCodes);

    }

    public static byte[] decode(byte[] compressedData) {
        StringBuilder stringCompressedData = new StringBuilder();
        List<Byte> decodedData = new ArrayList<>();
        for (byte b : compressedData) {
            String byteString = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
            stringCompressedData.append(byteString);
        }
        Node current = root;
        for (char character : stringCompressedData.toString().toCharArray()) {
            current = character == '0' ? current.left : current.right;
            if (current.left == null && current.right == null) {
                decodedData.add(current.data);
                current = root;
            }
        }
        byte[] decodedDataByteArray = new byte[decodedData.size()];
        for (int i = 0; i < decodedData.size(); i++) {
            decodedDataByteArray[i] = decodedData.get(i);
        }
        return decodedDataByteArray;
    }
}
