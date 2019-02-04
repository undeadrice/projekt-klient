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
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import studia.projekt.client.Client;
import studia.projekt.client.connection.Bundle;
import studia.projekt.client.connection.Connection;
import studia.projekt.client.connection.ValueEntryModel;
import studia.projekt.client.model.DateModel;
import studia.projekt.client.model.MeasurementEntry;

/**
 * Obsługa przycisków itp. w panelu głównym ( po zalogowaniu )
 */
public class ListController {

	private int selectedIndex = 0;
	
	private ObservableList<MeasurementEntry> allData = FXCollections.observableArrayList();
	private ObservableList<ValueEntryModel> dataObservable = FXCollections.observableArrayList();
	private ObservableList<DateModel> datesObservable = FXCollections.observableArrayList();

	// okno
	private Stage stage;
	// połączenie z serwerem
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

	/**
	 * metoda wykonywana po każdym przełączeniu okna
	 * jej celem jest przekazywanie obiektu połączenia aktualnie funkcjonującemu kontrolerowi
	 * @param stage
	 * @param decorator
	 * @param connection
	 */
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

		valueList.setEditable(true);
		valueList.setPlaceholder(new Label("Proszę wybrać wpis"));
		valueList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		valueCol.setEditable(true);
		valueCol.setCellFactory(TextFieldTableCell.forTableColumn());
		valueCol.setOnEditCommit(a -> onEditCommit(a));

		entryList.getItems().setAll(datesObservable);
		valueList.getItems().setAll(dataObservable);

		parameterCol.setCellValueFactory(new PropertyValueFactory<>("parameter"));
		valueCol.setCellValueFactory(new PropertyValueFactory<>("value"));

		unitCol.setCellValueFactory(new PropertyValueFactory<>("unit"));
		refCol.setCellValueFactory(new PropertyValueFactory<>("reference"));

	}

	/**
	 * wysyła do serwera prośbę o dodanie nowego wpisu z datą jaka jest po stronie klienta
	 * aktualizacja klienta następuje po otrzymaniu informacji zwrotnej od serwera
	 */
	@FXML
	private void add() {
		Bundle b = new Bundle(Connection.addMeasurementByte);
		b.putLong("date", System.currentTimeMillis());
		connection.send(b);
	}

	/**
	 * wysyła do serwera prośbę o usunięcie wpisu, podobnie jak w metodzie add()
	 */
	@FXML
	private void delete() {
		Bundle b = new Bundle(Connection.removeMeasurementByte);
		int id = entryList.getSelectionModel().getSelectedItem().getEntry_id();
		b.putInt("id", id);
		connection.send(b);
	}

	/**
	 * metoda wywoływana przy każdej zmianie wybranej wartości wpisu
	 * wpisy które są po stronie klienta są modyfikowane od razu
	 * @param value
	 */
	private void onEditCommit(CellEditEvent<ValueEntryModel, String> value) {
		Bundle b = new Bundle(Connection.editMeasurementByte);
		int id = entryList.getSelectionModel().getSelectedItem().getEntry_id();
		
		MeasurementEntry m = null;
		for(MeasurementEntry me : allData) {
			if(me.getId().equals(id)) {
				m = me;
				break;
			}
		}
		if(m == null) {
			return;
		}
		
		String paramName = valueList.getSelectionModel().getSelectedItem().getParameter();
		m.setParameter(paramName, Double.parseDouble(value.getNewValue()));
		
		
		b.putDouble("leukocyte", m.getLeukocyte()); 
		b.putDouble("erythrocyte", m.getErythrocyte());
		b.putDouble("hemoglobin", m.getHemoglobin());
		b.putDouble("hematocrit", m.getHematocrit());
		b.putDouble("mcv", m.getMcv());
		b.putDouble("mch", m.getMch());
		b.putDouble("mchc", m.getMchc());
		b.putDouble("platelets", m.getPlatelets());
		b.putDouble("lymphocyte", m.getLymphocyte());
		b.putInt("id", id);
		b.putLong("date", m.getDate());
		update();

		connection.send(b);
	}


	/**
	 * drukuje wybrany wpis przy użyciu domyślnej drukarki
	 */
	@FXML
	private void print() {
		// TODO
	}

	@FXML
	private void logout() throws IOException {
		Bundle b = new Bundle(Connection.logoutByte);
		connection.send(b);
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

	/**
	 * aktualizuje przechowywane wpisy
	 */
	public void update() {
		Platform.runLater(() -> {
			datesObservable.clear();
			for (MeasurementEntry e : allData) {
				datesObservable.add(new DateModel(new Date(e.getDate()), e.getId()));

			}
			if (entryList != null) {
				entryList.setItems(datesObservable);
			}
		});

	};

	/**
	 * metoda wywoływana przy wybraniu daty
	 * ładuje wartości wybranego wpisu do tabeli
	 */
	@FXML
	private void onDateSelected() {
		if (entryList.getSelectionModel().getSelectedItem() != null) {
			selectedIndex = entryList.getSelectionModel().getSelectedIndex();
			int id = entryList.getSelectionModel().getSelectedItem().getEntry_id();
			allData.stream().filter(a -> a.getId().equals(id)).forEach(a -> {
				valueList.getItems().setAll(ValueEntryModel.generateConstantsMale(a));
			});
		}
	}

}
