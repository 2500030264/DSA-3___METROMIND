package algorithms;

import java.util.*;

/**
 * Course Outcome 4 (CO4) - Network Flow.
 * Provides authentic implementations of:
 * A. Ford-Fulkerson (DFS-based augmenting paths)
 * B. Edmonds-Karp (BFS-based shortest augmenting paths, O(V * E^2))
 * C. Dinic's Algorithm (Level graph BFS + Blocking flow DFS with dead-end pruning)
 * D. Bipartite Matching (Compatible Vehicle-to-Service assignments)
 * E. Minimum Cut (Residual graph reachability S-T cut with capacity verification)
 */
public class NetworkFlow {

    public static class FlowEdge {
        public final int u;
        public final int v;
        public final int capacity;
        public int flow;
        public FlowEdge rev;

        public FlowEdge(int u, int v, int capacity) {
            this.u = u;
            this.v = v;
            this.capacity = capacity;
            this.flow = 0;
        }

        public int residualCapacity() {
            return capacity - flow;
        }
    }

    public static class FlowNetwork {
        public final int n;
        public final List<List<FlowEdge>> adj;
        public final List<FlowEdge> allEdges;
        public final Map<String, Integer> nameToIndex;
        public final List<String> indexToName;

        public FlowNetwork(int n) {
            this.n = n;
            this.adj = new ArrayList<>();
            for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
            this.allEdges = new ArrayList<>();
            this.nameToIndex = new HashMap<>();
            this.indexToName = new ArrayList<>();
        }

        public void addEdge(int u, int v, int cap) {
            FlowEdge forward = new FlowEdge(u, v, cap);
            FlowEdge backward = new FlowEdge(v, u, 0);
            forward.rev = backward;
            backward.rev = forward;
            adj.get(u).add(forward);
            adj.get(v).add(backward);
            allEdges.add(forward);
        }

        public void registerNode(String name, int idx) {
            nameToIndex.put(name, idx);
            while (indexToName.size() <= idx) indexToName.add("");
            indexToName.set(idx, name);
        }

        public void resetFlows() {
            for (FlowEdge e : allEdges) {
                e.flow = 0;
                if (e.rev != null) e.rev.flow = 0;
            }
        }
    }

    // ==========================================
    // A. Ford-Fulkerson Algorithm (DFS)
    // ==========================================

    public static class FlowResult {
        public final int maxFlow;
        public final List<String> augmentingPaths;
        public final String algorithmName;
        public final String complexity;

        public FlowResult(int maxFlow, List<String> augmentingPaths, String algorithmName, String complexity) {
            this.maxFlow = maxFlow;
            this.augmentingPaths = augmentingPaths;
            this.algorithmName = algorithmName;
            this.complexity = complexity;
        }
    }

    public static FlowResult fordFulkerson(FlowNetwork net, int s, int t) {
        net.resetFlows();
        int maxFlow = 0;
        List<String> paths = new ArrayList<>();

        while (true) {
            boolean[] visited = new boolean[net.n];
            List<FlowEdge> currentPath = new ArrayList<>();
            int bottleneck = dfsAugment(net, s, t, visited, Integer.MAX_VALUE, currentPath);
            if (bottleneck <= 0) break;

            maxFlow += bottleneck;
            StringBuilder sb = new StringBuilder();
            sb.append(net.indexToName.isEmpty() ? s : net.indexToName.get(s));
            for (FlowEdge e : currentPath) {
                sb.append(" -> ").append(net.indexToName.isEmpty() ? e.v : net.indexToName.get(e.v));
            }
            sb.append(" (Pushed: ").append(bottleneck).append(")");
            paths.add(sb.toString());
        }

        return new FlowResult(maxFlow, paths, "Ford-Fulkerson (DFS)", "O(E * MaxFlow)");
    }

    private static int dfsAugment(FlowNetwork net, int u, int t, boolean[] visited, int minCap, List<FlowEdge> currentPath) {
        if (u == t) return minCap;
        visited[u] = true;

        for (FlowEdge e : net.adj.get(u)) {
            if (!visited[e.v] && e.residualCapacity() > 0) {
                currentPath.add(e);
                int pushed = dfsAugment(net, e.v, t, visited, Math.min(minCap, e.residualCapacity()), currentPath);
                if (pushed > 0) {
                    e.flow += pushed;
                    e.rev.flow -= pushed;
                    return pushed;
                }
                currentPath.remove(currentPath.size() - 1);
            }
        }
        return 0;
    }

    // ==========================================
    // B. Edmonds-Karp Algorithm (BFS)
    // ==========================================

    public static FlowResult edmondsKarp(FlowNetwork net, int s, int t) {
        net.resetFlows();
        int maxFlow = 0;
        List<String> paths = new ArrayList<>();

        while (true) {
            FlowEdge[] parentEdge = new FlowEdge[net.n];
            Queue<Integer> queue = new LinkedList<>();
            queue.add(s);

            while (!queue.isEmpty() && parentEdge[t] == null) {
                int u = queue.poll();
                for (FlowEdge e : net.adj.get(u)) {
                    if (e.v != s && parentEdge[e.v] == null && e.residualCapacity() > 0) {
                        parentEdge[e.v] = e;
                        queue.add(e.v);
                    }
                }
            }

            if (parentEdge[t] == null) break;

            // Find bottleneck capacity along BFS path
            int bottleneck = Integer.MAX_VALUE;
            for (FlowEdge e = parentEdge[t]; e != null; e = parentEdge[e.u]) {
                bottleneck = Math.min(bottleneck, e.residualCapacity());
            }

            // Apply augmentation
            List<FlowEdge> augPath = new ArrayList<>();
            for (FlowEdge e = parentEdge[t]; e != null; e = parentEdge[e.u]) {
                e.flow += bottleneck;
                e.rev.flow -= bottleneck;
                augPath.add(e);
            }
            Collections.reverse(augPath);

            maxFlow += bottleneck;
            StringBuilder sb = new StringBuilder();
            sb.append(net.indexToName.isEmpty() ? s : net.indexToName.get(s));
            for (FlowEdge e : augPath) {
                sb.append(" -> ").append(net.indexToName.isEmpty() ? e.v : net.indexToName.get(e.v));
            }
            sb.append(" (Bottleneck: ").append(bottleneck).append(")");
            paths.add(sb.toString());
        }

        return new FlowResult(maxFlow, paths, "Edmonds-Karp (BFS)", "O(V * E^2)");
    }

    // ==========================================
    // C. Dinic's Algorithm
    // ==========================================

    public static FlowResult dinicsAlgorithm(FlowNetwork net, int s, int t) {
        net.resetFlows();
        int maxFlow = 0;
        int[] level = new int[net.n];
        int[] ptr = new int[net.n];
        int phase = 1;
        List<String> phases = new ArrayList<>();

        while (dinicBfs(net, s, t, level)) {
            Arrays.fill(ptr, 0);
            int pushedInPhase = 0;
            while (true) {
                int pushed = dinicDfs(net, s, t, Integer.MAX_VALUE, level, ptr);
                if (pushed <= 0) break;
                pushedInPhase += pushed;
            }
            maxFlow += pushedInPhase;
            phases.add(String.format("Level Graph Phase %d: Pushed %d units of blocking flow", phase++, pushedInPhase));
        }

        return new FlowResult(maxFlow, phases, "Dinic's Algorithm (Level Graph + Blocking Flow)", "O(V^2 * E)");
    }

    private static boolean dinicBfs(FlowNetwork net, int s, int t, int[] level) {
        Arrays.fill(level, -1);
        level[s] = 0;
        Queue<Integer> queue = new LinkedList<>();
        queue.add(s);

        while (!queue.isEmpty()) {
            int u = queue.poll();
            for (FlowEdge e : net.adj.get(u)) {
                if (e.residualCapacity() > 0 && level[e.v] < 0) {
                    level[e.v] = level[u] + 1;
                    queue.add(e.v);
                }
            }
        }
        return level[t] >= 0;
    }

    private static int dinicDfs(FlowNetwork net, int u, int t, int pushed, int[] level, int[] ptr) {
        if (pushed == 0 || u == t) return pushed;

        for (; ptr[u] < net.adj.get(u).size(); ptr[u]++) {
            FlowEdge e = net.adj.get(u).get(ptr[u]);
            if (level[e.v] == level[u] + 1 && e.residualCapacity() > 0) {
                int tr = dinicDfs(net, e.v, t, Math.min(pushed, e.residualCapacity()), level, ptr);
                if (tr > 0) {
                    e.flow += tr;
                    e.rev.flow -= tr;
                    return tr;
                }
            }
        }
        return 0;
    }

    // ==========================================
    // D. Bipartite Matching (Vehicles to Services)
    // ==========================================

    public static class BipartiteMatchResult {
        public final Map<String, String> vehicleToService; // Vehicle -> Service
        public final List<String> unassignedVehicles;
        public final List<String> unfilledServices;
        public final int matchCount;

        public BipartiteMatchResult(Map<String, String> vehicleToService, List<String> unassignedVehicles,
                                    List<String> unfilledServices, int matchCount) {
            this.vehicleToService = vehicleToService;
            this.unassignedVehicles = unassignedVehicles;
            this.unfilledServices = unfilledServices;
            this.matchCount = matchCount;
        }
    }

    public static BipartiteMatchResult computeBipartiteMatching(List<String> vehicleIds, List<String> serviceIds,
                                                               Map<String, List<String>> compatibilityMap) {
        int vCount = vehicleIds.size();
        int sCount = serviceIds.size();
        int totalNodes = 1 + vCount + sCount + 1;
        int source = 0;
        int sink = totalNodes - 1;

        FlowNetwork net = new FlowNetwork(totalNodes);
        net.registerNode("Source", source);
        net.registerNode("Sink", sink);

        for (int i = 0; i < vCount; i++) {
            net.registerNode("Veh:" + vehicleIds.get(i), 1 + i);
            net.addEdge(source, 1 + i, 1);
        }

        for (int j = 0; j < sCount; j++) {
            net.registerNode("Serv:" + serviceIds.get(j), 1 + vCount + j);
            net.addEdge(1 + vCount + j, sink, 1);
        }

        for (int i = 0; i < vCount; i++) {
            String vId = vehicleIds.get(i);
            List<String> compatibleServices = compatibilityMap.getOrDefault(vId, Collections.emptyList());
            for (int j = 0; j < sCount; j++) {
                String sId = serviceIds.get(j);
                if (compatibleServices.contains(sId)) {
                    net.addEdge(1 + i, 1 + vCount + j, 1);
                }
            }
        }

        edmondsKarp(net, source, sink);

        Map<String, String> assignments = new LinkedHashMap<>();
        Set<String> matchedVehicles = new HashSet<>();
        Set<String> matchedServices = new HashSet<>();

        for (FlowEdge e : net.allEdges) {
            if (e.u >= 1 && e.u <= vCount && e.v > vCount && e.v < sink && e.flow == 1) {
                String vId = vehicleIds.get(e.u - 1);
                String sId = serviceIds.get(e.v - 1 - vCount);
                assignments.put(vId, sId);
                matchedVehicles.add(vId);
                matchedServices.add(sId);
            }
        }

        List<String> unassignedVehs = new ArrayList<>();
        for (String v : vehicleIds) {
            if (!matchedVehicles.contains(v)) unassignedVehs.add(v);
        }

        List<String> unfilledServs = new ArrayList<>();
        for (String s : serviceIds) {
            if (!matchedServices.contains(s)) unfilledServs.add(s);
        }

        return new BipartiteMatchResult(assignments, unassignedVehs, unfilledServs, assignments.size());
    }

    // ==========================================
    // E. Minimum Cut Algorithm
    // ==========================================

    public static class MinCutResult {
        public final Set<Integer> sourcePartition;
        public final Set<Integer> sinkPartition;
        public final List<FlowEdge> cutEdges;
        public final int cutCapacity;
        public final int maxFlow;
        public final boolean verifiesMaxFlowMinCut;

        public MinCutResult(Set<Integer> sourcePartition, Set<Integer> sinkPartition,
                            List<FlowEdge> cutEdges, int cutCapacity, int maxFlow) {
            this.sourcePartition = sourcePartition;
            this.sinkPartition = sinkPartition;
            this.cutEdges = cutEdges;
            this.cutCapacity = cutCapacity;
            this.maxFlow = maxFlow;
            this.verifiesMaxFlowMinCut = (cutCapacity == maxFlow);
        }
    }

    public static MinCutResult computeMinimumCut(FlowNetwork net, int s, int t) {
        // Run Edmonds-Karp to compute max flow
        FlowResult flowRes = edmondsKarp(net, s, t);

        // Find all vertices reachable from source in residual graph
        Set<Integer> sPartition = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();
        boolean[] visited = new boolean[net.n];

        visited[s] = true;
        queue.add(s);
        sPartition.add(s);

        while (!queue.isEmpty()) {
            int u = queue.poll();
            for (FlowEdge e : net.adj.get(u)) {
                if (!visited[e.v] && e.residualCapacity() > 0) {
                    visited[e.v] = true;
                    sPartition.add(e.v);
                    queue.add(e.v);
                }
            }
        }

        Set<Integer> tPartition = new HashSet<>();
        for (int i = 0; i < net.n; i++) {
            if (!sPartition.contains(i)) {
                tPartition.add(i);
            }
        }

        // Cut edges are original edges going from sPartition to tPartition
        List<FlowEdge> cutEdges = new ArrayList<>();
        int cutCapacity = 0;

        for (FlowEdge e : net.allEdges) {
            if (sPartition.contains(e.u) && tPartition.contains(e.v)) {
                cutEdges.add(e);
                cutCapacity += e.capacity;
            }
        }

        return new MinCutResult(sPartition, tPartition, cutEdges, cutCapacity, flowRes.maxFlow);
    }
}
