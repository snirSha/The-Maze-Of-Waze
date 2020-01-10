package gameClient;

import oop_elements.OOP_NodeData;

import oop_dataStructure.oop_edge_data;
import oop_dataStructure.oop_graph;
import oop_dataStructure.oop_node_data;
import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.json.JSONException;
import org.json.JSONObject;

import Server.Fruit;
import Server.Game_Server;
import Server.game_service;
import oop_dataStructure.OOP_DGraph;
import oop_utils.OOP_Point3D;
import oop_utils.OOP_Range;
/*
 * This class draw graphs using stdDraw
 *
 * @authors Snir and Omer 
 */ 
public class MyGameGUI{

	public OOP_DGraph g;



	/*
	 * Default constructor
	 */
	public MyGameGUI() {

		g = new OOP_DGraph();
		int s = -1;
		while(s == -1) {
			s = pickScenario();
			if(s == -1) JOptionPane.showMessageDialog(null, "choose a valid scenario");
			else if (s == -2) return;
		}
		runGameGUI(s);
	}
	public MyGameGUI(game_service game){
		this.g.init(game.getGraph());
	}






	/*
	 * Copy constructor using the init function from Graph_Algo class
	 */
	public MyGameGUI(oop_graph gg) {	
		g=(OOP_DGraph)gg;

		drawDGraph();
	}

	private int pickScenario() {

		JTextField SPDestField = new JTextField(5);

		JPanel SPEdgePanel = new JPanel();

		SPEdgePanel.add(new JLabel("scenario:"));
		SPEdgePanel.add(SPDestField);

		int SPEdgeRes = JOptionPane.showConfirmDialog(null, SPEdgePanel, 
				"Pick scenario (0 - 23)", JOptionPane.OK_CANCEL_OPTION);
		if (SPEdgeRes == JOptionPane.OK_OPTION) {

			try {

				int sce = Integer.parseInt(SPDestField.getText());
				if(sce <= 23 && sce >= 0) {
					return sce;
				}
				else return -1;

			}catch(Exception err) {
				JOptionPane.showMessageDialog(null, "Please enter valid number","Error",0);
			}
		}
		else return -2; //error happened or choose to cancel the game
		return -2;
	}

	private void runGameGUI(int s) {
		game_service game = Game_Server.getServer(s); // you have [0,23] games
		String g = game.getGraph();
		this.g.init(g);
		drawDGraph();

		String info = game.toString();
		JSONObject line;
		try {
			line = new JSONObject(info);
			JSONObject ttt = line.getJSONObject("GameServer");
			int rs = ttt.getInt("robots");
			System.out.println(info);
			System.out.println(g);
			// the list of fruits should be considered in your solution
			Iterator<String> f_iter = game.getFruits().iterator();
			while(f_iter.hasNext()) {
				System.out.println(f_iter.next());
			}	
			int src_node = 0;  // arbitrary node, you should start at one of the fruits
			for(int a = 0;a<rs;a++) {
				game.addRobot(src_node+a);
			}
		}
		catch (JSONException e) {e.printStackTrace();}
		game.startGame();
		while(game.isRunning()) {

			StdDraw.enableDoubleBuffering();

			refreshDraw();
			MyGameGUI.drawElements(game);
			MyGameGUI.drawRobot(game);
			SimpleGameClient.moveRobots(game, this.g);

			String results = game.toString();
			long t = game.timeToEnd();
			try {
				JSONObject score = new JSONObject(results);
				JSONObject ttt = score.getJSONObject("GameServer");
				int scoreInt = ttt.getInt("grade");
				//System.out.println("omer" + rid);
				//System.out.println("Score: " + scoreInt + "\nTime: " + t / 1000);
				String toScreen = "Score: " + scoreInt + "\nTime: " + t / 1000;

				final double xMax = 35.1516;
				final double xMin = 35.1335;
				final double yMax = 32.15;
				final double yMin = 32.1;

				StdDraw.setPenRadius(0.1);
				StdDraw.setPenColor(Color.BLACK);

				StdDraw.text(xMax , yMax, toScreen);
				
			}catch (Exception e) {
				System.out.println("Failed to print score");
			}

			StdDraw.show();
		}
		try {
			String results = game.toString();
			JSONObject score = new JSONObject(results);
			System.out.println("Game Over: "+results);
		}catch (Exception e) {
			// TODO: handle exception
		}


	}

	/*
	 * Add a node to the drawing using addNode function from DGraph
	 */
	public void addNode(OOP_NodeData a) {
		this.g.addNode(a);

	}


	/*
	 * Draw the nodes
	 * @param x = the x of the node location (point) 
	 * @param y = the y of the node location (point)
	 * @param abs = the number of the node
	 */
	public void drawNodes() {
		try {
			Collection<oop_node_data> n=g.getV();
			if(n != null && n.size() > 0) {
				for (oop_node_data a:n) {
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
			Collection<oop_node_data> allNodes=g.getV();
			if(allNodes != null && allNodes.size() > 0) {
				for(oop_node_data n:allNodes) {
					Collection<oop_edge_data> allEdgesOfNode=g.getE(n.getKey());
					if(allEdgesOfNode != null && allEdgesOfNode.size() > 0) {
						for(oop_edge_data edges:allEdgesOfNode) {
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

							double tmp = edges.getWeight();
							String dou = String.format("%.4g%n", tmp);

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

	public void refreshDraw() {
		StdDraw.clear();
		drawEdges();
		drawNodes();
	}

	private void setPageSize() {
		final double xMax = 35.216;
		final double xMin = 35.1835;
		final double yMax = 32.11;
		final double yMin = 32.1;
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
		g = new OOP_DGraph();
	}

	public static void drawRobot(game_service game) {


		List<String> log = game.move();
		if(log!=null) {

			for(int i=0;i<log.size();i++) {
				String robot_json = log.get(i);
				try {
					JSONObject line = new JSONObject(robot_json);
					JSONObject ttt = line.getJSONObject("Robot");
					int rid = ttt.getInt("id");
					String pos = ttt.getString("pos");
					OOP_Point3D posP = new OOP_Point3D(pos);
					StdDraw.picture(posP.x(), posP.y(), "robot.jpg", 0.0007, 0.0007);

				} 
				catch (JSONException e) {e.printStackTrace();}
			}
		}
	}


	public static void drawElements(game_service game) {

		List<String> eStrList = new ArrayList<>();
		eStrList = game.getFruits();

		for(int i=0;i<eStrList.size();i++) {
			String fruit_json = eStrList.get(i);
			try {
				JSONObject line = new JSONObject(fruit_json);
				JSONObject ttt = line.getJSONObject("Fruit");
				double value = ttt.getDouble("value");
				int type = ttt.getInt("type");
				String pos = ttt.getString("pos");
				OOP_Point3D posP = new OOP_Point3D(pos);
				if(type == -1) {
					StdDraw.picture(posP.x(), posP.y(), "banana.jpg", 0.0007, 0.0007);
				}else if(type == 1) {
					StdDraw.picture(posP.x(), posP.y(), "apple.jpg", 0.0007, 0.0007);
				}

			} 
			catch (JSONException e) {e.printStackTrace();}
		}

	}



}