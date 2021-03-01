package de.wwu.muli.listener;

import de.wwu.muggl.vm.initialization.Objectref;
import de.wwu.muli.vm.LogicVirtualMachine;

import java.util.BitSet;
import java.util.LinkedHashMap;

public interface TcgListener {

    void setMethod(String methodName);

    LinkedHashMap<String, Object> getInputs();

    String getClassName();

    String getMethodName();

    boolean[] getCover(LogicVirtualMachine vm);

    boolean isObjectMethod();

}
