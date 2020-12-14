package mom.cliente;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;

import mom.commons.Message;
import mom.commons.Utils;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.awt.event.ActionEvent;

public class Login extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	private JTextField textFieldNome;
	private JTextField textFieldPort;
	private JLabel labelLogin;
	private JTextField textFieldHost;
	private JLabel labelNome;
	private JLabel labelPorta;
	private JButton btnEntrar;
	private JLabel labelHost;

	/**
	 * Launch the application.
	 */
	/*public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Login frame = new Login();
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
	public Login() {
		this.initComponents();
		this.initActions();
	}
	
	private void initComponents() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		textFieldNome = new JTextField();
		textFieldNome.setBounds(157, 66, 151, 29);
		contentPane.add(textFieldNome);
		textFieldNome.setColumns(10);
		
		labelNome = new JLabel("Nome");
		labelNome.setBounds(101, 73, 46, 14);
		contentPane.add(labelNome);
		
		labelPorta = new JLabel("Porta");
		labelPorta.setBounds(101, 171, 46, 14);
		contentPane.add(labelPorta);
		
		textFieldPort = new JTextField();
		textFieldPort.setBounds(157, 164, 151, 29);
		contentPane.add(textFieldPort);
		textFieldPort.setColumns(10);
		
		labelLogin = new JLabel("LOGIN");
		labelLogin.setFont(new Font("Tahoma", Font.BOLD, 18));
		labelLogin.setBounds(174, 28, 80, 20);
		contentPane.add(labelLogin);
		
		btnEntrar = new JButton("Entrar");
		btnEntrar.setBounds(174, 221, 99, 29);
		contentPane.add(btnEntrar);
		
		labelHost = new JLabel("Host");
		labelHost.setBounds(101, 131, 46, 14);
		contentPane.add(labelHost);
		
		textFieldHost = new JTextField();
		textFieldHost.setBounds(157, 124, 151, 29);
		contentPane.add(textFieldHost);
		textFieldHost.setColumns(10);
	}
	
	private void initActions() {
		btnEntrar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnEntrarActionPerformed(e);
			}
		});
	}
	
	private void btnEntrarActionPerformed(ActionEvent e) {
		String nickname = this.textFieldNome.getText().toUpperCase();
		String host = this.textFieldHost.getText();
		int port = Integer.parseInt(this.textFieldPort.getText());
		
		this.textFieldNome.setText("");
		this.textFieldHost.setText("");
		this.textFieldPort.setText("");
		
		Socket connection;
		try {
			String portaServidorCliente = JOptionPane.showInputDialog(this, "Informe a porta a qual o servidor do cliente irá rodar.");
			
			if(portaServidorCliente.equals(Integer.toString(port))) {
				throw new IOException("A porta já está em uso. Tente novamente.");
			}
			
			connection = new Socket(host, port);
		
			String connectionInfo = 
					new StringBuilder()
					.append(nickname)
					.append(":")
					.append(connection.getLocalAddress().getHostAddress())
					.append(":")
					.append(portaServidorCliente)
					.toString();
			
			Utils.sendMessage(connection, connectionInfo);
			
			if(Utils.receiveMessage(connection).equals(Message.SUCCESS)) {
				Home frameHome = new Home(connection, connectionInfo);
				frameHome.setVisible(true);
				this.dispose();
			
			} else {
				JOptionPane.showMessageDialog(this, "Já existe um usuário com este nome. Tente novamente.");
			}
			
		} catch (UnknownHostException e1) {
			JOptionPane.showMessageDialog(this, "Este host não é válido. Tente novamente.");
			e1.printStackTrace();
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(this, "Falha ao conectar. Verifique se o servidor está em execução.");
			e1.printStackTrace();
		}
	}
}
