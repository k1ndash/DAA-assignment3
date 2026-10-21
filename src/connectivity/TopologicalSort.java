package connectivity;

import graph.Edge;
import graph.Graph;
import graph.OperationCounter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TopologicalSort {
    public static List<Integer> run(Graph graph, OperationCounter counter) {
        if (!graph.directed) throw new IllegalArgumentException("Topological sort needs a directed graph");
        int[] indegree = new int[graph.n];
        for (Edge edge : graph.edges) {
            indegree[edge.v]++;
            counter.operations++;
        }

        ArrayDeque<Integer> queue = new ArrayDeque<>();
        for (int i = 0; i < graph.n; i++)
            if (indegree[i] == 0) queue.add(i);

        List<Integer> order = new ArrayList<>();
        while (!queue.isEmpty()) {
            int u = queue.remove();
            order.add(u);
            for (Edge edge : graph.adj.get(u)) {
                counter.operations++;
                if (--indegree[edge.v] == 0) queue.add(edge.v);
            }
        }

        return order.size() == graph.n ? order : Collections.emptyList();
    }
}
