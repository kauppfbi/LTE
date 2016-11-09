package com.lte.gui;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;
import com.lte.controller.MainController;
import com.lte.features.CredentialsInputDialog;
import com.lte.features.SoundManager;
import com.lte.interfaces.CredentialsManager;
import com.lte.interfaces.InterfaceManager;
import com.lte.models.GameInfo;
import com.lte.models.Settings;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
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
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Class Controller1 manages the Game-Screen
 * It´s the main part of the application and shows among other things the game-field
 * @author FelixH
 *
 */
public class Controller1 {

	// FXML Declarations
	@FXML 
	AnchorPane pane;
	
	@FXML
	Button fileSelect;

	@FXML
	Button startGame;

	@FXML
	Text ltePoints;

	@FXML
	Text opponentPoints;

	@FXML
	Text namePlayerO;

	@FXML
	Label namePlayerX;

	@FXML
	Text set;

	@FXML
	ChoiceBox<String> dataTrans;

	@FXML
	Spinner<Double> timeSpinner;

	@FXML
	GridPane gameGrid;

	@FXML
	Button backToStart;

	@FXML
	ImageView imageView;

	@FXML
	Text textKontaktpfad;

	@FXML
	ChoiceBox<String> playerChoice;
	
	@FXML
	Button muteButton;
	

	// non-FXML Declarations
	private MainController controller;
	private Settings settings;
	private GameInfo gameInfo;
	final FileChooser fileChooser;
	private SoundManager soundManager;
	private HashMap<String, Image> images;

	String playerX = "defaultX";
	String playerO = "defaultO";

	public Controller1(MainController mainController) {
		this.controller = mainController;
		this.settings = mainController.getSettings();
		this.fileChooser = new FileChooser();
		this.gameInfo = mainController.getGameInfo();
		this.soundManager = controller.getSoundManager();		
		this.images = controller.getImages();
	}

	// Getter and Setter
	public MainController getController() {
		return controller;
	}

	public void setController(MainController controller) {
		this.controller = controller;
	}

	public Settings getSettings() {
		return settings;
	}

	public void setSettings(Settings settings) {
		this.settings = settings;
	}

	
	/**
	 * JavaFX initializations
	 */
	@FXML
	public void initialize() {
		Status status = soundManager.getStatus();
		if (status == Status.PAUSED) {
			muteButton.setGraphic(new ImageView(images.get("speaker1-mute")));
		} else if (status == Status.PLAYING) {
			muteButton.setGraphic(new ImageView(images.get("speaker1")));
		}
		muteButton.setStyle("-fx-background-color: transparent;");
		
		set.setText("0");

		//set points
		ltePoints.setText("0");
		opponentPoints.setText("0");

		// set fileSelect to disable
		fileSelect.setDisable(true);

		// ChoiceBox initialization + ChangeListener
		// pusher is default
		dataTrans.setItems(FXCollections.observableArrayList("Pusher", "Datei", "Pusher JSON"));
		dataTrans.getSelectionModel().selectFirst();
		ChangeListener<Number> listener = new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				System.out.println("Ausgewaehlte Schnittstelle:" + dataTrans.getSelectionModel().getSelectedIndex());
				if (dataTrans.getSelectionModel().getSelectedIndex() == 0) {
					settings.setInterfaceType(InterfaceManager.EVENT_TYPE);
					settings.setContactPath(null);
					fileSelect.setDisable(true);
				} else if (dataTrans.getSelectionModel().getSelectedIndex() == 1) {
					settings.setInterfaceType(InterfaceManager.FILE_Type);
					fileSelect.setDisable(false);
				} else if (dataTrans.getSelectionModel().getSelectedIndex() == 2) {
					settings.setInterfaceType(InterfaceManager.EVENT_TYPE_JSON);
					fileSelect.setDisable(true);
				}
			}
		};
		dataTrans.getSelectionModel().selectedIndexProperty().addListener(listener);

		// PlayerChoice initialization + ChangeListener
		playerChoice.setItems(FXCollections.observableArrayList("X", "O"));
		playerChoice.getSelectionModel().selectFirst();
		ChangeListener<Number> listener1 = new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				System.out.println("Ausgewaehlter Player:" + playerChoice.getSelectionModel().getSelectedIndex());
				if (playerChoice.getValue().equals("X")) {
					settings.setServerChar('O');
				} else if (playerChoice.getValue().equals("O")) {
					settings.setServerChar('X');
				}
			}
		};
		playerChoice.getSelectionModel().selectedIndexProperty().addListener(listener1);

		// TimeSpinner initialization + ChangeListener
		timeSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(2, 10, 0.5, 0.1));
		timeSpinner.setEditable(false);
		ChangeListener<Number> listener2 = new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				System.out.println(timeSpinner.getValue());
				settings.setCalculationTime(timeSpinner.getValue());
			}
		};
		timeSpinner.valueProperty().addListener(listener2);

		// initialization FileChooser
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Kontaktpfad w�hlen");

		// Background Image
		File file = new File("files/images/gameplay.png");
		Image image = new Image(file.toURI().toString());
		imageView.setImage(image);
		
		namePlayerX.setText("LTE");
		namePlayerO.setText(controller.getGameInfo().getOpponentName());
	}

	@FXML
	private void mute(ActionEvent event){
		Status status = soundManager.playPause();
		if (status != null) {
			if (status == Status.PAUSED) {
				muteButton.setGraphic(new ImageView(images.get("speaker1-mute")));
			} else if (status == Status.PLAYING) {
				muteButton.setGraphic(new ImageView(images.get("speaker1")));
			}
		}
	}

	/**
	 * Back to Screen0
	 * @param event
	 * @throws IOException
	 */
	@FXML
	private void goToStartmenu(ActionEvent event) throws IOException {
		// TODO: don't save the current game/set, if it isn't finished
		
		Stage stage;
		stage = (Stage) backToStart.getScene().getWindow();

		// set Icon
		File file = new File("files/images/icon.png");
		Image image = new Image(file.toURI().toString());
		stage.getIcons().add(image);

		// FXMLLoader
		FXMLLoader loader = new FXMLLoader(getClass().getResource("views/layout0.fxml"));
		loader.setController(controller.getOrCreateController0());
		stage.setScene(new Scene((AnchorPane) loader.load()));

		stage.show();
	}

	/**
	 * DirectoryChooser for file-interface
	 * @return String kontaktpfad
	 */
	@FXML
	private String fileSelect() {
		Stage mainStage = null;
		DirectoryChooser directoryChooser = new DirectoryChooser(); //new directoryChooser
		directoryChooser.setTitle("Verzeichnis des Kontaktpfades w�hlen!");
		File selectedFile = directoryChooser.showDialog(mainStage);
		String kontaktpfad = selectedFile.getPath(); //contact-path for FileInterface
		settings.setContactPath(kontaktpfad);
		textKontaktpfad.setText(kontaktpfad);
		System.out.println("Kontaktpfad: " + kontaktpfad);
		return kontaktpfad;
	}

	/**
	 * starts the game set
	 * @param event
	 */
	@FXML
	private void startSet(ActionEvent event) {
		String interfaceType = settings.getInterfaceType();
		if (interfaceType.equals(InterfaceManager.EVENT_TYPE)
				|| interfaceType.equals(InterfaceManager.EVENT_TYPE_JSON)) {
			updateCredentials();
		}
		set.setText(String.valueOf(gameInfo.getSet() + 1));
		gameInfo.setSet(gameInfo.getSet() + 1);
		
		controller.playSet(); //triggers play-method
		
		//Disable settings during game
		startGame.setDisable(true);
		backToStart.setDisable(true);
		timeSpinner.setDisable(true);
		fileSelect.setDisable(true);
		dataTrans.setDisable(true);
		playerChoice.setDisable(true);
	}
	
	/**
	 * Client shows that it is ready to play the set
	 */
	public void showReady(){
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Information");
		alert.setHeaderText("Spiel initialisiert");
		alert.setContentText("Meldung bestätigen und auf Antwort des Servers warten");
		alert.show();
	}

	/**
	 * gameOver-method shows the game-result and asks
	 * for the next steps (play new set, ...)
	 * 
	 * @param winningPlayer
	 * @param winningCombo
	 * @throws IOException 
	 */
	public void gameOver(byte winningPlayer, int[][] winningCombo) throws IOException {
		highlightWinning(winningCombo); //highlights the winning-combo
		
		// Winner gets +1 Set-Point
		if (winningPlayer == 1) {
			int playerX = Integer.parseInt(ltePoints.getText());
			ltePoints.setText(String.valueOf(playerX + 1));
		} else if (winningPlayer == 2) {
			int playerO = Integer.parseInt(opponentPoints.getText());
			opponentPoints.setText(String.valueOf(playerO + 1));
		}
		

		//Change the number of the set
		// Set-Number +1
		set.setText(String.valueOf(gameInfo.getSet()));
		
		// Alert-Dialog (Confirmation-Options: Go on with next Set || exit to Startmenu)
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Game Over"); //Ask the user what the next steps are
		if (winningPlayer == 1) {
			alert.setHeaderText("Sie haben gewonnen!" + "\n" + "Was nun?");
		} else if (winningPlayer == 2) {
			alert.setHeaderText("Sie haben verloren!" + "\n" + "Was nun?");
		} else {
			alert.setHeaderText("Unentschieden!" + "\n" + "Was nun?");
		}
		
		if(!(controller.getGameInfo().getOwnPoints() == 3 || controller.getGameInfo().getOpponentPoints() == 3)){
		ButtonType weiter = new ButtonType("Weiter spielen");
		ButtonType beenden = new ButtonType("Beenden");
		ButtonType changeSettings = new ButtonType("Einstellungen ändern");

		alert.getButtonTypes().setAll(weiter, beenden, changeSettings);

		Optional<ButtonType> result = alert.showAndWait();

		if (result.get() == weiter) {
			clearGrid();
			
			//update set
			set.setText(String.valueOf(gameInfo.getSet() + 1));
			gameInfo.setSet(gameInfo.getSet() + 1);
			

			// start new Set
			controller.playSet();

		} if (result.get() == beenden) {			
			//DB: delete unfinished game
			if(!(controller.getGameInfo().getOwnPoints() == 3 || controller.getGameInfo().getOpponentPoints() == 3)){
			controller.getConnection().deleteUnfinishedGame(controller.getGameInfo().getGameID());
			}
			
			if(!controller.deleteUnfinishedGame()){
				System.err.println("Deleting unfinished Game in DB was not sucessfully!");
			}

			Stage stage;
			stage = (Stage) backToStart.getScene().getWindow();

			// set Icon
			File file = new File("files/images/icon.png");
			Image image = new Image(file.toURI().toString());
			stage.getIcons().add(image);

			// FXMLLoader
			FXMLLoader loader = new FXMLLoader(getClass().getResource("views/layout0.fxml"));
			loader.setController(controller.getOrCreateController0());
			try {
				stage.setScene(new Scene((AnchorPane) loader.load()));
			} catch (IOException e) {
				e.printStackTrace();
			}
			stage.show();
		} else if (result.get() == changeSettings){
			//enable Settings
			startGame.setDisable(false);
			backToStart.setDisable(false);
			timeSpinner.setDisable(false);
			fileSelect.setDisable(false);
			dataTrans.setDisable(false);
			playerChoice.setDisable(false);
			clearGrid();
		}
		}
		else{
		ButtonType beenden = new ButtonType("Beenden");

		alert.getButtonTypes().setAll(beenden);

		Optional<ButtonType> result = alert.showAndWait();

		if (result.get() == beenden) {
			// TODO ggf. Spiel zu Rekonstruieren speichern
			
			Stage stage;
			stage = (Stage) backToStart.getScene().getWindow();

			// set Icon
			File file = new File("files/images/icon.png");
			Image image = new Image(file.toURI().toString());
			stage.getIcons().add(image);

			// FXMLLoader
			FXMLLoader loader = new FXMLLoader(getClass().getResource("views/layout0.fxml"));
			loader.setController(controller.getController0());
			stage.setScene(new Scene((AnchorPane) loader.load()));

			stage.show();
		}
		}
		
	}

	
	/**
	 * Shows the stones corresponding to their position in the field
	 * 
	 */
	// ************************Fill-Method***************************
	public void fill(int columnIndex, int rowIndex, char player, boolean endGame) {
		Circle circle = new Circle();
		circle.setRadius(35.0);

		if (player == 'X') {
			circle.setFill(Color.web("#62dbee", 0.85));
			GridPane.setColumnIndex(circle, columnIndex);
			GridPane.setRowIndex(circle, (5 - rowIndex));
			gameGrid.getChildren().add(circle);
			gameGrid.setHalignment(circle, HPos.CENTER);
		} else if (player == 'O') {
			circle.setFill(Color.web("#46c668", 0.8));
			GridPane.setColumnIndex(circle, columnIndex);
			GridPane.setRowIndex(circle, (5 - rowIndex));
			gameGrid.getChildren().add(circle);
			gameGrid.setHalignment(circle, HPos.CENTER);
		}
	}

	/**
	 * clears the field
	 */
	@FXML
	public void clearGrid() {
		Node node = gameGrid.getChildren().get(0); //saves gameGrid as a node
	    gameGrid.getChildren().clear(); //clears the Grid
	    gameGrid.getChildren().add(0,node); //adds the node
		
	}
	
	/**
	 * highlights the winning-combo
	 * @param woGewonnen
	 */
	public void highlightWinning(int[][] woGewonnen){
		//Get the positions from the array
		for(int i = 0; i<=3; i++){
			int column = woGewonnen[i][0];
			int row = woGewonnen[i][1];
			setHighlight(column, row);
		}
	}
	
	/**
	 * changes color to highlight the winning-combo
	 * @param column
	 * @param row
	 */
	public void setHighlight(int column, int row){
		//new Circle
		Circle circle2 = new Circle();
		circle2.setRadius(35.0);
		
		circle2.setFill(Color.web("#FF0000", 0.8));
		GridPane.setColumnIndex(circle2, column);
		GridPane.setRowIndex(circle2, (5 - row));
		gameGrid.getChildren().add(circle2);
		gameGrid.setHalignment(circle2, HPos.CENTER);
	}
	
	/**
	 * updates the Credentials
	 */
	private void updateCredentials(){
		CredentialsManager credentialsManager = new CredentialsManager();
		String [] defaultCredentials = credentialsManager.readCredentials();
		
		
		CredentialsInputDialog dialog = new CredentialsInputDialog(defaultCredentials);
		String [] selectedCredentials = dialog.getResult();
		
		if (selectedCredentials == null){
			System.err.println("Credentials wurden nicht gewählt!");
		} else {
			settings.setCredentials(selectedCredentials);
			credentialsManager.setCredentials(selectedCredentials);
		}
		
	}
	
	/**
	 * Event for leaving the application
	 * @param event
	 */
	@FXML
	public void exitApplication(WindowEvent event) {
		controller.getConnection().deleteUnfinishedGame(controller.getGameInfo().getGameID());
		if(controller.getPlayingThread() != null){
			controller.getPlayingThread().stop();
		}
		System.out.println("controller1 exit completed");
		Platform.exit();
	}
}