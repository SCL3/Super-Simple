package items;
import javafx.scene.paint.Color;
import model.Item;
import model.Simple;

public class HealPotion extends Item{
	private int healAmount;
	private Simple simple;
	public HealPotion(String name, Color col, int amount, Simple simple) {
		super(name, col);
		this.healAmount = amount;
		this.simple = simple;
		isUsable = true;
	}
	
	public void use() {
        simple.regenerateHealth(this.healAmount);
    }
}
