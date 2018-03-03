package com.wordcount.service;


import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;

import java.net.URI;
import java.net.URISyntaxException;
import org.apache.log4j.Logger;
import org.apache.xbean.spring.context.ClassPathXmlApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.apache.activemq.command.ActiveMQTopic;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;

public class process {
	final static Logger logger = Logger.getLogger(process.class);
	static BrokerService broker=null;
	//Connection connection;
	public static void main(String[] args) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("spring-web-servlet.xml");

		try {
			broker = BrokerFactory.createBroker(new URI("broker:(tcp://localhost:61619)"));
			broker.start();
			SpringProducer prdSvc = (SpringProducer) ctx.getBean("producer");
			prdSvc.start(args[0]);
			SpringConsumer conSvc = (SpringConsumer) ctx.getBean("consumer");
			conSvc.start();
		} catch (JMSException e) {
			logger.error(e);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try{
				//broker.stop();
			}catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
	}
}
