package controller;

import javafx.stage.Stage;

/**
 * Fonction des différents boutons dans le menu
 *
 */
import scene.GameView;
public class MenuController {
	private Stage stage;
	
	public MenuController(Stage stg) {
		this.stage = stg;
	}
	
	public void startNewGame() {
		GameView gameView = new GameView(stage, 0);
        gameView.show();
	}
	
	public void exitGame() {
        stage.close();
    }
}
