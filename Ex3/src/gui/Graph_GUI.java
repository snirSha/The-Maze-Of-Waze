package gui;

import elements.NodeData;
import dataStructure.edge_data;
import dataStructure.graph;
import dataStructure.node_data;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import dataStructure.DGraph;
import utils.Point3D;
import utils.StdDraw;
/*
 * This class draw graphs using stdDraw
 *
 * @authors Snir and Omer 
 */ 
public class Graph_GUI{

	public DGraph g;
	private boolean isDraw;


	/*
	 * Default constructor
	 */
	public Graph_GUI() {
		
		g=new DGraph();
		isDraw = false;
	}

	/*
	 * Copy constructor using the init function from Graph_Algo class
	 */
	public Graph_GUI(graph gg) {	
		g=(DGraph)gg;
		isDraw = false;
	}

	/*
	 * Add a node to the drawing using addNode function from DGraph
	 */
	public void addNode(NodeData a) {
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
			Collection<node_data> n=g.getV();
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
			Collection<node_data> allNodes=g.getV();
			if(allNodes != null && allNodes.size() > 0) {
				for(node_data n:allNodes) {
					Collection<edge_data> allEdgesOfNode=g.getE(n.getKey());
					if(allEdgesOfNode != null && allEdgesOfNode.size() > 0) {
						for(edge_data edges:allEdgesOfNode) {
							double Sx = g.getNode(edges.getSrc()).getLocation().x();
							double Sy = g.getNode(edges.getSrc()).getLocation().y();
							double Dx = g.getNode(edges.getDest()).getLocation().x();
							double Dy = g.getNode(edges.getDest()).getLocation().y();

							StdDraw.setPenRadius(0.005);
							StdDraw.setPenColor(StdDraw.ORANGE);//paint the line between the nodes in orange
							StdDraw.line(Sx,Sy,Dx,Dy);

							StdDraw.setPenRadius(0.02);
							StdDraw.setPenColor(StdDraw.RED);

							double arrowX= (Dx*8+Sx)/9;
							double arrowY= (Dy*8+Sy)/9;
							StdDraw.point(arrowX,arrowY);

							String dou = String.format("%.5g%n", edges.getWeight());

							String te = dou+"";

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
		g.removeNode(x);
	}
	
	/*
	 * Remove edge from the drawing using removeEdge from DGraph class 
	 */
	public void removeEdge(int x,int y) {
		g.removeEdge(x,y);
	}

	/*
	 * This function open a window and calls to drawNode and drawEdge
	 */
	public void drawDGraph() {
		isDraw = true;
		try {
			if(g.getV() != null) {
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
		double xMax = 35.216;
		double xMin = 35.1835;
		double yMax = 32.11;
		double yMin = 32.1;
//		Collection <node_data> col = g.getV();
//		if(col != null && col.size() > 0) {
//			for(node_data nd: col) {
//				NodeData n = (NodeData)nd;
//				if(n.getLocation().x() > xMax) xMax = n.getLocation().x();
//				else if (n.getLocation().x() < xMin) xMin = n.getLocation().x();
//				if(n.getLocation().y() > yMax) yMax = n.getLocation().y();
//				else if (n.getLocation().y() < yMin) yMin = n.getLocation().y();
//			}
//			
//			int xCanvas = 3 * (int)(Math.abs(xMax) + Math.abs(xMin));
//			int yCanvas = 3 * (int)(Math.abs(yMax) + Math.abs(yMin));
//			
			StdDraw.setCanvasSize(1200 , 600 );
			StdDraw.setXscale(xMin, xMax);
			StdDraw.setYscale(yMin, yMax);
//		}else {
//			StdDraw.setCanvasSize(1000, 800);
//			StdDraw.setXscale(-100,100);
//			StdDraw.setYscale(-100,100);
//		}	
		
	}

	/*
	 * Delete the graph in the drawing
	 */
	public void deleteGraph() {
		StdDraw.clear();
		g = new DGraph();
	}
	
	

}