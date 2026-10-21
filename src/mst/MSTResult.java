package mst;

public class MSTResult {
    public final long weight;
    public final int edgesUsed;
    public final boolean connected;

    public MSTResult(long weight, int edgesUsed, int vertices) {
        this.weight = weight;
        this.edgesUsed = edgesUsed;
        this.connected = edgesUsed == vertices - 1;
    }
}
