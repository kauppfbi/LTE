package com.lte.gui;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Optional;
import java.util.TreeMap;
import com.lte.controller.MainController;
import com.lte.controller.ThreadReconstruct;
import com.lte.features.SoundManager;
import com.lte.models.GameDB;
import com.lte.models.SetDB;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Class Controller2 manages the Reconstruction-Screen
 * @author FelixH
 *
 */
public class Controller2 {
	
	// FXML Declarations
	@FXML
	AnchorPane pane;
	
	@FXML
	GridPane gameGrid;
	
	@FXML
	Button pause;
	
	@FXML
	Button play;
	
	@FXML
	Button stop;
	
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
	
	@FXML
	Button muteButton;
	
	// non-FXML Declarations
	private MainController controller;	
	private ThreadReconstruct threadReconstruct;
	private SetDB[] sets;
	private GameDB[] games;
	private int gameID;
	private SoundManager soundManager;
	private HashMap<String, Image> speakerImages;
	
	
	public Controller2(MainController mainController) {
		this.controller = mainController;
		this.soundManager = controller.getSoundManager();		
		this.speakerImages = controller.getSpeakerImages();
	}

	/**
	 * FXML initializations
	 */
	@FXML
	public void initialize(){
		Status status = soundManager.getStatus();
			if (status == Status.PAUSED) {
				muteButton.setGraphic(new ImageView(speakerImages.get("speaker1-mute")));
			} else if (status == Status.PLAYING) {
				muteButton.setGraphic(new ImageView(speakerImages.get("speaker1")));
			}
		muteButton.setStyle("-fx-background-color: transparent;");
		
		//Background
		File file = new File("files/images/gameplay.png");
		Image image = new Image(file.toURI().toString());
		imageView.setImage(image);
		
		//Pause, Stop default is disabled:
		pause.setDisable(true);
		stop.setDisable(true);
				
		threadReconstruct = new ThreadReconstruct(this, null);	
	}
	
	@FXML
	private void mute(ActionEvent event){
		Status status = soundManager.playPause();
		if (status != null) {
			if (status == Status.PAUSED) {
				muteButton.setGraphic(new ImageView(speakerImages.get("speaker1-mute")));
			} else if (status == Status.PLAYING) {
				muteButton.setGraphic(new ImageView(speakerImages.get("speaker1")));
			}
		}
	}
	
	// TODO onCloseRequest Thread Terminaten
	
	/**
	 * Go back to Screen0
	 * @param event
	 * @throws IOException
	 */
	@FXML
	public void goToStartmenu(ActionEvent event) throws IOException{
		
		// interrupt the Thread, if it was running
		if(threadReconstruct.getState() == Thread.State.RUNNABLE || threadReconstruct.getState() == Thread.State.TIMED_WAITING){
			synchronized(threadReconstruct){
				threadReconstruct.interrupt();
				play.setDisable(false);
			}
		}
		
		// Show Alert, if Rec-Game isn't finished jet
		if(threadReconstruct.getState() != Thread.State.TERMINATED){

			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Zum Startmenu");
			alert.setHeaderText("Wenn Sie nun zum Startmenu wechseln," + "\n" + "geht das aktuell rekonstruierte Spiel verloren!" + "\n\n" + "Trotzdem wechseln?");
			
			ButtonType change = new ButtonType("Wechseln");
			ButtonType cancle = new ButtonType("Abbrechen");
	
			alert.getButtonTypes().setAll(change, cancle);
	
			Optional<ButtonType> result = alert.showAndWait();
			
			if(result.get() == change){
				
				synchronized(threadReconstruct){
					threadReconstruct.stop();
				}
				
				Stage stage; 
			    stage = (Stage) backToStart.getScene().getWindow();
			    // FXMLLoader             
		        FXMLLoader loader = new FXMLLoader(getClass().getResource("views/layout0.fxml"));
		        loader.setController(controller.getController0());
			    stage.setScene(new Scene((AnchorPane) loader.load()));  
				stage.show();
				
			} else if(result.get() == cancle){
				alert.close();
				pause.setDisable(true);
			}
		// Just switch the View, if Rec-Game is terminated	
		} else{
			Stage stage; 
		    // Referrenz zur aktuellen Stage herstellen
		    stage = (Stage) backToStart.getScene().getWindow();
		    // FXMLLoader               
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("views/layout0.fxml"));
	        loader.setController(controller.getController0());
		    stage.setScene(new Scene((AnchorPane) loader.load()));
			stage.show();    
		}
	}
	
	/**
	 * sets the current Set-Meta-Information on the Screen<br>
	 * returns the reconstruct Turns of the current Set<br>
	 * 
	 * @return
	 */
	private int [] prepareRecTurns(){
		//System.out.println("GameID:" + gameID);
		//System.out.println("sets beim Spielen: " + sets[0].getSetID());
		
		int recSetNumber = setChoice.getSelectionModel().getSelectedIndex();
		//System.out.println("Setnumber:" + recSetNumber);
		//System.out.println("SetID: " + sets[recSetNumber].getSetID());
		
		int[] recTurns = sets[recSetNumber].getReplayTurns();
		//System.out.println("RecTurns:" + recTurns);
		
		String pointsOpponent = String.valueOf(sets[recSetNumber].getPointsOpponent()); //saves the meta-information of the game
		String pointsOwn = String.valueOf(sets[recSetNumber].getPointsOwn());
		
		String nameOpponent = games[gameID].getOpponentName() ;
		String nameOwn = "LTE";
		
		String numberAllSets = String.valueOf(games[gameID].getNumberOfSets());
		String numberCurrentSet = String.valueOf(recSetNumber+1);
		
		//shows the meta-information of the game
		metaPlayer0.setText(nameOwn);
		metaPlayerX.setText(nameOpponent);
		points0.setText(pointsOwn);
		pointsX.setText(pointsOpponent);
		currentSet.setText(numberCurrentSet+" / "+numberAllSets);
		
		return recTurns;
	}
	
	/**
	 * is called by the "Play" button<br>
	 * sets the Reconstruct-Thread on RUNNABLE<br>
	 * calls prepareRecTurns() to give the Reconstruct-Thread the recTurns<br>
	 * 
	 * @param event
	 */
	@FXML
	public void playRec(ActionEvent event){
		
		pause.setDisable(false);
		play.setDisable(true);
		stop.setDisable(false);
		gameChoice.setDisable(true);
		setChoice.setDisable(true);


		synchronized (threadReconstruct) {
			if (threadReconstruct.getState() == Thread.State.NEW) {
				threadReconstruct.setRecTurns(prepareRecTurns());
				threadReconstruct.start();
			} else if (threadReconstruct.getState() == Thread.State.TERMINATED){
				threadReconstruct = new ThreadReconstruct(this, null);
				threadReconstruct.setRecTurns(prepareRecTurns());
				threadReconstruct.start();
			} else if (threadReconstruct.getState() == Thread.State.WAITING) {
				threadReconstruct.notify();
			}
		}
	}
	
	/**
	 * called when Reconstruct-Thread of current Set finished<br>
	 * disables navigation buttons (play, pause, stop)<br>
	 * enables Choice-Boxes and allows user to select new game/set<br>
	 */
	public void playRecFinished(){
		play.setDisable(true);
		pause.setDisable(true);
		stop.setDisable(true);
		gameChoice.setDisable(false);
		setChoice.setDisable(false);
	}
	
	
	/**
	 * shows the turns of the selected set in GridPane gameGrid<br>
	 * is called by the Reconstruct-Thread<br>
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

	
	/**
	 * is called by the "pause" button <br>
	 * sets the currently running Reconstruct-Thread to WAIT by interrupting it<br>
	 * 
	 * @param event
	 */
	@FXML
	public void pauseRec(ActionEvent event) {
		
		play.setDisable(false);
		pause.setDisable(true);
		stop.setDisable(false);
		gameChoice.setDisable(true);
		setChoice.setDisable(true);
		
		synchronized (threadReconstruct) {
			System.out.println(threadReconstruct.getState());
			if (threadReconstruct.getState() == Thread.State.RUNNABLE || threadReconstruct.getState() == Thread.State.TIMED_WAITING) {
				threadReconstruct.interrupt();
			} else {
				threadReconstruct.notify();
			}
		}	
	}
	
	
	/**
	 * if button "Abbruch" is clicked<br>
	 * sets the State of threadReconstruction to TERMINATED<br>
	 * allows the user to select another game/set to reconstruct<br>
	 * 
	 * @param event
	 */
	@FXML
	public void stopAction(ActionEvent event){
		
		play.setDisable(true);
		pause.setDisable(true);
		stop.setDisable(true);
		gameChoice.setDisable(false);
		setChoice.setDisable(false);
		
		synchronized(threadReconstruct){
			if(threadReconstruct.getState() != Thread.State.TERMINATED){
				threadReconstruct.stop();
			}
		}
	}
	
	/**
	 * initialization of of ChoiceBoxes and ChangeListener<br>
	 * gets data from Database<br>
	 */
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

		// PlayerChoice initialization + ChangeListener
		gameChoice.setItems(gameInfo);
		gameChoice.getSelectionModel().selectFirst();
		
		// set first entry as default
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
				setChoice.getSelectionModel().selectFirst();
				clearGrid();
				metaPlayer0.setText("");
				metaPlayerX.setText("");
				points0.setText("");
				pointsX.setText("");
				currentSet.setText("");
				play.setDisable(false);
			}
		};
		gameChoice.getSelectionModel().selectedIndexProperty().addListener(listenerGame);
		
		// if there is no game in DB
		if(gameChoice.getItems().isEmpty()){
			play.setDisable(true);
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Fehlermeldung");
			alert.setHeaderText("Leider konnten keine rekonstruierbare Spiele gefunden werden!");
			alert.setContentText("Spiele zuerst ein Spiel, um es anschliessend rekonstruieren zu koennen.");
			alert.showAndWait();
		}
	}
	
	/**
	 * clears the field
	 */
	@FXML
	public void clearGrid() {
		Node node = gameGrid.getChildren().get(0);
	    gameGrid.getChildren().clear();
	    gameGrid.getChildren().add(0,node);	
	}
	
	/**
	 * Event for leaving the application
	 * @param event
	 */
	@FXML
	public void exitApplication(ActionEvent event) {
		Platform.exit();
	}
}
