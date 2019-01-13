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

public class LoginController {


	
	private Stage stage;
	
	private Connection connection;
	
	private JFXDecorator decorator;
	
	@FXML
	private JFXTextField loginFld;
	@FXML
	private JFXPasswordField passwordFld;
	
	public void init(Stage stage, JFXDecorator decorator, Connection connection) {
		this.stage = stage;
		this.connection = connection;
		this.decorator = decorator;
	}
	

	
	
	@FXML
	private void login() throws IOException {
		switchSceneList();
		System.out.println(loginFld.getText());
		System.out.println(passwordFld.getText());
		
		
	}
	
	@FXML
	private void switchSceneCreate() throws IOException {
		
		CreateController createController = new CreateController();
		createController.init(stage,decorator,connection);
			
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Client.class.getResource("createScene.FXML"));
		loader.setController(createController);
		Parent root = loader.load();

		decorator.setContent(root);
	



	}
	@FXML
	private void switchSceneList() throws IOException {
		
		ListController listController = new ListController();
		listController.init(stage,decorator,connection);
			
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Client.class.getResource("listScene.FXML"));
		loader.setController(listController);
		Parent root = loader.load();

		decorator.setContent(root);

		
	

	}

}
