package legv8emuH.instances;
import static legv8emuH.global.HelperFunctions.*;

//import java.util.Arrays;

public class ProcessorState {
	
	byte[][] registers;
	boolean[] registerNZCV;
	
	byte[] heapMemory;
	byte[] stackMemory;
	
	/**
	 * Creates a register file, stack, and heap. Sets SP and FP to size of stack.
	 * @param heapSize the size of the heap
	 * @param stackSize the size of the stack
	 */
	public ProcessorState(int heapSize, int stackSize) {
		registers = new byte[31][8];
		registerNZCV = new boolean[4];
		
		heapMemory = new byte[heapSize];
		stackMemory = new byte[stackSize];
		// set SP (X28) to stack size
		setValueOfRegister(28, stackMemory.length);
		// set FP (X29) to stack size
		setValueOfRegister(29, stackMemory.length);
	}
	
	/**
	 * @param register the register number to access
	 * @return the value currently in register
	 */
	public long getValueOfRegister(int register) {
		if (!validateRegister(register)) return 0;
		return binStringToLong(bigEndianToBinString(registers[register]));
	}
	
	/**
	 * @param register the register number to access
	 * @param value the value to place in register
	 */
	public void setValueOfRegister(int register, long value) {
		if (!validateRegister(register)) return;
		registers[register] = binStringToBigEndian(longToBinString(value));
		//System.err.println(Arrays.toString(registers[register]));
	}
	
	/**
	 * @return the values currently in the NZCV register
	 */
	public boolean[] getValueOfNZCV() {
		return registerNZCV;
	}
	
	/**
	 * Sets the NZCV register based on a SUBS or SUBIS instruction.
	 * @param income1 the minuend (first parameter) of SUBS or SUBIS
	 * @param outcome the difference of SUBS or SUBIS
	 */
	public void setValueOfNZCV(long income1, long outcome) {
		// Works only for subtraction
		if (outcome < 0)
			registerNZCV[0] = true;
		else
			registerNZCV[0] = false;
		
		if (outcome == 0)
			registerNZCV[1] = true;
		else
			registerNZCV[1] = false;
		
		boolean setC = false;
		if (income1 >= 0) {
			if (outcome < 0) setC = true;
			if (outcome > income1) setC = true;
		} else {
			if (outcome < 0) if (outcome > income1) setC = true;
		}
		registerNZCV[2] = setC;
		
		if (outcome > income1)
			registerNZCV[3] = true;
		else
			registerNZCV[3] = false;
	}
	
	/**
	 * @return the pointer to heap memory
	 */
	public byte[] getHeapMemory() {
		return heapMemory;
	}
	
	/**
	 * @return the pointer to stack memory
	 */
	public byte[] getStackMemory() {
		return stackMemory;
	}

}
