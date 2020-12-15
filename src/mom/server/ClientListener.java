package mom.server;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;

import mom.commons.Message;
import mom.commons.Utils;

public class ClientListener implements Runnable {

	private String connectionInfo;
	private Socket connection;
	private Server server;
	
	private boolean running;
	
	public ClientListener(String connectionInfo, Socket connection, Server server) {
		this.connectionInfo = connectionInfo;
		this.connection = connection;
		this.server = server;
		this.running = false;
	}
	
	@Override
	public void run() {
		this.running = true;
		String message;
		
		while(running) {
			message = Utils.receiveMessage(connection);
			if(message.equals(Message.QUIT)) {
				server.getClients().remove(connectionInfo);
				
				try {
					connection.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				running = false;
			
			} else if(message.equals(Message.GET_CONNECTED_USERS)) {
				System.out.println("Solicitação de atualizar lista de contatos...");
				String response = "";
				
				for(Map.Entry<String, ClientListener> pair : server.getClients().entrySet()) {
					response = response.concat(pair.getKey().concat(";"));
				}
				Utils.sendMessage(connection, response);
				
			} else {
				System.out.println("Recebido: " + message);
			}
		}
	}
	
	public boolean getRunning() {
		return this.running;
	}
	
	public void setRunning(boolean run) {
		this.running = run;
	}
}
