package items;

import java.util.ArrayList;
import java.util.List;

import javafx.animation.PauseTransition;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import model.Item;
import model.Mob;
import model.Npc;
import model.Simple;
import model.World;
import utils.Constants;

/**
 * La super épée qu'utilise notre personnage. 
 * Il faut appuyer sur la touche "c" pour s'en servir
 * - Temps d'attente avant de taper
 * - Detection de colision avec un pnj ou un mob
 */
public class Sword extends Item {
    private boolean canAttack;
    private Node sword;
    
    public Sword(String name, Color color) {
        super(name, color);
        //this.isUsable = true;
        this.canAttack = true;
    }

    public void use(Simple player, Pane gamePane, World world) {
        if (canAttack) {
            canAttack = false;

            // Créer l'épée comme un rectangle
            Rectangle sword = new Rectangle(70, 15, this.color);
            this.sword = sword;
            double posX = player.getSimple().getTranslateX();
            double posY = player.getSimple().getTranslateY();

            // Positionner l'épée en fonction de la direction du joueur
            if (player.isFacingRight()) {
                sword.setTranslateX(posX + Constants.DIMENSION);
            } else {
                sword.setTranslateX(posX - sword.getWidth());
            }
            sword.setTranslateY(posY + Constants.DIMENSION / 2);

            // Ajouter l'épée à la scène
            gamePane.getChildren().add(sword);
            
            // Liste temporaire pour les nœuds à traiter
            List<Node> nodesToProcess = new ArrayList<>(gamePane.getChildren());
            // Vérifier les collisions avec les mobs et NPCs
            for (Node node : nodesToProcess) {
                if (node instanceof Rectangle && node != sword) {
                    Rectangle rect = (Rectangle) node;
                    if (rect != player.getSimple() && sword.getBoundsInParent().intersects(rect.getBoundsInParent())) {
                        if (rect.getUserData() instanceof Mob) {
                            ((Mob) rect.getUserData()).takeDamage(1, gamePane, world);
                            //System.out.println(((Mob) rect.getUserData()).getHealth());
                        }
                    	else if (rect.getUserData() instanceof Npc) {
                    		((Npc) rect.getUserData()).takeDamage(1, gamePane, world);
                    		//System.out.println(((Npc) rect.getUserData()).getHealth());
                    	}
                    }
                }
            }
            
            // Enlever l'épée après 1 seconde
            PauseTransition delay = new PauseTransition(Duration.seconds(0.05));
            delay.setOnFinished(event -> gamePane.getChildren().remove(sword));
            delay.play();

            // Réinitialiser la capacité d'attaquer après 0.8 secondes
            PauseTransition attackCooldown = new PauseTransition(Duration.seconds(0.8));
            attackCooldown.setOnFinished(event -> canAttack = true);
            attackCooldown.play();
        }
    }
    public Node getSword() {
    	return this.sword;
    }
}
