package de.wwu.muli.listener;

import java.util.BitSet;
import java.util.LinkedHashMap;

public interface TcgListener {

    void setMethod(String methodName);

    LinkedHashMap<String, Object> getInputs();

    String getClassName();

    String getMethodName();

    BitSet getCover();

}
