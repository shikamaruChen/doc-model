package yifan.doc;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import static yifan.util.Property.TOPIC_NUM;
import yifan.util.IOUtils;

import com.google.common.collect.Maps;

public class Graph {
	private Map<Integer, Node> nodes;
	private int num;
	private double maxtp, mintp;// target interval
	private double maxp, minp; // current interval
	private double avgp;

	public void print() {
		int[] labels = new int[TOPIC_NUM];
		int edgen = 0;
		maxp = 0;
		minp = 1;
		avgp = 0;
		for (int sid : nodes.keySet()) {
			Node node = nodes.get(sid);
			labels[node.label]++;
			for (int tid : node.edges.keySet())
				if (sid < tid) {
					double p = node.edges.get(tid);
					maxp = (maxp < p) ? p : maxp;
					minp = (minp > p) ? p : minp;
					avgp += p;
					edgen++;
				}
		}
		avgp /= edgen;
		IOUtils.console(String.format("|V|=%d,|E|=%d", nodes.size(), edgen));
		IOUtils.console(String.format("max(p)=%.2f, min(p)=%.2f, avg(p)=%.2f",
				maxp, minp, avgp));

		for (int i = 0; i < labels.length; i++)
			IOUtils.console(String.format("label %d=%d", i, labels[i]));
	}

	public Graph(double maxtp, double mintp) {
		this.maxtp = maxtp;
		this.mintp = mintp;
		nodes = Maps.newTreeMap();
	}

	public void addNode(int label) {
		Node newnode = new Node(label);
		nodes.put(num++, newnode);
	}

	public void addEdge(int nid, int tid, double p) {
		Node node = nodes.get(nid);
		node.edges.put(tid, p);
	}

	public boolean checkEdge(int id1, int id2) {
		Node node1 = nodes.get(id1);
		Node node2 = nodes.get(id2);
		if (node1.hasTarget(id2) || node2.hasTarget(id1))
			return true;
		return false;
	}

	public void clean() {
		for (int i = 0; i < num; i++)
			if (nodes.containsKey(i)) {
				Node nodeA = nodes.get(i);
				Iterator<Integer> iter = nodeA.edges.keySet().iterator();
				// need to remove tid, use iterator
				while (iter.hasNext()) {
					int tid = iter.next();
					double p = nodeA.edges.get(tid);
					if (p > 0.99) {// regards as the same doc
						Node nodeB = nodes.get(tid);
						iter.remove(); // remove this -> target
						// IOUtils.console(String.format("i=%d,tid=%d", i,tid));
						nodeB.edges.remove(i); // remove target -> this
						// remove all edges to target
						for (int sid : nodeB.edges.keySet()) {
							Node node = nodes.get(sid);
							node.removeEdge(tid);
						}
						nodes.remove(tid);// remove target
					}
				}
			}
	}

	public void neo4jFile(String nodefile, String edgefile) throws IOException {
		IOUtils.console("write to neo4j graph file");
		BufferedWriter nodeWriter = IOUtils.bufferWriter(nodefile);
		BufferedWriter edgeWriter = IOUtils.bufferWriter(edgefile);
		nodeWriter.write("Node\tLabel");
		nodeWriter.newLine();
		for (int i = 0; i < nodes.size(); i++) {
			Node node = nodes.get(i);
			nodeWriter.write(String.format("%d\t%d", i, node.label));
			nodeWriter.newLine();
		}
		nodeWriter.close();
		edgeWriter.write("start\tend\ttype");
		edgeWriter.newLine();
		for (int i = 0; i < nodes.size(); i++) {
			Node node = nodes.get(i);
			for (int tid : node.edges.keySet()) {
				edgeWriter.write(String.format("%d\t%d\t1", i, tid));
				edgeWriter.newLine();
			}
		}
		edgeWriter.close();
	}

	public void diverseFile(String out) throws IOException {
		IOUtils.console("the graph maintains only the edges between nodes labeled differently");
		BufferedWriter writer = IOUtils.bufferWriter(out);
		int i = 0;
		Map<Integer, Integer> idMap = Maps.newHashMap();
		for (int id : nodes.keySet()) {
			Node node = nodes.get(id);
			writer.write(String.format("v %d %d", i, node.label));
			writer.newLine();
			idMap.put(id, i++);
		}
		for (int sid : nodes.keySet()) {
			Node nodeA = nodes.get(sid);
			int id1 = idMap.get(sid);
			for (int tid : nodeA.edges.keySet()) {
				Node nodeB = nodes.get(tid);
				int id2 = idMap.get(tid);
				if (id1 < id2 && nodeA.label != nodeB.label) {
					double p = nodeA.edges.get(tid);
					writer.write(String.format("e %d %d 1 %.2f", id1, id2,
							transform(p)));
					writer.newLine();
				}
			}
		}
		writer.close();
	}

	public void standardFile(String out) throws IOException {
		IOUtils.console("write to standard graph file");
		BufferedWriter writer = IOUtils.bufferWriter(out);

		int i = 0;
		Map<Integer, Integer> idMap = Maps.newHashMap();
		for (int id : nodes.keySet()) {
			Node node = nodes.get(id);
			writer.write(String.format("v %d %d", i, node.label));
			writer.newLine();
			idMap.put(id, i++);
		}
		for (int sid : nodes.keySet()) {
			Node node = nodes.get(sid);
			int id1 = idMap.get(sid);
			for (int tid : node.edges.keySet()) {
				int id2 = idMap.get(tid);
				if (id1 < id2) {
					double p = node.edges.get(tid);
					writer.write(String.format("e %d %d 1 %.2f", id1, id2,
							transform(p)));
					writer.newLine();
				}
			}
		}
		writer.close();
	}

	private double transform(double p) {
		// TODO Auto-generated method stub
		return (p - minp) / (maxp - minp) * (maxtp - mintp) + mintp;
	}
}
