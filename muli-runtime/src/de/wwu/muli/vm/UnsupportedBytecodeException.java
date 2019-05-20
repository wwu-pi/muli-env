package de.wwu.muli.vm;

public class UnsupportedBytecodeException extends IllegalStateException {
    public UnsupportedBytecodeException(String canonicalName) {
        super(canonicalName);
    }
}
