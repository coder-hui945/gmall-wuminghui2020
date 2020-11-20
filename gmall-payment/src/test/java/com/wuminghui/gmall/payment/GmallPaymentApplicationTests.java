package com.wuminghui.gmall.payment;

import com.wuminghui.gmall.mq.ActiveMQUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

@SpringBootTest
class GmallPaymentApplicationTests {

	@Autowired
	ActiveMQUtil activeMQUtil;

	@Test
	void contextLoads() throws JMSException {
		ConnectionFactory connectionFactory = activeMQUtil.getConnectionFactory();
		Connection connection = connectionFactory.createConnection();
		System.out.println(connection);

}

}
