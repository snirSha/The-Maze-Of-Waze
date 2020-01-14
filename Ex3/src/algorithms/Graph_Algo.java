package algorithms;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import dataStructure.DGraph;
import dataStructure.Node;
import dataStructure.edge_data;
import dataStructure.graph;
import dataStructure.node_data;
/*
 * The implementation of graph_algorithms interface.
 * Algorithms:
 * init from graph = initial new graph from other graph
 * init from file = initial new graph from text file 
 * save to file = save graph in text file
 * isConnected = check if the graph is strongly connected
 * shortestPathDist = calculate the shortest path between source node and destination node by the weights of the edges
 * shortestPath = return the shortest route through the nodes (source and destination) 
 * TSP = return a route between a list of nodes (don't have to be the shortest route)
 * copy = deep copy a graph
 * 
 * @authors Snir and Omer 
 */
public class Graph_Algo implements graph_algorithms{
	public DGraph g;

	/*
	 * Default constructor
	 */
	public Graph_Algo() {
		this.g = new DGraph();
	}

	public Graph_Algo(graph _graph) {
		this.g = new DGraph();
		init(_graph);
	}

	/*
	 * Set this graph to the parameter's graph
	 */
	@Override
	public void init(graph g) {
		this.g = (DGraph) g;		
	}

	/*
	 * Set the graph from a file (using Yael's algorithm)
	 */
	@Override
	public void init(String file_name) {
		try
		{    
			FileInputStream file = new FileInputStream(file_name); 
			ObjectInputStream in = new ObjectInputStream(file);
			this.g = (DGraph)in.readObject();
			in.close(); 
			file.close(); 
			System.out.println("Object has been deserialized");
		} 

		catch(IOException | ClassNotFoundException ex) 
		{ 
			System.out.println("IOException is caught"); 
		} 
	}

	/*
	 * Save a graph in a text file (using Yael's algorithm)
	 */
	@Override
	public void save(String file_name) {	
		try
		{
			FileOutputStream file = new FileOutputStream(file_name); 
			ObjectOutputStream out = new ObjectOutputStream(file);
			out.writeObject(this.g);
			out.close(); 
			file.close(); 

			System.out.println("Object has been serialized"); 
		}   
		catch(IOException ex) 
		{
			System.out.println("IOException is caught"); 
		}
	}

	/*
	 * The function's steps: 
	 * zero all tags in every node -> DFS -> check if all tags are 1 -> if true ,zero all tags and reverse the graph
	 * -> DFS again -> check if all tags are 1-> if true, the graph is strongly connected 
	 * 
	 * @param x = get the first node in the graph and save it to future use
	 * @function getFirtNode = return the first node in the graph  
	 * @function zeroTags = sets all node's tag to 0
	 * @function DFS = start at a node and keep going until it set all the node's tag to 1, if not the graph is not connected 
	 * @function checkAllTags = check if all the node's tag is 1 
	 * @return true if the graph is strongly connected
	 */
	@Override
	public boolean isConnected() {
		if(g.nodeSize()==0)
			return true;
		else {
			int x=getFirstNode();
			zeroTags();
			DFS(x,g.nodeSize());			
			if(!checkAllTags())
				return false;
			zeroTags();
			g.reversedGraph();
			zeroTags();
			DFS(x,g.nodeSize());

			boolean ans=true;
			if(!checkAllTags()) {
				ans= false; 
			}
			zeroTags();
			g.reversedGraph();
			return ans;
		}
	}

	/*
	 * A helper function that check if all tags are 1
	 * @param nod = A collection of all the nodes in the graph
	 */
	private boolean checkAllTags() {
		Collection<node_data> nod=g.getV();
		for(node_data a:nod) {
			if(a.getTag()!=1)
				return false;
		}
		return true;
	}

	/*
	 * A recursive helper that start at first node and find all his neighbors and their neighbors and so on until it gets to any connects node
	 * @param counter = The number of nodes in the graph
	 * @param i = The data of the first node
	 * @param edgesOf = The collection of all the edges that came out of the parameter's node
	 */
	private void DFS(int i,int counter) {
		if(g.getNode(i)==null || counter==0)
			return;
		g.getNode(i).setTag(1);
		counter--;
		Collection<edge_data> edgesOf = g.getE(i);
		for(edge_data e:edgesOf) 
			DFS(e.getDest(),counter);			
	}


	/*
	 * A helper function that zero all tags in all the nodes.
	 * @param n = A collection of all the nodes in the graph 
	 * @param a = Parameter that iterate through all the nodes
	 */ 
	private void zeroTags() {
		Collection<node_data> n=g.getV();
		for(node_data a: n) {
			a.setTag(0);
		}		
	}

	/*
	 * A helper function that return the first node in the graph
	 * @param a = Parameter that iterate through all the nodes
	 */
	private int getFirstNode() {
		for(node_data a:g.getV())
			return a.getKey();
		return 0;
	}
	
	/*
	 * @function zeroTags = Zero all the node's tags
	 * @function maxValueWeight = Set all the node's weight to infinity
	 * @param source = The source node
	 * @param str = The information that will contain the list of nodes of the shortest path
	 * @param areWeInLoop = the amount of nodes in the graph, will help us to detect if we are in a infinite loop
	 * @function diakstra = A function that calculate the shortest path between 2 nodes   
	 * @return the distance of the shortest path between source node and destination node (using the weights of node and edge)
	 */
	@Override
	public double shortestPathDist(int src, int dst) {
		zeroTags();
		maxValueWeight();
		Node source = (Node)g.getNode(src);
		source.setWeight(0);
		String str="";
		int areWeInLoop=g.nodeSize();
		diakstra(src,dst,str,areWeInLoop,src);
		if(g.getNode(dst).getWeight()!=Double.MAX_VALUE)
			return g.getNode(dst).getWeight();
		else {
			System.out.println("There is no path between src and dst");
			return -1;
		}
	}

	/*
	 * A recursive function that calculate the shortest path between 2 nodes and update the weights and the information of the nodes
	 * 
	 * @param src = Source node
	 * @param dst = Destination node
	 * @param str = The string of the list of the shortest path
	 * @param areWeInLoop = Tell us if we are in a loop
	 * @param theFirstsrc = save the first source node and check if we in loop
	 * @param runner = A node that start in source and move forward until it get to destination
	 * @param edges = A collection of the edges that came of the source node
	 * @param newWeight = Sum of the weight (last node and edge)
	 * @param oldWeight = The old weight 
	 */
	public void diakstra(int src,int dst,String str,int areWeInLoop, int theFirstsrc) {
		Node runner=(Node) g.getNode(src);
		if(src==theFirstsrc)
			areWeInLoop--;
		if((runner.getTag()==1 && dst==src )||(areWeInLoop<0)) {
			return;
		}
		Collection<edge_data> edges=g.getE(src);
		for(edge_data e:edges) {
			double newWeight=runner.getWeight()+e.getWeight();
			double oldWeight=g.getNode(e.getDest()).getWeight();
			if(newWeight<oldWeight) {
				g.getNode(e.getDest()).setWeight(newWeight);
				g.getNode(e.getDest()).setInfo(str+","+src);
				diakstra(e.getDest(),dst,str+","+src,areWeInLoop,theFirstsrc);
				runner.setTag(1);
			}

		}
	}

	/*
	 * Sets all the node's weight to infinity
	 * @param nodes = A collection of all the nodes in the graph 
	 */
	private void maxValueWeight() {
		Collection<node_data> nodes = g.getV();
		for(node_data a: nodes)
			a.setWeight(Double.MAX_VALUE);
	}


	/*
	 * @param src = Source node
	 * @param dest = Destination node
	 * @param str = The string of the list of the shortest path
	 * @param k = Every node in the list of nodes from the shortest path
	 * @function zeroTags = Zero all the node's tags
	 * @function maxValueWeight = Set all the node's weight to infinity
	 * @param areWeInLoop = the amount of nodes in the graph, will help us to detect if we are in a infinite loop
	 * @function diakstra = A function that calculate the shortest path between 2 nodes
	 * @param source = Source node
	 * @param ans = The list of nodes in the shortest path 
	 * @param strArray = array of all the nodes in the list of shortest path
	 * @param tmp = A node from the list after casting
	 * @return a list of nodes in the shortest path between source node and destination node
	 */
	@Override
	public List<node_data> shortestPath(int src, int dest) {
		String str="";
		int k;
		zeroTags();
		maxValueWeight();
		Node source = (Node)g.getNode(src);
		source.setWeight(0);
		int areWeInLoop=g.nodeSize();

		ArrayList<node_data> arr=new ArrayList<>();
		diakstra(src,dest,str,areWeInLoop,src);
		if(g.getNode(dest).getWeight()==shortestPathDist(src,dest)) {

			String ans =g.getNode(dest).getInfo();
			ans=ans.substring(1);
			String[] strArray=ans.split(",");
			for (int i = 0; i < strArray.length; i++) {
				k=Integer.parseInt(strArray[i]);
				node_data tmp=g.getNode(k);
				arr.add(tmp);
			}
			arr.add(g.getNode(dest));
			return arr;
		}
		else {
			System.out.println("There is no path between src and dst");
			return null;
		}
	}
	
	/*
	 *@param targets = A list of nodes that we need to find a path between them
	 *@function checkTargetsInGraph = Check if all the targets's nodes are in the graph
	 *@function isConnected = Check if the graph is strongly connected,  if not return null
	 *@param array = A list of the path's nodes 
	 *@param tmp = Another list of the path's nodes 
	 *@param checkTargetsInAnswer = Check if all the targets's nodes are in the answer List
	 */
	@Override
	public List<node_data> TSP(List<Integer> targets) {
		if((!targets.isEmpty()) && (targets.size()<=g.nodeSize()) && (checkTargetsInGraph(targets))) {
			List<node_data> array=new ArrayList<>();
			if(targets.size()==1) {
				array.add(g.getNode(targets.get(0)));
				return array;
			}
			if(shortestPath(targets.get(0),targets.get(1))!=null){
					array.addAll((shortestPath(targets.get(0),targets.get(1))));
			}
			else {
				System.out.println("\nThere is no path between the nodes in 'targets'");
				return null;				
			}
			if(targets.size()==2)
				return array;
			List<node_data> tmp = new ArrayList<>();
			for (int i = 1; i < targets.size()-1; i++) {
				int j=i+1;
				if(shortestPath(targets.get(i),targets.get(j))!=null) {
					tmp.addAll(shortestPath(targets.get(i),targets.get(j)));
				}
				else {
					System.out.println("\nThere is no path between the nodes in 'targets'");
					return null;						
				}
				if((tmp!=null) && checkTargetsInAnswer(targets,tmp) && tmp.containsAll(array)) {
					return tmp;
				}
				else if(tmp!=null && checkTargetsInAnswer(targets,array) && array.containsAll(tmp)) {
					return array;
				}
				else if((tmp!=null)){
					tmp.remove(0);
					array.addAll(tmp);
					tmp.clear();
					if(checkTargetsInAnswer(targets,array))
						return array;
				}
			}
		}
		System.out.println("\nThere is no path between the nodes in 'targets'");
		return null;
	}


	/*
	 * @param counter = counts the nodes that are similar
	 * @return if all the targets's nodes are in the answer list
	 */
	private boolean checkTargetsInAnswer(List<Integer> targets,List<node_data> array) {
		int counter = 0;
		for(Integer i:targets) {
			for(node_data n:array) {
				if(i==n.getKey()) {
					counter++;
					break;
				}
			}
		}
		return (counter == targets.size());
	}

	/*
	 * @param nod = A collection of all the nodes in the graph
	 * @param count = counts the nodes that are similar
	 * @return if all the targets's nodes are in the graph
	 */
	private boolean checkTargetsInGraph(List<Integer> targets) {
		Collection<node_data> nod=g.getV();
		for(int a=0;a<targets.size();a++) {//check if there is a integer that repeats itself in targets
			for(int b=0;b<targets.size();b++) {
				if(a!=b&&targets.get(a)==targets.get(b))
					return false;
			}
		}
		int count = 0;
		for(int i:targets) {
			for(node_data n:nod) {
				if(i == n.getKey())
					count++;
			}
		}
		return (count == targets.size());
	}

	/*
	 * Deep copy function
	 * @param newG = A new DGraph 
	 */
	@Override
	public graph copy() {
		DGraph newG = new DGraph((DGraph) g);
		return newG;
	}
}