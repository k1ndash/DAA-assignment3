package dsu;

import graph.OperationCounter;

public class DSU {
    private final int[] parent;
    private final int[] rank;
    private final OperationCounter counter;

    public DSU(int n, OperationCounter counter) {
        this.counter = counter;
        parent = new int[n];
        rank = new int[n];
        for (int i = 0; i < n; i++) parent[i] = i;
    }

    public int find(int x) {
        counter.operations++;
        if (parent[x] != x) parent[x] = find(parent[x]);
        return parent[x];
    }

    public boolean union(int a, int b) {
        counter.operations++;
        a = find(a);
        b = find(b);
        if (a == b) return false;
        if (rank[a] < rank[b]) { int t = a; a = b; b = t; }
        parent[b] = a;
        if (rank[a] == rank[b]) rank[a]++;
        return true;
    }
}
