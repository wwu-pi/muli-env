package de.wwu.muli.defuse;

import java.util.TreeSet;

/**
 * Structure to represent variable definitions and usages while executing the program.
 */
public class DefUseRegister {

    // has this definition/ usage already been passed
    public boolean visited;
    // Link to related usages or definitions for defuses
    public TreeSet<Integer> link;

    public DefUseRegister(int link, boolean visited) {
        this.link = new TreeSet<Integer>();
        this.link.add(link);
        this.visited = visited;
    }

    public void addLink(int link) {
        this.link.add(link);
    }

    public DefUseRegister clone(){
        DefUseRegister r = new DefUseRegister(link.first(), visited);
        for(int i : link){
            r.addLink(i);
        }
        return r;
    }

    public String toString(){
        String output = "Visited: "+visited+"; Links:";
        for(int i: link){
            output += ""+i+",";
        }
        String output2 = output.substring(0,output.lastIndexOf(","));
        return output2;
    }
}