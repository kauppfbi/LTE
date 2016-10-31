package com.lte.gui;

import java.io.File;
import java.io.IOException;
import java.util.TreeMap;

import com.lte.controller.MainController;
import com.lte.controller.ThreadReconstruct;
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
import javafx.scene.Node;
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
	@FXML
	protected AnchorPane pane;
	
	@FXML
	protected GridPane gameGrid;
	

	// FXML Declarations
	@FXML
	Button pause;
	
	@FXML
	Button next;
	
	@FXML
	Button back;
	
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
	Text metaPlayer0;
	
	@FXML
	Text metaPlayerX;
	
	@FXML
	Text points0;
	
	@FXML
	Text pointsX;
	
	@FXML
	Text currentSet;
	
	// non-FXML Declarations
	private ThreadReconstruct threadReconstruct;
	private SetDB[] sets;
	private GameDB[] games;
	private int gameID;
	private MainController controller;

	// Getter and Setter
	public MainController getController() {
		return controller;
	}

	public void setController(MainController controller) {
		this.controller = controller;
	}
	@FXML
	public void initialize(){
		//Background
		File file = new File("files/images/gameplay.png");
		Image image = new Image(file.toURI().toString());
		imageView.setImage(image);
		
		//Pause, Next, Back default is disabled:
		pause.setDisable(true);
		next.setDisable(true);
		back.setDisable(true);
		
		threadReconstruct = new ThreadReconstruct(this, null);	
	}
	
	
	// *******************Zur�ck zum Startbildschirm**********************
	@FXML
	public void goToStartmenu(ActionEvent event) throws IOException{
		Stage stage; 
	    // Referrenz zur aktuellen Stage herstellen
	    stage = (Stage) pane.getScene().getWindow();
	    // FXMLLoader        
	    FXMLLoader loader = new FXMLLoader(getClass().getResource("views/layout0.fxml"));
		// Neues Layout in eine neue Scene laden und auf die Stage setzen
		stage.setScene(new Scene((AnchorPane) loader.load()));
		    
		// erstellter Controller1 wird geladen und anschlie�end der Agent �bergeben
		Controller0 controller0 = loader.<Controller0>getController();
		controller0.setController(controller);
		    
		stage.show();    
	 }
	
	
	private int [] prepareRecTurns(){
		//System.out.println("GameID:" + gameID);
		//System.out.println("sets beim Spielen: " + sets[0].getSetID());
		
		int recSetNumber = setChoice.getSelectionModel().getSelectedIndex();
		//System.out.println("Setnumber:" + recSetNumber);
		//System.out.println("SetID: " + sets[recSetNumber].getSetID());
		
		int[] recTurns = sets[recSetNumber].getReplayTurns();
		//System.out.println("RecTurns:" + recTurns);
		
		String pointsOpponent = String.valueOf(sets[recSetNumber].getPointsOpponent());
		String pointsOwn = String.valueOf(sets[recSetNumber].getPointsOwn());
		
		String nameOpponent = games[gameID].getOpponentName() ;
		String nameOwn = "LTE";
		
		String numberAllSets = String.valueOf(games[gameID].getNumberOfSets());
		String numberCurrentSet = String.valueOf(recSetNumber);
		
		//metaText.setText(nameOwn + " " + pointsOwn + " | " + pointsOpponent + " " + nameOpponent + "    " + numberCurrentSet + "/" + numberAllSets);
		
		metaPlayer0.setText(nameOwn);
		metaPlayerX.setText(nameOpponent);
		points0.setText(pointsOwn);
		pointsX.setText(pointsOpponent);
		currentSet.setText(numberCurrentSet+" / "+numberAllSets);
		
		return recTurns;
	}
	
	@FXML
	public void playRec(ActionEvent event){
		//Next und Back disabled
		//Pause enabled
		//nextStep.setDisable(true);
		//backStep.setDisable(true);
		
		pause.setDisable(false);
		play.setDisable(true);
//		next.setDisable(true);
//		back.setDisable(true);


		synchronized (threadReconstruct) {
			if (threadReconstruct.getState() == Thread.State.NEW) {
				clearGrid();
				threadReconstruct.setRecTurns(prepareRecTurns());
				threadReconstruct.start();
			} else if (threadReconstruct.getState() == Thread.State.WAITING) {
				threadReconstruct.notify();
			}
		}
	}
	
	// fills in the rec turns into the Gridpane
	/**
	 * fillRec method replays the turns of the selected set into the GridPane gameGrid<br>
	 * fillRec is called by playRec-method<br>
	 * playRec-method listens to Button "Play"<br>
	 * 
	 * @param recTurns
	 */
	public void replayTurn (int columnIndex, int rowIndex, int color) {
		Circle circle = new Circle();
		circle.setRadius(35.0);

		if (color == 0) {	
			// blue
			circle.setFill(Color.web("#62dbee", 0.85));
		} else if (color == 1) {
			// green
			circle.setFill(Color.web("#46c668", 0.8));
		}	
			
		GridPane.setColumnIndex(circle, columnIndex);
		GridPane.setRowIndex(circle, (5 - rowIndex));
		gameGrid.getChildren().add(circle);
		gameGrid.setHalignment(circle, HPos.CENTER);	
	}

	
	
	@FXML
	public void pauseRec(ActionEvent event) {
		play.setDisable(false);
		pause.setDisable(true);
		synchronized (threadReconstruct) {
			System.out.println(threadReconstruct.getState());
			if (threadReconstruct.getState() == Thread.State.RUNNABLE || threadReconstruct.getState() == Thread.State.TIMED_WAITING) {
				threadReconstruct.interrupt();
			} else {
				threadReconstruct.notify();
			}
		}	
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
		
		// ohne "ChangeEvent" ist der erste Wert standardmaessig der ausgewaehlte Wert, bzw. GameID
		gameID = gameChoice.getSelectionModel().getSelectedIndex();

		//setChoice shows first entry without ChangeListener
		gameID = gameChoice.getSelectionModel().getSelectedIndex();// testing purpose
		System.out.println("Rekonstruierbares Spiel: (Index, gameID)" + gameID + ", " + connection.get(gameID));// for setChoice
		System.out.println("gameID beim konfigurieren: " + gameID);
		sets = controller.getRecSetInfo(gameID);
		System.out.println("sets beim konfigurieren: " + sets[0].getSetID());
		ObservableList<Integer> setNumber = FXCollections.observableArrayList();// setNumber ObservableList gets filled with the number of played Sets
		for(int i = 1; i <= sets.length; i++){
			setNumber.add(i);
		}
		setChoice.setItems(setNumber);
		setChoice.getSelectionModel().selectFirst();
		
		
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
	
	@FXML
	/**
	 * clearGrid Methodes cleans the gameGrid
	 */
	public void clearGrid() {
		Node node = gameGrid.getChildren().get(0);
	    gameGrid.getChildren().clear();
	    gameGrid.getChildren().add(0,node);
	}
}
