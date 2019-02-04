package studia.projekt.client.connection;

import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;

public class Sender {

	private ArrayBlockingQueue<Bundle> sendQueue;
	private DataOutputStream dout;

	public Sender(ArrayBlockingQueue<Bundle> sendQueue, DataOutputStream dout) {
		super();
		this.sendQueue = sendQueue;
		this.dout = dout;
	}

	/**
	 * pobiera nastepne zadanie z kolejki sendQueue i je wykonuje odpowiednia metoda
	 * jest wywoływana poprzez identyfikujący bajt
	 */
	public void performNextSend() throws InterruptedException, IOException, SQLException {
		Bundle b = sendQueue.take();
		switch (b.getHeader()) {
		case Connection.loginByte:
			onLoginSend(b);
			break;
		case Connection.createByte:
			onCreateAccountSend(b);
			break;
		case Connection.getMeasurementsByte:
			onGetMeasurementsByteSend(b);
			break;
		case Connection.addMeasurementByte:
			onAddMeasurementByteSend(b);
			break;
		case Connection.editMeasurementByte:
			onEditMeasurementByteSend(b);
			break;
		case Connection.removeMeasurementByte:
			onRemoveMeasurementByteSend(b);
			break;
		case Connection.logoutByte:
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
		dout.writeInt(b.getInt("id"));
		dout.writeLong(b.getLong("date"));
		dout.writeDouble(b.getDouble("leukocyte"));
		dout.writeDouble(b.getDouble("erythrocyte"));
		dout.writeDouble(b.getDouble("hemoglobin"));
		dout.writeDouble(b.getDouble("hematocrit"));
		dout.writeDouble(b.getDouble("mcv"));
		dout.writeDouble(b.getDouble("mch"));
		dout.writeDouble(b.getDouble("mchc"));
		dout.writeDouble(b.getDouble("platelets"));
		dout.writeDouble(b.getDouble("lymphocyte"));

	}

	private void onRemoveMeasurementByteSend(Bundle b) throws IOException, SQLException {
		dout.writeByte(b.getHeader());
		dout.writeInt(b.getInt("id"));
	}

	private void onLogoutByteSend(Bundle b) throws IOException {
		dout.writeByte(b.getHeader());
	}
}
