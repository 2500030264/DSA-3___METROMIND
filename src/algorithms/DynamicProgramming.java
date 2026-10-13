package algorithms;

import java.util.*;

/**
 * Course Outcome 3 (CO3) - Advanced Dynamic Programming.
 * Provides authentic implementations of:
 * A. Levenshtein Distance (for station name spelling corrections)
 * B. Damerau-Levenshtein Distance (unrestricted true Damerau with last-seen character table)
 * C. Bitmask Dynamic Programming (TSP / multi-destination route optimization)
 * D. Matrix-Chain Multiplication (optimal analytics pipeline matrix parenthesization)
 * E. Optimal Binary Search Tree (OBST with dynamic programming tables)
 */
public class DynamicProgramming {

    // ==========================================
    // A. Levenshtein Distance
    // ==========================================

    public static int levenshteinDistance(String s1, String s2) {
        if (s1 == null || s2 == null) return Integer.MAX_VALUE;
        int m = s1.length();
        int n = s2.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) dp[i][0] = i;
        for (int j = 0; j <= n; j++) dp[0][j] = j;

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(
                    dp[i - 1][j] + 1,       // deletion
                    Math.min(
                        dp[i][j - 1] + 1,   // insertion
                        dp[i - 1][j - 1] + cost // substitution
                    )
                );
            }
        }
        return dp[m][n];
    }

    public static class SpellingSuggestion {
        public final String candidate;
        public final int distance;

        public SpellingSuggestion(String candidate, int distance) {
            this.candidate = candidate;
            this.distance = distance;
        }

        @Override
        public String toString() {
            return String.format("%s (Distance: %d)", candidate, distance);
        }
    }

    public static List<SpellingSuggestion> suggestStationNames(String query, List<String> stationNames, int maxDistance) {
        List<SpellingSuggestion> suggestions = new ArrayList<>();
        if (query == null || stationNames == null) return suggestions;

        for (String name : stationNames) {
            int dist = levenshteinDistance(query.toLowerCase(), name.toLowerCase());
            if (dist <= maxDistance) {
                suggestions.add(new SpellingSuggestion(name, dist));
            }
        }
        suggestions.sort(Comparator.comparingInt(a -> a.distance));
        return suggestions;
    }

    // ==========================================
    // B. Damerau-Levenshtein Distance (Unrestricted)
    // ==========================================

    /**
     * Unrestricted Damerau-Levenshtein distance algorithm.
     * Handles insertion, deletion, substitution, and transposition of adjacent characters
     * with arbitrary intervening edits using the last-seen-character dictionary logic.
     */
    public static int damerauLevenshteinDistance(String a, String b) {
        if (a == null || b == null) return Integer.MAX_VALUE;
        int m = a.length();
        int n = b.length();
        int inf = m + n;

        int[][] h = new int[m + 2][n + 2];
        h[0][0] = inf;
        for (int i = 0; i <= m; i++) {
            h[i + 1][0] = inf;
            h[i + 1][1] = i;
        }
        for (int j = 0; j <= n; j++) {
            h[0][j + 1] = inf;
            h[1][j + 1] = j;
        }

        Map<Character, Integer> da = new HashMap<>();

        for (int i = 1; i <= m; i++) {
            int db = 0;
            char ca = a.charAt(i - 1);
            for (int j = 1; j <= n; j++) {
                char cb = b.charAt(j - 1);
                int i1 = da.getOrDefault(cb, 0);
                int j1 = db;
                int cost = (ca == cb) ? 0 : 1;
                if (cost == 0) db = j;

                h[i + 1][j + 1] = Math.min(
                    h[i][j] + cost, // substitution
                    Math.min(
                        h[i + 1][j] + 1, // insertion
                        Math.min(
                            h[i][j + 1] + 1, // deletion
                            h[i1][j1] + (i - i1 - 1) + 1 + (j - j1 - 1) // transposition
                        )
                    )
                );
            }
            da.put(ca, i);
        }
        return h[m + 1][n + 1];
    }

    // ==========================================
    // C. Bitmask Dynamic Programming (TSP Multi-Stop)
    // ==========================================

    public static class BitmaskTspResult {
        public final List<String> stationTour;
        public final double totalCost;
        public final boolean isTourComplete;
        public final String complexity = "O(2^K * K^2) time, O(2^K * K) space where K = number of stations";

        public BitmaskTspResult(List<String> stationTour, double totalCost, boolean isTourComplete) {
            this.stationTour = stationTour;
            this.totalCost = totalCost;
            this.isTourComplete = isTourComplete;
        }
    }

    /**
     * Solves multi-destination optimal visit order using Bitmask DP.
     * @param stationIds Ordered list of K station identifiers (index 0 is start).
     * @param distMatrix K x K distance matrix between these stations.
     * @param returnToStart True if the vehicle must return to the start station.
     */
    public static BitmaskTspResult solveBitmaskTsp(List<String> stationIds, double[][] distMatrix, boolean returnToStart) {
        int k = stationIds.size();
        if (k == 0) return new BitmaskTspResult(Collections.emptyList(), 0.0, true);
        if (k == 1) {
            return new BitmaskTspResult(Collections.singletonList(stationIds.get(0)), 0.0, true);
        }
        if (k > 18) {
            throw new IllegalArgumentException("Instance size " + k + " is too large for exact Bitmask DP (limit K <= 18).");
        }

        int numStates = 1 << k;
        double[][] dp = new double[numStates][k];
        int[][] parent = new int[numStates][k];

        for (int i = 0; i < numStates; i++) {
            Arrays.fill(dp[i], Double.POSITIVE_INFINITY);
            Arrays.fill(parent[i], -1);
        }

        // Base state: at start station (index 0) with mask 1
        dp[1][0] = 0;

        for (int mask = 1; mask < numStates; mask++) {
            for (int u = 0; u < k; u++) {
                if (dp[mask][u] == Double.POSITIVE_INFINITY) continue;
                for (int v = 0; v < k; v++) {
                    if ((mask & (1 << v)) == 0) {
                        double edgeCost = distMatrix[u][v];
                        if (edgeCost < Double.POSITIVE_INFINITY) {
                            int nextMask = mask | (1 << v);
                            double newCost = dp[mask][u] + edgeCost;
                            if (newCost < dp[nextMask][v]) {
                                dp[nextMask][v] = newCost;
                                parent[nextMask][v] = u;
                            }
                        }
                    }
                }
            }
        }

        int finalMask = numStates - 1;
        double bestCost = Double.POSITIVE_INFINITY;
        int lastNode = -1;

        if (returnToStart) {
            for (int u = 1; u < k; u++) {
                if (dp[finalMask][u] < Double.POSITIVE_INFINITY && distMatrix[u][0] < Double.POSITIVE_INFINITY) {
                    double tourCost = dp[finalMask][u] + distMatrix[u][0];
                    if (tourCost < bestCost) {
                        bestCost = tourCost;
                        lastNode = u;
                    }
                }
            }
        } else {
            for (int u = 0; u < k; u++) {
                if (dp[finalMask][u] < bestCost) {
                    bestCost = dp[finalMask][u];
                    lastNode = u;
                }
            }
        }

        if (bestCost == Double.POSITIVE_INFINITY) {
            return new BitmaskTspResult(Collections.emptyList(), -1.0, false);
        }

        // Reconstruct path
        List<String> tour = new ArrayList<>();
        int currMask = finalMask;
        int currNode = lastNode;

        List<Integer> reversePath = new ArrayList<>();
        while (currNode != -1) {
            reversePath.add(currNode);
            int prevNode = parent[currMask][currNode];
            currMask ^= (1 << currNode);
            currNode = prevNode;
        }
        Collections.reverse(reversePath);

        for (int idx : reversePath) {
            tour.add(stationIds.get(idx));
        }
        if (returnToStart) {
            tour.add(stationIds.get(0));
        }

        return new BitmaskTspResult(tour, bestCost, true);
    }

    // ==========================================
    // D. Matrix-Chain Multiplication
    // ==========================================

    public static class MatrixChainResult {
        public final int[] dimensions;
        public final long minMultiplications;
        public final String optimalParenthesization;
        public final int[][] sTable;
        public final String complexity = "O(N^3) time, O(N^2) space";

        public MatrixChainResult(int[] dimensions, long minMultiplications, String optimalParenthesization, int[][] sTable) {
            this.dimensions = dimensions;
            this.minMultiplications = minMultiplications;
            this.optimalParenthesization = optimalParenthesization;
            this.sTable = sTable;
        }
    }

    public static MatrixChainResult solveMatrixChain(int[] p) {
        if (p == null || p.length < 2) {
            throw new IllegalArgumentException("Matrix dimensions array must have at least 2 entries for 1 matrix.");
        }
        int n = p.length - 1; // Number of matrices
        long[][] m = new long[n + 1][n + 1];
        int[][] s = new int[n + 1][n + 1];

        for (int len = 2; len <= n; len++) {
            for (int i = 1; i <= n - len + 1; i++) {
                int j = i + len - 1;
                m[i][j] = Long.MAX_VALUE;
                for (int k = i; k <= j - 1; k++) {
                    long cost = m[i][k] + m[k + 1][j] + (long) p[i - 1] * p[k] * p[j];
                    if (cost < m[i][j]) {
                        m[i][j] = cost;
                        s[i][j] = k;
                    }
                }
            }
        }

        String paren = buildOptimalParenthesization(s, 1, n);
        return new MatrixChainResult(p, m[1][n], paren, s);
    }

    private static String buildOptimalParenthesization(int[][] s, int i, int j) {
        if (i == j) {
            return "M" + i;
        } else {
            return "(" + buildOptimalParenthesization(s, i, s[i][j]) + " x " +
                   buildOptimalParenthesization(s, s[i][j] + 1, j) + ")";
        }
    }

    // ==========================================
    // E. Optimal Binary Search Tree (OBST)
    // ==========================================

    public static class ObstResult {
        public final String[] keys;
        public final double[] frequencies;
        public final double minSearchCost;
        public final int[][] rootTable;
        public final String treeStructure;
        public final String complexity = "O(N^3) time, O(N^2) space";

        public ObstResult(String[] keys, double[] frequencies, double minSearchCost,
                          int[][] rootTable, String treeStructure) {
            this.keys = keys;
            this.frequencies = frequencies;
            this.minSearchCost = minSearchCost;
            this.rootTable = rootTable;
            this.treeStructure = treeStructure;
        }
    }

    /**
     * Solves Optimal Binary Search Tree given keys and their search frequencies.
     */
    public static ObstResult solveObst(String[] keys, double[] p) {
        int n = keys.length;
        if (n == 0) return new ObstResult(keys, p, 0.0, new int[0][0], "Empty");

        // Sort keys and associate frequencies
        Integer[] order = new Integer[n];
        for (int i = 0; i < n; i++) order[i] = i;
        Arrays.sort(order, Comparator.comparing(a -> keys[a]));

        String[] sortedKeys = new String[n];
        double[] sortedP = new double[n + 1];
        for (int i = 0; i < n; i++) {
            sortedKeys[i] = keys[order[i]];
            sortedP[i + 1] = p[order[i]];
        }

        double[][] e = new double[n + 2][n + 2];
        double[][] w = new double[n + 2][n + 2];
        int[][] root = new int[n + 1][n + 1];

        for (int i = 1; i <= n + 1; i++) {
            e[i][i - 1] = 0;
            w[i][i - 1] = 0;
        }

        for (int len = 1; len <= n; len++) {
            for (int i = 1; i <= n - len + 1; i++) {
                int j = i + len - 1;
                e[i][j] = Double.POSITIVE_INFINITY;
                w[i][j] = w[i][j - 1] + sortedP[j];
                for (int r = i; r <= j; r++) {
                    double t = e[i][r - 1] + e[r + 1][j] + w[i][j];
                    if (t < e[i][j]) {
                        e[i][j] = t;
                        root[i][j] = r;
                    }
                }
            }
        }

        StringBuilder treeSb = new StringBuilder();
        formatObstStructure(root, sortedKeys, 1, n, -1, "ROOT", treeSb, 0);

        return new ObstResult(sortedKeys, sortedP, e[1][n], root, treeSb.toString());
    }

    private static void formatObstStructure(int[][] root, String[] keys, int i, int j, int parent, String side, StringBuilder sb, int depth) {
        if (i <= j) {
            int r = root[i][j];
            sb.append("  ".repeat(depth))
              .append(side).append(": ").append(keys[r - 1]);
            if (parent != -1) {
                sb.append(" (Child of ").append(keys[parent - 1]).append(")");
            }
            sb.append("\n");
            formatObstStructure(root, keys, i, r - 1, r, "Left", sb, depth + 1);
            formatObstStructure(root, keys, r + 1, j, r, "Right", sb, depth + 1);
        }
    }
}
