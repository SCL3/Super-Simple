package scene;

import controller.GameController;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import model.World;
import utils.Constants;
import utils.WorldData;


public class GameView {
	private Stage stage;
	private GameController gameController;
	private World currentWorld;
    
	public GameView(Stage stg, int indexWorld) {
		this.stage = stg;
		this.gameController = new GameController();
		this.gameController.setGameView(this); // Passer la vue du jeu au contrôleur du jeu
		loadWorld(indexWorld);
		setupScene();
	}
	
	public Stage getStage() {
		return this.stage;
	}
	
	public void loadWorld(int levelIndex) {
        String[] newWorldData;
        switch (levelIndex) {
            case 1:
            	newWorldData = WorldData.WORLD1;
                break;
            case 2:
            	newWorldData = WorldData.WORLD2;
                break;
            default:
            	newWorldData = WorldData.WORLD1;
        }
        currentWorld = new World(newWorldData);
        gameController.setWorld(currentWorld, levelIndex);
    }
	
	public void setupScene() {
        // Initialisation de la scène du jeu
        Scene scene = new Scene(gameController.getGamePane(), Constants.JEU_WIDTH, Constants.JEU_HEIGTH);
        currentWorld.getWorldRoot().setId("gamePane");
        scene.getStylesheets().add(getClass().getResource("scene.css").toExternalForm());
        scene.setOnKeyPressed(event -> gameController.setKeyPressed(event.getCode(), true));
        scene.setOnKeyReleased(event -> gameController.setKeyPressed(event.getCode(), false));
        stage.setScene(scene);
        stage.centerOnScreen();  // Centre la fenêtre sur l'écran
        stage.setTitle("Super Simple");
    }
	
	
	public void show() {
        stage.show();
    }
}
