package algorithms;

import java.util.*;

/**
 * Course Outcome 1 (CO1) - String Algorithms.
 * Provides authentic implementations of:
 * A. Knuth-Morris-Pratt (KMP) with LPS array
 * B. Z-Algorithm with Z-array
 * C. Rabin-Karp with rolling hash and collision verification
 * D. Aho-Corasick Multi-Pattern Trie with failure links
 */
public class StringAlgorithms {

    // ==========================================
    // A. KMP - Knuth-Morris-Pratt Algorithm
    // ==========================================

    public static class KmpResult {
        public final String text;
        public final String pattern;
        public final List<Integer> matchIndices;
        public final int[] lps;
        public final int comparisons;
        public final String timeComplexity = "O(N + M) where N = text length, M = pattern length";

        public KmpResult(String text, String pattern, List<Integer> matchIndices, int[] lps, int comparisons) {
            this.text = text;
            this.pattern = pattern;
            this.matchIndices = matchIndices;
            this.lps = lps;
            this.comparisons = comparisons;
        }
    }

    public static int[] computeLpsArray(String pattern) {
        int m = pattern.length();
        int[] lps = new int[m];
        if (m == 0) return lps;
        int len = 0;
        int i = 1;
        lps[0] = 0;
        while (i < m) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len != 0) {
                    len = lps[len - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }
        return lps;
    }

    public static KmpResult kmpSearch(String text, String pattern) {
        List<Integer> matches = new ArrayList<>();
        if (text == null || pattern == null || pattern.isEmpty() || text.length() < pattern.length()) {
            return new KmpResult(text, pattern, matches, new int[0], 0);
        }

        int[] lps = computeLpsArray(pattern);
        int n = text.length();
        int m = pattern.length();
        int i = 0; // index for text
        int j = 0; // index for pattern
        int comparisons = 0;

        while (i < n) {
            comparisons++;
            if (pattern.charAt(j) == text.charAt(i)) {
                i++;
                j++;
            }
            if (j == m) {
                matches.add(i - j);
                j = lps[j - 1];
            } else if (i < n && pattern.charAt(j) != text.charAt(i)) {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }
        return new KmpResult(text, pattern, matches, lps, comparisons);
    }

    // ==========================================
    // B. Z-Algorithm
    // ==========================================

    public static class ZResult {
        public final String text;
        public final String pattern;
        public final int[] zArray;
        public final List<Integer> matchIndices;
        public final String timeComplexity = "O(N + M) linear time via [L, R] Z-box intervals";

        public ZResult(String text, String pattern, int[] zArray, List<Integer> matchIndices) {
            this.text = text;
            this.pattern = pattern;
            this.zArray = zArray;
            this.matchIndices = matchIndices;
        }
    }

    public static int[] calculateZArray(String s) {
        int n = s.length();
        int[] z = new int[n];
        if (n == 0) return z;
        z[0] = n;
        int l = 0, r = 0;
        for (int i = 1; i < n; i++) {
            if (i <= r) {
                z[i] = Math.min(r - i + 1, z[i - l]);
            }
            while (i + z[i] < n && s.charAt(z[i]) == s.charAt(i + z[i])) {
                z[i]++;
            }
            if (i + z[i] - 1 > r) {
                l = i;
                r = i + z[i] - 1;
            }
        }
        return z;
    }

    public static ZResult zSearch(String text, String pattern) {
        List<Integer> matches = new ArrayList<>();
        if (text == null || pattern == null || pattern.isEmpty() || text.length() < pattern.length()) {
            return new ZResult(text, pattern, new int[0], matches);
        }

        String concat = pattern + "$" + text;
        int[] z = calculateZArray(concat);
        int m = pattern.length();

        for (int i = m + 1; i < concat.length(); i++) {
            if (z[i] == m) {
                matches.add(i - m - 1);
            }
        }
        return new ZResult(text, pattern, z, matches);
    }

    // ==========================================
    // C. Rabin-Karp Algorithm
    // ==========================================

    public static class RabinKarpResult {
        public final String text;
        public final String pattern;
        public final List<Integer> matchIndices;
        public final int hashComparisons;
        public final int spuriousCollisions;
        public final int verifiedMatches;
        public final long patternHash;
        public final String timeComplexity = "O(N + M) average, O(N * M) worst-case";

        public RabinKarpResult(String text, String pattern, List<Integer> matchIndices,
                                int hashComparisons, int spuriousCollisions, int verifiedMatches, long patternHash) {
            this.text = text;
            this.pattern = pattern;
            this.matchIndices = matchIndices;
            this.hashComparisons = hashComparisons;
            this.spuriousCollisions = spuriousCollisions;
            this.verifiedMatches = verifiedMatches;
            this.patternHash = patternHash;
        }
    }

    public static RabinKarpResult rabinKarpSearch(String text, String pattern) {
        List<Integer> matches = new ArrayList<>();
        if (text == null || pattern == null || pattern.isEmpty() || text.length() < pattern.length()) {
            return new RabinKarpResult(text, pattern, matches, 0, 0, 0, 0);
        }

        int n = text.length();
        int m = pattern.length();
        long d = 256;
        long q = 1_000_000_007L; // Prime modulus
        long pHash = 0; // Pattern hash
        long tHash = 0; // Current window text hash
        long h = 1;

        // Compute h = d^(m-1) % q
        for (int i = 0; i < m - 1; i++) {
            h = (h * d) % q;
        }

        // Calculate initial hash values
        for (int i = 0; i < m; i++) {
            pHash = (d * pHash + pattern.charAt(i)) % q;
            tHash = (d * tHash + text.charAt(i)) % q;
        }

        int hashComparisons = 0;
        int spuriousCollisions = 0;
        int verifiedMatches = 0;

        for (int i = 0; i <= n - m; i++) {
            hashComparisons++;
            if (pHash == tHash) {
                // Potential match: explicitly verify characters
                boolean isMatch = true;
                for (int j = 0; j < m; j++) {
                    if (text.charAt(i + j) != pattern.charAt(j)) {
                        isMatch = false;
                        break;
                    }
                }
                if (isMatch) {
                    matches.add(i);
                    verifiedMatches++;
                } else {
                    spuriousCollisions++;
                }
            }

            // Compute hash value for next window
            if (i < n - m) {
                tHash = (d * (tHash - text.charAt(i) * h) + text.charAt(i + m)) % q;
                if (tHash < 0) {
                    tHash = tHash + q;
                }
            }
        }

        return new RabinKarpResult(text, pattern, matches, hashComparisons, spuriousCollisions, verifiedMatches, pHash);
    }

    // ==========================================
    // D. Aho-Corasick Multi-Pattern Algorithm
    // ==========================================

    public static class AhoCorasickMatch {
        public final String keyword;
        public final int startIndex;
        public final int endIndex;

        public AhoCorasickMatch(String keyword, int startIndex, int endIndex) {
            this.keyword = keyword;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }

        @Override
        public String toString() {
            return String.format("'%s' at [%d..%d]", keyword, startIndex, endIndex);
        }
    }

    public static class AhoCorasickNode {
        public final Map<Character, AhoCorasickNode> children = new HashMap<>();
        public AhoCorasickNode fail = null;
        public final List<String> output = new ArrayList<>();
    }

    public static class AhoCorasickTrie {
        private final AhoCorasickNode root = new AhoCorasickNode();
        private boolean isBuilt = false;

        public void insert(String keyword) {
            if (keyword == null || keyword.isEmpty()) return;
            AhoCorasickNode curr = root;
            for (char ch : keyword.toCharArray()) {
                curr = curr.children.computeIfAbsent(ch, k -> new AhoCorasickNode());
            }
            curr.output.add(keyword);
            isBuilt = false;
        }

        public void buildFailureLinks() {
            Queue<AhoCorasickNode> queue = new LinkedList<>();
            // Level 1 children fail to root
            for (AhoCorasickNode child : root.children.values()) {
                child.fail = root;
                queue.add(child);
            }

            while (!queue.isEmpty()) {
                AhoCorasickNode curr = queue.poll();
                for (Map.Entry<Character, AhoCorasickNode> entry : curr.children.entrySet()) {
                    char ch = entry.getKey();
                    AhoCorasickNode child = entry.getValue();

                    AhoCorasickNode f = curr.fail;
                    while (f != null && !f.children.containsKey(ch)) {
                        f = f.fail;
                    }
                    child.fail = (f != null) ? f.children.get(ch) : root;
                    // Merge dictionary outputs from fail link
                    if (child.fail != null) {
                        child.output.addAll(child.fail.output);
                    }
                    queue.add(child);
                }
            }
            isBuilt = true;
        }

        public List<AhoCorasickMatch> search(String text) {
            if (!isBuilt) {
                buildFailureLinks();
            }
            List<AhoCorasickMatch> results = new ArrayList<>();
            if (text == null || text.isEmpty()) return results;

            AhoCorasickNode curr = root;
            for (int i = 0; i < text.length(); i++) {
                char ch = text.charAt(i);
                while (curr != null && !curr.children.containsKey(ch)) {
                    curr = curr.fail;
                }
                if (curr == null) {
                    curr = root;
                    continue;
                }
                curr = curr.children.get(ch);
                if (curr != null) {
                    for (String pattern : curr.output) {
                        results.add(new AhoCorasickMatch(pattern, i - pattern.length() + 1, i));
                    }
                }
            }
            return results;
        }
    }
}
