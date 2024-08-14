package model;

import javafx.scene.paint.Color;

public class Item {
	private final String name;
	private Color color;
	protected boolean isUsable = false;
	
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
}
