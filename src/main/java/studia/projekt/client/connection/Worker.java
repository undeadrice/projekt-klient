package studia.projekt.client.connection;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import studia.projekt.client.controller.ListController;
import studia.projekt.client.controller.LoginController;
import studia.projekt.client.model.MeasurementEntry;

public class Worker {

	private ArrayBlockingQueue<Bundle> workQueue;
	private Connection con;

	public Worker(ArrayBlockingQueue<Bundle> workQueue, Connection con) {
		super();
		this.workQueue = workQueue;
		this.con = con;
	}
	/**
	 * pobiera następne zadanie do wykonania z kolejki workQueue
	 * odpowiednia metoda jest wywoływana poprzez identyfikujący bajt
	 */
	public void performNextWork() throws InterruptedException, IOException, SQLException {
		Bundle b = workQueue.take();
		switch (b.getHeader()) {
		case Connection.loginByte:
			onLoginWork(b);
			break;
		case Connection.createByte:
			onCreateAccountWork(b);
			break;
		case Connection.getMeasurementsByte:
			onGetMeasurementsByteWork(b);
			break;
		case Connection.addMeasurementByte:
			onAddMeasurementByteWork(b);
			break;
		case Connection.editMeasurementByte:
			onEditMeasurementByteWork(b);
			break;
		case Connection.removeMeasurementByte:
			onRemoveMeasurementByteWork(b);
			break;
		case Connection.logoutByte:
			onLogoutByteWork(b);
			break;

		}

	}

	private void onLoginWork(Bundle b) throws IOException, SQLException, InterruptedException {
		if (b.getBool("status")) {
			LoginController lc = con.getController();
			Platform.runLater(() -> {
				try {
					lc.switchSceneList();
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		}

	}

	private void onCreateAccountWork(Bundle b) throws IOException, SQLException, InterruptedException {

		if (b.getBool("status")) {
			Platform.runLater(() -> {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Konto zostało utworzone");
				alert.setContentText(":)");
				alert.showAndWait();
			});
		} else if (!b.getBool("status")) {
			Platform.runLater(() -> {
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Błąd przy tworzeniu konta");
				alert.setContentText("Konto o podanej nazwie istnieje");
				alert.showAndWait();
			});
		}
	}

	private void onGetMeasurementsByteWork(Bundle b) throws SQLException {

		int size = b.getInt("size");

		List<MeasurementEntry> entries = new ArrayList<>();

		for (int i = 0; i < size; i++) {
			MeasurementEntry e = new MeasurementEntry(b.getInt("id" + Integer.toString(i)), null,
					b.getLong("date" + Integer.toString(i)), b.getDouble("leukocyte" + Integer.toString(i)),
					b.getDouble("erythrocyte" + Integer.toString(i)), b.getDouble("hemoglobin" + Integer.toString(i)),
					b.getDouble("hematocrit" + Integer.toString(i)), b.getDouble("mcv" + Integer.toString(i)),
					b.getDouble("mch" + Integer.toString(i)), b.getDouble("mchc" + Integer.toString(i)),
					b.getDouble("platelets" + Integer.toString(i)), b.getDouble("lymphocyte" + Integer.toString(i)));
			entries.add(e);
		}
		entries.sort((x, y) -> {
			return (int) (x.getDate() - y.getDate());
		});

		ListController c = con.getController();
		c.getAllData().clear();
		c.getAllData().setAll(entries);
		c.update();

	}

	private void onAddMeasurementByteWork(Bundle b) throws IOException, SQLException {
		ListController c = con.getController();

		c.getAllData().add(new MeasurementEntry(b.getLong("date"), b.getInt("id")));
		c.update();
	}

	private void onEditMeasurementByteWork(Bundle b) throws IOException {

	}

	private void onRemoveMeasurementByteWork(Bundle b) throws IOException, SQLException, InterruptedException {

		ListController c = con.getController();
		c.getAllData().removeIf(a -> a.getId().equals(b.getInt("id")));
		c.update();

	}

	private void onLogoutByteWork(Bundle b) {
		con.disconnect();
	}
}
