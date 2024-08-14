package model;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

import utils.Constants;

public class World {
	private Pane worldRoot;
    private ArrayList<Node> platforms;
    private ArrayList<Door> doors; // Liste des portes
    private int worldWidth;
    private int worldHeight;
    
    public World(String[] levelData) {
        this.worldRoot = new Pane();
        this.platforms = new ArrayList<>();
        this.doors = new ArrayList<>();
        loadWorld(levelData);
        Rectangle background = new Rectangle(worldWidth, worldHeight, Color.BLACK);
        worldRoot.getChildren().add(0, background);  // Ajouite un fond d'ecran noir 
    }
    
    private void loadWorld(String[] worldData) {
    	worldHeight = worldData.length * Constants.OBSTACLE;  // Obtient la hauteur du monde actuel
        for (int i = 0; i < worldData.length; i++) {
            String line = worldData[i];
            worldWidth = line.length() * Constants.OBSTACLE; // Calculer la largeur du niveau
            for (int j = 0; j < line.length(); j++) {
                switch (line.charAt(j)) {
                    case '0':
                        break;
                    case '1':
                        Node platform = createEntity(j * Constants.OBSTACLE, i * Constants.OBSTACLE, Constants.OBSTACLE, Constants.OBSTACLE, Color.RED);
                        platforms.add(platform);
                        break;
                    case 'y':
                        Door door = createDoor(j * Constants.OBSTACLE, i * Constants.OBSTACLE, Constants.OBSTACLE, Constants.OBSTACLE, Color.YELLOW, "Cle Jaune");
                        this.doors.add(door);
                        break;
                }
            }
        }
    }
    
    public int getWorldWidth() {
        return worldWidth;
    }

    public int getWorldHeight() {
        return worldHeight;
    }
    
    private Door createDoor(int x, int y, int w, int h, Color color, String id) {
        Door door = new Door(x, y, w, h, color, id);
        worldRoot.getChildren().add(door);
        return door;
    }
    
    private Node createEntity(int x, int y, int w, int h, Color color) {
        Rectangle entity = new Rectangle(w, h);
        entity.setTranslateX(x);
        entity.setTranslateY(y);
        entity.setFill(color);
        worldRoot.getChildren().add(entity);
        return entity;
    }
    
    public ArrayList<Door> getDoors() {
        return doors;
    }

    
    public Pane getWorldRoot() {
        return worldRoot;
    }

    public ArrayList<Node> getPlatforms() {
        return platforms;
    }
}
