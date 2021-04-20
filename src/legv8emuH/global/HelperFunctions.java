package legv8emuH.global;

public class HelperFunctions {

	//=============================================================//
	// Conversions to String //====================================//
	//=============================================================//
	
	public static String bigEndianToBinString(byte[] bytes) {
		StringBuilder sb = new StringBuilder(8*bytes.length);
		for (int i = 0; i < bytes.length; i++) for (int j = 7; j >= 0; j--) {
			if (((bytes[i] >>> j) & 1) == 1)
				sb.append('1');
			else
				sb.append('0');
		}
		return sb.toString();
	}
	
	public static String longToBinString(long l) {
		StringBuilder sb1 = new StringBuilder(Long.toUnsignedString(l, 2));
		StringBuilder sb2 = new StringBuilder(64);
		for (int i = sb1.length(); i < 64; i++) {
			sb2.append('0');
		}
		sb2.append(sb1.toString());
		return sb2.toString();
	}
	
	public static String intToHexString(int k) {
		StringBuilder sb1 = new StringBuilder(Long.toUnsignedString(k, 16));
		StringBuilder sb2 = new StringBuilder(8);
		for (int i = sb1.length(); i < 8; i++) {
			sb2.append('0');
		}
		sb2.append(sb1.toString());
		return sb2.toString();
	}
	
	//=============================================================//
	// Conversions from String //==================================//
	//=============================================================//
	
	public static byte[] binStringToBigEndian(String s) {
		byte[] ret = (s.length() % 8 == 0) ? new byte[s.length() / 8] : new byte[(s.length() / 8) + 1];
		for (int i = 0; 8 * i < s.length(); i++) {
			ret[i] = (byte) Short.parseShort(s.substring(8 * i, 8 * i + 8), 2);
			// Byte parser will complain > 127
		}
		return ret;
	}

	public static long binStringToLong(String s) {
		return Long.parseUnsignedLong(s, 2);
	}
	
	public static String binStringToHexString(String binString) {
		long temp = binStringToLong(binString);
		StringBuilder sb1 = new StringBuilder(Long.toUnsignedString(temp, 16));
		StringBuilder sb2 = new StringBuilder(16);
		for (int i = sb1.length(); i < 16; i++) {
			sb2.append('0');
		}
		sb2.append(sb1.toString());
		return sb2.toString();
	}
	
	//=============================================================//
	// Other functions //==========================================//
	//=============================================================//
	
	/**
	 * Confirms that a register number is correct.
	 * @param register the register number to validate
	 * @return true if register is 0-30, false if 31 (XZR)
	 * @throws IllegalArgumentException if outside the range 0-31 (inclusive)
	 */
	public static boolean validateRegister(int register) {
		if (register == 31) return false;
		if ((register > 31) || (register < 0))
				throw new IllegalArgumentException("Invalid Register: " + register);
		return true;
	}
	
}
