/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.redhat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.jbpm.process.workitem.core.AbstractLogOrThrowWorkItemHandler;
import org.jbpm.process.workitem.core.util.RequiredParameterValidator;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("MyWorkItemHandler")
public class MyWorkItemHandler extends AbstractLogOrThrowWorkItemHandler {
    Logger LOGGER = LoggerFactory.getLogger(MyWorkItemHandler.class);

    public void executeWorkItem(WorkItem workItem,
                                WorkItemManager manager) {
        LOGGER.info("Starting executeWorkItem for Process Instance ID {} ", workItem.getProcessInstanceId());

        try {
            RequiredParameterValidator.validate(this.getClass(),
               workItem);

            // sample parameters
            String sampleParam = (String) workItem.getParameter("SampleParam");
            String sampleParamTwo = (String) workItem.getParameter("SampleParamTwo");
            LOGGER.info("sampleParam: {}", sampleParam);
            LOGGER.info("sampleParamTwo: {}", sampleParamTwo);

            // complete workitem impl...
            ArrayList<String> params = new ArrayList<>(); 
            // return results
            String firstResult =  sampleParam + " This is the first result ";
            String secondResult = sampleParamTwo + " This is the second result ";
            params.add(firstResult);
            params.add(secondResult);
            Map<String, Object> results = new HashMap<String, Object>();
            results.put("workitemResult", params);

            manager.completeWorkItem(workItem.getId(), results);
            LOGGER.info("Completing executeWorkItem for Process Instance ID {} ", workItem.getProcessInstanceId());

        } catch(Throwable cause) {
            handleException(cause);
        }
    }

    @Override
    public void abortWorkItem(WorkItem workItem,
                              WorkItemManager manager) {
        manager.abortWorkItem(workItem.getId());

    }
}


