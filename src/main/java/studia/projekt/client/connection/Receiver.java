package studia.projekt.client.connection;

import java.io.DataInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;

public class Receiver {

	private ArrayBlockingQueue<Bundle> workQueue;
	private DataInputStream din;

	public Receiver(ArrayBlockingQueue<Bundle> workQueue, DataInputStream din) {
		super();
		this.workQueue = workQueue;
		this.din = din;
	}
	/**
	 * odczytuje identyfikujący bajt i na jego podstawie wywołuje odpowiednią metodę
	 * każda z tych metod przekazuje zadanie dalej przez obiekt Bundle do kolejki workQueue
	 */
	public void performNextRec() throws IOException, SQLException, InterruptedException {
		byte b = din.readByte();
		System.out.println("REC BYTE: " + b);
		switch (b) {
		case Connection.loginByte:
			onLoginRec();
			break;
		case Connection.createByte:
			onCreateAccountRec();
			break;
		case Connection.getMeasurementsByte:
			onGetMeasurementsByteRec();
			break;
		case Connection.addMeasurementByte:
			onAddMeasurementByteRec();
			break;
		case Connection.editMeasurementByte:
			onEditMeasurementByteRec();
			break;
		case Connection.removeMeasurementByte:
			onRemoveMeasurementByteRec();
			break;
		case Connection.logoutByte:
			onLogoutByteRec();
			break;

		}
	}

	private void onLoginRec() throws IOException, InterruptedException {
		Bundle b = new Bundle(Connection.loginByte);
		b.putBool("status", din.readBoolean());
		b.putString("login", din.readUTF());
		b.putString("name", din.readUTF());
		b.putString("surname", din.readUTF());
		workQueue.put(b);
	}

	private void onCreateAccountRec() throws IOException, InterruptedException {
		Bundle b = new Bundle(Connection.createByte);
		b.putBool("status", din.readBoolean());
		b.putString("login", din.readUTF());
		workQueue.put(b);
	}

	private void onGetMeasurementsByteRec() throws IOException, InterruptedException {
		Bundle b = new Bundle(Connection.getMeasurementsByte);
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
		Bundle b = new Bundle(Connection.addMeasurementByte);
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
		Bundle b = new Bundle(Connection.removeMeasurementByte);
		b.putInt("id", din.readInt());
		workQueue.put(b);
	}

	private void onLogoutByteRec() throws InterruptedException {
		Bundle b = new Bundle(Connection.logoutByte);
		workQueue.put(b);
	}

}
