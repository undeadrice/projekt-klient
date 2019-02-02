package studia.projekt.client.connection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.crypto.CipherInputStream;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import studia.projekt.client.controller.ListController;
import studia.projekt.client.controller.LoginController;
import studia.projekt.client.model.MeasurementEntry;

/**
 * Klasa reprezentująca połączenie klienta z serwerem
 * 
 * Połączenie opiera się na protokole TCP
 * 
 * @author bruce
 *
 */
public class Connection {

	public static final byte loginByte = 0x01;
	public static final byte createByte = 0x02;
	public static final byte getMeasurementsByte = 0x03;
	public static final byte addMeasurementByte = 0x04;
	public static final byte editMeasurementByte = 0x05;
	public static final byte removeMeasurementByte = 0x06;
	public static final byte logoutByte = 0x07;

	
	private UserInfo userInfo = new UserInfo();
	
	/**
	 * rozmiar tablic znajdujących się w kolejkach
	 */
	public static final int QUEUE_SIZE = 10;

	/**
	 * liczba wątków do utworzenia
	 */
	public static final int THREAD_COUNT = 3;

	/**
	 * stan połączenia
	 */
	private volatile boolean connected = false;

	/**
	 * 
	 */
	private volatile Object ownerController;

	/**
	 * pula wątków do obsługi połączenia, każdy wątek będzie przypisany do jednego z
	 * poniższych zadań: 1. Obsługa strumieni wyjścia (wysyłanie danych) 2. Obsługa
	 * strumieni wejścia (odbiór danych); 3. Wykonywanie pozostałych czynności
	 */
	private ExecutorService threads;

	/*
	 * kolejki wejscia i wyjscia ich zadaniem jest umożliwienie współpracy różnych
	 * wątków
	 */
	private ArrayBlockingQueue<Bundle> workQueue;
	private ArrayBlockingQueue<Bundle> sendQueue;

	// strumień wejścia din oraz wyjscia dout
	private DataInputStream din;
	private DataOutputStream dout;

	/**
	 * połączenie z serwerem
	 */
	private Socket s;

	/**
	 * konstruktor
	 * 
	 * @throws IOException
	 */
	public Connection() {

	}

	public void sendLoop() throws InterruptedException, IOException, SQLException {
		while (true) {
			performNextSend();
			Thread.sleep(10);
		}
	}

	public void receiveLoop() throws InterruptedException, IOException, SQLException {
		while (true) {
			performNextRec();
			Thread.sleep(10);
		}
	}

	public void workLoop() throws InterruptedException, IOException, SQLException {
		while (true) {
			performNextWork();
			Thread.sleep(10);
		}
	}

	/**
	 * przekazuje zadania wątkom z puli threads i je uruchamia
	 */
	public void start() {
		threads.execute(() -> {
			try {
				sendLoop();
			} catch (InterruptedException | IOException | SQLException e) {
				e.printStackTrace();
				disconnect();
			}
		});
		threads.execute(() -> {
			try {
				receiveLoop();
			} catch (InterruptedException | IOException | SQLException e) {
				e.printStackTrace();
				disconnect();
			}
		});
		threads.execute(() -> {
			try {
				workLoop();
			} catch (InterruptedException | IOException | SQLException e) {
				e.printStackTrace();
				disconnect();
			}
		});
	}

	public void connect(String host, int port) throws UnknownHostException, IOException {
		s = new Socket(host, port);
		workQueue = new ArrayBlockingQueue<>(QUEUE_SIZE);
		sendQueue = new ArrayBlockingQueue<>(QUEUE_SIZE);
		din = new DataInputStream(s.getInputStream());
		dout = new DataOutputStream(s.getOutputStream());
		threads = Executors.newFixedThreadPool(THREAD_COUNT);
		connected = true;
		start();
	}

	public synchronized void disconnect() {
		try {
			if (!s.isClosed() && connected == true) {
				s.close();
				s = null;
				workQueue = null;
				sendQueue = null;
				threads.shutdownNow();
				connected = false;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public DataInputStream getDin() {
		return din;
	}

	public void setDin(DataInputStream din) {
		this.din = din;
	}

	public DataOutputStream getDout() {
		return dout;
	}

	public void setDout(DataOutputStream dout) {
		this.dout = dout;
	}

	private void performNextRec() throws IOException, SQLException, InterruptedException {
		byte b = din.readByte();
		System.out.println("REC BYTE: " + b);
		switch (b) {
		case loginByte:
			onLoginRec();
			break;
		case createByte:
			onCreateAccountRec();
			break;
		case getMeasurementsByte:
			onGetMeasurementsByteRec();
			break;
		case addMeasurementByte:
			onAddMeasurementByteRec();
			break;
		case editMeasurementByte:
			onEditMeasurementByteRec();
			break;
		case removeMeasurementByte:
			onRemoveMeasurementByteRec();
			break;
		case logoutByte:
			onLoginRec();
			break;

		}
	}

	private void onLoginRec() throws IOException, InterruptedException {
		Bundle b = new Bundle(this.loginByte);

		b.putBool("status", din.readBoolean());
		b.putString("login", din.readUTF());
		workQueue.put(b);
	}

	private void onCreateAccountRec() throws IOException, InterruptedException {
		Bundle b = new Bundle(createByte);
		b.putBool("status", din.readBoolean());
		b.putString("login", din.readUTF());
		workQueue.put(b);
	}

	private void onGetMeasurementsByteRec() throws IOException, InterruptedException {
		Bundle b = new Bundle(getMeasurementsByte);
		int size = din.readInt();

		for (int i = 0; i < size; i++) {
			b.putDouble("leukocyte" + Integer.toString(i), din.readDouble());
			b.putDouble("erythrocyte" + Integer.toString(i), din.readDouble());
			b.putDouble("hemoglobin" + Integer.toString(i), din.readDouble());
			b.putDouble("hematocrit" + Integer.toString(i), din.readDouble());
			b.putDouble("mcv" + Integer.toString(i), din.readDouble());
			b.putDouble("mch" + Integer.toString(i), din.readDouble());
			b.putDouble("mchc" + Integer.toString(i), din.readDouble());
			b.putDouble("platelets" + Integer.toString(i), din.readDouble());
			b.putDouble("lymphocyte" + Integer.toString(i), din.readDouble());
			b.putInt("id" + Integer.toString(i), din.readInt());
			b.putLong("date" + Integer.toString(i), din.readLong());
		}
		b.putInt("size", size);
		workQueue.put(b);

	}

	private void onAddMeasurementByteRec() throws IOException, InterruptedException {
		Bundle b = new Bundle(addMeasurementByte);
		int id = din.readInt();
		long date = din.readLong();
		b.putInt("id", id);
		b.putLong("date", date);
		workQueue.put(b);
	}

	private void onEditMeasurementByteRec() throws IOException {
		//
	}

	private void onRemoveMeasurementByteRec() throws IOException, InterruptedException {
		Bundle b = new Bundle(removeMeasurementByte);
		b.putInt("id", din.readInt());
		workQueue.put(b);
	}

	private void onLogoutByteRec() {
		Bundle b = new Bundle(logoutByte);
	}

	private void performNextWork() throws InterruptedException, IOException, SQLException {
		Bundle b = workQueue.take();
		switch (b.getHeader()) {
		case loginByte:
			onLoginWork(b);
			break;
		case createByte:
			onCreateAccountWork(b);
			break;
		case getMeasurementsByte:
			onGetMeasurementsByteWork(b);
			break;
		case addMeasurementByte:
			onAddMeasurementByteWork(b);
			break;
		case editMeasurementByte:
			onEditMeasurementByteWork(b);
			break;
		case removeMeasurementByte:
			onRemoveMeasurementByteWork(b);
			break;
		case logoutByte:
			onLogoutByteWork(b);
			break;

		}

	}

	private void onLoginWork(Bundle b) throws IOException, SQLException, InterruptedException {
		if (b.getBool("status")) {
			LoginController lc = getController();
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

		ListController c = getController();
		c.getAllData().clear();
		c.getAllData().setAll(entries);
		c.update();

	}

	private void onAddMeasurementByteWork(Bundle b) throws IOException, SQLException {
		ListController c = getController();

		c.getAllData().add(new MeasurementEntry(b.getLong("date"), b.getInt("id")));
		c.update();
	}

	private void onEditMeasurementByteWork(Bundle b) throws IOException {

	}

	private void onRemoveMeasurementByteWork(Bundle b) throws IOException, SQLException, InterruptedException {
		System.out.println("insdd");
		ListController c = getController();
		System.out.println(c.getAllData().size());
		c.getAllData().removeIf(a -> a.getId().equals(b.getInt("id")));
		System.out.println(c.getAllData().size());
		c.update();
		System.out.println("done");

	}

	private void onLogoutByteWork(Bundle b) {

	}

	private void performNextSend() throws InterruptedException, IOException, SQLException {
		Bundle b = sendQueue.take();
		switch (b.getHeader()) {
		case loginByte:
			onLoginSend(b);
			break;
		case createByte:
			onCreateAccountSend(b);
			break;
		case getMeasurementsByte:
			onGetMeasurementsByteSend(b);
			break;
		case addMeasurementByte:
			onAddMeasurementByteSend(b);
			break;
		case editMeasurementByte:
			onEditMeasurementByteSend(b);
			break;
		case removeMeasurementByte:
			onRemoveMeasurementByteSend(b);
			break;
		case logoutByte:
			onLogoutByteSend(b);
			break;

		}

	}

	private void onLoginSend(Bundle b) throws IOException, SQLException, InterruptedException {
		dout.writeByte(b.getHeader());
		dout.writeUTF(b.getString("login"));
		dout.writeUTF(b.getString("password"));
		dout.flush();
	}

	private void onCreateAccountSend(Bundle b) throws IOException, SQLException, InterruptedException {
		dout.writeByte(b.getHeader());
		dout.writeUTF(b.getString("login"));
		dout.writeUTF(b.getString("password"));
		dout.writeUTF(b.getString("name"));
		dout.writeUTF(b.getString("surname"));
		dout.writeUTF(b.getString("code"));
		dout.writeByte(b.getByte("sex"));
		dout.flush();
	}

	private void onGetMeasurementsByteSend(Bundle b) throws SQLException, IOException {
		dout.writeByte(b.getHeader());
		dout.flush();
	}

	private void onAddMeasurementByteSend(Bundle b) throws IOException, SQLException {
		dout.writeByte(b.getHeader());
		dout.writeLong(b.getLong("date"));
		dout.flush();
	}

	private void onEditMeasurementByteSend(Bundle b) throws IOException {
		dout.writeByte(b.getHeader());
		dout.writeDouble(b.getDouble("leukocyte"));
		dout.writeDouble(b.getDouble("erythrocyte"));
		dout.writeDouble(b.getDouble("hemoglobin"));
		dout.writeDouble(b.getDouble("hematocrit"));
		dout.writeDouble(b.getDouble("mcv"));
		dout.writeDouble(b.getDouble("mch"));
		dout.writeDouble(b.getDouble("mchc"));
		dout.writeDouble(b.getDouble("platelets"));
		dout.writeDouble(b.getDouble("lymphocyte"));
		dout.writeInt(b.getInt("id"));
		dout.writeLong(b.getLong("date"));
	
	}

	private void onRemoveMeasurementByteSend(Bundle b) throws IOException, SQLException {
		dout.writeByte(b.getHeader());
		dout.writeInt(b.getInt("id"));
	}

	private void onLogoutByteSend(Bundle b) throws IOException {
		dout.writeByte(b.getHeader());
	}

	public void send(Bundle b) {
		sendQueue.offer(b);
	}

	@SuppressWarnings("unchecked")
	private <T> T getController() {
		return (T) ownerController;
	}

	public Object getOwnerController() {
		return ownerController;
	}

	public void setOwnerController(Object ownerController) {
		this.ownerController = ownerController;
	}

	public void fillTest() {

	}

}
