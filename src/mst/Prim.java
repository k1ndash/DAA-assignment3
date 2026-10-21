package mst;

import graph.Edge;
import graph.Graph;
import graph.OperationCounter;
import java.util.PriorityQueue;

public class Prim {
    public static MSTResult run(Graph graph, OperationCounter counter) {
        if (graph.directed) throw new IllegalArgumentException("MST needs an undirected graph");
        boolean[] visited = new boolean[graph.n];
        PriorityQueue<Edge> queue = new PriorityQueue<>((a, b) -> {
            counter.operations++;
            return Integer.compare(a.w, b.w);
        });
        queue.add(new Edge(0, 0, 0));
        long weight = 0;
        int vertices = 0;

        while (!queue.isEmpty()) {
            Edge edge = queue.poll();
            counter.operations++;
            if (visited[edge.v]) continue;
            visited[edge.v] = true;
            vertices++;
            weight += edge.w;
            for (Edge next : graph.adj.get(edge.v)) {
                counter.operations++;
                if (!visited[next.v]) queue.add(next);
            }
        }
        return new MSTResult(weight, vertices - 1, graph.n);
    }
}
