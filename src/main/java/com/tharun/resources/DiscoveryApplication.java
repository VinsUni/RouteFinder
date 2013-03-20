package com.tharun.resources;

import org.restlet.Application;

import org.restlet.Component;

import org.restlet.Restlet;

import org.restlet.data.Protocol;

import org.restlet.routing.Router;

import org.restlet.service.MetadataService;

public class DiscoveryApplication  extends Application{

/**

* Creates a root Restlet that will receive all incoming calls.

*/

@Override

public synchronized Restlet createInboundRoot() {

// Create a router Restlet that routes each call to a new instance of HelloWorldResource.

Router router = new Router(getContext());

// Defines only one route

router.attach("/pref/routes", Routes.class);

return router;

}

 public static void main(String[] args) throws Exception {

 

 MetadataService media = new MetadataService();

 media.getAllMediaTypes(null);

 

  // Create a new Component.

  Component component = new Component();

 

  // Add a new HTTP server listening on port 8182.

  component.getServers().add(Protocol.HTTP, 8183);

 

  // Attach the sample application.

  component.getDefaultHost().attach("/discovery",

   new DiscoveryApplication());

 

  // Start the component.

  component.start();

 }

}