package com.lte.gui;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;
import com.lte.controller.MainController;
import com.lte.features.SoundManager;
import com.lte.models.GameInfo;
import com.lte.models.Settings;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Controller4 Class for Player-Player
 * 
 * @author FelixH
 *
 *
 *         TODO: Siegmustererkennung! Line155: Uebernahme des GegnerNamen
 *         funktioniert noch nicht, ebenso in Con3 Außerdem wird Name1 ncoh auf
 *         default LTE gelassen Selbiger Fehler sollte ggf auch für unser
 *         Hauptprogramm KI-KI angepasst werden Siegmustererkennung muss
 *         RadioButtons wieder freigeben Logik für gewonnene Sätze?!
 *
 *
 *         Problem: nach ein paar geworfenen Steinen -> back to Startmenu ->
 *         wieder auf Screen4 -> keine Steine können mehr geworfen werden
 *
 */
public class Controller4 {

	// FXML Declarations
	@FXML
	Pane gameSet;

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
	GridPane gameGrid;

	@FXML
	Button backToStart;

	@FXML
	ImageView imageView;

	@FXML
	ImageView row1;

	@FXML
	ImageView row2;

	@FXML
	ImageView row3;

	@FXML
	ImageView row4;

	@FXML
	ImageView row5;

	@FXML
	ImageView row6;

	@FXML
	ImageView row7;

	@FXML
	RadioButton radioPlayer1;

	@FXML
	RadioButton radioPlayer2;
	
	@FXML
	Button muteButton;

	// non-FXML Declarations
	private MainController controller;
	private ToggleGroup tgroup;
	private SoundManager soundManager;
	private HashMap<String, Image> images;
	
	//Integer Stones per column -> hight of the row

	private int rowHigh0 = 0;
	private int rowHigh1 = 0;
	private int rowHigh2 = 0;
	private int rowHigh3 = 0;
	private int rowHigh4 = 0;
	private int rowHigh5 = 0;
	private int rowHigh6 = 0;

	// Initial Player
	// char player = 'X';

	public Controller4(MainController mainController) {
		this.controller = mainController;
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

	/**
	 * JavaFX initializations
	 */
	// *******FXML-Methoden************
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

		// set points
		ltePoints.setText("0");
		opponentPoints.setText("0");

		// Background Image
		File file = new File("files/images/gameplay.png");
		Image image = new Image(file.toURI().toString());
		imageView.setImage(image);

		// RadioButton ToggleGroup
		tgroup = new ToggleGroup();
		radioPlayer1.setToggleGroup(tgroup);
		radioPlayer1.setSelected(true);
		radioPlayer2.setToggleGroup(tgroup);

		// set Player name
		namePlayerO.setText(controller.getGameInfo().getOpponentName());
		namePlayerX.setText(controller.getGameInfo().getOwnName());
		System.out.println(controller.getGameInfo().getOwnName());
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
	 * 
	 * @param event
	 * @throws IOException
	 */
	@FXML
	private void goToStartmenu(ActionEvent event) throws IOException {
		controller.setThreadPlayerPlayerNull();

		// Integer Stones per column -> hight of the row
		rowHigh0 = 0;
		rowHigh1 = 0;
		rowHigh2 = 0;
		rowHigh3 = 0;
		rowHigh4 = 0;
		rowHigh5 = 0;
		rowHigh6 = 0;

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

	/**
	 * gameOver-method shows the game-result and asks for the next steps (play
	 * new set, ...)
	 * 
	 * @param winningPlayer
	 * @param winningCombo
	 */
	public void gameOver(byte winningPlayer, int[][] winningCombo) {
		highlightWinning(winningCombo);
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Game Over");
		if (winningPlayer == 1) {
			alert.setHeaderText(controller.getGameInfo().getOwnName() + " hat gewonnen" + "\n" + "Was nun?");
		} else if (winningPlayer == 2) {
			alert.setHeaderText(controller.getGameInfo().getOpponentName() + " hat gewonnen" + "\n" + "Was nun?");
		} else {
			alert.setHeaderText("Unentschieden!" + "\n" + "Was nun?");
		}

		ButtonType weiter = new ButtonType("Weiter spielen");
		ButtonType beenden = new ButtonType("Beenden");

		alert.getButtonTypes().setAll(weiter, beenden);

		Optional<ButtonType> result = alert.showAndWait();

		if (result.get() == weiter) {
			clearGrid();

			// Winner gets one point
			if (winningPlayer == 1) {
				int playerX = Integer.parseInt(ltePoints.getText());
				ltePoints.setText(String.valueOf(playerX + 1));
				controller.getGameInfo().setOwnPoints(playerX);
			} else if (winningPlayer == 2) {
				int playerO = Integer.parseInt(opponentPoints.getText());
				opponentPoints.setText(String.valueOf(playerO + 1));
				controller.getGameInfo().setOpponentPoints(playerO);
			}

			// raise set
			int satz = Integer.parseInt(set.getText());
			set.setText(String.valueOf(satz + 1));
			controller.getGameInfo().setSet(satz);

			// Neues Spiel
			controller.setThreadPlayerPlayerNull();

			// Zeilen zurücksetzen
			rowHigh0 = 0;
			rowHigh1 = 0;
			rowHigh2 = 0;
			rowHigh3 = 0;
			rowHigh4 = 0;
			rowHigh5 = 0;
			rowHigh6 = 0;

			//

		} else if (result.get() == beenden) {

			controller.setThreadPlayerPlayerNull();

			// Integer Stones per column -> hight of the row
			rowHigh0 = 0;
			rowHigh1 = 0;
			rowHigh2 = 0;
			rowHigh3 = 0;
			rowHigh4 = 0;
			rowHigh5 = 0;
			rowHigh6 = 0;

			Stage stage;
			stage = (Stage) backToStart.getScene().getWindow();

			// set Icon
			File file = new File("files/images/icon.png");
			Image image = new Image(file.toURI().toString());
			stage.getIcons().add(image);

			// FXMLLoader
			FXMLLoader loader = new FXMLLoader(getClass().getResource("views/layout0.fxml"));
			loader.setController(controller.getController0());
			try {
				stage.setScene(new Scene((AnchorPane) loader.load()));
			} catch (IOException e) {
				e.printStackTrace();
			}
			stage.show();
		}
	}

	private void addListener(int colIndex, int rowIndex) {
		Pane pane = new Pane();
		pane.setOnMouseClicked(e -> {
			if (controller.getGameInfo().isGameInProgress()) {
				fill(colIndex, getRow(colIndex), controller.getGameInfo().getNextPlayer(), false);
				controller.playTurnPlayerPlayer(colIndex);
				highlightColumn(colIndex, controller.getGameInfo().getNextPlayer());
			}
		});

		pane.setOnMouseEntered(e -> {
			highlightColumn(colIndex, controller.getGameInfo().getNextPlayer());
		});

		pane.setOnMouseExited(e -> {
			deHighlightColumn(colIndex);
		});

		gameGrid.add(pane, colIndex, rowIndex);
	}

	private void highlightColumn(int column, char nextPlayer) {
		for (Node n : gameGrid.getChildren()) {

			if (n instanceof Pane) {
				Pane pane = (Pane) n;
				if (gameGrid.getColumnIndex(pane) == column) {
					if (nextPlayer == 'X') {
						String colorString = "#62bdee";
						pane.setStyle("-fx-background-color: " + colorString + "; -fx-opacity: 0.7");
					} else if (nextPlayer == 'O') {
						String colorString = "#46c668";
						pane.setStyle("-fx-background-color: " + colorString + "; -fx-opacity: 0.7");
					}

				}
			}
		}
	}

	private void deHighlightColumn(int column) {
		for (Node n : gameGrid.getChildren()) {

			if (n instanceof Pane) {
				Pane pane = (Pane) n;
				if (gameGrid.getColumnIndex(pane) == column) {
					pane.setStyle(null);
				}
			}
		}
	}

	/**
	 * Shows the stones corresponding to their position in the field
	 * 
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
		if (columnIndex == 0) {
			rowHigh0++;
		} else if (columnIndex == 1) {
			rowHigh1++;
		} else if (columnIndex == 2) {
			rowHigh2++;
		} else if (columnIndex == 3) {
			rowHigh3++;
		} else if (columnIndex == 4) {
			rowHigh4++;
		} else if (columnIndex == 5) {
			rowHigh5++;
		} else if (columnIndex == 6) {
			rowHigh6++;
		}

		System.out.println(rowHigh0 + " " + rowHigh1 + " " + rowHigh2 + " " + rowHigh3 + " " + rowHigh4 + " " + rowHigh5
				+ " " + rowHigh6);
	}

	/**
	 * clears the field
	 */
	@FXML
	public void clearGrid() {
		Node node = gameGrid.getChildren().get(0);
		gameGrid.getChildren().clear();
		gameGrid.getChildren().add(0, node);

		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 6; j++) {
				addListener(i, j);
			}
		}
	}

	/**
	 * highlights the winning-combo
	 * 
	 * @param woGewonnen
	 */
	public void highlightWinning(int[][] woGewonnen) {
		// Get the positions from the array
		for (int i = 0; i <= 3; i++) {
			int column = woGewonnen[i][0];
			int row = woGewonnen[i][1];
			setHighlight(column, row);
		}
	}

	/**
	 * changes color to highlight the winning-combo
	 * 
	 * @param column
	 * @param row
	 */
	public void setHighlight(int column, int row) {
		// new Circle
		Circle circle2 = new Circle();
		circle2.setRadius(35.0);

		circle2.setFill(Color.web("#FF0000", 0.8));
		GridPane.setColumnIndex(circle2, column);
		GridPane.setRowIndex(circle2, (5 - row));
		gameGrid.getChildren().add(circle2);
		gameGrid.setHalignment(circle2, HPos.CENTER);
	}

	private int getRow(int column) {
		if (column == 0) {
			return rowHigh0;
		}
		if (column == 1) {
			return rowHigh1;
		}
		if (column == 2) {
			return rowHigh2;
		}
		if (column == 3) {
			return rowHigh3;
		}
		if (column == 4) {
			return rowHigh4;
		}
		if (column == 5) {
			return rowHigh5;
		}
		if (column == 6) {
			return rowHigh6;
		}
		return 0;
	}

	/**
	 * fixes the player choice
	 */
	@FXML
	private void fixPlayerChoice() {
		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 6; j++) {
				addListener(i, j);
			}
		}

		if (radioPlayer1.isSelected() == true) {
			controller.getGameInfo().setNextPlayer('X');
			controller.getGameInfo().setStartingPlayer('X');
		} else if (radioPlayer2.isSelected() == true) {
			controller.getGameInfo().setNextPlayer('O');
			controller.getGameInfo().setStartingPlayer('O');
		}

		// Disable RadioButtons
		radioPlayer1.setDisable(true);
		radioPlayer2.setDisable(true);

		controller.getGameInfo().setGameInProgress(true);
	}
	
	/**
	 * Event for leaving the application
	 * @param event
	 */
	public void exitApplication(){
		Platform.exit();
	}
}
