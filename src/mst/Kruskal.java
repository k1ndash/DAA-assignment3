package mst;

import dsu.DSU;
import graph.Edge;
import graph.Graph;
import graph.OperationCounter;
import java.util.ArrayList;
import java.util.List;

public class Kruskal {
    public static MSTResult run(Graph graph, OperationCounter counter) {
        if (graph.directed) throw new IllegalArgumentException("MST needs an undirected graph");
        List<Edge> sorted = new ArrayList<>(graph.edges);
        sorted.sort((a, b) -> {
            counter.operations++;
            return Integer.compare(a.w, b.w);
        });

        DSU dsu = new DSU(graph.n, counter);
        long weight = 0;
        int used = 0;
        for (Edge edge : sorted) {
            counter.operations++;
            if (dsu.union(edge.u, edge.v)) {
                weight += edge.w;
                used++;
                if (used == graph.n - 1) break;
            }
        }
        return new MSTResult(weight, used, graph.n);
    }
}
