package model;

import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import utils.Constants;

/**
 * Pnj qui possède un pane pour parler avec le joueur
 *
 */
public class Npc {
	private Rectangle npc;
	private boolean showText = false;
	private VBox npcPane;
	private double x;
	private double y;
	private int compteur = 0;
	private boolean change = false;
	private Inventory inventory;
	private int health;
	private Color color;
	
	private boolean isHere = true;
	private boolean isAlive = true;
	private boolean invicible = false;
	
    public Npc(double x, double y, Color col) {
    	this.color = col;
        this.npc = new Rectangle(Constants.DIMENSION, Constants.DIMENSION, this.color);
        this.npc.setUserData(this);  // Associer cet Npc à son Rectangle
        // Recuperation des coordonnees
        this.x = x;
        this.y = y;
        this.npc.setTranslateX(x);
        this.npc.setTranslateY(y);
        this.inventory = new Inventory();
        this.health = 1;
        setNpcPane();
    }
    
    // FOnction pour enlever le npc du pane
    public void removeNpc(Pane gamePane) {
    	if(this.isHere) {
	    	if(!this.isAlive) {
	    		gamePane.getChildren().removeAll(this.npc, this.npcPane);
	    		this.isHere = false;
	    	}
    	}
    }
    public boolean getIsHere() {
    	return this.isHere;
    }
    public boolean getInvicible() {
    	return this.invicible;
    }
    public void makeInvicible (boolean bool) {
    	this.invicible = bool;
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
	
	public void setCompteur(int val) {
		this.compteur = val;
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
	
	 // Health
	public void takeDamage(int dmg, Pane gamePane, World world) {
		if(!this.invicible) {
			((Rectangle) npc).setFill(Color.RED);

 	        // Revenir à la couleur originale après 0.2 seconde
 	        PauseTransition pause = new PauseTransition(Duration.seconds(0.1));
 	        pause.setOnFinished(event -> ((Rectangle) npc).setFill(this.color)); // Assurez-vous de définir la couleur originale
 	        pause.play();
			this.health -= dmg;
		}
		if(this.health <= 0) {
			this.isAlive = false;
			dropItems(gamePane, world);
		}	
	}
	private void dropItems(Pane gamePane, World world) {
        for(Item item : this.getInventory().getItems()) {
        	item.createVisual(npc.getTranslateX(), npc.getTranslateY());
        	world.addItem(item);
            gamePane.getChildren().add(item.getVisual());
        }
        //this.inventory.clearItem();
    }
	
	public boolean getIsAlive() {
		return this.isAlive;
	}
	
    public void regenerateHealth(int val) {
    	this.health += val;
    }
    public void setHealth(int hp) {
    	this.health = hp;
    }
    public int getHealth() {
    	return this.health ;
    }
    
    public boolean collidesWith(Rectangle other) {
        return npc.getBoundsInParent().intersects(other.getBoundsInParent());
    }
}
