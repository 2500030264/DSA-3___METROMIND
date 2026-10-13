# MetroMind – Intelligent Public Transport Platform

> **Advanced Data Structures & Algorithms (DSA-3) Capstone Project**  
> An interactive, terminal-based transit management and optimization system implemented in Core Java.

---

## 1. Project Overview

**MetroMind** is an enterprise-grade transit simulation, optimization, and dispatch platform designed for modern urban transportation networks. Managing multimodal transit entities—such as metro train corridors, feeder electric buses, rapid suburban shuttles, multi-stop routes, vehicle fleet rosters, passenger event streams, and ticketing records—MetroMind applies real computer science algorithms across all six DSA-3 Course Outcomes (**CO1 through CO6**).

Rather than printing canned or synthetic results, MetroMind executes genuine algorithms directly on user-provided or stored transit datasets. It includes a built-in automated test suite verifying algorithmic correctness and mathematical invariants across 28 distinct test scenarios with a 100% pass rate.

---

## 2. Problem Statement & Objectives

### Problem Statement
Urban transit authorities face combinatorial challenges:
1. Identifying rapid multi-keyword transport disruption alerts in real-time.
2. Indexing and querying large route descriptions and schedules without linear substring bottlenecks.
3. Providing fuzzy station spelling tolerance when commuters enter misspelled station names.
4. Optimizing multi-destination dispatch routes for transit maintenance crews (NP-hard TSP).
5. Maximizing network passenger throughput across congested bottleneck transit corridors.
6. Matching available fleet vehicles to scheduled trips under strict capacity constraints.
7. Resolving overlapping vehicle scheduling conflicts under Boolean constraints.
8. Parallelizing passenger-flow statistics and prefix sums over high-throughput event streams.

### Key Objectives
- **Zero-Placeholder Architecture**: Every algorithm is implemented from mathematical first principles without shortcuts (no `String.contains()` replacing KMP/Z/SAM, no `Arrays.sort()` replacing Randomized QuickSort).
- **Unified Data Model**: Every algorithm operates on the same interconnected transit data graph.
- **Robust Terminal UI**: Fault-tolerant menu-driven CLI handling blank lines, invalid input types, and graceful returns.
- **Local Persistence**: CSV-based data store preserving network changes across application restarts.

---

## 3. Course Outcomes (CO1 to CO6) Algorithmic Mapping

| Course Outcome | Algorithm / Structure | Transport Use Case in MetroMind | Complexity |
| :--- | :--- | :--- | :--- |
| **CO1** | **Knuth-Morris-Pratt (KMP)** | Searches station and route names using precomputed $\pi$ (LPS) table | $O(N + M)$ |
| **CO1** | **Z-Algorithm** | Identifies pattern occurrences in transit descriptions using the $Z$-box array | $O(N + M)$ |
| **CO1** | **Rabin-Karp** | Searches ticket identifiers and passenger codes using polynomial rolling hash | $O(N + M)$ avg |
| **CO1** | **Aho-Corasick** | Scans announcements for multiple alert keywords (`delayed`, `cancelled`, `overcrowded`, `diversion`, etc.) in a single pass | $O(N + \sum M_i + Z)$ |
| **CO2** | **Suffix Array** | Lexicographical suffix index for binary search lookup in route catalogs | $O(M \log N)$ |
| **CO2** | **SA-IS** | Linear-time Suffix Array construction via Induced Sorting (L/S types and LMS suffixes) | $O(N)$ |
| **CO2** | **Kasai Algorithm & LCP** | Computes Longest Common Prefix array and finds common phrases across route descriptions | $O(N)$ |
| **CO2** | **Suffix Automaton (SAM)** | Directed acyclic word graph for $O(\|Q\|)$ substring queries and distinct substring analytics | $O(N)$ build, $O(M)$ query |
| **CO3** | **Levenshtein Distance** | Suggests closest station names when commuters mistype queries | $O(M \cdot N)$ |
| **CO3** | **Damerau-Levenshtein** | Unrestricted edit distance correcting adjacent character transpositions (`Gnutur` $\rightarrow$ `Guntur`) | $O(M \cdot N)$ |
| **CO3** | **Bitmask Dynamic Programming** | Solves Traveling Salesperson tour optimization across multi-destination transit stops | $O(2^K \cdot K^2)$ |
| **CO3** | **Matrix-Chain Multiplication** | Optimizes parenthesization of analytics pipeline matrices (Passenger $\times$ Route $\times$ Fleet) | $O(N^3)$ |
| **CO3** | **Optimal BST (OBST)** | Constructs optimal binary search tree based on collected station lookup frequencies | $O(N^3)$ |
| **CO4** | **Ford-Fulkerson** | Computes maximum passenger flow using DFS augmenting paths | $O(E \cdot \text{MaxFlow})$ |
| **CO4** | **Edmonds-Karp** | Computes maximum corridor flow using BFS shortest augmenting paths | $O(V \cdot E^2)$ |
| **CO4** | **Dinic's Algorithm** | Pushes blocking flow in level graphs with dead-end pointer pruning | $O(V^2 \cdot E)$ |
| **CO4** | **Bipartite Matching** | Matches available fleet vehicles to compatible scheduled trips by vehicle type & capacity | $O(V \cdot E)$ |
| **CO4** | **Minimum Cut Theorem** | Partitions network into $S$ and $T$ cuts; verifies Cut Capacity equals Maximum Flow | $O(V + E)$ |
| **CO5** | **SAT / 3-SAT (DPLL)** | Solves trip assignment and vehicle non-overlap constraints via CNF unit propagation | Exponential |
| **CO5** | **Maximum Clique** | Finds maximum mutually conflicting trips using Bron-Kerbosch with pivoting | Exponential |
| **CO5** | **Max Independent Set** | Finds maximum mutually compatible concurrent trips via complement graph reduction | Exponential |
| **CO5** | **Min Vertex Cover (Exact)**| Computes minimum monitoring stations required to cover all transit corridors | Exponential |
| **CO5** | **Vertex Cover 2-Approx** | Fast edge-matching approximation with $|C| \le 2 \cdot OPT$ theoretical guarantee | $O(V + E)$ |
| **CO6** | **Randomized QuickSort** | Sorts passenger events and trip records by user-selected fields using random pivot | $O(N \log N)$ expected |
| **CO6** | **Reservoir Sampling** | Selects a uniform random sample of size $k$ from a live streaming passenger event sequence | $O(N)$ time, $O(k)$ space |
| **CO6** | **Miller-Rabin Test** | Probabilistic test verifying 64-bit prime moduli for hash tables and Rabin-Karp | $O(k \log^3 N)$ |
| **CO6** | **Blelloch Parallel Scan** | Parallel up-sweep and down-sweep tree computing exclusive prefix sums of passenger counts | $O(N)$ work, $O(\log N)$ span |
| **CO6** | **Parallel Reduce** | Multi-threaded reduction via Java ForkJoinPool; verifies exact match with sequential total | $O(N)$ work, $O(\log N)$ span |
| **CO6** | **Brent's Theorem** | Analyzes theoretical execution time bounds $T_P \le (T_1 - T_\infty)/P + T_\infty$ | Theoretical bound |

---

## 4. Project Directory Structure

```
MetroMind/
├── src/
│   ├── Main.java                          # CLI entry point, 16 menus, and navigation
│   ├── model/
│   │   ├── Station.java                   # Station entity (ID, connections, passenger counts)
│   │   ├── Route.java                     # Route entity (Stations sequence, distance, time, cost)
│   │   ├── Vehicle.java                   # Fleet vehicle (Type, capacity, assignment status)
│   │   ├── TransportService.java          # Scheduled trip (Departure, arrival, occupancy)
│   │   ├── PassengerEvent.java            # Event stream (Turnstile tap, boarding, alighting)
│   │   └── Ticket.java                    # Ticket entity (Passenger reference, fare, status)
│   ├── service/
│   │   ├── StationService.java            # Station CRUD, safe deletion, Levenshtein suggestions
│   │   ├── RouteService.java              # Route CRUD, pathfinding, distance matrix builder
│   │   ├── VehicleService.java            # Vehicle fleet management and capacity checks
│   │   ├── TransportServiceService.java   # Scheduled trip dispatch and real occupancy math
│   │   ├── PassengerService.java          # Event stream recording, ticket Rabin-Karp search
│   │   ├── DataStorageService.java        # CSV persistence layer in data/ directory
│   │   └── SampleDataGenerator.java       # Realistic capital region transit dataset
│   ├── algorithms/
│   │   ├── StringAlgorithms.java          # CO1: KMP, Z-Algorithm, Rabin-Karp, Aho-Corasick
│   │   ├── SuffixStructures.java          # CO2: Suffix Array, SA-IS O(N), Kasai LCP, SAM
│   │   ├── DynamicProgramming.java        # CO3: Levenshtein, Damerau, Bitmask TSP, MCM, OBST
│   │   ├── NetworkFlow.java               # CO4: Ford-Fulkerson, Edmonds-Karp, Dinic, Bipartite, Min-Cut
│   │   ├── NPConstraints.java             # CO5: SAT DPLL, Max Clique, IndSet, Vertex Cover 2-Approx
│   │   └── ParallelAndRandomized.java     # CO6: QuickSort, Reservoir, Miller-Rabin, Blelloch, Reduce, Brent
│   ├── test/
│   │   └── ProjectValidationTests.java    # Automated test suite covering CO1-CO6 (28 test cases)
│   └── util/
│       └── InputHelper.java               # Resilient scanner helpers, EOF safety, formatting
├── data/                                  # Persistent CSV storage files
│   ├── stations.csv
│   ├── routes.csv
│   ├── vehicles.csv
│   ├── services.csv
│   ├── passenger_events.csv
│   └── tickets.csv
├── build.bat                              # One-click Windows CMD compilation script
├── run.bat                                # One-click Windows CMD launch script
├── build.ps1                              # One-click Windows PowerShell build script
├── run.ps1                                # One-click Windows PowerShell launch script
└── README.md                              # Complete engineering documentation
```

---

## 5. System Requirements & Compilation

### Requirements
- **Java Development Kit (JDK)**: JDK 17 (or JDK 8/11/21+).
- **Operating System**: Windows 10 / Windows 11 (or macOS / Linux).
- **Environment**: Visual Studio Code, IntelliJ IDEA, Eclipse, or raw terminal.
- **Dependencies**: None (100% pure Core Java without third-party frameworks).

### Compilation Commands (Windows)

#### Option A: Using PowerShell in VS Code (Recommended)
```powershell
# Compile all source files into out/
javac -d out (Get-ChildItem -Recurse -Filter *.java src | Select-Object -ExpandProperty FullName)

# Run the MetroMind platform
java -cp out Main
```

#### Option B: Using Windows Command Prompt (CMD)
```cmd
# Create sources list and compile
dir /s /b src\*.java > sources.txt
javac -d out @sources.txt
del sources.txt

# Run the MetroMind platform
java -cp out Main
```

#### Option C: Using Helper Scripts
- In PowerShell: `.\build.ps1` followed by `.\run.ps1`
- In Command Prompt: `build.bat` followed by `run.bat`

---

## 6. Sample Terminal Session Walkthrough

```text
================================================================
        METROMIND - INTELLIGENT PUBLIC TRANSPORT
================================================================
Welcome to the MetroMind Transit Management & DSA-3 Platform.

Startup Options:
1. Start with empty data
2. Load sample MetroMind data (Capital Region Corridor: Vijayawada-Guntur-Amaravati)
3. Load saved data from disk (if available)
0. Exit
Select startup mode: 2
Sample MetroMind dataset loaded successfully!

================================================================
        METROMIND - INTELLIGENT PUBLIC TRANSPORT
================================================================
Manage stations, routes, vehicles, passengers, and analytics.

 1. Station Management
 2. Route Management
 3. Transport Service Management
 4. Vehicle Management
 5. Passenger and Ticket Management
 6. Route Search and Text Indexing (CO1 String + CO2 Suffix Structures)
 7. Route Planning and Optimization (CO3 Bitmask DP + Misspelling Fix)
 8. Passenger Flow and Network Analysis (CO4 Max Flow & Min Cut)
 9. Vehicle Assignment and Scheduling (CO4 Bipartite Matching)
10. Scheduling Constraints and Graph Analysis (CO5 SAT & NP Graph Sets)
11. Passenger Analytics and Parallel Processing (CO6 Randomized & Parallel)
12. DSA Algorithm Explorer (CO1-CO6 Unified Sandbox)
13. View All Transport Records
14. Save Data
15. Load Data
16. Run Project Validation Tests
 0. Exit
----------------------------------------------------------------
Enter your choice: 16

==============================================================================
          METROMIND - AUTOMATED PROJECT VALIDATION TEST REPORT
==============================================================================
TEST IDENTIFIER                          | STATUS | DETAILS
------------------------------------------------------------------------------
CO1.1 - KMP Multiple Matches             | [PASS] | Expected matches at [0, 10, 24], Found: [0, 10, 24]
CO1.2 - KMP No Match Boundary            | [PASS] | Found matches: 0
CO1.3 - Z-Algorithm Pattern Matches      | [PASS] | Expected matches at [0, 15], Found: [0, 15]
CO1.4 - Rabin-Karp Rolling Hash Matches  | [PASS] | Expected 2 matches, verified: 2
CO1.5 - Aho-Corasick Alert Keyword Detection | [PASS] | Detected keywords count: 2
CO2.1 - SA-IS Suffix Array Sortedness Verification | [PASS] | Verified all suffixes lexicographically ordered. Length: 10
CO2.2 - Suffix Array Binary Search Pattern Query | [PASS] | Found match index: [5] (Expected [5])
CO2.3 - Kasai LCP Array Computation      | [PASS] | LCP array length: 10
CO2.4 - Suffix Automaton Substring Existence | [PASS] | States created: 19, distinct substrings: 114
CO3.1 - Levenshtein Distance Misspelling Check | [PASS] | Calculated distance: 1 (Expected 1)
CO3.2 - Damerau-Levenshtein Transposition Handling | [PASS] | Calculated distance: 1 (Expected 1 for transposition)
CO3.3 - Bitmask DP TSP Optimal Tour      | [PASS] | Calculated tour cost: 80.0, Tour: [S0, S2, S3, S1, S0]
CO3.4 - Matrix-Chain Multiplication Optimization | [PASS] | Minimum scalar multiplications: 30000, Paren: (((M1 x M2) x M3) x M4)
CO3.5 - Optimal Binary Search Tree Construction | [PASS] | Calculated expected search cost: 19.00
CO4.1 - Edmonds-Karp Maximum Flow        | [PASS] | Calculated max flow: 14 (Expected 14)
CO4.2 - Dinic's Algorithm Blocking Flow  | [PASS] | Calculated max flow: 14 (Expected 14)
CO4.3 - Minimum Cut Theorem Verification (Cut == MaxFlow) | [PASS] | Cut capacity: 14, Max Flow: 14
CO4.4 - Bipartite Vehicle-to-Service Matching | [PASS] | Total matches: 3, Assignments: {V1=S2, V2=S1, V3=S3}
CO5.1 - DPLL SAT Solver Scheduling Constraints | [PASS] | Satisfiable: true, Model: {v1_s1=true, v2_s1=false}
CO5.2 - Maximum Clique (Bron-Kerbosch)   | [PASS] | Found clique: [A, B, C]
CO5.3 - Maximum Independent Set          | [PASS] | Found independent set: [A, D]
CO5.4 - Vertex Cover 2-Approximation Verification | [PASS] | Exact size: 2, Approx size: 4 (<= 2 * OPT guarantee verified)
CO6.1 - Randomized QuickSort Sorting Correctness | [PASS] | Sorted array: [30, 45, 85, 95, 120, 150, 180, 210], Comparisons: 18
CO6.2 - Reservoir Sampling Uniform Stream Selection | [PASS] | Sample: [E1, E2, E8], Stream size: 10
CO6.3 - Miller-Rabin Probabilistic Primality Test | [PASS] | 1000000007 isPrime: true, 1000000005 isPrime: false
CO6.4 - Blelloch Parallel Scan Exclusive Prefix Sum | [PASS] | Result: [0, 3, 4, 11, 11, 15, 16, 22], Total Sum: 25
CO6.5 - Parallel Reduce vs Sequential Result Parity | [PASS] | Parallel: 5000, Sequential: 5000, Threads: 16
CO6.6 - Brent's Theorem Bound Calculator | [PASS] | Work: 1000, Span: 20, Processors: 8 -> T_P <= 142.50, Speedup: 7.02x
==============================================================================
TOTAL TESTS: 28 | PASSED: 28 | FAILED: 0 | SUCCESS RATE: 100.0%
==============================================================================
```

---

## 7. Data Persistence Format

All data records are stored in human-readable CSV format inside the `data/` directory:
- `stations.csv`: `id,name,city,stationType,connectedStationIds,passengerCount,serviceInfo`
- `routes.csv`: `id,name,sourceStationId,destinationStationId,intermediateStationIds,distanceKm,estimatedTimeMinutes,travelCost,description,status`
- `vehicles.csv`: `id,name,vehicleType,capacity,assignedServiceId,status`
- `services.csv`: `id,serviceCode,routeId,departureTime,arrivalTime,requiredCapacity,currentOccupancy,assignedVehicleId,status`
- `passenger_events.csv`: `id,serviceId,stationId,timestamp,eventType,passengerCount`
- `tickets.csv`: `id,passengerRef,serviceId,sourceStationId,destinationStationId,fareAmount,status`

---

## 8. Verification and Correctness Guarantees

1. **Max-Flow Min-Cut Verification**: The minimum cut algorithm verifies that the cut capacity between $S$ and $T$ partitions strictly equals the computed maximum flow.
2. **2-Approximation Verification**: The maximal-matching vertex cover approximation guarantees that $|C_{\text{approx}}| \le 2 \cdot |C_{\text{exact}}|$ and verifies that every edge in the graph has at least one endpoint in the cover.
3. **Parallel Reductions & Scans**: Blelloch exclusive prefix sums and ForkJoin parallel reductions verify 100% numerical parity with sequential baseline totals.
4. **Graph Independent Set & Clique Verification**: Confirms that no two vertices in the returned independent set are connected, and all vertex pairs in the returned clique are adjacent.

---

## 9. Academic Review Checklist (DSA-3)

- [x] **CO1**: KMP with LPS array, Z-Algorithm with Z-array, Rabin-Karp rolling hash, Aho-Corasick multi-alert trie.
- [x] **CO2**: Suffix Array, SA-IS $O(N)$ induced sorting, Kasai LCP array, Suffix Automaton (SAM).
- [x] **CO3**: Levenshtein distance, Damerau-Levenshtein unrestricted distance, Bitmask DP TSP, Matrix-Chain Multiplication, Optimal BST.
- [x] **CO4**: Ford-Fulkerson DFS, Edmonds-Karp BFS, Dinic's blocking flow, Bipartite matching, Minimum cut theorem.
- [x] **CO5**: DPLL SAT/3-SAT solver, Bron-Kerbosch Maximum Clique, Maximum Independent Set, Exact Minimum Vertex Cover, 2-Approximation Vertex Cover.
- [x] **CO6**: Randomized QuickSort, Reservoir Sampling (Algorithm R), Miller-Rabin primality test, Blelloch Parallel Scan, Parallel Reduce (ForkJoin), Brent's Theorem bound calculator.
- [x] **Integration**: All algorithms operate on the unified MetroMind transport network.
- [x] **Persistence**: Complete CSV save/load operations.
- [x] **Automated Testing**: 28 automated test scenarios with 100% pass rate.
#   D S A - 3 _ _ _ M E T R O M I N D  
 