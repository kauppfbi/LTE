package com.lte.gui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Optional;

import com.lte.controller.MainController;
import com.lte.interfaces.CredentialsManager;
import com.lte.interfaces.InterfaceManager;
import com.lte.models.Settings;
import com.lte.models.GameScore;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;

public class Controller1 {

	// FXML Declarations
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

	//TODO playerChoice auslesen
	@FXML
	ChoiceBox<String> playerChoice;

	// non-FXML Declarations
	private MainController controller;
	// private ThreadReconstruct controller;
	private Settings settings;
	final FileChooser fileChooser = new FileChooser();

	// TODO Datenhaltung optimieren --> Informationen aus Agenten!
	String playerX = "defaultX";
	String playerO = "defaultO";

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

	// *******FXML-Methoden************
	@FXML
	private void initialize() {

		set.setText("0");

		// Fixset des Punktstandes
		ltePoints.setText("0");
		opponentPoints.setText("0");

		// Da standardmaessig Pusher ausgewaehlt ist, wird der Kontaktpfadbutton
		// erstmal ausgegraut
		fileSelect.setDisable(true);

		// ChoiceBox Initialisierung + ChangeListener
		// Standardwert im Settings Objekt f�r Dateischnittstelle MUSS Pusher
		// sein!
		dataTrans.setItems(FXCollections.observableArrayList("Pusher", "Datei", "Pusher JSON"));
		dataTrans.getSelectionModel().selectFirst();
		ChangeListener<Number> listener = new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				System.out.println("Ausgewaehlte Schnittstelle:" + dataTrans.getSelectionModel().getSelectedIndex());
				if (dataTrans.getSelectionModel().getSelectedIndex() == 0) {
					settings.setInterfaceType(InterfaceManager.EVENT_TYPE);
					settings.setContactPath(null);
					// Ausgrauen des Kontaktpfades
					fileSelect.setDisable(true);
				} else if (dataTrans.getSelectionModel().getSelectedIndex() == 1) {
					settings.setInterfaceType(InterfaceManager.FILE_Type);
					// Einblenden des Kontaktpfades
					fileSelect.setDisable(false);
				} else if (dataTrans.getSelectionModel().getSelectedIndex() == 2) {
					settings.setInterfaceType(InterfaceManager.EVENT_TYPE_JSON);
					// Ausgrauen des Kontaktpfades
					fileSelect.setDisable(true);
				}
			}
		};
		dataTrans.getSelectionModel().selectedIndexProperty().addListener(listener);

		// PlayerChoice Initialisierung + ChangeListener
		playerChoice.setItems(FXCollections.observableArrayList("X", "O"));
		playerChoice.getSelectionModel().selectFirst();
		ChangeListener<Number> listener1 = new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				System.out.println("Ausgewaehlter Player:" + playerChoice.getSelectionModel().getSelectedIndex());
				if (playerChoice.getValue().equals("X")) {
					settings.setServerChar('X');
				} else if (playerChoice.getValue().equals("O")) {
					settings.setServerChar('O');
				}
			}
		};
		playerChoice.getSelectionModel().selectedIndexProperty().addListener(listener1);

		// TimeSpinner Initialisierung + ChangeListener
		timeSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.5, 10, 0.5, 0.1));
		timeSpinner.setEditable(false);
		ChangeListener<Number> listener2 = new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				System.out.println(timeSpinner.getValue());
				settings.setCalculationTime(timeSpinner.getValue());
			}
		};
		timeSpinner.valueProperty().addListener(listener2);

		// Initialisierung FileChooser
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Kontaktpfad w�hlen");

		// Background Image
		File file = new File("files/images/gameplay.png");
		Image image = new Image(file.toURI().toString());
		imageView.setImage(image);
	}


	// *******************Zur�ck zum Startbildschirm**********************
	@FXML
	private void goToStartmenu(ActionEvent event) throws IOException {
		Stage stage;
		// Referrenz zur aktuellen Stage herstellen
		stage = (Stage) backToStart.getScene().getWindow();

		// set Icon
		File file = new File("files/images/icon.png");
		Image image = new Image(file.toURI().toString());
		stage.getIcons().add(image);

		// FXMLLoader
		FXMLLoader loader = new FXMLLoader(getClass().getResource("views/layout0.fxml"));
		// Neues Layout in eine neue Scene laden und auf die Stage setzen
		stage.setScene(new Scene((AnchorPane) loader.load()));

		// erstellter Controller1 wird geladen und anschlie�end der Agent
		// �bergeben
		Controller0 controller0 = loader.<Controller0>getController();
		controller0.setController(controller);

		stage.show();

	}

	// Button fileSelect: �ffnen eines Dateiexplorers zur Auswahl des jeweiligen
	// Kontaktpfades - Felix
	@FXML
	private String fileSelect() {
		Stage mainStage = null;
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Verzeichnis des Kontaktpfades w�hlen!");
		File selectedFile = directoryChooser.showDialog(mainStage);
		String kontaktpfad = selectedFile.getPath();
		settings.setContactPath(kontaktpfad);
		textKontaktpfad.setText(kontaktpfad);
		System.out.println("Kontaktpfad: " + kontaktpfad);
		return kontaktpfad;
	}

	@FXML
	private void startSet(ActionEvent event) {
		// triggert Spielmethode des Agenten
		// Logik liegt nun beim Agenten
		// Gui visualisiert nur noch "passiv" auf Aufruf des Agenten
		String interfaceType = settings.getInterfaceType();
		if (interfaceType.equals(InterfaceManager.EVENT_TYPE)
				|| interfaceType.equals(InterfaceManager.EVENT_TYPE_JSON)) {
			updateCredentials();
		}
		controller.playSet();
	}
	
	//**********************Show Ready************************
	public void showReady(){
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Information");
		alert.setHeaderText("Spiel initialisiert");
		alert.setContentText("Meldung bestätigen und auf Antwort des Servers warten");
		alert.show();
	}

	// *********************GAME OVER*************************
	public void gameOver(char winningPlayer, int[][] winningCombo) {
		highlightWinning(winningCombo);
		// private void gameOver(char winningPlayer, int[] winningCombo){
		highlightWinning(winningCombo);
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Game Over");
		if (winningPlayer == 'X') {
			alert.setHeaderText("Sie haben gewonnen!" + "\n" + "Was nun?");
		} else if (winningPlayer == 'O') {
			alert.setHeaderText("Sie haben verloren!" + "\n" + "Was nun?");
		} else {
			alert.setHeaderText("Unentschieden!" + "\n" + "Was nun?");
		}

		ButtonType weiter = new ButtonType("Weiter spielen");
		ButtonType beenden = new ButtonType("Beenden");

		alert.getButtonTypes().setAll(weiter, beenden);

		Optional<ButtonType> result = alert.showAndWait();

		if (result.get() == weiter) {
			// Steine wieder entfernen, sauberes Spielfeld
			clearGrid();

			// Gewinner bekommt einen Punkt
			if (winningPlayer == 'X') {
				int playerX = Integer.parseInt(ltePoints.getText());
				ltePoints.setText(String.valueOf(playerX + 1));
			} else if (winningPlayer == 'O') {
				int playerO = Integer.parseInt(opponentPoints.getText());
				opponentPoints.setText(String.valueOf(playerO + 1));
			}

			// Satz um eines Hochz�hlen
			int satz = Integer.parseInt(set.getText());
			set.setText(String.valueOf(satz + 1));

		} else if (result.get() == beenden) {
			// TODO altes Controller Modell verwerfen und dem Agenten mitteilen
			// TODO ggf. Spiel zu Rekonstruieren speichern

			// zur�ck zu Controller0/layout0
			Stage stage;
			// Referrenz zur aktuellen Stage herstellen
			stage = (Stage) backToStart.getScene().getWindow();
			// FXMLLoader
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/views/layout0.fxml"));
			// Neues Layout in eine neue Scene laden und auf die Stage setzen
			try {
				stage.setScene(new Scene((AnchorPane) loader.load()));
			} catch (IOException e) {
				e.printStackTrace();
			}

			// erstellter Controller1 wird geladen und anschlie�end der Agent
			// �bergeben
			Controller0 controller0 = loader.<Controller0>getController();
			controller0.setController(controller);

			stage.show();
		}
	}

	/**
	 * Fill-Methode bef�llt die Felder des gridPanes (= Spielfeld) von layout1
	 * mit den geworfenen Steinen �bergabeparameter: int columnIndex, int
	 * rowIndex, int player, (boolean?) game finished (true/false)
	 */

	// ************************Fill-Methode***************************
	public void fill(int columnIndex, int rowIndex, char player, boolean endGame) {
		// player 0 = red, player 1 = yellow
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

	public void initialize2() {
		namePlayerX.setText("LTE");
		namePlayerO.setText(controller.getGameInfo().getOpponentName());

	}

	//clear GameGrid
	@FXML
	public void clearGrid() {
		Node node = gameGrid.getChildren().get(0);
	    gameGrid.getChildren().clear();
	    gameGrid.getChildren().add(0,node);
		
	}
	
	//highlight winning-combo
	public void highlightWinning(int[][] woGewonnen){
		//int [4][2] --> 4 Steine mit jeweils x und y Pos.
		//[0][0] = Spalte  --  [0][1] = Zeile usw.
		//Methodenaufruf von woGewonnen!!! --> Fabi fragen
		
		//Durchiterieren des Arrays
		for(int i = 0; i<=3; i++){
			int column = woGewonnen[i][0];
			int row = woGewonnen[i][1];
			setHighlight(column, row);
		}
	}
	
	//Einfuegen der neuen Kreise der winning-combo
	public void setHighlight(int column, int row){
		//new Circle
		Circle circle2 = new Circle();
		circle2.setRadius(35.0);
		
		//Ueberschreiben des vorhandenen Kreises
		circle2.setFill(Color.web("#FF0000", 0.8));
		GridPane.setColumnIndex(circle2, column);
		GridPane.setRowIndex(circle2, (5 - row));
		gameGrid.getChildren().add(circle2);
		gameGrid.setHalignment(circle2, HPos.CENTER);
	}
	
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
}