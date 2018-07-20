package de.wwu.muli.iteratorsearch.structures;

import de.wwu.muggl.configuration.MugglException;
import de.wwu.muggl.solvers.expressions.ConstraintExpression;
import de.wwu.muggl.symbolic.searchAlgorithms.choice.ChoicePoint;
import de.wwu.muggl.symbolic.searchAlgorithms.depthFirst.trailelements.TrailElement;
import de.wwu.muggl.vm.Frame;

import java.util.Stack;

public class RootChoicePoint implements ChoicePoint {
    private final ChoicePoint parent;
    private final int pc;
    private final Frame frame;
    private Stack<TrailElement> trail = new Stack<>();
    private Stack<TrailElement> inverseTrail = new Stack<>();
    /**
     * Next available ID number.
     */
    static int nextIdNumber = 0;
    /**
     * This choicepoint's ID.
     */
    final int idNumber;

    /**
     * Create the initial Choicepoint that represents the root of a search region.
     *
     * @param frame The currently executed Frame.
     * @param pc The pc of the instruction that generates the Choicepoint.
     */
    public RootChoicePoint(Frame frame, int pc, ChoicePoint parent) {
        // Possible exceptions.
        if (frame == null) throw new NullPointerException("The Frame must not be null.");
        if (parent != null) throw new IllegalStateException("This is a root choice point; it strictly expects to NOT have a parent!");

        // Set the fields.
        this.parent = parent;
        this.frame = frame;
        this.pc = pc; // This CP is created by a specific invokestatic call with a fixed length. pc is expected to continue after this one.

        // Graph visualisation.
        this.idNumber = nextIdNumber++;
    }

    @Override
    public String getID() {
        return this.getChoicePointType() + "_" + this.idNumber;
    }

    @Override
    public Frame getFrame() {
        return this.frame;
    }

    @Override
    public int getPc() {
        return this.pc;
    }

    @Override
    public int getPcNext() {
        // Before proceeding, make sure, that after subsequent backtracking execution continues in the part where a solution is wrapped:
        return 43; // TODO make dynamic. Needs to be the instruction immediately after the one that calls SearchRegion#get().
    }

    @Override
    public ChoicePoint getParent() {
        return this.parent;
    }

    /**
     * Find out if this ChoicePoint has a trail.
     *
     * @return true.
     */
    public boolean hasTrail() {
        return true;
    }

    /**
     * Add an object to the trail of this ChoicePoint.
     * @param element The TrailElement to be added to the trail.
     */
    public void addToTrail(TrailElement element) {
        this.trail.push(element);
    }

    /**
     * Add an object to the inverse trail of this ChoicePoint.
     * @param element The TrailElement to be added to the trail.
     */
    public void addToInverseTrail(TrailElement element) {
        this.inverseTrail.push(element);
    }

    /**
     * Getter for the trail.
     * @return The trail.
     */
    public Stack<TrailElement> getTrail() {
        return this.trail;
    }

    /**
     * Getter for the inverse trail.
     * @return The trail.
     */
    public Stack<TrailElement> getInverseTrail() {
        return this.inverseTrail;
    }

    @Override
    public long getNumber() {
        return 0;
    }

    @Override
    public boolean hasAnotherChoice() {
        return false;
    }

    @Override
    public void changeToNextChoice(){ }

    @Override
    public boolean changesTheConstraintSystem() {
        return false;
    }

    @Override
    public ConstraintExpression getConstraintExpression() {
        return null;
    }

    @Override
    public void setConstraintExpression(ConstraintExpression constraintExpression) { }

    @Override
    public boolean enforcesStateChanges() {
        return false;
    }

    @Override
    public void applyStateChanges() { }

    @Override
    public String getChoicePointType() {
        return RootChoicePoint.class.getSimpleName();
    }
}
