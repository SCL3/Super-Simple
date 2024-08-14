package model;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import utils.Constants;

public class Simple {
	private Node simple;
	private Point2D velocity;
	private boolean canJump;
	private Inventory inventory;
	private int health;
	//private GridPane;
	
	public Simple(double x, double y) {
        this.simple = createSimple(x, y);  // Création du node (rectangle)
        this.velocity = new Point2D(0, 0);  // vitesse à zero
        this.canJump = true; 
        this.health = 1;
        this.inventory = new Inventory();
    }
	
	private Node createSimple(double x, double y) {
		//  Le joueur est tout simple... C'est un rectangle bleu
		Rectangle rectangle = new Rectangle(Constants.DIMENSION, Constants.DIMENSION, Color.BLUE);
		rectangle.setTranslateX(x);
        rectangle.setTranslateY(y);
		return rectangle;  // Retourne le Rectangle en tant que Node
	}
	
	// Inventaire du joueur
	public Inventory getInventory() {
		return this.inventory;
	}
	
	// Getters et setters pour playerNode, velocity et canJump

    public Node getSimple() {
        return simple;
    }
    
    public void setVelocity(Point2D velocity) {
        this.velocity = velocity;
    }
    public Point2D getVelocity() {
        return velocity;
    }
    
    public void setCanJump(boolean canJump) {
        this.canJump = canJump;
    }
    public boolean canJump() {
        return canJump;
    }
    
    // Méthodes de déplacement pour le joueur
    public void moveX(int value) {
        simple.setTranslateX(simple.getTranslateX() + value);
    }

    public void moveY(int value) {
    	simple.setTranslateY(simple.getTranslateY() + value);
    }
    
    public void setX(int value) {
    	simple.setTranslateX(value);
    }
    public void setY(int value) {
    	simple.setTranslateY(value);
    }
    // Méthode de saut pour le joueur
    public void jump() {
        if (canJump) {
            velocity = velocity.add(0, -30);
            canJump = false;
        }
    }
    
    // Health
    public void regenerateHealth(int val) {
    	this.health += val;
    }
    public int getHealth() {
    	return this.health ;
    }
}
