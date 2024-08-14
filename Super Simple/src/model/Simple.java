package model;

import javafx.animation.PauseTransition;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import utils.Constants;

/**
 * Notre personnage du jeu simple
 *
 */
public class Simple {
	private Node simple;
	private Point2D velocity;
	private boolean canJump;
	private Inventory inventory;
	private int health;
	private boolean facingRight;  // Pour connaitre la derniere direction du joueur
	private boolean isInvicible = false;
	//private GridPane;
	
	public Simple(double x, double y) {
        this.simple = createSimple(x, y);  // Création du node (rectangle)
        this.velocity = new Point2D(0, 0);  // vitesse à zero
        this.canJump = true; 
        this.health = 1;
        this.inventory = new Inventory();
        this.facingRight = true; // Par défaut, le joueur fait face à droite
    }
	
	private Node createSimple(double x, double y) {
		//  Le joueur est tout simple... C'est un rectangle bleu
		Rectangle rectangle = new Rectangle(Constants.DIMENSION, Constants.DIMENSION, Color.BLUE);
		rectangle.setTranslateX(x);
        rectangle.setTranslateY(y);
		return rectangle;  // Retourne le Rectangle en tant que Node
	}
	
	public boolean isFacingRight() {
        return facingRight;
    }
	public Parent getParent() {
        return simple.getParent();
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
    	if (value > 0) {
            facingRight = true;
        } else if (value < 0) {
            facingRight = false;
        }
        simple.setTranslateX(simple.getTranslateX() + value);
    }

    public void moveY(int value) {
    	simple.setTranslateY(simple.getTranslateY() + value);
    }
    
    public void setX(double value) {
    	simple.setTranslateX(value);
    }
    public void setY(double value) {
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
    	if(val < 0) {
    		if (simple instanceof Rectangle) {
                ((Rectangle) simple).setFill(Color.RED);

                // Revenir à la couleur originale après 0.2 seconde
                PauseTransition pause = new PauseTransition(Duration.seconds(0.15));
                pause.setOnFinished(event -> ((Rectangle) simple).setFill(Color.BLUE)); // Assurez-vous de définir la couleur originale
                pause.play();
                if(!this.isInvicible) {
                	this.health += val;
                }
                // Temps d'invincibilite
                this.isInvicible = true;
                PauseTransition invicible = new PauseTransition(Duration.seconds(0.6));
                invicible.setOnFinished(e -> this.isInvicible = false); // Assurez-vous de définir la couleur originale
                invicible.play();
            }
    	}
    	else {
    		this.health += val;
    	}
    }
    public int getHealth() {
    	return this.health ;
    }
}
