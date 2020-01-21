package de.wwu.muli.iteratorsearch.structures;

import de.wwu.muggl.symbolic.searchAlgorithms.choice.ChoicePoint;
import de.wwu.muggl.symbolic.searchAlgorithms.depthFirst.StackWithTrail;
import de.wwu.muggl.symbolic.searchAlgorithms.depthFirst.trailelements.Pop;
import de.wwu.muggl.symbolic.searchAlgorithms.depthFirst.trailelements.Push;
import de.wwu.muggl.symbolic.searchAlgorithms.depthFirst.trailelements.VmPop;
import de.wwu.muggl.symbolic.searchAlgorithms.depthFirst.trailelements.VmPush;
import de.wwu.muli.vm.LogicVirtualMachine;

import java.util.Stack;
import java.util.function.Supplier;

/**
 * The StackToTrailWithInverse extends the java.util.Stack. Is overrides the functionality for push and pop. In
 * general, it will invoke the super method whenever the methods push() and pop() are invoked. If it
 * is not set to restoring mode, it will also add information to the trail of the current
 * ChoicePoint.<br />
 * <br />
 * The trail can be used to track back to a former state of the execution. Hence, when pushing an
 * item the command to pop will be added to the trail, and when popping an item the command to push
 * it will be added to the trail.
 *
 * @author Jan C. Dageförde
 * @version 1.0.0, 2010-03-10
 */
public class StackToTrailWithInverse extends Stack<Object> implements StackWithTrail {
	private static final long serialVersionUID = 2507257891851151819L;
	// Fields.
	private boolean isVmStack;
    private boolean restoringMode;
    private final LogicVirtualMachine vm;

    /**
     * Initialize a new StackToTrailWithInverse.
     * @param isVmStack If set to true, this StackToTrailWithInverse should be used as a virtual machine stack. It should be used as a operand stack otherwise.
     */
    public StackToTrailWithInverse(boolean isVmStack, LogicVirtualMachine vm) {
        super();
        this.isVmStack = isVmStack;
        this.vm = vm;
        this.restoringMode = false;
    }

	/**
	 * Push an item onto the stack. If there is a ChoicePoint set and this StackToTrailWithInverse
	 * is not in restoring mode, add the command to pop to the trail.
	 * @param item The item to push onto the stack.
	 * @return The supplied item.
	 */
	@Override
	public Object push(Object item) {
		if (!this.restoringMode) {
            // New (ST) choice structure:
            if (this.isVmStack) {
                vm.addToTrail(new VmPop());
            } else {
                vm.addToTrail(new Pop());
            }
		}

		return super.push(item);
	}

	/**
	 * Pop an item from the stack. If there is a ChoicePoint set and this StackToTrailWithInverse
	 * is not in restoring mode, add the command to push this item to the trail.
	 * @return The popped item.
	 */
	@Override
	public synchronized Object pop() {
		Object item = super.pop();
		if (!this.restoringMode) {
            // New (ST) choice structure:
            if (this.isVmStack) {
                vm.addToTrail(new VmPush(item));
            } else {
                vm.addToTrail(new Push(item));
            }
		}

		return item;
	}

    /**
     * Setter for the restoring mode.
     * @param restoringMode true enables restoring mode, false disables it.
     */
    public void setRestoringMode(boolean restoringMode) {
        this.restoringMode = restoringMode;
    }

    /**
     * Getter for the restoring mode.
     */
    public boolean getRestoringMode() {
        return this.restoringMode;
    }

	/**
	 * Indicates whether some other object is equal to this one.
	 *
	 * @param obj The object to check equality with.
	 * @return true, if the two value fields for the StackToTrailWithInverse are equal and if the inherited stack
	 *         is equal; false otherwise.
	 * @see java.util.Vector#equals(java.lang.Object)
	 */
	@Override
	public synchronized boolean equals(Object obj) {
		if (obj instanceof StackToTrailWithInverse) {
			StackToTrailWithInverse stack = (StackToTrailWithInverse) obj;
			if (stack.isVmStack == this.isVmStack && stack.restoringMode == this.restoringMode && stack.vm == this.vm) {
				return super.equals(obj);
			}
		}
		return false;
	}
	
	/**
	 * Returns the hash code value for this stack.
	 * 
	 * @return The hash code value for this stack.
	 * @see java.util.Vector#hashCode()
	 */
	@Override
	public synchronized int hashCode() {
		return super.hashCode();
	}

}
