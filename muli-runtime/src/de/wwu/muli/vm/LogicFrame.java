package de.wwu.muli.vm;

import de.wwu.muggl.symbolic.searchAlgorithms.choice.ChoicePoint;
import de.wwu.muggl.symbolic.searchAlgorithms.depthFirst.trailelements.PopFromFrame;
import de.wwu.muggl.symbolic.structures.Loop;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.classfile.structures.Constant;
import de.wwu.muggl.vm.classfile.structures.Method;
import de.wwu.muggl.vm.execution.ExecutionException;
import de.wwu.muli.search.dfs.StackToTrail;

import java.util.ArrayList;

/**
 * The LogicFrame inherits the functionality of a "normal" Frame. It also offers some
 * features needed for logic execution.
 * 
 * At time of creation this is rather similar to a SymbolicFrame, but interoperates with
 * LogicVirtualMachine VMs instead of SymbolicVirtualMachine VMs. Furthermore, coverage
 * tracking stuff is omitted as it is irrelevant to logic execution.
 *
 * @author Jan C. Dageförde
 * @version 1.0.0, 2016-11-14
 */
public class LogicFrame extends Frame {

	// New fields.
	private boolean loopsHaveBeenChecked;
	private ArrayList<Loop> loops;
	private boolean executionFinishedNormally;

	/**
	 * Constructor that simply invokes the super constructor, before initializing the class'
	 * own fields.
	 * @param invokedBy The frame this frame was invoked by. Might be null.
	 * @param vm The symbolic virtual machine this frame can be executed on.
	 * @param method The Method this frame represents.
	 * @param constantPool A reference to the constant pool of the methods class.
	 * @param arguments The predefined arguments for this method.
	 * @throws ExecutionException Thrown on any fatal error that happens during execution and is not coped by one of the other Exceptions.
	 */
	public LogicFrame(Frame invokedBy, LogicVirtualMachine vm, Method method,
			Constant[] constantPool, Object[] arguments) throws ExecutionException {
		super(invokedBy, vm, method, constantPool, arguments);

		// Initialize the fields of this class.
		this.loopsHaveBeenChecked = false;
		this.loops = new ArrayList<Loop>();
		this.executionFinishedNormally = false;
	}

	/**
	 * Getter for the loops ArrayList.
	 * @return The ArrayList of loops.
	 */
	public ArrayList<Loop> getLoops() {
		return this.loops;
	}

	/**
	 * Getter for loopsHaveBeenChecked.
	 * @return true, if the loops have been checked, false otherwise.
	 */
	public boolean getLoopsHaveBeenChecked() {
		return this.loopsHaveBeenChecked;
	}

	/**
	 * Set loopsHaveBeenChecked to true. Setting it back to false is not possible.
	 */
	public void setLoopsHaveBeenChecked() {
		this.loopsHaveBeenChecked = true;
	}

	/**
	 * Mark that the execution finished normally and invoke the super implementation.
	 */
	@Override
	public void returnFromMethod() {
		this.executionFinishedNormally = true;
		super.returnFromMethod();
	}

	/**
	 * Mark that the execution finished normally and pushes a returned object onto the stack of the
	 * invoking frame, or if there was none onto the virtual machines stack and sets the frame
	 * inactive. Clears this frames' operand stack afterwards.
	 *
	 * @param value The value returned from the method.
	 */
	@Override
	public void returnFromMethod(Object value) {
		this.executionFinishedNormally = true;
		if (this.invokedBy != null) {
			StackToTrail operandStack = (StackToTrail) this.invokedBy.getOperandStack();
			// Enable restoring mode to the pushed item will not be added as a pop trail element to the trail.
			operandStack.setRestoringMode(true);
			// Push the return value.
			operandStack.push(value);
			// Enable restoring mode.
			operandStack.setRestoringMode(false);
			// Add this item manually to the trail.
			ChoicePoint choicePoint = ((LogicVirtualMachine) this.vm).getCurrentChoicePoint();
			if (choicePoint != null && choicePoint.hasTrail())
				choicePoint.addToTrail(new PopFromFrame(this.invokedBy));
		} else {
			this.vm.getStack().push(value);
		}
		while (!this.operandStack.isEmpty()) {
			this.operandStack.pop();
		}
		this.active = false;
	}

	/**
	 * Find out if execution in this frame was finished by the execution of a return instruction.
	 * @return true if execution in this frame was finished normally, false otherwise.
	 */
	public boolean hasExecutionFinishedNormally() {
		return this.executionFinishedNormally;
	}

	/**
	 * Reset the execution status of this symbolic frame. When tracking back, the frame must no longer
	 * be marked as a frame that has normally finished its execution (i.e. its return method has been
	 * executed.)
	 */
	public void resetExecutionFinishedNormally() {
		this.executionFinishedNormally = false;
	}

}
