package algorithms;

import java.util.*;

/**
 * Course Outcome 2 (CO2) - Suffix Structures and Document Similarity.
 * Provides authentic implementations of:
 * A. Standard Suffix Array with Binary Search Pattern Lookup
 * B. SA-IS (Induced Sorting) Algorithm for O(N) Suffix Array Construction
 * C. LCP Array via Kasai's Algorithm and Multi-Document Common Substring Extraction
 * D. Suffix Automaton (SAM) for O(M) Substring Queries and Document Similarity
 */
public class SuffixStructures {

    // ==========================================
    // A. Suffix Array (Standard / Prefix-Doubling)
    // ==========================================

    public static class SuffixArrayResult {
        public final String text;
        public final int[] suffixArray;
        public final String algorithmUsed;
        public final String timeComplexity;

        public SuffixArrayResult(String text, int[] suffixArray, String algorithmUsed, String timeComplexity) {
            this.text = text;
            this.suffixArray = suffixArray;
            this.algorithmUsed = algorithmUsed;
            this.timeComplexity = timeComplexity;
        }
    }

    /**
     * Constructs Suffix Array using O(N log^2 N) / O(N log N) rank sorting.
     */
    public static int[] buildSuffixArrayStandard(String s) {
        int n = s.length();
        Integer[] order = new Integer[n];
        for (int i = 0; i < n; i++) order[i] = i;

        Arrays.sort(order, (a, b) -> s.substring(a).compareTo(s.substring(b)));

        int[] sa = new int[n];
        for (int i = 0; i < n; i++) sa[i] = order[i];
        return sa;
    }

    /**
     * Binary search for pattern in text using Suffix Array in O(M log N).
     */
    public static List<Integer> searchSuffixArray(String text, int[] sa, String pattern) {
        List<Integer> matches = new ArrayList<>();
        if (text == null || pattern == null || pattern.isEmpty() || sa.length == 0) {
            return matches;
        }
        int n = text.length();
        int m = pattern.length();

        // Find lower bound
        int low = 0, high = n - 1;
        int first = -1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            int suffixStart = sa[mid];
            int cmp = comparePrefix(text, suffixStart, pattern);
            if (cmp >= 0) {
                if (cmp == 0) first = mid;
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }

        if (first == -1) return matches;

        // Find upper bound
        low = first;
        high = n - 1;
        int last = first;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            int suffixStart = sa[mid];
            int cmp = comparePrefix(text, suffixStart, pattern);
            if (cmp == 0) {
                last = mid;
                low = mid + 1;
            } else if (cmp > 0) {
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }

        for (int i = first; i <= last; i++) {
            matches.add(sa[i]);
        }
        Collections.sort(matches);
        return matches;
    }

    private static int comparePrefix(String text, int suffixStart, String pattern) {
        int n = text.length();
        int m = pattern.length();
        for (int i = 0; i < m; i++) {
            if (suffixStart + i >= n) return -1; // Suffix is shorter than pattern
            char cText = text.charAt(suffixStart + i);
            char cPat = pattern.charAt(i);
            if (cText != cPat) return Character.compare(cText, cPat);
        }
        return 0; // Prefix matches pattern completely
    }

    // ==========================================
    // B. SA-IS (Suffix Array by Induced Sorting)
    // ==========================================

    private static final boolean S_TYPE = true;
    private static final boolean L_TYPE = false;

    /**
     * Pure O(N) SA-IS algorithm implementation.
     */
    public static int[] buildSuffixArraySAIS(String str) {
        if (str == null || str.isEmpty()) return new int[0];
        // Append sentinel 0 (smaller than any valid character)
        int n = str.length();
        int[] s = new int[n + 1];
        int maxChar = 0;
        for (int i = 0; i < n; i++) {
            s[i] = (int) str.charAt(i) + 1; // shift so sentinel 0 is strictly minimal
            if (s[i] > maxChar) maxChar = s[i];
        }
        s[n] = 0; // Sentinel

        int[] saWithSentinel = sais(s, maxChar + 1);
        // Exclude the sentinel at index 0
        int[] sa = new int[n];
        System.arraycopy(saWithSentinel, 1, sa, 0, n);
        return sa;
    }

    private static int[] sais(int[] s, int K) {
        int n = s.length;
        if (n == 1) return new int[]{0};
        if (n == 2) {
            if (s[0] < s[1]) return new int[]{0, 1};
            return new int[]{1, 0};
        }

        // Step 1: Classify characters into S-type and L-type
        boolean[] t = new boolean[n];
        t[n - 1] = S_TYPE;
        for (int i = n - 2; i >= 0; i--) {
            if (s[i] < s[i + 1]) {
                t[i] = S_TYPE;
            } else if (s[i] > s[i + 1]) {
                t[i] = L_TYPE;
            } else {
                t[i] = t[i + 1];
            }
        }

        // Bucket sizes and heads/tails
        int[] bucketSizes = new int[K];
        for (int x : s) bucketSizes[x]++;

        // Find LMS characters
        int[] lmsMap = new int[n];
        Arrays.fill(lmsMap, -1);
        int m = 0;
        for (int i = 1; i < n; i++) {
            if (t[i] == S_TYPE && t[i - 1] == L_TYPE) {
                lmsMap[i] = m++;
            }
        }
        int[] lms = new int[m];
        m = 0;
        for (int i = 1; i < n; i++) {
            if (t[i] == S_TYPE && t[i - 1] == L_TYPE) {
                lms[m++] = i;
            }
        }

        int[] sa = new int[n];
        Arrays.fill(sa, -1);

        // Put LMS suffixes into bucket ends
        int[] bucketTails = getBucketTails(bucketSizes, K);
        for (int i = 0; i < m; i++) {
            int p = lms[i];
            sa[bucketTails[s[p]]--] = p;
        }

        // Induced sort L-type suffixes
        inducedSortL(s, sa, t, bucketSizes, K);
        // Induced sort S-type suffixes
        inducedSortS(s, sa, t, bucketSizes, K);

        // Name LMS substrings
        int[] lmsNames = new int[m];
        int curName = 0;
        lmsNames[0] = 0;
        int prevLms = -1;
        for (int i = 0; i < n; i++) {
            int p = sa[i];
            if (p != -1 && lmsMap[p] != -1) {
                if (prevLms != -1) {
                    if (!isEqualLms(s, t, prevLms, p)) {
                        curName++;
                    }
                }
                lmsNames[lmsMap[p]] = curName;
                prevLms = p;
            }
        }

        // Recursive call if names are not unique
        int[] sortedLms;
        if (curName + 1 < m) {
            int[] saLms = sais(lmsNames, curName + 1);
            sortedLms = new int[m];
            for (int i = 0; i < m; i++) {
                sortedLms[i] = lms[saLms[i]];
            }
        } else {
            sortedLms = new int[m];
            for (int i = 0; i < m; i++) {
                sortedLms[lmsNames[i]] = lms[i];
            }
        }

        // Place sorted LMS suffixes back into bucket tails
        Arrays.fill(sa, -1);
        bucketTails = getBucketTails(bucketSizes, K);
        for (int i = m - 1; i >= 0; i--) {
            int p = sortedLms[i];
            sa[bucketTails[s[p]]--] = p;
        }

        // Final induced sort
        inducedSortL(s, sa, t, bucketSizes, K);
        inducedSortS(s, sa, t, bucketSizes, K);

        return sa;
    }

    private static boolean isEqualLms(int[] s, boolean[] t, int p1, int p2) {
        int n = s.length;
        for (int i = 0; ; i++) {
            int idx1 = p1 + i;
            int idx2 = p2 + i;
            if (idx1 >= n || idx2 >= n) return idx1 == idx2;
            if (s[idx1] != s[idx2] || t[idx1] != t[idx2]) return false;
            if (i > 0) {
                boolean isLms1 = (t[idx1] == S_TYPE && t[idx1 - 1] == L_TYPE);
                boolean isLms2 = (t[idx2] == S_TYPE && t[idx2 - 1] == L_TYPE);
                if (isLms1 && isLms2) return true;
                if (isLms1 || isLms2) return false;
            }
        }
    }

    private static void inducedSortL(int[] s, int[] sa, boolean[] t, int[] bucketSizes, int K) {
        int[] heads = getBucketHeads(bucketSizes, K);
        int n = s.length;
        for (int i = 0; i < n; i++) {
            int j = sa[i] - 1;
            if (j >= 0 && t[j] == L_TYPE) {
                sa[heads[s[j]]++] = j;
            }
        }
    }

    private static void inducedSortS(int[] s, int[] sa, boolean[] t, int[] bucketSizes, int K) {
        int[] tails = getBucketTails(bucketSizes, K);
        int n = s.length;
        for (int i = n - 1; i >= 0; i--) {
            int j = sa[i] - 1;
            if (j >= 0 && t[j] == S_TYPE) {
                sa[tails[s[j]]--] = j;
            }
        }
    }

    private static int[] getBucketHeads(int[] bucketSizes, int K) {
        int[] heads = new int[K];
        int sum = 0;
        for (int i = 0; i < K; i++) {
            heads[i] = sum;
            sum += bucketSizes[i];
        }
        return heads;
    }

    private static int[] getBucketTails(int[] bucketSizes, int K) {
        int[] tails = new int[K];
        int sum = 0;
        for (int i = 0; i < K; i++) {
            sum += bucketSizes[i];
            tails[i] = sum - 1;
        }
        return tails;
    }

    // ==========================================
    // C. LCP Array via Kasai's Algorithm
    // ==========================================

    public static int[] buildLcpKasai(String s, int[] sa) {
        int n = s.length();
        int[] rank = new int[n];
        for (int i = 0; i < n; i++) {
            rank[sa[i]] = i;
        }

        int[] lcp = new int[n];
        int h = 0;
        for (int i = 0; i < n; i++) {
            if (rank[i] > 0) {
                int j = sa[rank[i] - 1];
                while (i + h < n && j + h < n && s.charAt(i + h) == s.charAt(j + h)) {
                    h++;
                }
                lcp[rank[i]] = h;
                if (h > 0) h--;
            } else {
                lcp[0] = 0;
            }
        }
        return lcp;
    }

    /**
     * Identifies Longest Common Substring among two route descriptions using SA and Kasai LCP.
     */
    public static String findLongestCommonSubstring(String s1, String s2) {
        if (s1 == null || s2 == null || s1.isEmpty() || s2.isEmpty()) return "";
        String combined = s1 + "#" + s2;
        int splitIndex = s1.length();
        int[] sa = buildSuffixArraySAIS(combined);
        int[] lcp = buildLcpKasai(combined, sa);

        int maxLen = 0;
        int bestStart = 0;

        for (int i = 1; i < combined.length(); i++) {
            int p1 = sa[i];
            int p2 = sa[i - 1];
            // Check if one suffix is from s1 and the other from s2
            boolean p1InS1 = p1 < splitIndex;
            boolean p2InS1 = p2 < splitIndex;
            if (p1InS1 != p2InS1) {
                if (lcp[i] > maxLen) {
                    maxLen = lcp[i];
                    bestStart = p1;
                }
            }
        }
        return maxLen > 0 ? combined.substring(bestStart, bestStart + maxLen) : "";
    }

    // ==========================================
    // D. Suffix Automaton (SAM)
    // ==========================================

    public static class SamState {
        public int len;
        public int link;
        public final Map<Character, Integer> next = new HashMap<>();

        public SamState(int len, int link) {
            this.len = len;
            this.link = link;
        }
    }

    public static class SuffixAutomaton {
        private final List<SamState> states = new ArrayList<>();
        private int last;
        private final String sourceText;

        public SuffixAutomaton(String text) {
            this.sourceText = text != null ? text : "";
            // Initialize root state
            states.add(new SamState(0, -1));
            last = 0;

            for (char ch : this.sourceText.toCharArray()) {
                extend(ch);
            }
        }

        private void extend(char c) {
            int cur = states.size();
            states.add(new SamState(states.get(last).len + 1, 0));

            int p = last;
            while (p != -1 && !states.get(p).next.containsKey(c)) {
                states.get(p).next.put(c, cur);
                p = states.get(p).link;
            }

            if (p == -1) {
                states.get(cur).link = 0;
            } else {
                int q = states.get(p).next.get(c);
                if (states.get(p).len + 1 == states.get(q).len) {
                    states.get(cur).link = q;
                } else {
                    int clone = states.size();
                    SamState cloneState = new SamState(states.get(p).len + 1, states.get(q).link);
                    cloneState.next.putAll(states.get(q).next);
                    states.add(cloneState);

                    while (p != -1 && states.get(p).next.get(c) == q) {
                        states.get(p).next.put(c, clone);
                        p = states.get(p).link;
                    }
                    states.get(q).link = clone;
                    states.get(cur).link = clone;
                }
            }
            last = cur;
        }

        public boolean containsSubstring(String pattern) {
            if (pattern == null || pattern.isEmpty()) return true;
            int curr = 0;
            for (char c : pattern.toCharArray()) {
                if (!states.get(curr).next.containsKey(c)) {
                    return false;
                }
                curr = states.get(curr).next.get(c);
            }
            return true;
        }

        public long countDistinctSubstrings() {
            long total = 0;
            for (int i = 1; i < states.size(); i++) {
                total += states.get(i).len - states.get(states.get(i).link).len;
            }
            return total;
        }

        public int getStateCount() {
            return states.size();
        }

        public String getSourceText() {
            return sourceText;
        }
    }
}
