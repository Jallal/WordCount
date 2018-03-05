package com.wordcount.service;

import java.util.regex.Pattern;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;

public class SpringProducer {
	private static final Logger LOG = Logger.getLogger(SpringProducer.class);

	private int messageCount = 0;
	private Destination destination;
	private static final Pattern WORD_BOUNDARY = Pattern.compile("\\s*\\b\\s*");
	
	/*
	 *  Start the producer
	 * */
	public void start(String CurrentLine) throws JMSException {
		ConnectionFactory factory = new ActiveMQConnectionFactory(
				"vm://localhost?broker.persistent=false&marshal=true");
		Connection connection = factory.createConnection();
		Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
		Queue queue = session.createQueue("customerQueue");

		for (String word : WORD_BOUNDARY.split(CurrentLine)) {
			if (word.isEmpty()) {
				continue;
			}

			if ((word.length() > 1) && (!Character.isDigit(word.charAt(0))) && (Character.isLetter(word.charAt(0)))) {
				Message msg = session.createTextMessage(word);
				MessageProducer producer = session.createProducer(queue);
				producer.send(msg);
			} else {
				if (Character.isLetter(word.charAt(0)) || Character.isDigit(word.charAt(0))
						|| (!Pattern.matches("\\p{Punct}", word.toString()))) {
					// don't add it will be counted with the letters
				} else {
					Message msg = session.createTextMessage(word);
					MessageProducer producer = session.createProducer(queue);
					producer.send(msg);

				}
			}

			for (int i = 0; i < word.length(); i++) {
				char c = word.charAt(i);
				if (Character.isLetter(c)) {
					char newC = Character.toUpperCase(c);
					String currentLetter = Character.toString(newC);
					Message msg = session.createTextMessage(currentLetter);
					MessageProducer producer = session.createProducer(queue);
					producer.send(msg);
				}
			}
		}

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
