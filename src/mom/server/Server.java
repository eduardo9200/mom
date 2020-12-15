package mom.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import mom.commons.Message;
import mom.commons.Utils;

public class Server {

	public static final String HOST = "127.0.0.1";
	public static final int PORT = 4444;
	
	private ServerSocket server;
	
	private Map<String, ClientListener> clients;
	
	public Server(int port) {
		
		try {
			String connectionInfo;
			
			clients = new HashMap<String, ClientListener>();
			server = new ServerSocket(port);
			
			System.out.println("Servidor rodando em: " + server.getInetAddress().getHostName() + ":" + server.getLocalPort());
			
			while(true) {
				Socket connection = server.accept();
				connectionInfo = Utils.receiveMessage(connection);

				if(this.checkLogin(connectionInfo)) {
					ClientListener cl = new ClientListener(connectionInfo, connection, this);
					clients.put(connectionInfo, cl);
					Utils.sendMessage(connection, Message.SUCCESS);
					new Thread(cl).start();
				
				} else {
					Utils.sendMessage(connection, Message.ERROR);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		
	private boolean checkLogin(String connectionInfo) {
		String[] splited = connectionInfo.split(":");
		
		for(Map.Entry<String, ClientListener> pair : clients.entrySet()) {
			String[] parts = pair.getKey().split(":");
						
			if(parts[0].toLowerCase().equals(splited[0].toLowerCase())) {
				return false;
			
			} /*else if((parts[1] + parts[2]).equals(splited[1] + splited[2])) {
				return false;
			}*/
		}
		return true;
	}
	
	public Map<String, ClientListener> getClients() {
		return this.clients;
	}
}
