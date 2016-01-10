package com.galaxy.service.interfaces;

import org.springframework.stereotype.Service;

import com.galaxy.retry.Queueable;
@Service
public interface IDataService {
	String callLocalMethod(String arg1);
	String callWebService(Integer failureRatePencentage);
}
