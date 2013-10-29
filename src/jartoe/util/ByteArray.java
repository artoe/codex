package jartoe.util;

/**
 * @author Artoe
 */
public final class ByteArray {
	private static final byte[] EMPTY = new byte[0];

	private Object first = EMPTY;
	private int firstLength;
	private int offset;
	private byte[] second = EMPTY;

	public ByteArray() {}

	public ByteArray(byte[] array) {
		if (array != null)
			second = array;
	}

	private ByteArray(Object first, byte[] second) {
		this.first = first;
		this.second = second;
		if (first instanceof ByteArray) {
			this.firstLength = ((ByteArray) first).available();
		} else {
			this.firstLength = ((byte[]) first).length;
		}
	}

	public void append(byte[] array) {
		if (array != null && array.length != 0) {
			if (firstLength == 0) {
				first = second;
				firstLength = second.length;
			} else if (second.length > 0) {
				ByteArray first = new ByteArray(this.first, second);
				this.first = first;
				firstLength = first.available();
			}
			second = array;
		}
	}

	public int available() {
		return firstLength + second.length;
	}

	public byte[] peekAll() {
		return new byte[0];
	}

	public int read() {
		if (offset < firstLength)
			return readFromFirst();
		if (offset < second.length) {
			int val = second[offset];
			if (++offset == second.length) {
				second = EMPTY;
				offset = 0;
			}
			return val;
		}
		return -1;
	}

	private int readFromFirst() {
		int val;
		if (first instanceof ByteArray) {
			val = ((ByteArray) first).read();
		} else {
			byte[] f = (byte[]) first;
			val = f[offset];
		}
		if (++offset == firstLength) {
			first = EMPTY;
			offset = 0;
			firstLength = 0;
		}
		return val;
	}
}
