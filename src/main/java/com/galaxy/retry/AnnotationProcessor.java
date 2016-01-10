package com.galaxy.retry;


import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.galaxy.mq.Producer;
import com.galaxy.service.DataService;
/**Class to handle custom annotations
 * */
@Component
@Aspect
public class AnnotationProcessor {
	@Autowired Producer producer;

//	@AfterReturning("@annotation(syncJob)")
//	public void handleSyncJob(JoinPoint joinPoint,SyncJob syncJob ) throws Throwable {
//		Object[] args = joinPoint.getArgs();		
//		QueueTask syncTask = new QueueTask()
//		.setArgs(args)
//		.setMethodClass(syncJob.methodClass())
//		.setMethodName(syncJob.methodName());
//		Map<String, Object> header = new HashMap<String, Object>();
//		header.put("retryTimes", 0);
//
//		/*
//		 * header: set task properties such as retryTimes
//		 * task: the task to be published to the queue
//		 * */
//		producer.publish("syncExchange","mainWorkQueue",header, syncTask);
//	}

	@Around("@annotation(queueable)")
	public void handleQueueable(ProceedingJoinPoint joinPoint,Queueable queueable) throws Throwable {
		String exchange = queueable.exchange();
		String queue = queueable.queue();
		QueueTask queueTask = TaskUtils.convertToQueueTask(joinPoint);
		Map<String, Object> header = new HashMap<String, Object>();
		header.put("retryTimes", 0);
		producer.publish(exchange,queue,header, queueTask);
	}

}
