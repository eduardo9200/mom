package mom.cliente;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import mom.commons.Message;
import mom.commons.Utils;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.Socket;

public class Chat extends JFrame {

	private String connection_info;
	private List<String> message_list;
	
	private Home home;
	private Socket connection;
	
	private String titulo;
	
	private JPanel contentPane;
	private JTextField textFieldNome;
	private JButton btnEnviar;
	private JTextField textFieldMensagem;
	private JScrollPane scrollPane;
	private JTextArea textArea;

	/**
	 * Launch the application.
	 */
	/*public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Chat frame = new Chat("123:123", "oi");
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}*/

	/**
	 * Create the frame.
	 */
	public Chat(Home home, Socket connection, String connection_info, String title) {
		super("Chat - " + title);
		this.titulo = title;
		this.connection_info = connection_info;
		this.connection = connection;
		this.home = home;
		this.message_list = new ArrayList<String>();
		
		this.initComponents();
		this.initActions();
		this.setVisible(true);
	}
	
	private void initComponents() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 392, 424);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		textFieldNome = new JTextField(Utils.getNomeUsuario(connection_info));
		textFieldNome.setHorizontalAlignment(SwingConstants.CENTER);
		textFieldNome.setEditable(false);
		textFieldNome.setBounds(10, 11, 170, 32);
		contentPane.add(textFieldNome);
		textFieldNome.setColumns(10);
		
		btnEnviar = new JButton("Enviar");
		btnEnviar.setBounds(277, 328, 89, 32);
		contentPane.add(btnEnviar);
		
		textFieldMensagem = new JTextField();
		textFieldMensagem.setBounds(10, 328, 257, 32);
		contentPane.add(textFieldMensagem);
		textFieldMensagem.setColumns(10);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 54, 356, 263);
		contentPane.add(scrollPane);
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		scrollPane.setViewportView(textArea);
	}
	
	private void initActions() {
		textFieldMensagem.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				btnEnviarKeyReleased(e);
			}
		});
		
		btnEnviar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnEnviarActionPerformed(e);
			}
		});
		
		this.addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowClosing(WindowEvent e) {
				Utils.sendMessage(connection, Message.CHAT_CLOSE);
				home.getOpened_chats().remove(connection_info);
				home.getConnectedListeners().get(connection_info).setChatOpen(false);
				home.getConnectedListeners().get(connection_info).setRunning(false);
				home.getConnectedListeners().remove(connection_info);
				
			}

			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
	
	public void appendMessage(String received) {
		message_list.add(received);
		String message = "";
		
		for(String str : message_list) {
			message += str;
		}
		textArea.setText(message);
	}
	
	private void send() {
		if(this.textFieldMensagem.getText().length() > 0) {
			DateFormat df = new SimpleDateFormat("hh:mm:ss");
			
			String messageToSend = "[" + df.format(new Date()) + "]" + Utils.getNomeUsuario(connection_info) + ": " + this.textFieldMensagem.getText() + "\n";
			//String messageToMe = "[" + df.format(new Date()) + "] EU: " + this.textFieldMensagem.getText() + "\n";
			
			Utils.sendMessage(connection, Message.MESSAGE + ";" + messageToSend);
			this.appendMessage(messageToSend);
			this.textFieldMensagem.setText("");
		}		
	}
	
	private void btnEnviarActionPerformed(ActionEvent e) {
		this.send();
	}
	
	private void btnEnviarKeyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			this.send();
		}
	}
}
