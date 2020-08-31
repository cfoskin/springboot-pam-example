package com.redhat;

import org.springframework.jms.support.destination.DestinationResolver;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;

public class NamingDestinationResolver implements DestinationResolver {

    private String destinationType;

    private static final String TOPIC = "topic";
    private static final String QUEUE = "queue";

    public NamingDestinationResolver(String destinationType) {
        this.destinationType = destinationType;
    }

    @Override
    public Destination resolveDestinationName(Session session, String destinationName, boolean pubsub)
            throws JMSException {
        System.out.println("IN HERE *********** " + session.toString());
        if (this.destinationType.toLowerCase().equals(TOPIC)) {

            return session.createTopic(destinationName);
        } else {
            return session.createQueue(destinationName);
        }
    }
}
