package com.lte.gui;

import java.sql.ResultSet;
import java.util.TreeMap;

import com.lte.controller.Agent;
import com.lte.models.GameDB;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Controller2Fabian extends Application {

	final Button button = new Button("Test");
	final TextField subject = new TextField("");
	final TextArea text = new TextArea("");

	public void start(Stage stage, Agent agent) {
		stage.setTitle("Controller2Fabian");
		Scene scene = new Scene(new Group(), 450, 250);

		final ChoiceBox<String> gameChoiceBox = new ChoiceBox<String>();
		final ChoiceBox<Integer> setChoiceBox = new ChoiceBox<Integer>();

		// Initialize GameChoice Box
		// resultSet from DB
		ResultSet res = agent.getRecGameInfo();

		// ArrayList for Choice Box
		ObservableList<String> gameChoiceBoxInput = FXCollections.observableArrayList();

		// Connection between resultset and choiceBox
		TreeMap<String, GameDB> connection = new TreeMap<String, GameDB>();

		// input data in collections
		try {
			GameDB gameDB = null;
			while (res.next()) {
				String inputString = "";

				int gameID = res.getInt(1);
				int opponentID = res.getInt(2);
				String playTime = res.getString(3);
				int pointsOwn = res.getInt(4);
				int pointsOpponent = res.getInt(5);
				String winner = res.getString(6);

				inputString += playTime.substring(0, 16);
				inputString += ", " + opponentID;

				gameChoiceBoxInput.add(inputString);
				
				gameDB = new GameDB(gameID, opponentID, playTime, pointsOwn, pointsOpponent, winner);
				connection.put(inputString, gameDB);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// set Items/input Strings in choiceBox
		gameChoiceBox.setItems(gameChoiceBoxInput);

		// set Tooltip
		gameChoiceBox.setTooltip(new Tooltip("Select a game to reconstruct"));

		// add ChangeListener
		gameChoiceBox.valueProperty().addListener(new ChangeListener<String>() {
			// ObservableValue --> JavaFX Bean
			@Override
			public void changed(ObservableValue ov, String selectedBefore, String selectedNow) {
				// read selectedBefore and fill SetChoiceBox
//				int gameID = 0;
//
//				GameDB selectedGame = connection.get(selectedNow);
//				gameID = selectedGame.getGameID();
				
				int gameID = connection.get(selectedNow).getGameID();

				ObservableList<Integer> setChoiceBoxInput = FXCollections.observableArrayList();
				int numberOfSets = agent.getRecSetNumber(gameID);
				for (int i = 1; i <= numberOfSets; i++) {
					setChoiceBoxInput.add(i);
				}
				setChoiceBox.setItems(setChoiceBoxInput);
			}
		});

		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				text.setText(text.getText() + "\n" + "gameSelected: " + gameChoiceBox.getValue() + ", setNumber: "
						+ setChoiceBox.getValue());

			}
		});

		// add elements to grid pane
		// for demonstration reasons
		GridPane grid = new GridPane();
		grid.setVgap(4);
		grid.setHgap(10);
		grid.setPadding(new Insets(5, 5, 5, 5));
		grid.add(new Label("Game: "), 0, 0);
		grid.add(gameChoiceBox, 1, 0);
		grid.add(new Label("Set: "), 2, 0);
		grid.add(setChoiceBox, 3, 0);
		grid.add(text, 0, 2, 4, 1);
		grid.add(button, 0, 3);

		Group root = (Group) scene.getRoot();
		root.getChildren().add(grid);
		stage.setScene(scene);
		stage.show();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {}

}
