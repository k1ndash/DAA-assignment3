package connectivity;

import graph.Edge;
import graph.Graph;
import graph.OperationCounter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Kosaraju {
    public static List<List<Integer>> run(Graph graph, OperationCounter counter) {
        if (!graph.directed) throw new IllegalArgumentException("SCC needs a directed graph");
        boolean[] visited = new boolean[graph.n];
        List<Integer> order = new ArrayList<>();


        for (int root = 0; root < graph.n; root++) {
            if (visited[root]) continue;
            ArrayDeque<int[]> stack = new ArrayDeque<>();
            visited[root] = true;
            stack.push(new int[]{root, 0});
            while (!stack.isEmpty()) {
                int[] top = stack.peek();
                int u = top[0];
                if (top[1] < graph.adj.get(u).size()) {
                    int v = graph.adj.get(u).get(top[1]++).v;
                    counter.operations++;
                    if (!visited[v]) {
                        visited[v] = true;
                        stack.push(new int[]{v, 0});
                    }
                } else {
                    order.add(u);
                    stack.pop();
                }
            }
        }

        List<List<Integer>> reverse = new ArrayList<>();
        for (int i = 0; i < graph.n; i++) reverse.add(new ArrayList<>());
        for (Edge edge : graph.edges) {
            reverse.get(edge.v).add(edge.u);
            counter.operations++;
        }


        Arrays.fill(visited, false);
        List<List<Integer>> components = new ArrayList<>();
        for (int i = order.size() - 1; i >= 0; i--) {
            int root = order.get(i);
            if (visited[root]) continue;
            List<Integer> component = new ArrayList<>();
            ArrayDeque<Integer> stack = new ArrayDeque<>();
            stack.push(root);
            visited[root] = true;
            while (!stack.isEmpty()) {
                int u = stack.pop();
                component.add(u);
                for (int v : reverse.get(u)) {
                    counter.operations++;
                    if (!visited[v]) {
                        visited[v] = true;
                        stack.push(v);
                    }
                }
            }
            components.add(component);
        }
        return components;
    }
}
