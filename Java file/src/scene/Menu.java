package scene;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import controller.MenuController;
import utils.Constants;

public class Menu {
	private Stage stage;
	private MenuController menuController;
	
	public Menu(Stage stg) {
		this.stage = stg;
		this.menuController = new MenuController(stg);
	}
	
	public void show() {
		VBox menuFenetre = new VBox(40);  // On utilise VBox car c'est pratique pour faire un menu
		menuFenetre.setId("menuFenetre");  // On attribue un ID pour le css
		
		Scene scene = new Scene(menuFenetre,Constants.MENU_WIDTH, Constants.MENU_HEIGTH);  // Contenu de la fenetre et ses paramètre (taille)
		
		Label menuTitre = new Label("Super Simple");
		menuTitre.setId("menuTitre");
		// Création des boutons
		Button newGameButton = new Button("Nouvelle Partie");
		newGameButton.getStyleClass().add("menuBouton");
	    newGameButton.setOnAction(e -> menuController.startNewGame());
	 
		Button exitButton = new Button("Quitter");
		exitButton.getStyleClass().add("menuBouton");
		exitButton.setOnAction(e -> menuController.exitGame());
		
		// Ajout des boutons au contenu de la fenetre
		menuFenetre.getChildren().addAll(menuTitre, newGameButton, exitButton);  
		
		// Recuperation du css 
		scene.getStylesheets().add(getClass().getResource("scene.css").toExternalForm());
		
		// Ouverture de la fenetre
		stage.setScene(scene);  
        stage.setTitle("Super Simple - Menu Principal");  
		stage.show();
	}
}
