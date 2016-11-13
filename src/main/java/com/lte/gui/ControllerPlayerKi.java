package com.lte.gui;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;
import com.lte.controller.MainController;
import com.lte.controller.ThreadPlayerKi;
import com.lte.features.SoundManager;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
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
 * Class for Screen AI vs. User<br>
 * 
 * @author FelixH
 */
public class ControllerPlayerKi {

	// FXML Declarations
	@FXML
	Pane gameSet;

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
	Spinner<Double> timeSpinner;

	@FXML
	GridPane gameGrid;

	@FXML
	Button backToStart;

	@FXML
	ImageView imageView;

	@FXML
	RadioButton radioKi;

	@FXML
	RadioButton radioPlayer;

	@FXML
	Button muteButton;

	private MainController controller;
	private ToggleGroup tgroup;
	private SoundManager soundManager;
	private HashMap<String, Image> images;
	private ThreadPlayerKi threadPlayerKi;

	/**
	 * constructor for ControllerPlayerKi<br>
	 * sets the mainController, soundManager and Images<br>
	 * 
	 * @param mainController
	 */
	public ControllerPlayerKi(MainController mainController) {
		this.controller = mainController;
		this.soundManager = controller.getSoundManager();
		this.images = controller.getImages();
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

		// set points
		ltePoints.setText("0");
		opponentPoints.setText("0");

		// TimeSpinner initialization + ChangeListener
		timeSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.1, 10, 2, 0.1));
		timeSpinner.setEditable(false);
		ChangeListener<Number> listener2 = new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				System.out.println(timeSpinner.getValue());
				controller.getSettings().setCalculationTime(timeSpinner.getValue());
			}
		};
		timeSpinner.valueProperty().addListener(listener2);

		// Background Image
		File file = new File("files/images/gameplay.png");
		Image image = new Image(file.toURI().toString());
		imageView.setImage(image);

		namePlayerX.setText("LTE");

		// RadioButton ToggleGroup
		tgroup = new ToggleGroup();
		radioKi.setToggleGroup(tgroup);
		radioKi.setSelected(true);
		radioPlayer.setToggleGroup(tgroup);

		// set Player name
		namePlayerO.setText(controller.getGameInfo().getOpponentName());
	}

	/**
	 * gameOver-method shows the game-result and asks for the next steps (play
	 * new set, ...)<br>
	 * 
	 * @param winningPlayer
	 * @param winningCombo
	 * @throws IOException
	 */
	public void gameOver(byte winningPlayer, int[][] winningCombo) {
		// highlights the winning-combo
		highlightWinning(winningCombo);

		// Winner gets one point
		if (winningPlayer == 1) {
			int playerX = Integer.parseInt(ltePoints.getText());
			ltePoints.setText(String.valueOf(playerX + 1));
		} else if (winningPlayer == 2) {
			int playerO = Integer.parseInt(opponentPoints.getText());
			opponentPoints.setText(String.valueOf(playerO + 1));
		}

		// Alert-Dialog (Confirmation-Options: Go on with next Set || exit to
		// Startmenu)
		if (!(controller.getGameInfo().getOwnPoints() == 3 || controller.getGameInfo().getOpponentPoints() == 3)) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Game Over"); // Ask the user what the next steps are
			alert.setContentText("Unvollständige Spiele werden nicht gespeichert!");
			if (winningPlayer == 1) {
				alert.setHeaderText("Die KI hat gewonnen!" + "\n" + "Was nun?");
			} else if (winningPlayer == 2) {
				alert.setHeaderText("Sie haben gewonnen!" + "\n" + "Was nun?");
			} else {
				alert.setHeaderText("Unentschieden!" + "\n" + "Was nun?");
			}

			ButtonType weiter = new ButtonType("Weiter spielen");
			ButtonType beenden = new ButtonType("Beenden");

			alert.getButtonTypes().setAll(weiter, beenden);

			Optional<ButtonType> result = alert.showAndWait();

			if (result.get() == weiter) {
				clearGrid();

				// raise set
				int satz = Integer.parseInt(set.getText());
				set.setText(String.valueOf(satz + 1));
				controller.getGameInfo().setSet(satz);

				startNewSet();
			}
			if (result.get() == beenden) {
				// DB: delete unfinished game
				if (!(controller.getGameInfo().getOwnPoints() == 3
						|| controller.getGameInfo().getOpponentPoints() == 3)) {
					controller.deleteUnfinishedGame();
				}

				Stage stage;
				stage = (Stage) backToStart.getScene().getWindow();

				// set Icon
				File file = new File("files/images/icon.png");
				Image image = new Image(file.toURI().toString());
				stage.getIcons().add(image);

				// FXMLLoader
				FXMLLoader loader = new FXMLLoader(getClass().getResource("views/layout0.fxml"));
				loader.setController(controller.getControllerStart());
				try {
					stage.setScene(new Scene((AnchorPane) loader.load()));
				} catch (IOException e) {
					e.printStackTrace();
				}

				stage.show();
			}
		} else {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Game Over");
			// Ask the user what the next steps are
			if (winningPlayer == 1) {
				alert.setHeaderText("Die KI hat gewonnen!" + "\n" + "Das Spiel ist nun entschieden.");
			} else if (winningPlayer == 2) {
				alert.setHeaderText("Sie haben gewonnen!" + "\n" + "Das Spiel ist nun entschieden.");
			} else {
				alert.setHeaderText("Unentschieden!" + "\n" + "Das Spiel ist nun entschieden.");
			}
			ButtonType beenden = new ButtonType("Beenden");

			alert.getButtonTypes().setAll(beenden);

			Optional<ButtonType> result = alert.showAndWait();

			if (result.get() == beenden) {

				Stage stage;
				stage = (Stage) backToStart.getScene().getWindow();

				// set Icon
				File file = new File("files/images/icon.png");
				Image image = new Image(file.toURI().toString());
				stage.getIcons().add(image);

				// FXMLLoader
				FXMLLoader loader = new FXMLLoader(getClass().getResource("views/start.fxml"));
				loader.setController(controller.getControllerStart());
				try {
					stage.setScene(new Scene((AnchorPane) loader.load()));
				} catch (IOException e) {
					e.printStackTrace();
				}
				stage.show();
			}
		}
	}

	/**
	 * Visualize the turns corresponding to their position in the field
	 */
	public void fill(int columnIndex, int rowIndex, char player) {
		// player 0 = red, player 1 = yellow
		Circle circle = new Circle();
		circle.setRadius(35.0);
		addListener((Node) circle, columnIndex, rowIndex);

		if (player == 'X') {
			circle.setFill(Color.web("#62dbee", 0.85));
			GridPane.setColumnIndex(circle, columnIndex);
			GridPane.setRowIndex(circle, (5 - rowIndex));
			GridPane.setHalignment(circle, HPos.CENTER);
			gameGrid.getChildren().add(circle);
		} else if (player == 'O') {
			circle.setFill(Color.web("#46c668", 0.8));
			GridPane.setColumnIndex(circle, columnIndex);
			GridPane.setRowIndex(circle, (5 - rowIndex));
			GridPane.setHalignment(circle, HPos.CENTER);
			gameGrid.getChildren().add(circle);
		}
	}

	/**
	 * pause the Sound<br>
	 * 
	 * @param event
	 */
	@FXML
	private void mute(ActionEvent event) {
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
	 * Back to ScreenStart<br>
	 * 
	 * @param event
	 * @throws IOException
	 */
	@FXML
	private void goToStartmenu(ActionEvent event) throws IOException {
		// terminate running Thread
		if (threadPlayerKi != null) {
			synchronized (threadPlayerKi) {
				threadPlayerKi.stop();
				System.out.println("Thread beendet");
			}
		}

		// DB: delete unfinished game
		if (!(controller.getGameInfo().getOwnPoints() == 3 || controller.getGameInfo().getOpponentPoints() == 3)) {
			controller.deleteUnfinishedGame();
		}

		Stage stage;
		stage = (Stage) backToStart.getScene().getWindow();

		// set Icon
		File file = new File("files/images/icon.png");
		Image image = new Image(file.toURI().toString());
		stage.getIcons().add(image);

		// FXMLLoader
		FXMLLoader loader = new FXMLLoader(getClass().getResource("views/layoutStart.fxml"));
		loader.setController(controller.getControllerStart());
		stage.setScene(new Scene((AnchorPane) loader.load()));

		stage.show();
	}

	/**
	 * starts the game set<br>
	 * 
	 * @param event
	 */
	@FXML
	private void startSet(ActionEvent event) {
		threadPlayerKi = controller.getThreadPlayerKi();

		// RadioButton
		if (radioKi.isSelected() == true) {
			controller.getGameInfo().setNextPlayer('X');
			controller.getGameInfo().setStartingPlayer('X');
			while (true) {
				synchronized (threadPlayerKi) {
					if (threadPlayerKi.isReady()) {
						try {
							threadPlayerKi.setNextMove(-1);
						} catch (Exception e) {
							e.printStackTrace();
						}
						threadPlayerKi.notify();
						break;
					}
				}
			}

		} else if (radioPlayer.isSelected() == true) {
			controller.getGameInfo().setNextPlayer('O');
			controller.getGameInfo().setStartingPlayer('O');
		}

		controller.getGameInfo().setSet(controller.getGameInfo().getSet() + 1);
		set.setText(String.valueOf(controller.getGameInfo().getSet()));

		// PlayerChoice disabled
		radioKi.setDisable(true);
		radioPlayer.setDisable(true);

		// startButton disabled
		startGame.setDisable(true);

		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 6; j++) {
				addListener(i, j);
			}
		}

		controller.getGameInfo().setGameInProgress(true);
	}

	/**
	 * starts a new Set<b>
	 */
	private void startNewSet() {
		threadPlayerKi = controller.getThreadPlayerKi();
		if (controller.getGameInfo().getStartingPlayer() == 'X') {
			while (true) {
				synchronized (threadPlayerKi) {
					if (threadPlayerKi.isReady()) {
						try {
							threadPlayerKi.setNextMove(-1);
							threadPlayerKi.notify();
							break;
						} catch (Exception e) {
							System.out.println("Zug nicht möglich!");
						}
					}
				}
			}
		}

		// PlayerChoice disabled
		radioKi.setDisable(true);
		radioPlayer.setDisable(true);

		// startButton disabled
		startGame.setDisable(true);

		controller.getGameInfo().setGameInProgress(true);
	}

	/**
	 * clears the field
	 */
	@FXML
	private void clearGrid() {
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
	private void highlightWinning(int[][] woGewonnen) {
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
	private void setHighlight(int column, int row) {
		// new Circle
		Circle circle2 = new Circle();
		circle2.setRadius(35.0);

		circle2.setFill(Color.web("#FF0000", 0.8));
		GridPane.setColumnIndex(circle2, column);
		GridPane.setRowIndex(circle2, (5 - row));
		GridPane.setHalignment(circle2, HPos.CENTER);
		gameGrid.getChildren().add(circle2);
	}

	/**
	 * sets listeners on the columns and cells<br>
	 * 
	 * @param colIndex
	 * @param rowIndex
	 */
	private void addListener(int colIndex, int rowIndex) {
		Pane pane = new Pane();
		pane.setOnMouseClicked(e -> {
			if (controller.getGameInfo().isGameInProgress() && controller.getGameInfo().getNextPlayer() == 'O') {
				synchronized (threadPlayerKi) {
					try {
						threadPlayerKi.setNextMove(colIndex);
						controller.getGameInfo().setNextPlayer('X');

						if (threadPlayerKi.getState() == Thread.State.WAITING) {
							threadPlayerKi.notify();
						}
					} catch (Exception e1) {
						System.out.println("Zug nicht möglich!");
					}

				}
			}
		});

		pane.setOnMouseEntered(e -> {
			highlightColumn(colIndex);
		});

		pane.setOnMouseExited(e -> {
			deHighlightColumn(colIndex);
		});

		gameGrid.add(pane, colIndex, rowIndex);
	}

	/**
	 * sets listeners on the visualized circles<br>
	 * 
	 * @param node
	 * @param colIndex
	 * @param rowIndex
	 */
	private void addListener(Node node, int colIndex, int rowIndex) {
		node.setOnMouseClicked(e -> {
			if (controller.getGameInfo().isGameInProgress() && controller.getGameInfo().getNextPlayer() == 'O') {
				synchronized (threadPlayerKi) {
					try {
						threadPlayerKi.setNextMove(colIndex);
						controller.getGameInfo().setNextPlayer('X');

						if (threadPlayerKi.getState() == Thread.State.WAITING) {
							threadPlayerKi.notify();
						}
					} catch (Exception e1) {
						System.out.println("Zug nicht möglich!");
					}
				}
			}
		});

		node.setOnMouseEntered(e -> {
			highlightColumn(colIndex);
		});

		node.setOnMouseExited(e -> {
			deHighlightColumn(colIndex);
		});
	}

	/**
	 * Highlights the hovered column<br>
	 * 
	 * @param column
	 */
	private void highlightColumn(int column) {
		for (Node n : gameGrid.getChildren()) {
			if (n instanceof Pane) {
				Pane pane = (Pane) n;
				if (GridPane.getColumnIndex(pane) == column) {
					pane.setStyle("-fx-background-color: #46c668; -fx-opacity: 0.7");
				}
			}
		}
	}

	/**
	 * Stops highlighting the last column, as soon as hovered over the next
	 * column<br>
	 * 
	 * @param column
	 */
	private void deHighlightColumn(int column) {
		for (Node n : gameGrid.getChildren()) {

			if (n instanceof Pane) {
				Pane pane = (Pane) n;
				if (GridPane.getColumnIndex(pane) == column) {
					pane.setStyle(null);
				}
			}
		}
	}

	/**
	 * Event for leaving the application<br>
	 * 
	 * @param event
	 */
	public void exitApplication() {
		if (threadPlayerKi != null) {
			threadPlayerKi.stop();
		}
		Platform.exit();
	}
}
