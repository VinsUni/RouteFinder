package com.tharun.routes;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;

import com.tharun.persistence.HibernateUtil;

/**
 * Hello world!
 * 
 */
public class App {
	public static void main(String[] args) {

		// Step 1: Go to the URLs and fetch the content.
	   //				insertUrlDescriptions();

		
			//Step 2: Now fetch all the URLs
			List list = getAllRouteDescRecords();
			
			//Step 3: Now send this to sentense Splitter .
			insertIntoSentensesTable(list);
			
			
			
			//Step 4: Now find the landmarks
			
	
	}
	private static void insertIntoSentensesTable(List list)
	{
		for(int i=0; i< list.size();i++)
		{
			RouteDescriptions rd = (RouteDescriptions) list.get(i);
			
			String[] sentenses = Autotagging.sentenseDetection(rd.getRouteDescription());
			insertIntoSentensesTable(rd,sentenses);
			
		}
	}
	
	private static void insertUrlDescriptions()
	{
		HtmlToPlainText htmlObj = new HtmlToPlainText();
		HashMap<String, String> routeDesc = htmlObj.getPlainTextData();

		Iterator it = routeDesc.entrySet().iterator();
		while (it.hasNext())
		{
			Session session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();
			RouteDescriptions routeDescriptionsObj = new RouteDescriptions();
			Map.Entry pairs = (Map.Entry) it.next();

			String key = (String) pairs.getKey();
			String value = (String) pairs.getValue();
			routeDescriptionsObj.setRouteIdUrl(key.trim());
			routeDescriptionsObj.setRouteDescription(value.trim());

			session.save(routeDescriptionsObj);
			session.getTransaction().commit();
		}

	}
	
	
	private static void insertIntoSentensesTable(RouteDescriptions rd, String[] sentenses)
	{
		for(int i =0; i <sentenses.length;i++)
		{
			Session session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();
			RouteSentenses routeSentensesObj = new RouteSentenses();
			routeSentensesObj.setRouteIdUrl(rd.getRouteIdUrl());
			routeSentensesObj.setRouteDescriptionPosition(i);
			routeSentensesObj.setSentenses(sentenses[i]);
	
			session.save(routeSentensesObj);
			session.getTransaction().commit();
		}
	}
	
	public static List getAllRouteDescRecords()
	{
		Session session = HibernateUtil.getSessionFactory().openSession();
		Query query = session.createQuery("from RouteDescriptions");
		List list = query.list();
//		System.out.println(list.size());
//		for(int i=0; i < 2; i++)
//		{
//			RouteDescriptions rd = (RouteDescriptions) list.get(i);
//			System.out.println(rd.getRouteIdUrl());
//		}
		return list;
	}
	
	
	
	
}
