package items;
import javafx.scene.paint.Color;
import model.Item;

/**
 * Les cailloux que recherche le pnj pour donner au joueur l'écran de victoire
 *
 */
public class Stone extends Item{
	int amount;
	
	public Stone(String name, Color col) {
		super(name, col);
		this.amount = 1;
	}
	
}
