package gameClient;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import Server.game_service;
import dataStructure.Fruit;
import dataStructure.Robot;
import dataStructure.graph;

public class KML_Logger {
	game_service game;
	graph g;
	MyGameGUI mgg;
	ArrayList<String> content;
	BufferedWriter bw;
	
	public KML_Logger(graph g) {
		this.g = g;
		mgg = new MyGameGUI();
	}

	public void setGame(game_service game){
		this.game = game;
		mgg.game = game;
	}

	public KML_Logger(game_service game) {
		Time time = new Time();
		Time maxtime = new Time();
		maxtime.setHour(1);
		content = new ArrayList<>();

		
		Collection<Fruit> fru = mgg.fruits.values();
		Collection<Robot> rob = mgg.robots.values();


		String kmlstart = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
				"<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n<Document><Style id=\"red\">\r\n" + 
				"<IconStyle><Icon><href>http://maps.google.com/mapfiles/ms/icons/red-dot.png</href></Icon></IconStyle>\r\n" + 
				"</Style><Style id=\"Robot\"><IconStyle><Icon><href>https://png2.cleanpng.com/sh/95fe9b063e89c606ea787537f2ab125a/L0KzQYm3VcExN6Z8iZH0aYP2gLBuTgJqa5wyi9N3Y3join70jCJ1gV54hdt9aD3vccj1jf94baMyfNHwLYPmebb1lPl0fF5zRadqZkTpSYPpg8E1PJM6RqY5NkK6SYGAUcUzPmE1TKI6MUe8QYe1kP5o/kisspng-rick-sanchez-morty-smith-lawnmower-dog-scientist-n-5af4f92bc144b5.4062790715260040117916.png</href></Icon></IconStyle>\r\n" + 
				"</Style><Style id=\"Fruit\"><IconStyle><Icon><href></href>https://i7.pngguru.com/preview/440/9/227/snout-clip-art-sticker-forehead-rick-and-morty.jpg</Icon></IconStyle></Style>";
		content.add(kmlstart);

		String kmlend = "\n</Document></kml>";
		try{
			FileWriter fw = new FileWriter("data\\myKml.kml");
			bw = new BufferedWriter(fw);

			for(Fruit f : fru) {
				Fruit tmp = f;
				String kmlFruit ="<Placemark>\n" +
						"<value>Fruit"+tmp.getValue()+"</value>\n" +
						"<description>Type: Fruit\nlat: "+tmp.getP().y()+"\nlon :"+tmp.getP().x()+"\nAlt: "+tmp.getP().z()+"\nType: "+tmp.getType()+ "</description>\n" +
						"<styleUrl>"+"Fruit"+"</styleUrl>"+"<Point>\n" +
						"<coordinates>"+tmp.getP().y()+","+tmp.getP().x()+","+tmp.getP().z()+"</coordinates>" +
						"</Point>\n" +
						"<TimeSpan>"
						+"<begin>"+time+"</begin>"
						+"<end>"+tmp.getTime()+"</end>"
						+"</TimeSpan>"+
						"</Placemark>";
				content.add(kmlFruit);
			}

			for(Robot r : rob) {
				Robot tmp = r;
				String kmlRobot ="<Placemark>\n" +
						"<name>Robot"+tmp.getId()+"</name>\n" +
						"<description>Type: Packman\nlat: "+tmp.getLocation().y()+"\nlon :"+tmp.getLocation().x()+"\nAlt: "+tmp.getLocation().z()+"\nSpeed: "+tmp.getSpeed()+"\nValue: "+tmp.getValue()+
						"</description>\n" +
						"<styleUrl>"+"Packman"+
						"</styleUrl>"+"<Point>\n" +
						"<coordinates>"+tmp.getLocation().y()+","+tmp.getLocation().x()+","+tmp.getLocation().z()+"</coordinates>" +
						"</Point>\n" +
						"<TimeSpan>"
						+"<begin>"+time+"</begin>"
						+"<end>"+tmp.getTime()+"</end>"
						+"</TimeSpan>"
						+"</Placemark>";
				content.add(kmlRobot);
			}

			content.add(kmlend);
			
		}catch (Exception e) {
			e.printStackTrace();
			//return false;
		}
		//return true;
	}
	
	
    public void saveToFile() throws IOException {
    	
    	String csv = content.toString().replaceAll("</Placemark>, <Placemark>", "</Placemark><Placemark>").replaceAll("</Placemark>, ", "</Placemark>").replaceAll(", <Placemark>", "<Placemark>");
		csv = csv.substring(1, csv.length()-1);
		bw.write(csv);
		bw.close();
    }
	
}
