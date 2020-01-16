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
import java.util.Set;

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
import dataStructure.Edge;
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
	final static double robotIconSize = .0015;
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

	public void GameManagement() {
		int w = manualOrAuto(); //0 for manual, 1 for auto
		if(w == 0) {
			InstructionForManual();
		}else if(w == -1) {
			return;
		}

		int s = -1;
		while(s == -1) {
			s = pickScenario();
			if(s == -1) 
				JOptionPane.showMessageDialog(null, "choose a valid scenario");
			else if (s == -2) return;
		}

		if(w == 0) {
			manual(s);
		}else {
			auto(s);
		}

		displayFinalScore(game);
	}

	private void manual(int s) {
		this.game = gametManualScenario(s);
		initFruits(game);
		initRobots(game);
		runManualScenario(game);

	}
	private void auto(int s) {
		this.game = gameAutoScenario(s);
		initFruits(game);
		initRobots(game);
		runAutoScenario(game);
	}

	private void runAutoScenario(game_service game) {
		game.startGame();
		while(game.isRunning()) {
			StdDraw.enableDoubleBuffering();

			refreshDraw();
			drawFruits(game);
			drawRobots(game);
			moveRobotsAuto(game);
			refreshElements(game);
			printScore(game);

			StdDraw.show();
		}

	}

	void moveRobotsAuto(game_service game) {

		List<String> log = game.move();
		if(log != null)
		{
			long t = game.timeToEnd();
			ArrayList<Robot> botsToMove = new ArrayList<Robot>();
			ArrayList<Fruit> fruitsWithoutBots = new ArrayList<Fruit>();
			Set<Integer> botS = robots.keySet();
			for (Integer integer : botS) {
				Robot b = robots.get(integer);
				if(b.getTrack() == null)
				{
					botsToMove.add(b);
				}
			}
			Set<Point3D> fruitSet = fruits.keySet();
			for (Point3D point3d : fruitSet) {
				Fruit currF = fruits.get(point3d);
				if(!currF.isTaken());
				{
					fruitsWithoutBots.add(currF);
				}
			}

			while(!botsToMove.isEmpty() && !fruitsWithoutBots.isEmpty())
			{
				int srcIndex = 0;
				Robot SrcFrom = null;
				Fruit DestTo = null;
				int destIndex = 0;
				double dist = Integer.MAX_VALUE;
				for(int i = 0; i < botsToMove.size(); i++)
				{
					//double distTemp = Integer.MAX_VALUE;
					for(int j = 0; j < fruitsWithoutBots.size(); j++)
					{
						double tmp = ga.shortestPathDist(botsToMove.get(i).getNodeKey(), fruitsWithoutBots.get(j).getEdge().getSrc()) + fruitsWithoutBots.get(j).getEdge().getWeight();
						if(tmp < dist)
						{
							srcIndex = i;
							destIndex = j;
							SrcFrom = botsToMove.get(i);
							DestTo = fruitsWithoutBots.get(j);
							dist = tmp;
						}
					}
				}
				List<node_data> path = ga.shortestPath(SrcFrom.getNodeKey(), DestTo.getEdge().getSrc());
				path.add(ga.g.getNode(DestTo.getEdge().getDest()));
				path.remove(0);
				SrcFrom.setTrack(path);
				botsToMove.remove(srcIndex);
				DestTo.setTaken(true);
				fruitsWithoutBots.remove(destIndex);
			}

			for (Integer integer : botS) {
				Robot b = robots.get(integer);
				if(b.getTrack() != null)
				{
					if(b.getNode().getLocation().distance2D(b.getLocation())<= 0.00001)
					{
						//System.out.println(botidtoMove);
						//System.out.println(b.getPos().toString());
						List<node_data> path = b.getTrack();
						//System.out.println(b.getId() + " " +path.get(0).getKey());
						game.chooseNextEdge(b.getId(), path.get(0).getKey());
						b.setNode(path.get(0));
						path.remove(0);
						if(path.size() == 0)
						{
							b.setTrack(null);
						}
						game.move();
					}
				}
			}


		}
//		Iterator<String> f_iter = game.getFruits().iterator();
//		fruits.clear();
//
//		while(f_iter.hasNext())
//		{
//			String json = f_iter.next();
//			Fruit n = new Fruit(ga.g);
//			n.initJson(json);
//			//System.out.println(n.getEdge().getSrc() + " " + n.getEdge().getDest());
//			fruits.put(n.getP(),n);
//		}
		initFruits(game);
		initRobots(game);

//		List<String> botsStr = game.getRobots();
//		for (String string : botsStr) {
//			Robot r = new Robot();
//			r.setG(ga.g);
//			r.initJson(string);
//
//			robots.put(r.getId(), r);
//		}

	}


	private List<node_data> nextNodeAuto(int src) {
		System.out.println("nextNode");
		Fruit f = new Fruit();
		double dist = Integer.MAX_VALUE;
		List <node_data> ans = null;
		for (Fruit t : fruits.values()) {//finding the closest fruit
			if(!t.isTaken()) {
				if(t.getType() == -1) {
					int startEdgeOfFruit = Math.max(t.getEdge().getSrc(), t.getEdge().getDest());
					double tmpDist = ga.shortestPathDist(src, startEdgeOfFruit);
					if(tmpDist < dist) {
						dist = tmpDist;
						ans = ga.shortestPath(src, startEdgeOfFruit);
						int finalNode = Math.min(t.getEdge().getSrc(), t.getEdge().getDest());
						ans.add(ga.g.getNode(finalNode));
						f = t;
					}
				}
				else {
					int startEdgeOfFruit = Math.min(t.getEdge().getSrc(), t.getEdge().getDest());
					double tmpDist = ga.shortestPathDist(src, startEdgeOfFruit);
					if(tmpDist < dist) {
						dist = tmpDist;
						ans = ga.shortestPath(src, startEdgeOfFruit);
						int finalNode = Math.max(t.getEdge().getSrc(), t.getEdge().getDest());
						ans.add(ga.g.getNode(finalNode));
						f = t;
					}
				}
			}
		}

		f.setTaken(true);
		return ans;
	}

	private game_service gameAutoScenario(int s) {
		game_service game = Game_Server.getServer(s); // you have [0,23] games
		String g = game.getGraph();


		this.ga.g.init(g);
		initFruits(game);
		initRobots(game);
		drawDGraph();

		String info = game.toString();
		JSONObject line;
		int rs = 0;
		try {
			line = new JSONObject(info);
			JSONObject ttt = line.getJSONObject("GameServer");
			rs = ttt.getInt("robots");
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}

		int i = 0;
		Collection<Fruit> f = fruits.values();
		for (Fruit fruit : f) {
			if(i >= rs)break;
			if(!fruit.isTaken()) {
				edge_data e = fruit.getEdge();
				
				if(fruit.getType() == -1) {
					game.addRobot(e.getDest());
				}else {
					game.addRobot(e.getSrc());
				}
				fruit.setTaken(true);
				i++;
			}
		}
		return game;
	}



	private int manualOrAuto() {
		Object[] options = {"Manual",
		"Auto"};
		int n = JOptionPane.showOptionDialog(null,
				"How would you like to play?",
				"Choose way of game",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,     //do not use a custom Icon
				options,  //the titles of buttons
				options[0]); //default button title
		return n;
	}

	private void InstructionForManual() {
		JPanel Instructions = new JPanel();
		JLabel info = new JLabel("<html>Hello player, in this manual game you can choose from 24 maps (0-23).<br>"
				+ "Some maps have one robot and some have more than one.<br>"
				+ "The purpose of the robot, is to eat as many fruits as possible.<br><br>" 
				+ "Banana - Bananas can be taken from high node to low node (number).<br>"
				+ "Apple - Apples can be taken from low node to high node(number).<br><br>"
				+ "Manual control of a single robot will be performed by left-clicking on the<br>"
				+ "neighboring node of the robot."
				+ "In the game where there is more than one robot, control is done by clicking the<br>"
				+ "key number of the robot on the keyboard, and than click on the neighboring nodes"
				+ "you wish to go.<br><br>"
				+ "Good luck!");

		JOptionPane.showInternalConfirmDialog(Instructions, 
				info, null, JOptionPane.DEFAULT_OPTION);

	}

	public game_service gametManualScenario(int s) {
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

			String[] nodes = new String[ga.g.nodeSize()];
			for (int i = 0; i < ga.g.nodeSize(); i++) {
				nodes[i] = "" + i;
			}
			for(int a = 0; a < rs ; a++) {

				String string = (String) JOptionPane.showInputDialog(
						null, "Pick node for robot " + a + "\n",				                   
						"Pick starting noeds",
						JOptionPane.PLAIN_MESSAGE,
						null, nodes,
						"ham");
				int node = 0;
				try {
					node = Integer.parseInt(string);
					src_node = node;
				}catch (Exception e) {
					e.getMessage();
				}
				game.addRobot(src_node);
				src_node = 0;
			}
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
		return game;
	}

	private void runManualScenario(game_service game) {
		game.startGame();
		while(game.isRunning()) {
			StdDraw.enableDoubleBuffering();

			refreshDraw();
			drawFruits(game);
			drawRobots(game);
			moveRobotsManual(game);
			refreshElements(game);
			printScore(game);

			StdDraw.show();
		}
	}


	void refreshElements(game_service game) {
		initFruits(game);
		initRobots(game);
	}


	void moveRobotsManual(game_service game) {
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
						if(game.getRobots().size() > 1) {
							rid = chooseRid();
							if(rid == -1)
								return;
							Robot r = robots.get(rid);
							System.out.println(rid);
							if(r.getNode() != null) {
								src = robots.get(rid).getNode().getKey();
							}
						}
						dest = nextNodeManual(ga.g,src);
						////////////////////

						game.chooseNextEdge(rid, dest);
						Robot r = robots.get(rid);
						r.setNode(ga.g.getNode(dest));
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
		if(StdDraw.pointOfMouse != null) {
			Point3D mouseClick = new Point3D(StdDraw.pointOfMouse);
			for (edge_data e: g.getE(src)) {
				if(mouseClick.distance2D(g.getNode(e.getDest()).getLocation()) < ourEPS)
					ans = e.getDest();
			}
		}
		return ans;
	}

	private int chooseRid() {
		char p = StdDraw.keyPress;
		int rid = -1;
		switch (p) {
		case '0':
			rid = 0;
			break;
		case '1':
			rid = 1;
			break;
		case '2':
			rid = 2;
			break;
		case '3':
			rid = 3;
			break;
		case '4':
			rid = 4;
			break;

		default:
		}

		System.out.println(rid);
		return rid;
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
					StdDraw.setPenRadius(0.03);
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

							StdDraw.setPenRadius(0.01);
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
			if(rid == 0) {
				file = "robot0.png";
			}
			else if(rid == 1) {
				file = "robot1.png";
			}
			else if(rid == 2) {
				file = "robot2.png";
			}
			else if(rid == 3) {
				file = "robot3.png";
			}
			else file = "robot4.png";
			try {
				StdDraw.picture(pos.x(), pos.y(), file, robotIconSize, robotIconSize);
			}catch (Exception e) {
				StdDraw.circle(pos.x(), pos.y(), robotIconSize * 0.3);
			}

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
			Fruit f = new Fruit(ga.g);
			f.initJson(json);
			edge_data e = f.getEdgeFruit();
			if(e != null) {
				f.setEdge(f.getEdgeFruit());
				fruits.put(f.getP(),f);
			}
			else {
				System.out.println("could not find edge to fruit");

			}

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
				r.setG(ga.g);
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
			String endGame = "Youre score is: " + scoreInt;

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