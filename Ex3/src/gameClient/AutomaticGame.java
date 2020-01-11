package gameClient;

import javax.swing.JOptionPane;
import Server.game_service;
import oop_dataStructure.OOP_DGraph;

public class AutomaticGame {
	MyGameGUI mgg;
	final double xMax = 35.216;
	final double xMin = 35.1835;
	final double yMax = 32.11;
	final double yMin = 32.1;
	
	
	public AutomaticGame(){
		mgg=new MyGameGUI();
		mgg.g = new OOP_DGraph();
	}
	

	public void automaticGameManagement() {
		int s = -1;
		while(s == -1) {
			s = mgg.pickScenario();
			if(s == -1) 
				JOptionPane.showMessageDialog(null, "choose a valid scenario");
			else if (s == -2) return;
		}
		game_service game=mgg.startScenario(s);
		runAutomaticScenario(game);
		mgg.displayFinalScore(game);
	}


	private void runAutomaticScenario(game_service game) {
		int x=1;//We in Automatic game
		game.startGame();
		while(game.isRunning()) {
			StdDraw.enableDoubleBuffering();

			mgg.refreshDraw();
			MyGameGUI.drawElements(game);
			MyGameGUI.drawRobot(game);
			SimpleGameClient.moveRobots(game, mgg.g,x);
			mgg.printScore(game);
			
			StdDraw.show();
		}
	}
}