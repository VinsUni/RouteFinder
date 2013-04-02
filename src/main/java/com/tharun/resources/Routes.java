package com.tharun.resources;

import java.util.Stack;

import org.json.JSONStringer;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.tharun.routes.FindRoutes;

public class Routes  extends ServerResource
{
	 @Get("json")
	  public Representation toJson() throws ResourceException, Exception
	  {
		 
		 Representation rep = null;
		  StringBuilder routeDesc = null;
	      try
	      {
	          //get param from request
	    	  String from =  getQuery().getValues("from");
	    	  String to = getQuery().getValues("to");
	        System.out.println(  from+"\t REached Server");
	        System.out.println(to);
	        
	        Stack<String> landmarks =FindRoutes.findRoutes(from.trim(),to.trim()); 
	         routeDesc = FindRoutes.printRoutes(landmarks);
	        
	         //call DAO with parameter
	      }
	      catch(Exception e)
	      {
	          throw e;
	      }
	      JSONStringer jsReply = new JSONStringer();
	      jsReply.object();

	      jsReply.key("CODE").value("SUCCESS");
	      
	      jsReply.key("DESC").value(routeDesc);

	      jsReply.endObject();
	      rep = new JsonRepresentation(jsReply);
	      getResponse().setStatus(Status.SUCCESS_OK);
	      
		return rep;
	  }
}
