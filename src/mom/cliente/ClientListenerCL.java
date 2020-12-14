package mom.cliente;

import java.io.IOException;
import java.net.Socket;

import lombok.Getter;
import lombok.Setter;
import mom.commons.Message;
import mom.commons.Utils;

public class ClientListenerCL implements Runnable {

	@Getter @Setter
	private boolean running;
	@Getter @Setter
	private boolean chatOpen;
	private Socket connection;
	private Home home;
	private String connectionInfo;
	@Getter @Setter
	private Chat chat;
	
	public ClientListenerCL(Home home, Socket connection) {
		this.home = home;
		this.connection = connection;
		this.chatOpen = false;
		this.running = false;
		this.connectionInfo = null;
		this.chat = null;
		
	}

	@Override
	public void run() {
		running = true;
		String message;
		
		while(running) {
			message = Utils.receiveMessage(connection);
			
			if(message == null || message.equals(Message.CHAT_CLOSE)) {
				if(chatOpen) {
					home.getOpened_chats().remove(connectionInfo);
					home.getConnectedListeners().remove(connectionInfo);
					chatOpen = false;
					
					try {
						connection.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					chat.dispose();
				}
				running = false;
			
			} else {
				String[] fields = message.split(";");
				if(fields.length > 1) {
					if(fields[0].equals(Message.OPEN_CHAT)) {
						String[] splited = fields[1].split(":");
						connectionInfo = fields[1];
						
						if(!chatOpen) {
							home.getOpened_chats().add(connectionInfo);
							home.getConnectedListeners().put(connectionInfo, this);
							chatOpen = true;
							chat = new Chat(home, connection, connectionInfo, Utils.getNomeUsuario(home.getConnection_info()));
						}
					} else if(fields[0].equals(Message.MESSAGE)) {
						String msg = "";
						
						for(int i = 1; i < fields.length; i++) {
							msg += fields[i] + ";";
						}
						chat.appendMessage(msg);
					}
				}
			}
			System.out.println(">> Mensagem: " + message);
		}
		
	}
}
