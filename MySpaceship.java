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
	
	public ArrayList<Integer> visited = new ArrayList<Integer>();

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
	//@Override
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
	/*@Override
	public void rescue(RescuePhase state) {
		// TODO: Complete the rescue mission and collect gems
		
		List<Node> shortestPath = Paths.minPath(state.currentNode(), state.earth());
		shortestPath.remove(0);
		
		for (Node planet : shortestPath) {
			if (state == state.earth()) return;
			returnToEarth(state, planet);
		}
	}*/
	
	public void returnToEarth(RescuePhase state, Node nextPlanet) {
		for (Node x : state.nodes()) {
			if (x.equals(nextPlanet)) {
				state.moveTo(nextPlanet);
			}
		}
	}
	
	@Override
	public void rescue(RescuePhase state) {
		
		HashMap<Node, Integer> neighbors = state.currentNode().neighbors();
		System.out.println("all neighbors: ");
		System.out.println(neighbors);
		
		List<Node> bestPath = Paths.minPath(state.currentNode(), state.earth());
		
		int maxGems = bestPath.get(1).gems();
		int milesLeft = state.fuelRemaining();
		
		for (Node n : neighbors.keySet()) {
			if (n.gems() > maxGems) {
				maxGems = n.gems();
				
				List<Node> newPath = Paths.minPath(n, state.earth());
				System.out.println("here goes that goo boy");
				System.out.println(newPath);
				
				int distance = 0;
				Node prev = newPath.get(0);
				for (Node x : newPath) {
					System.out.println("entered a new round");
					System.out.println("prev: " + prev.name());
					System.out.println(x.name());
					//System.out.println("neighbors distance: " + x.getEdge(prev).length);
					if (!x.equals(state.currentNode()) && !x.equals(prev)) distance += x.getEdge(prev).length;
					prev = x;
				}
				System.out.println("got out");
				if (distance <= milesLeft) {
					bestPath = newPath;
				}
			}
		}
		System.out.println("new best path: " );
		System.out.println(bestPath);
		if (state.currentNode() != bestPath.get(0)) state.moveTo(bestPath.get(0));
		bestPath.remove(0);
		for (Node planet : bestPath) {
			if (state == state.earth()) return;
			returnToEarth(state, planet);
		}
		
	}
	
}
