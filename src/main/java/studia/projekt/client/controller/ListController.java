package studia.projekt.client.controller;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDecorator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import studia.projekt.client.Client;
import studia.projekt.client.connection.Connection;
import studia.projekt.client.connection.ValueEntryModel;

public class ListController {

	public static final String key1 = "Parametr";
	public static final String key2 = "Wynik";
	public static final String key3 = "Jednostka";
	public static final String key4 = "Wartość referencyjna";

	private List<ValueEntryModel> data = new ArrayList<>();
	private ObservableList<ValueEntryModel> dataObservable = FXCollections.observableArrayList();
	
	private List<Date> dates = new ArrayList<>();
	private ObservableList<Date> datesObservable = FXCollections.observableArrayList();

	private Stage stage;

	private Connection connection;

	private JFXDecorator decorator;

	@FXML
	private JFXButton addBtn;

	@FXML
	private JFXButton deleteBtn;

	@FXML
	private JFXButton editBtn;

	@FXML
	private JFXButton printBtn;

	@FXML
	private JFXButton logoutBtn;

	@FXML
	private TableView<ValueEntryModel> valueList;

	@FXML
	private TableColumn<ValueEntryModel, String> parameterCol;

	@FXML
	private TableColumn<ValueEntryModel, String> valueCol;

	@FXML
	private TableColumn<ValueEntryModel, String> unitCol;

	@FXML
	private TableColumn<ValueEntryModel, String> refCol;

	@FXML
	private ListView<Date> entryList;

	public void init(Stage stage, JFXDecorator decorator, Connection connection) {
		this.stage = stage;
		this.connection = connection;
		this.decorator = decorator;
	}

	public void initialize() {

	
		entryList.getItems().setAll(datesObservable);
	
		parameterCol.setCellValueFactory(new PropertyValueFactory<>("parameter"));
		valueCol.setCellValueFactory(new PropertyValueFactory<>("value"));
		unitCol.setCellValueFactory(new PropertyValueFactory<>("unit"));
		refCol.setCellValueFactory(new PropertyValueFactory<>("reference"));
		
		
		// załaduj odpowiednie stałe zależnie od płci
		data = ValueEntryModel.generateConstantsMale();
		dataObservable.setAll(data);
		valueList.getItems().setAll(data);
	
		
		
	}
	/**
	 * metoda wywoływana za każdym razem gdy wybierzemy element (date) z listy
	 * 
	 * 
	 */
	private void onListCellSelected() {
		
	}

	@FXML
	private void add() {
		Date date = new Date(System.currentTimeMillis());
		entryList.getItems().add(date);
		// wyślij do serwera!
	}

	@FXML
	private void delete() {

	}

	@FXML
	private void edit() {

	}

	@FXML
	private void print() {

	}

	@FXML
	private void logout() throws IOException {
		switchSceneLogin();
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
