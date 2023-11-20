package Patterns.BoyerMoore;

public class BoyerMoore {

    private byte[] pattern;
    private byte[] data;

    public BoyerMoore(byte[] pattern, byte[] data) {
        this.pattern = pattern;
        this.data = data;
    }

    private static int[] generateBadCharTable(byte[] pattern) {
        int m = pattern.length;
        int[] badChar = new int[256];

        for (int i = 0; i < 256; i++) {
            badChar[i] = m;
        }

        for (int i = 0; i < m - 1; i++) {
            badChar[pattern[i] & 0xFF] = m - 1 - i;
        }

        return badChar;
    }

    private static int[] generateGoodSuffixTable(byte[] pattern) {
        int m = pattern.length;
        int[] goodSuffix = new int[m];
        int[] suffix = new int[m];

        suffix[m - 1] = m;
        int g = m - 1;
        int f = 0;

        for (int i = m - 2; i >= 0; i--) {
            if (i > g && suffix[i + m - 1 - f] < i - g) {
                suffix[i] = suffix[i + m - 1 - f];
            } else {
                if (i < g) {
                    g = i;
                }
                f = i;
                while (g >= 0 && pattern[g] == pattern[g + m - 1 - f]) {
                    g--;
                }
                suffix[i] = f - g;
            }
        }

        for (int i = 0; i < m - 1; i++) {
            goodSuffix[i] = m;
        }

        for (int i = m - 1; i >= 0; i--) {
            if (suffix[i] == i + 1) {
                for (int j = 0; j < m - 1 - i; j++) {
                    if (goodSuffix[j] == m) {
                        goodSuffix[j] = m - 1 - i;
                    }
                }
            }
        }

        return goodSuffix;
    }

    public int boyerMooreSearch() {
        int m = pattern.length;
        int n = data.length;

        int[] badChar = generateBadCharTable(pattern);
        int[] goodSuffix = generateGoodSuffixTable(pattern);

        int j = 0;
        int i = m - 1;
        int comparisonCount = 0;

        while (j <= n - m) {
            i = m - 1;
            while (i >= 0 && pattern[i] == data[j + i]) {
                comparisonCount++;
                i--;
            }

            if (i < 0) {
                // Pattern found
                System.out.println("Number of comparisons: " + comparisonCount);
                return j;
            } else {
                // Use both heuristics to shift the pattern
                int badCharShift = badChar[data[j + i] & 0xFF] - (m - 1 - i);
                int goodSuffixShift = goodSuffix[i];

                j += Math.max(badCharShift, goodSuffixShift);
            }
        }

        // Pattern not found
        System.out.println("Number of comparisons: " + comparisonCount);
        return -1;
    }
}
