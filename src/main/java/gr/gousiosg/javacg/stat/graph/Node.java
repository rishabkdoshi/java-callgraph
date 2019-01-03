package gr.gousiosg.javacg.stat.graph;

import java.util.ArrayList;
import java.util.List;

public class Node {

    private String methodName;
    private List<Edge> outboundEdges;

    public Node(String methodName) {
        this.methodName = methodName;
        this.outboundEdges = new ArrayList<>();
    }

    public String getMethodName() {
        return methodName;
    }

    public void addNewOutboundEdge(Edge e) {
        this.outboundEdges.add(e);
    }

    public List<Edge> getOutboundEdges() {
        return outboundEdges;
    }



}
