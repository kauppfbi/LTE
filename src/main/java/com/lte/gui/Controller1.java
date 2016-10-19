package com.lte.gui;



import java.io.File;
import java.io.IOException;
import java.util.Optional;

import com.lte.controller.AgentSpiele;
import com.lte.interfaces.InterfaceManager;
import com.lte.models.Settings;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
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


/*
 * Im Controller wird ein Agent Objekt erstellt, um aus dem Controller mit
 * dem Agent zu kommunizieren. z.B.: F�r User-Eingaben, wie "neues_Spiel",
 * "Spiel_rekonstruieren", ...
 * 
 * Im Agent wird ein Controller Objekt erstellt, damit der Agent mit dem
 * Controller kommunizieren kann. z.B.: F�r Agent-Events, wie
 * "Spiele_Stein", ...
 */
public class Controller1 {

	// *****************layout1 Komponenten*******************
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
	Button BColumn0;

	@FXML
	Button BColumn1;

	@FXML
	Button BColumn2;

	@FXML
	Button BColumn3;

	@FXML
	Button BColumn4;

	@FXML
	Button BColumn5;

	@FXML
	Button BColumn6;

	@FXML
	ImageView imageView;
	
	@FXML
	Text textKontaktpfad;
	
	@FXML
	ChoiceBox playerChoice;
	
	// non fxml-objects
	private AgentSpiele agent;

	private Settings settings;

	final FileChooser fileChooser = new FileChooser();

	//TODO Datenhaltung optimieren --> Informationen aus Agenten!
	String playerX = "defaultX";
	String playerO = "defaultO";


	//Getter and Setter 
	public void setAgent(AgentSpiele agent) {
		this.agent = agent;
	}

	public AgentSpiele getAgent() {
		return agent;
	}

	public Settings getSettings() {
		return settings;
	}

	public void setSettings(Settings settings) {
		this.settings = settings;
	}
	
	//*******FXML-Methoden************
	
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
				if (dataTrans.getSelectionModel().getSelectedIndex() == 0) {
					settings.setServerChar('X');;
				} else if (dataTrans.getSelectionModel().getSelectedIndex() == 1) {
					settings.setServerChar('O');;
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
		File file = new File("files/images/kuenstliche-intellegenz.jpg");
		Image image = new Image(file.toURI().toString());
		imageView.setImage(image);
	}

	@FXML
	private void dataTest() {
		String test = dataTransChoices();
	}


	// *******************Zur�ck zum Startbildschirm**********************
	@FXML
	private void goToStartmenu(ActionEvent event) throws IOException {
		Stage stage;
		AnchorPane layout;
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
		controller0.setAgent(agent);

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
			agent.start();
		}

		// *********************GAME OVER*************************
		public void gameOver(char winningPlayer, int[][] winningCombo) {
			// private void gameOver(char winningPlayer, int[] winningCombo){
			String ergebnis = "default";
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Game Over");
			if(winningPlayer == 'X'){
				alert.setHeaderText("Sie haben gewonnen!" + "\n" + "Was nun?");	
			} else if(winningPlayer == 'O'){
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
				gameGrid.getChildren().clear();
				gameGrid.setGridLinesVisible(true);

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
				AnchorPane layout;
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
				controller0.setAgent(agent);

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
		circle.setRadius(20.0);

		if (player == 'X') {
			circle.setFill(Color.web("#62dbee", 0.85));
			GridPane.setColumnIndex(circle, columnIndex);
			GridPane.setRowIndex(circle, (5-rowIndex));
			gameGrid.getChildren().add(circle);
			gameGrid.setHalignment(circle, HPos.CENTER);
		} else if (player == 'O') {
			circle.setFill(Color.web("#46c668", 0.8));
			GridPane.setColumnIndex(circle, columnIndex);
			GridPane.setRowIndex(circle, (5-rowIndex));
			gameGrid.getChildren().add(circle);
			gameGrid.setHalignment(circle, HPos.CENTER);
		}
	}

	public void initialize2() {
		namePlayerX.setText("LTE");
		namePlayerO.setText(agent.getGameInfo().getOpponentName());

	}
	
	// (Dateischnittstelle oder Pusher) - Felix
	private String dataTransChoices() {
		String selection = "";
		selection = dataTrans.getValue();
		System.out.println("ChoiceBox Auswahl: " + selection);
		return selection;
	}

	
	// Spinner timeSpinner: Auswahl, was das Zeitlimit f�r die jeweilige
	// Berechnung ist - Felix
	private double getTimelimit() {
		double timelimit = 0;
		// timeSpinner Value auslesen
		timelimit = (double) timeSpinner.getValue();
		System.out.println("Rechenzeit= " + timelimit);
		return timelimit;
	}

	
}