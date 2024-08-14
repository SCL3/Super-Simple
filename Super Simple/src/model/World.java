package model;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

import items.Stone;
import items.Sword;
import utils.Constants;

/**
 * Fonction qui génère les mondes grâce a la matrice dans WorldData
 *
 */
public class World {
	private Pane worldRoot;
    private ArrayList<Node> platforms;
    private ArrayList<Door> doors; // Liste des portes
    private ArrayList<Item> items; // Liste des items
    private ArrayList<Mob> mobs; // Liste des mobs
    private int worldWidth;
    private int worldHeight;
    
    public World(String[] levelData) {
        this.worldRoot = new Pane();
        this.platforms = new ArrayList<>();
        this.doors = new ArrayList<>();
        this.items = new ArrayList<>();
        this.mobs = new ArrayList<>();
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
                    case 's':
                        Sword sword = createSword(j * Constants.OBSTACLE, i * Constants.OBSTACLE, Color.DARKGREY);
                        this.items.add(sword);
                        break;
                    case 'm':
                    	//mob = new Mob(Constants.DIMENSION*8, currentWorld.getWorldHeight()- Constants.OBSTACLE*3,  80, 80, Color.WHITE, 100, 3);
                        Mob mob = createMob(j * Constants.OBSTACLE, i * Constants.OBSTACLE,  80, 80, Color.WHITE, 100, 3);
                        this.mobs.add(mob);
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
    
    public void removeItem(Item item) {
    	items.remove(item);
    	worldRoot.getChildren().remove(item.getVisual());
    }
    
    // Sword /////////////////
    private Sword createSword(int x, int y, Color col) {
    	Sword sword = new Sword("epee", col);
    	sword.createVisual(x, y);
    	worldRoot.getChildren().add(sword.getVisual());
    	return sword;
    }
    
    // Door //////////////////////////////
    private Door createDoor(int x, int y, int w, int h, Color color, String id) {
        Door door = new Door(x, y, w, h, color, id);
        worldRoot.getChildren().add(door);
        return door;
    }
    
    // Mob /////////////////
    private Mob createMob(double x, double y, double width, double height, Color col, double speed, int health) {
    	Mob mob = new Mob(x, y, width, height, col, speed, health);
    	Stone stone = new Stone("Caillou", Color.VIOLET);
    	mob.getInventory().addItem(stone);
    	worldRoot.getChildren().add(mob.getMob());
    	return mob;
    }
    
    // Wall /////////////////////
    private Node createEntity(int x, int y, int w, int h, Color color) {
        Rectangle entity = new Rectangle(w, h);
        entity.setTranslateX(x);
        entity.setTranslateY(y);
        entity.setFill(color);
        worldRoot.getChildren().add(entity);
        return entity;
    }
    public void addItem(Item item) {  // Ajoute un item au monde
    	this.items.add(item);
    }
    public ArrayList<Door> getDoors() {
        return doors;
    }
    public ArrayList<Item> getItems() {
        return this.items;
    }
    public ArrayList<Mob> getMobs() {
        return mobs;
    }
    public Pane getWorldRoot() {
        return worldRoot;
    }

    public ArrayList<Node> getPlatforms() {
        return platforms;
    }
}
