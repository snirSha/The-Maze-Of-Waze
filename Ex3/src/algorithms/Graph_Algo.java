package algorithms;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
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
	private int amountNodes(node_data n)
	{
		int counter = 0;
		ArrayList<node_data> nodesInGraph = new ArrayList<node_data>();
		nodesInGraph.add(n);
		while(!nodesInGraph.isEmpty())
		{
			node_data temp = nodesInGraph.get(0);
			if(temp.getTag() == 0)
			{
				temp.setTag(1);
				counter++;
				nodesInGraph.remove(0);
				Collection<edge_data> edge = g.getE(temp.getKey());

				for (edge_data edge_data : edge) {
					node_data other = g.getNode(edge_data.getDest());
					if(other.getTag() == 0)
					{
						nodesInGraph.add(0,other);
					}
				}
			}
			else
			{
				nodesInGraph.remove(0);
			}
		}
		return counter;
	}
	
	@Override
	public boolean isConnected()
	{
		HashSet<node_data> allreayConnected = new HashSet<node_data>();
		Collection<node_data> s = g.getV();
		boolean firstNode = true;
		node_data firstN = null;
		boolean ansBool = true;
		for (node_data node_data: s) {
			setTagsZero();
			if(firstNode)
			{
				firstN = node_data;
				firstNode = false;
				int ans = amountNodes(node_data);
				if(ans != s.size())
				{
					return false;
				}
			}
			else
			{
				if(!canOthersGeToThis(node_data, firstN, allreayConnected))
				{
					return false;
				}
			}
		}
		return ansBool;
	}
	/**
	 * Iterates over all the nodes and checks if there any way to get to the first node
	 * @param n
	 * @param first
	 * @param allreadtconnected
	 * @return
	 */
	private boolean canOthersGeToThis(node_data n,node_data first,HashSet<node_data> allreadtconnected)
	{
		if(n.getKey() == first.getKey())
		{
			allreadtconnected.add(n);
			return true;
		}
		ArrayList<node_data> nodesToCheck = new ArrayList<node_data>();
		nodesToCheck.add(n);
		while(!nodesToCheck.isEmpty())
		{
			node_data currN = nodesToCheck.remove(0);
			if(currN.getTag() == 0)
			{
				currN.setTag(1);
				Collection<edge_data> edge = g.getE(currN.getKey());
				for (edge_data edge_data : edge) {
					node_data dest = g.getNode(edge_data.getDest());
					if(first.getKey() == edge_data.getDest() || allreadtconnected.contains(dest))
					{
						allreadtconnected.add(currN);
						return true;
					}
					else {
						if(dest.getTag() == 0)
						{
							nodesToCheck.add(0,dest);
						}
					}
				}
			}
		}
		return false;
	}
	/**
	 * Sets all tags to 0
	 */
	private void setTagsZero()
	{
		Collection<node_data> nodes = g.getV();
		for (node_data node_data : nodes) {
			node_data.setTag(0);
		}
	}
	/**
	 * sets tag to 0 and weight and MAX_VALUE on all nodes
	 */
	private void cleanTagsSetweight()
	{
		Collection<node_data> s = g.getV();
		for (node_data node_data : s) {
			node_data.setTag(0);
			node_data.setWeight(Integer.MAX_VALUE);
			node_data.setInfo("");
		}
	}
	/**
	 * Dijkstra algorithm from source 
	 * @param src
	 */
	private void Dijksta(int src)
	{
		cleanTagsSetweight();
		ArrayList<node_data> Mins = new ArrayList<node_data>();
		Mins.add(g.getNode(src));
		Mins.get(0).setWeight(0);
		while(!Mins.isEmpty())
		{
			node_data currNode = Mins.get(0);
			if(currNode.getTag() ==0)
			{
				currNode.setTag(1);
				Mins.remove(0);
				Collection<edge_data> edges = g.getE(currNode.getKey());
				for (edge_data edge_data : edges) {
					node_data destNode = g.getNode(edge_data.getDest());
					if(destNode.getWeight() > currNode.getWeight()+edge_data.getWeight())
					{
						destNode.setWeight(currNode.getWeight()+edge_data.getWeight());
						destNode.setInfo(currNode.getKey()+"");
						if(destNode.getTag() == 0)
						{

							Mins.add(getPosInArray(Mins, destNode.getWeight()),destNode);
						}
					}
				}
			}
			else
			{
				Mins.remove(0);
			}
		}
	}
	/**
	 * get the insert position in sorted array
	 * @param Mins
	 * @param destNodeW
	 * @return
	 */
	private int getPosInArray(ArrayList<node_data> Mins,double destNodeW)
	{
		int minIndex = 0;
		int maxIndex = Mins.size()-1;
		int middle = minIndex + (maxIndex-minIndex)/2;

		while(minIndex <= maxIndex)
		{
			if(Mins.get(middle).getWeight() == destNodeW)
			{
				return middle;
			}
			if(destNodeW > Mins.get(middle).getWeight())
			{
				minIndex = middle +1;
			}
			if(destNodeW < Mins.get(middle).getWeight())
			{
				maxIndex = middle -1;
			}
			middle = minIndex+(maxIndex-minIndex)/2;
		}
		return middle;
	}

	@Override
	public double shortestPathDist(int src, int dest) {

		Dijksta(src);
		return g.getNode(dest).getWeight();
	}
	@Override
	public List<node_data> shortestPath(int src, int dest) {
		Dijksta(src);
		List<node_data> ans = new ArrayList<node_data>();
		node_data currNode = g.getNode(dest);
		while(!currNode.getInfo().isEmpty())
		{
			ans.add(0, currNode);
			currNode = g.getNode(Integer.parseInt(currNode.getInfo()));
		}
		ans.add(0, currNode);
		return ans;
	}
	/**
	 * Special shortest path for TSP algorithm
	 * @param src
	 * @param dest
	 * @param wasNotThere
	 * @return
	 */
	private List<node_data> shortestPathForTSP(int src, int dest,HashSet<Integer> wasNotThere) {
		List<node_data> ans = new ArrayList<node_data>();
		Dijksta(src);
		node_data currNode = g.getNode(dest);
		while(!currNode.getInfo().isEmpty())
		{
			ans.add(0, currNode);
			currNode = g.getNode(Integer.parseInt(currNode.getInfo()));
			if(wasNotThere.contains(currNode.getKey()))
			{
				wasNotThere.remove(currNode.getKey());
			}
		}
		ans.add(0, currNode);
		return ans;
	}
	
	@Override
	public List<node_data> TSP(List<Integer> targets)
	{
		List<node_data> ans = new ArrayList<node_data>();
		if(targets.size() == 0)
		{
			return ans;
		}
		if(targets.size() == 1)
		{
			ans.add(g.getNode(targets.get(0)));
			return ans;
		}
		HashSet<Integer> wasNotThere = new HashSet<Integer>();
		for (Integer integer : targets) {
			wasNotThere.add(integer);
		}
		int dest = 1;
		int from = 0;
		boolean firstRun = true;
		while(dest<targets.size())
		{
			if(wasNotThere.contains(targets.get(from)))
			{
				if(wasNotThere.contains(targets.get(dest)))
				{
					if(firstRun)
					{
						ans.addAll(shortestPathForTSP(targets.get(from), targets.get(dest),wasNotThere));
						firstRun=false;
					}
					else
					{
						List<node_data> pre =shortestPathForTSP(targets.get(from), targets.get(dest),wasNotThere);
						pre.remove(0);
						ans.addAll(pre);
					}
					dest++;
					from++;
				}
				else
				{
					dest++;
				}
			}
			else
			{
				from++;
			}
		}
		return ans;
	}
	@Override
	public graph copy() {
		DGraph newG = new DGraph((DGraph) g);
		return newG;
	}
}