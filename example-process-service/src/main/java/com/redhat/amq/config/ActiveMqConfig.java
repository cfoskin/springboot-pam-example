package com.redhat.amq.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import javax.jms.ConnectionFactory;

@Profile("!test")
@Configuration
@EnableJms
public class ActiveMqConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActiveMqConfig.class);

    @Value("${activemq.port}")
    private String activemqPort;

    @Value("${activemq.host}")
    private String activeMqServiceName;

    public ActiveMqConfig() {
    }

    /*
     * Initial ConnectionFactory
     */
    @Bean
    public ConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(getBrokerUrl());
        return connectionFactory;
    }

    @Bean
    public MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }

    /*
     * Used for sending Messages.
     */
    @Bean
    public JmsTemplate jmsTemplate() {
        JmsTemplate template = new JmsTemplate();
        template.setConnectionFactory(connectionFactory());
        template.setMessageConverter(jacksonJmsMessageConverter());
        template.setPubSubDomain(true);
        return template;
    }

    private String getBrokerUrl() {
        String brokerURL = String.format("tcp://%s:%s?keepAlive=true", activeMqServiceName, activemqPort);
        LOGGER.info("Attempting Activemq connection url [{}]", brokerURL);
        return brokerURL;
    }

}
