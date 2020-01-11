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
import oop_dataStructure.OOP_DGraph;
import oop_dataStructure.oop_graph;
import oop_dataStructure.oop_node_data;
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
//		MyGameGUI mgg = new MyGameGUI();//manual game
//		mgg.manualGameManagement();
		
		AutomaticGame ag=new AutomaticGame();//automatic
		ag.automaticGameManagement();
		
		
//		game_service game = Game_Server.getServer(scenario_num); // you have [0,23] games
//		List<String> listF = game.getFruits();
//		test1();

	}

	public static void test1() {
		int scenario_num = 6;
		game_service game = Game_Server.getServer(scenario_num); // you have [0,23] games
		String g = game.getGraph();
		OOP_DGraph gg = new OOP_DGraph();
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
			moveRobots(game, gg,x);
			
			StdDraw.show();
		}
		String results = game.toString();
		System.out.println("Game Over: "+results);
	}
	/** 
	 * Moves each of the robots along the edge, 
	 * in case the robot is on a node the next destination (next edge) is chosen (randomly).
	 * @param game
	 * @param gg
	 * @param log
	 */
	public static void moveRobots(game_service game, oop_graph gg,int x) {
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
					
					
					if(dest==-1) {	
						if(x==0) {//from MyGameGUI
							dest = nextNode(gg, src);
						}
						else {//from Automatic
							dest = nextNodeAuto(game, gg, src);
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
	private static int nextNode(oop_graph g, int src) {//The manual moves
		int ans = -1;
		if(StdDraw.pointOfMouse!=null) {
			OOP_Point3D dd=new OOP_Point3D(StdDraw.pointOfMouse);
			Collection<oop_node_data> nodes=g.getV();
			for(oop_node_data n : nodes) {
				if(dd.distance2D(n.getLocation())<ourEPS) {
					ans=n.getKey();
				}
			}
		}
//		/boaz's random walk/
//		Collection<oop_edge_data> ee = g.getE(src);
//		Iterator<oop_edge_data> itr = ee.iterator();
//		int s = ee.size();
//		int r = (int)(Math.random()*s);
//		int i=0;
//		while(i<r) {itr.next();i++;}
//		ans = itr.next().getDest();
		return ans;
	}
	
	private static int nextNodeAuto(game_service game, oop_graph g, int src) {//The manual moves
		int nextNode = getNodeFruit(game, g, src);
		System.out.println(nextNode);
		Graph_Algo ga = new Graph_Algo(g);
		List <oop_node_data> travel = ga.shortestPath(src, nextNode);
		if (travel != null) {
			int i = 0;
			for (oop_node_data oop_node_data : travel) {

				int ans = oop_node_data.getKey();
				if(ans != src) {
					return ans;
				}

			}
		}
		return -1;


	}

	private static int getNodeFruit(game_service game, oop_graph g, int src) {
		int ans = -1;
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

				for(oop_node_data ni: g.getV()) {
					for(oop_node_data nj: g.getV()) {
						OOP_Point3D niP = ni.getLocation();
						OOP_Point3D njP = nj.getLocation();
						if(Math.abs(posOfFruit.distance2D(niP) + posOfFruit.distance2D(njP) - niP.distance2D(njP)) <= minEPS) {
							int nextMin = Math.min(ni.getKey(), nj.getKey());
							int nextMax = Math.max(ni.getKey(), nj.getKey());
							if(type == 1) {
								if(nextMin != src) {
									return nextMin;
								}else return nextMax;
							}
							else {
								if(nextMax != src) {
									return nextMax;
								}else return nextMin;
							}
						}
					}
				}
			} 
			catch (JSONException e) {
				e.printStackTrace();
				return ans;
			}
		}
		return ans;
	}
}