package com.lte.gui;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.TreeMap;

import com.lte.controller.Agent;
import com.lte.models.GameDB;

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
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


public class Controller2 {
	
	// import logik.Agent; notwendig!!!
	private Agent agent;
	
	public void setAgent(Agent agent){
		this.agent = agent;
	}
	
	public Agent getAgent(){
		return agent;
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
	ChoiceBox selectGame;
	
	@FXML
	ImageView imageView;
	
	@FXML
	ChoiceBox gameChoice;
	
	@FXML
	ChoiceBox setChoice;
	
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
		AnchorPane layout;
	    if(event.getSource()==backToStart){
	    	// Referrenz zur aktuellen Stage herstellen
	    	stage = (Stage) backToStart.getScene().getWindow();
	        // FXMLLoader        
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("views/layout0.fxml"));
		    // Neues Layout in eine neue Scene laden und auf die Stage setzen
		    stage.setScene(new Scene((AnchorPane) loader.load()));
		    
		    // erstellter Controller1 wird geladen und anschlie�end der Agent �bergeben
		    Controller0 controller0 = loader.<Controller0>getController();
		    controller0.setAgent(agent);
		    
		    stage.show();    
	    }
	}
	
	@FXML
	public void playRec(ActionEvent event){
		System.out.println(gameID);
		System.out.println(setNumber);
		int[] recGame =  agent.getReplayTurns(gameID, setNumber);
		// klappt leider noch nicht, aktueller Stand.
//		for (int i = 0; i < recGame.length; i++) {
//			System.out.println(recGame[i]);
//		}
	}
	
	public void getRecGameInfo(){
		GameDB[] recGame = agent.getRecGameInfo();
		// GameIDs
		ObservableList<Integer> gameID = FXCollections.observableArrayList();
		// Shown content in gameChoiceBox, Game Info (opponentplayer und playtime)
		ObservableList<String> info = FXCollections.observableArrayList();
		// Connection between content and gameID
		TreeMap<String, Integer> connection = new TreeMap<String, Integer>();
		
		for(int i = 0; i < recGame.length; i++){
			gameID.add(recGame[i].getGameID());
			info.add(recGame[i].getOpponentName().concat(" | ").concat(recGame[i].getPlayTime()));
			connection.put(info.get(i), gameID.get(i));
			System.out.println(gameID.get(i));
		}

		// Initialisiere die ChoiceBox mit den rekonstruierbaren Spielen!
		// PlayerChoice Initialisierung + ChangeListener
		gameChoice.setItems(info);
		gameChoice.getSelectionModel().selectFirst();
		setChoice.getSelectionModel().selectFirst();

//		ChangeListener<Number> listenerGame = new ChangeListener<Number>() {
//			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
//				System.out.println("Rekonstruierbares Spiel: (Index, gameID)" + gameChoice.getSelectionModel().getSelectedIndex() + ", " + game.get(gameChoice.getSelectionModel().getSelectedIndex()));
//				ObservableList<Integer> sets = FXCollections.observableArrayList();
//				for(int i = 1; i <= agent.getRecSetNumber(game.get(gameChoice.getSelectionModel().getSelectedIndex())); i++){
//					sets.add(i);
//				}
//				setChoice.setItems(sets);
//				ChangeListener<Number> listenerSet = new ChangeListener<Number>() {
//					public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
//						gameID = game.get(gameChoice.getSelectionModel().getSelectedIndex());
//						setNumber = sets.get(setChoice.getSelectionModel().getSelectedIndex());
//					}
//				};
//				setChoice.getSelectionModel().selectedIndexProperty().addListener(listenerSet);
//			}
//		};		
//		gameChoice.getSelectionModel().selectedIndexProperty().addListener(listenerGame);
	}
}
