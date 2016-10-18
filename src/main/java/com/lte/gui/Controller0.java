package com.lte.gui;

import java.io.IOException;
import java.sql.SQLException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.File;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import com.lte.controller.Agent;
import com.lte.models.*;

public class Controller0 {

	// import logik.Agent notwendig!!!
	private Agent agent;
	private Settings settings;
	private GameInfo gameInfo;

	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	public Agent getAgent() {
		return agent;
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



	@FXML
	AnchorPane pane;
	
	@FXML
	Button toGame;

	@FXML
	Button reGame;

	@FXML
	TextField playerX;

	@FXML
	TextField playerO;

	@FXML
	ImageView imageView;

	// *******************Switch von Welcome zu Game Screen********************
	@FXML
	public void buttonPressed(ActionEvent event) throws IOException {
		Stage stage;
		if (event.getSource() == toGame) {
			// Team-Namen setzen
			String nameX = playerX.getText();
			String nameO = playerO.getText();

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

			// erstellter Controller1 wird geladen und anschlie�end der Agent
			// �bergeben
			Controller1 controller1 = loader.<Controller1>getController();
			controller1.setAgent(agent);
			controller1.getAgent().setSettings(settings);
			controller1.getAgent().setGameInfo(gameInfo);
			controller1.getAgent().setController1(controller1);
			controller1.setSettings(agent.getSettings());
			controller1.initialize2();

			stage.show();
		}
	}

	// *******************Switch von Welcome zum Rekonstruieren
	// Screen********************
	public void reconstructGame(ActionEvent event) throws IOException {
		Stage stage;
		AnchorPane layout;
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

			// erstellter Controller2 wird geladen und anschlie�end der Agent
			// �bergeben
			Controller2 controller2 = loader.<Controller2>getController();
			controller2.setAgent(agent);
			controller2.getAgent().setController2(controller2);
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
	}
	
	@FXML
	public void exitApplication(ActionEvent event) {
		try {
			agent.getConnection().stmt.close();
			agent.getConnection().con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	    ((Stage)pane.getScene().getWindow()).close();
	}

}