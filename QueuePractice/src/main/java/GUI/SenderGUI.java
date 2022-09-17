package GUI;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.util.Date;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.apache.log4j.BasicConfigurator;

public class SenderGUI extends JFrame implements ActionListener {
	private JButton btnSend;
	private JTextField txtMessage;

	public SenderGUI() {
		setTitle("Sender");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(300, 300);
		JLabel lblMessage = new JLabel("Message");
		txtMessage = new JTextField();

		btnSend = new JButton("Send");
		Box south = new Box(BoxLayout.X_AXIS);
		south.add(lblMessage);
		south.add(txtMessage);
		Box north = new Box(BoxLayout.Y_AXIS);
		north.add(south);
		north.add(btnSend);
		this.add(north);
		btnSend.addActionListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (!e.equals(btnSend)) {
			try {
				String mess = txtMessage.getText();
				BasicConfigurator.configure();
				Properties settings = new Properties();
				settings.setProperty(Context.INITIAL_CONTEXT_FACTORY,
						"org.apache.activemq.jndi.ActiveMQInitialContextFactory");
				settings.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
				Context ctx = new InitialContext(settings);
				ConnectionFactory factory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
				Destination destination = (Destination) ctx.lookup("dynamicQueues/phuocduy");

				Connection con = factory.createConnection("admin", "admin");

				con.start();

				Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
				MessageProducer producer = session.createProducer(destination);
				Message msg = session.createTextMessage("Hello mesage from ActiveMQ");
				producer.send(msg);
				msg = session.createTextMessage(mess);
				producer.send(msg);

				session.close();
				con.close();
				System.out.println("Finished...");
			} catch (Exception e2) {
				// TODO: handle exception
				e2.printStackTrace();
			}

		}
	}
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SenderGUI frame = new SenderGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
