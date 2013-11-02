package jartoe.util;

/**
 * @author Artoe
 */
public final class Bytes {
	public static byte signedByte(byte[] data, int offset) {
		return data[offset];
	}

	public static int signedInt(byte[] data, int offset) {
		return signedInt(signedShort(data, offset), signedShort(data, offset + 2));
	}

	public static int signedInt(int highByte, int b2, int b3, int lowByte) {
		return signedInt(signedShort(highByte, b2), signedShort(b3, lowByte));
	}

	public static int signedInt(short highShort, short lowShort) {
		int val = highShort & 0xffff;
		val <<= 16;
		val |= lowShort & 0xffff;
		return val;
	}

	public static long signedLong(byte[] data, int offset) {
		return signedLong(signedInt(data, offset), signedInt(data, offset + 4));
	}

	public static long signedLong(int highInt, int lowInt) {
		long val = highInt & 0xffffffffL;
		val <<= 32;
		val |= lowInt & 0xffffffffL;
		return val;
	}

	public static long signedLong(int highByte, int b2, int b3, int b4, int b5, int b6, int b7, int lowByte) {
		return signedLong(signedInt(highByte, b2, b3, b4), signedInt(b5, b6, b7, lowByte));
	}

	public static short signedShort(byte[] data, int offset) {
		return signedShort(data[offset], data[offset + 1]);
	}

	public static short signedShort(int highByte, int lowByte) {
		int val = highByte & 0xff;
		val <<= 8;
		val |= lowByte & 0xff;
		return (short) val;
	}

	public static int unsignedByte(byte[] data, int offset) {
		return unsignedByte(data[offset]);
	}

	public static int unsignedByte(int b) {
		return 0xff & b;
	}

	public static long unsignedInt(byte[] data, int offset) {
		int high = unsignedShort(data, offset);
		int low = unsignedShort(data, offset + 2);
		return unsignedInt(high, low);
	}

	public static long unsignedInt(int highShort, int lowShort) {
		return 0xffffffffL & (highShort << 16 | lowShort);
	}

	public static long unsignedInt(int highByte, int b2, int b3, int lowByte) {
		return unsignedInt(signedShort(highByte, b2), signedShort(b3, lowByte));
	}

	public static int unsignedShort(byte[] data, int offset) {
		return unsignedShort(data[offset], data[offset + 1]);
	}

	public static int unsignedShort(int highByte, int lowByte) {
		return 0xffff & signedShort(highByte, lowByte);
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
}
