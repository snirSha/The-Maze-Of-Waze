package algorithms;
/**
 * This interface represents the "regular" Graph Theory algorithms including:
 * 0. clone();
 * 1. init(String file_name);
 * 2. save(String file_name);
 * 3. isConnected();
 * 5. double shortestPathDist(int src, int dest);
 * 6. List<Node> shortestPath(int src, int dest);
 *
 * @author boaz.benmoshe
 *
 */

import java.util.List;


import dataStructure.graph;
import dataStructure.node_data;

public interface graph_algorithms {
	
	/**
	 * Init this set of algorithms on the parameter - graph.
	 * @param g
	 */
	public void init(graph g);

	/**
	 * Init a graph from file
	 * @param file_name
	 */
	public void init(String file_name);
	/** Saves the graph to a file.
	 * 
	 * @param file_name
	 */
	public void save(String file_name);

	/**
	 * returns the length of the shortest path between src to dest
	 * @param src - start node
	 * @param dest - end (target) node
	 * @return
	 */
	public double shortestPathDist(int src, int dest);
	/**
	 * returns the the shortest path between src to dest - as an ordered List of nodes:
	 * src--> n1-->n2-->...dest
	 * see: https://en.wikipedia.org/wiki/Shortest_path_problem
	 * @param src - start node
	 * @param dest - end (target) node
	 * @return
	 */
	public List<node_data> shortestPath(int src, int dest);
	
}
