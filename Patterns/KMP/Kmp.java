package Patterns.KMP;

public class Kmp {
    private byte[] pattern;
    private byte[] data;

    public Kmp(byte[] pattern, byte[] data) {
        this.pattern = pattern;
        this.data = data;
    }

    public static int[] calculateLPS(byte[] pattern) {
        int m = pattern.length;
        int[] lps = new int[m];
        int j = 0;

        for (int i = 1; i < m;) {
            if (pattern[i] == pattern[j]) {
                lps[i] = j + 1;
                j++;
                i++;
            } else {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }

        return lps;
    }

    public int[] kmpSearch() {
        int m = pattern.length;
        int n = data.length;
        int[] lps = calculateLPS(pattern);
        int[] result = new int[2];

        int i = 0, j = 0, comparisons = 0;

        while (i < n) {
            if (data[i] == pattern[j]) {
                i++;
                j++;
                comparisons++;
                if (j == m) {
                    result[0] = comparisons;
                    result[1] = i - j;
                    return result; // Padrão encontrado
                }
            } else {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                    comparisons++;
                }
            }
        }

        result[0] = comparisons;
        result[1] = -1; // Padrão não encontrado
        return result;
    }

}