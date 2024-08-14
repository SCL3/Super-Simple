package items;

import javafx.scene.paint.Color;
import model.Item;

/**
 * Clé qui permet d'ouvrir une porte qui a le meme nom que la clé
 *
 */
public class Key extends Item {
	//private int id;  // id utile pour le comparer à une porte
	
    public Key(String name, Color col) {
    	super(name, col);
    	//isUsable = true;
    }
    
    // Méthode pour utiliser la clé et ouvrir la porte
    public void use() {
        // Logic to open the door
        //System.out.println("The key has been used to open the door.");
    }
}
