package model;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import utils.Constants;

/**
 * Super classe item qui sera utilisé pour créer différent item
 *
 */
public class Item {
	private final String name;
	protected Color color;
	protected boolean isUsable = false;
	private Circle visual;
	
	public Item(String name, Color col) {
		this.name = name;
		this.color = col;
	}
	public String getName() {
		return this.name;
	}
	
	public void use() {
		
	};
	public Color getColor() {
		return this.color;
	}
	public boolean getIsUsable() {
		return this.isUsable;
	}
	
	// Visuel de l'objet sur la carte
	public void createVisual(double x, double y) {
        Circle circle = new Circle(15, color);
        circle.setCenterX(x+Constants.OBSTACLE/2);
    	circle.setCenterY(y+Constants.OBSTACLE-17);
        circle.setStroke(Color.LIGHTSKYBLUE);
        circle.setStrokeWidth(2);
        this.visual = circle;
    }
    public Circle getVisual() {
        return visual;
    }
}
