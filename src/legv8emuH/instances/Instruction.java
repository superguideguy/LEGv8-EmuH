package legv8emuH.instances;
import static legv8emuH.global.HelperFunctions.*;

import legv8emuH.global.Opcode;

public class Instruction {

	String instruction;
	
	public Instruction(byte[] bytes) {
		instruction = bigEndianToBinString(bytes);
	}
	
	// Special
	
	/**
	 * Returns the Opcode with the correct prefix, or UNKNOWN if no such opcode exists.
	 * @return the Opcode with the same prefix
	 */
	public Opcode getOpcode() {
		for (Opcode o : Opcode.values())
			if (instruction.startsWith(o.getPrefix()))
				return o;
		return Opcode.UNKNOWN;
	}
	
	private int getImmediate(int offset, int length) {
		String sImm = instruction.substring(offset, offset + length);
		return (int) (binStringToLong(sImm) & 0x7fff_ffff);
	}
	
	// R type
	
	private int getRegister(int offset) {
		int val = getImmediate(offset, 5);
		return (val & 0x1f);
	}

	/**
	 * @return the register number of register M
	 */
	public int getRegM() {
		return getRegister(11);
	}
	
	/**
	 * @return the shift amount used for ALU shifted register instructions
	 */
	public int getShift() {
		return (getImmediate(16, 6) & 0x3f);
	}
	
	/**
	 * @return the register number of register N
	 */
	public int getRegN() {
		return getRegister(22);
	}
	
	/**
	 * @return the register number of registers D and T
	 */
	public int getRegD() {
		return getRegister(27);
	}
	
	// I type and D type
	
	/**
	 * @return the immediate used in ALU immediate instructions
	 */
	public int getAluImm() {
		return (getImmediate(10, 12) & 0xfff); // Always unsigned
	}
	
	/**
	 * @return the immediate used in load/store instructions
	 */
	public int getDtAddr() {
		int immediate = (getImmediate(11, 9) & 0x1ff);
		if (immediate >= 256) immediate -= 512;
		return immediate;
	}
	
	/**
	 * Returns the secondary operation for load/store instructions.
	 * For the subset of ARM implemented, this should always be zero.
	 * @return the secondary load/store operation
	 */
	public int getSecondOp() {
		return (getImmediate(20, 2) & 0x3);
	}
	
	// (C)B type and IW type
	
	/**
	 * @return the immediate used by B and BL instructions
	 */
	public int getBranch() {
		int immediate = (getImmediate(6, 26) & 0x3ff_ffff);
		if (immediate >= 0x200_0000) immediate -= 0x400_0000;
		//immediate <<= 2; // Program Counter is separate, and thus shift unneeded
		return immediate;
	}
	
	/**
	 * @return the immediate used by B.cond, CBZ, and CBNZ instructions
	 */
	public int getCondBranch() {
		int immediate = (getImmediate(8, 19) & 0x7_ffff);
		if (immediate >= 0x4_0000) immediate -= 0x8_0000;
		//immediate <<= 2; // Program Counter is separate, and thus shift unneeded
		return immediate;
	}
	
	/**
	 * Unused.
	 * @return the immediate used by MOVK and MOVW instructions
	 */
	public int getMovImm() {
		return (getImmediate(11, 16) & 0xffff); // Unused
	}
	
}
