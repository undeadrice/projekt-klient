package studia.projekt.client;

import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.jfoenix.controls.JFXDecorator;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import studia.projekt.client.connection.Connection;
import studia.projekt.client.controller.LoginController;

public class Client extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		init(stage);

	}

	public void init(Stage stage) throws IOException {
		Connection con = new Connection();
		connectionTest(con);

		LoginController loginController = new LoginController();

		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Client.class.getResource("loginScene.FXML"));
		loader.setController(loginController);
		Parent root = loader.load();

		JFXDecorator decorator = new JFXDecorator(stage, root);
		decorator.setCustomMaximize(true);
		
		Scene scene = new Scene(decorator,640,480);
		
		String uri = Client.class.getResource("orange.css").toExternalForm();
		scene.getStylesheets().add(uri);

		loginController.init(stage, decorator, con);

		stage.setScene(scene);
		stage.show();

	}
	
	public void connectionTest(Connection con) {
		try {
			con.connect("127.0.0.7", 8080);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
