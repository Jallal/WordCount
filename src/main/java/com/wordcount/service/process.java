package com.wordcount.service;

import javax.jms.JMSException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import org.apache.log4j.Logger;
import org.apache.xbean.spring.context.ClassPathXmlApplicationContext;
import org.springframework.context.ApplicationContext;
import org.apache.activemq.broker.BrokerFactory;
import org.apache.activemq.broker.BrokerService;

public class process {
	final static Logger logger = Logger.getLogger(process.class);
	static BrokerService broker = null;

	public static void main(String[] args) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("spring-web-servlet.xml");
		File inputfile = new File(args[0]);
		File outputfile = new File(args[1]);
		BufferedReader br = null;
		FileReader fr = null;
		try {
			broker = BrokerFactory.createBroker(new URI("broker:(tcp://localhost:61619)"));
			broker.start();
			String sCurrentLine;
			fr = new FileReader(inputfile);
			br = new BufferedReader(fr);
			SpringProducer prdSvc = (SpringProducer) ctx.getBean("producer");
			SpringConsumer conSvc = (SpringConsumer) ctx.getBean("consumer");
			while ((sCurrentLine = br.readLine()) != null) {
				prdSvc.start(sCurrentLine);
				conSvc.start();
			}
			conSvc.printToFile(outputfile);
		} catch (JMSException e) {
			logger.error(e);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			try {

				if (br != null)
					br.close();

				if (fr != null)
					fr.close();

			} catch (IOException ex) {

				ex.printStackTrace();

			}

		}

	}
}
