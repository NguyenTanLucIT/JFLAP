/* -- JFLAP 4.0 --
 *
 * Copyright information:
 *
 * Susan H. Rodger, Thomas Finley
 * Computer Science Department
 * Duke University
 * April 24, 2003
 * Supported by National Science Foundation DUE-9752583.
 *
 * Copyright (c) 2003
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms are permitted
 * provided that the above copyright notice and this paragraph are
 * duplicated in all such forms and that any documentation,
 * advertising materials, and other materials related to such
 * distribution and use acknowledge that the software was developed
 * by the author.  The name of the author may not be used to
 * endorse or promote products derived from this software without
 * specific prior written permission.
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 * WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 */
 
package automata.fsa;

import automata.*;
import automata.fsa.*;
import grammar.*;
import grammar.reg.*;
import java.util.*;

/**
 * The FSA to regular grammar converter can be used to convert
 * a finite state automaton into an equivalent regular grammar.
 * The fsa and grammar will be equivalent in that they will accept
 * exactly the same language.  In order to use this converter you
 * must first call initializeConverter to map the states in the
 * fsa to variables in the regular grammar.  Then you can perform
 * the conversion simply by calling convertToRegularGrammar, or
 * you can perform the conversion step by step by repeatedly calling
 * getProductionForTransition for every transition in the automaton
 * and adding the returned productions to the grammar.  If you do this
 * for every Transition in the fsa, you will have an equivalent regular
 * grammar.
 *
 * @see grammar.reg.RegularGrammar
 *
 * @author Ryan Cavalcante
 */

public class FSAToRegularGrammarConverter {
    /**
     * Creates an instance of <CODE>FSAToRegularGrammarConverter</CODE>
     */
    public FSAToRegularGrammarConverter() {

    }
    
    /**
     * Maps the states in <CODE>automaton</CODE> to the variables 
     * in the grammar the converter will produce.
     * @param automaton the automaton.
     */
    public void initializeConverter(Automaton automaton) {
	MAP = new HashMap();
	State[] states = automaton.getStates();
	State initialState = automaton.getInitialState();
	// Do the variables.
	VARIABLE = new LinkedList();
	for (char c = 'A'; c <= 'Z'; c++)
	    VARIABLE.add(""+c);
	// Map the initial state to S.
	if (initialState != null) {
	    VARIABLE.remove(START_VARIABLE);
	    MAP.put(initialState, START_VARIABLE);
	}
	// Assign variables to the other states.
	List stateList = new ArrayList(Arrays.asList(states));
	stateList.remove(initialState);
	Collections.sort(stateList, new Comparator() {
		public int compare(Object o1, Object o2) {
		    return ((State)o1).getID() - ((State)o2).getID();
		}
		public boolean equals(Object o) { return false; }
	    });
	Iterator it = stateList.iterator();
	while (it.hasNext()) {
	    State state = (State) it.next();
	    MAP.put(state, VARIABLE.removeFirst());
	}
    }

    /**
     * Returns a production object that is equivalent to 
     * <CODE>transition</CODE>.
     * @param transition the transition.
     * @return a produciton object that is equivalent to
     * <CODE>transition</CODE>.
     */
    public Production getProductionForTransition(Transition transition) {
	FSATransition trans = (FSATransition) transition;
	
	State toState = trans.getToState();
	State fromState = trans.getFromState();
	String label = trans.getLabel();
	String lhs = (String) MAP.get(fromState);
	String rhs = label.concat((String) MAP.get(toState));
	Production production = new Production(lhs, rhs);
	
	return production;
    }

    /**
     * Returns a lambda production on the variable mapped to 
     * <CODE>state</CODE> in <CODE>map</CODE>.
     * @param automaton the automaton that <CODE>state</CODE> is in.
     * @param state the state to make the lambda production for.
     * @return a lambda production on the variable mapped to
     * <CODE>state</CODE> in <CODE>MAP</CODE>.
     */
    public Production getLambdaProductionForFinalState(Automaton automaton, 
						       State state) {
	/** Check if state is a final state. */
	if(!automaton.isFinalState(state)) {
	    System.err.println(state + " IS NOT A FINAL STATE");
	    return null;
	}
	String llhs = (String) MAP.get(state);
	String lrhs = LAMBDA;
	Production lprod = new Production(llhs, lrhs);
	return lprod;
    }

    /**
     * Returns a RegularGrammar object that represents a grammar
     * equivalent to <CODE>automaton</CODE>.
     * @param automaton the automaton.
     * @return a regular grammar equivalent to <CODE>automaton</CODE>
     */
    public RegularGrammar convertToRegularGrammar(Automaton automaton) {
	/** check if automaton is fsa. */
	if(!(automaton instanceof FiniteStateAutomaton)) {
	    System.err.println("ATTEMPTING TO CONVERT NON FSA TO "
			       + "REGULAR GRAMMAR");
	    return null;
	}
	
	RegularGrammar grammar = new RegularGrammar();
	/** map states in automaton to variables in grammar. */
	initializeConverter(automaton);
	/** go through all transitions in automaton, creating
	 * production for each. */
	Transition[] transitions = automaton.getTransitions();
	for(int k = 0; k < transitions.length; k++) {
	    Production production = 
		getProductionForTransition(transitions[k]);
	    grammar.addProduction(production);
	}
	
	/** for all final states in automaton, add lambda
	 * production. */
	State[] finalStates = automaton.getFinalStates();
	for(int j = 0; j < finalStates.length; j++) {
	    Production lprod = 
		getLambdaProductionForFinalState(automaton, 
						 finalStates[j]);
	    grammar.addProduction(lprod);
	}

	return grammar;
    }

    /**
     * Returns the variable in the grammar corresponding to the state.
     * @param state the state to get the variable for
     * @return the variable in the grammar corresponding to the state,
     * or <CODE>null</CODE> if there is no variable corresponding to
     * this state
     */
    public String variableForState(State state) {
	return (String) MAP.get(state);
    }

    /** The map of states in the fsa to variables in the grammar. */
    protected HashMap MAP;
    /** The start variable. */
    protected static final String START_VARIABLE = "S";
    /** The string for lambda. */
    protected static final String LAMBDA = "";
    /** The list of unclaimed variable symbols. */
    protected LinkedList VARIABLE;
}
