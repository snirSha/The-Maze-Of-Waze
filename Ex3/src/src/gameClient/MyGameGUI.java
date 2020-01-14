package gameClient;

import java.util.Iterator;
import dataStructure.Node;
import dataStructure.Robot;
import dataStructure.edge_data;
import dataStructure.graph;
import dataStructure.node_data;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.json.JSONException;
import org.json.JSONObject;

import Server.Game_Server;
import Server.game_service;
import algorithms.Graph_Algo;
import dataStructure.DGraph;
import dataStructure.Fruit;
import utils.Point3D;
import utils.StdDraw;
/*
 * This class draw graphs using stdDraw
 *
 * @authors Snir and Omer 
 */ 
public class MyGameGUI{
	public Graph_Algo ga;
	final double xMax = 35.216;
	final double xMin = 35.1835;
	final double yMax = 32.11;
	final double yMin = 32.1;
	final static double robotIconSize = .0007;
	final static double ourEPS = 0.0002;
	final static double minEPS = 0.00001;
	static int rid = -1;

	HashMap<Integer, Robot> robots;
	HashMap<Point3D, Fruit> fruits;
	game_service game;

	/*
	 * Default constructor
	 */
	public MyGameGUI() {
		ga = new Graph_Algo();
		this.robots = new HashMap<>();
		this.fruits = new HashMap<>();
		GameManagement();
		
	}

	/*
	 * Copy constructor using the init function from Graph_Algo class
	 */
	public MyGameGUI(graph g) {

		this.ga = new Graph_Algo();
		ga.init(g);
	}
/*
	private void play() {
		int scenario_num = -1;
		while(scenario_num == -1) {
			scenario_num = pickScenario();
			if(scenario_num == -1) JOptionPane.showMessageDialog(null, "choose valid scenario");
			else if (scenario_num == -2) return;
		}
		game = Game_Server.getServer(scenario_num); // you have [0,23] games
		
		String jsonSTR = game.getGraph();
		//this.ga.g = new DGraph();
		this.ga.g.init(jsonSTR);
		drawDGraph();
		game.startGame();
		System.out.println("asd");
		while(game.isRunning()) {
			
			StdDraw.enableDoubleBuffering();
			refreshDraw();
			drawFruits();
			drawRobots();
			moveRobots(game);
			StdDraw.show();
		}
	}
	*/
	
	public void GameManagement() {
		int s = -1;
		while(s == -1) {
			s = pickScenario();
			if(s == -1) 
				JOptionPane.showMessageDialog(null, "choose a valid scenario");
			else if (s == -2) return;
		}
		this.game = startScenario(s);
		initFruits(game);
		initRobots(game);
		runManualScenario(game);
		displayFinalScore(game);
	}

	public game_service startScenario(int s) {
		game_service game = Game_Server.getServer(s); // you have [0,23] games
		String g = game.getGraph();
		this.ga.g.init(g);
		drawDGraph();
		String info = game.toString();
		JSONObject line;
		try {
			line = new JSONObject(info);
			JSONObject ttt = line.getJSONObject("GameServer");
			int rs = ttt.getInt("robots");
			int src_node = 0;  
			for(int a = 0;a<rs;a++) {
				game.addRobot(src_node+a);
			}
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
		return game;
	}
	
	private void runManualScenario(game_service game) {
		int x = 0;//We in MyGameGUI (manual game)
		game.startGame();
		while(game.isRunning()) {
			StdDraw.enableDoubleBuffering();

			refreshDraw();
			drawFruits(game);
			drawRobots(game);
			moveRobots(game);
			refreshElements(game);
			printScore(game);
			
			StdDraw.show();
		}
	}
	
	void refreshElements(game_service game) {
		fruits.clear();
		initFruits(game);
		robots.clear();
		initRobots(game);
	}
	
	
	void moveRobots(game_service game) {
		List<String> log = game.move();
		if(log != null) {
			long t = game.timeToEnd();
			for(int i = 0; i < log.size(); i++) {
				String robot_json = log.get(i);
				try {
					JSONObject line = new JSONObject(robot_json);
					JSONObject ttt = line.getJSONObject("Robot");
					int rid = ttt.getInt("id");
					int src = ttt.getInt("src");
					int dest = ttt.getInt("dest");
				
					if(dest == -1) {
						/* snir's shit */
						dest = nextNodeManual(ga.g,src);
						////////////////////
						game.chooseNextEdge(rid, dest);
						System.out.println("Turn to node: " + dest + "  time to end:" + (t / 1000));
						System.out.println(ttt);
					}
				} 
				catch (JSONException e) {e.printStackTrace();}
			}
		}
	}
	/**
	 * a very simple random walk implementation!
	 * @param g
	 * @param src
	 * @return
	 */
	/*snir's shit*/
	private static int nextNodeManual(graph g, int src) {//The manual moves
		int ans = -1;
		if(StdDraw.pointOfMouse!=null) {
			Point3D mouseClick = new Point3D(StdDraw.pointOfMouse);
			for (edge_data e:g.getE(src)) {
				if(mouseClick.distance2D(g.getNode(e.getDest()).getLocation()) < ourEPS)
					ans = e.getDest();
			}
		}
		return ans;
	}



	/*
	 * Add a node to the drawing using addNode function from DGraph
	 */
	public void addNode(Node a) {
		ga.g.addNode(a);
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
			Collection<node_data> allNodes = ga.g.getV();
			if(allNodes != null && allNodes.size() > 0) {
				for(node_data n:allNodes) {
					Collection<edge_data> allEdgesOfNode = ga.g.getE(n.getKey());
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
		final double fixScale = 0.0015;
		StdDraw.setCanvasSize(1200 , 600 );
		StdDraw.setXscale(xMin - fixScale, xMax + fixScale);
		StdDraw.setYscale(yMin - fixScale, yMax + fixScale);
	}

	public void refreshDraw() {
		StdDraw.clear();
		drawEdges();
		drawNodes();
	}

	/*
	 * Delete the graph in the drawing
	 */
	public void deleteGraph() {
		StdDraw.clear();
		ga.g = new DGraph();
	}
	/*new staff*/
	public int pickScenario() {
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

	public void drawFruits(game_service game) {

		Collection<Fruit> f_list = fruits.values();
		for (Fruit fruit : f_list) {
			int type = fruit.getType();
			Point3D pos = new Point3D(fruit.getP());
			if(type == -1) {
				StdDraw.picture(pos.x(), pos.y(), "banana.png", 0.0007, 0.0007);
			}else if(type == 1) {
				StdDraw.picture(pos.x(), pos.y(), "apple.png", 0.0007, 0.0007);
			}
		}
	}

	public void drawRobots(game_service game) {

		Collection<Robot> r_list = robots.values();
		for (Robot robot : r_list) {
			int rid = robot.getId();
			Point3D pos = new Point3D(robot.getLocation());
			String file = "";
			if(rid == 1) {
				file = "robot1.png";
			}
			else if(rid == 2) {
				file = "robot2.png";
			}
			else file = "robot3.png";

			StdDraw.picture(pos.x(), pos.y(), file, robotIconSize, robotIconSize);
		}
	}

	public void initFruits(game_service game) {
		List<String> f_list = game.getFruits();
		if(fruits != null)
		{
			fruits.clear();
		}
		else fruits = new HashMap<>();

		for (String string : f_list) {
			String json = string;
			Fruit f = new Fruit(this.ga.g);
			f.initJson(json);
			fruits.put(f.getP(),f);
		}
	}

	public void initRobots(game_service game) {
		List<String> r_list = game.getRobots();
		if(robots != null)
		{
			robots.clear();
		}
		else robots = new HashMap<>();
		try {
			for (String json : r_list) {
				Robot r = new Robot();
				r.initJson(json);
				robots.put(r.getId(),r);
			}
		}catch (Exception e) {

		}

	}

	public void displayFinalScore(game_service game){
		int scoreInt = 0;
		try {
			String results = game.toString();
			System.out.println("Game Over: "+results);

			JSONObject score = new JSONObject(results);
			JSONObject ttt = score.getJSONObject("GameServer");
			scoreInt = ttt.getInt("grade");
			String endGame="Youre score is: "+scoreInt;

			JOptionPane.showMessageDialog(null, endGame);
		}
		catch (Exception e) {
			e.getMessage();
		}
	}


	public void printScore(game_service game) {
		String results = game.toString();
		long t = game.timeToEnd();
		try {
			int scoreInt=0;
			JSONObject score = new JSONObject(results);
			JSONObject ttt = score.getJSONObject("GameServer");
			scoreInt = ttt.getInt("grade");

			String countDown = "Time: " + t / 1000 + "." + t % 1000;
			String scoreStr = "Score: " + scoreInt;
			double tmp1 = xMax - xMin;
			double tmp2 = yMax - yMin;

			StdDraw.setPenRadius(0.05);
			StdDraw.setPenColor(Color.BLACK);
			StdDraw.text(xMin+tmp1 / 1.05 , yMin + tmp2 / 0.95, countDown);
			StdDraw.text(xMin+tmp1 / 1.05 , yMin + tmp2, scoreStr);

		}catch (Exception e) {
			System.out.println("Failed to print score");
		}
	}



}