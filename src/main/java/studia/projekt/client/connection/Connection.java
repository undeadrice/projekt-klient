package studia.projekt.client.connection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Klasa reprezentująca połączenie klienta z serwerem
 * 
 * Połączenie opiera się na protokole TCP
 * 
 * @author bruce
 *
 */
public class Connection {

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
	 * pula wątków do obsługi połączenia, każdy wątek będzie przypisany do jednego z
	 * poniższych zadań: 1. Obsługa strumieni wyjścia (wysyłanie danych) 2. Obsługa
	 * strumieni wejścia (odbiór danych); 3. Wykonywanie pozostałych czynności
	 */
	private ExecutorService threads;

	/*
	 * kolejki wejscia i wyjscia ich zadaniem jest umożliwienie współpracy różnych
	 * wątków
	 */
	private ArrayBlockingQueue<ReceiveTask> qin;
	private ArrayBlockingQueue<SendTask> qout;

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

	public void sendLoop() throws InterruptedException {
		while (true) {
			// pobierz następne zadanie SendTask z kolejki qin, jeśli zadania nie ma, czekaj
			qout.take().send(this.dout);
		}
	}

	public void receiveLoop() throws InterruptedException {
		while (true) {
			// utwórz nowe zadanie ReceiveTask z kolejki qin, jeśli zadania nie ma, czekaj
			qout.take().send(this.dout);
		}
	}

	public void workLoop() throws InterruptedException {
		while (true) {
			// pobierz następne zadanie ReceiveTask z kolejki qin, jeśli zadania nie ma,
			// czekaj
			qout.take().send(this.dout);
		}
	}

	/**
	 * przekazuje zadania wątkom z puli threads i je uruchamia
	 */
	public void start() {
		threads.execute(() -> {
			try {
				sendLoop();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		threads.execute(() -> {
			try {
				receiveLoop();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		threads.execute(() -> {
			try {
				workLoop();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
	}

	
	public void connect(String host, int port) throws UnknownHostException, IOException  {
		s = new Socket(host, port);
		qin = new ArrayBlockingQueue<>(QUEUE_SIZE);
		qout = new ArrayBlockingQueue<>(QUEUE_SIZE);
		din = new DataInputStream(s.getInputStream());
		dout = new DataOutputStream(s.getOutputStream());
		threads = Executors.newFixedThreadPool(THREAD_COUNT);
		connected = true;
		start();
	}
	
	public void disconnect()  {
		try {
			s.close();
			s = null;
			qin = null;
			qout = null;
			threads.shutdownNow();
			connected = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	
	}
}
