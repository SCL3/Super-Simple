package model;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import utils.Constants;

public class Npc {
	private Rectangle npc;
	private boolean showText = false;
	private VBox npcPane;
	private double x;
	private double y;
	private int compteur = 0;
	private boolean change = false;
	private Inventory inventory;
	
    public Npc(double x, double y, Color col) {
        this.npc = new Rectangle(Constants.DIMENSION, Constants.DIMENSION, col);
        // Recuperation des coordonnees
        this.x = x;
        this.y = y;
        this.npc.setTranslateX(x);
        this.npc.setTranslateY(y);
        this.inventory = new Inventory();
        setNpcPane();
    }
    
    // Inventaire du NPC
 	public Inventory getInventory() {
 		return this.inventory;
 	}
 	
    public Node getNPC() {
        return npc;
    }
	public boolean nearby(Simple simple){
		if(simple.getSimple().getTranslateX() >= this.npc.getTranslateX()-Constants.DIMENSION &&
        		simple.getSimple().getTranslateX() <= this.npc.getTranslateX()+ Constants.DIMENSION*2 &&
        		simple.getSimple().getTranslateY() >= this.npc.getTranslateY() - Constants.DIMENSION &&
        		simple.getSimple().getTranslateY() <= this.npc.getTranslateY() + Constants.DIMENSION * 2) {
			return true;
		}
		return false;
	}
	public boolean getShowText() {
		return this.showText;
	}
	public void setShowText(boolean bool) {
		this.showText = bool;
	}
	
	// Vbox du npc
	public void setNpcPane() {
		this.npcPane = new VBox(10);
		this.npcPane.setLayoutX(this.x-Constants.npcPaneX/2+ Constants.DIMENSION/2);
		this.npcPane.setLayoutY(this.y - 180);
		this.npcPane.setAlignment(Pos.CENTER);
		this.npcPane.setStyle("-fx-background-color: green;");
		this.npcPane.setPrefSize(Constants.npcPaneX, Constants.npcPaneY); // Taille de la fenêtre
	}
	public VBox getNpcPane() {
		return this.npcPane;
	}
	
	public void addCompteur() {
		this.compteur ++;
		this.change = false;
	}
	public int getCompteur() {
		return this.compteur;
	}
	// Change (valeur qui permet le changement du contenu de la pane)
	public boolean getChange() {
		return this.change;
	}
	public void setChange(boolean bool) {
		this.change = bool;
	}
}
