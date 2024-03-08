package fr.epita.queue;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.jms.ConnectionFactory;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Message;

import java.util.Scanner;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;


public class SynchronousConsumer {
	
	private static final  Logger LOGGER = LogManager.getLogger(SynchronousConsumer.class);
	String username = "";
	
	static Scanner scanner = new Scanner(System.in);
	
	public SynchronousConsumer() {}
	
	public void receiveMessage() throws JMSException {
		Connection connection = null;
		Session session = null;
		try {
			
			//http://localhost:61616
			ConnectionFactory factory = (ConnectionFactory) new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_BROKER_URL);
			connection = factory.createConnection();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Destination destination = session.createQueue("Queue" + username);
			LOGGER.info("Queue name: " + "Queue" + username);
			MessageConsumer consumer = session.createConsumer(destination);
			connection.start();
			while(true) {
				Message message = consumer.receive();
				if (message instanceof TextMessage) {
					TextMessage text = (TextMessage) message;
					LOGGER.info("Received message is"
							+ " : " + text.getText());
					if (text.getText().equalsIgnoreCase("bye")) {
						break;
					}
				}
			}
			
		} catch (JMSException ex) {
			ex.printStackTrace();
		} finally {
			scanner.close();
			if (session != null) {
				session.close();
			}
			if (connection != null) {
				connection.close();
			}
		}
	}
	public static void main(String[] args) {
		SynchronousConsumer consumer = new SynchronousConsumer();
		System.out.println("Enter your name:");
		consumer.username = scanner.next();
		try {
			consumer.receiveMessage();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
