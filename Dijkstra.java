import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.TreeMap;


public class Dijkstra 
{
	private Map<String, Vertex> airportGraph = new TreeMap<String, Vertex>();//a map of vertices, a "graph"
	private String start = "";//departure airport
	private String end = "";//arrival airport
	private ArrayList<Vertex> shortestPath = new ArrayList< >();
	
	public Dijkstra()
	{
		makeAirportGraph();
		getUserInputs();
		computeDistances(start); 
		getShortestPath();//gets the shortest path in an arraylist and shoves it into an arraylist
		printCheapestCost();
		printConnections();
		printShortestPath();
	}
	
	//Dijkstra's algorithm. Computes the values of all the vertices
	private void computeDistances(String name)
	{
		Vertex source = airportGraph.get(name);
		source.distance = 0;
		
		PriorityQueue<Vertex> airportPQ = new PriorityQueue< >();
		airportPQ.add(source);
		
		while(!airportPQ.isEmpty())
		{
			Vertex w = airportPQ.poll();
			Edge edge;
			int adjListSize = w.adjList.size();
			
			w.visited = true;
			
			//loops through Vertex w's adjacency list
			for(int i = 0; i < adjListSize; i++)
			{
				edge = w.adjList.get(i);//gets an edge
				String airportName = edge.connectedEdge;
				int edgeWeight = edge.weight;
				
				Vertex adjVertex = airportGraph.get(airportName);
				
				if(!adjVertex.visited)
				{
					int totalDistance = w.distance + edgeWeight;//total distance is the distance from the vertex to its adjacent neighbor
					if(totalDistance < adjVertex.distance)
					{
						airportPQ.remove(adjVertex);
						adjVertex.distance = totalDistance;
						adjVertex.parent = w.name;
						airportPQ.add(adjVertex);
					}
				}
			}
		}
	}
	
	private void getShortestPath()
	{
		/*
		 * Vertex v = last vertex on graph
		 * condition: parent (node you came from) isn't blank
		 * increment: get parent vertex
		 */
		for(Vertex v = airportGraph.get(end); v.parent != null;  v = airportGraph.get(v.parent))
			shortestPath.add(v);
		
		shortestPath.add(airportGraph.get(start));//can't get it to grab starting vertex. Adding this statement in
		Collections.reverse(shortestPath);//path added in backwards. Reversing it
			
	}
	
	/*
	 * gets starting/departure and ending/arrival vertices
	 */
	private void getUserInputs()
	{
		Scanner scanner = new Scanner(System.in);
		boolean canContinue = false;
		
		do
		{
			System.out.print("Enter departing airport: ");
			start = scanner.nextLine().toUpperCase();
			
			if(!airportGraph.containsKey(start))
				System.err.println("That's not an airport.");
			else
				canContinue = true;
			
		}while(!canContinue);
		
		canContinue = false;
		
		do
		{
			System.out.print("Enter arrival airport: ");
			end = scanner.nextLine().toUpperCase();
			
			if(!airportGraph.containsKey(end))
				System.err.println("Invalid airport");
			else if(end.equals(start))
				System.err.println("Cannot travel to self");
			else
				canContinue = true;	
			
		}while(!canContinue);
		
		System.out.println();
	}
	
	private void printShortestPath()
	{
		int shortestPathSize = shortestPath.size();
		
		System.out.print("Route: ");
		for(int i = 0; i < shortestPathSize; i++)
		{
			System.out.print(shortestPath.get(i));
			
			if(i < shortestPathSize - 1)//prints -> until it gets to the 2nd to last element
				System.out.print(" -> ");
		}
		
		System.out.println("\n");//create two new lines to make output easier to read
	}
	
	private void printConnections()
	{
		System.out.println("Connections: " + (shortestPath.size() - 2) );//# of connections = # of elements in shortest path - 2
	}
	
	
	//Makes a graph out of the airports.txt file
	public void makeAirportGraph()
	{
		try 
		{
			Scanner fileScanner = new Scanner(new File("airports.txt"));
			while(fileScanner.hasNextLine())
			{
				String line = fileScanner.nextLine();
				String tokens[] = line.split(" ");
				
				Vertex v = new Vertex(tokens[0]);//takes the 1st item from the text file and makes it the vertex name
				String airport = "";
				int weight;
				
				//initialize the adjacency list inside of the vertex
				for(int i = 1; i < tokens.length; i++)
				{
					String input = tokens[i];
					if(!isInteger(input))
					{
						airport = input;
					}
					else
					{
						weight = Integer.parseInt(input);
						v.adjList.add(new Edge(weight, airport));//attaches an edge to the vertex	
					}
				}
				
				airportGraph.put(tokens[0], v);	//maps the Vertex to the vertex name 			
			}
			
			fileScanner.close();
			
		} catch (FileNotFoundException e) 
		{
			System.err.println("Unable to find airports.txt");
		}
	}
	
	private void printCheapestCost()
	{
		System.out.println("Price: "+ airportGraph.get(end).distance);
	}
	
	private boolean isInteger(String s)
	{
		try
		{
			Integer.parseInt(s);
		}catch(NumberFormatException e)
		{
			return false;
		}
			
		return true;
	}
	
	public class Vertex implements Comparable<Vertex>
	{
		public String name;
		public int distance;
		public String parent;//name of the vertex you came from
		public ArrayList<Edge> adjList;//adjacency list is our edges.
										//an edge has the name of the vertex it's connected to (String)
										//and the distance of the vertex (Integer)
		boolean visited;
		
		public Vertex(String n)
		{
			name = n;
			distance = Integer.MAX_VALUE;
			parent = null;
			adjList =  new ArrayList< >();
			visited = false;
		}
		
		@Override
		public int compareTo(Vertex other) 
		{
			if(distance < other.distance)
				return distance - other.distance;//returns negative number
			else if(distance > other.distance)
				return distance - other.distance;//returns positive number
			else
				return 0;//the equal condition
		}
		
		public String toString()
		{
			return name;
		}
	}
	
	/*
	An edge consists of a weight and the name of the vertex its connected to
	*/
	private class Edge
	{
		int weight;
		String connectedEdge;
		
		Edge(int w, String c)
		{
			weight = w;
			connectedEdge = c;
		}
		
		public String toString()
		{
			return "(" + connectedEdge + ", " + weight + ")";
			
		}
		
	}
	
	public static void main(String[] args)
	{
		Scanner scanner = new Scanner(System.in);
		boolean shouldContinue = true;
		boolean hasValidResponse = false;
		String response;
		
		while(shouldContinue)
		{
			new Dijkstra();
			
			//loop to check if response to the prompt is correct
			do
			{
				System.out.print("Check another route (Y/N)?: ");
				response = scanner.next().toUpperCase();
				
				if(response.equals("Y"))
				{
					hasValidResponse = true;
					shouldContinue = true;
				}
				else if(response.equals("N"))
				{
					hasValidResponse = true;
					shouldContinue = false;
				}
				else
				{
					hasValidResponse = false;
				}
					
			}while(!hasValidResponse);
			
			System.out.println();
		}
		
		scanner.close();
	}
}
