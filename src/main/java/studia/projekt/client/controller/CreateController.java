package studia.projekt.client.controller;

import java.io.IOException;

import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import studia.projekt.client.Client;
import studia.projekt.client.connection.Bundle;
import studia.projekt.client.connection.Connection;

public class CreateController {

	private Stage stage;

	private Connection connection;

	private JFXDecorator decorator;

	@FXML
	private JFXTextField createLoginFld;

	@FXML
	private JFXPasswordField createPasswordFld;

	@FXML
	private JFXTextField createNameFld;

	@FXML
	private JFXTextField createSurnameFld;

	@FXML
	private JFXTextField createSexFld;

	@FXML
	private JFXTextField createCodeFld;

	public void init(Stage stage, JFXDecorator decorator, Connection connection) {
		this.stage = stage;
		this.connection = connection;
		this.decorator = decorator;
		connection.setOwnerController(this);
	}

	@FXML
	private void back() throws IOException {
		switchSceneLogin();
	}

	@FXML
	private void createAccount() {
		Bundle b = new Bundle(Connection.createByte);
		b.putString("login", createLoginFld.getText());
		b.putString("password", createPasswordFld.getText());
		b.putString("name", createNameFld.getText());
		b.putString("surname", createSurnameFld.getText());
		b.putString("code", createCodeFld.getText());
		b.putByte("sex", Byte.valueOf(createSexFld.getText()));
		connection.send(b);

	}

	@FXML
	private void switchSceneLogin() throws IOException {

		LoginController loginController = new LoginController();
		loginController.init(stage, decorator, connection);

		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Client.class.getResource("loginScene.FXML"));
		loader.setController(loginController);
		Parent root = loader.load();

		decorator.setContent(root);

	}

}
