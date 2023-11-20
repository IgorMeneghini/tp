package Compressions.Huffman;

class Node implements Comparable<Node> {
    byte data;
    int frequency;
    Node left;
    Node right;

    public Node(byte data, int frequency) {
        this.data = data;
        this.frequency = frequency;
    }

    @Override
    public int compareTo(Node other) {
        return this.frequency - other.frequency;
    }
}