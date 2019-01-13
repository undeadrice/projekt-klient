package studia.projekt.client.connection;

import java.io.DataInputStream;

public interface ReceiveTask {
	
	void receive(DataInputStream in);

}
