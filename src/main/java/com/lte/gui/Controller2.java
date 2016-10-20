package com.lte.gui;

import java.io.File;
import java.io.IOException;
import java.util.TreeMap;

import com.lte.controller.MainController;
import com.lte.models.GameDB;
import com.lte.models.SetDB;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class Controller2 {
	
	private MainController controller;	
	
	public MainController getController() {
		return controller;
	}

	public void setController(MainController controller) {
		this.controller = controller;
	}
	
	int setNumber = 0;
	int gameID = 0;
	int opponentID = 0;
	String playTime = null;
	
	// FXML-Referrenzen
	@FXML
	Button startReconstruction;
	
	@FXML
	Button nextStep;
	
	@FXML
	Button backStep;
	
	@FXML
	Button pause;
	
	@FXML
	Button play;
	
	@FXML
	Button backToStart;
	
	@FXML
	ChoiceBox<?> selectGame;
	
	@FXML
	ImageView imageView;
	
	@FXML
	ChoiceBox<String> gameChoice;
	
	@FXML
	ChoiceBox<Integer> setChoice;
	
	@FXML
	Text metaText;
	
	@FXML
	public void initialize(){
		//Background
		File file = new File("files/images/loadgamescreen.png");
		Image image = new Image(file.toURI().toString());
		imageView.setImage(image);
		
	}
	
	// *******************Zur�ck zum Startbildschirm**********************
	@FXML
	public void goToStartmenu(ActionEvent event) throws IOException{
		Stage stage; 
	    if(event.getSource()==backToStart){
	    	// Referrenz zur aktuellen Stage herstellen
	    	stage = (Stage) backToStart.getScene().getWindow();
	        // FXMLLoader        
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("views/layout0.fxml"));
		    // Neues Layout in eine neue Scene laden und auf die Stage setzen
		    stage.setScene(new Scene((AnchorPane) loader.load()));
		    
		    // erstellter Controller1 wird geladen und anschlie�end der Agent �bergeben
		    Controller0 controller0 = loader.<Controller0>getController();
		    controller0.setController(controller);
		    
		    stage.show();    
	    }
	}
	
	@FXML
	public void playRec(ActionEvent event){
		//Next und Back disabled
		nextStep.setDisable(true);
		backStep.setDisable(true);
		
		System.out.println(gameID);
		System.out.println(setNumber);
		//************************************HIER WEITER***********************************
		// SetDB[] recGame =  controller.getSetInfos(gameID);
		// klappt leider noch nicht, aktueller Stand.
//		for (int i = 0; i < recGame.length; i++) {
//			System.out.println(recGame[i]);
//		}
	}
	
	@FXML
	public void pauseRec(ActionEvent event){
		//Next und Back disabled to false
		nextStep.setDisable(false);
		backStep.setDisable(false);
	}
	
	
	// Methode zum Befuellen der Choice Boxes und zum Anfuegen der ChangeListeners
	public void getRecGameInfo(){
	
		GameDB[] recGame = controller.getRecGameInfo();
		// Shown content in gameChoiceBox, Game Info (opponentplayer und playtime)
		ObservableList<String> gameInfo = FXCollections.observableArrayList();
		// Connection between content and gameID
		TreeMap<Integer, Integer> connection = new TreeMap<Integer, Integer>();
		
		for(int i = 0; i < recGame.length; i++){
			int gameID = recGame[i].getGameID();
			System.out.println(gameID);
			gameInfo.add(recGame[i].getOpponentName().concat(" | ").concat(recGame[i].getPlayTime()));
			connection.put(i, gameID);
		}

		// Initialisiere die ChoiceBox mit den rekonstruierbaren Spielen!
		// PlayerChoice Initialisierung + ChangeListener
		gameChoice.setItems(gameInfo);
		gameChoice.getSelectionModel().selectFirst();
		setChoice.getSelectionModel().selectFirst();

		ChangeListener<Number> listenerGame = new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				
				int selectedGameID = gameChoice.getSelectionModel().getSelectedIndex();
				
				// testing purpose
				System.out.println("Rekonstruierbares Spiel: (Index, gameID)" + selectedGameID + ", " + connection.get(selectedGameID));
				
				// for setChoice
				SetDB[] sets = controller.getRecSetInfo(selectedGameID);
				ObservableList<Integer> setNumber = FXCollections.observableArrayList();
				
				// setNumber ObservableList gets filled with the number of played Sets
				for(int i = 1; i <= sets.length; i++){
					setNumber.add(i);
				}
				setChoice.setItems(setNumber);
			}
		};
		
		gameChoice.getSelectionModel().selectedIndexProperty().addListener(listenerGame);
	}
}
