package com.galaxy;

public class Constants {
	public static final int RETRY_MAX_ATTEMPTS = 3;
	public static final int RETRY_DELAY = 100000;
	public static final int RETRY_MULTIPLIER = 3;
	public static final int RECOVERY_COUNTER = 2;
	public static final String SYNC_EXCHANGE = "syncExchange";
	public static final String MAIN_WORK_QUEUE = "mainWorkQueue";
	public static final String RETRY_QUEUE = "retryQueue";
	public static final String DEAD_MESSAGE_QUEUE = "deadMessageQueue";
}
