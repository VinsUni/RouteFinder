package com.tharun.routes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import org.hibernate.Query;
import org.hibernate.Session;

import com.tharun.persistence.HibernateUtil;

public class FindRoutes
{
	public static HashMap<String, String> landmarksAdjacenyMap = new HashMap<String, String>();
	
	
	public static boolean flag = false;
	
	public static void main(String args[])
	{
		Stack<String> result =findRoutes("Provo","Pleasant Grove"); 
		
		printRoutes(result);
		
		/**
		 * ("SR-147","US-89")
		 * ("US-89","Pleasant Grove")
		 * ("I-70","Vernal")
		 * ("Chester","SR-120")
		 * ("Chester","Glenwood")
		 * ("Tooele","Glenwood")
		 * ("Bacchus","Tooele")
		 * ("Provo","Orem")
		 * ("Provo","Pleasant Grove")
		 */
		while(!result.isEmpty())
		{
			System.out.println(result.pop());
		}
		
		
		
	}
	
	public static void printRoutes(Stack<String> landmarks)
	{
		while(!landmarks.isEmpty())
		{
			Session session = HibernateUtil.getSessionFactory().openSession();
			String senteseIds = null;
			String hql = "from RouteLandmarks R where R.landmark = "+"'"+landmarks.pop().trim()+"'";
			System.out.println(hql);
			Query query = session.createQuery(hql);
			List list = query.list();
			 for(int i=0; i <list.size();i++)
			 {
				 RouteLandmarks  routeLandmarks  = (RouteLandmarks) list.get(i);
				 senteseIds = 	routeLandmarks.getSentenseIds();
			 }
			 
			 for(String s: senteseIds.split(","))
			 {
				 if(s != null && !s.equals(" ") && !s.isEmpty())
				 {
					 Session sessionObj = HibernateUtil.getSessionFactory().openSession();
					 String hqlQuery = "from RouteSentenses RS where RS.sentenseId =  "+s.trim();
					 Query querySetenses = sessionObj.createQuery(hqlQuery);
					 List listSentenses = querySetenses.list();
					for(int i =0;i< listSentenses.size(); i++)
					{
						RouteSentenses routeSentenses = (RouteSentenses) listSentenses.get(i);
						System.out.println(routeSentenses.getSentenses());
					}
				 }
			 }
			 
			 
			 
		}
	}
	
	
	public static Stack<String> findRoutes(String startPoint, String endPoint)
	{
	    HashMap<String, String> resultingPathMap = new HashMap<String, String>(); // Store the parent and landmark
		Stack<String> finalLandmarks = new Stack<String>();
		loadLandmarksAdjacencyList(); // Load the graph from database.
		Queue<String> q = new LinkedList<String>();
		ArrayList<String> tempRemovedItems = new ArrayList<String>();
		q.add(startPoint.trim());
		while(!q.isEmpty())
		{
			String landmark = q.remove();
			tempRemovedItems.add(landmark);
			if(landmark.equalsIgnoreCase(endPoint))
			{
				flag = true;
				break;
			}
			String adjacencyList = landmarksAdjacenyMap.get(landmark);
			for(String s: adjacencyList.split(","))
			{
				if(!tempRemovedItems.contains(s.trim()) && !s.equals(""))
				{
					q.add(s.trim());
					//Storing the landmark and its parent.
					resultingPathMap.put(s.trim(), landmark);
				}
					
			}
			
		}
		
		if(flag)
		{
			
			finalLandmarks = getPathLandmarks(startPoint,endPoint,resultingPathMap);
			
		}
		return finalLandmarks;
		
	}
	
	private static Stack<String> getPathLandmarks(String startPoint,String endPoint, HashMap<String, String> resultingPathMap)
	{
		Stack<String> finalLandmarks = new Stack<String>();
		String landmark = endPoint;
		finalLandmarks.add(landmark);
		while(true)
		{
			String parent = resultingPathMap.get(landmark);
			finalLandmarks.add(parent);
			landmark = parent;
			if(landmark.equalsIgnoreCase(startPoint))
				break;
			
		}
		
		return finalLandmarks;
		
	}
	
	
	
	private static void loadLandmarksAdjacencyList()
	{
		Session session = HibernateUtil.getSessionFactory().openSession();
		Query query = session.createQuery("from RouteLandmarksGraph");
		List list = query.list();
		 for(int i=0; i <list.size();i++)
		 {
			 RouteLandmarksGraph routeLandmarksGraph = (RouteLandmarksGraph) list.get(i);
			 landmarksAdjacenyMap.put(routeLandmarksGraph.getLandmark().trim(), routeLandmarksGraph.getAdjacencyLandmarks().trim());
				
		 }
	}
	
}
