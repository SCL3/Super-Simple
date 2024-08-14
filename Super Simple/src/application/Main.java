package application;
	
import javafx.application.Application;
import javafx.stage.Stage;

import scene.Menu;

/**
* Un jeu en Java, avec la bibliothèque JavaFX.
* Pour le projet java de Cy TECH (année universitaire : 2023 - 2024)
*
* @author  Simon CHANTHRABOUTH-LIEBBE
*/

public class Main extends Application {
	/**
	 * Fonction Main qui va demarrer le code JavaFX
	 *
	 */
	@Override
	public void start(Stage primaryStage) throws Exception{  // Fonction nécessaire pour démarrer le programme avec JavaFX
		try {
			Menu menu = new Menu(primaryStage);
			menu.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
