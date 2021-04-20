package legv8emuH.global;

public enum Opcode {

	B			("000101"),
	B_COND		("01010100"),
	
	AND			("10001010000"),
	ADD			("10001011000"),
	ADDI		("1001000100"),
	ANDI		("1001001000"),
	BL			("100101"),
	
	ORR			("10101010000"),
	ORRI		("1011001000"),
	CBZ			("10110100"),
	CBNZ		("10110101"),
	
	EOR			("11001010000"),
	SUB			("11001011000"),
	SUBI		("1101000100"),
	EORI		("1101001000"),
	LSR			("11010011010"),
	LSL			("11010011011"),
	BR			("11010110000"),
	
	SUBS		("11101011000"),
	SUBIS		("1111000100"),
	STUR		("11111000000"),
	LDUR		("11111000010"),
	
	PRNL		("11111111100"),
	PRNT		("11111111101"),
	DUMP		("11111111110"),
	HALT		("11111111111"),
	
	UNKNOWN		("???????????"),
	
	;
	private final String prefix;
	Opcode(String a) {
		prefix = a;
	}
	/**
	 * @return the prefix associated with the Opcode
	 */
	public String getPrefix() {
		return prefix;
	}
	
}
