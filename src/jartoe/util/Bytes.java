package jartoe.util;

/**
 * @author Artoe
 */
public final class Bytes {
	public static byte signedByte(byte[] data, int offset) {
		return data[offset];
	}

	public static int signedInt(byte highByte, byte b2, byte b3, byte lowByte) {
		return signedInt(signedShort(highByte, b2), signedShort(b3, lowByte));
	}

	public static int signedInt(byte[] data, int offset) {
		return signedInt(signedShort(data, offset), signedShort(data, offset + 2));
	}

	public static int signedInt(short highShort, short lowShort) {
		int val = highShort & 0xffff;
		val <<= 16;
		val |= lowShort & 0xffff;
		return val;
	}

	public static long signedLong(byte highByte, byte b2, byte b3, byte b4, byte b5, byte b6, byte b7, byte lowByte) {
		return signedLong(signedInt(highByte, b2, b3, b4), signedInt(b5, b6, b7, lowByte));
	}

	public static long signedLong(byte[] data, int offset) {
		return signedLong(signedInt(data, offset), signedInt(data, offset + 4));
	}

	public static long signedLong(int highInt, int lowInt) {
		long val = highInt & 0xffffffff;
		val <<= 32;
		val |= lowInt & 0xffffffff;
		return val;
	}

	public static short signedShort(int highByte, int lowByte) {
		int val = highByte & 0xff;
		val <<= 8;
		val |= lowByte & 0xff;
		return (short) val;
	}

	public static short signedShort(byte[] data, int offset) {
		return signedShort(data[offset], data[offset + 1]);
	}

	public static int unsignedByte(int b) {
		return 0xff & b;
	}

	public static int unsignedByte(byte[] data, int offset) {
		return unsignedByte(data[offset]);
	}

	public static long unsignedInt(byte[] data, int offset) {
		int high = unsignedShort(data, offset);
		int low = unsignedShort(data, offset + 2);
		System.out.println("<unsignedInt>");
		System.out.println("<high>" + Integer.toHexString(high) + "</high>");
		System.out.println("<low>" + Integer.toHexString(low) + "</low>");
		System.out.println("</unsignedInt>");
		return unsignedInt(high, low);
	}

	public static long unsignedInt(int highShort, int lowShort) {
		return 0xffffffffL & (highShort << 16 | lowShort);
	}

	public static int unsignedShort(int highByte, int lowByte) {
		return 0xffff & signedShort(highByte, lowByte);
	}

	public static int unsignedShort(byte[] data, int offset) {
		return unsignedShort(data[offset], data[offset + 1]);
	}

	public static int writeByte(byte[] to, int offset, int value) {
		to[offset] = (byte) (value & 0xff);
		return 1;
	}

	public static int writeInt(byte[] to, int offset, int value) {
		int pos = writeShort(to, offset, value >> 16);
		pos += writeShort(to, offset + pos, value);
		return pos;
	}

	public static int writeLong(byte[] to, int offset, long value) {
		int pos = writeInt(to, offset, (int) ((value >> 32) & 0xffffffff));
		pos += writeInt(to, offset + pos, (int) (value & 0xffffffff));
		return pos;
	}

	public static int writeShort(byte[] to, int offset, int value) {
		int pos = writeByte(to, offset, value >> 8);
		pos += writeByte(to, offset + pos, value);
		return pos;
	}

	public static long unsignedInt(int highByte, int b2, int b3, int lowByte) {
		return unsignedInt(signedShort(highByte, b2), signedShort(b3, lowByte));
	}
}

// legacy impl
///**
// * @author Artoe
// */
//public final class ByteHelper {
//	/**
//	 * Gets a signed <code>byte</code> value.
//	 * 
//	 * @param au {@link ArrayUnion} to extract value from
//	 * @return extracted <code>byte</code>
//	 */
//	public static byte getSignedByte(ArrayUnion au) {
//		return (byte) getUnsignedByte(au);
//	}
//
//	/**
//	 * Gets a signed <code>byte</code> value.
//	 * 
//	 * @param data byte array to extract value from
//	 * @param offset offset at which to extract
//	 * @return extracted <code>byte</code>
//	 */
//	public static byte getSignedByte(byte[] data, int offset) {
//		return (byte) getUnsignedByte(data, offset);
//	}
//
//	/**
//	 * Gets a signed <code>int</code> value.
//	 * 
//	 * @param au {@link ArrayUnion} to extract value from
//	 * @return extracted <code>int</code>
//	 */
//	public static int getSignedInt(ArrayUnion au) {
//		return (int) getUnsignedInt(au);
//	}
//
//	/**
//	 * Gets a signed <code>int</code> value.
//	 * 
//	 * @param data byte array to extract value from
//	 * @param offset offset at which to extract
//	 * @return extracted <code>int</code>
//	 */
//	public static int getSignedInt(byte[] data, int offset) {
//		return (int) getUnsignedInt(data, offset);
//	}
//
//	/**
//	 * Gets a signed <code>long</code> value.
//	 * 
//	 * @param au {@link ArrayUnion} to extract value from
//	 * @return extracted <code>long</code>
//	 */
//	public static long getSignedLong(ArrayUnion au) {
//		return getLong(au.read(), au.read(), au.read(), au.read(), au.read(), au.read(), au.read(), au.read());
//	}
//
//	/**
//	 * Gets a signed <code>long</code> value.
//	 * 
//	 * @param data byte array to extract value from
//	 * @param offset offset at which to extract
//	 * @return extracted <code>long</code>
//	 */
//	public static long getSignedLong(byte[] data, int offset) {
//		return getLong(data[offset], data[offset + 1], data[offset + 2], data[offset + 3], data[offset + 4],
//				data[offset + 5], data[offset + 6], data[offset + 7]);
//	}
//
//	/**
//	 * Gets a signed <code>short</code> value.
//	 * 
//	 * @param au {@link ArrayUnion} to extract value from
//	 * @return extracted <code>short</code>
//	 */
//	public static short getSignedShort(ArrayUnion au) {
//		return (short) getShort(au.read(), au.read());
//	}
//
//	/**
//	 * Gets a signed <code>short</code> value.
//	 * 
//	 * @param data byte array to extract value from
//	 * @param offset offset at which to extract
//	 * @return extracted <code>short</code>
//	 */
//	public static short getSignedShort(byte[] data, int offset) {
//		return (short) getUnsignedShort(data, offset);
//	}
//
//	/**
//	 * Gets an unsigned <code>byte</code> value.
//	 * 
//	 * @param au {@link ArrayUnion} to extract value from
//	 * @return extracted unsigned <code>byte</code>
//	 */
//	public static int getUnsignedByte(ArrayUnion au) {
//		return getByte(au.read());
//	}
//
//	/**
//	 * Gets an unsigned <code>byte</code> value.
//	 * 
//	 * @param data byte array to extract value from
//	 * @param offset offset at which to extract
//	 * @return extracted unsigned <code>byte</code>
//	 */
//	public static int getUnsignedByte(byte[] data, int offset) {
//		return getByte(data[offset]);
//	}
//
//	/**
//	 * Gets an unsigned <code>int</code> value.
//	 * 
//	 * @param au {@link ArrayUnion} to extract value from
//	 * @return extracted unsigned <code>int</code>
//	 */
//	public static long getUnsignedInt(ArrayUnion au) {
//		return getInt(au.read(), au.read(), au.read(), au.read());
//	}
//
//	/**
//	 * Gets an unsigned <code>int</code> value.
//	 * 
//	 * @param data byte array to extract value from
//	 * @param offset offset at which to extract
//	 * @return extracted unsigned <code>int</code>
//	 */
//	public static long getUnsignedInt(byte[] data, int offset) {
//		return getInt(data[offset], data[offset + 1], data[offset + 2], data[offset + 3]);
//	}
//
//	/**
//	 * Gets an unsigned <code>short</code> value.
//	 * 
//	 * @param au {@link ArrayUnion} to extract value from
//	 * @return extracted unsigned <code>short</code>
//	 */
//	public static int getUnsignedShort(ArrayUnion au) {
//		return getShort(au.read(), au.read());
//	}
//
//	/**
//	 * Gets an unsigned <code>short</code> value.
//	 * 
//	 * @param data byte array to extract value from
//	 * @param offset offset at which to extract
//	 * @return extracted unsigned <code>short</code>
//	 */
//	public static int getUnsignedShort(byte[] data, int offset) {
//		return getShort(data[offset], data[offset + 1]);
//	}
//
//	/**
//	 * Puts a <code>byte</code> value.
//	 * 
//	 * @param data byte array to put into
//	 * @param offset offset at which to put
//	 * @param value value to put
//	 */
//	public static void putByte(byte[] data, int offset, int value) {
//		data[offset] = (byte) (0xff & value);
//	}
//
//	/**
//	 * Puts an <code>int</code> value.
//	 * 
//	 * @param data byte array to put into
//	 * @param offset offset at which to put
//	 * @param value value to put
//	 */
//	public static void putInt(byte[] data, int offset, long value) {
//		putShort(data, offset, (int) (0xffff & (value >> 16)));
//		putShort(data, offset + 2, (int) (0xffff & value));
//	}
//
//	/**
//	 * Puts a <code>long</code> value.
//	 * 
//	 * @param data byte array to put into
//	 * @param offset offset at which to put
//	 * @param value value to put
//	 */
//	public static void putLong(byte[] data, int offset, long value) {
//		long tmp = value & 0x7fffffffffffffffL;
//		putInt(data, offset, (tmp >> 32) | (value < 0 ? 0x80000000 : 0));
//		putInt(data, offset + 4, tmp & 0xffffffff);
//	}
//
//	/**
//	 * Puts a <code>short</code> value.
//	 * 
//	 * @param data byte array to put into
//	 * @param offset offset at which to put
//	 * @param value value to put
//	 */
//	public static void putShort(byte[] data, int offset, int value) {
//		putByte(data, offset, value >> 8);
//		putByte(data, offset + 1, value);
//	}
//
//	/**
//	 * Converts the given <code>byte</code> to an unsigned <code>byte</code>.
//	 * 
//	 * @param b <code>byte</code> to convert
//	 * @return unsigned <code>byte</code>
//	 */
//	private static int getByte(int b) {
//		return 0xff & b;
//	}
//
//	/**
//	 * Constructs an unsigned <code>int</code> from the given <code>byte</code>s.
//	 * 
//	 * @param b1 highest order <code>byte</code>
//	 * @param b2 second highest order <code>byte</code>
//	 * @param b3 second lowest order <code>byte</code>
//	 * @param b4 lowest order <code>byte</code>
//	 * @return unsigned <code>int</code>
//	 */
//	private static long getInt(int b1, int b2, int b3, int b4) {
//		return (((long) getShort(b1, b2)) << 16) | getShort(b3, b4);
//	}
//
//	/**
//	 * Constructs a signed <code>long</code> from the given <code>byte</code>s.
//	 * 
//	 * @param b1 highest order <code>byte</code>
//	 * @param b2 second highest order <code>byte</code>
//	 * @param b3 third highest order <code>byte</code>
//	 * @param b4 fourth highest order <code>byte</code>
//	 * @param b5 fourth lowest order <code>byte</code>
//	 * @param b6 third lowest order <code>byte</code>
//	 * @param b7 second lowest order <code>byte</code>
//	 * @param b8 lowest order <code>byte</code>
//	 * @return unsigned <code>long</code>
//	 */
//	private static long getLong(int b1, int b2, int b3, int b4, int b5, int b6, int b7, int b8) {
//		return (getInt(b1, b2, b3, b4) << 32) | getInt(b5, b6, b7, b8);
//	}
//
//	/**
//	 * Constructs an unsigned <code>short</code> from the given <code>byte</code>s.
//	 * 
//	 * @param b1 high order <code>byte</code>
//	 * @param b2 low order <code>byte</code>
//	 * @return unsigned <code>short</code>
//	 */
//	private static int getShort(int b1, int b2) {
//		return ((getByte(b1) << 8) | getByte(b2));
//	}
//
//	/**
//	 * Constructor.
//	 */
//	private ByteHelper() {}
//}
