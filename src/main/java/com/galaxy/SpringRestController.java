package com.galaxy;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.galaxy.service.interfaces.IDataService;

@RestController  
@RequestMapping("/") 
@EnableRetry
public class SpringRestController {
	@Autowired IDataService dataService;
	
	@RequestMapping(value = "/localmethod", method = RequestMethod.GET)  
	public String callReliableLocalMethod() {  
		String result = dataService.callLocalMethod("Arg");
		return result;  
	}
	
	@RequestMapping(value = "/webservice/{percent}", method = RequestMethod.GET)  
	public String callRemoteWebSerivice(@PathVariable Integer percent) {  
		String result = dataService.callWebService(percent);
		return result;
	}

	}
 