package com.galaxy.retry;

import java.lang.reflect.Method;
import java.util.Map;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.converter.JsonMessageConverter;
import org.springframework.amqp.support.converter.SerializerMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.retry.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import com.galaxy.Constants;
import com.galaxy.mq.Producer;

@Component
@EnableRetry 
@EnableRabbit
public class TaskConsumer {
	@Autowired private ApplicationContext context;
	@Autowired Producer producer;
	@Autowired JsonMessageConverter jsonMessageConverter;
	@Autowired SerializerMessageConverter serializerMessageConverter;

	/**
	 * Consume a task from the queue and proceed the task. If the task cannot be proceeded, put it to retry queue.
	 * @param message message consumed from the queue which contains the task
	 * @return void
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * */
	@RabbitListener(queues = Constants.MAIN_WORK_QUEUE)
	public void consumeTask(Message message) throws InstantiationException, IllegalAccessException {
		System.out.println("[main work queue consumer] Consumer started...");
		QueueTask queueTask = (QueueTask)serializerMessageConverter.fromMessage(message);
		Object o = null;
		//o = context.getBean(queueTask.getMethodClass());
		try {
			o = queueTask.getMethodClass().newInstance();
			Method method = TaskUtils.getMethod(queueTask);
			ReflectionUtils.invokeMethod(method, o, queueTask.getArgs());
		} catch (Exception e) {//if the task fails, put it to retry queue
			producer.publish(Constants.SYNC_EXCHANGE, Constants.RETRY_QUEUE, message);
		}
	}
	
	/**
	 * Consume a task from the queue and proceed the task with retry
	 * @param message message consumed from the queue which contains the task
	 * @return void
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * */
	@Retryable(maxAttempts=Constants.RETRY_MAX_ATTEMPTS,
			value=Exception.class,
			backoff = @Backoff(delay = Constants.RETRY_DELAY,
			multiplier=Constants.RETRY_MULTIPLIER))
	@RabbitListener(queues = Constants.RETRY_QUEUE)
	public void retryTask(Message message) throws InstantiationException, IllegalAccessException {
		System.out.println("[retry queue consumer] Consumer started...");
		QueueTask queueTask = (QueueTask)serializerMessageConverter.fromMessage(message);
		Object o = null;
		//o = context.getBean(queueTask.getMethodClass());
		o = queueTask.getMethodClass().newInstance();
		Method method = TaskUtils.getMethod(queueTask);
		ReflectionUtils.invokeMethod(method, o, queueTask.getArgs());	
	}

	/**Recovery mechanism for the @Retryable method.
	 * @param e the exception captured while retrying
	 * @param message the message argument correspondint to the retryable method
	 * @return void
	 * */
	@Recover
	public void recover(Exception e, Message message){
		System.out.println("Recovering start...");
		String exchange = Constants.SYNC_EXCHANGE;
		String mainWorkQueue = Constants.MAIN_WORK_QUEUE;
		String deadMessageQueue = Constants.DEAD_MESSAGE_QUEUE;
		Map<String, Object> header = message.getMessageProperties().getHeaders();
		int retryTimes = (int)header.get("retryTimes");
		header.put("retryTimes", ++retryTimes);//increase the counter
		if (retryTimes >= Constants.RECOVERY_COUNTER){//reached the recovery counter. publish the message to 
													  //dead message queue for manual processing
			System.out.println("[Recovery] Recovery counter reached. Publish message to dead message queue...");
			producer.publish(exchange, deadMessageQueue, message);
		}else {
			System.out.println("[Recovery] Ready for next retry routine. Publish message to main work queue...");
			producer.publish(exchange, mainWorkQueue, message);
		}
	}

}
