# springboot-pam-example

Example project for learning about RHPAM with Spring Boot Kieserver, Event listeners and WorkItem Handlers.

Basic Spring boot RHPAM project which includes a sample BPMN process diagram that uses a workitem handler to update some process variables and also uses an event listener to modify these before sending them to an AMQ instance which should be deployed locally.

`mvn clean install`

Navigate to example-process-service and run `mvn spring-boot:run`

Use Postman to start process with some process vars:

POST
```
http://localhost:8090/rest/server/containers/example-process-kjar-1_0-SNAPSHOT/processes/SampleProcess/instances
```

Body:
```
{
    "SomeParam": "process parameter a",
    "SomeOtherParam": "process parameter b"
}
```

Should see some logs such as

```
2020-08-31 15:58:54.972  INFO 6397 --- [0.0-8090-exec-1] MyProcessEventListener Audit             : process vars: key SomeParam,value MY GREAT NEW PARAM
2020-08-31 15:58:54.972  INFO 6397 --- [0.0-8090-exec-1] MyProcessEventListener Audit             : process vars: key initiator,value anonymousUser
2020-08-31 15:58:54.972  INFO 6397 --- [0.0-8090-exec-1] MyProcessEventListener Audit             : process vars: key SomeOtherParam,value process parameter b
2020-08-31 15:58:54.975  INFO 6397 --- [0.0-8090-exec-1] com.redhat.MyWorkItemHandler             : Starting executeWorkItem for Process Instance ID 1
2020-08-31 15:58:54.976  INFO 6397 --- [0.0-8090-exec-1] com.redhat.MyWorkItemHandler             : sampleParam: Changed by an Event Listener!
2020-08-31 15:58:54.976  INFO 6397 --- [0.0-8090-exec-1] com.redhat.MyWorkItemHandler             : sampleParamTwo: process parameter b
...................

2020-08-31 17:36:53.406  INFO 11954 --- [0.0-8090-exec-1] MyProcessEventListener Audit             : Message to be sent: {SomeParam=MY GREAT NEW PARAM, initiator=anonymousUser, PROCESS_INSTANCE_ID=1, CORRELATION_KEY=1, SomeOtherParam=process parameter b, PROCESS_ID=SampleProcess}

..........
2020-08-31 17:36:53.676  INFO 11954 --- [0.0-8090-exec-1] MyProcessEventListener Audit             : ActionNodeInstance process vars: key workitemResult,value [New Value Changed by an Event Listener! This is the first result , process parameter b This is the second result ]
2020-08-31 17:36:53.676  INFO 11954 --- [0.0-8090-exec-1] MyProcessEventListener Audit             : ActionNodeInstance process vars: key SomeOtherParam,value process parameter b
workitemResult1: New Value Changed by an Event Listener! This is the first result
workitemResult2: process parameter b This is the second result
Sample Process Finished
```

Some key parts of this based on https://github.com/redhat-cop/businessautomation-cop/tree/master/pam-quick-examples/spring-boot-examples
