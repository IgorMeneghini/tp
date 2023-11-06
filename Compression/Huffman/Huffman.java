package Compression.Huffman;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public class Huffman {
    protected Node root;
    private final String text;
    private Map<Character, Integer> charFrequencies;
    private final Map<Character, String> huffmanCodes;

    public Huffman(String text) {
        this.text = text;
        charFrequencies = new HashMap<>();
        fillCharacterFrequency();
        huffmanCodes = new HashMap<>();
    }

    private void fillCharacterFrequency() {
        for (char character : text.toCharArray()) {
            Integer integer = charFrequencies.get(character);
            charFrequencies.put(character, integer != null ? integer + 1 : 1);
        }
    }

    public String encode() {
        Queue<Node> queue = new PriorityQueue<>();
        charFrequencies.forEach((character, frequency) -> queue.add(new Leaf(character, frequency)));
        while (queue.size() > 1) {
            Node leftNode = queue.poll();
            Node rightNode = queue.poll();
            Node newNode = new Node(leftNode, rightNode);
            queue.add(newNode);
        }
        generateHuffmanCodes((root = queue.poll()), "");
        return getEncodedText();
    }

    private void generateHuffmanCodes(Node node, String code) {
        if (node instanceof Leaf leaf) {// stop condition if is leaf put encoded text of char x, and return
            huffmanCodes.put(leaf.getCharacter(), code);
            return;
        }
        generateHuffmanCodes(node.getLeftNode(), code.concat("0"));
        generateHuffmanCodes(node.getRightNode(), code.concat("1"));
    }

    private String getEncodedText() {
        StringBuilder sb = new StringBuilder();
        for (char character : text.toCharArray()) {
            sb.append(huffmanCodes.get(character));
        }
        return sb.toString();
    }

    public String decode(String encodedText) {
        StringBuilder sb = new StringBuilder();
        Node current = root;
        for (char character : encodedText.toCharArray()) {
            current = character == '0' ? current.getLeftNode() : current.getRightNode();
            if (current instanceof Leaf leaf) {
                sb.append(leaf.getCharacter());
                current = root;
            }
        }
        return sb.toString();
    }
}
