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
	@Override
	public void search(SearchPhase state) {
		// TODO: Find the missing spaceship
		if (state.onPlanetX()) return;
		
		int u = state.currentID();
		HashMap<Integer, Double> visited = new HashMap<Integer, Double>();
		visited.put(state.currentID(), state.signal());
		
		for (NodeStatus ns : state.neighbors()) {
			if (!visited.containsKey(ns.id())) {
				if (state.onPlanetX()) return;
				state.moveTo(ns.id());
				visited.remove(u);
				dfs(state, visited);
				if (!state.onPlanetX()) state.moveTo(u);
			}
		}
	}
	
	public static void dfs(SearchPhase state, HashMap<Integer, Double> visited) {
		if (state.onPlanetX()) return;
		int u = state.currentID();
		visited.put(state.currentID(), state.signal());
		NodeStatus[] neighbors = state.neighbors();
		//Arrays.sort(neighbors, Collections.reverseOrder());
		
		int visits = 0;
		for (NodeStatus ns : neighbors) {
			System.out.println("trying to move to: " +ns.id());
			if (!visited.containsKey(ns.id())) {
				if (state.onPlanetX()) return;
				state.moveTo(ns.id());
				visits++;
				dfs(state, visited);
				if (!state.onPlanetX()) state.moveTo(u);
			}
		}
		
		/**
		// Move backwards if node has no unvisited neighbors
		if (neighbors.length == 1 && visits == 0) {
			state.moveTo(neighbors[0].id());
			dfs(state, visited);
		}
		*/
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
	@Override
	public void rescue(RescuePhase state) {
		// TODO: Complete the rescue mission and collect gems
		System.out.println("here's the boi");
		
		List<Node> shortestPath = Paths.minPath(state.currentNode(), state.earth());
		shortestPath.remove(0);
		System.out.println(shortestPath);
		
		for (Node planet : shortestPath) {
			System.out.println("current state: " + state.currentNode());
			if (state == state.earth()) return;
			returnToEarth(state, planet);
		}
	}
	
	public void returnToEarth(RescuePhase state, Node nextPlanet) {
		System.out.println("want to move to: "  + nextPlanet);
		for (Node x : state.nodes()) {
			if (x.equals(nextPlanet)) {
				state.moveTo(nextPlanet);
			}
		}
	}
}
