package graph;

import java.util.ArrayList;
import java.util.List;

public class Graph {
    public final int n;
    public final boolean directed;
    public final List<Edge> edges = new ArrayList<>();
    public final List<List<Edge>> adj = new ArrayList<>();

    public Graph(int n, boolean directed) {
        if (n < 1) throw new IllegalArgumentException("Graph must have at least one vertex");
        this.n = n;
        this.directed = directed;
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
    }

    public void add(int u, int v, int weight) {
        if (u < 0 || u >= n || v < 0 || v >= n)
            throw new IllegalArgumentException("Invalid vertex number");
        Edge edge = new Edge(u, v, weight);
        edges.add(edge);
        adj.get(u).add(edge);
        if (!directed) adj.get(v).add(new Edge(v, u, weight));
    }
}
