package com.lte;

import java.io.File;
import java.io.IOException;

import com.lte.controller.DBconnection;
import com.lte.controller.MainController;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * main class which starts all components and controllers
 * @author kauppfbi
 *
 */
public class App extends Application{

	// Stage ist der Au�encontainer, unique
	private Stage stage;

	// DB Connection
	private DBconnection connection;
	private MainController controller;

	public void start(Stage stage) throws IOException {
		this.stage = stage;
		this.stage.setTitle("4 Gewinnt!");

		try {
			// initialize DB
			connection = new DBconnection();
			
			// Main Controller
			controller = new MainController(connection);
			// FXMLLoader
			FXMLLoader loader = new FXMLLoader(getClass().getResource("gui/views/layoutStart.fxml"));
			loader.setController(controller.getControllerStart());
			stage.setScene(new Scene((AnchorPane) loader.load()));
			// set Icon
			File file = new File("files/images/icon.png");
			Image image = new Image(file.toURI().toString());
			stage.getIcons().add(image);
			
			stage.setOnCloseRequest(
				new EventHandler<WindowEvent>(){
					public void handle(WindowEvent event) {
						controller.shutdownApplication();
					}
				}
			);
			
			stage.setResizable(false);
			stage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// Main-Methode, launched das Layout
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void stop(){
	    System.out.println("Close App, shutdown DB!");
		connection.close();
	}
}
