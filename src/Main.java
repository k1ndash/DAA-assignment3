import connectivity.Kosaraju;
import connectivity.TopologicalSort;
import graph.Edge;
import graph.Graph;
import graph.GraphGenerator;
import graph.OperationCounter;
import mst.Kruskal;
import mst.MSTResult;
import mst.Prim;
import shortestpath.Dijkstra;
import shortestpath.ShortestPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class Main {
    interface Experiment {
        void run(OperationCounter counter);
    }

    static String measure(String name, int n, int m, Experiment experiment) {

        for (int i = 0; i < 3; i++) experiment.run(new OperationCounter());
        long totalTime = 0;
        long totalOperations = 0;
        for (int i = 0; i < 5; i++) {
            OperationCounter counter = new OperationCounter();
            long start = System.nanoTime();
            experiment.run(counter);
            totalTime += System.nanoTime() - start;
            totalOperations += counter.operations;
        }
        return String.format(Locale.US, "%s,%d,%d,%.6f,%d",
                name, n, m, totalTime / 5.0 / 1e6, totalOperations / 5);
    }

    static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    static void tests() {
        Graph single = new Graph(1, false);
        check(Kruskal.run(single, new OperationCounter()).connected, "single vertex Kruskal");
        check(Prim.run(single, new OperationCounter()).connected, "single vertex Prim");

        Graph disconnected = new Graph(4, false);
        disconnected.add(0, 1, 2);
        disconnected.add(2, 3, 2);
        check(!Kruskal.run(disconnected, new OperationCounter()).connected, "disconnected Kruskal");
        check(!Prim.run(disconnected, new OperationCounter()).connected, "disconnected Prim");

        Graph equal = new Graph(4, false);
        equal.add(0, 1, 1);
        equal.add(1, 2, 1);
        equal.add(2, 3, 1);
        equal.add(3, 0, 1);
        equal.add(0, 2, 2);
        check(Kruskal.run(equal, new OperationCounter()).weight == 3, "multiple MSTs");
        check(Prim.run(equal, new OperationCounter()).weight == 3, "duplicate weights");

        Graph dag = new Graph(4, true);
        dag.add(0, 1, 1);
        dag.add(0, 2, 1);
        dag.add(2, 3, 1);
        List<Integer> order = TopologicalSort.run(dag, new OperationCounter());
        check(order.size() == 4, "DAG topological order");
        int[] position = new int[4];
        for (int i = 0; i < 4; i++) position[order.get(i)] = i;
        for (Edge edge : dag.edges)
            check(position[edge.u] < position[edge.v], "topological edge order");
        dag.add(3, 0, 1);
        check(TopologicalSort.run(dag, new OperationCounter()).isEmpty(), "cycle detection");

        Graph directed = new Graph(5, true);
        directed.add(0, 1, 2);
        directed.add(1, 0, 2);
        directed.add(1, 2, 3);
        directed.add(2, 3, 1);
        directed.add(3, 2, 1);
        List<List<Integer>> components = Kosaraju.run(directed, new OperationCounter());
        check(components.size() == 3, "SCC count");
        Set<Integer> seen = new HashSet<>();
        for (List<Integer> component : components)
            for (int v : component) check(seen.add(v), "SCC uniqueness");
        check(seen.size() == 5, "SCC coverage");

        ShortestPaths paths = Dijkstra.run(directed, 0, new OperationCounter());
        check(paths.dist[3] == 6, "shortest distance");
        check(paths.path(3).equals(Arrays.asList(0, 1, 2, 3)), "shortest path");
        check(paths.path(4).isEmpty(), "unreachable vertex");

        Graph negative = new Graph(2, true);
        negative.add(0, 1, -1);
        boolean rejected = false;
        try {
            Dijkstra.run(negative, 0, new OperationCounter());
        } catch (IllegalArgumentException ex) {
            rejected = true;
        }
        check(rejected, "negative weight rejection");

        for (int n : new int[]{10, 100, 1000}) {
            int m = n == 10 ? 30 : n == 100 ? 500 : 5000;
            Graph graph = GraphGenerator.undirected(n, m, 42 + n);
            MSTResult a = Kruskal.run(graph, new OperationCounter());
            MSTResult b = Prim.run(graph, new OperationCounter());
            check(a.connected && b.connected && a.weight == b.weight,
                    "random MST comparison " + n);
        }
        System.out.println("All tests passed.");
    }

    static void demo() {
        Graph graph = new Graph(5, false);
        graph.add(0, 1, 4);
        graph.add(0, 2, 2);
        graph.add(1, 2, 1);
        graph.add(1, 3, 5);
        graph.add(2, 3, 8);
        MSTResult a = Kruskal.run(graph, new OperationCounter());
        MSTResult b = Prim.run(graph, new OperationCounter());
        System.out.println("Demo MST: Kruskal=" + a.weight + ", Prim=" + b.weight
                + ", connected=" + a.connected);

        ShortestPaths paths = Dijkstra.run(graph, 0, new OperationCounter());
        for (int i = 0; i < graph.n; i++) {
            String distance = paths.dist[i] == Long.MAX_VALUE ? "unreachable" : "" + paths.dist[i];
            System.out.println("District " + i + ": distance=" + distance + ", path=" + paths.path(i));
        }

        Graph directed = new Graph(4, true);
        directed.add(0, 1, 1);
        directed.add(1, 2, 1);
        directed.add(2, 0, 1);
        directed.add(2, 3, 1);
        System.out.println("Topological ordering possible: "
                + !TopologicalSort.run(directed, new OperationCounter()).isEmpty());
        System.out.println("SCCs: " + Kosaraju.run(directed, new OperationCounter()));
    }

    static void benchmarks() throws IOException {
        Path output = Paths.get("results/tables/benchmark.csv");
        Files.createDirectories(output.getParent());
        List<String> lines = new ArrayList<>();
        lines.add("algorithm,vertices,edges,avg_ms,avg_operations");
        int[][] sizes = {{10, 30}, {100, 500}, {1000, 5000}};

        for (int[] size : sizes) {
            int n = size[0], m = size[1];
            Graph undirected = GraphGenerator.undirected(n, m, 42 + n);
            Graph dag = GraphGenerator.directed(n, m, 77 + n, true);
            Graph cyclic = GraphGenerator.directed(n, m, 99 + n, false);
            check(TopologicalSort.run(cyclic, new OperationCounter()).isEmpty(),
                    "random directed graph should contain a cycle");

            lines.add(measure("Kruskal", n, m, c -> Kruskal.run(undirected, c)));
            lines.add(measure("Prim", n, m, c -> Prim.run(undirected, c)));
            lines.add(measure("Kosaraju", n, m, c -> Kosaraju.run(cyclic, c)));
            lines.add(measure("Topo DAG", n, m, c -> TopologicalSort.run(dag, c)));
            lines.add(measure("Topo cyclic", n, m, c -> TopologicalSort.run(cyclic, c)));
            lines.add(measure("Dijkstra", n, m, c -> Dijkstra.run(undirected, 0, c)));
        }
        Files.write(output, lines);
        System.out.println("Saved " + output);
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0) { demo(); return; }
        switch (args[0]) {
            case "test": tests(); break;
            case "benchmark": tests(); benchmarks(); break;
            case "demo": demo(); break;
            default: System.out.println("Usage: java -cp out Main [demo|test|benchmark]");
        }
    }
}
