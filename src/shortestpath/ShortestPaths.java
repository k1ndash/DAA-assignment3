package shortestpath;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ShortestPaths {
    public final long[] dist;
    public final int[] prev;

    public ShortestPaths(long[] dist, int[] prev) {
        this.dist = dist;
        this.prev = prev;
    }

    public List<Integer> path(int target) {
        if (dist[target] == Long.MAX_VALUE) return Collections.emptyList();
        LinkedList<Integer> path = new LinkedList<>();
        for (int v = target; v != -1; v = prev[v]) path.addFirst(v);
        return path;
    }
}
