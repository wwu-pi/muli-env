package de.wwu.muli.defuse;

import java.util.TreeSet;

public class DefUseRegister {

    public boolean visited;
    public TreeSet<Integer> link;

    public DefUseRegister(int link, boolean visited) {
        this.link = new TreeSet<Integer>();
        this.link.add(link);
        this.visited = visited;
    }

    public void addLink(int link){
        this.link.add(link);
    }
}