package fr.epita.queue;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.jms.ConnectionFactory;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.MessageProducer;

import java.util.Scanner;

public class User {

    // Logger for logging messages
    private static final Logger LOGGER = LogManager.getLogger(User.class);

    // User's name and the receiver's code
    private String username = "";
    private String receiver = "";

    // Scanner for reading input from the console
    private static Scanner scanner = new Scanner(System.in);

    // Constructor for the User class
    public User() {
    }

    // Method for sending messages to the queue
    public void sendMessage() {
        // JMS-related objects
        Session session = null;
        Connection connection = null;

        try {
            // Create a connection factory and connect to the broker
            ConnectionFactory factory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_BROKER_URL);
            connection = factory.createConnection();
            connection.start();

            // Create a session for message processing
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create a destination (queue) for sending messages
            Destination destination = session.createQueue("Queue" + this.receiver);
            LOGGER.info("Queue name: " + "Queue" + this.receiver);

            // Create a message producer for sending messages
            MessageProducer producer = session.createProducer(destination);

            // Continuously prompt the user to type a message
            while (true) {
                System.out.println("Type a message:");
                String line = scanner.nextLine();

                // If the user types "bye," close resources and break the loop
                if (line.equalsIgnoreCase("bye")) {
                    scanner.close();
                    session.close();
                    connection.close();
                    break;
                }

                // Create a text message and send it to the queue
                TextMessage message = session.createTextMessage();
                message.setText(username + " sends: " + line);
                producer.send(message);
                LOGGER.info("Sent message: " + message.getText());
            }

        } catch (JMSException ex) {
            ex.printStackTrace();
        }
    }

    // Method for receiving messages from the queue
    public void receiveMessage() {
        // JMS-related objects
        Connection connection = null;
        Session session = null;

        try {
            // Create a connection factory and connect to the broker
            ConnectionFactory factory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_BROKER_URL);
            connection = factory.createConnection();

            // Create a session for message processing (to inform)
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create a destination (queue) for receiving messages
            Destination destination = session.createQueue("Queue" + username);
            LOGGER.info("Queue name: " + "Queue" + username);

            // Create a message consumer for receiving messages from the queue
            MessageConsumer consumer = session.createConsumer(destination);

            // Start the connection
            connection.start();

            // Continuously receive and process messages
            while (true) {
                Message message = consumer.receive();

                // If the received message is a text message, log it
                if (message instanceof TextMessage) {
                    TextMessage text = (TextMessage) message;
                    LOGGER.info("Received message is: " + text.getText());

                    // If the received message is "bye," break the loop
                    if (text.getText().equalsIgnoreCase("bye")) {
                        break;
                    }
                }
            }

        } catch (JMSException ex) {
            ex.printStackTrace();
        } finally {
            // Close resources in the finally block
            scanner.close();
            if (session != null) {
                try {
                    session.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Main method for executing the User class
    public static void main(String[] args) throws JMSException {
        // Create an instance of the User class
        User user = new User();

        // Prompt the user to enter their name and the receiver code
        System.out.println("Enter your name:");
        user.username = scanner.nextLine();
        System.out.println("Enter the receiver code:");
        user.receiver = scanner.nextLine();

        // Use separate threads for sending and receiving messages
        Thread senderThread = new Thread(() -> user.sendMessage());
        Thread receiverThread = new Thread(() -> {
            user.receiveMessage();
        });

        // Start the threads
        senderThread.start();
        receiverThread.start();
    }
}
