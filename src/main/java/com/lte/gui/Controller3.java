package com.lte.gui;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import com.lte.controller.MainController;

import com.lte.models.GameInfo;
import com.lte.models.Settings;

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
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Class for Screen3
 * AI vs. User
 * @author FelixH
 *
 */
public class Controller3{

	//FXML Declarations
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
	
	// non-FXML Declarations
	private MainController controller;
	// private ThreadReconstruct controller;
	private Settings settings;
	private GameInfo gameInfo;
	
	public Controller3(MainController mainController) {
		this.controller = mainController;
		this.settings = mainController.getSettings();
		this.gameInfo = mainController.getGameInfo();
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
	// *******FXML-Methoden************
	@FXML
	public void initialize() {

		set.setText("0");

		//set points
		ltePoints.setText("0");
		opponentPoints.setText("0");

		// TimeSpinner initialization + ChangeListener
		timeSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.5, 10, 0.5, 0.1));
		timeSpinner.setEditable(false);
		ChangeListener<Number> listener2 = new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				System.out.println(timeSpinner.getValue());
				settings.setCalculationTime(timeSpinner.getValue());
			}
		};
		timeSpinner.valueProperty().addListener(listener2);

		// Background Image
		File file = new File("files/images/gameplay.png");
		Image image = new Image(file.toURI().toString());
		imageView.setImage(image);
		
		namePlayerX.setText("LTE");
		namePlayerO.setText(controller.getGameInfo().getOpponentName());
	}
	
	/**
	 * Back to Screen0
	 * @param event
	 * @throws IOException
	 */
	@FXML
	private void goToStartmenu(ActionEvent event) throws IOException {
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
	 * starts the game set
	 * @param event
	 */
	@FXML
	private void startSet(ActionEvent event) {
		//Spiel starten
		controller.playSet();
	}
	
	
	/**
	 * gameOver-method shows the game-result and asks
	 * for the next steps (play new set, ...)
	 * 
	 * @param winningPlayer
	 * @param winningCombo
	 */
	public void gameOver(byte winningPlayer, int[][] winningCombo) {
		highlightWinning(winningCombo);
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Game Over");
		if (winningPlayer == 1) {
			alert.setHeaderText("Sie haben gewonnen!" + "\n" + "Was nun?");
		} else if (winningPlayer == 2) {
			alert.setHeaderText("Sie haben verloren!" + "\n" + "Was nun?");
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
			
			//Neuen Satz starten
			controller.playSet();

		} else if (result.get() == beenden) {
			// TODO altes Controller Modell verwerfen und dem Agenten mitteilen
			// TODO ggf. Spiel zu Rekonstruieren speichern

			// back to Screen0
			Stage stage;
			stage = (Stage) backToStart.getScene().getWindow();
			// FXMLLoader
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/views/layout0.fxml"));
			loader.setController(controller.getController0());
			try {
				stage.setScene(new Scene((AnchorPane) loader.load()));
			} catch (IOException e) {
				e.printStackTrace();
			}

			stage.show();
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
	 * Event for leaving the application
	 * @param event
	 */
	@FXML
	public void exitApplication(ActionEvent event) {
		Platform.exit();
	}
	
	/**
	 * Mouse Event to let the User select the row
	 * to throw the stone
	 * @param e
	 */
	@FXML
	private void mouseEntered(MouseEvent e) {
        Node source = (Node)e.getSource() ;
        Integer colIndex = GridPane.getColumnIndex(source);
        Integer rowIndex = GridPane.getRowIndex(source);
        System.out.printf("Mouse entered cell [%d, %d]%n", colIndex.intValue(), rowIndex.intValue());
    }
}
