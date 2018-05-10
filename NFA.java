/**
   * file: NFA.java
   * author: Artur Barbosa
   * course: CMPT 440
   * assignment: Grephy 
   * due date: 5/7/2018
   * Version: 2
   * 
   * This file contains the NFA.java file for my grep project.
   *
   */
package grephy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

/**
 * This class implements a NFA based on a given regex.
 */
public class NFA {

	private State startState;
	private State finalState;
	private String alphabets;
	private List<State> states = new ArrayList<State>();
	
	public NFA() {
		
	}
	
	/**
	 * Check whether a given input is recognized by this NFA.
	 * 
	 * @param input given input string
	 * @return true if input is recognized by the NFA, else return false
	 */
	public boolean validateInput(String input) {
		return validateInput(startState, input);
	}
		
	/**
	 * Inner helper method to validate an input string from a given state of this NFA
	 * 
	 * @param currState the current state
	 * @param input the input string
	 * @return true if input string is consumed and NFA reaches the final state, else return false
	 */
	private boolean validateInput(State currState, String input) {
		//System.out.println(currState.toString()+", " + input);
		if(input.isEmpty())
			return (currState == finalState);
		
		String symbol = input.charAt(0)+"";
		if(!alphabets.contains(symbol))
			return false;
		//if(!currState.hasTransitionOnInput(symbol))
		//	return false;
		Set<State> nextPossibleStates = currState.getNextStates(symbol);
		if(nextPossibleStates.isEmpty())
			return false;
		String restInput = input.substring(1);
		for(State nextState : nextPossibleStates) {
			if(validateInput(nextState, restInput))
				return true;
		}
		return false;
	}
	
	public State getStartState() {
		return startState;
	}
	
	public void setStartState(State startState) {
		this.startState = startState;
	}
	
	public State getFinalState() {
		return finalState;
	}
	
	public void setFinalState(State finalState) {
		this.finalState = finalState;
	}
	
	
	public String getAlphabets() {
		return alphabets;
	}
	
	public void setAlphabets(String alphabets) {
		this.alphabets = alphabets;
	}
	
	/**
	 * Create this NFA from a given regex.
	 * 
	 * @param regex the input regex
	 */
	public static NFA buildFromRegex(String regex, String alphabets) {
		List<String> infixRegexTokens = parseRegexTokens(regex);
		//System.out.println(Arrays.toString(infixRegexTokens.toArray()));
		List<String> postfixRegexTokens = convertToPostfixRegexTokens(infixRegexTokens);
		//System.out.println(Arrays.toString(postfixRegexTokens.toArray()));
		
		Stack<NFA> nfaStack = new Stack<NFA>();
		
		for(String tk : postfixRegexTokens) {
			if(isRegexOperator(tk)) {
				if(tk.equals("*")) {
					NFA topNFA = nfaStack.pop();
					NFA star = topNFA.star();
					nfaStack.push(star);
				}
				else if(tk.equals("+")) {
					NFA topNFA = nfaStack.pop();
					NFA star = topNFA.plus();
					nfaStack.push(star);
				}
				else if(tk.equals(".")) {
					NFA nfaRight = nfaStack.pop();
					NFA nfaLeft = nfaStack.pop();
					NFA and = nfaLeft.and(nfaRight);
					nfaStack.push(and);
				}
				else if(tk.equals("|")) {
					NFA nfaRight = nfaStack.pop();
					NFA nfaLeft = nfaStack.pop();
					NFA and = nfaLeft.or(nfaRight);
					nfaStack.push(and);
				}
			}
			else {
				NFA nfa = NFA.createNFAFromLetter(tk.charAt(0), alphabets);
				nfaStack.push(nfa);
			}
		}
		
		return nfaStack.pop();
	}
	
	/**
	 * Inner helper method to create a NFA for a single letter.
	 * @param letter input letter
	 * @param alphabets the set of alphabets
	 * @return the NFA for the letter
	 */
	public static NFA createNFAFromLetter(char letter, String alphabets) {
		State s = State.getNewState();
		State f = State.getNewState();
		
		s.addTransition(letter+"", f);
		
		NFA nfa = new NFA();
		nfa.setStartState(s);
		nfa.setFinalState(f);
		nfa.setAlphabets(alphabets);
		
		return nfa;
	}
	
	/**
	 * Generate (NFA)*
	 * 
	 * @return the star of this NFA
	 */
	public NFA star() {
		
		//State newStart = State.getNewState();
		//State newFinal = State.getNewState();
		startState.addTransition("e", finalState);
		//newStart.addTransition("e", startState);
		finalState.addTransition("e", startState);
		//finalState.addTransition("e", newFinal);
		
		NFA star = new NFA();
		star.setAlphabets(alphabets);
		star.setStartState(startState);
		star.setFinalState(finalState);
		
		return star;
	}
	
	/**
	 * Generate NFA for  (NFA1 | NFA2)
	 * @param otherNFA the other NFA
	 * @return the resulting NFA after or-ing otherNFA with this NFA
	 */
	public NFA or(NFA otherNFA) {
		State newStart = State.getNewState();
		State newFinal = State.getNewState();
		
		newStart.addTransition("e", startState);
		newStart.addTransition("e", otherNFA.getStartState());
		otherNFA.getFinalState().addTransition("e", newFinal);
		finalState.addTransition("e", newFinal);
		
		NFA or = new NFA();
		or.setAlphabets(alphabets);
		or.setStartState(newStart);
		or.setFinalState(newFinal);
		
		return or;
	}
	
	/**
	 * Generate NFA for NFA1.NFA-2
	 * @param otherNFA the other NFA
	 * @return the resulting NFA after and-ing otherNFA with this NFA
	 */
	public NFA and(NFA otherNFA) {
		
		finalState.addTransition("e", otherNFA.getStartState());
		
		NFA or = new NFA();
		or.setAlphabets(alphabets);
		or.setStartState(startState);
		or.setFinalState(otherNFA.getFinalState());
		
		return or;
	}
	
	/**
	 * Generate NFA for  (NFA)+
	 * @return the plus of this NFA
	 */
	public NFA plus() {
		
		finalState.addTransition("e", startState);
		
		NFA or = new NFA();
		or.setAlphabets(alphabets);
		or.setStartState(startState);
		or.setFinalState(finalState);
		
		return or;
	}

	/**
	 * Parse a regex to get individual tokens.
	 * 
	 * @param regex the regex string
	 * @return a List of String of the tokens
	 */
	private static List<String> parseRegexTokens(String regex){
		List<String> tokens = new ArrayList<String>();
		regex = regex.replaceAll("\\(", " \\( ");
		regex = regex.replaceAll("\\)", " \\) ");
		regex = regex.replaceAll("\\+", " \\+ ");
		regex = regex.replaceAll("\\*", " \\* ");
		regex = regex.replaceAll("\\|", " \\| ");
		regex = regex.replaceAll("\\.", " \\. ");
		regex = regex.trim();
		String[] tokensArr = regex.split(" ");
		for(String tk : tokensArr)
			if(!tk.isEmpty())
				tokens.add(tk);
		return tokens;
	}
	
	/**
	 * Convert a infix regex to postfix regex.
	 * 
	 * @param infixRegexTokens the list of tokens of infix regex
	 * @return the list of tokens of postfix regex
	 */
	private static List<String> convertToPostfixRegexTokens(List<String> infixRegexTokens){
		List<String> postfixRegexTokens = new ArrayList<String>();
		Stack<String> operators = new Stack<String>();
		
		for(String tk : infixRegexTokens) {
			if(!isRegexOperator(tk)) {
				postfixRegexTokens.add(tk);
			}
			else {
				if(operators.isEmpty()) {
					operators.push(tk);
				}
				else if(tk.equals(")")){
					String top = operators.pop();
					while(!top.equals("(")) {
						postfixRegexTokens.add(top);
						top = operators.pop();
					}
					//postfixRegexTokens.add(tk);
				}
				else {
					operators.push(tk);
				}
			}
		}
		while(!operators.isEmpty()) {
			postfixRegexTokens.add(operators.pop());
		}
		return postfixRegexTokens;
	}
	
	/**
	 * Check whether a given token is a regex operator.
	 * @param tk the token string
	 * @return true if tk is a regex operator, else return false
	 */
	private static boolean isRegexOperator(String tk) {
		return "()|*+.".contains(tk);
	}
	
	public String toString() {
		String s = "Start: " + startState.toString()+"\n";
		s += "Final: " + finalState.toString()+"\n";
		s += "Transitions:\n";
		List<State> visited = new ArrayList<State>();
		Queue<State> q = new LinkedList<State>();
		q.add(startState);
		
		while(!q.isEmpty()) {
			State state = q.remove();
			visited.add(state);
			HashMap<String, Set<State>> transitions = state.getTransitions();
			for(String symbol : transitions.keySet()) {
				Set<State> nextStates = transitions.get(symbol);
				for(State nextState : nextStates) {
					s += state.toString() + "("+symbol+") -> " + nextState.toString()+"\n";
					if(!visited.contains(nextState)) {
						q.add(nextState);
						visited.add(nextState);
					}
				}
			}
		}
		return s;
	}
}
