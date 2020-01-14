package gameClient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import Server.Game_Server;
import Server.game_service;
import algorithms.Graph_Algo;
import dataStructure.DGraph;
import dataStructure.edge_data;
import dataStructure.graph;
import dataStructure.node_data;
import oop_utils.OOP_Point3D;
/**
 * This class represents a simple example for using the GameServer API:
 * the main file performs the following tasks:
 * 1. Creates a game_service [0,23] (line 36)
 * 2. Constructs the graph from JSON String (lines 37-39)
 * 3. Gets the scenario JSON String (lines 40-41)
 * 4. Prints the fruits data (lines 49-50)
 * 5. Add a set of robots (line 52-53) // note: in general a list of robots should be added
 * 6. Starts game (line 57)
 * 7. Main loop (should be a thread) (lines 59-60)
 * 8. move the robot along the current edge (line 74)
 * 9. direct to the next edge (if on a node) (line 87-88)
 * 10. prints the game results (after "game over"): (line 63)
 *  
 * @author boaz.benmoshe
 *
 */
public class SimpleGameClient {
	public static final double ourEPS=0.0002;
	public static final double minEPS=0.00001;

	public static void main(String[] a) {
		MyGameGUI mgg = new MyGameGUI();//manual game
				mgg.manualGameManagement();

		//AutomaticGame ag=new AutomaticGame();//automatic

		//ag.mgg.g.connect(14, 13, 90);
		//ag.automaticGameManagement();


		//		game_service game = Game_Server.getServer(scenario_num); // you have [0,23] games
		//		List<String> listF = game.getFruits();
		//		test1();

	}

	public static void test1() {
		int scenario_num = 6;
		game_service game = Game_Server.getServer(scenario_num); // you have [0,23] games
		String g = game.getGraph();
		DGraph gg = new DGraph();
		gg.init(g);

		/*our sh*t*/
		MyGameGUI guiava = new MyGameGUI(gg);
		/*done our sh*t*/

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
		int x=0;
		// should be a Thread!!!
		while(game.isRunning()) {

			StdDraw.enableDoubleBuffering();
			guiava.refreshDraw();
			MyGameGUI.drawElements(game);
			MyGameGUI.drawRobot(game);
			locateRobots(game, x);
			moveRobots(game, gg,x);

			StdDraw.show();
		}
		String results = game.toString();
		System.out.println("Game Over: "+results);
	}
	private static void locateRobots(game_service game, int x) {
		//if(x == 0) {
			List<String> log = game.getRobots();
			System.out.println(log.size());
//			if(log != null) {
//				for(int i = 0; i < log.size(); i++) {
//					String robot_json = log.get(i);
//					try {
//						JSONObject line = new JSONObject(robot_json);
//						JSONObject ttt = line.getJSONObject("Robot");
//						int rId = ttt.getInt("id");
//						int robotSrc = ttt.getInt("src");
//						int robotDest = ttt.getInt("dest");
//						//System.out.println("dest: " + dest);
//
//
//						if(robotDest==-1) {	
//							if(x==0) {//from MyGameGUI
//								robotDest = nextNodeManual(gg, robotSrc);
//								//game.chooseNextEdge(rId, robotDest);
//							}
//							else {//from Automatic
//								robotDest = nextNodeAuto(game, gg, robotSrc);
//
//								//game.chooseNextEdge(rId, robotDest);
//								//SimpleGameClientauto(game, gg, rId, robotSrc);
//							}
//							game.chooseNextEdge(rId, robotDest);
//							//							System.out.println("Turn to node: "+robotDest+"  time to end:"+(t/1000));
//							//							System.out.println(ttt);
//						}
//					}
//					catch (JSONException e) {e.printStackTrace();}
//				}
//			}
//		}
//		else {
//
//		}

	}

	/** 
	 * Moves each of the robots along the edge, 
	 * in case the robot is on a node the next destination (next edge) is chosen (randomly).
	 * @param game
	 * @param gg
	 * @param log
	 */
	public static void moveRobots(game_service game, graph gg,int x) {
		List<String> log = game.move();
		if(log!=null) {
			long t = game.timeToEnd();
			for(int i=0;i<log.size();i++) {

				String robot_json = log.get(i);
				try {			
					JSONObject line = new JSONObject(robot_json);
					JSONObject ttt = line.getJSONObject("Robot");
					int rid = ttt.getInt("id");
					int src = ttt.getInt("src");
					int dest = ttt.getInt("dest");
					//String pos = ttt.getString("pos");
					//OOP_Point3D pointOfRobot = new OOP_Point3D(pos);

					if(dest==-1) {	
						if(x==0) {//from MyGameGUI (manual)
							dest = nextNodeManual(gg ,src,game.getRobots().size());
						}
						else {//from Automatic
							dest = nextNodeAuto(game ,gg ,src);
						}
						game.chooseNextEdge(rid, dest);
						System.out.println("Turn to node: "+dest+"  time to end:"+(t/1000));
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
	private static int nextNodeManual(graph g, int src, int robotsCounter) {//The manual moves
		int ans = -1;
		if(robotsCounter==1) {
			if(StdDraw.pointOfMouse!=null) {
				OOP_Point3D mouseClick = new OOP_Point3D(StdDraw.pointOfMouse);
				for (edge_data e:g.getE(src)) {
					if(mouseClick.distance2D(g.getNode(e.getDest()).getLocation()) < ourEPS)
						ans = e.getDest();
				}
			}
		}
		else {//if we have more then one robot...
			if(StdDraw.pointOfMouse!=null) {
				OOP_Point3D mouseClick=new OOP_Point3D(StdDraw.pointOfMouse);//the first press is the robot we choose
				if(mouseClick.distance2D(g.getNode(src).getLocation()) < ourEPS) {
					do {//until we get another press (not on robot)
						mouseClick=StdDraw.pointOfMouse;
					}while(mouseClick.distance2D(g.getNode(src).getLocation()) < ourEPS);
					for (edge_data e:g.getE(src)) {
						if(mouseClick.distance2D(g.getNode(e.getDest()).getLocation()) < ourEPS)
							ans=e.getDest();
					}

				}	
			}
		}
		return ans;
	}
	private static void moveByList(game_service game, int rId, List<node_data> l) {
		int d = -1;
		System.out.println(game.move());
		for (node_data node_data : l) {

			d = node_data.getKey();
			//System.out.println(oop_node_data.getKey());
			game.chooseNextEdge(rId, node_data.getKey());
		}
		//game.chooseNextEdge(rId, oop_node_data.getKey());

	}

	private static void auto(game_service game, graph gg, int rId, int robotSrc) {

		int robotDest = getNodeFruit(game, gg, robotSrc);
		Graph_Algo ga = new Graph_Algo(gg);
		List<node_data> l = ga.shortestPath(robotSrc, robotDest);
		moveByList(game, rId, l);

	}

	private static int nextNodeAuto(game_service game, graph g, int robotSrc) {//The auto moves
		int robotDest = getNodeFruit(game, g, robotSrc);


		//System.out.println(destNode);
		Graph_Algo ga = new Graph_Algo(g);
		List <node_data> travel = ga.shortestPath(robotSrc, robotDest);
		if (travel != null) {
			//			for (oop_node_data oop_node_data : travel) {
			//				System.out.print(oop_node_data.getKey() + ", ");
			//			}

			for (node_data oop_node_data : travel) {
				//System.out.println(oop_node_data.getKey() + " " + oop_node_data.getKey());
				int ans = oop_node_data.getKey();
				if(ans != robotSrc) {
					return ans;
				}
			}
		}
		return -1;


	}
	
//	private static int getNodeFruit(game_service game, graph gg, int src) {
//		int notFound = -1;
//		List<String> eStrList = new ArrayList<>();
//		eStrList = game.getFruits();
//		for(int i=0;i<eStrList.size();i++) {
//			String fruit_json = eStrList.get(i);
//			try {
//				JSONObject line = new JSONObject(fruit_json);
//				JSONObject ttt = line.getJSONObject("Fruit");
//				double value = ttt.getDouble("value");
//				int type = ttt.getInt("type");
//				String pos = ttt.getString("pos");
//				OOP_Point3D posOfFruit = new OOP_Point3D(pos);
//
//				for(node_data ni: gg.getV()) {
//					for(node_data nj: gg.getV()) {
//						OOP_Point3D niP = ni.getLocation();
//						OOP_Point3D njP = nj.getLocation();
//						if(ni == nj)continue;
//						double destFruitniP = posOfFruit.distance2D(niP);
//						double destFruitnjP = posOfFruit.distance2D(njP);
//						double destniPnjP = niP.distance2D(njP);
//						if(Math.abs(destFruitniP + destFruitnjP - destniPnjP) <= minEPS) {
//							int niKey = ni.getKey();
//							int njKey = nj.getKey();
//							int nextMin = Math.min(niKey, njKey);
//							int nextMax = Math.max(niKey, njKey);
//							if((Node)gg.getNode(nextMin).)
//							if(type == 1) {
//								if(nextMin != src) {
//									return nextMin;
//								}return nextMax;
//							}
//							else {
//								if(nextMax != src) {
//									return nextMax;
//								}return nextMin;
//							}
//						}
//					}
//				}
//			} 
//			catch (JSONException e) {
//				e.printStackTrace();
//				return notFound;
//			}
//		}
//		return notFound;
//	}

	private static int getNodeFruit(game_service game, graph gg, int src) {
		int notFound = -1;
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
				OOP_Point3D posOfFruit = new OOP_Point3D(pos);


				for(node_data ni: gg.getV()) {
					for(node_data nj: gg.getV()) {
						OOP_Point3D niP = ni.getLocation();
						OOP_Point3D njP = nj.getLocation();
						if(ni == nj)continue;
						double destFruitniP = posOfFruit.distance2D(niP);
						double destFruitnjP = posOfFruit.distance2D(njP);
						double destniPnjP = niP.distance2D(njP);
						if(Math.abs(destFruitniP + destFruitnjP - destniPnjP) <= minEPS) {
							int niKey = ni.getKey();
							int njKey = nj.getKey();
							int nextMin = Math.min(niKey, njKey);
							int nextMax = Math.max(niKey, njKey);
							if(type == 1) {
								if(nextMin != src) {
									return nextMin;
								}return nextMax;
							}
							else {
								if(nextMax != src) {
									return nextMax;
								}return nextMin;
							}
						}
					}
				}
			} 
			catch (JSONException e) {
				e.printStackTrace();
				return notFound;
			}
		}
		return notFound;
	}
}