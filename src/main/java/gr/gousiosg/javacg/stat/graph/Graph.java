package gr.gousiosg.javacg.stat.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Graph {

	Map<String, gr.gousiosg.javacg.stat.graph.Node> nodeIndex;
	Map<String, Edge> edgeIndex;

	static String edgeIdFormat = "%s->%s";

	public Graph() {
		nodeIndex = new HashMap<>();
		edgeIndex = new HashMap<>();
	}

	public Node createOrRetrieveNode(String methodName) {
		nodeIndex.putIfAbsent(methodName, createNewNode(methodName));
		return nodeIndex.get(methodName);
	}

	public Edge createOrRetrieveEdge(String m1, String m2) {
		String edgeId = String.format(edgeIdFormat, m1, m2);
		edgeIndex.putIfAbsent(edgeId, createNewEdge(m1, m2));
		return edgeIndex.get(edgeId);
	}

	private Edge createNewEdge(String m1, String m2) {
		Node n1 = createOrRetrieveNode(m1);
		Node n2 = createOrRetrieveNode(m2);
		Edge e = new Edge(n1, n2);
		n1.addNewOutboundEdge(e);
		return e;
	}

	private Node createNewNode(String methodName) {
		Node node = new Node(methodName);
		return node;
	}

	public Node getNode(String methodName) {
		return nodeIndex.get(methodName);
	}

	public Set<String> getReachableMethods(String origin) {
		Node src = nodeIndex.get(origin);

		// visit all reachable nodes by dfs
		Set<String> visited = new HashSet<>();
		if (src != null) {
			dfs(src, visited);
		}else {
			System.out.println(String.format("%s method not found", origin));
		}

		return visited;
	}

	void dfs(Node src, Set<String> visited) {
		if (visited.contains(src.getMethodName()))
			return;
		visited.add(src.getMethodName());
		src.getOutboundEdges().forEach(edge -> {
			dfs(edge.getDest(), visited);
		});

	}

}
