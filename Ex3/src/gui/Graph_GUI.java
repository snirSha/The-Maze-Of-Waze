package gui;

import dataStructure.Node;
import dataStructure.edge_data;
import dataStructure.graph;
import dataStructure.node_data;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import algorithms.Graph_Algo;
import dataStructure.DGraph;
import utils.Point3D;
import utils.StdDraw;
/*
 * This class draw graphs using stdDraw
 *
 * @authors Snir and Omer 
 */ 
public class Graph_GUI{


	public Graph_Algo ga;
	private boolean isDraw;


	/*
	 * Default constructor
	 */
	public Graph_GUI() {
		
		ga = new Graph_Algo();
		isDraw = false;
	}

	/*
	 * Copy constructor using the init function from Graph_Algo class
	 */
	public Graph_GUI(graph g) {	
		
		this.ga = new Graph_Algo();
		ga.init(g);
		isDraw = false;
	}

	/*
	 * Add a node to the drawing using addNode function from DGraph
	 */
	public void addNode(Node a) {
		ga.g.addNode(a);
		if(isDraw) {
			drawDGraph();
		}
	}
	

	/*
	 * Draw the nodes
	 * @param x = the x of the node location (point) 
	 * @param y = the y of the node location (point)
	 * @param abs = the number of the node
	 */
	public void drawNodes() {
		try {
			Collection<node_data> n=ga.g.getV();
			if(n != null && n.size() > 0) {
				for (node_data a:n) {
					double x=a.getLocation().x();
					double y=a.getLocation().y();
					StdDraw.setPenRadius(0.05);
					StdDraw.setPenColor(StdDraw.BLUE);//nodes in blue
					StdDraw.point(x,y);
					StdDraw.setPenColor(StdDraw.BLACK);
					String abs = a.getKey()+"";
					StdDraw.text(x,y,abs);
				}
			}
			
		}catch(Exception e) {
			System.out.println("No nodes to draw");
		}
	}

	/*
	 * Draw the edges
	 * @param allNodes = A collection of all the nodes
	 * @param allEdgesOfNode = A collection of all the edges that came out of the parameter's node
	 * @param Sx = the x of the source location
	 * @param Sy = the y of the source location
	 * @param Dx = the x of the destination location
	 * @param Dy = the y of the destination location
	 * @param arrowX = the x of the "arrow" point location
	 * @param arrowY = the y of the "arrow" point location
	 * @param te = the string of the weight number
	 */
	public void drawEdges() {
		try {
			Collection<node_data> allNodes=ga.g.getV();
			if(allNodes != null && allNodes.size() > 0) {
				for(node_data n:allNodes) {
					Collection<edge_data> allEdgesOfNode=ga.g.getE(n.getKey());
					if(allEdgesOfNode != null && allEdgesOfNode.size() > 0) {
						for(edge_data edges:allEdgesOfNode) {
							double Sx = ga.g.getNode(edges.getSrc()).getLocation().x();
							double Sy = ga.g.getNode(edges.getSrc()).getLocation().y();
							double Dx = ga.g.getNode(edges.getDest()).getLocation().x();
							double Dy = ga.g.getNode(edges.getDest()).getLocation().y();

							StdDraw.setPenRadius(0.005);
							StdDraw.setPenColor(StdDraw.ORANGE);//paint the line between the nodes in orange
							StdDraw.line(Sx,Sy,Dx,Dy);

							StdDraw.setPenRadius(0.02);
							StdDraw.setPenColor(StdDraw.RED);

							double arrowX= (Dx*8+Sx)/9;
							double arrowY= (Dy*8+Sy)/9;
							StdDraw.point(arrowX,arrowY);

							String te = edges.getWeight()+"";

							StdDraw.setPenRadius(0.1);
							StdDraw.setPenColor(Color.BLACK);

							double newX= (Dx*4+Sx)/5;
							double newY= (Dy*4+Sy)/5;

							StdDraw.text(newX, newY, te);
						}
					}
				}
			}

		}catch(Exception e) {
			System.out.println("No edges to Draw");
		}
	}


	/*
	 * Remove node from the drawing using removeNode from DGraph class
	 */
	public void removeNode(int x) {
		ga.g.removeNode(x);
	}
	
	/*
	 * Remove edge from the drawing using removeEdge from DGraph class 
	 */
	public void removeEdge(int x,int y) {
		ga.g.removeEdge(x,y);
	}

	/*
	 * Reverse the graph using reverseGraph in DGraph class
	 */
	public void reversedGraph() {
		ga.g.reversedGraph();
	}

	/*
	 * This function open a window and calls to drawNode and drawEdge
	 */
	public void drawDGraph() {
		isDraw = true;
		try {
			if(ga.g.getV() != null) {
				StdDraw.setGui(this);
				setPageSize();
				drawEdges();
				drawNodes();
			}
		}catch(Exception e){
			System.out.println("Nothing to draw");
		}
	}
	
	private void setPageSize() {
		double xMax = 0;
		double xMin = 0;
		double yMax = 0;
		double yMin = 0;
		Collection <node_data> col = ga.g.getV();
		if(col != null && col.size() > 0) {
			for(node_data nd: col) {
				Node n = (Node)nd;
				if(n.getLocation().x() > xMax) xMax = n.getLocation().x();
				else if (n.getLocation().x() < xMin) xMin = n.getLocation().x();
				if(n.getLocation().y() > yMax) yMax = n.getLocation().y();
				else if (n.getLocation().y() < yMin) yMin = n.getLocation().y();
			}
			
			int xCanvas = 3 * (int)(Math.abs(xMax) + Math.abs(xMin));
			int yCanvas = 3 * (int)(Math.abs(yMax) + Math.abs(yMin));
			
			StdDraw.setCanvasSize(xCanvas , yCanvas );
			StdDraw.setXscale(xMin - 10, xMax + 10);
			StdDraw.setYscale(yMin - 10, yMax + 10);
		}else {
			StdDraw.setCanvasSize(1000, 800);
			StdDraw.setXscale(-100,100);
			StdDraw.setYscale(-100,100);
		}
		
		
	}

	/*
	 * Delete the graph in the drawing
	 */
	public void deleteGraph() {
		StdDraw.clear();
		ga.g = new DGraph();
	}
	
	public static void main (String [] args) {
		Graph_GUI gg = new Graph_GUI();
		Point3D p0=new Point3D(-50,50);
		Point3D p1=new Point3D(50,50);
		Point3D p2=new Point3D(0,0);
		Point3D p3=new Point3D(0,-50);

		Node a=new Node(0,p0 ,0, "", 0);
		Node b=new Node(1,p1, 0, "", 0);
		Node c=new Node(2,p2, 0, "", 0);
		Node d=new Node(3,p3, 0, "", 0);
		Node e=new Node(4, new Point3D(100, 100), 0, "", 0);


		gg.addNode(a);
		gg.addNode(b);
		gg.addNode(c);
		gg.addNode(d);

		gg.ga.g.connect(c.getKey(),a.getKey(), 1);
		gg.ga.g.connect(c.getKey(),d.getKey(),2);
		gg.ga.g.connect(c.getKey(),b.getKey(), 3);
		gg.ga.g.connect(a.getKey(), c.getKey(), 2.5);
		gg.ga.g.connect(b.getKey(), c.getKey(), 1.5);
		gg.ga.g.connect(d.getKey(), c.getKey(), 3);

		
		gg.drawDGraph();
		


	}

}