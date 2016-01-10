package com.galaxy.retry;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;

public class TaskUtils {
	/**
	 * @param annotation the annotation to be modified
	 * @param key the key in the annotation
	 * @param newValue the new value to be associated with the key
	 * */
	public static Object modifyAnnotation(Annotation annotation, String key, Object newValue){
		    Object handler = null;
		    try{
		     handler = Proxy.getInvocationHandler(annotation);
		    }catch(Exception e){
		    	e.printStackTrace();
		    }
		    Field f;
		    try {
		        f = handler.getClass().getDeclaredField("memberValues");
		    } catch (NoSuchFieldException | SecurityException e) {
		        throw new IllegalStateException(e);
		    }
		    f.setAccessible(true);
		    Map<String, Object> memberValues;
		    try {
		        memberValues = (Map<String, Object>) f.get(handler);
		    } catch (IllegalArgumentException | IllegalAccessException e) {
		        throw new IllegalStateException(e);
		    }
		    Object oldValue = memberValues.get(key);
		    if (oldValue == null || oldValue.getClass() != newValue.getClass()) {
		        throw new IllegalArgumentException();
		    }
		    memberValues.put(key,newValue);
		    return oldValue;
		}

	public static Method getMethod(QueueTask queueTask){
		Method method = null;
		try {
			for (Method m: queueTask.getMethodClass().getMethods()){
				if (m.getName().equals(queueTask.getMethodName())){
					method = m;
					break;
				}
			}
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return method;
	}
	
	public static Method getMethod(ProceedingJoinPoint joinPoint) throws NoSuchMethodException, SecurityException{
		Object[] args = joinPoint.getArgs();
		QueueTask queueTask = new QueueTask()
		.setArgs(args)
		.setMethodClass(joinPoint.getSignature().getDeclaringType())
		.setMethodName(joinPoint.getSignature().getName());
		Method method = getMethod(queueTask);
		
		//convert the method to the one in the implementation class 
		method = joinPoint.getTarget().getClass().getDeclaredMethod(joinPoint.getSignature().getName(),
                method.getParameterTypes());
		return method;
	}
	
	public static QueueTask convertToQueueTask(ProceedingJoinPoint joinPoint) throws NoSuchMethodException, SecurityException{
		Object[] args = joinPoint.getArgs();
		QueueTask queueTask = new QueueTask()
		.setArgs(args)
		.setMethodClass(joinPoint.getSignature().getDeclaringType())
		.setMethodName(joinPoint.getSignature().getName());
		Method method = getMethod(queueTask);
		
		//convert the method to the one in the implementation class 
		method = joinPoint.getTarget().getClass().getDeclaredMethod(joinPoint.getSignature().getName(),
                method.getParameterTypes());
		
		queueTask.setMethodClass(method.getDeclaringClass());
		return queueTask;
	}
}
