
/**
   * file: State.java
   * author: Artur Barbosa
   * course: CMPT 440
   * assignment: Grephy 
   * due date: 5/7/2018
   * Version 1
   * 
   * This file contains the State.java file for my grep project.
   */
package grephy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * This class represents a state in a NFA
 *
 */
public class State {
	
	private String name;
	private int id;
	HashMap<String, Set<State>> transitions = new HashMap<String, Set<State>>();
	private static int nextStateNum = 0;
	
	/**
	 * Returns a new state with unique next id
	 * @return A State object with unique id
	 */
	public static State getNewState() {
		return new State(nextStateNum++);
	}
	
	/**
	 * Class constructor that creates a new State with given id.
	 * @param id the id of the state
	 */
	private State(int id) {
		this.id  = id;
	}
	
	
	public int getId() {
		return id;
	}
	
	public HashMap<String, Set<State>> getTransitions() {
		return transitions;
	}

	/**
	 * Add a new transition from this state to another state on a given
	 * input.
	 * 
	 * @param symbol input symbol of transition
	 * @param toState next state of transition
	 */
	public void addTransition(String symbol, State toState) {
		if(!transitions.containsKey(symbol))
			transitions.put(symbol, new HashSet<State>());
		transitions.get(symbol).add(toState);
	}
	
	/**
	 * Checks whether this has any transition on a given input.
	 * 
	 * @param symbol the symbol of transition
	 * @return true if this state has a transition, else return false
	 */
	public boolean hasTransitionOnInput(String symbol) {
		return transitions.containsKey(symbol);
	}
	
	/**
	 * Get the list of all states reachable from this state on a given input symbol
	 * @param symbol given input symbol
	 * @return the list of reachable states
	 */
	public Set<State> getNextStates(String symbol){
		Set<State> states = new HashSet<State>();
		Set<State> epsilonReachableStates = new HashSet<State>();
		epsilonReachableStates.add(this);
		
		if(this.hasTransitionOnInput("e")) {
			Set<State> epsilonStates = transitions.get("e");
			for(State s : epsilonStates) {
				Set<State> possibleNextStates = getReachableStatesOnEpsilon(s, epsilonReachableStates);
				for(State s2 : possibleNextStates) {
					epsilonReachableStates.add(s2);
				}
			}
		}
		//epsilonReachableStates.add(this);
		Set<State> visited = new HashSet<State>();
		visited.addAll(epsilonReachableStates);
		for(State s : epsilonReachableStates) {
			if(s.hasTransitionOnInput(symbol)) {
				for(State s2 : s.transitions.get(symbol)) {
					states.add(s2);
					visited.add(s2);
					Set<State> moreEpsilonStates = getReachableStatesOnEpsilon(s2, visited);
					for(State s3 : moreEpsilonStates) {
						states.add(s3);
						visited.add(s3);
					}
				}
			}
		}
		return states;
	}
	
	/**
	 * Get the list of all states reachable from a given state with epsilon transition.
	 * 
	 * @param fromState the given from state
	 * @return a list of all states reachable from a given state with epsilon transition.
	 */
	private Set<State> getReachableStatesOnEpsilon(State fromState, Set<State> visited){
		//System.out.println(fromState.toString());
		Set<State> states = new HashSet<State>();
		Queue<State> q = new LinkedList<State>();
		q.add(fromState);
		visited.add(fromState);
		
		while(!q.isEmpty()) {
			State s = q.remove();
			states.add(s);
			
			if(s.hasTransitionOnInput("e")) {
				Set<State> nextStates = s.transitions.get("e");
				for(State s2 : nextStates) {
					if(!q.contains(s2) && !visited.contains(s2)) {
						q.add(s2);
						visited.add(s2);
					}
				}
			}
		}		
		return states;
	}
	
	public String toString() {
		return "q"+id;
	}
	
	public boolean equals(Object obj) {
		if(!(obj instanceof State))
			return false;
		return id == ((State)obj).id;
	}
}
