package com.redhat;

import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;

import org.jbpm.services.api.ProcessService;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.jbpm.workflow.instance.node.ActionNodeInstance;
import org.jbpm.workflow.instance.node.WorkItemNodeInstance;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.kie.api.runtime.process.NodeInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component("MyProcessEventListener")
public class MyProcessEventListener implements ProcessEventListener {
    private static Logger LOGGER = LoggerFactory.getLogger("MyProcessEventListener Audit");

    @Autowired
    ProcessService processService;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Value("kieserver.auditLog")
    private String defaultDestinationName;

    @Value("kieserver.auditDestinationType")
    private String defaultDestinationType;

    @Override
    public void beforeProcessStarted(ProcessStartedEvent event) {
        LOGGER.info("beforeProcessStarted process {} ", event.getProcessInstance().getId());

    }

    @Override
    public void afterProcessStarted(ProcessStartedEvent event) {
        LOGGER.info("afterProcessStarted process {} ", event.getProcessInstance().getId());

    }

    @Override
    public void beforeProcessCompleted(ProcessCompletedEvent event) {
        LOGGER.info("beforeProcessCompleted process {} ", event.getProcessInstance().getId());

    }

    @Override
    public void afterProcessCompleted(ProcessCompletedEvent event) {
        LOGGER.info("afterProcessCompleted process {} ", event.getProcessInstance().getId());

    }

    @Override
    public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
        LOGGER.info("beforeNodeTriggered process {} ", event.getProcessInstance().getId());
        NodeInstance node = event.getNodeInstance();
        LOGGER.info("Node name: {}", node.getNodeName());
        LOGGER.info("Node node: {}", node.getNode());
        LOGGER.info("Node class: {}", node.getClass());
        LOGGER.info("Node NodeInstanceContainer: {}", node.getNodeInstanceContainer());
        LOGGER.info("Node toString: {}", node.toString());

        WorkflowProcessInstanceImpl processImpl = (WorkflowProcessInstanceImpl) processService
                .getProcessInstance(event.getProcessInstance().getId());

        jmsTemplate.setDestinationResolver(new NamingDestinationResolver(defaultDestinationType));

        if (node instanceof WorkItemNodeInstance) {
            LOGGER.info("Entering WorkItemNodeInstance node instance {}", node);

            try {
                LOGGER.info("Sending JMSNotification to destination type='{}' name='{}'}", defaultDestinationType,
                        defaultDestinationName);
                jmsTemplate.convertAndSend(defaultDestinationName, createCustomMessage(processImpl));
            } catch (JMSException e) {
                e.printStackTrace();
            }

            processImpl.getVariables().forEach((k, v) -> {
                processImpl.setVariable("SomeParam", "Changed by an Event Listener!");
                LOGGER.info("process vars: key {},value {}", k, v);
            });
        }
        if (node instanceof ActionNodeInstance) {
            LOGGER.info("Entering ActionNodeInstance node instance {}", node);

            processImpl.getVariables().forEach((k, v) -> {
                LOGGER.info("ActionNodeInstance process vars: key {},value {}", k, v);
            });          
        }

    }

    protected Map<String, Object> createCustomMessage(
            WorkflowProcessInstanceImpl processImpl) throws JMSException {
        Map<String, Object> msg = new HashMap<String, Object>();
        msg.put("PROCESS_ID", processImpl.getProcessId());
        msg.put("PROCESS_INSTANCE_ID", processImpl.getId());
        msg.put("CORRELATION_KEY", processImpl.getCorrelationKey());
      

        return msg;
    }

    @Override
    public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
        LOGGER.info("afterNodeTriggered process {} ", event.getProcessInstance().getId());

    }

    @Override
    public void beforeNodeLeft(ProcessNodeLeftEvent event) {
        LOGGER.info("beforeNodeLeft process {} ", event.getProcessInstance().getId());
    }

    @Override
    public void afterNodeLeft(ProcessNodeLeftEvent event) {
        LOGGER.info("afterNodeLeft process {} ", event.getProcessInstance().getId());
    }

    @Override
    public void beforeVariableChanged(ProcessVariableChangedEvent event) {
        // TODO Auto-generated method stub

    }

    @Override
    public void afterVariableChanged(ProcessVariableChangedEvent event) {
        // TODO Auto-generated method stub

    }

}
