package graph;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class GraphGenerator {
    public static Graph undirected(int n, int m, long seed) {
        if (m < n - 1 || m > (long) n * (n - 1) / 2)
            throw new IllegalArgumentException("Invalid number of edges");
        Graph graph = new Graph(n, false);
        Random random = new Random(seed);
        Set<Long> pairs = new HashSet<>();

        // Start with a spanning tree, so every district is connected.
        for (int v = 1; v < n; v++)
            addUnique(graph, pairs, v, random.nextInt(v), 1 + random.nextInt(100));

        while (graph.edges.size() < m) {
            int a = random.nextInt(n), b = random.nextInt(n);
            if (a != b) addUnique(graph, pairs, a, b, 1 + random.nextInt(100));
        }
        return graph;
    }

    private static void addUnique(Graph graph, Set<Long> pairs, int a, int b, int weight) {
        int x = Math.min(a, b), y = Math.max(a, b);
        long key = ((long) x << 32) | y;
        if (pairs.add(key)) graph.add(a, b, weight);
    }

    public static Graph directed(int n, int m, long seed, boolean dag) {
        long maximum = dag ? (long) n * (n - 1) / 2 : (long) n * (n - 1);
        if (m < 0 || m > maximum) throw new IllegalArgumentException("Invalid number of edges");
        Graph graph = new Graph(n, true);
        Random random = new Random(seed);
        Set<Long> pairs = new HashSet<>();

        while (graph.edges.size() < m) {
            int a = random.nextInt(n), b = random.nextInt(n);
            if (a == b) continue;
            if (dag && a > b) { int t = a; a = b; b = t; }
            long key = ((long) a << 32) | b;
            if (pairs.add(key)) graph.add(a, b, 1 + random.nextInt(100));
        }
        return graph;
    }
}
