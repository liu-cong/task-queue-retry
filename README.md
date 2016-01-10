This project implements a simple task queuing, persistence and retrying framework backed by RabbitMQ and Spring-retry framework. The example is shown by a Spring MVC web service.
##Features
* Asynchronous processing of tasks
* Guaranteed delivery of tasks
* Configurable retry scheme of tasks
* Persist failed tasks after retrying

##Usage
The core packages for queuing and retrying are the `com.galaxy.retry` and `com.galaxy.mq`. 

To enable queuing and retrying of a method, simply annotate your method as 
	`@Queueable` .
This annotation automatically queue the method call and keep retrying if it fails with an exponential back-off policy. The framework will eventually put the task in a "dead message queue" for manual processing if all the retries failed. 

##Demo
This example implements two RESTful APIs:
* GET `http://localhost:8080/mqsyncadapter/localmethod`<br/>Simulates a local task (with 0% failure rate).

* GET `http://localhost:8080/mqsyncadapter/webservice/{percent}` <br/>Simulates a remote web service call with failure rate {percent}%.      
  
Deploy this project in a tomcat server and install RabbitMQ server. By calling the two APIs you can observe how queuing and retry works.