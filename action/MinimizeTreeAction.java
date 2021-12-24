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
 
package gui.action;

import automata.fsa.FiniteStateAutomaton;
import automata.fsa.Minimizer;
import automata.AutomatonChecker;
import gui.environment.*;
import javax.swing.JOptionPane;
import java.io.Serializable;
import java.awt.event.ActionEvent;
import java.awt.Dimension;
import gui.tree.*;
import gui.minimize.*;
import gui.environment.tag.*;
import gui.minimize.MinimizeNodeDrawer;

/**
 * This action allows the user to manually minimize a DFA using a
 * minimization tree.
 * 
 * @author Thomas Finley
 */

public class MinimizeTreeAction extends FSAAction {
    /**
     * Instantiates a new <CODE>MinimizeTreeAction</CODE>.
     * @param automaton the automaton that the tree will be shown for
     * @param environment the environment object that we shall add our
     * simulator pane to
     */
    public MinimizeTreeAction(FiniteStateAutomaton automaton,
			      Environment environment) {
	super("Miniminize DFA", null);
	this.automaton = automaton;
	this.environment = environment;
	/* putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke
	   (KeyEvent.VK_R, MAIN_MENU_MASK+InputEvent.SHIFT_MASK));*/
    }

    /**
     * Puts the DFA form in another window.
     * @param e the action event
     */
    public void actionPerformed(ActionEvent e) {
	if (automaton.getInitialState() == null) {
	    JOptionPane
		.showMessageDialog(Universe.frameForEnvironment(environment),
				   "The automaton should have "+
				   "an initial state.");
	    return;
	}
	AutomatonChecker ac = new AutomatonChecker();
	if(ac.isNFA(automaton)) {
	    JOptionPane
		.showMessageDialog(Universe.frameForEnvironment(environment),
				   "This isn't a DFA!");
	    return;
	}
	// Show the new environs pane.
	FiniteStateAutomaton minimized = 
	    (FiniteStateAutomaton) automaton.clone();
	MinimizePane minPane = new MinimizePane(minimized, environment);
	environment.add(minPane, "Minimization", new CriticalTag() {});
	environment.setActive(minPane);
    }

    /** The automaton. */
    private FiniteStateAutomaton automaton;
    /** The environment. */
    private Environment environment;
    /** That which minimizes a DFA. */
    private static Minimizer minimizer = new Minimizer();

}
