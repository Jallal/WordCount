package com.wordcount.service;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;

public class SpringProducer {
	private static final Logger LOG = Logger.getLogger(SpringProducer.class);

	private int messageCount = 0;
	private Destination destination;

	public void start(final String imsg) throws JMSException {
		ConnectionFactory factory = new ActiveMQConnectionFactory(
				"vm://localhost?broker.persistent=false&marshal=true");
		Connection connection = factory.createConnection();
		Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
		Queue queue = session.createQueue("customerQueue");
		String payload = imsg;
		Message msg = session.createTextMessage(payload);
		MessageProducer producer = session.createProducer(queue);
		producer.send(msg);
		System.out.println("*************************** SENDING MESSAGE *****************************");
		System.out.println("*************************************************************************");
		System.out.println("Sending to service  : " + msg.getJMSDestination());
		System.out.println("Sending timstamp    : " + ((TextMessage) msg).getJMSTimestamp());
		System.out.println("Sending Mesaage     : " + ((TextMessage) msg).getText());
		System.out.println("**************************************************************************");
		session.close();
	}

	public int getMessageCount() {
		return messageCount;
	}

	public void setMessageCount(int messageCount) {
		this.messageCount = messageCount;
	}

	public Destination getDestination() {
		return destination;
	}

	public void setDestination(Destination destination) {
		this.destination = destination;
	}
}
