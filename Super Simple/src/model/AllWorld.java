package model;

/**
 * Stock tout les mondes et son contenu pour une meilleur logistique du jeu
 *
 */
import java.util.ArrayList;
import utils.WorldData;

// Obligé de créer cette classe, car je n'ai pas beaucoup de temps et mon projet et mal structuré a cause de ca...
// Permet de stocker les mondes, et leurs états.
public class AllWorld {
	private ArrayList<World> worldList = new ArrayList<>();;  // Pour stocker l'état des mondes

	public AllWorld(){
		World w1 = new World(WorldData.WORLD1);
		this.worldList.add(w1);
		World w2 = new World(WorldData.WORLD2);
		this.worldList.add(w2);
		World w3 = new World(WorldData.WORLD3);
		this.worldList.add(w3);
	}
	
	public ArrayList<World> getWorldList() {
		return this.worldList;
	}
}
