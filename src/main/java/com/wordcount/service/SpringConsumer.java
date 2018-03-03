package com.wordcount.service;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;


public class SpringConsumer implements MessageListener {
	private static final Logger LOG = Logger.getLogger(SpringConsumer.class);

	private Destination destination;
	private String myId = "foo";

	public void start() throws JMSException {
		try {
			ConnectionFactory factory = new ActiveMQConnectionFactory(
					"vm://localhost?broker.persistent=false&marshal=true");
			Connection connection = factory.createConnection();
			Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
			session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
			Queue queue = session.createQueue("customerQueue");
			synchronized (connection) {
				if (connection.getClientID() == null) {
					connection.setClientID(myId);
				}
			}
			connection.start();
			MessageConsumer consumer = session.createConsumer(queue);
			consumer.setMessageListener(this);
			connection.start();
		} catch (JMSException ex) {
			LOG.error("", ex);
			throw ex;
		}
	}

	public void onMessage(Message message) {
		try {
			message.acknowledge();
			TextMessage msg = null;
			if (message instanceof TextMessage) {
				msg = (TextMessage) message;
				System.out.println("*************************** RECEIVING  MESSAGE *****************************");
				System.out.println("****************************************************************************");
				System.out.println("Delivery Mode         : " + msg.getJMSDeliveryMode());
				System.out.println("Receiving timstamp    : " + ((TextMessage) msg).getJMSTimestamp());
				System.out.println("Receiving Mesaage     : " + ((TextMessage) msg).getText());
				System.out.println("****************************************************************************");

				String str = msg.getText();
				String[] filePath = str.split("\\s+");
				WordCount count = new WordCount();
				count.CountWordsInFile(filePath);

			} else {
				System.out.println("Message is not a " + "TextMessage");
			}

		} catch (JMSException e) {
			LOG.error("Failed to acknowledge: " + e, e);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Destination getDestination() {
		return destination;
	}

	public void setDestination(Destination destination) {
		this.destination = destination;
	}

	public String getMyId() {
		return myId;
	}

	public void setMyId(String myId) {
		this.myId = myId;
	}
}
