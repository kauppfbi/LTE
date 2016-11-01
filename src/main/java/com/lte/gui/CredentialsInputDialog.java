package com.lte.gui;

import java.util.Optional;

import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class CredentialsInputDialog {
	private String[] credentials;
	private Dialog<String []> dialog;

	public CredentialsInputDialog(String[] defaultCredentials) {
		this.credentials = defaultCredentials;
	}
	
	public String [] getResult(){

		// Create the custom dialog.
		dialog = new Dialog<String[]>();
		dialog.setTitle("App Credentials");
		dialog.setHeaderText("Please insert App Credentials.");

		// Set the button types.
		ButtonType confirmType = new ButtonType("Confirm");
		dialog.getDialogPane().getButtonTypes().addAll(confirmType, ButtonType.CANCEL);

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 20, 10, 10));

		TextField id = new TextField();
		id.setText(credentials[0]);
		TextField key = new TextField();
		key.setText(credentials[1]);
		TextField secret = new TextField();
		secret.setText(credentials[2]);

		grid.add(new Label("App-ID:"), 0, 0);
		grid.add(id, 1, 0);
		grid.add(new Label("App-Key:"), 0, 1);
		grid.add(key, 1, 1);
		grid.add(new Label("App-Secret:"), 0, 2);
		grid.add(secret, 1, 2);

		id.textProperty().addListener((observable, oldValue, newValue) -> {
			credentials[0] = newValue;
		});
		
		key.textProperty().addListener((observable, oldValue, newValue) -> {
			credentials[1] = newValue;
		});
		
		secret.textProperty().addListener((observable, oldValue, newValue) -> {
			credentials[2] = newValue;
		});
		
		dialog.setResultConverter(dialogButton -> {
		    if (dialogButton == confirmType) {
		        return new String [] {id.getText(), key.getText(), secret.getText()};
		    }
		    return null;
		});

		dialog.getDialogPane().setContent(grid);
		Optional<String[]> optionalResult = dialog.showAndWait();
		credentials = null;
		optionalResult.ifPresent(result ->{
			credentials = result;
		});
		return credentials;
	}
}
