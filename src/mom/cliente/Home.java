package mom.cliente;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.jms.JMSException;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;

import lombok.Getter;
import mom.commons.Message;
import mom.commons.Utils;
import mom.comunicacao.Consumidor;
import mom.comunicacao.Produtor;
import mom.comunicacao.Publisher;
import mom.comunicacao.Subscriber;

import java.awt.Color;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.awt.event.ActionEvent;

public class Home extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
	
	private Socket connection;
	private ServerSocket server;
	private boolean running;
	
	private String connection_info;
	
	private List<String> connectedUsers;
	
	private List<String> opened_chats;
	
	private Map<String, ClientListenerCL> connectedListeners;
	
	private JPanel contentPane;
	private JTextField textFieldNome;
	private JPanel panelContatos;
	private JButton btnAbrirConversa;
	private JList<String> listContatos;
	private JButton btnAderirTopico;
	private JButton btnAtualizar;
	private JButton btnCriarTopico;
	private JPanel panel;
	private JList<String> listTopicos;
	
	/**
	 * Launch the application.
	 */
	/*public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Home frame = new Home("123:123");
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
	public Home(Socket connection, String connection_info) {
		this.server = null;
		this.running = false;
		this.connection = connection;
		this.connection_info = connection_info;
		
		this.connectedUsers = new ArrayList<String>();
		this.opened_chats = new ArrayList<String>();
		this.connectedListeners = new HashMap<String, ClientListenerCL>();
		
		this.initComponents();
		this.initActions();
		
		this.criaProdutor();
		
		startServer(this, Integer.parseInt(connection_info.split(":")[2]));
	}
	
	private void initComponents() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 567, 508);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		panelContatos = new JPanel();
		panelContatos.setBackground(Color.WHITE);
		panelContatos.setBounds(10, 83, 255, 309);
		contentPane.add(panelContatos);
		
		panelContatos.setLayout(null);
		
		listContatos = new JList<String>();
		listContatos.setBounds(10, 5, 235, 293);
		panelContatos.add(listContatos);
		listContatos.setBorder(BorderFactory.createTitledBorder("Usuários on-line"));
		listContatos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		textFieldNome = new JTextField("< Usuário: " + connection_info.split(":")[0] + " >");
		textFieldNome.setHorizontalAlignment(SwingConstants.CENTER);
		textFieldNome.setEditable(false);
		textFieldNome.setBounds(10, 11, 255, 46);
		contentPane.add(textFieldNome);
		textFieldNome.setColumns(10);
		
		btnAbrirConversa = new JButton("Abrir conversa");
		btnAbrirConversa.setBounds(57, 403, 157, 34);
		contentPane.add(btnAbrirConversa);
		
		btnAderirTopico = new JButton("Aderir ao t\u00F3pico");
		btnAderirTopico.setBounds(407, 403, 134, 34);
		contentPane.add(btnAderirTopico);
		
		btnAtualizar = new JButton("Atualizar");
		btnAtualizar.setBounds(353, 17, 106, 34);
		contentPane.add(btnAtualizar);
		
		btnCriarTopico = new JButton("Criar Fila/Top.");
		btnCriarTopico.setBounds(275, 403, 106, 34);
		contentPane.add(btnCriarTopico);
		
		panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.setBounds(275, 83, 266, 309);
		contentPane.add(panel);
		panel.setLayout(null);
		
		listTopicos = new JList<String>();
		listTopicos.setBounds(10, 11, 246, 287);
		panel.add(listTopicos);
		listTopicos.setBorder(BorderFactory.createTitledBorder("Filas e Tópicos existentes"));
		listTopicos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}
	
	private void initActions() {
		btnAtualizar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnAtualizarActionPerformed(e);
			}
		});
		
		btnAbrirConversa.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnAbrirConversaActionPerformed(e);
			}
		});
		
		btnCriarTopico.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnCriarTopicoActionPerformed(e);
			}
		});
		
		btnAderirTopico.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnAderirTopicoActionPerformed(e);
			}
		});
		
		this.addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent e) { }

			@Override
			public void windowClosing(WindowEvent e) {
				running = false;
				Utils.sendMessage(connection, Message.QUIT);
				System.out.println("> Conexão encerrada.");
			}

			@Override
			public void windowClosed(WindowEvent e) { }

			@Override
			public void windowIconified(WindowEvent e) { }

			@Override
			public void windowDeiconified(WindowEvent e) { }

			@Override
			public void windowActivated(WindowEvent e) { }

			@Override
			public void windowDeactivated(WindowEvent e) { }
			
		});
	}
	
	private void btnAtualizarActionPerformed(ActionEvent e) {
		this.getConnectedUsers();
		this.getTopicos();
	}
	
	private void btnAbrirConversaActionPerformed(ActionEvent e) {
		this.openChat();
	}
	
	private void btnCriarTopicoActionPerformed(ActionEvent e) {
		
		int opcao = JOptionPane.showConfirmDialog(this, "Criar Fila?",  "Fila ou tópico", JOptionPane.YES_NO_OPTION);
		
		if(opcao == 0) {
			String nomeFila = JOptionPane.showInputDialog(this, "Nome da fila");
			String mensagem = JOptionPane.showInputDialog(this, "Mensagem");
			
			Produtor produtor = new Produtor(nomeFila, mensagem);
			try {
				produtor.execute();
			} catch (JMSException e1) {
				e1.printStackTrace();
			}
		
		} else if(opcao == 1) {
			String nomeTopico = JOptionPane.showInputDialog(this, "Nome do tópico");
			String mensagem = JOptionPane.showInputDialog(this, "Mensagem");
			
			Publisher publisher = new Publisher(nomeTopico, mensagem);
			try {
				publisher.execute();
				
				Chat chat = new Chat(nomeTopico, this.connection_info);
			} catch (JMSException e1) {
				e1.printStackTrace();
			}	
		}
	}
	
	private void btnAderirTopicoActionPerformed(ActionEvent e) {
		if(this.listTopicos.getSelectedIndex() != -1) {
			String nomeTopico = this.listTopicos.getSelectedValue().toString();
			Chat chat = new Chat(nomeTopico, this.connection_info);
		}
	}
	
	private void criaProdutor() {
		Produtor p = new Produtor(Utils.getNomeUsuario(connection_info), "Oi");
		
		try {
			p.execute();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	private void getConnectedUsers() {
		Utils.sendMessage(connection, Message.GET_CONNECTED_USERS);
		String response = Utils.receiveMessage(connection);
		
		listContatos.removeAll();
		connectedUsers.clear();
		
		for(String str : response.split(";")) {
			if(!str.equals(connection_info)) {
				connectedUsers.add(str);
			}
		}
		
		listContatos.setListData(connectedUsers.toArray(new String[connectedUsers.size()]));
	}
	
	private void getTopicos() {
		List<String> topicos = new ArrayList<String>();
		int filas = 0;
		int topics = 0;
		
		try {
			ActiveMQConnection conn = ActiveMQConnection.makeConnection(url);
			conn.start();
			
			Set<ActiveMQQueue> allque = conn.getDestinationSource().getQueues();
			Set<ActiveMQTopic> allTop = conn.getDestinationSource().getTopics();
			
			Iterator<ActiveMQQueue> itr = allque.iterator();
			Iterator<ActiveMQTopic> itrTop = allTop.iterator();
			
			topicos.add("<- FILAS ->");
			while(itr.hasNext()) {
				ActiveMQQueue queue = itr.next();
				topicos.add(queue.getQueueName());
				filas++;
			}
			
			topicos.add("<- TÓPICOS ->");			
			while(itrTop.hasNext()) {
				ActiveMQTopic topic = itrTop.next();
				topicos.add(topic.getTopicName());
				topics++;
			}
			
			String info = filas + " Filas e " + topics + " tópicos";
			topicos.add(info);
			
			listTopicos.setListData(topicos.toArray(new String[topicos.size()]));
			
		} catch(JMSException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	private void openChat() {
		int index = listContatos.getSelectedIndex();
		
		if(index != -1) {
			String connection_info = listContatos.getSelectedValue().toString();
			String [] splited = connection_info.split(":");
			
			if(!opened_chats.contains(connection_info)) {
				try {
					Socket connection = new Socket(splited[1], Integer.parseInt(splited[2]));
					Utils.sendMessage(connection, Message.OPEN_CHAT + ";" + this.connection_info);
					ClientListenerCL cl = new ClientListenerCL(this, connection);
					Chat chat = new Chat(this, connection, connection_info, Utils.getNomeUsuario(connection_info));
					cl.setChat(chat);
					cl.setChatOpen(true);
					this.connectedListeners.put(connection_info, cl);
					opened_chats.add(connection_info);
					new Thread(cl).start();
				
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					JOptionPane.showMessageDialog(this, "Falha ao conectar.");
				}
			}
		}
	}
	
	private void startServer(Home home, int port) {
		new Thread() {
			@Override
			public void run() {
				running = true;
				try {
					server = new ServerSocket(port);
					System.out.println("Servidor cliente iniciado na porta: " + port);
					
					while(running) {
						Socket connection = server.accept();
						ClientListenerCL cl = new ClientListenerCL(home, connection);
						new Thread(cl).start();
					}
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	
	public String getConnection_info() {
		return this.connection_info;
	}
	
	public List<String> getOpened_chats() {
		return this.opened_chats;
	}
	
	public Map<String, ClientListenerCL> getConnectedListeners() {
		return this.connectedListeners;
	}
}
