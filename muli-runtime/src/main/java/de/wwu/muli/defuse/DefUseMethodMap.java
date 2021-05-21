package de.wwu.muli.defuse;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Collection;

public class DefUseMethodMap {
    private Map<String, DefUseChoice> defUseMap;

    public DefUseMethodMap(){
        defUseMap = new HashMap<>();
    }

    public void put(String method, DefUseChoice defuse){
        defUseMap.put(method, defuse);
    }

    public DefUseChoice get(String method){
        return defUseMap.get(method);
    }

    public Set<Map.Entry<String, DefUseChoice>> entrySet(){
        return defUseMap.entrySet();
    }

    public Collection<DefUseChoice> values(){
        return defUseMap.values();
    }
}
