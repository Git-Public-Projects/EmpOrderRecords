package com.mindtree.controller;

import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.mindtree.entity.Employee;
import com.mindtree.entity.Order;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

@RestController
@RequestMapping("/CallApi")
public class RestControllers {
	public static String jsp="welcome";
	Client client = Client.create();
	Gson gson = new Gson();
	private static final Logger logger = Logger.getLogger(RestControllers.class);
	
	@RequestMapping(value="/empinsert",method = RequestMethod.GET)
	public ModelAndView getRequestForEmployee(HttpServletRequest request,HttpServletResponse response,@RequestParam("name") String name,@RequestParam("age") String age,@RequestParam("gender") String gender)
	{
	String msg=null;
	Employee employee=new Employee();	
	String empname=name;
	String empAge=age;
	String empGender=gender;
	employee.setName(empname);
	employee.setAge(empAge);
	employee.setGender(empGender);
	try {
		Gson g = new Gson();
		String json = g.toJson(employee);
		System.out.println("Server running....");
		WebResource webResource = client.resource("http://13.76.131.131:8083/EmpOrderRecords/rest/post/employee");
		ClientResponse clientResponse = webResource.type("application/json").post(ClientResponse.class, json.toString());
		if (clientResponse.getStatus() != 201) {
			msg="Employee record could not be inserted";
			throw new RuntimeException("Failed : HTTP error code : " + clientResponse.getStatus());
		}
		msg="Employee record inserted successfully";
	} catch (Exception e) {
		logger.error("Error inserting records");
	}
	ModelAndView mav=new ModelAndView();
	mav.addObject("msg",msg);
	mav.setViewName(jsp);
	return mav;
	
	}

	@RequestMapping(value="/orderinsert",method = RequestMethod.GET)
	public ModelAndView getRequestForOrder(HttpServletRequest request,HttpServletResponse response,@RequestParam("name") String name,@RequestParam("description") String desc)
	{
		String msg=null;
		Order order=new Order();
		String ordername=name;
		String description=desc;
		order.setOrderName(ordername);
		order.setDescription(description);
		try {
			Gson g = new Gson();
			String json = g.toJson(order);
			System.out.println("Generating Response from Server....");
			WebResource webResource = client.resource("http://13.76.131.131:8083/EmpOrderRecords/rest/post/order");
			ClientResponse clientResponse = webResource.type("application/json").post(ClientResponse.class, json.toString());
			if (clientResponse.getStatus() != 201) {
				msg="Order record could not be inserted";
				throw new RuntimeException("Failed : HTTP error code : " + clientResponse.getStatus());
			}
			msg="Order record inserted sucessfully";
		
		} catch (Exception e) {
			logger.error("Order could not be inserted");
		}
		ModelAndView mav=new ModelAndView();
		mav.addObject("msg",msg);
		mav.setViewName(jsp);
		return mav;
	}
	
	@RequestMapping(value="/display",method = RequestMethod.GET)
	public ModelAndView searchRequest(HttpServletRequest request,HttpServletResponse response,@RequestParam("name") String name,@RequestParam("table") String table)
	{
		String searchTable=table;
		String queryname=name;
		String msg=null;
		try {
			String names = URLEncoder.encode (queryname,"UTF-8").replace("+", "%20");
			WebResource webResource = client.resource("http://13.76.131.131:8083/EmpOrderRecords/rest/"+searchTable+"/"+names);
			ClientResponse clientResponse = webResource.accept("application/json").get(ClientResponse.class);
			if (clientResponse.getStatus() != 201) {
				throw new RuntimeException("Failed : HTTP error code : "+ clientResponse.getStatus());
			}
			String out=clientResponse.getEntity(String.class);
			msg=out;
		} catch (Exception e) {
			logger.error("Exception man");
		}
		ModelAndView mav=new ModelAndView();
		mav.addObject("msg",msg);
		mav.setViewName(jsp);
		return mav;
		
	}
}
