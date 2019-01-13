package studia.projekt.client.controller;

import java.io.IOException;

import com.jfoenix.controls.JFXDecorator;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import studia.projekt.client.Client;

public class SceneSwitcher {

	private Stage stage;

	private CreateController createC;
	private LoginController loginC;
	private ListController listC;

	public SceneSwitcher(Stage stage, CreateController createC, LoginController loginC, ListController listC) {
		this.stage = stage;
		this.createC = createC;
		this.loginC = loginC;
		this.listC = listC;
	}

	public void switchLogin() throws IOException {

		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Client.class.getResource("loginScene.FXML"));
		loader.setController(loginC);
		Parent root = loader.load();

		JFXDecorator decorator = new JFXDecorator(stage, root);
		decorator.setCustomMaximize(true);
		Scene scene = new Scene(decorator);
		String uri = Client.class.getResource("orange.css").toExternalForm();
		scene.getStylesheets().add(uri);

		stage.setScene(scene);
		stage.show();

	}

	public void switchCreate() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Client.class.getResource("createScene.FXML"));
		loader.setController(loginC);
		Parent root = loader.load();

		JFXDecorator decorator = new JFXDecorator(stage, root);
		decorator.setCustomMaximize(true);
		Scene scene = new Scene(decorator);
		String uri = Client.class.getResource("orange.css").toExternalForm();
		scene.getStylesheets().add(uri);

		stage.setScene(scene);
		stage.show();

	}

	public void switchList() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Client.class.getResource("listScene.FXML"));
		loader.setController(loginC);
		Parent root = loader.load();

		JFXDecorator decorator = new JFXDecorator(stage, root);
		decorator.setCustomMaximize(true);
		Scene scene = new Scene(decorator);
		String uri = Client.class.getResource("orange.css").toExternalForm();
		scene.getStylesheets().add(uri);

		stage.setScene(scene);
		stage.show();

	}

}
