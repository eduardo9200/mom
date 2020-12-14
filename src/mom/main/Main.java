package mom.main;

import java.awt.EventQueue;

import javax.swing.JOptionPane;

import mom.cliente.Login;
import mom.server.Server;

public class Main {

public static void main(String[] args) {
		
		int resposta = JOptionPane.showConfirmDialog(null, "Você está entrando como servidor?", "Cliente ou Servidor", JOptionPane.YES_NO_OPTION);
		
		if(resposta == 0) {
			String port = JOptionPane.showInputDialog(null, "Informe a porta a qual o servidor irá rodar");
			int porta = Integer.parseInt(port);
			
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						Server servidor = new Server(porta);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});	
		
		} else if(resposta == 1) {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						Login loginFrame = new Login();
						loginFrame.setVisible(true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
		
	}
}
