package gr.gousiosg.javacg.stat.graph;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class GraphUtil {
    public static Graph buildGraph(Map<String, Set<Set<String>>> adjList) {
        Graph g = new Graph();

        // create all nodes and edges
        adjList.entrySet().forEach(entry -> {

            String src = entry.getKey();

            Set<String> destSet = entry.getValue().stream().flatMap(Set::stream).collect(Collectors.toSet());

            destSet.forEach(dest -> {
//				if (!dest.startsWith("java")) {
                g.createOrRetrieveEdge(src, dest);
//				}
            });
        });

        return g;
    }

}
