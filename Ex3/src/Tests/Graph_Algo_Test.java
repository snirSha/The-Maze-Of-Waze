package Tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;

import algorithms.Graph_Algo;
import dataStructure.DGraph;
import dataStructure.Edge;
import dataStructure.Node;
import dataStructure.edge_data;
import dataStructure.graph;
import dataStructure.node_data;
import gui.Graph_GUI;
import utils.Point3D;

class Graph_Algo_Test {

	@Test
	void initTest() {
		Graph_Algo ga = new Graph_Algo();
		DGraph dg = new DGraph();
		ga.init(dg);
		boolean ans = ga.g == dg;
		assertEquals(true, ans);
	}
	/** 
	 * Compute a deep copy of this graph.
	 * @return
	 */
	@Test
	void copyTest() {
		Graph_Algo ga = new Graph_Algo();
		ga.g.addNode(new Node());
		ga.g.addNode(new Node(1,new Point3D(5, 5), 2, "", 0));
		ga.g.connect(0, 1, 2);
		graph ga2 =new DGraph();
		ga2 =ga.copy();
		Collection<edge_data> cl = ga2.getE(0);
		for(edge_data e: cl) {
			Edge ed = (Edge)e;
			assertEquals(true, ed.getSrc() == 0 && ed.getDest() == 1);
		}
	}
	/**
	 * Init a graph from file
	 * @param file_name
	 */
	@Test
	void initAndSaveFromFileTest() {
		Graph_Algo ga = new Graph_Algo();
		ga.g.addNode(new Node(4,new Point3D(15, 15), 3.5, "cz", 0));
		ga.g.addNode(new Node(5,new Point3D(5, 5), 2, "asd", 0));
		ga.g.connect(4, 5, 2);
		ga.save("JunitTest");
		Graph_Algo ga2 = new Graph_Algo();
		ga2.init("JunitTest");
		Collection<edge_data> cl = ga2.g.getE(0);
		for(edge_data e: cl) {
			Edge ed = (Edge)e;
			System.out.println(ed.getDest());
			assertEquals(true, ed.getSrc() == 4 && ed.getDest() == 5);
		}
	}

	/**
	 * Returns true if and only if (iff) there is a valid path from EVREY node to each
	 * other node. NOTE: assume directional graph - a valid path (a-->b) does NOT imply a valid path (b-->a).
	 * @return
	 */
	@Test
	void isConnectedTest() {
		Graph_Algo ga = new Graph_Algo();
		for(int i=0;i<11;i++) {//0 1 2 3 4 5 6 7 8 9 10
			ga.g.addNode(new Node(i,new Point3D(i, i), 0, "cz", 0));
		}
		for(int j=0;j<10;j++) {
			ga.g.connect(j,j+1 , 2);
		}
		assertEquals(false,ga.isConnected());
		ga.g.connect(10, 0, 50);
		assertEquals(true,ga.isConnected());
	}
	/**
	 * returns the length of the shortest path between src to dest
	 * @param src - start node
	 * @param dest - end (target) node
	 * @return
	 */
	@Test
	void shortestPathDistTest() {
		Graph_Algo ga = new Graph_Algo();
		ga.g.addNode(new Node(0,new Point3D(-50, -50), 0, "snir", 0));
		ga.g.addNode(new Node(1,new Point3D(50, 50), 0, "rotze", 0));
		ga.g.addNode(new Node(2,new Point3D(50, -50), 0, "shetishvy", 0));
		ga.g.addNode(new Node(3,new Point3D(-50, 50), 0, "lo", 0));
		ga.g.addNode(new Node(4,new Point3D(0, -75), 0, "hal", 0));
		ga.g.addNode(new Node(5,new Point3D(0, 75), 0, "hapanim", 0));
		ga.g.addNode(new Node(6,new Point3D(0, 0), 0, "ya shramu&*^^%#@", 0));
		ga.g.connect(0,3, 2);
		ga.g.connect(3,5, 1);
		ga.g.connect(5,1, 5);
		ga.g.connect(1,2, 0);
		ga.g.connect(2,4, 6);
		ga.g.connect(4,0, 3);
		ga.g.connect(6,4, 4);
		ga.g.connect(6,5, 2.5);
		
		assertEquals(ga.shortestPathDist(0, 2),8);
		assertEquals(ga.shortestPathDist(6, 2),7.5);
		assertEquals(ga.shortestPathDist(6, 3),9);
		assertEquals(ga.shortestPathDist(0, 6),-1);//Error: can't get to node 6
	}
	/**
	 * returns the the shortest path between src to dest - as an ordered List of nodes:
	 * src--> n1-->n2-->...dest
	 * see: https://en.wikipedia.org/wiki/Shortest_path_problem
	 * @param src - start node
	 * @param dest - end (target) node
	 * @return
	 */
	@Test
	void shortestPathTest() {
		Graph_Algo ga = new Graph_Algo();
		ga.g.addNode(new Node(0,new Point3D(0, 0), 0, "ba", 0));
		ga.g.addNode(new Node(1,new Point3D(-50,-50), 0, "li", 0));
		ga.g.addNode(new Node(2,new Point3D(0,75), 0, "lamut", 0));
		ga.g.addNode(new Node(3,new Point3D(50,50), 0, "mehazaeen", 0));
		ga.g.addNode(new Node(4,new Point3D(50,-50), 0, "hakatan", 0));
		ga.g.addNode(new Node(5,new Point3D(0,-75), 0, "haze", 0));
		ga.g.addNode(new Node(6,new Point3D(-50,50), 0, "sheli", 0));
		ga.g.connect(0,1, 1);
		ga.g.connect(0,2, 2);
		ga.g.connect(0,3, 3);
		ga.g.connect(0,4, 4);
		ga.g.connect(0,5, 5);
		ga.g.connect(0,6, 6);
		ga.g.connect(1,0, 6);
		ga.g.connect(2,0, 5);
		ga.g.connect(3,0, 4);
		ga.g.connect(4,0, 3);
		ga.g.connect(5,0, 2);
		ga.g.connect(6,0, 1);
		ga.g.connect(6,1, 0);
		ga.g.connect(1,2, 0);
		ga.g.connect(2,3, 0);
		ga.g.connect(3,4, 0);
		ga.g.connect(4,5, 0);
		ga.g.connect(5,6, 0);

		List<node_data> ans=new ArrayList<>();
		ans.add(ga.g.getNode(6));
		ans.add(ga.g.getNode(1));
		ans.add(ga.g.getNode(2));
		List<node_data> ans2=ga.shortestPath(6, 2);
		assertEquals(ans,ans2);
	
		ans.clear();
		List<node_data> ans3=ga.shortestPath(0, 4);	
		ans.add(ga.g.getNode(0));
		ans.add(ga.g.getNode(1));
		ans.add(ga.g.getNode(2));
		ans.add(ga.g.getNode(3));
		ans.add(ga.g.getNode(4));
		assertEquals(ans,ans3);
	}
	/**
	 * computes a relatively short path which visit each node in the targets List.
	 * Note: this is NOT the classical traveling salesman problem, 
	 * as you can visit a node more than once, and there is no need to return to source node - 
	 * just a simple path going over all nodes in the list. 
	 * @param targets
	 * @return
	 */
	@Test
	void TSPTest() {
		Graph_Algo ga = new Graph_Algo();
		ga.g.addNode(new Node(0,new Point3D(0, 0), 0, "ra", 0));
		ga.g.addNode(new Node(1,new Point3D(-10,-10), 0, "li", 0));
		ga.g.addNode(new Node(2,new Point3D(10,10), 0, "ra", 0));
		ga.g.addNode(new Node(3,new Point3D(-10,10), 0, "li", 0));
		ga.g.addNode(new Node(4,new Point3D(0,-20), 0, "ra", 0));
		ga.g.addNode(new Node(5,new Point3D(10, -10), 0, "li", 0));

		ga.g.connect(0,1, 1);
		ga.g.connect(1,0, 1.5);
		ga.g.connect(0,2, 0);
		ga.g.connect(2,0, 2);
		ga.g.connect(0,3, 5);
		ga.g.connect(3,0, 4);
		ga.g.connect(0,4, 2);
		ga.g.connect(4,0, 3);
		ga.g.connect(0,5, 1.2);
		ga.g.connect(5,0, 2.5);

		List<Integer> targets=new ArrayList<>();
		targets.add(1);
		targets.add(2);
		targets.add(3);
		targets.add(4);
		targets.add(5);

		List<node_data> ans=ga.TSP(targets);
		List<node_data> myAns=new ArrayList<>();
		myAns.add(ga.g.getNode(1));
		myAns.add(ga.g.getNode(0));
		myAns.add(ga.g.getNode(2));
		myAns.add(ga.g.getNode(0));
		myAns.add(ga.g.getNode(3));
		myAns.add(ga.g.getNode(0));
		myAns.add(ga.g.getNode(4));
		myAns.add(ga.g.getNode(0));
		myAns.add(ga.g.getNode(5));
		System.out.println(myAns+"\n"+ans);
		assertEquals(myAns,ans);
		
		ga.g.removeNode(0);
		ans=ga.TSP(targets);
		assertEquals(ans,null);//Error: deleted all edges, there is no path between the nodes
	}

}
