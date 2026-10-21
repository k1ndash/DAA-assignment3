package shortestpath;

import graph.Edge;
import graph.Graph;
import graph.OperationCounter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

public class Dijkstra {
    public static ShortestPaths run(Graph graph, int source, OperationCounter counter) {
        if (source < 0 || source >= graph.n) throw new IllegalArgumentException("Invalid source");
        // Check even unreachable edges: the input must have no negative weights.
        for (Edge edge : graph.edges)
            if (edge.w < 0) throw new IllegalArgumentException("Dijkstra requires non-negative weights");

        long[] dist = new long[graph.n];
        Arrays.fill(dist, Long.MAX_VALUE);
        int[] prev = new int[graph.n];
        Arrays.fill(prev, -1);
        dist[source] = 0;

        PriorityQueue<long[]> queue = new PriorityQueue<>(Comparator.comparingLong(a -> a[1]));
        queue.add(new long[]{source, 0});
        while (!queue.isEmpty()) {
            long[] current = queue.poll();
            int u = (int) current[0];
            counter.operations++;
            if (current[1] != dist[u]) continue;
            for (Edge edge : graph.adj.get(u)) {
                counter.operations++;
                long candidate = dist[u] + edge.w;
                if (candidate < dist[edge.v]) {
                    dist[edge.v] = candidate;
                    prev[edge.v] = u;
                    queue.add(new long[]{edge.v, candidate});
                }
            }
        }
        return new ShortestPaths(dist, prev);
    }
}
