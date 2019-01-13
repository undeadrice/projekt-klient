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
	}
	

	
	@FXML
	private void back() throws IOException {
		switchSceneLogin();
	}
	
	@FXML
	private void createAccount() {
		System.out.println(createLoginFld.getText());
		System.out.println(createPasswordFld.getText());
		System.out.println(createNameFld.getText());
		System.out.println(createSurnameFld.getText());
		System.out.println(createSexFld.getText());
		System.out.println(createCodeFld.getText());
	}
	@FXML
	private void switchSceneLogin() throws IOException {
		
		LoginController loginController = new LoginController();
		loginController.init(stage,decorator,connection);
			
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Client.class.getResource("loginScene.FXML"));
		loader.setController(loginController);
		Parent root = loader.load();

		decorator.setContent(root);
	
		
	

	}
}
