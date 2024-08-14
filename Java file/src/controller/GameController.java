package controller;

import java.util.HashMap;

import items.HealPotion;
import items.Key;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
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
import utils.WorldData;
import model.World;
import scene.GameView;
import scene.Menu;
import model.Door;
import model.Item;
import model.Npc;

public class GameController {
	private Pane gamePane;
	private Simple simple;
	private World currentWorld;
	private int currentWorldIndex;  // On est dans le monde 1 au debut
    private HashMap<KeyCode, Boolean> keys;
    private long lastUpdateTime;
    //private Runnable onPlayerDeath;
    private AnimationTimer timer;
    private GameView gameView;
    private Npc npc1;
    private Key key1 = new Key("Cle Jaune", Color.YELLOW);
    private HealPotion npcPotion;
    private boolean isPaused;
    
    
    public GameController() {
        keys = new HashMap<>();
    }
	
	public void setWorld(World world, int worldIndex) {
        this.currentWorld = world;
        this.currentWorldIndex = worldIndex;
        this.gamePane = world.getWorldRoot();
        // SIMPLE ( C'est le personnage hein) //////
        this.simple = new Simple(Constants.DIMENSION*4, currentWorld.getWorldHeight()- Constants.OBSTACLE*5);
        gamePane.getChildren().add(simple.getSimple());
        
        simple.getInventory().getInventoryPane().getChildren().addAll(createHautInventory());
		gamePane.getChildren().add(simple.getInventory().getInventoryPane());
		
        // PNJ //////////
        this.npc1 = new Npc(Constants.npc1X, Constants.npc1Y, Color.YELLOW);
        gamePane.getChildren().add(npc1.getNPC());
        gamePane.getChildren().add(npc1.getNpcPane());
        npc1.getNpcPane().setVisible(false);
        setNpc1Pane();
        Key key1 = new Key("Cle Jaune", Color.YELLOW);
        npc1.getInventory().addItem(key1);
        npcPotion = new HealPotion("Potion", Color.BLUEVIOLET, 2, simple);
        npc1.getInventory().addItem(npcPotion);
        //npc1.getInventory().print();
        
        HealPotion potion1 = new HealPotion("Potion Suspecte", Color.DARKGREEN, -1, this.simple);
        simple.getInventory().addItem(potion1);
		simple.getInventory().getInventoryPane().setVisible(false);
		
        // Initialisation du jeu
		timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
            	if (lastUpdateTime > 0 && !isPaused) {  // Pour ralentir / accelerer la frequence
                    double deltaTime = (now - lastUpdateTime) / 10_000_000.0;
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
    
            //System.out.println("Show");
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
        
        // Vérifier la collision avec les portes
        checkDoorCollisions();
        
        // Verifier si on sort pas de la carte (on change de carte si oui)
        checkBorderWorld();
        
        // Check les pv du joueur
        checkHealth();

        // Affiche le text (s'il est a proximité)
        showNpcText(npc1);
       
        // Appel à la méthode de mise à jour de la caméra
        updateCamera();
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

		// Bouton pour recommencer Ajouter le bouton de fermeture en haut à droite
        Button boutonContinuer = new Button("X");
        boutonContinuer.setFocusTraversable(false);  // enleve le focus sur le bouton 
        boutonContinuer.setOnAction(e -> toggleInventory());
        
        HBox hautInventaire = new HBox(155);
        hautInventaire.getChildren().addAll(titleLabel, boutonContinuer);
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
	private void setNpc1Pane(){
		if(!npc1.getChange()){
			if(npc1.getCompteur() == 0) {
		        Text message = new Text("Y'a un monstre à côté !\nPrend cette clé, ouvre la porte et va le tuer !");
		        message.getStyleClass().add("npcMessage");
		        
		        // Bouton pour ajouter l'objet à l'inventaire
		        Button continuer = new Button("Compte sur moi");
		        continuer.setFocusTraversable(false);  // enleve le focus sur le bouton
		        continuer.getStyleClass().add("npcBouton");
		        continuer.setOnAction(event -> {
		        	npc1.addCompteur();  // Ajoute 1 au compteur, et ajoute vrai au change
		        	
		        	npc1.getInventory().removeItem(key1);
		        	simple.getInventory().addItem(key1);
		        	
		        	npc1.getNpcPane().getChildren().clear();
		        	setNpc1Pane();
		        });
		        npc1.getNpcPane().getChildren().addAll(message, continuer);
		        //gamePane.getChildren().add(npc1.getNpcPane());
		        npc1.setChange(true);
			}
			if(npc1.getCompteur() == 1) {
				Text message = new Text("Qu'est-ce que t'attends ? Va le vaincre !");
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
			if(npc1.getCompteur() == 2) {
				Text message = new Text("Pfff... Je t'ai donné une potion de vie...\n Va le vaincre maintenant... ");
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
		if(simple.getSimple().getTranslateX() >= currentWorld.getWorldWidth()) {
			this.currentWorldIndex++;
			loadWorld();
		}
	}
	
	private void loadWorld() {
		this.gamePane.getChildren().clear();
        String[] newWorldData;
        switch (2) {
            case 1:
            	newWorldData = WorldData.WORLD1;
                break;
            case 2:
            	newWorldData = WorldData.WORLD2;
                break;
            default:
            	newWorldData = WorldData.WORLD1;
        }
        this.currentWorld = new World(WorldData.WORLD2);
        this.gamePane = currentWorld.getWorldRoot();
        simple.setX(0);  // On met le personnage en haut a gauche pour afficher des trucs sur l'écran (c'est le seul moyen que j'ai trouvé)
		simple.setY(0);
		gamePane.getChildren().add(simple.getSimple());
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
    	gameView = new GameView(gameView.getStage(), 1);
    }
    
    private void checkHealth() {
    	if(simple.getHealth() <= 0) {
    		showDeathScreen();
    	}
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
