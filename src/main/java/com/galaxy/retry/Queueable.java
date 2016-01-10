package com.galaxy.retry;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.galaxy.Constants;

@Target(value = {ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Queueable {
	public String exchange()  default Constants.SYNC_EXCHANGE;
	public String queue() default Constants.MAIN_WORK_QUEUE;
	//public boolean executeNow() default false;//if true, do not queue it and proceed right now
}
