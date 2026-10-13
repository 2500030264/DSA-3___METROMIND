package algorithms;

import java.util.*;

/**
 * Course Outcome 5 (CO5) - NP-Completeness and Approximation Algorithms.
 * Provides authentic implementations of:
 * A. SAT / 3-SAT (DPLL Solver with Unit Propagation and Pure Literal Elimination)
 * B. Maximum Clique (Bron-Kerbosch Algorithm with Pivoting)
 * C. Maximum Independent Set (Complement Graph Max-Clique Reduction)
 * D. Minimum Vertex Cover (Exact Branch-and-Bound / Recursive Search)
 * E. Vertex-Cover 2-Approximation (Maximal Matching Edge Algorithm with 2x Guarantee)
 */
public class NPConstraints {

    // ==========================================
    // A. SAT / 3-SAT (DPLL Solver)
    // ==========================================

    public static class SatResult {
        public final boolean isSatisfiable;
        public final Map<String, Boolean> model; // Variable name -> true/false
        public final int variableCount;
        public final int clauseCount;
        public final String algorithm = "DPLL (Unit Propagation + Pure Literal Elimination + Backtracking)";

        public SatResult(boolean isSatisfiable, Map<String, Boolean> model, int variableCount, int clauseCount) {
            this.isSatisfiable = isSatisfiable;
            this.model = model != null ? model : Collections.emptyMap();
            this.variableCount = variableCount;
            this.clauseCount = clauseCount;
        }
    }

    public static class CnfFormula {
        public final List<String> variables;
        public final List<List<Integer>> clauses; // Literals: +varIndex for positive, -varIndex for negated (1-indexed)
        public final Map<String, Integer> varToIndex = new HashMap<>();

        public CnfFormula(List<String> variableNames) {
            this.variables = new ArrayList<>(variableNames);
            this.clauses = new ArrayList<>();
            for (int i = 0; i < variables.size(); i++) {
                varToIndex.put(variables.get(i), i + 1);
            }
        }

        public void addClause(String... literals) {
            List<Integer> clause = new ArrayList<>();
            for (String lit : literals) {
                String clean = lit.trim();
                boolean negated = clean.startsWith("~") || clean.startsWith("!");
                String var = negated ? clean.substring(1).trim() : clean;
                if (!varToIndex.containsKey(var)) {
                    variables.add(var);
                    varToIndex.put(var, variables.size());
                }
                int idx = varToIndex.get(var);
                clause.add(negated ? -idx : idx);
            }
            clauses.add(clause);
        }
    }

    public static SatResult solveSat(CnfFormula formula) {
        int n = formula.variables.size();
        int m = formula.clauses.size();
        int[] assignment = new int[n + 1]; // 0: unassigned, 1: true, -1: false

        boolean sat = dpll(formula.clauses, assignment, n);
        Map<String, Boolean> model = new LinkedHashMap<>();
        if (sat) {
            for (int i = 1; i <= n; i++) {
                model.put(formula.variables.get(i - 1), assignment[i] >= 0);
            }
        }
        return new SatResult(sat, model, n, m);
    }

    private static boolean dpll(List<List<Integer>> clauses, int[] assignment, int numVars) {
        // Unit propagation
        boolean changed = true;
        while (changed) {
            changed = false;
            for (List<Integer> clause : clauses) {
                int unassignedLit = 0;
                int unassignedCount = 0;
                boolean clauseSatisfied = false;

                for (int lit : clause) {
                    int var = Math.abs(lit);
                    int val = assignment[var];
                    if (val != 0) {
                        if ((lit > 0 && val == 1) || (lit < 0 && val == -1)) {
                            clauseSatisfied = true;
                            break;
                        }
                    } else {
                        unassignedCount++;
                        unassignedLit = lit;
                    }
                }

                if (clauseSatisfied) continue;
                if (unassignedCount == 0) return false; // Clause cannot be satisfied (conflict)
                if (unassignedCount == 1) {
                    int var = Math.abs(unassignedLit);
                    assignment[var] = (unassignedLit > 0) ? 1 : -1;
                    changed = true;
                }
            }
        }

        // Pure literal elimination
        int[] posCounts = new int[numVars + 1];
        int[] negCounts = new int[numVars + 1];
        for (List<Integer> clause : clauses) {
            boolean satisfied = false;
            for (int lit : clause) {
                int var = Math.abs(lit);
                if ((lit > 0 && assignment[var] == 1) || (lit < 0 && assignment[var] == -1)) {
                    satisfied = true;
                    break;
                }
            }
            if (!satisfied) {
                for (int lit : clause) {
                    int var = Math.abs(lit);
                    if (assignment[var] == 0) {
                        if (lit > 0) posCounts[var]++;
                        else negCounts[var]++;
                    }
                }
            }
        }

        for (int i = 1; i <= numVars; i++) {
            if (assignment[i] == 0) {
                if (posCounts[i] > 0 && negCounts[i] == 0) {
                    assignment[i] = 1;
                } else if (negCounts[i] > 0 && posCounts[i] == 0) {
                    assignment[i] = -1;
                }
            }
        }

        // Check if all clauses are satisfied
        boolean allSatisfied = true;
        for (List<Integer> clause : clauses) {
            boolean sat = false;
            for (int lit : clause) {
                int var = Math.abs(lit);
                if ((lit > 0 && assignment[var] == 1) || (lit < 0 && assignment[var] == -1)) {
                    sat = true;
                    break;
                }
            }
            if (!sat) {
                allSatisfied = false;
                break;
            }
        }
        if (allSatisfied) return true;

        // Choose next unassigned variable for branching
        int nextVar = 0;
        for (int i = 1; i <= numVars; i++) {
            if (assignment[i] == 0) {
                nextVar = i;
                break;
            }
        }
        if (nextVar == 0) return false;

        // Try setting true
        int[] copy1 = Arrays.copyOf(assignment, assignment.length);
        copy1[nextVar] = 1;
        if (dpll(clauses, copy1, numVars)) {
            System.arraycopy(copy1, 0, assignment, 0, copy1.length);
            return true;
        }

        // Try setting false
        int[] copy2 = Arrays.copyOf(assignment, assignment.length);
        copy2[nextVar] = -1;
        if (dpll(clauses, copy2, numVars)) {
            System.arraycopy(copy2, 0, assignment, 0, copy2.length);
            return true;
        }

        return false;
    }

    // ==========================================
    // B. Maximum Clique (Bron-Kerbosch)
    // ==========================================

    public static class Graph {
        public final List<String> vertices;
        public final Map<String, Integer> vertexIndex = new HashMap<>();
        public final boolean[][] adj;

        public Graph(List<String> vertexNames) {
            this.vertices = new ArrayList<>(vertexNames);
            int n = vertices.size();
            this.adj = new boolean[n][n];
            for (int i = 0; i < n; i++) {
                vertexIndex.put(vertices.get(i), i);
            }
        }

        public void addEdge(String u, String v) {
            Integer ui = vertexIndex.get(u);
            Integer vi = vertexIndex.get(v);
            if (ui != null && vi != null && !ui.equals(vi)) {
                adj[ui][vi] = true;
                adj[vi][ui] = true;
            }
        }

        public Graph getComplement() {
            Graph comp = new Graph(vertices);
            int n = vertices.size();
            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    if (!adj[i][j]) {
                        comp.addEdge(vertices.get(i), vertices.get(j));
                    }
                }
            }
            return comp;
        }
    }

    public static List<String> findMaximumClique(Graph graph) {
        int n = graph.vertices.size();
        Set<Integer> r = new HashSet<>();
        Set<Integer> p = new HashSet<>();
        Set<Integer> x = new HashSet<>();
        for (int i = 0; i < n; i++) p.add(i);

        List<Integer> bestClique = new ArrayList<>();
        bronKerboschPivot(graph, r, p, x, bestClique);

        List<String> result = new ArrayList<>();
        for (int idx : bestClique) {
            result.add(graph.vertices.get(idx));
        }
        return result;
    }

    private static void bronKerboschPivot(Graph g, Set<Integer> r, Set<Integer> p, Set<Integer> x, List<Integer> best) {
        if (p.isEmpty() && x.isEmpty()) {
            if (r.size() > best.size()) {
                best.clear();
                best.addAll(r);
            }
            return;
        }

        // Pivot selection from P union X
        Set<Integer> pxUnion = new HashSet<>(p);
        pxUnion.addAll(x);
        int pivot = pxUnion.iterator().next();

        Set<Integer> candidates = new HashSet<>(p);
        for (int v : p) {
            if (g.adj[pivot][v]) {
                candidates.remove(v);
            }
        }

        for (int v : candidates) {
            Set<Integer> nextR = new HashSet<>(r);
            nextR.add(v);

            Set<Integer> nextP = new HashSet<>();
            for (int u : p) if (g.adj[v][u]) nextP.add(u);

            Set<Integer> nextX = new HashSet<>();
            for (int u : x) if (g.adj[v][u]) nextX.add(u);

            bronKerboschPivot(g, nextR, nextP, nextX, best);

            p.remove(v);
            x.add(v);
        }
    }

    // ==========================================
    // C. Maximum Independent Set
    // ==========================================

    public static List<String> findMaximumIndependentSet(Graph graph) {
        // Max Independent Set of G is the Max Clique of complement graph ~G
        Graph complement = graph.getComplement();
        return findMaximumClique(complement);
    }

    // ==========================================
    // D. Minimum Vertex Cover (Exact)
    // ==========================================

    public static class VertexCoverResult {
        public final List<String> cover;
        public final int size;
        public final boolean isExact;
        public final boolean allEdgesCovered;

        public VertexCoverResult(List<String> cover, int size, boolean isExact, boolean allEdgesCovered) {
            this.cover = cover;
            this.size = size;
            this.isExact = isExact;
            this.allEdgesCovered = allEdgesCovered;
        }
    }

    public static VertexCoverResult findExactMinimumVertexCover(Graph graph) {
        int n = graph.vertices.size();
        List<int[]> edges = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (graph.adj[i][j]) {
                    edges.add(new int[]{i, j});
                }
            }
        }

        if (edges.isEmpty()) {
            return new VertexCoverResult(Collections.emptyList(), 0, true, true);
        }

        List<Integer> bestCover = new ArrayList<>();
        for (int k = 1; k <= n; k++) {
            List<Integer> current = new ArrayList<>();
            if (findCoverOfSizeK(edges, current, k, 0, n)) {
                bestCover = current;
                break;
            }
        }

        List<String> result = new ArrayList<>();
        for (int idx : bestCover) {
            result.add(graph.vertices.get(idx));
        }
        return new VertexCoverResult(result, result.size(), true, true);
    }

    private static boolean findCoverOfSizeK(List<int[]> edges, List<Integer> current, int k, int startNode, int n) {
        if (current.size() == k) {
            return isCoverValid(edges, current);
        }
        for (int i = startNode; i < n; i++) {
            current.add(i);
            if (findCoverOfSizeK(edges, current, k, i + 1, n)) return true;
            current.remove(current.size() - 1);
        }
        return false;
    }

    private static boolean isCoverValid(List<int[]> edges, List<Integer> cover) {
        Set<Integer> set = new HashSet<>(cover);
        for (int[] e : edges) {
            if (!set.contains(e[0]) && !set.contains(e[1])) return false;
        }
        return true;
    }

    // ==========================================
    // E. Vertex-Cover 2-Approximation
    // ==========================================

    public static VertexCoverResult findVertexCover2Approx(Graph graph) {
        int n = graph.vertices.size();
        boolean[][] edgesCopy = new boolean[n][n];
        int edgeCount = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                edgesCopy[i][j] = graph.adj[i][j];
                if (i < j && edgesCopy[i][j]) edgeCount++;
            }
        }

        Set<Integer> coverIndices = new HashSet<>();

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (edgesCopy[i][j]) {
                    // Pick edge (i, j), add both endpoints
                    coverIndices.add(i);
                    coverIndices.add(j);
                    // Remove all incident edges to i and j
                    for (int k = 0; k < n; k++) {
                        edgesCopy[i][k] = false;
                        edgesCopy[k][i] = false;
                        edgesCopy[j][k] = false;
                        edgesCopy[k][j] = false;
                    }
                }
            }
        }

        List<String> result = new ArrayList<>();
        for (int idx : coverIndices) {
            result.add(graph.vertices.get(idx));
        }

        // Verify that all edges in original graph are covered
        boolean allCovered = true;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (graph.adj[i][j]) {
                    if (!coverIndices.contains(i) && !coverIndices.contains(j)) {
                        allCovered = false;
                    }
                }
            }
        }

        return new VertexCoverResult(result, result.size(), false, allCovered);
    }
}
