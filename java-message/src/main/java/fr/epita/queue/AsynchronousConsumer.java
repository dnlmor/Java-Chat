package fr.epita.queue;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.jms.ConnectionFactory;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Message;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;


public class AsynchronousConsumer {
	
	private static final  Logger LOGGER = LogManager.getLogger(AsynchronousConsumer.class);
	
	public AsynchronousConsumer() {}
	
	public void receiveMessage() {
		try {
			
			//http://localhost:61616
			ConnectionFactory factory = (ConnectionFactory) new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_BROKER_URL);
			Connection connection = factory.createConnection();
			connection.start();
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Destination destination = session.createTopic("Topic1");
			MessageConsumer consumer = session.createConsumer(destination);
			consumer.setMessageListener(new MessageListener() {
				@Override
				public void onMessage(Message message) {
					if (message instanceof TextMessage) {
						TextMessage text = (TextMessage) message;
						try {
							LOGGER.info("Received message is : " + text.getText());
						} catch (JMSException e) {
							e.printStackTrace();
						}
					} else {
						LOGGER.info("Received message is : " + message);
					}
				}
			});
		
		} catch (JMSException ex) {
			ex.printStackTrace();
		}
	}
	public static void main(String[] args) {
		AsynchronousConsumer consumer = new AsynchronousConsumer();
		consumer.receiveMessage();
	}
}
