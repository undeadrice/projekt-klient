package studia.projekt.client.controller;

import java.io.IOException;
import java.sql.Date;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDecorator;

import javafx.application.Platform;
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
import studia.projekt.client.connection.Bundle;
import studia.projekt.client.connection.Connection;
import studia.projekt.client.connection.ValueEntryModel;
import studia.projekt.client.model.DateModel;
import studia.projekt.client.model.MeasurementEntry;

public class ListController {

	private ObservableList<MeasurementEntry> allData = FXCollections.observableArrayList();
	private ObservableList<ValueEntryModel> dataObservable = FXCollections.observableArrayList();
	private ObservableList<DateModel> datesObservable = FXCollections.observableArrayList();

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
	private ListView<DateModel> entryList;

	public void init(Stage stage, JFXDecorator decorator, Connection connection) {
		this.stage = stage;
		this.connection = connection;
		this.decorator = decorator;
		connection.setOwnerController(this);
		connection.send(new Bundle(Connection.getMeasurementsByte));

	}

	public void initialize() {

		entryList.getSelectionModel().selectedItemProperty().addListener(a -> {
			onDateSelected();
		});

		entryList.getItems().setAll(datesObservable);
		valueList.getItems().setAll(dataObservable);

		parameterCol.setCellValueFactory(new PropertyValueFactory<>("parameter"));
		valueCol.setCellValueFactory(new PropertyValueFactory<>("value"));
		valueCol.setOnEditCommit(a -> edit());
		unitCol.setCellValueFactory(new PropertyValueFactory<>("unit"));
		refCol.setCellValueFactory(new PropertyValueFactory<>("reference"));

		// załaduj odpowiednie stałe zależnie od płci

	}

	@FXML
	private void add() {
		Bundle b = new Bundle(Connection.addMeasurementByte);
		b.putLong("date", System.currentTimeMillis());
		connection.send(b);
	}

	@FXML
	private void delete() {
		Bundle b = new Bundle(Connection.removeMeasurementByte);
		int id = entryList.getSelectionModel().getSelectedItem().getEntry_id();
		b.putInt("id", id);
		connection.send(b);
	}

	@FXML
	private void edit() {
		Bundle b = new Bundle(Connection.editMeasurementByte);
		int id = entryList.getSelectionModel().getSelectedItem().getEntry_id();
		b.putInt("id", id);
		
		
		
		
	}

	@FXML
	private void print() {

	}

	@FXML
	private void logout() throws IOException {
		switchSceneLogin();
	}

	public ObservableList<MeasurementEntry> getAllData() {
		return allData;
	}

	public void setAllData(ObservableList<MeasurementEntry> allData) {
		this.allData = allData;
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

	public void update() {
		Platform.runLater(() -> {
			datesObservable.clear();
			for (MeasurementEntry e : allData) {
				datesObservable.add(new DateModel(new Date(e.getDate()), e.getId()));

			}
			if (entryList != null) {
				entryList.setItems(datesObservable);
			}
			System.out.println("listobs: " + datesObservable);
			System.out.println("items: " + entryList);
		});

	};

	@FXML
	private void onDateSelected() {

		int id = entryList.getSelectionModel().getSelectedItem().getEntry_id();
		allData.stream().filter(a -> a.getId().equals(id)).forEach(a -> {
			valueList.getItems().setAll(ValueEntryModel.generateConstantsMale(a));
		});

	}

}
