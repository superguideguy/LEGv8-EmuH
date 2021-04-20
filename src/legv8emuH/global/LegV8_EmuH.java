package legv8emuH.global;

import legv8emuH.instances.Instruction;
import legv8emuH.instances.LegFile;
import legv8emuH.instances.ProcessorState;

public class LegV8_EmuH {

	/**
	 * Emulates an assembled LEGv8 file.
	 * @param args a file path to the program to emulate, optionally followed by the heap and stack sizes
	 */
	public static void main(String[] args) {
		// Parse args[0] as a LEGv8 assembly file.
		LegFile assembly = new LegFile(args[0]);
		byte[] instructionBytes = assembly.getCurrentInstruction();
		
		int stackSize = 512;
		int heapSize = 4096;
		if (args.length >= 2)
			heapSize = Integer.parseInt(args[1]);
		if (args.length >= 3)
			stackSize = Integer.parseInt(args[2]);
			
		ProcessorState ps = new ProcessorState(heapSize, stackSize);
		
		while (instructionBytes != null) {
			Instruction inst = new Instruction(instructionBytes);
			int branchDistance = InstructionProcessor.decodeAndExecute(assembly, ps, inst);
			assembly.branch(ps, branchDistance);
			instructionBytes = assembly.getCurrentInstruction();
		}
		
		InstructionProcessor.execHALT(ps, "Encountered end of file.", + assembly.getCurrentOffset());
	}
	
}
