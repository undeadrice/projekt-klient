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

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import studia.projekt.client.controller.ListController;
import studia.projekt.client.controller.LoginController;
import studia.projekt.client.model.MeasurementEntry;

/**
 * Klasa reprezentująca połączenie klienta z serwerem
 * 
 */
public class Connection {

	// bajty służące do identyfikacji poleceń przy wysyłaniu, odbieraniu i pracy
	public static final byte loginByte = 0x01;
	public static final byte createByte = 0x02;
	public static final byte getMeasurementsByte = 0x03;
	public static final byte addMeasurementByte = 0x04;
	public static final byte editMeasurementByte = 0x05;
	public static final byte removeMeasurementByte = 0x06;
	public static final byte logoutByte = 0x07;

	/**
	 * dane o użytkowniku który jest obecnie zalogowany na tym kliencie
	 */
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
	 * kontroler który jest w posiadaniu połączenia ( ten który jest w danej chwili
	 * aktywny )
	 */
	private volatile Object ownerController;

	/**
	 * pula wątków do obsługi połączenia, każdy wątek będzie przypisany do jednego z
	 * poniższych zadań: 1. Obsługa strumieni wyjścia (wysyłanie danych) 2. Obsługa
	 * strumieni wejścia (odbiór danych); 3. Wykonywanie pozostałych czynności
	 */
	private ExecutorService threads;

	/*
	 * kolejki wejscia i wyjscia - umożliwiają współpracę różnych wątków
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

	private Receiver receiver;
	private Worker worker;
	private Sender sender;

	public Connection() {

	}

	/// pętle odpowiedzialne za odbieranie , wysyłanie oraz pracę z danymi

	public void sendLoop() throws InterruptedException, IOException, SQLException {
		while (true) {
			sender.performNextSend();
			Thread.sleep(10);
		}
	}

	public void receiveLoop() throws InterruptedException, IOException, SQLException {
		while (true) {
			receiver.performNextRec();
			Thread.sleep(10);
		}
	}

	public void workLoop() throws InterruptedException, IOException, SQLException {
		while (true) {
			worker.performNextWork();
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

		receiver = new Receiver(workQueue, din);
		worker = new Worker(workQueue, this);
		sender = new Sender(sendQueue, dout);

		connected = true;
		start();
	}

	/**
	 * rozłączenie i "reset" połączenia
	 */
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

	/**
	 * umieszcza obiekt bundle na końcu kolejki sendQueue
	 */
	public void send(Bundle b) {
		sendQueue.offer(b);
	}

	/**
	 * zwraca kontroler który jest obecnie właścicielem tego połączenia
	 */
	@SuppressWarnings("unchecked")
	public <T> T getController() {
		return (T) ownerController;
	}

	public void setOwnerController(Object ownerController) {
		this.ownerController = ownerController;
	}

}
