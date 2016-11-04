package com.lte.gui;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import com.lte.controller.MainController;

import com.lte.models.GameInfo;
import com.lte.models.Settings;
import com.sun.corba.se.pept.transport.EventHandler;

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
 *TODO: Einbindung der KI, Nach dem Wurf des Spielers muss KI initialisiert werden <br>
 *KI sollte ebenfalls rowHigh1 - rowHigh7 beim Wurf eines Steines hochzählen, damit der Player<br>
 *nicht die Würfe der KI überschreiben kann. <br>
 *Außerdem müssen die Würfe des Players an KI übergeben werden
 *
 *Siegmustererkennung!
 *
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
	
	// non-FXML Declarations
	private MainController controller;
	// private ThreadReconstruct controller;
	private Settings settings;
	private GameInfo gameInfo;
	
	//Integer Stones per column -> hight of the row
	private int rowHigh0 = 0;
	private int rowHigh1 = 0;
	private int rowHigh2 = 0;
	private int rowHigh3 = 0;
	private int rowHigh4 = 0;
	private int rowHigh5 = 0;
	private int rowHigh6 = 0;
	
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
		//namePlayerO.setText(controller.getGameInfo().getOpponentName());
		
		//imageView to let the player choose the row to throw stones
		File file2 = new File("files/images/Pfeil_unten_bearbeitet.png");
		Image image2 = new Image(file2.toURI().toString());
		row1.setImage(image2);
		row2.setImage(image2);
		row3.setImage(image2);
		row4.setImage(image2);
		row5.setImage(image2);
		row6.setImage(image2);
		row7.setImage(image2);
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
		controller.getGameInfo().setNextPlayer('O');
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
			
			//Neues Spiel
			controller.setThreadPlayerKiNull();
			
			//Zeilen zurücksetzen
			rowHigh0 = 0;
			rowHigh1 = 0;
			rowHigh2 = 0;
			rowHigh3 = 0;
			rowHigh4 = 0;
			rowHigh5 = 0;
			rowHigh6 = 0;
			
			//

		} else if (result.get() == beenden) {
			// TODO altes Controller Modell verwerfen und dem Agenten mitteilen
			controller.setThreadPlayerKiNull();
			
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
	 * @throws InterruptedException 
	 * 
	 */

	// ************************Fill-Methode***************************
	public void fill(int columnIndex, int rowIndex, char player, boolean endGame) throws InterruptedException {
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
		if(columnIndex == 0){rowHigh0++;}
		else if(columnIndex == 1){rowHigh1++;}
		else if(columnIndex == 2){rowHigh2++;}
		else if(columnIndex == 3){rowHigh3++;}
		else if(columnIndex == 4){rowHigh4++;}
		else if(columnIndex == 5){rowHigh5++;}
		else if(columnIndex == 6){rowHigh6++;}
		
		System.out.println(rowHigh0 + " " + rowHigh1 + " " + rowHigh2 + " " + rowHigh3 + " " + rowHigh4 + " " + rowHigh5 + " " + rowHigh6);
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
	 * Mouse Event to let the User select the row<br>
	 * to throw the stone<br>
	 * @param e
	 * @throws InterruptedException 
	 */
	@FXML
	private void mouseClicked(MouseEvent e) throws InterruptedException {
		//fill(int columnIndex, int rowIndex, char player, boolean endGame)
		Node node = (Node) e.getSource();
		System.out.println("Node: "+ node.getId());
		if(node.getId().equals("row1")){
			if(rowHigh0<=5){
				fill(0,rowHigh0,'O',false);
				int nextMove = controller.playTurn(0);
				fill(nextMove, getRow(nextMove), 'X', false);
			}
		}else if(node.getId().equals("row2")){
			if(rowHigh1<=5){
				fill(1,rowHigh1,'O',false);
				int nextMove = controller.playTurn(1);
				fill(nextMove, getRow(nextMove), 'X', false);
			}
		}else if (node.getId().equals("row3")){
			if(rowHigh2<=5){
				fill(2,rowHigh2,'O',false);
				int nextMove = controller.playTurn(2);
				fill(nextMove, getRow(nextMove), 'X', false);
			}
		}else if (node.getId().equals("row4")){
			if(rowHigh3<=5){
				fill(3,rowHigh3,'O',false);
				int nextMove = controller.playTurn(3);
				fill(nextMove, getRow(nextMove), 'X', false);
			}
		}else if (node.getId().equals("row5")){
			if(rowHigh4<=5){
				fill(4,rowHigh4,'O',false);
				int nextMove = controller.playTurn(4);
				fill(nextMove, getRow(nextMove), 'X', false);
			}
		}else if(node.getId().equals("row6")){
			if(rowHigh5<=5){
				fill(5,rowHigh5,'O',false);
				int nextMove = controller.playTurn(5);
				fill(nextMove, getRow(nextMove), 'X', false);
			}
		}else if(node.getId().equals("row7")){
			if(rowHigh6<=5){
				fill(6,rowHigh6,'O',false);
				int nextMove = controller.playTurn(6);
				fill(nextMove, getRow(nextMove), 'X', false);
			}
		}
    }
	private int getRow(int column){
		if(column == 0){return rowHigh0;}
		if(column == 1){return rowHigh1;}
		if(column == 2){return rowHigh2;}
		if(column == 3){return rowHigh3;}
		if(column == 4){return rowHigh4;}
		if(column == 5){return rowHigh5;}
		if(column == 6){return rowHigh6;}
		return 0;
	}
}
