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
