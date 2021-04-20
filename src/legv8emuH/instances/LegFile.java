package legv8emuH.instances;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import legv8emuH.global.InstructionProcessor;

public class LegFile {
	
	byte[] bytes;
	int offset;

	/**
	 * Provides a container for easier access of instructions
	 * @param pathname the path to the assembled LEGv8 binary
	 */
	public LegFile(String pathname) {
		offset = 0;
		try {
			bytes = Files.readAllBytes(Paths.get(pathname));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @return the current 4 byte instruction
	 */
	public byte[] getCurrentInstruction() {
		if (4 * offset + 3 >= bytes.length) return null;
		byte[] ret = new byte[4];
		for (int i = 0; i < 4; i++) ret[i] = bytes[4*offset + i];
		return ret;
	}
	
	/**
	 * For all branch instructions other than BR.
	 * @param ps the current register file, stack, and heap
	 * @param distance the distance to branch
	 */
	public void branch(ProcessorState ps, int distance) {
		int query = offset + distance;
		queryOffset(ps, query);
		offset = query;
	}
	
	/**
	 * @return the current instruction number (0-indexed)
	 */
	public long getCurrentOffset() {
		return offset;
	}
	
	/**
	 * For BR instructions.
	 * @param ps the current register file, stack, and heap
	 * @param target the offset to branch to
	 */
	public void setCurrentOffset(ProcessorState ps, long target) {
		int query = (int) target;
		queryOffset(ps, query);
		offset = query;
	}
	
	private void queryOffset(ProcessorState ps, long query) {
		if (query < 0)
			InstructionProcessor.execHALT(ps, "Bad branch to negative PC.", offset);
		if (4 * query > bytes.length) // Equals implies EOF, which is not an error and is handled elsewhere
			InstructionProcessor.execHALT(ps, "Bad branch to PC beyond end of file.", offset);
	}
	
}
