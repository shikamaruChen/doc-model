package yifan.doc;

import java.util.Map;

import com.google.common.collect.Maps;

public class Node {
	int label;
	Map<Integer, Double> edges;

	public boolean hasTarget(int id) {
		return edges.containsKey(id);
	}
	
	public void removeEdge(int tid){
		edges.remove(tid);
	}

	public Node(int label) {
		this.label = label;
		edges = Maps.newHashMap();
	}
}
