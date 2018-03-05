package com.wordcount.service;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

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
import java.util.Comparator;

public class SpringConsumer implements MessageListener {
	private static final Logger LOG = Logger.getLogger(SpringConsumer.class);

	private Destination destination;
	private String myId = "getIt";
	BufferedWriter bw = null;
	FileWriter fw = null;
	Map<String, Integer> dataCounter = new HashMap<String, Integer>();
	
	
	/*
	 *  Function to start the message listener
	 * 
	 * */
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
	
	
	
	/*
	 * message listener
	 * */
	public void onMessage(Message message) {
		try {
			message.acknowledge();
			TextMessage msg = null;
			if (message instanceof TextMessage) {
				msg = (TextMessage) message;
				String word = msg.getText();
				
				/*
				 * pull the value from the MAP and update the count
				 * 
				 * */
				Integer count = dataCounter.get(word);
				count = (count == null) ? 1 : count + 1;
				dataCounter.put(word, count);

			} else {
				System.out.println("Message is not a " + "TextMessage");
			}

		} catch (JMSException e) {
			LOG.error("Failed to acknowledge: " + e, e);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	/*
	 *  printing the map to a file
	 * 
	 * */
	public void printToFile(File outputfile) {
		try {
			
			fw = new FileWriter(outputfile);
			bw = new BufferedWriter(fw);
			Iterator it = dataCounter.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry) it.next();
				System.out.println(pair.getKey() + " = " + pair.getValue());
				bw.write(pair.getKey() + " : " + pair.getValue());
				bw.newLine();
				it.remove(); 
			}
		} catch (Exception e) {

		} finally {

		try {

			if (bw != null)
				bw.close();

			if (fw != null)
				fw.close();

		} catch (IOException ex) {

			ex.printStackTrace();

		}

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
