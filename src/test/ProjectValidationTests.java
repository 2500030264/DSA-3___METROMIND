package test;

import algorithms.*;
import java.util.*;

/**
 * Section 18 - Project Validation Tests.
 * Runs comprehensive automated unit and verification test suites across
 * all CO1, CO2, CO3, CO4, CO5, and CO6 algorithms and data operations.
 */
public class ProjectValidationTests {

    public static class TestResult {
        public final String testName;
        public final String outcome; // "PASS" or "FAIL"
        public final String details;

        public TestResult(String testName, boolean passed, String details) {
            this.testName = testName;
            this.outcome = passed ? "PASS" : "FAIL";
            this.details = details;
        }
    }

    public static List<TestResult> runAllTests() {
        List<TestResult> results = new ArrayList<>();

        // ==========================================
        // CO1 Tests: String Algorithms
        // ==========================================
        // 1. KMP Normal & Multiple Matches
        String kmpText = "METROMIND_METRO_STATION_METRO";
        String kmpPat = "METRO";
        StringAlgorithms.KmpResult kmpRes = StringAlgorithms.kmpSearch(kmpText, kmpPat);
        boolean kmpPass = kmpRes.matchIndices.size() == 3 && kmpRes.matchIndices.equals(Arrays.asList(0, 10, 24));
        results.add(new TestResult("CO1.1 - KMP Multiple Matches", kmpPass,
                "Expected matches at [0, 10, 24], Found: " + kmpRes.matchIndices));

        // 2. KMP No Match & Boundary
        StringAlgorithms.KmpResult kmpNoMatch = StringAlgorithms.kmpSearch("VIJAYAWADA", "DELHI");
        results.add(new TestResult("CO1.2 - KMP No Match Boundary", kmpNoMatch.matchIndices.isEmpty(),
                "Found matches: " + kmpNoMatch.matchIndices.size()));

        // 3. Z-Algorithm Verification
        String zText = "GUNTUR_CENTRAL_GUNTUR";
        StringAlgorithms.ZResult zRes = StringAlgorithms.zSearch(zText, "GUNTUR");
        boolean zPass = zRes.matchIndices.size() == 2 && zRes.matchIndices.equals(Arrays.asList(0, 15));
        results.add(new TestResult("CO1.3 - Z-Algorithm Pattern Matches", zPass,
                "Expected matches at [0, 15], Found: " + zRes.matchIndices));

        // 4. Rabin-Karp Rolling Hash & Verification
        String rkText = "TK101_TK102_TK101_TK103";
        StringAlgorithms.RabinKarpResult rkRes = StringAlgorithms.rabinKarpSearch(rkText, "TK101");
        boolean rkPass = rkRes.verifiedMatches == 2 && rkRes.matchIndices.equals(Arrays.asList(0, 12));
        results.add(new TestResult("CO1.4 - Rabin-Karp Rolling Hash Matches", rkPass,
                "Expected 2 matches, verified: " + rkRes.verifiedMatches));

        // 5. Aho-Corasick Multi-Pattern Alert Detection
        StringAlgorithms.AhoCorasickTrie acTrie = new StringAlgorithms.AhoCorasickTrie();
        acTrie.insert("delayed");
        acTrie.insert("overcrowded");
        acTrie.insert("disruption");
        String announcement = "Route RT002 is delayed due to passenger overcrowding and service disruption";
        List<StringAlgorithms.AhoCorasickMatch> acMatches = acTrie.search(announcement);
        boolean acPass = acMatches.size() >= 2;
        results.add(new TestResult("CO1.5 - Aho-Corasick Alert Keyword Detection", acPass,
                "Detected keywords count: " + acMatches.size()));

        // ==========================================
        // CO2 Tests: Suffix Structures
        // ==========================================
        // 6. Suffix Array SA-IS Sortedness Verification
        String saText = "VIJAYAWADA";
        int[] saisSa = SuffixStructures.buildSuffixArraySAIS(saText);
        boolean saSorted = true;
        for (int i = 0; i < saisSa.length - 1; i++) {
            String suf1 = saText.substring(saisSa[i]);
            String suf2 = saText.substring(saisSa[i + 1]);
            if (suf1.compareTo(suf2) > 0) {
                saSorted = false;
                break;
            }
        }
        results.add(new TestResult("CO2.1 - SA-IS Suffix Array Sortedness Verification", saSorted,
                "Verified all suffixes lexicographically ordered. Length: " + saisSa.length));

        // 7. Suffix Array Binary Search
        List<Integer> saMatches = SuffixStructures.searchSuffixArray(saText, saisSa, "AWA");
        boolean saSearchPass = saMatches.size() == 1 && saMatches.get(0) == 5;
        results.add(new TestResult("CO2.2 - Suffix Array Binary Search Pattern Query", saSearchPass,
                "Found match index: " + saMatches + " (Expected [5])"));

        // 8. Kasai LCP Array Construction
        int[] lcp = SuffixStructures.buildLcpKasai(saText, saisSa);
        boolean lcpPass = lcp.length == saText.length() && lcp[0] == 0;
        results.add(new TestResult("CO2.3 - Kasai LCP Array Computation", lcpPass,
                "LCP array length: " + lcp.length));

        // 9. Suffix Automaton Substring Queries
        SuffixStructures.SuffixAutomaton sam = new SuffixStructures.SuffixAutomaton("AMARAVATI_METRO");
        boolean samPass = sam.containsSubstring("MARA") && sam.containsSubstring("METRO") && !sam.containsSubstring("GUNTUR");
        results.add(new TestResult("CO2.4 - Suffix Automaton Substring Existence", samPass,
                "States created: " + sam.getStateCount() + ", distinct substrings: " + sam.countDistinctSubstrings()));

        // ==========================================
        // CO3 Tests: Dynamic Programming
        // ==========================================
        // 10. Levenshtein Distance
        int levDist = DynamicProgramming.levenshteinDistance("Vijaywada", "Vijayawada");
        results.add(new TestResult("CO3.1 - Levenshtein Distance Misspelling Check", levDist == 1,
                "Calculated distance: " + levDist + " (Expected 1)"));

        // 11. Damerau-Levenshtein Distance Transposition
        int damDist = DynamicProgramming.damerauLevenshteinDistance("Gnutur", "Guntur");
        results.add(new TestResult("CO3.2 - Damerau-Levenshtein Transposition Handling", damDist == 1,
                "Calculated distance: " + damDist + " (Expected 1 for transposition)"));

        // 12. Bitmask DP TSP Multi-Destination Tour
        List<String> stations = Arrays.asList("S0", "S1", "S2", "S3");
        double[][] distMatrix = {
            {0, 10, 15, 20},
            {10, 0, 35, 25},
            {15, 35, 0, 30},
            {20, 25, 30, 0}
        };
        DynamicProgramming.BitmaskTspResult tspRes = DynamicProgramming.solveBitmaskTsp(stations, distMatrix, true);
        boolean tspPass = tspRes.isTourComplete && Math.abs(tspRes.totalCost - 80.0) < 1e-6;
        results.add(new TestResult("CO3.3 - Bitmask DP TSP Optimal Tour", tspPass,
                "Calculated tour cost: " + tspRes.totalCost + ", Tour: " + tspRes.stationTour));

        // 13. Matrix-Chain Multiplication
        int[] dims = {10, 20, 30, 40, 30};
        DynamicProgramming.MatrixChainResult mcmRes = DynamicProgramming.solveMatrixChain(dims);
        boolean mcmPass = mcmRes.minMultiplications == 30000;
        results.add(new TestResult("CO3.4 - Matrix-Chain Multiplication Optimization", mcmPass,
                "Minimum scalar multiplications: " + mcmRes.minMultiplications + ", Paren: " + mcmRes.optimalParenthesization));

        // 14. Optimal Binary Search Tree (OBST)
        String[] keys = {"Amaravati", "Guntur", "Tenali", "Vijayawada"};
        double[] freqs = {4, 2, 1, 3};
        DynamicProgramming.ObstResult obstRes = DynamicProgramming.solveObst(keys, freqs);
        results.add(new TestResult("CO3.5 - Optimal Binary Search Tree Construction", obstRes.minSearchCost > 0,
                "Calculated expected search cost: " + String.format("%.2f", obstRes.minSearchCost)));

        // ==========================================
        // CO4 Tests: Network Flow
        // ==========================================
        // 15. Edmonds-Karp & Ford-Fulkerson Max Flow
        NetworkFlow.FlowNetwork net = new NetworkFlow.FlowNetwork(6);
        net.addEdge(0, 1, 10);
        net.addEdge(0, 2, 10);
        net.addEdge(1, 2, 2);
        net.addEdge(1, 3, 4);
        net.addEdge(1, 4, 8);
        net.addEdge(2, 4, 9);
        net.addEdge(3, 5, 10);
        net.addEdge(4, 5, 10);

        NetworkFlow.FlowResult ekRes = NetworkFlow.edmondsKarp(net, 0, 5);
        results.add(new TestResult("CO4.1 - Edmonds-Karp Maximum Flow", ekRes.maxFlow == 14,
                "Calculated max flow: " + ekRes.maxFlow + " (Expected 14)"));

        // 16. Dinic's Algorithm Max Flow
        NetworkFlow.FlowResult dinicRes = NetworkFlow.dinicsAlgorithm(net, 0, 5);
        results.add(new TestResult("CO4.2 - Dinic's Algorithm Blocking Flow", dinicRes.maxFlow == 14,
                "Calculated max flow: " + dinicRes.maxFlow + " (Expected 14)"));

        // 17. Min-Cut Theorem Verification (Cut Capacity == Max Flow)
        NetworkFlow.MinCutResult minCut = NetworkFlow.computeMinimumCut(net, 0, 5);
        boolean cutPass = minCut.verifiesMaxFlowMinCut && minCut.cutCapacity == 14;
        results.add(new TestResult("CO4.3 - Minimum Cut Theorem Verification (Cut == MaxFlow)", cutPass,
                "Cut capacity: " + minCut.cutCapacity + ", Max Flow: " + minCut.maxFlow));

        // 18. Bipartite Matching
        List<String> vehs = Arrays.asList("V1", "V2", "V3");
        List<String> servs = Arrays.asList("S1", "S2", "S3");
        Map<String, List<String>> compat = new HashMap<>();
        compat.put("V1", Arrays.asList("S1", "S2"));
        compat.put("V2", Collections.singletonList("S1"));
        compat.put("V3", Arrays.asList("S2", "S3"));

        NetworkFlow.BipartiteMatchResult bMatch = NetworkFlow.computeBipartiteMatching(vehs, servs, compat);
        boolean bMatchPass = bMatch.matchCount == 3 && bMatch.unassignedVehicles.isEmpty() && bMatch.unfilledServices.isEmpty();
        results.add(new TestResult("CO4.4 - Bipartite Vehicle-to-Service Matching", bMatchPass,
                "Total matches: " + bMatch.matchCount + ", Assignments: " + bMatch.vehicleToService));

        // ==========================================
        // CO5 Tests: NP-Completeness & Approximation
        // ==========================================
        // 19. SAT DPLL Solver
        NPConstraints.CnfFormula formula = new NPConstraints.CnfFormula(Arrays.asList("v1_s1", "v2_s1"));
        formula.addClause("v1_s1", "v2_s1");        // At least one assigned
        formula.addClause("!v1_s1", "!v2_s1");      // At most one assigned (conflict)
        NPConstraints.SatResult satRes = NPConstraints.solveSat(formula);
        boolean satPass = satRes.isSatisfiable && (satRes.model.get("v1_s1") ^ satRes.model.get("v2_s1"));
        results.add(new TestResult("CO5.1 - DPLL SAT Solver Scheduling Constraints", satPass,
                "Satisfiable: " + satRes.isSatisfiable + ", Model: " + satRes.model));

        // 20. Maximum Clique (Bron-Kerbosch)
        NPConstraints.Graph cliqueGraph = new NPConstraints.Graph(Arrays.asList("A", "B", "C", "D"));
        cliqueGraph.addEdge("A", "B");
        cliqueGraph.addEdge("B", "C");
        cliqueGraph.addEdge("C", "A");
        cliqueGraph.addEdge("C", "D");
        List<String> maxClique = NPConstraints.findMaximumClique(cliqueGraph);
        boolean cliquePass = maxClique.size() == 3 && maxClique.containsAll(Arrays.asList("A", "B", "C"));
        results.add(new TestResult("CO5.2 - Maximum Clique (Bron-Kerbosch)", cliquePass,
                "Found clique: " + maxClique));

        // 21. Maximum Independent Set
        List<String> indSet = NPConstraints.findMaximumIndependentSet(cliqueGraph);
        boolean indPass = indSet.size() == 2;
        results.add(new TestResult("CO5.3 - Maximum Independent Set", indPass,
                "Found independent set: " + indSet));

        // 22. Vertex Cover Exact vs 2-Approximation
        NPConstraints.VertexCoverResult exactVc = NPConstraints.findExactMinimumVertexCover(cliqueGraph);
        NPConstraints.VertexCoverResult approxVc = NPConstraints.findVertexCover2Approx(cliqueGraph);
        boolean vcPass = exactVc.allEdgesCovered && approxVc.allEdgesCovered && approxVc.size <= 2 * exactVc.size;
        results.add(new TestResult("CO5.4 - Vertex Cover 2-Approximation Verification", vcPass,
                "Exact size: " + exactVc.size + ", Approx size: " + approxVc.size + " (<= 2 * OPT guarantee verified)"));

        // ==========================================
        // CO6 Tests: Randomized and Parallel Algorithms
        // ==========================================
        // 23. Randomized QuickSort
        List<Integer> numbers = new ArrayList<>(Arrays.asList(150, 45, 210, 85, 30, 180, 95, 120));
        ParallelAndRandomized.QuickSortStats qsStats = new ParallelAndRandomized.QuickSortStats();
        ParallelAndRandomized.randomizedQuickSort(numbers, Integer::compare, true, qsStats);
        boolean qsPass = true;
        for (int i = 0; i < numbers.size() - 1; i++) {
            if (numbers.get(i) > numbers.get(i + 1)) qsPass = false;
        }
        results.add(new TestResult("CO6.1 - Randomized QuickSort Sorting Correctness", qsPass,
                "Sorted array: " + numbers + ", Comparisons: " + qsStats.comparisons));

        // 24. Reservoir Sampling
        List<String> stream = Arrays.asList("E1", "E2", "E3", "E4", "E5", "E6", "E7", "E8", "E9", "E10");
        ParallelAndRandomized.ReservoirResult<String> resSamp = ParallelAndRandomized.reservoirSample(stream.iterator(), 3);
        boolean resPass = resSamp.sample.size() == 3 && resSamp.totalStreamItems == 10;
        results.add(new TestResult("CO6.2 - Reservoir Sampling Uniform Stream Selection", resPass,
                "Sample: " + resSamp.sample + ", Stream size: " + resSamp.totalStreamItems));

        // 25. Miller-Rabin Primality Test
        ParallelAndRandomized.PrimalityResult pPrime = ParallelAndRandomized.millerRabinTest(1000000007L, 10);
        ParallelAndRandomized.PrimalityResult pComp = ParallelAndRandomized.millerRabinTest(1000000005L, 10);
        boolean mrPass = pPrime.isProbablyPrime && !pComp.isProbablyPrime;
        results.add(new TestResult("CO6.3 - Miller-Rabin Probabilistic Primality Test", mrPass,
                "1000000007 isPrime: " + pPrime.isProbablyPrime + ", 1000000005 isPrime: " + pComp.isProbablyPrime));

        // 26. Blelloch Parallel Scan (Exclusive Prefix Sum)
        int[] scanInput = {3, 1, 7, 0, 4, 1, 6, 3};
        ParallelAndRandomized.BlellochScanResult scanRes = ParallelAndRandomized.blellochExclusiveScan(scanInput);
        int[] expectedExclusive = {0, 3, 4, 11, 11, 15, 16, 22};
        boolean scanPass = Arrays.equals(scanRes.exclusivePrefixSum, expectedExclusive) && scanRes.totalSum == 25;
        results.add(new TestResult("CO6.4 - Blelloch Parallel Scan Exclusive Prefix Sum", scanPass,
                "Result: " + Arrays.toString(scanRes.exclusivePrefixSum) + ", Total Sum: " + scanRes.totalSum));

        // 27. Parallel Reduce vs Sequential Verification
        int[] redData = new int[500];
        Arrays.fill(redData, 10);
        ParallelAndRandomized.ParallelReduceResult redRes = ParallelAndRandomized.parallelReduceSum(redData);
        boolean redPass = redRes.matchesSequential && redRes.parallelTotal == 5000;
        results.add(new TestResult("CO6.5 - Parallel Reduce vs Sequential Result Parity", redPass,
                "Parallel: " + redRes.parallelTotal + ", Sequential: " + redRes.sequentialTotal + ", Threads: " + redRes.threadCount));

        // 28. Brent's Theorem Theoretical Bound
        ParallelAndRandomized.BrentsTheoremResult brent = new ParallelAndRandomized.BrentsTheoremResult(1000, 20, 8);
        boolean brentPass = brent.boundTp > 0 && brent.theoreticalSpeedup > 1.0;
        results.add(new TestResult("CO6.6 - Brent's Theorem Bound Calculator", brentPass,
                String.format("Work: %.0f, Span: %.0f, Processors: %d -> T_P <= %.2f, Speedup: %.2fx",
                        brent.workT1, brent.spanTInf, brent.processorsP, brent.boundTp, brent.theoreticalSpeedup)));

        return results;
    }

    public static void printReport() {
        System.out.println("\n" + "=".repeat(78));
        System.out.println("          METROMIND - AUTOMATED PROJECT VALIDATION TEST REPORT");
        System.out.println("=".repeat(78));
        System.out.printf("%-40s | %-6s | %s%n", "TEST IDENTIFIER", "STATUS", "DETAILS");
        System.out.println("-".repeat(78));

        List<TestResult> tests = runAllTests();
        int passCount = 0;
        for (TestResult tr : tests) {
            if ("PASS".equals(tr.outcome)) passCount++;
            System.out.printf("%-40s | [%s] | %s%n", tr.testName, tr.outcome, tr.details);
        }
        System.out.println("=".repeat(78));
        System.out.printf("TOTAL TESTS: %d | PASSED: %d | FAILED: %d | SUCCESS RATE: %.1f%%%n",
                tests.size(), passCount, (tests.size() - passCount), ((double) passCount / tests.size()) * 100.0);
        System.out.println("=".repeat(78));
    }

    public static void main(String[] args) {
        printReport();
    }
}
