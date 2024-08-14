package model;

import java.awt.Button;
import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import utils.Constants;

public class Inventory {
    private List<Item> items;
    private BorderPane inventoryPane;
    
    public Inventory() {
        this.items = new ArrayList<>();
        setInventoryPane();
    }

    // Ajoute l'item
    public void addItem(Item item) {
        items.add(item);
    }

    // Enlever l'objet
    public void removeItem(Item item) {
        items.remove(item);
    }
    
    
    public Item getItemByName(String name) {
    	for (Item item : this.items) {
			if (item.getName().equals(name)) { // Utiliser .equals pour comparer les chaînes
	            return item;
	        }
    	}
        return null; // Retourner null si le nom ne correspond pas
	}
    	
    // verifie s'il y a l'objet
    public boolean hasItem(Item item) {
        return items.contains(item);
    }

    // renvoie tout l'inventaire
    public List<Item> getItems() {
        return items;
    }
    
    // Pane de l'inventaire 
    private void setInventoryPane() {
    	// Contenu de l'inventaire
    	this.inventoryPane = new BorderPane();
    	this.inventoryPane.setStyle("-fx-background-color: grey;");
    	this.inventoryPane.setPadding(new Insets(10));

		//this.inventoryPane.setLayoutX(300);
		//this.inventoryPane.setLayoutY(400);
		//this.inventoryPane.setAlignment(Pos.CENTER);
        
        //this.inventoryPane = new VBox(10);
		//this.inventoryPane.setStyle("-fx-background-color: grey;");
		//this.inventoryPane.setAlignment(Pos.TOP_RIGHT);
		this.inventoryPane.setPrefSize(Constants.inventaireX, Constants.inventaireY); // Taille de la fenêtre
		
		
    }
    public BorderPane getInventoryPane() {
    	return this.inventoryPane;
    }
    
    public void print(){
    	for (Item item : this.items) {
    		System.out.println(item.getName());
    	}
    }
}