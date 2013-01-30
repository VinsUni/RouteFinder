package com.tharun.routes;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.hibernate.Session;

import com.tharun.persistence.HibernateUtil;

/**
 * Hello world!
 * 
 */
public class App {
	public static void main(String[] args) {

		// Step 1: Go to the URLs and fetch the content.

		HtmlToPlainText htmlObj = new HtmlToPlainText();
		HashMap<String, String> routeDesc = htmlObj.getPlainTextData();

		Iterator it = routeDesc.entrySet().iterator();
		while (it.hasNext()) {
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

		// System.out.println( routeDescriptionsObj.getRouteIdUrl());
		// System.out.println(routeDescriptionsObj.getRouteDescription());
	}
}
