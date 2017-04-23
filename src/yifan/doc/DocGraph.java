package yifan.doc;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;

import yifan.util.IOUtils;
import static yifan.util.Property.*;

public class DocGraph {

	public static void readNode(Graph graph, String file)
			throws FileNotFoundException, IOException {
		IOUtils.console("read node labels");
		BufferedReader reader = IOUtils.bufferReader(file);
		String line;
		while ((line = reader.readLine()) != null) {
			String[] memberships = line.split(" ");
			double max = 0;
			int j = 0;
			for (int i = 0; i < memberships.length; i++) {
				double p = Double.parseDouble(memberships[i]);
				if (max < p) {
					max = p;
					j = i;
				}
			}
			graph.addNode(j);
		}
		reader.close();
	}

	public static void readEdge(Graph graph, String file) throws IOException {
		IOUtils.console("read edges");
		BufferedReader reader = IOUtils.bufferReader(file);
		String line;
		while ((line = reader.readLine()) != null) {
			String[] edges = line.split(" ");
			int sid = Integer
					.parseInt(edges[0].substring(3, edges[0].length()));
			for (int i = 1; i < edges.length; i++) {
				String edge = edges[i];
				// edge = edge.substring(1, edge.length() - 1);
				// IOUtils.console(edge);
				String[] terms = edge.split(",");
				int tid = Integer.parseInt(terms[0]);
				double p = Double.parseDouble(terms[1]);
				// if (sid < tid)
				if (sid != tid)
					graph.addEdge(sid, tid, p);
			}
		}
		reader.close();
	}

	public void run() throws IOException {
		Graph graph = new Graph(MAX_P,MIN_P);
		String node = DT_FILE;
		String edge = EDGE_FILE;
		String file = GRAPH_FILE;
		readNode(graph, node);
		readEdge(graph, edge);
		graph.clean();
		graph.print();
		// graph.neo4jFile(file + ".node", file + ".edge");
		graph.standardFile(file);
//		graph.diverseFile(file);
	}

}
