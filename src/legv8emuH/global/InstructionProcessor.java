package legv8emuH.global;

import static legv8emuH.global.HelperFunctions.*;

import legv8emuH.instances.Instruction;
import legv8emuH.instances.LegFile;
import legv8emuH.instances.ProcessorState;

public class InstructionProcessor {

	/**
	 * Decodes and then executes the current instruction.
	 * 
	 * @param assembly the file containing all instructions
	 * @param ps the current state of registers and memory
	 * @param inst the instruction currently being executed
	 * @return the number of instructions to branch
	 */
	public static int decodeAndExecute(LegFile assembly, ProcessorState ps, Instruction inst) {
		Opcode opcode = inst.getOpcode();
		
		int rD, rT, rM, rN, shamt;
		rD = rT = inst.getRegD();
		rM = inst.getRegM();
		rN = inst.getRegN();
		shamt = inst.getShift();
		
		int aluImm, bImm, cbImm, cond, dtImm, op2;
		aluImm = inst.getAluImm();
		bImm = inst.getBranch();
		cbImm = inst.getCondBranch();
		cond = rT & 0xf;
		dtImm = inst.getDtAddr();
		op2 = inst.getSecondOp();
		
		switch (opcode) {
		case ADD:
			return execADD(ps, rM, shamt, rN, rD);
		case ADDI:
			return execADDI(ps, aluImm, rN, rD);
		case AND:
			return execAND(ps, rM, shamt, rN, rD);
		case ANDI:
			return execANDI(ps, aluImm, rN, rD);
		case B:
			return execB(bImm);
		case BL:
			return execBL(assembly, ps, bImm);
		case BR:
			return execBR(assembly, ps, rN);
		case B_COND:
			return execBcond(ps, cbImm, cond);
		case CBNZ:
			return execCBNZ(ps, cbImm, rT);
		case CBZ:
			return execCBZ(ps, cbImm, rT);
		case DUMP:
			return execDUMP(ps);
		case EOR:
			return execEOR(ps, rM, shamt, rN, rD);
		case EORI:
			return execEORI(ps, aluImm, rN, rD);
		case HALT:
			return execHALT(ps, "Encountered HALT instruction.", assembly.getCurrentOffset());
		case LDUR:
			return execLDUR(assembly, ps, dtImm, op2, rN, rT);
		case LSL:
			return execLSL(ps, shamt, rN, rD);
		case LSR:
			return execLSR(ps, shamt, rN, rD);
		case ORR:
			return execORR(ps, rM, shamt, rN, rD);
		case ORRI:
			return execORRI(ps, aluImm, rN, rD);
		case PRNL:
			return execPRNL();
		case PRNT:
			return execPRNT(ps, rD);
		case STUR:
			return execSTUR(assembly, ps, dtImm, op2, rN, rT);
		case SUB:
			return execSUB(ps, rM, shamt, rN, rD);
		case SUBI:
			return execSUBI(ps, aluImm, rN, rD);
		case SUBIS:
			return execSUBIS(ps, aluImm, rN, rD);
		case SUBS:
			return execSUBS(ps, rM, shamt, rN, rD);
		case UNKNOWN:
		default:
			return execHALT(ps, "Encountered unimplemented opcode: "
					+ bigEndianToBinString(assembly.getCurrentInstruction()), assembly.getCurrentOffset());
		
		}
	}
	
	// ===================================//
	
	private static int execADD(ProcessorState ps, int rM, int shamt, int rN, int rD) {
		long value = ps.getValueOfRegister(rN);
		value += (ps.getValueOfRegister(rM) << shamt);
		ps.setValueOfRegister(rD, value);
		
		return 1;
	}
	private static int execADDI(ProcessorState ps, int imm, int rN, int rD) {
		long value = ps.getValueOfRegister(rN);
		value += imm;
		ps.setValueOfRegister(rD, value);
		
		return 1;
	}
	private static int execAND(ProcessorState ps, int rM, int shamt, int rN, int rD) {
		long value = ps.getValueOfRegister(rN);
		value &= (ps.getValueOfRegister(rM) << shamt);
		ps.setValueOfRegister(rD, value);
		
		return 1;
	}
	private static int execANDI(ProcessorState ps, int imm, int rN, int rD) {
		long value = ps.getValueOfRegister(rN);
		value &= imm;
		ps.setValueOfRegister(rD, value);
		
		return 1;
	}
	
	private static int execB(int imm) {
		return imm;
	}
	private static int execBcond(ProcessorState ps, int imm, int cond) {
		boolean jump = false;
		cond %= 16;
		boolean[] nzcv = ps.getValueOfNZCV();
		
		/*switch (cond) {
		case 0: if (nzcv[1]) jump = true; break;
		case 1: if (!nzcv[1]) jump = true; break;
		case 2: if (nzcv[2]) jump = true; break;
		case 3: if (!nzcv[2]) jump = true; break;
		case 4: if (nzcv[0]) jump = true; break;
		case 5: if (!nzcv[0]) jump = true; break;
		case 6: if (nzcv[3]) jump = true; break;
		case 7: if (!nzcv[3]) jump = true; break;
		
		case 8: if (nzcv[2] && !nzcv[1]) jump = true; break;
		case 9: if (!(nzcv[2] && !nzcv[1])) jump = true; break;
		case 10: if (nzcv[0] == nzcv[3]) jump = true; break;
		case 11: if (nzcv[0] != nzcv[3]) jump = true; break;
		case 12: if (!nzcv[1] && (nzcv[0] == nzcv[3])) jump = true; break;
		case 13: if (nzcv[1] || (nzcv[0] != nzcv[3])) jump = true; break;
		case 14: case 15: jump = true; break;
		}*/
		
		switch (cond) {
		case 0: if (nzcv[1]) jump = true; break; // EQ; S == 0
		case 10: case 2: case 5: if (!nzcv[0]) jump = true; // GE, HS, PL; S >= 0
		case 12: case 8: if (!nzcv[0] && !nzcv[1]) jump = true; break; // GT, HI; S > 0
		case 13: case 9: if (nzcv[0] || nzcv[1]) jump = true; break; // LE, LS; S <= 0
		case 3: case 11: case 4: if (nzcv[0]) jump = true; break; // LO, LT, MI; S < 0
		case 1: if (!nzcv[1]) jump = true; break; // NE; S != 0
		case 6: case 7: break; // VS, VC; 0
		case 14: case 15: jump = true; break; // AL, NV; 1
		}
		
		if (jump) {
			return imm;
		}
		return 1;
	}
	private static int execBL(LegFile assembly, ProcessorState ps, int imm) {
		long toStore = assembly.getCurrentOffset();
		ps.setValueOfRegister(30, toStore);
		return imm;
	}
	private static int execBR(LegFile assembly, ProcessorState ps, int rN) {
		long toGoto = ps.getValueOfRegister(rN);
		assembly.setCurrentOffset(ps, toGoto);
		return 1;
	}
	private static int execCBNZ(ProcessorState ps, int imm, int rT) {
		if (ps.getValueOfRegister(rT) != 0)
			return imm;
		return 1;
	}
	private static int execCBZ(ProcessorState ps, int imm, int rT) {
		if (ps.getValueOfRegister(rT) == 0)
			return imm;
		return 1;
	}
	
	private static int execEOR(ProcessorState ps, int rM, int shamt, int rN, int rD) {
		long value = ps.getValueOfRegister(rN);
		value ^= (ps.getValueOfRegister(rM) << shamt);
		ps.setValueOfRegister(rD, value);
		
		return 1;
	}
	private static int execEORI(ProcessorState ps, int imm, int rN, int rD) {
		long value = ps.getValueOfRegister(rN);
		value ^= imm;
		ps.setValueOfRegister(rD, value);
		
		return 1;
	}
	private static int execLDUR(LegFile assembly, ProcessorState ps, int imm, int op, int rN, int rT) {
		if (op != 0) return 1;
		
		byte[] accessMemory = ps.getHeapMemory();
		if ((rN == 28) || (rN == 29)) accessMemory = ps.getStackMemory();
		int accessPoint = (int) ps.getValueOfRegister(rN);
		accessPoint += imm;
		
		try {
			byte[] substructure = new byte[8];
			for (int i = 0; i < 8; i++)
				substructure[i] = accessMemory[accessPoint + i];
			ps.setValueOfRegister(rT, binStringToLong(bigEndianToBinString(substructure)));
		} catch (ArrayIndexOutOfBoundsException e) {
			if ((rN == 28) || (rN == 29)) {
				if (accessPoint < 0)
					execHALT(ps, "Stack overflow (read).", assembly.getCurrentOffset());
				else
					execHALT(ps, "Stack underflow (read)", assembly.getCurrentOffset());
			} else
				execHALT(ps, String.format("Out of bounds heap memory read: %08x", accessPoint),
						assembly.getCurrentOffset());
		}
		
		return 1;
	}
	private static int execLSL(ProcessorState ps, int shamt, int rN, int rD) {
		long value = ps.getValueOfRegister(rN);
		value <<= shamt;
		ps.setValueOfRegister(rD, value);
		
		return 1;
	}
	private static int execLSR(ProcessorState ps, int shamt, int rN, int rD) {
		long value = ps.getValueOfRegister(rN);
		value >>>= shamt;
		ps.setValueOfRegister(rD, value);
		
		return 1;
	}
	private static int execORR(ProcessorState ps, int rM, int shamt, int rN, int rD) {
		long value = ps.getValueOfRegister(rN);
		value |= (ps.getValueOfRegister(rM) << shamt);
		ps.setValueOfRegister(rD, value);
		
		return 1;
	}
	private static int execORRI(ProcessorState ps, int imm, int rN, int rD) {
		long value = ps.getValueOfRegister(rN);
		value |= imm;
		ps.setValueOfRegister(rD, value);
		
		return 1;
	}
	private static int execSTUR(LegFile assembly, ProcessorState ps, int imm, int op, int rN, int rT) {
		if (op != 0) return 1;
		
		byte[] accessMemory = ps.getHeapMemory();
		if ((rN == 28) || (rN == 29)) accessMemory = ps.getStackMemory();
		int accessPoint = (int) ps.getValueOfRegister(rN);
		accessPoint += imm;
		
		try {
			long value = ps.getValueOfRegister(rT);
			byte[] substructure = binStringToBigEndian(longToBinString(value));
			for (int i = 0; i < 8; i++)
				accessMemory[accessPoint + i] = substructure[i];
		} catch (ArrayIndexOutOfBoundsException e) {
			if ((rN == 28) || (rN == 29)) {
				if (accessPoint < 0)
					execHALT(ps, "Stack overflow (write).", assembly.getCurrentOffset());
				else
					execHALT(ps, "Stack underflow (write)", assembly.getCurrentOffset());
			} else
				execHALT(ps, String.format("Out of bounds heap memory write: %08x", accessPoint),
						assembly.getCurrentOffset());
		}
		
		return 1;
	}
	
	private static int execSUB(ProcessorState ps, int rM, int shamt, int rN, int rD) {
		long value = ps.getValueOfRegister(rN);
		value -= (ps.getValueOfRegister(rM) << shamt);
		ps.setValueOfRegister(rD, value);
		
		return 1;
	}
	private static int execSUBI(ProcessorState ps, int imm, int rN, int rD) {
		long value = ps.getValueOfRegister(rN);
		value -= imm;
		ps.setValueOfRegister(rD, value);
		
		return 1;
	}
	private static int execSUBIS(ProcessorState ps, int imm, int rN, int rD) {
		long value = ps.getValueOfRegister(rN);
		long income1 = value;
		value -= imm;
		ps.setValueOfRegister(rD, value);
		ps.setValueOfNZCV(income1, value);
		
		return 1;
	}
	private static int execSUBS(ProcessorState ps, int rM, int shamt, int rN, int rD) {
		long value = ps.getValueOfRegister(rN);
		long income1 = value;
		value -= (ps.getValueOfRegister(rM) << shamt);
		ps.setValueOfRegister(rD, value);
		ps.setValueOfNZCV(income1, value);
		
		return 1;
		
	}
	
	private static int execDUMP(ProcessorState ps) {
		execPRNL();
		System.out.println("Registers:");
		for (int i = 0; i < 32; i++) execPRNT(ps, i);
		
		execPRNL();
		byte[] heap = ps.getHeapMemory();
		System.out.println("Heap:");
		execDumpHelper(heap);
		System.out.printf("Total heap size: 0x%08x%n", heap.length);
		
		execPRNL();
		byte[] stack = ps.getStackMemory();
		System.out.println("Stack:");
		execDumpHelper(stack);
		System.out.printf("Total stack size: 0x%08x%n", stack.length);
		
		return 1;
	}
	private static void execDumpHelper(byte[] memory) {
		for (int i = 0; i < memory.length; i++) {
			if (i % 16 == 0)
				System.out.printf("%08x", i);
			if (i % 8 == 0)
				System.out.print(' ');
			
			System.out.printf(" %02x", memory[i]);
			
			if ((i % 16 == 15) || (i == memory.length - 1))
				execPRNL();
		}
	}
	public static int execHALT(ProcessorState ps, String reason, long location) {
		execDUMP(ps);
		execPRNL();
		System.out.println("Reason for halting: " + reason);
		System.out.println("Instruction address of halt: " + location);
		Runtime.getRuntime().halt(0);
		return 1;
	}
	private static int execPRNL() {
		System.out.println();
		return 1;
	}
	private static int execPRNT(ProcessorState ps, int rD) {
		validateRegister(rD);
		switch (rD) {
		case 28: System.out.print("(SP) X28"); break;
		case 29: System.out.print("(FP) X29"); break;
		case 30: System.out.print("(LR) X30"); break;
		case 31: System.out.print("     XZR"); break;
		default:
			if (rD < 10)
				 System.out.print("      X" + rD);
			else
				 System.out.print("     X" + rD);
		}
		long value = ps.getValueOfRegister(rD);
		String bin = longToBinString(value);
		String hex = binStringToHexString(bin);
		System.out.println(": " + bin + " 0x" + hex + " (" + value + ")");
		
		return 1;
	}
		
}
