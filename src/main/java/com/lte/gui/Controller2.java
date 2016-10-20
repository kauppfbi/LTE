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
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
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
	
	SetDB[] sets;
	GameDB[] games;
	int gameID;
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
	GridPane gameGrid;
	
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
		
		System.out.println("GameID:" + gameID);
		System.out.println("sets beim Spielen: " + sets[0].getSetID());
		
		int recSetNumber = setChoice.getSelectionModel().getSelectedIndex();
		System.out.println("Setnumber:" + recSetNumber);
		System.out.println("SetID: " + sets[recSetNumber].getSetID());
		
		int[] recTurns = sets[recSetNumber].getReplayTurns();
		System.out.println("RecTurns:" + recTurns);
		
		String pointsOpponent = String.valueOf(sets[recSetNumber].getPointsOpponent());
		String pointsOwn = String.valueOf(sets[recSetNumber].getPointsOwn());
		
		String nameOpponent = games[gameID].getOpponentName() ;
		String nameOwn = "LTE";
		
		String numberAllSets = String.valueOf(games[gameID].getNumberOfSets());
		String numberCurrentSet = String.valueOf(recSetNumber);
		
		metaText.setText(nameOwn + " " + pointsOwn + " | " + pointsOpponent + " " + nameOpponent + "    " + numberCurrentSet + "/" + numberAllSets);
		
		fillRec(recTurns);
	}
	
	// fills in the rec turns into the Gridpane
	private void fillRec(int[] recTurns) {
		
		
		int rowIndex0 = 0;
		int rowIndex1 = 0;
		int rowIndex2 = 0;
		int rowIndex3 = 0;
		int rowIndex4 = 0;
		int rowIndex5 = 0;
		int rowIndex6 = 0;
		
		for(int i = 1; i < recTurns.length; i++){
			
			Circle circle = new Circle();
			circle.setRadius(20.0);
			int columnIndex = recTurns[i];
			int rowIndex = 0;
			
			switch(recTurns[i]){
				case 0: rowIndex = rowIndex0;
						rowIndex0++;
						break;
				case 1: rowIndex = rowIndex1;
						rowIndex1++;
						break;
				case 2: rowIndex = rowIndex2;
						rowIndex2++;
						break;
				case 3: rowIndex = rowIndex3;
						rowIndex3++;
						break;
				case 4: rowIndex = rowIndex4;
						rowIndex4++;
						break;
				case 5: rowIndex = rowIndex5;
						rowIndex5++;
						break;
				case 6: rowIndex = rowIndex6;
						rowIndex6++;
						break;
			}
			
			if (recTurns[0] == 0) {	
				circle.setFill(Color.web("#62dbee", 0.85));
				recTurns[0] = 1;
			} else if (recTurns[0] == 1) {
				circle.setFill(Color.web("#46c668", 0.8));
				recTurns[0] = 0;
			}	
			GridPane.setColumnIndex(circle, columnIndex);
			GridPane.setRowIndex(circle, (5 - rowIndex));
			gameGrid.getChildren().add(circle);
			gameGrid.setHalignment(circle, HPos.CENTER);		

		}

	}
	
	
	@FXML
	public void pauseRec(ActionEvent event){
		//Next und Back disabled to false
		nextStep.setDisable(false);
		backStep.setDisable(false);
	}
	
	
	// Methode zum Befuellen der Choice Boxes und zum Anfuegen der ChangeListeners
	public void getRecGameInfo(){
	
		games = controller.getRecGameInfo();
		// Shown content in gameChoiceBox, Game Info (opponentplayer und playtime)
		ObservableList<String> gameInfo = FXCollections.observableArrayList();
		// Connection between content and gameID
		TreeMap<Integer, Integer> connection = new TreeMap<Integer, Integer>();
		
		for(int i = 0; i < games.length; i++){
			int gameID = games[i].getGameID();
			System.out.println(gameID);
			gameInfo.add(games[i].getOpponentName().concat(" | ").concat(games[i].getPlayTime()));
			connection.put(i, gameID);
		}

		// Initialisiere die ChoiceBox mit den rekonstruierbaren Spielen!
		// PlayerChoice Initialisierung + ChangeListener
		gameChoice.setItems(gameInfo);
		gameChoice.getSelectionModel().selectFirst();
		setChoice.getSelectionModel().selectFirst();
		
		// ohne "ChangeEvent" ist der erste Wert standardmaessig der ausgewaehlte Wert, bzw. GameID
		gameID = gameChoice.getSelectionModel().getSelectedIndex();

		ChangeListener<Number> listenerGame = new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				
				gameID = gameChoice.getSelectionModel().getSelectedIndex();
				
				// testing purpose
				System.out.println("Rekonstruierbares Spiel: (Index, gameID)" + gameID + ", " + connection.get(gameID));
				
				// for setChoice
				System.out.println("gameID beim konfigurieren: " + gameID);
				sets = controller.getRecSetInfo(gameID);
				System.out.println("sets beim konfigurieren: " + sets[0].getSetID());
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
