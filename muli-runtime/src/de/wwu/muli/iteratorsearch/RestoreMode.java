package de.wwu.muli.iteratorsearch;

public enum RestoreMode {
    /**
     * During restore, elements are only removed from the (regular) trail stack.
     */
    SimpleRestore,
    /**
     * During restore from trail, the elements' respective inverses are added to the inverse trail.
     */
    TrailToInverse,
    /**
     * During restore from the inverse trail, the respective inverses are added back to the regular trail stack.
     */
    InverseToTrail,
}
