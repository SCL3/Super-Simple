package model;

import javafx.animation.PauseTransition;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * Monstre du jeu qui font des dégats au joueur lorsqu'il y a une colision
 *
 */
public class Mob {
	private Rectangle mob;
	private double speed;
	private Inventory inventory;
	private boolean movingRight = true;
	private int health;
	private Color color;
	
	private boolean isHere = true;
	private boolean isAlive = true;
	private boolean invicible = false;
	
	
	
	public Mob(double x, double y, double width, double height, Color col, double speed, int health) {
		this.color = col;
		this.mob = new Rectangle(width, height, this.color);
		this.inventory = new Inventory();
        this.mob.setTranslateX(x);
        this.mob.setTranslateY(y);
        this.mob.setUserData(this);  // Associer ce mob a son rectangle
        this.speed = speed;
        this.health = health;
	}
	public Rectangle getMob() {
        return mob;
    }
	
	// Inventaire du NPC
 	public Inventory getInventory() {
 		return this.inventory;
 	}
	 	
    public boolean isAlive() {
        return isAlive;
    }
    
    // FOnction pour enlever le npc du pane
    public void removeMob(Pane gamePane) {
    	if(this.isHere) {
	    	if(!this.isAlive) {
	    		gamePane.getChildren().remove(this.mob);
	    		this.isHere = false;
	    	}
    	}
    }
    
    // Health
    public int getHealth() {
    	return this.health ;
    }
 	public void takeDamage(int dmg, Pane gamePane, World world) {
 		if(!this.invicible) {
 			((Rectangle) mob).setFill(Color.RED);

 	        // Revenir à la couleur originale après 0.2 seconde
 	        PauseTransition pause = new PauseTransition(Duration.seconds(0.1));
 	        pause.setOnFinished(event -> ((Rectangle) mob).setFill(this.color)); // Assurez-vous de définir la couleur originale
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
        	item.createVisual(mob.getTranslateX(), mob.getTranslateY());
        	world.addItem(item);
            gamePane.getChildren().add(item.getVisual());
        }
        this.inventory.clearItem();
    }
 	public void move(double deltaTime) {
        if (isAlive) {
            double dx = speed * deltaTime;
            if (!movingRight) {
                dx = -dx;
            }
            mob.setTranslateX(mob.getTranslateX() + dx);
        }
    }

    public void reverseDirection() {
        movingRight = !movingRight;
    }

    public boolean collidesWith(Rectangle other) {
        return mob.getBoundsInParent().intersects(other.getBoundsInParent());
    }
}
