package de.wwu.muli.defuse;

import de.wwu.muggl.symbolic.flow.defUseChains.structures.Def;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Class representing the analyzed variable definitions and usages for each method and incrementally
 * keeps track of the passed defUse chains
 */
public class DefUseChoices {

    HashMap<Integer, DefUseChoice> choices;

    public DefUseChoices(){
        choices = new HashMap<>();
    }

    public void addChoice(int pc, DefUseChoice defuse) {
        choices.put(pc, defuse);
    }

    public boolean hasChoice(int pc) {
        return choices.containsKey(pc);
    }

    public DefUseChoice getChoice(int pc){
        return choices.get(pc);
    }
}
