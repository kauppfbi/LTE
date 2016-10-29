package com.lte;

import java.io.File;
import java.io.IOException;

import com.lte.controller.MainController;
import com.lte.db.DBconnection;
import com.lte.gui.Controller0;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
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

	// DB Connection
	DBconnection connection;

	public void start(Stage stage) throws IOException {
		this.stage = stage;
		this.stage.setTitle("4 gewinnt!");

		try {
			// initialize DB
			connection = new DBconnection();
			
			//Main Controller
			MainController controller = new MainController(connection);
			

			// FXMLLoader
			FXMLLoader loader = new FXMLLoader(getClass().getResource("./gui/views/layout0.fxml"));
			// Neues Layout in eine neue Scene laden und auf die Stage setzen
			stage.setScene(new Scene((AnchorPane) loader.load()));
			// set Icon
			File file = new File("files/images/icon.png");
			Image image = new Image(file.toURI().toString());
			stage.getIcons().add(image);
			// erstellter Controller1 wird geladen und anschlie�end der Agent
			// �bergeben
			Controller0 controller0 = new Controller0(controller);
			loader.setController(controller0);

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
