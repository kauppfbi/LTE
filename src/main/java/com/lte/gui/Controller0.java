package com.lte.gui;

import java.io.IOException;
import java.sql.SQLException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.File;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import com.lte.controller.MainController;
import com.lte.models.*;

public class Controller0 {

	// FXML Declarations
	@FXML
	AnchorPane pane;
	
	@FXML
	Button toGame;

	@FXML
	Button reGame;

	@FXML
	ComboBox<String> playerX;

	@FXML
	ComboBox<String> playerO;

	@FXML
	ImageView imageView;
	
	@FXML
	ListView<String> scoreBoard;

	@FXML
	RadioButton AiVsAi;
	
	@FXML
	RadioButton AiVsPlayer;
	
	@FXML
	RadioButton PlayerVsPlayer;
	
	// non-FXML Declarations
	private MainController controller;
	private Settings settings;
	private GameInfo gameInfo;

	


	// *******************Switch von Welcome zu Game Screen********************
	@FXML
	public void buttonPressed(ActionEvent event) throws IOException {
		Stage stage;
		if (event.getSource() == toGame) {
			// Team-Namen setzen
			//String nameX = playerX.getText();
			String nameO = playerO.getValue();

			// new Settings object
			this.settings = new Settings();
			// pass opponennt name
			this.gameInfo = new GameInfo(nameO);

			// Referrenz zur aktuellen Stage herstellen
			stage = (Stage) toGame.getScene().getWindow();

			// set Icon
			File file = new File("files/images/icon.png");
			Image image = new Image(file.toURI().toString());
			stage.getIcons().add(image);

			// FXMLLoader
			FXMLLoader loader = new FXMLLoader(getClass().getResource("views/layout1.fxml"));
			// Neues Layout in eine neue Scene laden und auf die Stage setzen
			stage.setScene(new Scene((AnchorPane) loader.load()));

			// erstellter Controller1 wird geladen und anschlie�end der AgentSpiele
			// �bergeben
			Controller1 controller1 = loader.<Controller1>getController();
			controller1.setController(controller);
			controller1.getController().setSettings(settings);
			controller1.getController().setGameInfo(gameInfo);
			controller1.getController().setController1(controller1);
			controller1.setSettings(controller.getSettings());
			controller1.initialize2();

			stage.show();
		}
	}

	// *******************Switch von Welcome zum Rekonstruieren
	// Screen********************
	public void reconstructGame(ActionEvent event) throws IOException {
		Stage stage;
		if (event.getSource() == reGame) {
			// Referrenz zur aktuellen Stage herstellen
			stage = (Stage) toGame.getScene().getWindow();

			// set Icon
			File file = new File("files/images/icon.png");
			Image image = new Image(file.toURI().toString());
			stage.getIcons().add(image);
			
			// FXMLLoader
			FXMLLoader loader = new FXMLLoader(getClass().getResource("views/layout2.fxml"));
			// Neues Layout in eine neue Scene laden und auf die Stage setzen
			stage.setScene(new Scene((AnchorPane) loader.load()));

			// erstellter Controller2 wird geladen und anschlie�end der AgentSpiele
			// �bergeben
			Controller2 controller2 = loader.<Controller2>getController();
			controller2.setController(controller);
			controller2.getController().setController2(controller2);
			controller2.getRecGameInfo();
			
			stage.show();
		}
	}

	@FXML
	public void initialize() {
		// Background Image
		File file = new File("files/images/Screen0.png");
		Image image = new Image(file.toURI().toString());
		imageView.setImage(image);
		
		//name playerX default LTE
		playerX.setEditable(true);
		playerX.setValue("LTE");
		
		//set playerO editable
		playerO.setEditable(true);
		
		//initialize ToggleGroup for RadioButtons
		ToggleGroup tgroup = new ToggleGroup();
		AiVsAi.setToggleGroup(tgroup);
		AiVsPlayer.setToggleGroup(tgroup);
		PlayerVsPlayer.setToggleGroup(tgroup);
	}
	
	@FXML
	public void exitApplication(ActionEvent event) {
		try {
			controller.getConnection().stmt.close();
			controller.getConnection().con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	    ((Stage)pane.getScene().getWindow()).close();
	}
	
	/*
	 * Getter and Setter
	 */


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

	public GameInfo getGameInfo() {
		return gameInfo;
	}

	public void setGameInfo(GameInfo gameInfo) {
		this.gameInfo = gameInfo;
	}

}