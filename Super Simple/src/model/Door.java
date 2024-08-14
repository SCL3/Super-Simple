package model;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import utils.Constants;

/**
 * Porte qui est ouvert avec une clé qui a le meme nom
 *
 */

public class Door extends Rectangle {
    private final String name; // Identifiant unique pour la porte
    private boolean isOpen = false;
    
    public Door(int x, int y, int w, int h, Color color, String id) {
        super(w, h, color);
        this.setTranslateX(x);
        this.setTranslateY(y);
        this.name = id;
    }

    public String getName() {
        return this.name;
    }
    
    public boolean getIsOpen() {
    	return this.isOpen;
    }
    
    public String nearby(Simple simple){
		if(simple.getSimple().getTranslateX() >= this.getTranslateX()-Constants.OBSTACLE*0.6 &&
        		simple.getSimple().getTranslateX() <= this.getTranslateX()+ Constants.OBSTACLE*1.6 &&
        		simple.getSimple().getTranslateY() >= this.getTranslateY() - Constants.OBSTACLE &&
        		simple.getSimple().getTranslateY() <= this.getTranslateY() + Constants.OBSTACLE ) {
			for (Item item : simple.getInventory().getItems()) {
				if(item.getName() == this.name) {
					return this.name;
				}
			}
			
		}
		return "";
		//System.out.println("X :" + this.getTranslateX() + " Y : " + this.getTranslateY());
		//System.out.println("X :" + simple.getSimple().getTranslateX() + " Y : " + simple.getSimple().getTranslateY());
	}
    
    // Méthode pour ouvrir la porte
    public void open() {
    	this.isOpen = true;
        this.setFill(Color.BLACK); // Changer la couleur pour indiquer que la porte est ouverte
        //System.out.println("Door with id " + this.name + " has been opened.");
    }
}