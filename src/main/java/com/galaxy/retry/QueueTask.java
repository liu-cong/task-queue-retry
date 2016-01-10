package com.galaxy.retry;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonIgnoreType;

import com.fasterxml.jackson.annotation.JsonIgnore;
/**
 * */
public class QueueTask implements Serializable{
	private Class<?> methodClass;
	private String methodName;
	private Object[] args;
	
	public QueueTask(){}
	
	public Object invoke(){
		return args;
		
	}
	
	public Object[] getArgs() {
		return args;
	}
	public QueueTask setArgs(Object[] args) {
		this.args = args;
		return this;
	}

	public Class<?> getMethodClass() {
		return methodClass;
	}

	public QueueTask setMethodClass(Class<?> methodClass) {
		this.methodClass = methodClass;
		return this;
	}

	public String getMethodName() {
		return methodName;
	}

	public QueueTask setMethodName(String methodName) {
		this.methodName = methodName;
		return this;
	}
}
