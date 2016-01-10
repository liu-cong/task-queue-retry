package com.galaxy.service;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.galaxy.retry.Queueable;
import com.galaxy.service.interfaces.IDataService;

@Service
public class DataService implements IDataService{
	
	@Override
	@Queueable
	public String callLocalMethod(String arg1) {//local method, assuming always success
		System.out.println("[Service] Local method called.");
		return "OK";
	}

	@Override
	@Queueable
	public String callWebService(Integer failureRatePencentage){//unreliable web service, could fail randomly
		System.out.println("[Service] External web service called.");
		Float random = new Random().nextFloat();
		if (random*100 < failureRatePencentage){
			System.out.println("OOOps, Failed!!!");
			throw new RuntimeException();
		}
		System.out.println("HAHA, Success!!!");
		return "OK";
	}

}
