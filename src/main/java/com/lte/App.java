package com.lte;

import java.io.IOException;

import com.lte.controller.Agent;
import com.lte.db.DBconnection;
import com.lte.gui.Controller0;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * main class which starts all components and controllers
 * @author kauppfbi
 *
 */
public class App extends Application {

	// Stage ist der Au�encontainer, unique
	private Stage stage;
	private AnchorPane layout0;

	// DB Connection
	DBconnection connection;

	public void start(Stage stage) throws IOException {
		this.stage = stage;
		this.stage.setTitle("4 gewinnt!");

		try {
			// initialize DB
			//connection = new DBconnection();
			connection = null;

			// Agent-Objekt erstellen und dem Controller0 mitgeben
			Agent agent = new Agent(connection);

			// FXMLLoader
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/views/layout0.fxml"));
			// Neues Layout in eine neue Scene laden und auf die Stage setzen
			stage.setScene(new Scene((AnchorPane) loader.load()));
			// erstellter Controller1 wird geladen und anschlie�end der Agent
			// �bergeben
			Controller0 controller0 = loader.<Controller0>getController();
			controller0.setAgent(agent);

			stage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// Main-Methode, launched das Layout
	public static void main(String[] args) {
		launch(args);
	}
}
