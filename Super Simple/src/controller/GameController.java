package controller;

import java.util.ArrayList;
import java.util.HashMap;

import items.HealPotion;
import items.Key;
import items.Sword;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import model.Simple;
import utils.Constants;
import model.World;
import scene.GameView;
import model.AllWorld;
import model.Door;
import model.Item;
import model.Mob;
import model.Npc;

/**
 * Contient toutes les fonctions nécessaire au bon fonctionnement du jeu
 * et toute la logistique
 */
public class GameController {
	private Pane gamePane;  // Pane qui va contenir tout les éléments du jeu
	private Simple simple;  // Le personnage
	private World currentWorld;
	private int currentWorldIndex;  // On est dans le monde 1 au debut (indice : 0)
    private HashMap<KeyCode, Boolean> keys;
    private long lastUpdateTime;
    //private Runnable onPlayerDeath;
    private AnimationTimer timer;
    private GameView gameView;
    private boolean victoryCondition = false;
    private Npc npc1;
    private Npc npc2;
    private Key key1 = new Key("Cle Jaune", Color.YELLOW);
    
    private HealPotion npcPotion;
    private boolean isPaused;
    private AllWorld allWorld;
    private double deltaTime;
    public GameController() {
        keys = new HashMap<>();
    }
	
	public void setWorld(World world) {
		this.allWorld = new AllWorld();
		this.currentWorldIndex = 0;
		
        this.currentWorld = world;  //allWorld.getWorldList().get(currentWorldIndex);
        		
        this.gamePane = world.getWorldRoot();
        
        // SIMPLE ( C'est le personnage hein) //////
        this.simple = new Simple(Constants.DIMENSION*4, currentWorld.getWorldHeight()- Constants.OBSTACLE*5);
        gamePane.getChildren().add(simple.getSimple());
        
        simple.getInventory().getInventoryPane().getChildren().addAll(createHautInventory());
		gamePane.getChildren().add(simple.getInventory().getInventoryPane());
		
        // PNJ 1 //////////
        this.npc1 = new Npc(Constants.npc1X, Constants.npc1Y, Color.YELLOW);
        this.npc1.makeInvicible(true);
        gamePane.getChildren().add(npc1.getNPC());
        gamePane.getChildren().add(npc1.getNpcPane());
        npc1.getNpcPane().setVisible(false);
        setNpc1Pane();
        Key key1 = new Key("Cle Jaune", Color.YELLOW);
        npc1.getInventory().addItem(key1);
        npcPotion = new HealPotion("Potion", Color.BLUEVIOLET, 2, simple);
        npc1.getInventory().addItem(npcPotion);
        //npc1.getInventory().print();
        
        //npcPotion.createVisual(300, 700);  // Creer le visuel de l'item pour l'ajouter a la carte
        //currentWorld.addItem(npcPotion);
        //gamePane.getChildren().add(npcPotion.getVisual());
        
        // PNJ 2 //////////
        this.npc2 = new Npc(Constants.npc2X, Constants.npc2Y, Color.ORANGE);
        npc2.setHealth(2);
        gamePane.getChildren().add(npc2.getNPC());
        gamePane.getChildren().add(npc2.getNpcPane());
        npc1.getNpcPane().setVisible(false);
        setNpc2Pane();
        
        HealPotion potion1 = new HealPotion("Potion Suspecte", Color.DARKGREEN, -1, this.simple);
        //npc2.getInventory().addItem(potion1);
        simple.getInventory().addItem(potion1);
		simple.getInventory().getInventoryPane().setVisible(false);
		
        // Initialisation du jeu
		timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
            	if (lastUpdateTime > 0 && !isPaused) {  // Pour ralentir / accelerer la frequence
                    deltaTime = (now - lastUpdateTime) / 10_000_000.0;
                    update(deltaTime);
               
                }
                lastUpdateTime = now;
            }
        };
        timer.start();
    }
	
	public Pane getGamePane() {
        return currentWorld.getWorldRoot();
    }
	// Permet de recuperer le gameview de la scene
	public void setGameView(GameView gameView) {
        this.gameView = gameView;
    }
	
	// Fonction pour mettre a jour la camera en fonction du joueur
	private void updateCamera() { 
        double offsetX = simple.getSimple().getTranslateX() - Constants.JEU_WIDTH / 2 + Constants.DIMENSION / 2;
        double offsetY = simple.getSimple().getTranslateY() - Constants.JEU_HEIGTH / 2 + Constants.DIMENSION / 2;

        // Limiter le défilement pour ne pas sortir des limites du niveau
        double maxOffsetX = currentWorld.getWorldWidth() - Constants.JEU_WIDTH ;
        double maxOffsetY = currentWorld.getWorldHeight() - Constants.JEU_HEIGTH;

        offsetX = Math.max(0, Math.min(offsetX, maxOffsetX));
        offsetY = Math.max(0, Math.min(offsetY, maxOffsetY));

        gamePane.setLayoutX(-offsetX);
        gamePane.setLayoutY(-offsetY);
    }
	
	private void update(double deltaTime) {
		//System.out.println(npc2.getHealth());
        if (isPressed(KeyCode.SPACE)) {
            simple.jump();
        }
        if(isPressed(KeyCode.LEFT)) {
        	movePlayerX(-Constants.SPEED * deltaTime);
        }
        if (isPressed(KeyCode.RIGHT)) {
        	movePlayerX(Constants.SPEED * deltaTime);
        }
        // Vérifier si la touche "i" est pressée pour afficher l'inventaire
        if (isPressed(KeyCode.I)) {
            toggleInventory();
        }
        
        // Déplacer les mobs et vérifier les collisions
        for (Mob mob : this.currentWorld.getMobs()) {
        	mob.removeMob(this.gamePane);
        	//System.out.println(mob.isAlive());
            mob.move(deltaTime/80);
            // Vérifier les collisions avec les murs
            for (Node platform : currentWorld.getPlatforms()) {
	            if (mob.getMob().getBoundsInParent().intersects(platform.getBoundsInParent())) {
	                mob.reverseDirection();
	            }
	            // Vérifier les collisions avec le joueur
	            if(mob.isAlive()) {
		            if (mob.collidesWith((Rectangle) simple.getSimple())) {
		                simple.regenerateHealth(-1); // Infliger des dégâts au joueur
		            }
	            }
            }
        }
        
        // Utilisation de l'épée
        if (isPressed(KeyCode.C)) {
            Item epee = simple.getInventory().getItemByName("epee");
            if (epee != null && epee instanceof Sword) {  // Vérifie si l'épée est présente dans l'inventaire et est une instance de Sword
                ((Sword) epee).use(simple, this.gamePane, this.currentWorld);  // Utilise l'épée avec les bons arguments
            }
        }
        
        if (simple.getVelocity().getY() < 10) {
            simple.setVelocity(simple.getVelocity().add(0, Constants.GRAVITY * deltaTime));
        }
        movePlayerY((int) simple.getVelocity().getY() * deltaTime);
        
        // Vérifier si le joueur est tombé dans le vide
        if (simple.getSimple().getTranslateY() > currentWorld.getWorldHeight()) {
            //if (onPlayerDeath != null) {
                //onPlayerDeath.run();
        		showDeathScreen();
            //}
        }  
        
        // Vérifier les collisions avec les items
        checkItemCollisions();
        
        // Vérifier la collision avec les portes
        checkDoorCollisions();
        
        
        // Verifier si on sort pas de la carte (on change de carte si oui)
        checkBorderWorld();
        
        // Check les pv du joueur
        checkHealth();

        // Affiche le text (s'il est a proximité)
        showNpcText(npc1);
        showNpcText(npc2);
       
        // Enleve le npc2 s'il n'a plus de vie
        npc2.removeNpc(this.gamePane);
        
        // Regarde si la condition de victoire est respecté
        victoryCondition();
        
        // Appel à la méthode de mise à jour de la caméra
        updateCamera();
    }

	// Item //////////
	private void checkItemCollisions() {
        ArrayList<Item> itemsToRemove = new ArrayList<>();
        for (Item item : currentWorld.getItems()) {
            if (simple.getSimple().getBoundsInParent().intersects(item.getVisual().getBoundsInParent())) {
                simple.getInventory().addItem(item);
                itemsToRemove.add(item);
            }
        }
        for (Item item : itemsToRemove) {
            currentWorld.removeItem(item);
            gamePane.getChildren().remove(item.getVisual());
        }
    }
	
	// Fonction inventaire /////////////////////////
	private HBox createHautInventory() {
        // Bouton de fermeture
        //Button closeButton = new Button("X");
        //closeButton.setOnAction(event -> this.setVisible(false));
        //closeButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");

        //HBox.setMargin(closeButton, new Insets(0, 0, 0, 20));
       
		
    	// Titre de l'inventaire
        Label titleLabel = new Label("Inventaire");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: black;");

        // Affichage des pv
        
        Label health = new Label("Pv : "+simple.getHealth());
        health.setStyle("-fx-font-size: 20px; -fx-text-fill: red;");
        
		// Bouton pour recommencer Ajouter le bouton de fermeture en haut à droite
        Button boutonContinuer = new Button("X");
        boutonContinuer.setFocusTraversable(false);  // enleve le focus sur le bouton 
        boutonContinuer.setOnAction(e -> toggleInventory());
        
        HBox hautInventaire = new HBox(50);
        hautInventaire.getChildren().addAll(health, titleLabel, boutonContinuer);
        hautInventaire.setAlignment(Pos.CENTER);
        //hautInventaire.setAlignment(Pos.TOP_RIGHT);
        return hautInventaire;
	}
	private void toggleInventory() {
        if (simple.getInventory().getInventoryPane().isVisible()) {
        	simple.getInventory().getInventoryPane().setVisible(false);
        	resumeGame();
        } else {
            // Mise à jour de la position de l'inventaire au-dessus du joueur
        	simple.getInventory().getInventoryPane().setTranslateX( simple.getSimple().getTranslateX() - Constants.inventaireX/2 + Constants.DIMENSION/2);
        	simple.getInventory().getInventoryPane().setTranslateY(simple.getSimple().getTranslateY() - 180); // Ajuster la position au besoin

            // Mise à jour des éléments de l'inventaire
            updateInventoryItems();
        	
        	simple.getInventory().getInventoryPane().setVisible(true);
        	pauseGame();
        }
    }
	
	private void updateInventoryItems() {
        // Effacer les anciens éléments de l'inventaire
		simple.getInventory().getInventoryPane().getChildren().clear();
		
		// Contenu de l'inventaire
        GridPane contentPane = new GridPane();
        contentPane.setPadding(new Insets(10));
        contentPane.setHgap(10);
        contentPane.setVgap(10);
        
        // Ajoute le haut de l'inventaire
        simple.getInventory().getInventoryPane().setTop(createHautInventory());
        
        // Ajouter les éléments de l'inventaire
        int row = 0;
        int col = 0;
        for (Item item : simple.getInventory().getItems()) {
            VBox itemBox = createItemBox(item);
            contentPane.add(itemBox, col, row);
            col++;
            if (col > 3) { // Changez cette valeur selon le nombre de colonnes que vous souhaitez
                col = 0;
                row++;
            }
        }
        simple.getInventory().getInventoryPane().setCenter(contentPane);
        
        // Ajouter les éléments de l'inventaire du joueur
        /*for (Item item : simple.getInventory().getItems()) {
            Text itemText = new Text(item.getName());
            itemText.setFill(Color.WHITE);
            simple.getInventory().getInventoryPane().getChildren().add(itemText);
        }   */
    }

	private VBox createItemBox(Item item) {
        VBox itemBox = new VBox(5);
        itemBox.setAlignment(Pos.CENTER);

        Rectangle itemRect = new Rectangle(50, 50);
        itemRect.setFill(item.getColor());

        Label itemLabel = new Label(item.getName());
        if(item.getIsUsable()) {
	        Button useButton = new Button("Utiliser");
	        useButton.setOnAction(event -> {
	            item.use();
	            simple.getInventory().removeItem(item); // Supprime l'item de l'inventaire après utilisation
	            toggleInventory();
	        });
	        itemBox.getChildren().addAll(itemRect, itemLabel, useButton);
        }
        else {
        	itemBox.getChildren().addAll(itemRect, itemLabel);
        }
        return itemBox;
    }
	// FONCTION NPC ///////////////////////////////////////////////
	private void setNpc2Pane() {
		if(!npc2.getChange()){
			if(npc2.getCompteur() == 0) {
				Text message = new Text("Salut :)");
		        message.getStyleClass().add("npcMessage");
		        
		        npc2.getNpcPane().getChildren().addAll(message);
		        //gamePane.getChildren().add(npc1.getNpcPane());
		        npc2.setChange(true);
			}
			else if(npc2.getCompteur() == 1) {
				Text message = new Text("J'ai rien volé moi je te le jure !\nMe tue pas s'il te plaît\n:(");
		        message.getStyleClass().add("npcMessage");
		        
		        npc2.getNpcPane().getChildren().addAll(message);
		        //gamePane.getChildren().add(npc1.getNpcPane());
		        npc2.setChange(true);
			}
		}
	}
	
	private void setNpc1Pane(){
		if(!npc1.getChange()){
			if(victoryCondition) {
				Text message = new Text("Oh ! Tu as mes cailloux !\n Je te les échange contre ce bouton magique qui affiche l'écran de victoire.");
		        message.getStyleClass().add("npcMessage");
		        
		        // Bouton pour ajouter l'objet à l'inventaire
		        Button continuer = new Button("Pourquoi pas");
		        continuer.setFocusTraversable(false);  // enleve le focus sur le bouton
		        continuer.getStyleClass().add("npcBouton");
		        continuer.setOnAction(event -> {
		        	// flemme de faire l'échange de cailloux
		        	showWinScreen();
		        });
		        npc1.getNpcPane().getChildren().addAll(message, continuer);
		        //gamePane.getChildren().add(npc1.getNpcPane());
		        npc1.setChange(true);
			}
			else if(npc1.getCompteur() == 0) {
		        Text message = new Text("Y'a des monstres qui ont volé mes 3 cailloux violets !\nPrends cette clé, ouvre la porte et récupère-les !");
		        message.getStyleClass().add("npcMessage");
		        
		        // Bouton pour ajouter l'objet à l'inventaire
		        Button continuer = new Button("Compte sur moi");
		        continuer.setFocusTraversable(false);  // enleve le focus sur le bouton
		        continuer.getStyleClass().add("npcBouton");
		        continuer.setOnAction(event -> {
		        	npc1.addCompteur();  // Ajoute 1 au compteur, et ajoute vrai au change
		        	npc2.addCompteur(); 
		        	
		        	npc1.getInventory().removeItem(key1);
		        	simple.getInventory().addItem(key1);
		        	
		        	npc1.getNpcPane().getChildren().clear();
		        	npc2.getNpcPane().getChildren().clear();
		        	setNpc1Pane();
		        	setNpc2Pane();
		        });
		        npc1.getNpcPane().getChildren().addAll(message, continuer);
		        //gamePane.getChildren().add(npc1.getNpcPane());
		        npc1.setChange(true);
			}
			else if(npc1.getCompteur() == 1) {
				Text message = new Text("Qu'est-ce que t'attends ? Va les vaincre !");
		        message.getStyleClass().add("npcMessage");
		     // Bouton pour ajouter l'objet à l'inventaire
		        Button bouton1 = new Button("J'ai peur...");
		        bouton1.setFocusTraversable(false);  // enleve le focus sur le bouton
		        bouton1.getStyleClass().add("npcBouton");
		        bouton1.setOnAction(event -> {
		        	npc1.addCompteur();  // Ajoute 1 au compteur, et ajoute vrai au change
		        	
		        	npc1.getInventory().removeItem(npcPotion);
		        	simple.getInventory().addItem(npcPotion);
		        	
		        	npc1.getNpcPane().getChildren().clear();
		        	setNpc1Pane();
		        });
		        
		        npc1.getNpcPane().getChildren().addAll(message, bouton1);
		        npc1.setChange(true);
			}
			else if(npc1.getCompteur() == 2) {
				Text message = new Text("Pfff... Je t'ai donné ma potion de vie...\n Va les vaincre maintenant... ");
		        message.getStyleClass().add("npcMessage");
		        
		        npc1.getNpcPane().getChildren().addAll(message);
		        npc1.setChange(true);
			}
			
		}	
	}
	
	private void showNpcText(Npc npc) {
		if(npc.nearby(simple)){
        	//npc1.setShowText(true);
			if(!npc.getNpcPane().isVisible()) {
				npc.getNpcPane().setVisible(true);
				//System.out.println(npc.getNpcPane().isVisible());
			}
        }
        else {
        	if(npc.getNpcPane().isVisible()) {
        		npc.getNpcPane().setVisible(false);
        	}  	
        }
	}
	
	// Fonction porte ////////////
	private void checkDoorCollisions() {
		for (Door door : currentWorld.getDoors()) {
        	if(!door.getIsOpen()){  
        		String doorName = door.nearby(simple);
        		if(!door.nearby(simple).equals("")) { // On ouvre toute les portes qui ont le meme nom que la cle
        			openAllDoor(doorName);
        			simple.getInventory().removeItem(simple.getInventory().getItemByName(doorName));  // On enleve la cle
        		}
        	}
        }
	}
	private void openAllDoor(String name) {
		for (Door door : currentWorld.getDoors()) {
			if(door.getName().equals(name)) {
				door.open();
			}
		}
	}
	// FONCTION DU SIMPLE /////////////////////////
	private void movePlayerX(double value) {
        boolean movingRight = value > 0;
        int moveStep = movingRight ? 1 : -1;
        for (int i = 0; i < Math.abs(value); i++) {
            simple.moveX(moveStep);
            for (Node platform : currentWorld.getPlatforms()) {
                if (simple.getSimple().getBoundsInParent().intersects(platform.getBoundsInParent())) {
                	simple.moveX(-moveStep);
                    return;
                }
            }
            for (Door door : currentWorld.getDoors()) {
            	if(!door.getIsOpen()){
	                if (simple.getSimple().getBoundsInParent().intersects(door.getBoundsInParent())) {
	                	simple.moveX(-moveStep);
	                    return;
	                }
            	}
            }
        }
    }
	
	private void movePlayerY(double value) {
        boolean movingDown = value > 0;
        int moveStep = movingDown ? 1 : -1;
        for (int i = 0; i < Math.abs(value); i++) {
            simple.moveY(moveStep);
            for (Node platform : currentWorld.getPlatforms()) {
                if (simple.getSimple().getBoundsInParent().intersects(platform.getBoundsInParent())) {
                    simple.moveY(-moveStep);
                    if (movingDown) {
                        simple.setCanJump(true);
                    }
                    return;
                }
            }
            for (Door door : currentWorld.getDoors()) {
            	if(!door.getIsOpen()){
	                if (simple.getSimple().getBoundsInParent().intersects(door.getBoundsInParent())) {
	                	simple.moveY(-moveStep);
	                    if (movingDown) {
	                        simple.setCanJump(true);
	                    }
	                    return;
	                }
            	}
            }
        }
    }
	
	public void setKeyPressed(KeyCode key, boolean pressed) {
        keys.put(key, pressed);
    }
	
	private boolean isPressed(KeyCode key) {
        return keys.getOrDefault(key, false);
    }

	private void checkBorderWorld(){
		if(simple.getSimple().getTranslateX() >= currentWorld.getWorldWidth()-Constants.DIMENSION/2) {  // Sortie a droite
			pauseGame();

			this.currentWorldIndex++;
			//this.gamePane.getChildren().clear();
			
			//this.gamePane.getChildren().add(simple.getSimple());
			loadWorld(1);
		}
		else if(simple.getSimple().getTranslateX() < 0) { // Sortie a gauche
			pauseGame();

			this.currentWorldIndex--;
			loadWorld(-1);
		}
		
	}
	
	private void loadWorld(int direction) {
		double posY = this.simple.getSimple().getTranslateY();  // Futur position Y 
		
		this.gamePane.getChildren().clear();

        this.currentWorld = allWorld.getWorldList().get(this.currentWorldIndex);
        this.gamePane.getChildren().add(this.currentWorld.getWorldRoot());
        
        double posX = 25; // Futur postion X a droite par defaut
        if(direction == -1) {
			posX = currentWorld.getWorldWidth()-25;
		}
        simple.setX(posX);  // On met le personnage en haut a gauche pour afficher des trucs sur l'écran (c'est le seul moyen que j'ai trouvé)
		simple.setY(posY);
		if(this.currentWorldIndex == 0) {
			// Ajout des pnj s'ils sont vivant
			if(npc1.getIsAlive()) {
				gamePane.getChildren().addAll(npc1.getNPC(), npc1.getNpcPane());
			}
			if(npc2.getIsAlive()) {
				gamePane.getChildren().addAll(npc2.getNPC(), npc2.getNpcPane());
			}
		}
		for(Mob mob : this.currentWorld.getMobs()) {
			if(mob.isAlive()) {
	            gamePane.getChildren().add(mob.getMob());
            }
        }
		for(Item item : this.currentWorld.getItems()) {
			
	            gamePane.getChildren().add(item.getVisual());
            
        }
		gamePane.getChildren().addAll(simple.getSimple(), simple.getInventory().getInventoryPane());
		resumeGame();
        //
    }
	
	// Fonction pour arreter le jeu
    public void stopGame() {
        timer.stop();
    }
    public void pauseGame() {
        isPaused = true;
    }

    public void resumeGame() {
        isPaused = false;
    }
    
    private void restartGame() {
    	gameView = new GameView(gameView.getStage(), 0);
    }
    
    private void checkHealth() {
    	if(simple.getHealth() <= 0) {
    		showDeathScreen();
    	}
    }
    
    // Le joueur doit avoir 3 caillou pour gagner
    private void victoryCondition() {
    	if(!this.victoryCondition) {
	    	if(simple.getInventory().countItemByName("Caillou") >= 3) {
	    		// il doit parler au pnj pour gagner
	    		npc1.getNpcPane().getChildren().clear();
		    	npc1.setChange(false);  
		    	this.victoryCondition = true;
		    	setNpc1Pane();  // Rafraichit la conversation	
	    	}
    	}
    }
    
    private void showWinScreen() {
        // Création du StackPane pour la fenêtre de mort
    	simple.setX(0);  // On met le personnage en haut a gauche pour afficher des trucs sur l'écran (c'est le seul moyen que j'ai trouvé)
		simple.setY(0);
		stopGame();
        VBox winBox = new VBox(40);
        winBox.setAlignment(Pos.CENTER);
        winBox.setStyle("-fx-background-color: green;");
        winBox.setPrefSize(Constants.JEU_WIDTH, Constants.JEU_HEIGTH); // Taille de la fenêtre

        // Création du message de mort
        Text message = new Text("Vous avez gagné !!!");
        message.setStyle("-fx-font-size: 80px; -fx-fill: white;");

        // Bouton pour recommencer
        Button newGameButton = new Button("Nouvelle Partie");
        newGameButton.setOnAction(e -> restartGame());
        newGameButton.getStyleClass().add("menuBouton");
        
        // Bouton pour quitter 
        Button quitButton = new Button("Quitter");
        quitButton.setOnAction(event -> Platform.exit());
        quitButton.getStyleClass().add("menuBouton");
        // Ajout du message au StackPane
        winBox.getChildren().addAll(message, newGameButton, quitButton);
        StackPane.setAlignment(message, Pos.CENTER);
        
        // Mettre le death box en haut a gauche
        winBox.setLayoutX(0);
        winBox.setLayoutY(0);

        // Vérifier et afficher les dimensions pour le debug
        //System.out.println("gamePane width: " + gamePane.getWidth() + ", height: " + gamePane.getHeight());
        //System.out.println("deathPane layoutX: " + deathBox.getLayoutX() + ", layoutY: " + deathBox.getLayoutY());

        // Ajout du StackPane au gamePane
        gamePane.getChildren().add(winBox);
    }
    private void showDeathScreen() {
        // Création du StackPane pour la fenêtre de mort
    	simple.setX(0);  // On met le personnage en haut a gauche pour afficher des trucs sur l'écran (c'est le seul moyen que j'ai trouvé)
		simple.setY(0);
		stopGame();
        VBox deathBox = new VBox(40);
        deathBox.setAlignment(Pos.CENTER);
        deathBox.setStyle("-fx-background-color: black;");
        deathBox.setPrefSize(Constants.JEU_WIDTH, Constants.JEU_HEIGTH); // Taille de la fenêtre

        // Création du message de mort
        Text message = new Text("Vous êtes mort");
        message.setStyle("-fx-font-size: 80px; -fx-fill: white;");

        // Bouton pour recommencer
        Button newGameButton = new Button("Nouvelle Partie");
        newGameButton.setOnAction(e -> restartGame());
        newGameButton.getStyleClass().add("menuBouton");
        
        // Bouton pour quitter 
        Button quitButton = new Button("Quitter");
        quitButton.setOnAction(event -> Platform.exit());
        quitButton.getStyleClass().add("menuBouton");
        // Ajout du message au StackPane
        deathBox.getChildren().addAll(message, newGameButton, quitButton);
        StackPane.setAlignment(message, Pos.CENTER);
        
        // Mettre le death box en haut a gauche
        deathBox.setLayoutX(0);
        deathBox.setLayoutY(0);

        // Vérifier et afficher les dimensions pour le debug
        //System.out.println("gamePane width: " + gamePane.getWidth() + ", height: " + gamePane.getHeight());
        //System.out.println("deathPane layoutX: " + deathBox.getLayoutX() + ", layoutY: " + deathBox.getLayoutY());

        // Ajout du StackPane au gamePane
        gamePane.getChildren().add(deathBox);
    }
}
