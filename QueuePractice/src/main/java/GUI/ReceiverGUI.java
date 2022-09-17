package GUI;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.log4j.BasicConfigurator;

public class ReceiverGUI extends JFrame {
	private JTextField txtMessage;

	public ReceiverGUI() throws NamingException, JMSException {
		setTitle("Receiver");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(300, 300);
		JLabel lblMessage = new JLabel("Message");
		txtMessage = new JTextField();
		Box south = new Box(BoxLayout.X_AXIS);
		south.add(lblMessage);
		Receiver();
		south.add(txtMessage);
		Box north = new Box(BoxLayout.Y_AXIS);
		north.add(south);
		this.add(north);
	}

	private void Receiver() throws NamingException, JMSException {
		// TODO Auto-generated method stub
		BasicConfigurator.configure();
		Properties settings = new Properties();
		settings.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
		settings.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
		Context ctx = new InitialContext(settings);
		Object obj = ctx.lookup("ConnectionFactory");
		ConnectionFactory factory = (ConnectionFactory) obj;
		Destination destination = (Destination) ctx.lookup("dynamicQueues/phuocduy");
		Connection con = factory.createConnection("admin", "admin");
		con.start();
		Session session = con.createSession(false, Session.CLIENT_ACKNOWLEDGE);
		MessageConsumer receiver = session.createConsumer(destination);

		System.out.println(" was listened on queue...");
		receiver.setMessageListener(new MessageListener() {
			public void onMessage(Message msg) {
				try {
					if (msg instanceof TextMessage) {
						TextMessage tm = (TextMessage) msg;
						String txt = tm.getText();
						System.out.println("Receiver" + txt);
						txtMessage.setText(txt);
						msg.acknowledge();
					} else if (msg instanceof ObjectMessage) {
						ObjectMessage om = (ObjectMessage) msg;
						System.out.println(om);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ReceiverGUI frame = new ReceiverGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
