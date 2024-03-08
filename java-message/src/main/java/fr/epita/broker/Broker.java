package fr.epita.broker;

import org.apache.activemq.broker.BrokerService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Broker {
	private static final Logger LOGGER = LogManager.getLogger(Broker.class);
	public static void main(String[] args) {
		try {
			BrokerService broker = new BrokerService();
			broker.addConnector("tcp://0.0.0.0:61616");
			broker.start();
			LOGGER.info("Broker is running");
			Object lock = new Object();
			synchronized (lock) {
				lock.wait();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}