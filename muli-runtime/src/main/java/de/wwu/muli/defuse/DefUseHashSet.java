package de.wwu.muli.defuse;

import java.util.HashSet;

public class DefUseHashSet<E> extends HashSet<E> {

    HashSet<E> set = new HashSet<E>();

    @Override
    public boolean add(E e) {

        return super.add(e);
    }
}
