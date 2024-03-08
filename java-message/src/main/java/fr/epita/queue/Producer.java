package fr.epita.queue;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.jms.ConnectionFactory;
import javax.jms.Session;
import javax.jms.TextMessage;

import java.util.Scanner;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;

public class Producer {
	
	private static final  Logger LOGGER = LogManager.getLogger(Producer.class);
	String username = "";
	String receiver = "";
	static Scanner scanner = new Scanner(System.in);
	
	public Producer() {
	}
	
	public void sendMessage() {
		Session session = null;
		Connection connection = null;
		try {
			//http://localhost:61616
			ConnectionFactory factory = (ConnectionFactory) new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_BROKER_URL);
			connection = factory.createConnection();
			connection.start();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Destination destination = session.createQueue("Queue"+ this.receiver);
			LOGGER.info("Queue name: " + "Queue"+ this.receiver);
			MessageProducer producer = session.createProducer(destination);
			
			while(true) {
				System.out.println("Type a message:");
				String line =  scanner.nextLine();
				if (line.equalsIgnoreCase("bye")) {
					scanner.close();
					session.close();
					connection.close();
					break;
				}
				TextMessage message = session.createTextMessage();
				message.setText(username + "sends : "+ line);
				producer.send(message);
				LOGGER.info("Sent message: " + message.getText());
			}
	
		} catch (JMSException ex) {
			ex.printStackTrace();
		}
	}
	public static void main(String[] args) {
		Producer producer = new Producer();
		System.out.println("Enter your name:");
		producer.username = scanner.nextLine();
		System.out.println("Enter the receiver code:");
		producer.receiver = scanner.nextLine();
		producer.sendMessage();
	}
}
