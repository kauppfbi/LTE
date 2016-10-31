package com.lte.gui;

import java.io.IOException;
import java.sql.SQLException;

import javafx.application.Platform;
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


/**
 * Class Controller0 manages the first Screen
 * @author FelixH
 *
 */
public class Controller0 extends GUIController{
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
	// private ThreadReconstruct controller;
	private Settings settings;
	private GameInfo gameInfo;
	
	public Controller0(MainController mainController) {
		this.controller = mainController;
	}
	
	/*
	 * Getter and Setter
	 */
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

	/**
	 * buttonPressed event Handler
	 * Changes Layout from Layout0 to Game-Screen
	 * @param event
	 * @throws IOException
	 */
	// *******************Switch von Welcome zu Game Screen********************
	@FXML
	public void buttonPressed(ActionEvent event) throws IOException {
		Stage stage;
		if (event.getSource() == toGame) {
			// Set team-names
			//String nameX = playerX.getText();
			String nameO = playerO.getValue();

			// new Settings object
			settings = new Settings();
			controller.setSettings(settings);
			// pass opponennt name
			gameInfo = new GameInfo(nameO);
			controller.setGameInfo(gameInfo);

			stage = (Stage) toGame.getScene().getWindow();

			// set Icon
			File file = new File("files/images/icon.png");
			Image image = new Image(file.toURI().toString());
			stage.getIcons().add(image);

			// FXMLLoader
			FXMLLoader loader = new FXMLLoader(getClass().getResource("views/layout1.fxml"));
			loader.setController(controller.getController1());
			
			// set new layout
			stage.setScene(new Scene((AnchorPane) loader.load()));

			stage.show();
		}
	}

	
	/**
	 * Change Screen from Screen0 to Reconstruction-Screen
	 * @param event
	 * @throws IOException
	 */
	public void reconstructGame(ActionEvent event) throws IOException {
		Stage stage;
		if (event.getSource() == reGame) {
			stage = (Stage) toGame.getScene().getWindow();

			// set Icon
			File file = new File("files/images/icon.png");
			Image image = new Image(file.toURI().toString());
			stage.getIcons().add(image);
			
			// FXMLLoader
			FXMLLoader loader = new FXMLLoader(getClass().getResource("views/layout2.fxml"));
			loader.setController(controller.getController2());
			
			//set new layout
			stage.setScene(new Scene((AnchorPane) loader.load()));
			
			stage.show();
		}
	}

	/**
	 * JavaFX initializations 
	 */
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
		AiVsAi.setSelected(true);
		AiVsPlayer.setToggleGroup(tgroup);
		PlayerVsPlayer.setToggleGroup(tgroup);
	}

	
	@FXML
	public void exitApplication(ActionEvent event) {
		Platform.exit();
	}
}