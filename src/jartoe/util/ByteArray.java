package jartoe.util;

/**
 * @author Artoe
 */
public final class ByteArray {
	//	private Object first;
	private int firstLength;
	private int offset;
	private byte[] second;

	public ByteArray(byte[] array) {
		//		first = null;
		firstLength = 0;
		offset = 0;
		second = array;
	}

	public ByteArray() {
		this(new byte[0]);
	}

	public int available() {
		return firstLength + second.length - offset;
	}

	public byte[] peekAll() {
		return new byte[0];
	}

	public int read() {
		return -1;
	}
}
