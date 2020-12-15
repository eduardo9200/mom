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

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;

import mom.commons.Message;
import mom.commons.Utils;
import mom.comunicacao.Consumidor;
import mom.comunicacao.Subscriber;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
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

	private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
	
	private String connection_info;
	private List<String> message_list;
	
	private Home home;
	private Socket connection;
	
	private String titulo;
	
	private String nomeTopico;
	
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
		this.nomeTopico = null;
		
		this.initComponents();
		this.initActions();
		this.setVisible(true);
		
		Consumidor c = new Consumidor(Utils.getNomeUsuario(connection_info));
		try {
			c.execute();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	public Chat(String nomeTopico, String connectionInfo) {
		this.nomeTopico = nomeTopico;
		this.message_list = new ArrayList<String>();
		this.connection_info = connectionInfo;
		
		this.initComponents();
		this.initActions();
		
		implementaSubscriber();
		
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
				if(connection != null) {
					Utils.sendMessage(connection, Message.CHAT_CLOSE);
					home.getOpened_chats().remove(connection_info);
					home.getConnectedListeners().get(connection_info).setChatOpen(false);
					home.getConnectedListeners().get(connection_info).setRunning(false);
					home.getConnectedListeners().remove(connection_info);
				}
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
	
	public void implementaSubscriber() {
		Subscriber sub = new Subscriber(this.nomeTopico);
		sub.execute();
	}
	
	public void onMessage(Message message) {
        if(message instanceof TextMessage) {
            try {
            	this.appendMessage(((TextMessage)message).getText());
                System.out.println(((TextMessage)message).getText());
            }
            catch(JMSException e) {
                System.out.println("Erro: " + e);
            }
        }
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
			if(this.nomeTopico != null && !this.nomeTopico.isEmpty()) { //Está inscrito em um tópico
				try {
					implementaPublisher(this.nomeTopico, this.textFieldMensagem.getText());
				} catch (JMSException e) {
					e.printStackTrace();
				}
				
			} else {
				DateFormat df = new SimpleDateFormat("hh:mm:ss");
				
				String messageToSend = "[" + df.format(new Date()) + "]" + Utils.getNomeUsuario(connection_info) + ": " + this.textFieldMensagem.getText() + "\n";
				//String messageToMe = "[" + df.format(new Date()) + "] EU: " + this.textFieldMensagem.getText() + "\n";
				
				boolean mensagemEnviada = Utils.sendMessage(connection, Message.MESSAGE + ";" + messageToSend);
				
				if(!mensagemEnviada) {
					
				}
				
				this.appendMessage(messageToSend);
				this.textFieldMensagem.setText("");	
			}
		}		
	}
	
	public void implementaPublisher(String nomeTopico, String mensagem) throws JMSException {
		/*
         * Estabelecendo conexão com o Servidor JMS
         */		
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        Connection connection = connectionFactory.createConnection();
        connection.start();

        /*
         * Criando Session 
         */		
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        /*
         * Criando Topic
         */     
        Destination dest = session.createTopic(nomeTopico);

        /*
         * Criando Produtor
         */
        MessageProducer publisher = session.createProducer(dest);

        TextMessage message = session.createTextMessage();
        message.setText(mensagem);


        /*
         * Publicando Mensagem
         */
        publisher.send(message);

        publisher.close();
        session.close();
        connection.close();
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
