package student;

import controllers.Spaceship;
import models.Edge;
import models.Node;
import models.NodeStatus;

import controllers.SearchPhase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.HashMap;

import controllers.RescuePhase;

/** An instance implements the methods needed to complete the mission. */
public class MySpaceship implements Spaceship {
	
	//contains all visited planets used in the search method
	public ArrayList<Integer> visited = new ArrayList<Integer>();
	
	//contains all visited planets used in the rescue method
	public ArrayList<Node> rescueVisited = new ArrayList<Node>();

	/** The spaceship is on the location given by parameter state.
	 * Move the spaceship to Planet X and then return (with the spaceship is on
	 * Planet X). This completes the first phase of the mission.
	 * 
	 * If the spaceship continues to move after reaching Planet X, rather than
	 * returning, it will not count. If you return from this procedure while
	 * not on Planet X, it will count as a failure.
	 *
	 * There is no limit to how many steps you can take, but your score is
	 * directly related to how long it takes you to find Planet X.
	 *
	 * At every step, you know only the current planet's ID, the IDs of
	 * neighboring planets, and the strength of the signal from Planet X at
	 * each planet.
	 *
	 * In this rescuePhase,
	 * (1) In order to get information about the current state, use functions
	 * currentID(), neighbors(), and signal().
	 *
	 * (2) Use method onPlanetX() to know if you are on Planet X.
	 *
	 * (3) Use method moveTo(int id) to move to a neighboring planet with the
	 * given ID. Doing this will change state to reflect your new position.
	 */

	
	/** perform a DFS from earth to find Planet X */
	public void simpleSearch(SearchPhase state) {
		// TODO: Find the missing spaceship	
		int u = state.currentID();
		visited.add(state.currentID());
		
		if (state.onPlanetX()) return;
		
		for (NodeStatus ns : state.neighbors()) {
			if (!(visited.contains(ns.id()))) {
				state.moveTo(ns.id());
				simpleSearch(state);
				if (state.onPlanetX()) return;
				state.moveTo(u);
			}
		}
	}
	
	
	/** Perform a DFS, checking out nodes with higher signals 
	 * to Planet X first
	 */
	@Override
	public void search(SearchPhase state) {
		int u = state.currentID();
		visited.add(state.currentID());
		
		if (state.onPlanetX()) return;
		
		NodeStatus[] sortedNeighbors = sortNeighbors(state.neighbors());
		
		for (NodeStatus ns : sortedNeighbors) {
			if (!(visited.contains(ns.id()))) {
				state.moveTo(ns.id());
				search(state);
				if (state.onPlanetX()) return;
				state.moveTo(u);
			}
		}
	}
	
	
	/** Sort the neighbors of a given NodeStatus list so that the ones with larger signals
	 * are earlier in the array and will be searched earlier.
	 */
	public NodeStatus[] sortNeighbors(NodeStatus[] list) {
		for (int i = 0; i < list.length; i++) {
			int k = i;
			
			while (k > 0 && list[k].signal() > list[k-1].signal()) {
				NodeStatus temp = list[k];
				list[k] = list[k-1];
				list[k-1] = temp;
				k--;
			}
		}
		return list;
	}
	
	

	/** The spaceship is on the location given by state. Get back to Earth
	 * without running out of fuel and return while on Earth. Your ship can
	 * determine how much fuel it has left via method fuelRemaining().
	 * 
	 * In addition, each Planet has some gems. Passing over a Planet will
	 * automatically collect any gems it carries, which will increase your
	 * score; your objective is to return to earth successfully with as many
	 * gems as possible.
	 * 
	 * You now have access to the entire underlying graph, which can be accessed
	 * through parameter state. Functions currentNode() and earth() return Node
	 * objects of interest, and nodes() returns a collection of all nodes on the
	 * graph.
	 *
	 * Note: Use moveTo() to move to a destination node adjacent to your current
	 * node. */

	/** Uses Dijkstra's shortest path algorithm to find the closest path from the current
	 *  node to earth and return to earth
	 */
	public void simpleRescue(RescuePhase state) {
		// TODO: Complete the rescue mission and collect gems
		
		List<Node> shortestPath = Paths.minPath(state.currentNode(), state.earth());
		shortestPath.remove(0);
		
		for (Node planet : shortestPath) {
			if (state == state.earth()) return;
			returnToEarth(state, planet);
		}
	}
	
	/** Moves the state to the next planet */
	public void returnToEarth(RescuePhase state, Node nextPlanet) {
		for (Node x : state.nodes()) {
			if (x.equals(nextPlanet)) {
				System.out.println("third moveTo trying to move to: " + nextPlanet);
				System.out.println("current node being: " + state.currentNode());
				state.moveTo(nextPlanet);
			}
		}
	}
	
	/** An early iteration of finding more gems - recursively checks for neighbor
	 * with the most gems and if there's enough fuel to go to that neighbor and then back
	 * to earth, then go.
	 */
	public void minPathRescue(RescuePhase state) {
		
		HashMap<Node, Integer> neighbors = state.currentNode().neighbors();
		//System.out.println("all neighbors: ");
		//System.out.println(neighbors);
		
		List<Node> bestPath = Paths.minPath(state.currentNode(), state.earth());
		//System.out.println("first best path: ");
		//System.out.println(bestPath);
		//System.out.println(bestPath.size());
		//System.out.println(bestPath.get(0));
		//System.out.println(state.currentNode());
		
		if (bestPath.get(0).equals(state.earth())) {
			return;
		}
		
		int maxGems = bestPath.get(1).gems();
		int milesLeft = state.fuelRemaining();
		
		for (Node n : neighbors.keySet()) {
			if (n.gems() > maxGems) {
				maxGems = n.gems();
				
				List<Node> newPath = Paths.minPath(n, state.earth());
				//System.out.println("here goes that goo boy");
				//System.out.println(newPath);
				
				int distance = 0;
				Node prev = newPath.get(0);
				for (Node x : newPath) {
					//System.out.println("entered a new round");
					//System.out.println("prev: " + prev.name());
					//System.out.println(x.name());
					//System.out.println("neighbors distance: " + x.getEdge(prev).length);
					if (!x.equals(state.currentNode()) && !x.equals(prev)) distance += x.getEdge(prev).length;
					prev = x;
				}
				//System.out.println("got out");
				if (distance <= milesLeft) {
					bestPath = newPath;
				}
			}
		}
		//System.out.println("new best path: " );
		//System.out.println(bestPath);
		if (state.currentNode() != bestPath.get(0)) state.moveTo(bestPath.get(0));
		bestPath.remove(0);
		returnToEarth(state, bestPath.get(0));
		rescue(state);
		
	}
	
	/** Final optimized iteration - performs a DFS, making sure there's enough
	 * fuel for the minPath back and ends when there isn't
	 */
	@Override
	public void rescue(RescuePhase state) {
		System.out.println("started new round!");
		System.out.println(state.currentNode());
		System.out.println(state.earth());
		System.out.println(state.currentNode().equals(state.earth()));
		if (state == state.earth()) {
			System.out.println("yes");
			return;
		}
		
		Node u = state.currentNode();
		rescueVisited.add(u);
		
		HashMap<Node, Integer> neighbors = u.neighbors();
		Node maxGemsNeighbor = null;
		int maxGems = 0;
		
		//get neighbor with most gems
		for (Node n : neighbors.keySet()) {
			if (!rescueVisited.contains(n) && n.gems() > maxGems) {
				maxGems = n.gems();
				maxGemsNeighbor = n;
			}
		}
		if (maxGemsNeighbor == null) {
			if (state == state.earth()) return;
			System.out.println("yes");
			List<Node> backTrack = Paths.minPath(state.currentNode(), state.earth());
			System.out.println(backTrack);
			
			System.out.println("currently at: " + state.currentNode());
			System.out.println("calling moveTo to: " + backTrack.get(1));
			state.moveTo(backTrack.get(1));
			if (state == state.earth()) return;
			else rescue(state);
		}
		
		//check if can get back to earth from that neighbor
		System.out.println("maxGemsNeighbor is: " + maxGemsNeighbor);
		List<Node> pathBack = Paths.minPath(maxGemsNeighbor, state.earth());
		int distance = state.currentNode().neighbors().get(maxGemsNeighbor);
		System.out.println(pathBack);
		pathBack.remove(0);
		//System.out.println(pathBack);
		
		Node prev = maxGemsNeighbor;
		System.out.println("distance start: " + distance);
		for (Node x : pathBack) {
			if (!x.equals(state.currentNode()) && !x.equals(prev)) {
				System.out.println("adding to distance");
				System.out.println("prev is: " + prev.name());
				System.out.println("x is: " + x.name());
				System.out.println("current node is: " + u.name());
				System.out.println("distance between them is: " + x.getEdge(prev).length);
				distance += x.getEdge(prev).length;
			}
			prev = x;
		}
		
		//System.out.println("distance: " + distance);
		
		//if it has enough fuel, move to it and repeat the process
		if (distance <= state.fuelRemaining()) {
			System.out.println("second moveTo trying to move to: " + maxGemsNeighbor);
			System.out.println("current node being: " + state.currentNode());
			state.moveTo(maxGemsNeighbor);
			rescue(state);
		} else { //if not enough fuel get back to earth
			if (state == state.earth()) return;
			System.out.println("Not enough fuel");
			List<Node> newPath = Paths.minPath(state.currentNode(), state.earth());
			newPath.remove(0);
			for (Node planet : newPath) {
				if (state == state.earth()) return;
				returnToEarth(state, planet);
			}
		}
	}
	
	
	
}
