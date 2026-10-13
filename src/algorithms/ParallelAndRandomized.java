package algorithms;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.*;

/**
 * Course Outcome 6 (CO6) - Randomized and Parallel Algorithms.
 * Provides authentic implementations of:
 * A. Randomized QuickSort with random pivot selection
 * B. Reservoir Sampling (Algorithm R) for streaming passenger events
 * C. Miller-Rabin Probabilistic Primality Test for hash table moduli
 * D. Blelloch Parallel Scan (Exclusive Prefix Sum via Up-Sweep & Down-Sweep)
 * E. Parallel Reduce via ForkJoinPool with sequential verification
 * F. Brent's Theorem Theoretical Bound Calculator
 */
public class ParallelAndRandomized {

    // ==========================================
    // A. Randomized QuickSort
    // ==========================================

    public static class QuickSortStats {
        public long comparisons = 0;
        public long swaps = 0;
    }

    public static <T> void randomizedQuickSort(List<T> list, Comparator<T> comp, boolean ascending, QuickSortStats stats) {
        if (list == null || list.size() <= 1) return;
        Comparator<T> effectiveComp = ascending ? comp : comp.reversed();
        Random random = new Random();
        quickSortHelper(list, 0, list.size() - 1, effectiveComp, random, stats);
    }

    private static <T> void quickSortHelper(List<T> list, int low, int high, Comparator<T> comp, Random random, QuickSortStats stats) {
        if (low < high) {
            int pivotIndex = partition(list, low, high, comp, random, stats);
            quickSortHelper(list, low, pivotIndex - 1, comp, random, stats);
            quickSortHelper(list, pivotIndex + 1, high, comp, random, stats);
        }
    }

    private static <T> int partition(List<T> list, int low, int high, Comparator<T> comp, Random random, QuickSortStats stats) {
        // Choose randomized pivot uniformly in [low, high]
        int randPivot = low + random.nextInt(high - low + 1);
        swap(list, randPivot, high, stats);

        T pivotValue = list.get(high);
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (stats != null) stats.comparisons++;
            if (comp.compare(list.get(j), pivotValue) <= 0) {
                i++;
                swap(list, i, j, stats);
            }
        }
        swap(list, i + 1, high, stats);
        return i + 1;
    }

    private static <T> void swap(List<T> list, int i, int j, QuickSortStats stats) {
        if (i == j) return;
        T temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
        if (stats != null) stats.swaps++;
    }

    // ==========================================
    // B. Reservoir Sampling (Algorithm R)
    // ==========================================

    public static class ReservoirResult<T> {
        public final List<T> sample;
        public final int totalStreamItems;
        public final int requestedSampleSize;

        public ReservoirResult(List<T> sample, int totalStreamItems, int requestedSampleSize) {
            this.sample = sample;
            this.totalStreamItems = totalStreamItems;
            this.requestedSampleSize = requestedSampleSize;
        }
    }

    public static <T> ReservoirResult<T> reservoirSample(Iterator<T> stream, int k) {
        List<T> reservoir = new ArrayList<>();
        if (k <= 0 || stream == null) {
            return new ReservoirResult<>(reservoir, 0, k);
        }

        Random rand = new Random();
        int count = 0;

        while (stream.hasNext()) {
            T item = stream.next();
            count++;
            if (reservoir.size() < k) {
                reservoir.add(item);
            } else {
                int j = rand.nextInt(count); // Uniform in [0, count - 1]
                if (j < k) {
                    reservoir.set(j, item);
                }
            }
        }
        return new ReservoirResult<>(reservoir, count, k);
    }

    // ==========================================
    // C. Miller-Rabin Primality Test
    // ==========================================

    public static class PrimalityResult {
        public final long candidate;
        public final boolean isProbablyPrime;
        public final int roundsTested;
        public final List<Long> witnessBases;
        public final String roleInMetroMind = "Verifies prime moduli for Rabin-Karp polynomial rolling hash and hash tables";

        public PrimalityResult(long candidate, boolean isProbablyPrime, int roundsTested, List<Long> witnessBases) {
            this.candidate = candidate;
            this.isProbablyPrime = isProbablyPrime;
            this.roundsTested = roundsTested;
            this.witnessBases = witnessBases;
        }
    }

    private static long modPow(long base, long exp, long mod) {
        BigInteger b = BigInteger.valueOf(base);
        BigInteger e = BigInteger.valueOf(exp);
        BigInteger m = BigInteger.valueOf(mod);
        return b.modPow(e, m).longValue();
    }

    public static PrimalityResult millerRabinTest(long n, int kRounds) {
        if (n <= 1) return new PrimalityResult(n, false, 0, Collections.emptyList());
        if (n == 2 || n == 3) return new PrimalityResult(n, true, 1, Collections.singletonList(2L));
        if (n % 2 == 0) return new PrimalityResult(n, false, 1, Collections.singletonList(2L));

        // Write n - 1 = d * 2^s with d odd
        long d = n - 1;
        int s = 0;
        while (d % 2 == 0) {
            d /= 2;
            s++;
        }

        // Deterministic bases for 64-bit integers
        long[] deterministicBases = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37};
        List<Long> testedBases = new ArrayList<>();
        int rounds = Math.min(kRounds, deterministicBases.length);

        for (int r = 0; r < rounds; r++) {
            long a = deterministicBases[r];
            if (a >= n) break;
            testedBases.add(a);

            long x = modPow(a, d, n);
            if (x == 1 || x == n - 1) continue;

            boolean composite = true;
            for (int i = 0; i < s - 1; i++) {
                x = modPow(x, 2, n);
                if (x == n - 1) {
                    composite = false;
                    break;
                }
            }
            if (composite) {
                return new PrimalityResult(n, false, r + 1, testedBases);
            }
        }
        return new PrimalityResult(n, true, testedBases.size(), testedBases);
    }

    // ==========================================
    // D. Blelloch Scan (Work-Efficient Exclusive Prefix Sum)
    // ==========================================

    public static class BlellochScanResult {
        public final int[] input;
        public final int[] exclusivePrefixSum;
        public final int totalSum;
        public final List<String> upSweepStages;
        public final List<String> downSweepStages;

        public BlellochScanResult(int[] input, int[] exclusivePrefixSum, int totalSum,
                                  List<String> upSweepStages, List<String> downSweepStages) {
            this.input = input;
            this.exclusivePrefixSum = exclusivePrefixSum;
            this.totalSum = totalSum;
            this.upSweepStages = upSweepStages;
            this.downSweepStages = downSweepStages;
        }
    }

    public static BlellochScanResult blellochExclusiveScan(int[] input) {
        if (input == null || input.length == 0) {
            return new BlellochScanResult(new int[0], new int[0], 0, Collections.emptyList(), Collections.emptyList());
        }

        int origLen = input.length;
        // Pad to next power of 2
        int m = 1;
        while (m < origLen) m <<= 1;

        int[] a = new int[m];
        System.arraycopy(input, 0, a, 0, origLen);

        List<String> upStages = new ArrayList<>();
        List<String> downStages = new ArrayList<>();

        // Stage 1: Up-Sweep (Reduce Phase)
        for (int step = 1; step < m; step <<= 1) {
            for (int i = 0; i < m; i += (step << 1)) {
                a[i + (step << 1) - 1] += a[i + step - 1];
            }
            upStages.add(String.format("Step %-2d: %s", step, Arrays.toString(a)));
        }

        int totalSum = a[m - 1];

        // Set root to 0
        a[m - 1] = 0;
        downStages.add(String.format("Root cleared to 0: %s", Arrays.toString(a)));

        // Stage 2: Down-Sweep Phase
        for (int step = m >> 1; step >= 1; step >>= 1) {
            for (int i = 0; i < m; i += (step << 1)) {
                int leftChildVal = a[i + step - 1];
                a[i + step - 1] = a[i + (step << 1) - 1];
                a[i + (step << 1) - 1] += leftChildVal;
            }
            downStages.add(String.format("Step %-2d: %s", step, Arrays.toString(a)));
        }

        int[] exclusivePrefix = new int[origLen];
        System.arraycopy(a, 0, exclusivePrefix, 0, origLen);

        return new BlellochScanResult(input, exclusivePrefix, totalSum, upStages, downStages);
    }

    // ==========================================
    // E. Parallel Reduce (ForkJoinPool)
    // ==========================================

    public static class ParallelReduceResult {
        public final long parallelTotal;
        public final long sequentialTotal;
        public final int threadCount;
        public final int recordCount;
        public final long parallelTimeNanos;
        public final long sequentialTimeNanos;
        public final boolean matchesSequential;

        public ParallelReduceResult(long parallelTotal, long sequentialTotal, int threadCount,
                                    int recordCount, long parallelTimeNanos, long sequentialTimeNanos) {
            this.parallelTotal = parallelTotal;
            this.sequentialTotal = sequentialTotal;
            this.threadCount = threadCount;
            this.recordCount = recordCount;
            this.parallelTimeNanos = parallelTimeNanos;
            this.sequentialTimeNanos = sequentialTimeNanos;
            this.matchesSequential = (parallelTotal == sequentialTotal);
        }
    }

    private static class SumTask extends RecursiveTask<Long> {
        private static final int THRESHOLD = 250;
        private final int[] data;
        private final int start;
        private final int end;

        public SumTask(int[] data, int start, int end) {
            this.data = data;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Long compute() {
            if (end - start <= THRESHOLD) {
                long sum = 0;
                for (int i = start; i < end; i++) {
                    sum += data[i];
                }
                return sum;
            } else {
                int mid = start + (end - start) / 2;
                SumTask left = new SumTask(data, start, mid);
                SumTask right = new SumTask(data, mid, end);
                left.fork();
                long rightResult = right.compute();
                long leftResult = left.join();
                return leftResult + rightResult;
            }
        }
    }

    public static ParallelReduceResult parallelReduceSum(int[] data) {
        if (data == null || data.length == 0) {
            return new ParallelReduceResult(0, 0, 1, 0, 0, 0);
        }

        int n = data.length;

        // Sequential calculation
        long tSeqStart = System.nanoTime();
        long seqSum = 0;
        for (int val : data) seqSum += val;
        long tSeqEnd = System.nanoTime();

        // Parallel calculation via ForkJoinPool
        int parallelism = Runtime.getRuntime().availableProcessors();
        ForkJoinPool pool = new ForkJoinPool(parallelism);
        long tParStart = System.nanoTime();
        long parSum = pool.invoke(new SumTask(data, 0, n));
        long tParEnd = System.nanoTime();
        pool.shutdown();

        return new ParallelReduceResult(parSum, seqSum, parallelism, n, (tParEnd - tParStart), (tSeqEnd - tSeqStart));
    }

    // ==========================================
    // F. Brent's Theorem Theoretical Bound Calculator
    // ==========================================

    public static class BrentsTheoremResult {
        public final double workT1;
        public final double spanTInf;
        public final int processorsP;
        public final double boundTp;
        public final double theoreticalSpeedup;
        public final double theoreticalEfficiency;
        public final String formula = "T_P <= (T_1 - T_infinity) / P + T_infinity <= T_1 / P + T_infinity";

        public BrentsTheoremResult(double workT1, double spanTInf, int processorsP) {
            if (processorsP <= 0) throw new IllegalArgumentException("Processor count P must be positive.");
            if (workT1 < 0 || spanTInf < 0) throw new IllegalArgumentException("Work and span must be non-negative.");
            if (spanTInf > workT1) throw new IllegalArgumentException("Span T_infinity cannot exceed total work T_1.");

            this.workT1 = workT1;
            this.spanTInf = spanTInf;
            this.processorsP = processorsP;
            this.boundTp = ((workT1 - spanTInf) / processorsP) + spanTInf;
            this.theoreticalSpeedup = workT1 > 0 ? (workT1 / boundTp) : 1.0;
            this.theoreticalEfficiency = theoreticalSpeedup / processorsP;
        }
    }
}
